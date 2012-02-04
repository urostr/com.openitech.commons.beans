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
package com.openitech.db.model;

import com.openitech.db.model.sql.SQLDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author uros
 */
public class DbDataSourceFactory {

  private static DbDataSourceFactory instance;

  /**
   * Get the value of instance
   *
   * @return the value of instance
   */
  public static DbDataSourceFactory getInstance() {
    if (instance == null) {
      instance = new DbDataSourceFactory();
    }
    return instance;
  }

  private DbDataSourceFactory() {
  }
  private Class<? extends DbDataSourceImpl> dbDataSourceClass = SQLDataSource.class;

  /**
   * Get the value of DbDataSourceClass
   *
   * @return the value of DbDataSourceClass
   */
  public Class<? extends DbDataSourceImpl> getDbDataSourceClass() {
    return dbDataSourceClass;
  }

  /**
   * Set the value of DbDataSourceClass
   *
   * @param DbDataSourceClass new value of DbDataSourceClass
   */
  public void setDbDataSourceClass(Class<? extends DbDataSourceImpl> dbDataSourceClass) {
    this.dbDataSourceClass = dbDataSourceClass;
  }

  protected DbDataSourceImpl createDbDataSource(DbDataSource owner, Class<? extends DbDataSourceImpl> dbDataSourceClass) {
    DbDataSourceImpl result = null;
    try {
      result = (DbDataSourceImpl) dbDataSourceClass.getConstructor(DbDataSource.class).newInstance(owner);
    } catch (Exception ex) {
      Logger.getLogger(DbDataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  public static interface DbDataSourceImpl extends javax.sql.RowSet {

    public void clearSharedResults();

    public void doDeleteRow() throws SQLException;

    public int getType(int columnIndex) throws SQLException;

    public int getType(String columnName) throws SQLException;

    public void startUpdate() throws SQLException;

    public void doStoreUpdates(boolean insert, java.util.Map<String, Object> columnValues, java.util.Map<Integer, Object> oldValues, Integer row) throws SQLException;

    public void setCountSql(String countSql) throws SQLException;

    public String getCountSql();

    public java.sql.Connection getConnection();

    public void setConnection(java.sql.Connection connection) throws SQLException;

    public String getSelectSql();

    public void setSelectSql(String selectSql) throws SQLException;

    public String getUpdateTableName();

    public void setUpdateTableName(String updateTableName);

    public int getColumnIndex(String columnName) throws SQLException;

    public int getRowCount();

    public boolean isDataLoaded();

    public boolean loadData(boolean reload, int oldRow);

    public int getColumnCount() throws SQLException;

    public Object getValueAt(int rowIndex, int columnIndex) throws SQLException;

    public Object getValueAt(int rowIndex, String columnName) throws SQLException;

    public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException;

    public java.sql.ResultSet getResultSet() throws SQLException;

    public String getColumnName(int columnIndex) throws SQLException;

    public boolean isUpdating() throws SQLException;

    public void updateRefreshPending();

    public java.util.Map<Integer, java.util.Map<String, Object>> getStoredUpdates();

    public boolean hasChanged(int columnIndex) throws SQLException;

    public boolean hasChanged(String columnName) throws SQLException;

    public Object getOldValue(int columnIndex) throws SQLException;

    public Object getOldValue(String columnName) throws SQLException;

    public boolean wasUpdated(int row, String columnName) throws SQLException;

    public boolean isPending(String columnName, int row) throws SQLException;

    public void storeUpdates(boolean insert) throws SQLException;

    /**
     * Get the value of catalogName
     *
     * @return the value of catalogName
     */
    public String getCatalogName();

    /**
     * Set the value of catalogName
     *
     * @param catalogName new value of catalogName
     */
    public void setCatalogName(String catalogName);

    /**
     * Get the value of schemaName
     *
     * @return the value of schemaName
     */
    public String getSchemaName();

    /**
     * Set the value of schemaName
     *
     * @param schemaName new value of schemaName
     */
    public void setSchemaName(String schemaName);

    public String[] getUniqueID();

    public void setUniqueID(String[] uniqueID);

    public String getDelimiterLeft();

    public void setDelimiterLeft(String delimiterLeft);

    public String getDelimiterRight();

    public void setDelimiterRight(String delimiterRight);

    public java.util.Set<String> getUpdateColumnNames();

    public void addUpdateColumnName(String... fieldNames);

    public void removeUpdateColumnName(String... fieldNames);

    public String[] getGetValueColumns();

    public void setGetValueColumns(String[] columns);

    @Override
    public void setReadOnly(boolean readOnly);

    @Override
    public boolean isReadOnly();

    public boolean isRefreshPending();

    public void filterChanged() throws SQLException;

    public void setProvider(String providerClassName);

    public boolean fireEvents();

    public CachedRowSet getCachedRowSet() throws SQLException;

    public DbDataSourceImpl copy(DbDataSource owner);

    public void loadData(DbDataSourceImpl dataSource, int oldRow);

    public void destroy();

    public void setSource(Object source);

    public void setColumnReader(ColumnNameReader columnReader);
  }
}
