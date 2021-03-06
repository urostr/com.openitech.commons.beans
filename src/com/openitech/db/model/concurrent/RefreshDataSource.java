package com.openitech.db.model.concurrent;

import com.openitech.Settings;
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
  }
  
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults) {
    this(dataSource, parameters, defaults, false);
  }
  
  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String,Object> defaults, boolean filterChange) {
    super(new Event(dataSource, Event.Type.REFRESH));
    if (parameters!=null)
      this.parameters.addAll(parameters);
    if (defaults!=null)
      this.defaults.putAll(defaults);
    this.filterChange = filterChange;
  }
  
  public void run() {
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
      event.dataSource.setDefaultValues(defaults);
      event.dataSource.setParameters(parameters);
    } else
        Logger.getLogger(Settings.LOGGER).info("Skipped loading ["+event.dataSource.getName()+"]");
  }
  
  public static void timestamp(DbDataSource dataSource) {
    DataSourceEvent.timestamp(new Event(dataSource, Event.Type.REFRESH));
  }

  public final Object clone() {
    return new RefreshDataSource(this);
  }
  
}