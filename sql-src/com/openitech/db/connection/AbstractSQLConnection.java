/*
 * AbstractSQLConnection.java
 *
 * Created on Sobota, 6 maj 2006, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.connection;

import com.openitech.Settings;
import com.openitech.sql.pool.proxool.PooledConnection;
import com.openitech.ref.WeakListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
public abstract class AbstractSQLConnection implements DbConnection {

  private static boolean server = true;
  private static final String fileSeparator = File.separatorChar == '\\' ? "\\\\" : File.separator;
  private static final String userDir = System.getProperty("user.dir").replaceAll(fileSeparator, fileSeparator + fileSeparator);
  private Process databaseProcess = null;
  boolean useProxool = false;
  Boolean isCaseInsensitive = null;
  String dialect = null;
  String DB_URL = null;
  Properties connect = new Properties();
  private transient WeakListenerList actionListeners;
  /**
   * Creates a new instance of AbstractSQLConnection
   */
  protected java.sql.Connection connection = null;

  public AbstractSQLConnection() {
    settings = loadProperites("connection.properties");
    settings.putAll(System.getProperties());
  }
  protected Properties settings = new Properties();

  /**
   * Creates a new instance of ConnectionManager
   */
  @Override
  public void setServerConnect(boolean connecttoserver) {
    if (connection == null) {
      server = connecttoserver;
    } else {
      throw new IllegalStateException("Can't change connection method after jdbc initialization.");
    }
  }

  protected abstract Properties loadProperites(String propertiesName);

  @Override
  public boolean isPooled() {
    return useProxool;
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
    if (dialect == null) {
      try {
        String url = this.getUrl().toLowerCase();
        if (url.startsWith("jdbc:jtds:sqlserver:")) {
          dialect = "mssql";
        }
      } catch (NullPointerException ex) {
        //ignore
      }
    }
    return dialect;
  }

  @Override
  public boolean isCaseInsensitive() {
    return isCaseInsensitive == null ? (isCaseInsensitive = Boolean.valueOf(settings.getProperty(DB_CASE_INSESITIVE, Boolean.toString("mssql".equals(getDialect()))))) : isCaseInsensitive;
  }

  @Override
  public Connection getTemporaryConnection() {
    try {
      if (useProxool) {
        return PooledConnection.getInstance().getTemporaryConnection();
      } else {
        final Connection connection = DriverManager.getConnection(DB_URL, connect);
        fireActionPerformed(new ActionEvent(connection, DbConnection.ACTION_DB_CONNECT, DbConnection.ACTION_GET_TEMP_CONNECTION));
        return connection;
      }
    } catch (SQLException ex) {
      Logger.getLogger(AbstractSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Override
  public Connection getTxConnection() {
    if (useProxool) {
      return PooledConnection.getInstance().getTxConnection();
    } else {
      if (connection == null) {
        return getConnection();
      } else {
        return connection;
      }
    }
  }

  @Override
  public java.sql.Connection getConnection() {
    if (useProxool) {
      try {
        return PooledConnection.getInstance().getConnection();
      } catch (SQLException ex) {
        Logger.getLogger(AbstractSQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        return connection;
      }
    } else {

      try {
        if (connection == null || connection.isClosed()) {
          if (Boolean.valueOf(settings.getProperty(DB_USE_POOL, "false"))) {
            try {
              Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
              useProxool = true;
            } catch (ClassNotFoundException ex) {
              useProxool = false;
            }
          } else {
            useProxool = false;
          }
          
          String DB_USER = settings.getProperty(ConnectionManager.DB_USER);
          String DB_PASS = settings.getProperty(ConnectionManager.DB_PASS);

          java.sql.Connection result = null;


          if (server && settings.containsKey(ConnectionManager.DB_DRIVER_NET)) {
            Class.forName(settings.getProperty(ConnectionManager.DB_DRIVER_NET));
            DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_NET);
            try {
              if (settings.containsKey("db.startup.net")) {
                String dbStartupNetCommand = settings.getProperty("db.startup.net.command").
                        replaceAll("\\{user.dir\\}", userDir).
                        replaceAll("\\{file.separator\\}", fileSeparator).
                        replaceAll("\\{path.separator\\}", System.getProperty("path.separator"));
                String exec = MessageFormat.format(dbStartupNetCommand, settings.getProperty("db.jdbc.net.port"));
                Logger.getLogger(Settings.LOGGER).info("Executing:\n" + exec);
                databaseProcess = Runtime.getRuntime().exec(exec);
                Thread.currentThread().sleep(3000);
              }

              Properties connect = new Properties();
              if (DB_USER != null) {
                connect.put("user", DB_USER);
                connect.put("password", DB_PASS);
              }

              for (Iterator<Map.Entry<Object, Object>> s = settings.entrySet().iterator(); s.hasNext();) {
                Map.Entry<Object, Object> entry = s.next();
                if (((String) entry.getKey()).startsWith(DB_CONNECT_PREFIX)) {
                  connect.put(((String) entry.getKey()).substring(DB_CONNECT_PREFIX_LENGTH), entry.getValue());
                }
              }


              this.connect.clear();
              this.connect.putAll(connect);

              if (useProxool) {
                useProxool = PooledConnection.getInstance().init(settings, connect);
                result = PooledConnection.getInstance().getTxConnection();
              } else {
                result = DriverManager.getConnection(DB_URL, connect);
              }

              if (result != null) {
                createSchema(result);
              }

              if (settings.containsKey("db.shutdown.net")) {
                try {
                  Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK), true, this.getClass().getClassLoader()).newInstance());
                } catch (Exception ex) {
                  Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't attach a shutdown hook", ex);
                }
              }
            } catch (SQLException ex) {
              Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't get a connection to '" + DB_URL + "' [" + ex.getMessage() + "]");
              result = null;
              if (!settings.containsKey(ConnectionManager.DB_DRIVER_EMBEDDED)) {
                throw (SQLException) (new SQLException("Neuspešna prijava na bazo").initCause(ex));
              }
            }
          }
          if (result == null) {
            Class.forName(settings.getProperty(ConnectionManager.DB_DRIVER_EMBEDDED));
            DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_EMBEDDED);

            Properties connect = new Properties();

            if (DB_USER != null) {
              connect.put("user", DB_USER);
              connect.put("password", DB_PASS);
            }

            for (Iterator<Map.Entry<Object, Object>> s = settings.entrySet().iterator(); s.hasNext();) {
              Map.Entry<Object, Object> entry = s.next();
              if (((String) entry.getKey()).startsWith(DB_CONNECT_PREFIX)) {
                connect.put(((String) entry.getKey()).substring(DB_CONNECT_PREFIX_LENGTH), entry.getValue());
              }
            }

            this.connect.clear();
            this.connect.putAll(connect);

            result = DriverManager.getConnection(DB_URL, connect);

            if (result != null) {
              createSchema(result);
              fireActionPerformed(new ActionEvent(result, DbConnection.ACTION_DB_CONNECT, DbConnection.ACTION_GET_CONNECTION));
            }

            if (settings.containsKey(ConnectionManager.DB_SHUTDOWN_HOOK)) {
              try {
                Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK), true, this.getClass().getClassLoader()).newInstance());
              } catch (Exception ex) {
                Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't attach a shutdown hook", ex);
              }
            }
          }

          result.setReadOnly(false);
          result.setAutoCommit(Boolean.parseBoolean(settings.getProperty(DB_AUTOCOMMIT, "true")));

          connection = result;
        }
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a connection to the database", ex);
        connection = null;
      }

      return connection;
    }
  }

  protected abstract void createSchema(Connection conn) throws SQLException;

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

  public synchronized void removeActionListener(ActionListener l) {
    if (actionListeners != null && actionListeners.contains(l)) {
      actionListeners.removeElement(l);
    }
  }

  public synchronized void addActionListener(ActionListener l) {
    WeakListenerList v = actionListeners == null ? new WeakListenerList(2) : actionListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      actionListeners = v;
    }
  }

  public void fireActionPerformed(ActionEvent e) {
    if (actionListeners != null) {
      java.util.List listeners = actionListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActionListener) listeners.get(i)).actionPerformed(e);
      }
    }
  }

}
