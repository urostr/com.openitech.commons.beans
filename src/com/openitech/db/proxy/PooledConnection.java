/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.proxy;

import com.openitech.db.ConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.logicalcobwebs.proxool.ProxoolConstants;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

/**
 *
 * @author uros
 */
public class PooledConnection {

  static PooledConnection instance = null;

  public static PooledConnection getInstance() {
    if (instance == null) {
      instance = new PooledConnection();
    }
    return instance;
  }
  protected String connectionTest;
  private java.util.List<java.sql.Connection> pool = new java.util.ArrayList<java.sql.Connection>();
  private int roundrobin = 1;
  private int pool_size = 9;
  String proxoolPool;

  public PooledConnection() {
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
      pool_size = Integer.valueOf(settings.getProperty(ConnectionManager.DB_POOL_SIZE, "9"));
    } catch (Exception err) {
      pool_size = 9;
    }

    int max_pool_size = pool_size * 9;

    String PROXOOL_DB_URL = "proxool.default:" + settings.getProperty(ConnectionManager.DB_DRIVER_NET) + ":" + DB_URL;
    connect.setProperty(ProxoolConstants.MINIMUM_CONNECTION_COUNT_PROPERTY, "1");
    connect.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY, "" + max_pool_size);
    connect.setProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY, "15000");
    connect.setProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY, "7200000");
    connect.setProperty(ProxoolConstants.STATISTICS_PROPERTY, "15s,15m");
    connect.setProperty(ProxoolConstants.STATISTICS_LOG_LEVEL_PROPERTY, ProxoolConstants.STATISTICS_LOG_LEVEL_DEBUG);
    connect.setProperty(ProxoolConstants.TEST_BEFORE_USE_PROPERTY, "true");
    connect.setProperty(ProxoolConstants.HOUSE_KEEPING_TEST_SQL_PROPERTY, connectionTest);

    final String autoCommit = settings.getProperty(ConnectionManager.DB_AUTOCOMMIT, "true");
    ProxoolFacade.registerConnectionPool(PROXOOL_DB_URL, connect);
    org.logicalcobwebs.proxool.ConnectionListenerIF cl = new org.logicalcobwebs.proxool.ConnectionListenerIF() {

      @Override
      public void onBirth(Connection arg0) throws SQLException {
        arg0.setReadOnly(false);
        arg0.setAutoCommit(Boolean.parseBoolean(autoCommit));
        System.out.println("proxool:connection created");
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
        System.err.println("proxool:connection failed:" + arg0 + ":" + arg1.getMessage());
        arg1.printStackTrace();
      }
    };

    ProxoolFacade.addConnectionListener("default", cl);

    Connection result = DriverManager.getConnection(PROXOOL_DB_URL, connect);

    if (result != null) {
      this.proxoolPool = "proxool.default";
      ;
      System.out.println("Using proxool pool");

      pool.add(new ConnectionProxy(proxoolPool, connectionTest, result));

    }

    return result != null;
  }
}

