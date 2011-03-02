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
import java.util.concurrent.ExecutionException;
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
    if (isShadowLoading()&&isShadowLoadingInterrupt()) {
      try {
        Future<V> future = super.submit(task);
        return future.get();
//      return task.call();
      } catch (InterruptedException ex) {
        throw new SQLException("SQL execution interrupted", ex);
      } catch (Exception ex) {
        if (ex.getCause() instanceof SQLException) {
          throw new SQLException("SQL execution failed", ex.getCause());
        } else {
          if (ex.getCause()!=null) {
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
