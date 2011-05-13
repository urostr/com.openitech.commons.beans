/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.pool.proxool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;

/**
 *
 * @author uros
 */
public class ConnectionProxy implements java.sql.Connection {

  final String proxoolPool;
  final String test;
  java.sql.Connection connection;
  java.sql.PreparedStatement query;

  public ConnectionProxy(String proxoolPool, String test, Connection implementation) {
    this.proxoolPool = proxoolPool;
    this.connection = implementation;
    this.test = test;
  }

  public void testConnection() throws java.sql.SQLException {
    boolean passed = false;
    try {
      if (connection == null) {
        throw new java.sql.SQLException("Connection not ready");
      } else {
        connection.getWarnings();
//        connection.isValid(5);

        if (query != null) {
          query.execute();
        }
      }
      passed = true;
    } finally {
      if (!passed) {
        synchronized (this) {
          System.err.println(getClass().getName() + ":TEST FAILED:invalidating connection");
          try {
            ProxoolFacade.killConnecton(connection, false);
          } catch (ProxoolException ex) {
            Logger.getLogger(ConnectionProxy.class.getName()).log(Level.SEVERE, null, ex);
          }
          connection.close();
          connection = null;
        }
      }
    }
  }

  protected java.sql.Connection getConnection() {
    boolean create = (connection == null);

    if (!create) {
      try {
        testConnection();
      } catch (Throwable ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(ConnectionProxy.class.getName() + ":reopening connection:cause [" + ex.getMessage() + "]");
        create = true;
      }
    }
    if (create) {
      synchronized (this) {
        if (connection == null) {
          try {
            java.sql.Connection c = DriverManager.getConnection(proxoolPool);
            if (c != null) {
              connection = c;
              query = c.prepareStatement(test);
              query.setQueryTimeout(5);
              try {
                SnapshotIF snapshot = ProxoolFacade.getSnapshot(proxoolPool.replace("proxool.", ""), true);
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(ConnectionProxy.class.getName() + ":connection created:active:" + snapshot.getActiveConnectionCount() + ":available:" + snapshot.getAvailableConnectionCount() + ":total:" + snapshot.getConnectionCount());
              } catch (ProxoolException ex) {
                Logger.getLogger(ConnectionProxy.class.getName()).log(Level.SEVERE, ex.getMessage());
              }
            }

            if (c instanceof ConnectionProxy) {
              connection = ((ConnectionProxy) c).connection;
            }
          } catch (SQLException ex) {
            Logger.getLogger(ConnectionProxy.class.getName()).log(Level.SEVERE, "Failed to create connection", ex);
          }
        }
      }
    }
    return connection;
  }

  @Override
  public Statement createStatement() throws SQLException {
    return new StatementProxy(this, getConnection().createStatement());
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql), sql);
  }

  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    return new CallableStatementProxy(this, getConnection().prepareCall(sql), sql);
  }

  @Override
  public String nativeSQL(String sql) throws SQLException {
    return getConnection().nativeSQL(sql);
  }

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    getConnection().setAutoCommit(autoCommit);
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    return getConnection().getAutoCommit();
  }

  @Override
  public void commit() throws SQLException {
    getConnection().commit();
  }

  @Override
  public void rollback() throws SQLException {
    getConnection().rollback();
  }

  @Override
  public void close() throws SQLException {
    getConnection().close();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return getConnection().isClosed();
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return getConnection().getMetaData();
  }

  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    getConnection().setReadOnly(readOnly);
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    return getConnection().isReadOnly();
  }

  @Override
  public void setCatalog(String catalog) throws SQLException {
    getConnection().setCatalog(catalog);
  }

  @Override
  public String getCatalog() throws SQLException {
    return getConnection().getCatalog();
  }

  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    getConnection().setTransactionIsolation(level);
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    return getConnection().getTransactionIsolation();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return getConnection().getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    getConnection().clearWarnings();
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    return new StatementProxy(this, getConnection().createStatement(resultSetType, resultSetConcurrency));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    return new CallableStatementProxy(this, getConnection().prepareCall(sql, resultSetType, resultSetConcurrency), sql);
  }

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return getConnection().getTypeMap();
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    getConnection().setTypeMap(map);
  }

  @Override
  public void setHoldability(int holdability) throws SQLException {
    getConnection().setHoldability(holdability);
  }

  @Override
  public int getHoldability() throws SQLException {
    return getConnection().getHoldability();
  }

  @Override
  public Savepoint setSavepoint() throws SQLException {
    return getConnection().setSavepoint();
  }

  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    return getConnection().setSavepoint(name);
  }

  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    getConnection().rollback(savepoint);
  }

  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    getConnection().releaseSavepoint(savepoint);
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new StatementProxy(this, getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new CallableStatementProxy(this, getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql, autoGeneratedKeys), sql, autoGeneratedKeys);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql, columnIndexes), sql, columnIndexes);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return new PreparedStatementProxy(this, getConnection().prepareStatement(sql, columnNames), sql, columnNames);
  }

  @Override
  public Clob createClob() throws SQLException {
    return getConnection().createClob();
  }

  @Override
  public Blob createBlob() throws SQLException {
    return getConnection().createBlob();
  }

  @Override
  public NClob createNClob() throws SQLException {
    return getConnection().createNClob();
  }

  @Override
  public SQLXML createSQLXML() throws SQLException {
    return getConnection().createSQLXML();
  }

  @Override
  public boolean isValid(int timeout) throws SQLException {
    return getConnection().isValid(timeout);
  }

  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    getConnection().setClientInfo(name, value);
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    getConnection().setClientInfo(properties);
  }

  @Override
  public String getClientInfo(String name) throws SQLException {
    return getConnection().getClientInfo(name);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    return getConnection().getClientInfo();
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    return getConnection().createArrayOf(typeName, elements);
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    return getConnection().createStruct(typeName, attributes);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return getConnection().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return getConnection().isWrapperFor(iface);
  }
}
