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
package com.openitech.events.concurrent;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.connection.DbConnection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class DataSourcePoolExecutor extends ThreadPoolExecutor {

  protected static final Map<RefreshDataSource, Thread> tasks = new ConcurrentHashMap<RefreshDataSource, Thread>();
  public static boolean ALLOW_TERMINATE = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(DbConnection.DB_SHADOW_INTERRUPT, "false"));

  protected DataSourcePoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return new DataSourceFutureTask(runnable, value);
  }

  @Override
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    return new DataSourceFutureTask(callable);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    if ((r instanceof DataSourceFutureTask) && ((DataSourceFutureTask) r).getTask() instanceof RefreshDataSource) {
      for (Map.Entry<RefreshDataSource, Thread> entry : tasks.entrySet()) {
        if (entry.getKey().isShadowLoading()) {
          String action = "interrupted";
          if (!entry.getKey().isLastInQueue() && entry.getKey().isLoading()) {
            try {
              entry.getValue().interrupt();
              if (ALLOW_TERMINATE) {
                if (entry.getKey().isLoading()
                        && (entry.getKey().event.getDataSource().getConnection() instanceof Interruptable)) {
                  ((Interruptable) entry.getKey().event.getDataSource().getConnection()).interrupt();
                  action = "connection interrupted";
                }
              }
            } catch (Throwable ex) {
              Logger.getLogger(DataSourcePoolExecutor.class.getName()).log(Level.WARNING, "Exeption while interrupting the connection", ex.getMessage());
            }

            Logger.getLogger(DataSourcePoolExecutor.class.getName()).log(Level.FINE, "{0}...refresh thread {1}.", new Object[]{entry.getKey().event.dataSource, action});
          }
        }
      }
      tasks.put((RefreshDataSource) ((DataSourceFutureTask) r).getTask(), t);
    }
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    if ((r instanceof DataSourceFutureTask) && ((DataSourceFutureTask) r).getTask() instanceof RefreshDataSource) {
      tasks.remove((RefreshDataSource) ((DataSourceFutureTask) r).getTask());
    }
  }

  private static class DataSourceFutureTask<V> extends FutureTask<V> {

    Object task;

    /**
     * Creates a <tt>FutureTask</tt> that will upon running, execute the
     * given <tt>Callable</tt>.
     *
     * @param  callable the callable task
     * @throws NullPointerException if callable is null
     */
    public DataSourceFutureTask(Callable<V> callable) {
      super(callable);
      this.task = callable;

    }

    /**
     * Creates a <tt>FutureTask</tt> that will upon running, execute the
     * given <tt>Runnable</tt>, and arrange that <tt>get</tt> will return the
     * given result on successful completion.
     *
     * @param  runnable the runnable task
     * @param result the result to return on successful completion. If
     * you don't need a particular result, consider using
     * constructions of the form:
     * <tt>Future&lt;?&gt; f = new FutureTask&lt;Object&gt;(runnable, null)</tt>
     * @throws NullPointerException if runnable is null
     */
    public DataSourceFutureTask(Runnable runnable, V result) {
      super(runnable, result);
      this.task = runnable;
    }

    public Object getTask() {
      return task;
    }
  }
}
