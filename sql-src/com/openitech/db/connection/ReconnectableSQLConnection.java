/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.connection;

import com.openitech.Settings;
import com.openitech.jdbc.proxy.ConnectionProxy;
import com.openitech.sql.datasource.DataSourceFactory;
import com.openitech.sql.pool.ConnectionPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author uros
 */
public class ReconnectableSQLConnection implements DbConnection {

  private final AbstractSQLConnection owner;
  private DataSource dataSource;
  private ConnectionPool temporaryPool;
  private ConnectionPool connectionPool;
  private Boolean isCaseInsensitive = null;
  private Boolean isConvertToVarchar = null;
  private final Properties connect;
  private final java.util.List<String> executeOnCreate;
  private final SQLConnectionInitializer sqlConnectionInitializer;
  /**
   * Creates a new instance of AbstractSQLConnection
   */
  protected java.sql.Connection connection = null;

  protected ReconnectableSQLConnection(AbstractSQLConnection owner) {
    this.owner = owner;
    this.settings = owner.settings;
    this.sqlConnectionInitializer = new SQLConnectionInitializer(owner);
    this.executeOnCreate = sqlConnectionInitializer.executeOnCreate;
    this.connect = sqlConnectionInitializer.connect;
  }
  protected final Properties settings;

  @Override
  public void setServerConnect(boolean connecttoserver) {
    if (connection == null) {
      SQLConnectionInitializer.server = connecttoserver;
    } else {
      throw new IllegalStateException("Can't change connection method after jdbc initialization.");
    }
  }

  @Override
  public boolean isPooled() {
    return Boolean.valueOf(settings.getProperty(DB_USE_POOL, "false"));
  }

  @Override
  public boolean isConnectOnDemand() {
    return Boolean.valueOf(settings.getProperty(DB_CONNECT_ON_DEMAND, "false"));
  }

  @Override
  public boolean isCacheRowSet() {
    return Boolean.valueOf(settings.getProperty(DB_CACHEROWSET, "false"));
  }

  @Override
  public String getUrl() {
    return settings.getProperty(DbConnection.DB_JDBC_EMBEDDED, settings.getProperty(DbConnection.DB_JDBC_NET, null));
  }

  @Override
  public String getDialect() {
    return sqlConnectionInitializer.getDialect();
  }

  //za MS sql predpostavljamo da je case insensitive
  @Override
  public boolean isCaseInsensitive() {
    return isCaseInsensitive == null ? (isCaseInsensitive = Boolean.valueOf(settings.getProperty(DB_CASE_INSENSITIVE, Boolean.toString("mssql".equals(getDialect()))))) : isCaseInsensitive;
  }

  //za MS sql predpostavljamo da ne uporabljamo nvarchar
  @Override
  public boolean isConvertToVarchar() {
    return isConvertToVarchar == null ? (isConvertToVarchar = Boolean.valueOf(settings.getProperty(DB_CONVERT_TO_VARCHAR, Boolean.toString("mssql".equals(getDialect()))))) : isConvertToVarchar;
  }
  
  @Override
  public Connection getTemporaryConnection() {
    try {
      Connection result;
      if (dataSource == null) {
        result = DriverManager.getConnection(sqlConnectionInitializer.DB_URL, connect);
      } else if (temporaryPool != null) {
        result = temporaryPool.getConnection();
      } else {
        result = new ConnectionProxy(this.dataSource);
      }
      fireActionPerformed(new ActionEvent(result, DbConnection.ACTION_DB_CONNECT, DbConnection.ACTION_GET_TEMP_CONNECTION));
      return result;
    } catch (SQLException ex) {
      Logger.getLogger(AbstractSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Override
  public Connection getTxConnection() {
    if (!isValid()) {
      try {
        openConnection();
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a connection to the database", ex);
        connection = null;
      }
    }
    return connection;
  }

  @Override
  public java.sql.Connection getConnection() {
    java.sql.Connection result = null;
    if (!isValid()) {
      try {
        openConnection();
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a connection to the database", ex);
        connection = null;
      }
    }
    if (isPooled()) {
      try {
        result = getPooledConnection();
      } catch (SQLException ex) {
        Logger.getLogger(ReconnectableSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    if (result == null) {
      result = connection;
    }

    return result;
  }

  private boolean isValid() {
    try {
      return connection != null && !connection.isClosed();
    } catch (SQLException ex) {
      Logger.getLogger(ReconnectableSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }

  private void openConnection() throws InterruptedException, ClassNotFoundException, SQLException, IOException {
    this.connection = sqlConnectionInitializer.initConnection();

    if (this.connection != null) {
      DataSource ds = DataSourceFactory.getDataSource(sqlConnectionInitializer.DB_URL, connect);
      if (ds != null) {
        this.dataSource = ds;
        if (Boolean.valueOf(settings.getProperty(DB_USE_TEMPORARY_POOL, "false"))) {
          this.temporaryPool = new ConnectionPool(ds, Boolean.parseBoolean(settings.getProperty(DB_AUTOCOMMIT, "true")), 1, 1, executeOnCreate);
        } else {
          this.temporaryPool = null;
        }
        this.connection = new ConnectionProxy(ds);
      }
    }
  }

  protected void createSchema(Connection conn) throws SQLException {
    owner.createSchema(conn);
  }

  @Override
  public String getProperty(String key) {
    return settings.getProperty(key);
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    return settings.getProperty(key, defaultValue);
  }

  @Override
  public Object setProperty(String key, String value) {
    return settings.setProperty(key, value);
  }

  @Override
  public boolean containsKey(String key) {
    return settings.containsKey(key);
  }

  @Override
  public synchronized void removeActionListener(ActionListener l) {
    owner.removeActionListener(l);
  }

  @Override
  public synchronized void addActionListener(ActionListener l) {
    owner.addActionListener(l);
  }

  public void fireActionPerformed(ActionEvent e) {
    owner.fireActionPerformed(e);
  }

  private Connection getPooledConnection() throws SQLException {
    if (connectionPool == null) {
      try {
        this.connectionPool = new ConnectionPool(dataSource, Boolean.parseBoolean(settings.getProperty(DB_AUTOCOMMIT, "true")), Integer.parseInt(settings.getProperty(DB_POOL_SIZE, "0")), Integer.parseInt(settings.getProperty(DB_MAX_POOL_SIZE, "3")), executeOnCreate);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return connectionPool.getConnection();
  }
}
