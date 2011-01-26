/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class ConnectionPool {

  private final List<PooledConnectionProxy> connections = Collections.synchronizedList(new ArrayList<PooledConnectionProxy>());
  private final DataSource dataSource;
  private final int maxSize;
  private int roundrobin = 0;
  private Timer cleanup = new Timer("Connection pool cleanup", true);

  public ConnectionPool(DataSource dataSource) {
    this(dataSource, 3, 9);
  }

  public ConnectionPool(DataSource dataSource, int initialSize, int maxSize) {
    this.dataSource = dataSource;
    this.maxSize = maxSize;
    for (int i = 0; i < initialSize; i++) {
      try {
        connections.add(new PooledConnectionProxy(dataSource));
      } catch (SQLException ex) {
        Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    this.cleanup.scheduleAtFixedRate(new TimerTask() {

      @Override
      public void run() {
        for (PooledConnectionProxy pooledConnectionProxy : connections) {
          if (System.currentTimeMillis() - pooledConnectionProxy.getTimestamp() > 30000) {
            try {
              pooledConnectionProxy.close();
            } catch (SQLException ex) {
              Logger.getLogger(ConnectionPool.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{ex.getSQLState(), ex.getMessage()});
            }
          }
        }
      }
    }, 180000, 5000);
  }

  public Connection getConnection() throws SQLException {
    PooledConnectionProxy result = null;
    if (roundrobin < connections.size()) {
      result = connections.get(roundrobin++);
    } else {
      if (connections.size() < maxSize) {
        connections.add(result = new PooledConnectionProxy(dataSource));
      } else {
        result = connections.get(roundrobin++);

      }
    }
    if (result.isClosed()) {
      result.open();
    }
    if (roundrobin >= connections.size()) {
      roundrobin = 0;
    }
    return result;
  }
}
