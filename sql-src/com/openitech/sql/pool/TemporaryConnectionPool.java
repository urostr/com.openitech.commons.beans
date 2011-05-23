/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class TemporaryConnectionPool {

  private final List<TemporaryPooledConnectionProxy> connections = new ArrayList<TemporaryPooledConnectionProxy>();
  private final DataSource dataSource;
  protected boolean autoCommit;
  private final int initialSize;
  private java.util.List<String> executeOnCreate;
  private Semaphore listLock = new Semaphore(1);
  private Timer cleanup;

  public TemporaryConnectionPool(DataSource dataSource, boolean autoCommit, java.util.List<String> executeOnCreate) {
    this(dataSource, autoCommit, 0, executeOnCreate);
  }

  public TemporaryConnectionPool(DataSource dataSource, boolean autoCommit, final int initialSize, java.util.List<String> executeOnCreate) {
    this.dataSource = dataSource;
    this.autoCommit = autoCommit;
    this.executeOnCreate = executeOnCreate;
    this.initialSize = initialSize;

    for (int i = 0; i < initialSize; i++) {
      try {
        connections.add(new TemporaryPooledConnectionProxy(dataSource, autoCommit, executeOnCreate));
      } catch (SQLException ex) {
        Logger.getLogger(TemporaryConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    this.cleanup = new Timer("Temp Connection pool cleanup", true);
    this.cleanup.scheduleAtFixedRate(new TimerTask() {

      @Override
      public void run() {
        boolean removedConnection = false;
        while (removedConnection && connections.size() > initialSize) {
          removedConnection = false;
          for (int i = 0; i < connections.size(); i++) {
            try {
              TemporaryPooledConnectionProxy connection = connections.get(i);
              if (connection.isClosed()) {
                removeConnection(connection);
                removedConnection = true;
                break;
              }
            } catch (SQLException ex) {
              Logger.getLogger(TemporaryConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }
    }, 180000, 10000);
  }

  private void removeConnection(TemporaryPooledConnectionProxy connection) throws SQLException {
    try {
      listLock.acquire();
      try {
        connections.remove(connection);
      } finally {
        listLock.release();
      }
      connection.forceClose();
    } catch (InterruptedException ex) {
      Logger.getLogger(TemporaryConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Get the value of autoCommit
   *
   * @return the value of autoCommit
   */
  public boolean isAutoCommit() {
    return autoCommit;
  }

  /**
   * Set the value of autoCommit
   *
   * @param autoCommit new value of autoCommit
   */
  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  public synchronized Connection getConnection() throws SQLException {
    TemporaryPooledConnectionProxy result = null;
    try {
      listLock.acquire();
      try {
        for (TemporaryPooledConnectionProxy temporaryPooledConnectionProxy : connections) {
          if (temporaryPooledConnectionProxy.isClosed()) {
            result = temporaryPooledConnectionProxy;
            break;
          }
        }
      } finally {
        listLock.release();
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(TemporaryConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
    }

    if (result == null) {
      connections.add(result = new TemporaryPooledConnectionProxy(dataSource, autoCommit, executeOnCreate));
    }

    result.open();
    return result;
  }
}
