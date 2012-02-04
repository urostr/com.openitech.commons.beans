/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import com.openitech.io.LogWriter;
import com.openitech.sql.SQLWorker;
import com.openitech.util.Equals;
import com.openitech.value.events.EventType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SQLMaterializedView extends SubstSqlParameter {
  private final LogWriter logWriter = new LogWriter(Logger.getLogger(TemporarySubselectSqlParameter.class.getName()), Level.INFO);
  private final SQLWorker sqlWorker = new SQLWorker(logWriter);

  public SQLMaterializedView(String tableName) {
    super();

    final ConnectionManager cm = ConnectionManager.getInstance();
    final String dl = cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_LEFT, "");
    final String dr = cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_RIGHT, "");

    if (dl.length() > 0 && dr.length() > 0) {
      this.replace = "(\\" + dl + tableName + "\\" + dr + '|' + tableName + ')';
    } else {
      this.replace = tableName;
    }
  }

  @Override
  public int getType() {
    return Types.SUBST_ALL;
  }
  protected String name;

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name) {
    this.name = name;
  }
  protected String isViewValidSQL;

  /**
   * Get the value of isViewValidSQL
   *
   * @return the value of isViewValidSQL
   */
  public String getIsViewValidSQL() {
    return isViewValidSQL;
  }

  /**
   * Set the value of isViewValidSQL
   *
   * @param isViewValidSQL new value of isViewValidSQL
   */
  public void setIsViewValidSQL(String isViewValidSQL) {
    this.isViewValidSQL = isViewValidSQL;
  }
  protected String setViewVersionSql;

  /**
   * Get the value of setViewVersionSql
   *
   * @return the value of setViewVersionSql
   */
  public String getSetViewVersionSql() {
    return setViewVersionSql;
  }
  protected boolean useParameters = false;

  /**
   * Get the value of useParameters
   *
   * @return the value of useParameters
   */
  public boolean isUseParameters() {
    return useParameters;
  }

  /**
   * Set the value of useParameters
   *
   * @param useParameters new value of useParameters
   */
  public void setHasParameters(boolean useParameters) {
    this.useParameters = useParameters;
  }

  /**
   * Set the value of setViewVersionSql
   *
   * @param setViewVersionSql new value of setViewVersionSql
   */
  public void setSetViewVersionSql(String setViewVersionSql) {
    this.setViewVersionSql = setViewVersionSql;
  }

  protected boolean indexedView = false;

  /**
   * Get the value of indexedView
   *
   * @return the value of indexedView
   */
  public boolean isIndexedView() {
    return indexedView;
  }

  /**
   * Set the value of indexedView
   *
   * @param indexedView new value of indexedView
   */
  public void setIndexedView(boolean indexedView) {
    this.indexedView = indexedView;
  }
  
  protected boolean cacheEvent = false;

  /**
   * Get the value of cacheEvent
   *
   * @return the value of cacheEvent
   */
  public boolean isCacheEvent() {
    return cacheEvent;
  }

  /**
   * Set the value of cacheEvent
   *
   * @param cacheEvent new value of cacheEvent
   */
  public void setCacheEvent(boolean cacheEvent) {
    this.cacheEvent = cacheEvent;
  }

  protected List<EventType> cacheEventTypes = new ArrayList<EventType>();

  /**
   * Get the value of cacheEventTypes
   *
   * @return the value of cacheEventTypes
   */
  public List<EventType> getCacheEventTypes() {
    return cacheEventTypes;
  }


  private Connection connection = null;
  private String qIsViewValid = null;
  private List<PreparedStatement> isViewValid = new ArrayList<PreparedStatement>();
  private final ReentrantLock lock = new ReentrantLock();

  public boolean isViewValid(Connection connection, java.util.List<Object> parameters) {
    if(isViewValidSQL == null){
      return true;
    }
    lock.lock();
    try {
      long timer = System.currentTimeMillis();
      if (!Equals.equals(this.connection, connection)) {
        String query = sqlWorker.substParameters(isViewValidSQL, parameters);
        String[] sqls = query.split(";");
        for (PreparedStatement preparedStatement : this.isViewValid) {
          preparedStatement.close();
        }
        this.isViewValid.clear();
        for (String sql : sqls) {
          this.isViewValid.add(connection.prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY,
                  ResultSet.HOLD_CURSORS_OVER_COMMIT));
        }
        this.qIsViewValid = query;

        this.connection = connection;
      }

      boolean result = true;

      if (DbDataSource.DUMP_SQL) {
        logWriter.println("##############");
        logWriter.println(this.qIsViewValid);
      }
      for (PreparedStatement preparedStatement : isViewValid) {
        ResultSet executeQuery = sqlWorker.executeQuery(preparedStatement, parameters);
        try {
          if (executeQuery.next()) {
            result = result && executeQuery.getBoolean(1);
          }
        } finally {
          executeQuery.close();
        }
        if (!result) {
          break;
        }
      }
      if (DbDataSource.DUMP_SQL) {
        logWriter.println("materialized:isvalid:" + getValue() + "..[" + result + "]..." + (System.currentTimeMillis() - timer) + "ms");
        logWriter.println("##############");
      }
      return result;
    } catch (SQLException ex) {
      Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      lock.unlock();
      logWriter.flush();
    }

    return false;
  }
}
