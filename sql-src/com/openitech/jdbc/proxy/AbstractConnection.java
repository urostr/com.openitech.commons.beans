/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import java.sql.DatabaseMetaData;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.PooledConnection;

/**
 *
 * @author uros
 */
public abstract class AbstractConnection implements java.sql.Connection {

  protected java.sql.Connection connection;
  protected final javax.sql.PooledConnection pooledConnection;

  public AbstractConnection(PooledConnection pooledConnection) throws SQLException {
    this.pooledConnection = pooledConnection;
    this.connection = pooledConnection.getConnection();
  }

  protected java.sql.Connection getActiveConnection() throws SQLException {
    if (this.connection.isClosed()) {
      java.sql.Connection activeConnection = pooledConnection.getConnection();
      if (autoCommit != null) {
        activeConnection.setAutoCommit(autoCommit);
      }
      if (readOnly != null) {
        activeConnection.setReadOnly(readOnly);
      }
      if (catalog != null) {
        activeConnection.setCatalog(catalog);
      }
      if (transactionIsolation != null) {
        activeConnection.setTransactionIsolation(transactionIsolation);
      }
      if (typeMap != null) {
        activeConnection.setTypeMap(typeMap);
      }
      if (clientInfo != null) {
        activeConnection.setClientInfo(clientInfo);
      }
      this.connection = activeConnection;
      Logger.getLogger(AbstractConnection.class.getName()).info("Connection reopened.");
    }
    return this.connection;
  }

  @Override
  public String nativeSQL(String sql) throws SQLException {
    return getActiveConnection().nativeSQL(sql);
  }
  protected Boolean autoCommit;

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    getActiveConnection().setAutoCommit(autoCommit);
    this.autoCommit = autoCommit;
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    return getActiveConnection().getAutoCommit();
  }

  @Override
  public void commit() throws SQLException {
    getActiveConnection().commit();
  }

  @Override
  public void rollback() throws SQLException {
    getActiveConnection().rollback();
  }

  @Override
  public void close() throws SQLException {
    connection.close();
    Logger.getLogger(AbstractConnection.class.getName()).info("Connection closed.");
  }

  @Override
  public boolean isClosed() throws SQLException {
    return connection.isClosed();
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return getActiveConnection().getMetaData();
  }
  protected Boolean readOnly;

  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    getActiveConnection().setReadOnly(readOnly);
    this.readOnly = readOnly;
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    return getActiveConnection().isReadOnly();
  }
  String catalog;

  @Override
  public void setCatalog(String catalog) throws SQLException {
    getActiveConnection().setCatalog(catalog);
    this.catalog = catalog;
  }

  @Override
  public String getCatalog() throws SQLException {
    return getActiveConnection().getCatalog();
  }
  protected Integer transactionIsolation;

  @Override
  public void setTransactionIsolation(int transactionIsolation) throws SQLException {
    getActiveConnection().setTransactionIsolation(transactionIsolation);
    this.transactionIsolation = transactionIsolation;
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    return getActiveConnection().getTransactionIsolation();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return getActiveConnection().getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    getActiveConnection().clearWarnings();
  }
  protected Map<String, Class<?>> typeMap;

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return getActiveConnection().getTypeMap();
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> typeMap) throws SQLException {
    getActiveConnection().setTypeMap(typeMap);
    this.typeMap = typeMap;
  }
  protected Integer holdability;

  @Override
  public void setHoldability(int holdability) throws SQLException {
    getActiveConnection().setHoldability(holdability);
    this.holdability = holdability;
  }

  @Override
  public int getHoldability() throws SQLException {
    return getActiveConnection().getHoldability();
  }

  @Override
  public boolean isValid(int timeout) throws SQLException {
    return connection.isValid(timeout);
  }
  Properties clientInfo;

  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    try {
      getActiveConnection().setClientInfo(name, value);
      clientInfo = getActiveConnection().getClientInfo();
    } catch (SQLException ex) {
      Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, null, ex);
      connection.setClientInfo(name, value);
    }
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    try {
      getActiveConnection().setClientInfo(properties);
      clientInfo = connection.getClientInfo();
    } catch (SQLException ex) {
      Logger.getLogger(AbstractConnection.class.getName()).log(Level.WARNING, null, ex);
      connection.setClientInfo(properties);
    }
  }

  @Override
  public String getClientInfo(String name) throws SQLException {
    return getActiveConnection().getClientInfo(name);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    return getActiveConnection().getClientInfo();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return getActiveConnection().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return getActiveConnection().isWrapperFor(iface);
  }

  @Override
  public Statement createStatement() throws SQLException {
    return createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
            java.sql.ResultSet.CONCUR_READ_ONLY);
  }

  @Override
  public Statement createStatement(int type, int concurrency, int holdability)
          throws SQLException {
    setHoldability(holdability);

    return createStatement(type, concurrency);
  }
}