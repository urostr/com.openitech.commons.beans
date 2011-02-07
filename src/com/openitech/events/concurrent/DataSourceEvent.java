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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author uros
 */
public abstract class DataSourceEvent implements Runnable, ConcurrentEvent {

  private static final ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
          30L, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>());
  protected static final Map<Event, Long> timestamps = new ConcurrentHashMap<Event, Long>();
  protected static final Map<Event, Future> tasks = new ConcurrentHashMap<Event, Future>();
  protected final Long timestamp = new Long((new Date()).getTime());
  protected final Event event;
  protected Event suspend;
  protected Event cancel;

  protected DataSourceEvent(DataSourceEvent object) {
    this.event = object.event;
    this.suspend = object.suspend;
    this.cancel = object.cancel;
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
    event.submit(event, true);
  }

  protected void submit(DataSourceEvent event, boolean log) {
    if (log) {
      System.out.println("SUBMITTING:" + event.event.dataSource.getName() + ":" + (event.isSuspended() ? "SUSPENDED" : "ACTIVE"));
    }
    timestamps.remove(event.cancel);
    timestamps.put(event.event, event.timestamp);
    tasks.put(event.event, pool.submit(event));
    if (event.event.type.equals(Event.Type.REFRESH)) {
      event.event.dataSource.updateRefreshPending();
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
    return !(task == null || task.isDone() || task.isCancelled());
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

  @Override
  public abstract void run();

  @Override
  public abstract Object clone();

  public static class Event {

    public enum Type {

      ACTIVE_ROW_CHANGE_LISTENER,
      LIST_DATA_LISTENER,
      TASK,
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
      if (dataSource == null) {
        throw new IllegalArgumentException("DataSource can't be null");
      }
      this.dataSource = dataSource;
      this.type = type;
      this.onEventQueue = onEventQueue;
      this.hash = dataSource.hashCode() + 31 * type.hashCode();
    }

    @Override
    public int hashCode() {
      return this.hash;
    }

    public DbDataSource getDataSource() {
      return dataSource;
    }

    public Event.Type getType() {
      return type;
    }

    @Override
    public boolean equals(Object obj) {
      boolean result = obj instanceof Event;
      if (result) {
        result = this.dataSource.equals(((Event) obj).dataSource)
                && this.type.equals(((Event) obj).type);
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

  public static class ReloadCount {

    private static ReloadCount instance;

    public static ReloadCount getInstance() {
      if (instance == null) {
        instance = new ReloadCount();
      }
      return instance;
    }
    private Map<DbDataSource, Integer> count = new HashMap<DbDataSource, Integer>();

    public Map<DbDataSource, Integer> getCount() {
      return count;
    }

    public void add(DbDataSource dataSource) {
      int old = 0;
      if (count.containsKey(dataSource)) {
        old = count.get(dataSource);
      }
      int newCount = old + 1;
      count.put(dataSource, newCount);

    }

    public void reset(DbDataSource dataSource) {
      count.remove(dataSource);
    }

    public void reset() {
      count.clear();
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();

      for (DbDataSource dbDataSource : count.keySet()) {
        stringBuilder.append(dbDataSource.getName());
        stringBuilder.append(" ::: ");
        stringBuilder.append(count.get(dbDataSource).toString());
        stringBuilder.append("\n");
      }
      return stringBuilder.toString();
    }
  }
}
