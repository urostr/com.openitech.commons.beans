/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author uros
 */
public class StatementProxy implements java.sql.Statement {

  final int resultSetType;
  final int resultSetConcurrency;
  final AbstractConnection connection;
  java.sql.Statement statement;

  protected StatementProxy(AbstractConnection connection, int resultSetType, int resultSetConcurrency) throws SQLException {
    this.connection = connection;
    this.resultSetType = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
  }
  
  private static Boolean canUseIsClosed;

  protected java.sql.Statement createStatement() throws SQLException {
    return connection.getActiveConnection().createStatement(resultSetType, resultSetConcurrency);
  }

  private boolean canUseIsClosed() {
    if (canUseIsClosed == null) {
      try {
        this.statement.isClosed();
        canUseIsClosed = true;
      } catch (Throwable ex) {
        //ignore it
        canUseIsClosed = false;
      }
    }
    return canUseIsClosed;
  }

  protected void initStatement() throws SQLException {
    if (maxFieldSize != null) {
      statement.setMaxFieldSize(maxFieldSize);
    }
    if (maxRows != null) {
      statement.setMaxRows(maxRows);
    }
    if (escapeProcessing != null) {
      statement.setEscapeProcessing(escapeProcessing);
    }
    if (queryTimeout != null) {
      statement.setQueryTimeout(queryTimeout);
    }
    if (cursorName != null) {
      statement.setCursorName(cursorName);
    }
    if (fetchDirection != null) {
      statement.setFetchDirection(fetchDirection);
    }
    if (fetchSize != null) {
      statement.setFetchSize(fetchSize);
    }
    if (poolable != null) {
      statement.setPoolable(poolable);
    }
    if (batch != null && batch.size() > 0) {
      for (String sql : batch) {
        statement.addBatch(sql);
      }
    }
  }

  protected boolean isStatementClosed() {
    try {
      return statement == null || connection.connection.isClosed() || (canUseIsClosed()?statement.isClosed():false);
    } catch (SQLException ex) {
      return true;
    }
  }

  protected java.sql.Statement getActiveStatement() throws SQLException {
    if (isStatementClosed()) {
      statement = createStatement();
      initStatement();
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
    if (statement == null) {
      statement = getActiveStatement();
    }
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
    this.maxFieldSize = max;
  }
  Integer maxRows;

  @Override
  public int getMaxRows() throws SQLException {
    return getActiveStatement().getMaxRows();
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    getActiveStatement().setMaxRows(max);
    this.maxRows = max;
  }
  Boolean escapeProcessing;

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    getActiveStatement().setEscapeProcessing(enable);
    this.escapeProcessing = enable;
  }
  Integer queryTimeout;

  @Override
  public int getQueryTimeout() throws SQLException {
    return getActiveStatement().getQueryTimeout();
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    getActiveStatement().setQueryTimeout(seconds);
    this.queryTimeout = statement.getQueryTimeout();
  }

  @Override
  public void cancel() throws SQLException {
    if (statement != null) {
      statement.cancel();
    }
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return getActiveStatement().getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    getActiveStatement().clearWarnings();
  }
  String cursorName;

  @Override
  public void setCursorName(String name) throws SQLException {
    getActiveStatement().setCursorName(name);
    this.cursorName = name;
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    return getActiveStatement().execute(sql);
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    if (statement == null) {
      statement = getActiveStatement();
    }
    return statement.getResultSet();
  }

  @Override
  public int getUpdateCount() throws SQLException {
    if (statement == null) {
      statement = getActiveStatement();
    }
    return statement.getUpdateCount();
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    if (statement == null) {
      statement = getActiveStatement();
    }
    return statement.getMoreResults();
  }
  Integer fetchDirection;

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    getActiveStatement().setFetchDirection(direction);
    this.fetchDirection = direction;
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return getActiveStatement().getFetchDirection();
  }
  Integer fetchSize;

  @Override
  public void setFetchSize(int rows) throws SQLException {
    getActiveStatement().setFetchSize(rows);
    this.fetchSize = rows;
  }

  @Override
  public int getFetchSize() throws SQLException {
    return getActiveStatement().getFetchSize();
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    return getActiveStatement().getResultSetConcurrency();
  }

  @Override
  public int getResultSetType() throws SQLException {
    return getActiveStatement().getResultSetType();
  }
  List<String> batch;

  @Override
  public void addBatch(String sql) throws SQLException {
    getActiveStatement().addBatch(sql);
    if (batch == null) {
      batch = new ArrayList<String>();
    }
    batch.add(sql);
  }

  @Override
  public void clearBatch() throws SQLException {
    getActiveStatement().clearBatch();
    if (batch != null) {
      batch.clear();
    }
  }

  @Override
  public int[] executeBatch() throws SQLException {
    return getActiveStatement().executeBatch();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return getActiveStatement().getConnection();
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    if (statement == null) {
      statement = getActiveStatement();
    }
    return statement.getMoreResults(current);
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return getActiveStatement().getGeneratedKeys();
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    return getActiveStatement().executeUpdate(sql, autoGeneratedKeys);
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    return getActiveStatement().executeUpdate(sql, columnIndexes);
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    return getActiveStatement().executeUpdate(sql, columnNames);
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    return getActiveStatement().execute(sql, autoGeneratedKeys);
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    return getActiveStatement().execute(sql, columnIndexes);
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    return getActiveStatement().execute(sql, columnNames);
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return getActiveStatement().getResultSetHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    if (statement == null) {
      statement = getActiveStatement();
    }
    return statement.isClosed();
  }
  Boolean poolable;

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    getActiveStatement().setPoolable(poolable);
    this.poolable = poolable;
  }

  @Override
  public boolean isPoolable() throws SQLException {
    return getActiveStatement().isPoolable();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return getActiveStatement().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return getActiveStatement().isWrapperFor(iface);
  }
}
