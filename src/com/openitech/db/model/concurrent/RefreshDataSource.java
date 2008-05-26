package com.openitech.db.model.concurrent;

import com.openitech.Settings;
import com.openitech.components.JXDimBusyLabel;
import com.openitech.db.model.*;
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
  
  protected RefreshDataSource(RefreshDataSource object) {
    super(object);
    this.parameters = object.parameters;
    this.defaults = object.defaults;
    this.filterChange = object.filterChange;
  }
  
  public RefreshDataSource(DbDataSource dataSource) {
    super(new Event(dataSource, Event.Type.REFRESH));
    this.suspend = new Event(dataSource, Event.Type.SUSPEND);
  }
  
  public RefreshDataSource(DbDataSource dataSource, boolean filterChange) {
    this(dataSource,null,null,filterChange);
  }
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults) {
    this(dataSource, parameters, defaults, false);
  }
  
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults, boolean filterChange) {
    super(new Event(dataSource, Event.Type.REFRESH));
    if (parameters!=null)
      this.parameters.addAll(parameters);
    else
      this.parameters=null;
    if (defaults!=null)
      this.defaults.putAll(defaults);
    else
      this.defaults=null;
    this.filterChange = filterChange;
  }
  
  public void run() {
    if (isSuspended()) {
      try {
        Thread.sleep(27);
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted ["+event.dataSource.getName()+"]");
      }
    }
    if (isSuspended()) { //re-queue
      DataSourceEvent.submit(this);
    } else {
      try {
        Thread.sleep(event.dataSource.getQueuedDelay());
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted ["+event.dataSource.getName()+"]");
      }
      if (timestamps.get(event).longValue()<=timestamp.longValue()) {
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
        if (busy!=null) {
          busy.setBusy(true);
          if (event.dataSource.getBusyLabel()!=null)
            busy.setText(event.dataSource.getBusyLabel());
          else
            busy.setText("Osvežujem podatke ...");
        }
        tasks.remove(event);
        try {
          event.dataSource.reload(event.dataSource.getRow());
        } catch (SQLException ex) {
          event.dataSource.reload();
        }
        if (busy!=null) {
          busy.setBusy(false);
          busy.setText("Pripravljen...");
        }
      } else
          Logger.getLogger(Settings.LOGGER).fine("Skipped loading ["+event.dataSource.getName().substring(0,27)+"...]");
    }
  }
  
  public static void timestamp(DbDataSource dataSource) {
    DataSourceEvent.timestamp(new Event(dataSource, Event.Type.REFRESH));
  }

  public final Object clone() {
    return new RefreshDataSource(this);
  }
  
}