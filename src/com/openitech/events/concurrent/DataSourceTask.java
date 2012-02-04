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

import com.openitech.db.model.DbDataSource;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class DataSourceTask extends DataSourceEvent {

  public DataSourceTask(DbDataSource dataSource, boolean onEventQueue, Runnable... task) {
    super(new Event(dataSource, Event.Type.TASK, onEventQueue));
    this.task = task;
  }

  protected DataSourceTask(DataSourceTask object) {
    super(object.event);
    this.task = object.task;
  }
  protected Runnable[] task;

  /**
   * Get the value of task
   *
   * @return the value of task
   */
  public Runnable[] getTask() {
    return task;
  }

  /**
   * Set the value of task
   *
   * @param task new value of task
   */
  public void setTask(Runnable... task) {
    this.task = task;
  }

  @Override
  public void run() {
    if (isRefreshing(event.dataSource)) {
      try {
        Thread.sleep(27);
      } catch (InterruptedException ex) {
        Logger.getLogger(DataSourceTask.class.getName()).info("Thread interrupted [" + event.dataSource + "]");
      }
    }
    if (isRefreshing(event.dataSource)) {
      resubmit();
    } else {
      try {
        for (Runnable runnable : task) {
          if (event.isOnEventQueue()) {
            EventQueue.invokeAndWait(runnable);
          } else {
            runnable.run();
          }
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(DataSourceTask.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
        Logger.getLogger(DataSourceTask.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void resubmit() {
    this.submit(this, false);
  }

  @Override
  public Object clone() {
    return new DataSourceTask(this);
  }
}
