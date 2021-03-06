/*
 * DataSourceEvent.java
 *
 * Created on Sobota, 15 julij 2006, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model.concurrent;

import com.openitech.db.model.DbDataSource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author uros
 */
public abstract class DataSourceEvent implements Runnable, ConcurrentEvent {
  private   static final ExecutorService pool = Executors.newFixedThreadPool(3);
  protected static final Map<Event, Long> timestamps = new ConcurrentHashMap<Event, Long>();
  
  protected final Long timestamp = new Long((new Date()).getTime());
  protected Event event;
  
  protected DataSourceEvent(DataSourceEvent object) {
    this.event = object.event;
  }

  /**
   * Creates a new instance of DataSourceEvent
   */
  public DataSourceEvent(Event event) {
    this.event = event;
  }
  
  public static void submit(DataSourceEvent event) {
    timestamps.put(event.event, event.timestamp);
    pool.submit(event);
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
      REFRESH;
    }
    
    protected DbDataSource dataSource;
    protected Type type;
    
    protected int hash = 17;
    
    public Event(DbDataSource dataSource, Type type) {
      if (dataSource==null)
        throw new IllegalArgumentException("DataSource can't be null");
      this.dataSource = dataSource;
      this.type = type;
      this.hash = dataSource.hashCode()+type.hashCode();
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
  }
  
}
