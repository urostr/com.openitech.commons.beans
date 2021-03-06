/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author domenbasic
 */
public class ConnectionPool {

  private final List<PooledConnectionProxy> connections = new ArrayList<PooledConnectionProxy>();
  private final java.util.List<String> executeOnCreate = new ArrayList<String>();
  private final DataSource dataSource;
  private final int maxSize;
  private int roundrobin = 0;
  private Timer cleanup;
  protected boolean autoCommit;

  public ConnectionPool(DataSource dataSource, boolean autoCommit, java.util.List<String> executeOnCreate) {
    this(dataSource, autoCommit, 0, 3, executeOnCreate);
  }

  public ConnectionPool(DataSource dataSource, boolean autoCommit, int initialSize, int maxSize, java.util.List<String> executeOnCreate) {
    this.dataSource = dataSource;
    this.maxSize = maxSize;
    this.autoCommit = autoCommit;

    for (int i = 0; i < initialSize; i++) {
      try {
        connections.add(new PooledConnectionProxy(dataSource, autoCommit, executeOnCreate));
      } catch (SQLException ex) {
        Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    if (initialSize != maxSize) {
      this.cleanup = new Timer("Connection pool cleanup", true);
      this.cleanup.scheduleAtFixedRate(new TimerTask() {

        @Override
        public void run() {
          for (PooledConnectionProxy pooledConnectionProxy : connections) {
            if (pooledConnectionProxy.lock(false, false)) {
              try {
                if (!pooledConnectionProxy.isConnectionActive()) {
                  try {
                    pooledConnectionProxy.close();
                  } catch (SQLException ex) {
                    Logger.getLogger(ConnectionPool.class.getName()).log(Level.INFO, "{0}:{1}", new Object[]{ex.getSQLState(), ex.getMessage()});
                  }
                }
              } finally {
                pooledConnectionProxy.unlock();
              }
            }
          }
        }
      }, 180000, 5000);
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
    PooledConnectionProxy result = null;
    if (roundrobin < connections.size()) {
      result = connections.get(roundrobin++);
    } else {
      if (connections.size() < maxSize) {
        connections.add(result = new PooledConnectionProxy(dataSource, autoCommit, executeOnCreate));
      } else {
        result = connections.get(roundrobin++);
      }
    }
    if (result.isClosed()) {
      result.open();
    }
    if (roundrobin >= maxSize) {
      roundrobin = 0;
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "getConnection = {0}", result);
    return result;
  }
}
