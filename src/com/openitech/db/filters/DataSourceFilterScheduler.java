/*
 * DataSourceFilterScheduler.java
 *
 * Created on Èetrtek, 4 september 2008, 12:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.filters;

import com.openitech.db.filters.DataSourceFilters;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author uros
 */
public class DataSourceFilterScheduler {
  public static final long DELAY = 9;
  private static final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
  
  /** Creates a new instance of DataSourceFilterScheduler */
  protected DataSourceFilterScheduler() {
  }
  
  protected long delay;
  protected Future event = null;
  
  
  protected void cancel() {
    if (!(event==null||event.isDone()||event.isCancelled())) {
      event.cancel(false);
    }
  }
  
  protected void schedule(Runnable runnable) {
    schedule(runnable, this.delay);
  }
  
  protected void schedule(Runnable runnable, long delay) {
    cancel();
    event = schedule.schedule(runnable, delay, TimeUnit.MILLISECONDS);
  }
  
  public static class SeekValueUpdateRunnable<E> implements Runnable {
    DataSourceFilters filter;
    DataSourceFilters.AbstractSeekType<E> seek_type;
    E value;
    
    public SeekValueUpdateRunnable(DataSourceFilters filter, DataSourceFilters.AbstractSeekType<? extends E> seek_type, E value) {
      this.filter = filter;
      this.seek_type = (DataSourceFilters.AbstractSeekType<E>) seek_type;
      this.value = (E) value;
    }
    
    public void run() {
      filter.setSeekValue(seek_type, value);
    }
  }
}
