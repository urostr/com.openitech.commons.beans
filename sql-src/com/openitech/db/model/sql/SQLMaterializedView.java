/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import com.openitech.util.Equals;
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
  private Connection connection = null;
  private String qIsViewValid = null;
  private List<PreparedStatement> isViewValid = new ArrayList<PreparedStatement>();
  private final ReentrantLock lock = new ReentrantLock();

  public boolean isViewValid(Connection connection, java.util.List<Object> parameters) {
    lock.lock();
    try {
      long timer = System.currentTimeMillis();
      if (!Equals.equals(this.connection, connection)) {
        String query = SQLDataSource.substParameters(isViewValidSQL, parameters);
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
        System.out.println("##############");
        System.out.println(this.qIsViewValid);
      }
      for (PreparedStatement preparedStatement : isViewValid) {
        ResultSet executeQuery = SQLDataSource.executeQuery(preparedStatement, parameters);
        try {
          if (executeQuery.next()) {
            result = result || executeQuery.getBoolean(1);
          }
        } finally {
          executeQuery.close();
        }
        if (!result) {
          break;
        }
      }
      if (DbDataSource.DUMP_SQL) {
        System.out.println("materialized:isvalid:" + getValue() + "..." + (System.currentTimeMillis() - timer) + "ms");
        System.out.println("##############");
      }
      return result;
    } catch (SQLException ex) {
      Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      lock.unlock();
    }

    return false;
  }
}
