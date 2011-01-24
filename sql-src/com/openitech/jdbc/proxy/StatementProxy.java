/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.jdbc.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 *
 * @author uros
 */
public class StatementProxy implements java.sql.Statement {
  final int resultSetType;
  final int resultSetConcurrency;
  final java.sql.Connection connection;

  java.sql.Statement statement;

  protected StatementProxy(AbstractConnection connection, int resultSetType, int resultSetConcurrency) {
    this.connection = connection;
    this.resultSetType = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
  }

  private java.sql.Statement getActiveStatement() throws SQLException {
    if (statement==null||statement.isClosed()) {
      statement = connection.createStatement(resultSetType, resultSetConcurrency);
      if (maxFieldSize!=null) {
        statement.setMaxFieldSize(maxFieldSize);
      }
      if (maxRows!=null) {
        statement.setMaxRows(maxRows);
      }
      if (escapeProcessing!=null) {
        statement.setEscapeProcessing(escapeProcessing);
      }
    }
    return statement;
  }

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    return getActiveStatement().executeQuery(sql);
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    return getActiveStatement().executeUpdate(sql);
  }

  @Override
  public void close() throws SQLException {
    statement.close();
    statement = null;
  }

  Integer maxFieldSize;

  @Override
  public int getMaxFieldSize() throws SQLException {
    return getActiveStatement().getMaxFieldSize();
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    getActiveStatement().setMaxFieldSize(max);
    maxFieldSize = statement.getMaxFieldSize();
  }

  Integer maxRows;

  @Override
  public int getMaxRows() throws SQLException {
    return getActiveStatement().getMaxRows();
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    getActiveStatement().setMaxRows(max);
    maxRows = statement.getMaxRows();
  }

  Boolean escapeProcessing;

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    getActiveStatement().setEscapeProcessing(enable);
    escapeProcessing  = enable;
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void cancel() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clearWarnings() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getUpdateCount() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getFetchDirection() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getFetchSize() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getResultSetType() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addBatch(String sql) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clearBatch() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int[] executeBatch() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Connection getConnection() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isClosed() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isPoolable() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
