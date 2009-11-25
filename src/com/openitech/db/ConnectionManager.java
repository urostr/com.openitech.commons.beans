/*
 * ConnectionManager.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.6 $
 */
package com.openitech.db;

import java.sql.Connection;

/**
 *
 * @author uros
 */
public class ConnectionManager implements DbConnection {

  private static ConnectionManager instance = null;
  private static Class managedConnectionClass;
  private DbConnection connection = null;

  /**
   * Creates a new instance of ConnectionManager
   */
  protected ConnectionManager() {
  }

  public static void registerManagedConnection(Class newManagedConnectionClass) {
    managedConnectionClass = newManagedConnectionClass;
  }

  public static ConnectionManager getInstance() {
    if (instance == null) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  public void setConnection(DbConnection connection) {
    if (this.connection == null) {
      this.connection = connection;
    }
  }

  private DbConnection getDbConnection() {
    if ((connection == null) && (managedConnectionClass != null)) {
      try {
        connection = (DbConnection) managedConnectionClass.newInstance();
      } catch (Exception ex) {
        throw (IllegalStateException) (new IllegalStateException("Can't instantiate a managed connection")).initCause(ex);
      }
    }

    return connection;
  }

  public void setServerConnect(boolean connecttoserver) {
    getDbConnection().setServerConnect(connecttoserver);
  }

  public java.sql.Connection getConnection() {
    return getDbConnection() == null ? null : getDbConnection().getConnection();
  }

  public String getProperty(String key) {
    return getDbConnection().getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return getDbConnection().getProperty(key, defaultValue);
  }

  public Object setProperty(String key, String value) {
    return getDbConnection().setProperty(key, value);
  }

  public boolean containsKey(String key) {
    return getDbConnection().containsKey(key);
  }

  public String getDriverClassName() {
    return getDbConnection().getProperty(DbConnection.DB_DRIVER_EMBEDDED, getDbConnection().getProperty(DbConnection.DB_DRIVER_NET, null));
  }

  @Override
  public String getUrl() {
    return getDbConnection() == null ? null : getDbConnection().getUrl();
  }

  @Override
  public String getDialect() {
    return getDbConnection() == null ? null : getDbConnection().getDialect();
  }

  public String getHibernateDialect() {
    String dialect = "";
    try {
      String url = this.getUrl().toLowerCase();
      if (url.startsWith("jdbc:jtds:sqlserver:")) {
        dialect = "org.hibernate.dialect.SQLServerDialect";
      }
    } catch (NullPointerException ex) {
      //ignore
    }
    return dialect;
  }

  @Override
  public Connection getTxConnection() {
    return getDbConnection() == null ? null : getDbConnection().getTxConnection();
  }

  @Override
  public Connection getTemporaryConnection() {
    return getDbConnection() == null ? null : getDbConnection().getTemporaryConnection();
  }

  @Override
  public boolean isPooled() {
    return getDbConnection() == null ? false : getDbConnection().isPooled();
  }

  @Override
  public boolean isConnectOnDemand() {
    return getDbConnection() == null ? false : getDbConnection().isConnectOnDemand();
  }

  @Override
  public boolean isCacheRowSet() {
    return getDbConnection() == null ? false : getDbConnection().isCacheRowSet();
  }

  @Override
  public boolean isCaseInsensitive() {
    return getDbConnection() == null ? false : getDbConnection().isCaseInsensitive();
  }
}
