/*
 * DataSourceActiveRowChangeEvent.java
 *
 * Created on Sobota, 15 julij 2006, 15:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model.concurrent;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.model.DbDataSource;

/**
 *
 * @author uros
 */
public final class DataSourceActiveRowChangeEvent extends DataSourceEvent implements ActiveRowChangeListener {
  ActiveRowChangeEvent fireEvent;
  ActiveRowChangeListener listener;
  
  protected DataSourceActiveRowChangeEvent(DataSourceActiveRowChangeEvent object, ActiveRowChangeEvent event) {
    super(object);
    this.fireEvent = event;
    this.listener = object.listener;
  }
  
  /**
   * Creates a new instance of DataSourceActiveRowChangeEvent
   */
  public DataSourceActiveRowChangeEvent(DbDataSource dataSource, ActiveRowChangeListener listener) {
    super(new Event(dataSource, listener));
    this.listener = listener;
  }

  public void run() {
    if (timestamps.get(event).longValue()<=timestamp.longValue()&&fireEvent!=null) {
      if (fireEvent.getType()==ActiveRowChangeEvent.ROW_CHANGED)
        listener.activeRowChanged(fireEvent);
      else
        listener.fieldValueChanged(fireEvent);
    }
  }

  public void activeRowChanged(ActiveRowChangeEvent event) {
    DataSourceEvent.submit(new DataSourceActiveRowChangeEvent(this, event));
  }

  public void fieldValueChanged(ActiveRowChangeEvent event) {
    DataSourceEvent.submit(new DataSourceActiveRowChangeEvent(this, event));
  }

  public Object clone() {
    return new DataSourceActiveRowChangeEvent(this, this.fireEvent);
  }

  public boolean equals(Object obj) {
    boolean result = obj instanceof ActiveRowChangeListener;
    if (result)
      result = (obj instanceof DataSourceActiveRowChangeEvent)
                ?event.equals(((DataSourceActiveRowChangeEvent) obj).event)
                :listener.equals((ActiveRowChangeListener) obj);
    return result;
  }
  
  public static class Event extends DataSourceEvent.Event {
    ActiveRowChangeListener listener;
    
    public Event(DbDataSource dataSource, ActiveRowChangeListener listener) {
      super(dataSource, 
            DataSourceEvent.Event.Type.ACTIVE_ROW_CHANGE_LISTENER);
      this.listener = listener;
      hash = hash+37*listener.hashCode();
    }

    public boolean equals(Object obj) {
      boolean result = (obj instanceof Event) && super.equals(obj);
      if (result)
        result = this.listener.equals(((Event) obj).listener);
      return result;
    }
  }
  
}
