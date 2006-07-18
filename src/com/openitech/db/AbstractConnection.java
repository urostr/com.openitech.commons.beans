/*
 * AbstractConnection.java
 *
 * Created on Sobota, 6 maj 2006, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db;

import com.openitech.Settings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public abstract class AbstractConnection implements DbConnection {
  
  private static boolean server = true;
  
  /**
   * Creates a new instance of AbstractConnection
   */
  protected java.sql.Connection connection=null;
  
  public AbstractConnection() {
    settings = loadProperites("connection.properties");
  }
  
  
  protected Properties settings = new Properties();
  
  /**
   * Creates a new instance of ConnectionManager
   */
  public void setServerConnect(boolean connecttoserver) {
    if (connection==null) {
      server = connecttoserver;
    } else
      throw new IllegalStateException("Can't change connection method after jdbc initialization.");
  }
  
  protected abstract Properties loadProperites(String propertiesName);
  
  public java.sql.Connection getConnection() {
    try {
      if (connection==null || connection.isClosed()) {
        String DB_USER = settings.getProperty(ConnectionManager.DB_USER);
        String DB_PASS = settings.getProperty(ConnectionManager.DB_PASS);
        String DB_URL = null;
        
        java.sql.Connection result = null;
        
        if (server) {
          Class.forName(settings.getProperty(ConnectionManager.DB_DRIVER_NET));
          DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_NET);
          try {
            if (settings.containsKey("db.startup.net")) {
              String dbStartupNetCommand = settings.getProperty("db.startup.net.command").
                      replaceAll("{user.dir}", System.getProperty("user.dir")).
                      replaceAll("{file.separator}", System.getProperty("file.separator")).
                      replaceAll("{path.separator}", System.getProperty("path.separator"));
              String exec = MessageFormat.format(dbStartupNetCommand, settings.getProperty("db.jdbc.net.port"));
              Logger.getLogger(Settings.LOGGER).info("Executing:\n"+exec);
              Runtime.getRuntime().exec(exec);
            }
            
            Properties connect = new Properties();
            connect.put("user",DB_USER);
            connect.put("password",DB_PASS);
            
            for (Iterator<Map.Entry<Object,Object>> s=settings.entrySet().iterator(); s.hasNext(); ) {
              Map.Entry<Object,Object> entry = s.next();
              if (((String) entry.getKey()).startsWith(DB_CONNECT_PREFIX)) {
                connect.put(((String) entry.getKey()).substring(DB_CONNECT_PREFIX_LENGTH),entry.getValue());
              }
            }
            
            result = DriverManager.getConnection(DB_URL, connect);
            if (settings.containsKey("db.shutdown.net")) {
              try {
                Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK),true, this.getClass().getClassLoader()).newInstance());
              } catch (Exception ex) {
                Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't attach a shutdown hook", ex);
              }
            }
          } catch (SQLException ex) {
            Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't get a connection to '"+DB_URL+"'");
            result = null;
          }
        }
        if (result==null) {
          Class.forName(settings.getProperty(ConnectionManager.DB_DRIVER_EMBEDDED));
          DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_EMBEDDED);
          
          Properties connect = new Properties();
          connect.put("user",DB_USER);
          connect.put("password",DB_PASS);
          
          for (Iterator<Map.Entry<Object,Object>> s=settings.entrySet().iterator(); s.hasNext(); ) {
            Map.Entry<Object,Object> entry = s.next();
            if (((String) entry.getKey()).startsWith(DB_CONNECT_PREFIX)) {
              connect.put(((String) entry.getKey()).substring(DB_CONNECT_PREFIX_LENGTH),entry.getValue());
            }
          }
          
          result = DriverManager.getConnection(DB_URL, connect);
          
          createSchema(result);
          
          if (settings.containsKey(ConnectionManager.DB_SHUTDOWN_HOOK))
            try {
              Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK),true, this.getClass().getClassLoader()).newInstance());
            } catch (Exception ex) {
              Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't attach a shutdown hook", ex);
            }
        }
        
        result.setReadOnly(false);
        result.setAutoCommit(true);
        
        connection = result;
      }
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a connection to the database", ex);
      connection = null;
    }
    return connection;
  }
  
  protected abstract void createSchema(Connection conn) throws SQLException;
  
  public String getProperty(String key) {
    return settings.getProperty(key);
  }
  
  public String getProperty(String key, String defaultValue) {
    return settings.getProperty(key, defaultValue);
  }
  
  public Object setProperty(String key, String value) {
    return settings.setProperty(key,value);
  }
  
  public boolean containsKey(String key) {
    return settings.containsKey(key);
  }
  
}
