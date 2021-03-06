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
 * DataSourceListDataEvent.java
 *
 * Created on Torek, 18 julij 2006, 7:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.events.concurrent;

import com.openitech.db.model.DbDataSource;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class DataSourceListDataEvent extends DataSourceEvent implements ListDataListener {
  ListDataEvent fireEvent;
  ListDataListener listener;
  
  /**
   * Creates a new instance of DataSourceListDataEvent
   */
  protected DataSourceListDataEvent(DataSourceListDataEvent object, ListDataEvent event) {
    super(object);
    this.fireEvent = event;
    this.listener = object.listener;
  }
  
  /** Creates a new instance of DataSourceListDataEvent */
  public DataSourceListDataEvent(DbDataSource dataSource, ListDataListener listener) {
    super(new Event(dataSource, listener));
    this.listener = listener;
  }

  public void run() {
    if (timestamps.get(event).longValue()<=timestamp.longValue()&&fireEvent!=null) {
      switch (fireEvent.getType()) {
        case ListDataEvent.INTERVAL_ADDED: listener.intervalAdded(fireEvent); break;
        case ListDataEvent.INTERVAL_REMOVED: listener.intervalRemoved(fireEvent); break;
        case ListDataEvent.CONTENTS_CHANGED: listener.contentsChanged(fireEvent); break;
      }
    }
  }

  public Object clone() {
    return new DataSourceListDataEvent(this, this.fireEvent);
  }

  public boolean equals(Object obj) {
    boolean result = obj instanceof ListDataListener;
    if (result)
      result = (obj instanceof DataSourceListDataEvent)
                ?event.equals(((DataSourceListDataEvent) obj).event)
                :listener.equals((ListDataListener) obj);
    return result;
  }

  public void intervalAdded(ListDataEvent e) {
    DataSourceEvent.submit(new DataSourceListDataEvent(this, e));
  }

  public void intervalRemoved(ListDataEvent e) {
    DataSourceEvent.submit(new DataSourceListDataEvent(this, e));
  }

  public void contentsChanged(ListDataEvent e) {
    DataSourceEvent.submit(new DataSourceListDataEvent(this, e));
  }
  
  public static class Event extends DataSourceEvent.Event {
    ListDataListener listener;
    
    public Event(DbDataSource dataSource, ListDataListener listener) {
      super(dataSource, 
            DataSourceEvent.Event.Type.LIST_DATA_LISTENER);
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
