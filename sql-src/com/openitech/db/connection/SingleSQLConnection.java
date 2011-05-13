/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.connection;

import com.openitech.Settings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SingleSQLConnection implements DbConnection {
  private final AbstractSQLConnection owner;

  Boolean isCaseInsensitive = null;
  Boolean isConvertToVarchar = null;
  private final Properties connect;
  private final SQLConnectionInitializer sqlConnectionInitializer;
  /**
   * Creates a new instance of AbstractSQLConnection
   */
  protected java.sql.Connection connection = null;

  protected SingleSQLConnection(AbstractSQLConnection owner) {
    this.owner = owner;
    this.settings = owner.settings;
    this.sqlConnectionInitializer = new SQLConnectionInitializer(owner);
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
    return false;
  }

  @Override
  public boolean isConnectOnDemand() {
    return Boolean.valueOf(settings.getProperty(DB_CONNECT_ON_DEMAND, "true"));
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
      final Connection temporaryConnection = DriverManager.getConnection(sqlConnectionInitializer.DB_URL, connect);
      fireActionPerformed(new ActionEvent(temporaryConnection, DbConnection.ACTION_DB_CONNECT, DbConnection.ACTION_GET_TEMP_CONNECTION));
      return temporaryConnection;
    } catch (SQLException ex) {
      Logger.getLogger(AbstractSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Override
  public Connection getTxConnection() {
      if (connection == null) {
        return getConnection();
      } else {
        return connection;
      }
  }

  @Override
  public java.sql.Connection getConnection() {
      try {
        if (connection == null || connection.isClosed()) {
          connection = sqlConnectionInitializer.initConnection();
        }
      } catch (Exception ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't get a connection to the database", ex);
        connection = null;
      }

      return connection;
    
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
}
