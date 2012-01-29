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
package com.openitech.jdbc.proxy;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.connection.DbConnection;
import com.openitech.events.concurrent.Interruptable;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author uros
 */
public class InterruptableExecutor extends ThreadPoolExecutor implements Interruptable {

  protected boolean shadowLoading = Boolean.valueOf(ConnectionManager.getInstance().getProperty(DbConnection.DB_SHADOW_LOADING, "false"));
  protected boolean shadowLoadingInterrupt = Boolean.valueOf(ConnectionManager.getInstance().getProperty(DbConnection.DB_SHADOW_INTERRUPT, "false"));
  protected boolean stopThread = Boolean.valueOf(ConnectionManager.getInstance().getProperty(DbConnection.DB_STOP_LOADING_THREAD, "false"));
  private final Map<Runnable, Thread> tasks = new ConcurrentHashMap<Runnable, Thread>();

  public InterruptableExecutor() {
    super(0, 1,
            5L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    tasks.remove(r);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    tasks.put(r, t);
  }

  @Override
  public void interrupt() {
    purge();
    for (Thread thread : tasks.values()) {
      thread.interrupt();
      if (stopThread && !tasks.isEmpty()) {
        thread.stop(new SQLException("SQL execution cancelled."));
      }
    }
    tasks.clear();
  }

  /**
   * @throws CancellationException {@inheritDoc}
   */
  public <V> V get(Callable<V> task) throws SQLException {
    if (isShadowLoading() && isShadowLoadingInterrupt()) {
      try {
        V result;
        synchronized (this) {
          Future<V> future = super.submit(task);
          result = future.get();
        }
        return result;
//      return task.call();
      } catch (InterruptedException ex) {
        throw new SQLException("SQL execution interrupted", ex);
      } catch (Exception ex) {
        if (ex.getCause() instanceof SQLException) {
          throw new SQLException("SQL execution failed", ex.getCause());
        } else {
          if (ex.getCause() != null) {
            ex.getCause().printStackTrace(System.err);
          }
          throw new SQLException("SQL execution rejected", ex);
        }
      }
    } else {
      try {
        return task.call();
      } catch (Exception ex) {
        throw new SQLException(ex.getMessage(), (ex instanceof SQLException) ? ((SQLException) ex).getSQLState() : "", ex);
      }
    }
  }

  /**
   * Get the value of shadowLoading
   *
   * @return the value of shadowLoading
   */
  public boolean isShadowLoading() {
    return shadowLoading;
  }

  /**
   * Get the value of shadowLoadingInterrupt
   *
   * @return the value of shadowLoadingInterrupt
   */
  public boolean isShadowLoadingInterrupt() {
    return shadowLoadingInterrupt;
  }
}
