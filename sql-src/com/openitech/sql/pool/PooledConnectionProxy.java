/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.pool;

import com.openitech.events.concurrent.Locking;
import com.openitech.jdbc.proxy.ConnectionProxy;
import com.openitech.util.Equals;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class PooledConnectionProxy extends ConnectionProxy {

  private final java.util.Map<String, Object> defaults = new java.util.HashMap<String, Object>();
  private Timer timer;

  public PooledConnectionProxy(DataSource dataSource) throws SQLException {
    this(dataSource, true, null);
  }

  public PooledConnectionProxy(DataSource dataSource, boolean autoCommit, java.util.List<String> executeOnCreate) throws SQLException {
    super(dataSource, autoCommit, executeOnCreate);

    this.defaults.put("autocommit", this.connection.getAutoCommit());
    this.defaults.put("readOnly", this.connection.isReadOnly());
    this.defaults.put("catalog", this.connection.getCatalog());
    this.defaults.put("transactionIsolation", this.connection.getTransactionIsolation());
  }

  private boolean isDefault(String property, Object value) {
    return Equals.equals(defaults.get(property), value);
  }

  @Override
  protected boolean isConnectionActive() {
    return !closed;
  }

  @Override
  protected Connection getActiveConnection() throws SQLException {
    lock();
    try {
      if (closed) {
        open();
      }
      return super.getActiveConnection();
    } finally {
      unlock();
    }
  }

  protected void open() throws SQLException {
    lock();
    try {
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
      closed = Boolean.FALSE;
    } finally {
      unlock();
    }
  }

  @Override
  public void close() throws SQLException {
    lock();
    try {
      if (!closed) {
        if (!isDefault("autoCommit", getAutoCommit())
                || !isDefault("readOnly", isReadOnly())
                || !isDefault("catalog", getCatalog())
                || !isDefault("transactionIsolation", getTransactionIsolation())) {
          super.close();
        } else {
          for (Statement statement : activeStatemens) {
            statement.close();
          }
          for (Savepoint savepoint : activeSavepoints) {
            connection.releaseSavepoint(savepoint);
          }
          activeStatemens.clear();
          activeSavepoints.clear();
          closed = Boolean.TRUE;


          Logger.getLogger(PooledConnectionProxy.class.getName()).info("PooledConnection released.");

          if (timer != null) {
            timer.cancel();
          }
          (timer = new Timer()).schedule(new TimerTask() {

            @Override
            public void run() {
              if (!isConnectionActive()) {
                try {
                  PooledConnectionProxy.super.close();
                } catch (SQLException ex) {
                  Logger.getLogger(PooledConnectionProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            }
          }, 60000);
        }
      }

    } finally {
      unlock();
    }
  }
}
