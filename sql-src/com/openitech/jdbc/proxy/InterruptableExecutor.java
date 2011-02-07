/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

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

  protected static final Map<Runnable, Thread> tasks = new ConcurrentHashMap<Runnable, Thread>();

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
    for (Map.Entry<Runnable, Thread> entry : tasks.entrySet()) {
      entry.getValue().stop(new SQLException("SQL execution cancelled."));
    }
    tasks.clear();
  }

  /**
   * @throws CancellationException {@inheritDoc}
   */
  public <V> V get(Callable<V> task) throws SQLException {
    try {
      Future<V> future = super.submit(task);
      return future.get();
    } catch (Exception ex) {
      throw new SQLException(ex);
    }
  }
}
