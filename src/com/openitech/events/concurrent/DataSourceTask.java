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
