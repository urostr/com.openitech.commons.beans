/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.model.sql.SQLDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void setReadOnly(boolean readOnly);

    public boolean isReadOnly();

  }
}