/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.pool;

import com.openitech.jdbc.proxy.ConnectionProxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;

import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class PooledConnectionProxy extends ConnectionProxy {

  private Timer timer;

  public PooledConnectionProxy(DataSource dataSource) throws SQLException {
    super(dataSource);
  }

  protected synchronized void open() throws SQLException {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }

    closed = Boolean.FALSE;
    getActiveConnection();
  }

  @Override
  public synchronized void close() throws SQLException {
    if (!closed) {
      if ((autoCommit != null)
              || (readOnly != null)
              || (catalog != null)
              || (transactionIsolation != null)
              || (typeMap != null)
              || (clientInfo != null)) {
        super.close();
      } else {
        closed = Boolean.TRUE;


        Logger.getLogger(PooledConnectionProxy.class.getName()).info("PooledConnection released.");

        if (timer!=null) {
          timer.cancel();
        }
        (timer = new Timer()).schedule(new TimerTask() {

          @Override
          public void run() {
            try {
              PooledConnectionProxy.super.close();
            } catch (SQLException ex) {
              Logger.getLogger(PooledConnectionProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }, 60000);
      }
    }
  }
}
