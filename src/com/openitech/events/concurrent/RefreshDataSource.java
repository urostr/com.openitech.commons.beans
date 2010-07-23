package com.openitech.events.concurrent;

import com.openitech.Settings;
import com.openitech.swing.JXDimBusyLabel;
import com.openitech.db.model.*;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class RefreshDataSource extends DataSourceEvent {
  public static JXDimBusyLabel busy  = null;
  
  List<Object> parameters = new ArrayList<Object>();
  Map<String,Object> defaults = new HashMap<String,Object>();
  boolean filterChange = false;
  private boolean tryLock = true;
  private long queuedDelay = DbDataSource.DEFAULT_QUEUED_DELAY;

  protected RefreshDataSource(RefreshDataSource object) {
    super(object);
    this.parameters = object.parameters;
    this.defaults = object.defaults;
    this.filterChange = object.filterChange;
    this.queuedDelay = object.queuedDelay;
  }
  
  public RefreshDataSource(DbDataSource dataSource) {
    this(dataSource, false);
  }
  
  public RefreshDataSource(DbDataSource dataSource, boolean filterChange) {
    this(dataSource,dataSource.getParameters(),dataSource.getDefaultValues(),filterChange, dataSource.isReloadsOnEventQueue());
  }

  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults) {
    this(dataSource, parameters, defaults, false, dataSource.isReloadsOnEventQueue());
  }
  
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults, boolean filterChange) {
    this(dataSource, parameters, defaults, filterChange, dataSource.isReloadsOnEventQueue());
  }
  
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults, boolean filterChange, boolean onEventQueue) {
    super(new Event(dataSource, Event.Type.REFRESH, onEventQueue));
    if (parameters!=null)
      this.parameters.addAll(parameters);
    else
      this.parameters=null;
    if (defaults!=null)
      this.defaults.putAll(defaults);
    else
      this.defaults=null;
    this.filterChange = filterChange;
    this.queuedDelay = dataSource.getQueuedDelay();
  }

  public void setQueuedDelay(long queuedDelay) {
    this.queuedDelay = queuedDelay;
  }

  public long getQueuedDelay() {
    return queuedDelay;
  }

  /**
   * Get the value of tryLock
   *
   * @return the value of tryLock
   */
  public boolean isTryLock() {
    return tryLock;
  }

  /**
   * Set the value of tryLock
   *
   * @param tryLock new value of tryLock
   */
  public void setTryLock(boolean tryLock) {
    this.tryLock = tryLock;
  }

  
  private void setBusy(final String label) {
    if (busy!=null) {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          busy.setBusy(true);
          if (event.dataSource.getBusyLabel()!=null)
            busy.setText(label);
          else
            busy.setText("Osve�ujem podatke ...");
        }
      });
    }
  }
  
  private void setReady() {
    if (busy!=null) {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          busy.setBusy(false);
          busy.setText("Pripravljen...");
        }
      });
    }
  }
  
  public void run() {
    if (isCanceled()) {
      System.out.println(event.dataSource.getName()+" is canceled. Quitting.");
      return;
    }
    if (isSuspended()) {
      try {
        Thread.sleep(27);
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted ["+event.dataSource.getName()+"]");
      }
    }
    if (isCanceled()) {
     System.out.println(event.dataSource.getName()+" is canceled. Quitting.");
     return;
    }
    if (isSuspended()) { //re-queue
      resubmit();
    } else {
      try {
        System.out.println("QUEUED DELAY:"+this.queuedDelay+"ms:"+event.dataSource.getName()+":"+(isSuspended()?"SUSPENDED":"ACTIVE"));
        Thread.sleep(this.queuedDelay);
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted ["+event.dataSource.getName()+"]");
      }
      if (isCanceled()) {
        System.out.println(event.dataSource.getName()+" is canceled. Quitting.");
        return;
      }
      if (timestamps.get(event).longValue()<=timestamp.longValue()) {
        if (event.isOnEventQueue()) {
          System.out.println("trying to load on EQ:"+event.dataSource.getName());
          try {
            EventQueue.invokeAndWait(new Runnable() {
              public void run() {
                load();
              }
            });
          } catch (Exception ex) {
            Logger.getLogger(Settings.LOGGER).info("Thread interrupted ["+event.dataSource.getName()+"]");
          }
        } else {
          load();
        }
      } else
        Logger.getLogger(Settings.LOGGER).warning("Skipped loading ["+event.dataSource.getName().substring(0,27)+"...]");
    }
  }
  
  private void resubmit() {
    DataSourceEvent.submit(this, false);
  }
  
//  private void lockAndLoad() {
//    if (event.dataSource.canLock()) {
//      load();
//    } else {
//      resubmit();
//    }
//  }
  
  protected void load() {
    System.out.println("LOADING:"+event.dataSource.getName());
    event.dataSource.lock(true, true);
    try {
      if (filterChange)
        try {
          event.dataSource.filterChanged();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error resetting ["+event.dataSource.getName()+"]", ex);
        }
      if (this.defaults!=null)
        event.dataSource.setDefaultValues(defaults);
      if (this.parameters!=null)
        event.dataSource.setParameters(parameters,false);
    } finally {
      event.dataSource.unlock();
    }
    setBusy(event.dataSource.getBusyLabel());
    tasks.remove(event);
    try {
      if (event.dataSource.isDataLoaded())
        event.dataSource.reload(event.dataSource.getRow());
      else
        event.dataSource.reload();
    } catch (SQLException ex) {
      event.dataSource.reload();
    }
    setReady();
  }
  
  public static void timestamp(DbDataSource dataSource) {
    DataSourceEvent.timestamp(new Event(dataSource, Event.Type.REFRESH));
  }
  
  public final Object clone() {
    return new RefreshDataSource(this);
  }
  
}