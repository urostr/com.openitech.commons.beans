/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.proxy;

import com.openitech.db.ConnectionManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.logicalcobwebs.proxool.ProxoolConstants;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

/**
 *
 * @author uros
 */
public class PooledConnection {

  public static final int DEFAULT_POOL_SIZE = 3;
  public static final long RETRYS_LIMIT = 9;
  public static final long RETRY_DELAY = 54;
  
  static PooledConnection instance = null;

  public static PooledConnection getInstance() {
    if (instance == null) {
      instance = new PooledConnection();
    }
    return instance;
  }
  protected String connectionTest;
  private ConnectionProxy txConnection = null;
  private java.util.List<java.sql.Connection> pool = new java.util.ArrayList<java.sql.Connection>();
  private int roundrobin = 1;
  private int pool_size = DEFAULT_POOL_SIZE;
  String proxoolPool;
  String proxoolTemporary;

  public PooledConnection() {
  }

  public Connection getTxConnection() {
    return txConnection;
  }

  public Connection getTemporaryConnection() {
    Connection result = null;
    do {
      try {
        result = DriverManager.getConnection(proxoolTemporary);
      } catch (SQLException ex) {
        Logger.getLogger(PooledConnection.class.getName()).log(Level.WARNING, ex.getMessage());
        result = null;
      }
      if (result==null) {
        try {
          Thread.currentThread().sleep(108);
        } catch (InterruptedException ex) {
          Logger.getLogger(PooledConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } while (result ==null);
    return result;
  }

  public Connection getConnection() throws SQLException {
    java.sql.Connection result;
    if (pool.size() < pool_size) {
      pool.add(result = new ConnectionProxy(proxoolPool, connectionTest, DriverManager.getConnection(proxoolPool)));
    } else {
      result = pool.get(Math.max(pool.size() - 1, roundrobin++));
      if (roundrobin >= pool.size()) {
        roundrobin = 1;
      }
    }
    return result;
  }

  public boolean init(Properties settings, Properties connect) throws SQLException, ProxoolException {
    String DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_NET);
    connectionTest = settings.getProperty(ConnectionManager.DB_TEST, "select GETDATE()");

    try {
      pool_size = Integer.valueOf(settings.getProperty(ConnectionManager.DB_POOL_SIZE, "" + DEFAULT_POOL_SIZE));
    } catch (Exception err) {
      pool_size = DEFAULT_POOL_SIZE;
    }

    String max_pool_size = settings.getProperty(ConnectionManager.DB_MAX_POOL_SIZE, ""+pool_size * 9);

    String PROXOOL_DB_URL = "proxool.default:" + settings.getProperty(ConnectionManager.DB_DRIVER_NET) + ":" + DB_URL;
    connect.setProperty(ProxoolConstants.FATAL_SQL_EXCEPTION_PROPERTY, "the Connection object is closed,attempting to read when no request has been sent,TDS Protocol error");
    connect.setProperty(ProxoolConstants.MINIMUM_CONNECTION_COUNT_PROPERTY, "1");
    connect.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY, "" + pool_size);
    connect.setProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY, "3000");
    connect.setProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY, "7200000");
    connect.setProperty(ProxoolConstants.STATISTICS_PROPERTY, "15s,15m");
    connect.setProperty(ProxoolConstants.STATISTICS_LOG_LEVEL_PROPERTY, ProxoolConstants.STATISTICS_LOG_LEVEL_DEBUG);
    connect.setProperty(ProxoolConstants.TEST_BEFORE_USE_PROPERTY, "true");
    connect.setProperty(ProxoolConstants.HOUSE_KEEPING_TEST_SQL_PROPERTY, connectionTest);

    //final String autoCommit = settings.getProperty(ConnectionManager.DB_AUTOCOMMIT, "true");
    ProxoolFacade.registerConnectionPool(PROXOOL_DB_URL, connect);

    connect.setProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY, "15000");
    PROXOOL_DB_URL = "proxool.temporary:" + settings.getProperty(ConnectionManager.DB_DRIVER_NET) + ":" + DB_URL;
    connect.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY, "" + max_pool_size);
    ProxoolFacade.registerConnectionPool(PROXOOL_DB_URL, connect);


    org.logicalcobwebs.proxool.ConnectionListenerIF cl = new org.logicalcobwebs.proxool.ConnectionListenerIF() {

      @Override
      public void onBirth(Connection arg0) throws SQLException {
        boolean readonly = txConnection != null;
        if (readonly) {
          readonly = isTxConnectionValid();
        }
        if (readonly) {
          arg0.setReadOnly(true);
        } else {
          synchronized (PooledConnection.this) {
            if (!isTxConnectionValid()) {
              if (txConnection == null) {
                txConnection = new ConnectionProxy(proxoolPool, connectionTest, arg0);
              } else {
                txConnection.connection = arg0;
              }
            } else {
              readonly = true;
              arg0.setReadOnly(true);
            }
          }
        }

        System.out.println("proxool:connection created:" + (readonly ? "readonly" : "TX"));
      }

      @Override
      public void onDeath(Connection arg0, int arg1) throws SQLException {
        System.out.println("proxool:connection killed");
      }

      @Override
      public void onExecute(String arg0, long arg1) {
      }

      @Override
      public void onFail(String arg0, Exception arg1) {
        System.err.println("proxool:connection failed:" + arg0);
      }

      protected boolean isTxConnectionValid() {
        boolean valid = (txConnection!=null)&&(txConnection.connection != null);
        if (valid) {
          try {
            txConnection.testConnection();
          } catch (SQLException ex) {
            try {
              ProxoolFacade.killConnecton(txConnection.connection, false);
            } catch (ProxoolException ex1) {
              Logger.getLogger(PooledConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.out.println("proxool:the current TX connection is not valid. resetting.");
            valid = false;
          }
        }
        return valid;
      }

      private String readLine(String message) {
        try {
          java.io.LineNumberReader r = new java.io.LineNumberReader(new java.io.StringReader(message));
          return r.readLine();
        } catch (IOException ex) {
          return message;
        }
      }
    };

    ProxoolFacade.addConnectionListener("default", cl);

    Connection result = DriverManager.getConnection(PROXOOL_DB_URL, connect);

    if (result != null) {
      this.proxoolPool = "proxool.default";
      this.proxoolTemporary = "proxool.temporary";
      System.out.println("Using proxool pool");

      pool.add(new ConnectionProxy(proxoolPool, connectionTest, result));

    }

    return result != null;
  }
}

