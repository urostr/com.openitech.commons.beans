package com.openitech.db.connection;

import com.openitech.Settings;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLConnectionInitializer {
  private static final Semaphore lock = new Semaphore(1);

  private final AbstractSQLConnection sqlConnection;
  protected String DB_URL;
  protected Process databaseProcess = null;
  protected final Properties connect = new Properties();
  protected final java.util.List<String> executeOnCreate = new java.util.ArrayList<String>();
  protected String dialect = null;
  protected static boolean server = true;
  private static boolean firstConnectionInit = true;
  private static boolean addShutdownHook = false;
  private static final String fileSeparator = File.separatorChar == '\\' ? "\\\\" : File.separator;
  private static final String userDir = File.separatorChar == '\\' ? System.getProperty("user.dir").replaceAll(fileSeparator, fileSeparator + fileSeparator) : System.getProperty("user.dir");


  protected SQLConnectionInitializer(AbstractSQLConnection owner) {
    this.sqlConnection = owner;
  }

  protected String getDialect() {
    if (dialect == null) {
      try {
        String url = sqlConnection.getUrl().toLowerCase();
        if (url.startsWith("jdbc:jtds:sqlserver:")) {
          dialect = "mssql";
        }
      } catch (NullPointerException ex) {
        //ignore
      }
    }
    return dialect;
  }

  protected java.sql.Connection initConnection() throws IOException, InterruptedException, ClassNotFoundException, SQLException {
    final Properties settings = sqlConnection.settings;
    String DB_USER = settings.getProperty(ConnectionManager.DB_USER);
    String DB_PASS = settings.getProperty(ConnectionManager.DB_PASS);
    java.sql.Connection result = null;
    if (server && settings.containsKey(ConnectionManager.DB_DRIVER_NET)) {
      Class.forName(settings.getProperty(ConnectionManager.DB_DRIVER_NET));
      DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_NET);
      try {
        lock.acquire();
        Properties connect = new Properties();
        if (DB_USER != null) {
          connect.put("user", DB_USER);
          connect.put("password", DB_PASS);
        }
        for (Iterator<Map.Entry<Object, Object>> s = settings.entrySet().iterator(); s.hasNext();) {
          Map.Entry<Object, Object> entry = s.next();
          if (((String) entry.getKey()).startsWith(ConnectionManager.DB_CONNECT_PREFIX)) {
            connect.put(((String) entry.getKey()).substring(ConnectionManager.DB_CONNECT_PREFIX_LENGTH), entry.getValue());
          }
        }
        this.connect.clear();
        this.connect.putAll(connect);
        try {
          result = DriverManager.getConnection(DB_URL, connect);
        } catch (SQLException err) {
          result = null;
        }
        if (result == null) {
          if (SQLConnectionInitializer.firstConnectionInit && settings.containsKey("db.startup.net")) {
            SQLConnectionInitializer.firstConnectionInit = false;
            SQLConnectionInitializer.addShutdownHook = true;
            String dbStartupNetCommand = settings.getProperty("db.startup.net.command").replaceAll("\\{user.dir\\}", userDir).replaceAll("\\{file.separator\\}", fileSeparator).replaceAll("\\{path.separator\\}", System.getProperty("path.separator"));
            String exec = MessageFormat.format(dbStartupNetCommand, settings.getProperty("db.jdbc.net.port"));
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Executing:\n" + exec);
            databaseProcess = Runtime.getRuntime().exec(exec.replaceAll("\\\\", "\\\\"));
            Thread.currentThread().sleep(3000);
            result = DriverManager.getConnection(DB_URL, connect);
          }
          firstConnectionInit = false;
        }
        if (result != null) {
          sqlConnection.createSchema(result);
        }
        if (SQLConnectionInitializer.addShutdownHook && settings.containsKey("db.shutdown.net")) {
          SQLConnectionInitializer.addShutdownHook = false;
          try {
            Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK), true, this.getClass().getClassLoader()).newInstance());
          } catch (Exception ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can\'t attach a shutdown hook", ex);
          }
        }
      } catch (SQLException ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can\'t get a connection to \'" + DB_URL + "\' [" + ex.getMessage() + "]");
        result = null;
        if (!settings.containsKey(ConnectionManager.DB_DRIVER_EMBEDDED)) {
          throw (SQLException) (new SQLException("Neuspešna prijava na bazo").initCause(ex));
        }
      } finally {
        lock.release();
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
        if (((String) entry.getKey()).startsWith(DbConnection.DB_CONNECT_PREFIX)) {
          connect.put(((String) entry.getKey()).substring(DbConnection.DB_CONNECT_PREFIX_LENGTH), entry.getValue());
        }
      }
      this.connect.clear();
      this.connect.putAll(connect);
      result = DriverManager.getConnection(DB_URL, connect);
      if (result != null) {
        sqlConnection.createSchema(result);
        sqlConnection.fireActionPerformed(new ActionEvent(result, DbConnection.ACTION_DB_CONNECT, DbConnection.ACTION_GET_CONNECTION));
      }
      if (settings.containsKey(ConnectionManager.DB_SHUTDOWN_HOOK)) {
        try {
          Runtime.getRuntime().addShutdownHook((Thread) Class.forName(settings.getProperty(ConnectionManager.DB_SHUTDOWN_HOOK), true, this.getClass().getClassLoader()).newInstance());
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can\'t attach a shutdown hook", ex);
        }
      }
    }
    result.setReadOnly(false);
    result.setAutoCommit(Boolean.parseBoolean(settings.getProperty(DbConnection.DB_AUTOCOMMIT, "true")));
    for (Map.Entry<Object, Object> entry : settings.entrySet()) {
      if (entry.getKey().toString().startsWith(DbConnection.DB_POOL_EXECUTE_ON_CREATE)) {
        executeOnCreate.add(entry.getValue().toString());
      }
    }

    return result;
  }
}
