/*
 * ConnectionManager.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.2 $
 */
package com.openitech.db;

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

  public String getDialect() {
    String dialect = "";
    try {
      String url = this.getProperty("db.jdbc.net").toLowerCase();
      if (url.startsWith("jdbc:jtds:sqlserver:")) {
        dialect = "mssql";
      }
    } catch (NullPointerException ex) {
      //ignore
    }
    return dialect;
  }
}
