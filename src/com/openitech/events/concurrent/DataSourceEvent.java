/*
 * DataSourceEvent.java
 *
 * Created on Sobota, 15 julij 2006, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.events.concurrent;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbNavigatorDataSource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author uros
 */
public abstract class DataSourceEvent implements Runnable, ConcurrentEvent {
  private   static final ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      30L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
//  private   static final ExecutorService pool = Executors.newCachedThreadPool();
  protected static final Map<Event, Long> timestamps = new ConcurrentHashMap<Event, Long>();
  protected static final Map<Event, Future> tasks = new ConcurrentHashMap<Event, Future>();
  
  protected final Long timestamp = new Long((new Date()).getTime());
  protected Event event;
  protected Event suspend;
  protected Event cancel;

  
  protected DataSourceEvent(DataSourceEvent object) {
    this.event = object.event;
    this.suspend = object.suspend;
    this.suspend = object.cancel;
  }

  /**
   * Creates a new instance of DataSourceEvent
   */
  public DataSourceEvent(Event event) {
    this.event = event;
    this.suspend = new Event(event.dataSource, Event.Type.SUSPEND);
    this.cancel = new Event(event.dataSource, Event.Type.CANCEL);
  }
  
  public static void submit(DataSourceEvent event) {
    submit(event, true);
  }
  
  protected static void submit(DataSourceEvent event, boolean log) {
    if ((event instanceof RefreshDataSource)&&
        ((RefreshDataSource) event).getQueuedDelay()<=0) {
        if ((!((RefreshDataSource) event).isTryLock())||
              (event.event.dataSource.canLock())) {
           ((RefreshDataSource) event).load();
        }      
    } else {
      if (log) {
        System.out.println("SUBMITTING:"+event.event.dataSource.getName()+":"+(event.isSuspended()?"SUSPENDED":"ACTIVE"));
      }
      timestamps.remove(event.cancel);
      timestamps.put(event.event, event.timestamp);
      tasks.put(event.event, pool.submit(event));
      if (event.event.type.equals(Event.Type.REFRESH)) {
        event.event.dataSource.updateRefreshPending();
      }
    }
  }
  
  public static void suspend(DbDataSource dataSource) {
    timestamp(new Event(dataSource, Event.Type.SUSPEND));
  }
  
  public static void cancel(DbDataSource dataSource) {
    timestamp(new Event(dataSource, Event.Type.CANCEL));
  }

  public static void resume(DbDataSource dataSource) {
    timestamps.remove(new Event(dataSource, Event.Type.SUSPEND));
  }
  
  public static boolean isRefreshing(DbNavigatorDataSource dataSource) {
    return isRefreshing(dataSource.getDataSource());
  }
  
  public static boolean isRefreshing(DbDataSource dataSource) {
    Future task = tasks.get(new Event(dataSource, Event.Type.REFRESH));
    return !(task==null||task.isDone()||task.isCancelled());
  }

  public static boolean isSuspended(DbDataSource dataSource) {
    return timestamps.containsKey(new Event(dataSource, Event.Type.SUSPEND));
  }
  
  protected boolean isSuspended() {
    return timestamps.containsKey(suspend);
  }
  
  protected boolean isCanceled() {
    return timestamps.containsKey(cancel);
  }

  protected static void timestamp(Event event) {
    timestamps.put(event, new Long((new Date()).getTime()));
  }

  public abstract void run();
  
  public abstract Object clone();
  
  public static class Event {
    public enum Type {
      ACTIVE_ROW_CHANGE_LISTENER,
      LIST_DATA_LISTENER,
      REFRESH,
      SUSPEND,
      CANCEL;
    }
    
    protected DbDataSource dataSource;
    protected Type type;
    protected boolean onEventQueue = false;
    
    protected int hash = 17;
    
    public Event(DbDataSource dataSource, Type type) {
      this(dataSource, type, false);
    }
    public Event(DbDataSource dataSource, Type type, boolean onEventQueue) {
      if (dataSource==null)
        throw new IllegalArgumentException("DataSource can't be null");
      this.dataSource = dataSource;
      this.type = type;
      this.onEventQueue = onEventQueue;
      this.hash = dataSource.hashCode()+31*type.hashCode();
    }
    
    public int hashCode() {
      return this.hash;
    };
    
    public DbDataSource getDataSource() {
      return dataSource;
    }
    
    public Event.Type getType() {
      return type;
    }

    public boolean equals(Object obj) {
      boolean result = obj instanceof Event;
      if (result) {
        result = this.dataSource.equals(((Event) obj).dataSource) && 
                 this.type.equals(((Event) obj).type);
      }
      return result;
    }
    
    public boolean isOnEventQueue() {
      return onEventQueue;
    }

    public void setOnEventQueue(boolean onEventQueue) {
      this.onEventQueue = onEventQueue;
    }

  }
  
}