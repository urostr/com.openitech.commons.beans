package com.openitech.events.concurrent;

import com.openitech.Settings;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.connection.DbConnection;
import com.openitech.swing.JXDimBusyLabel;
import com.openitech.db.model.*;
import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RefreshDataSource extends DataSourceEvent {

  public static JXDimBusyLabel busy = null;
  List<Object> parameters = new ArrayList<Object>();
  Map<String, Object> defaults = new HashMap<String, Object>();
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
    this(dataSource, dataSource.getParameters(), dataSource.getDefaultValues(), filterChange, dataSource.isReloadsOnEventQueue());
  }

  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String, Object> defaults) {
    this(dataSource, parameters, defaults, false, dataSource.isReloadsOnEventQueue());
  }

  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String, Object> defaults, boolean filterChange) {
    this(dataSource, parameters, defaults, filterChange, dataSource.isReloadsOnEventQueue());
  }

  public RefreshDataSource(DbDataSource dataSource, List<Object> parameters, Map<String, Object> defaults, boolean filterChange, boolean onEventQueue) {
    super(new Event(dataSource, Event.Type.REFRESH, onEventQueue));
    if (parameters != null) {
      this.parameters.addAll(parameters);
    } else {
      this.parameters = null;
    }
    if (defaults != null) {
      this.defaults.putAll(defaults);
    } else {
      this.defaults = null;
    }
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

  public static void setBusy() {
    setBusy(null);
  }
  private static volatile int busyCount = 0;

  private synchronized static void setBusy(final String label) {
    if (busy != null) {
      EventQueue.invokeLater(new Runnable() {

        public void run() {
          busy.setBusy(true);
          busyCount++;
          if (label != null && !label.equals("")) {
            busy.setText(label);
          } else {
            busy.setText("Osvežujem podatke ...");
          }
        }
      });
    }
//    System.out.println("Busy!");
  }

  public synchronized static void setReady() {
    if (busy != null) {
      EventQueue.invokeLater(new Runnable() {

        public void run() {
          if (busyCount > 0) {
            busyCount--;
          }
          if (busyCount == 0) {
            busy.setBusy(false);
            busy.setText("Pripravljen...");
//            System.out.println("Ready!");
          }
        }
      });
    }

  }

  public void run() {
    if (isCanceled()) {
      System.out.println(event.dataSource + " is canceled. Quitting.");
      return;
    }
    if (isSuspended()) {
      try {
        Thread.sleep(27);
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted [" + event.dataSource + "]");
      }
    }
    if (isCanceled()) {
      System.out.println(event.dataSource + " is canceled. Quitting.");
      return;
    }
    if (isSuspended()) { //re-queue
      resubmit();
    } else {
      if (timestamps.get(event).longValue() > timestamp.longValue()) {
        return;
      }
      try {
        System.out.println("QUEUED DELAY:" + this.queuedDelay + "ms:" + event.dataSource + ":" + (isSuspended() ? "SUSPENDED" : "ACTIVE"));
        Thread.sleep(this.queuedDelay);
      } catch (InterruptedException ex) {
        Logger.getLogger(Settings.LOGGER).info("Thread interrupted [" + event.dataSource + "]");
      }
      if (isCanceled()) {
        System.out.println(event.dataSource + " is canceled. Quitting.");
        return;
      }
      if (timestamps.get(event).longValue() <= timestamp.longValue()) {
        load();
      } else {
        Logger.getLogger(Settings.LOGGER).warning("Skipped loading [" + event.dataSource + "...]");
      }
    }
  }

  private void resubmit() {
    this.submit(this, false);
  }

  @Override
  protected void submit(DataSourceEvent event, boolean log) {
    if ((event instanceof RefreshDataSource)
            && ((RefreshDataSource) event).getQueuedDelay() <= 0) {
      if ((!((RefreshDataSource) event).isTryLock()) || (event.event.dataSource.canLock())) {
        ((RefreshDataSource) event).load();
      }
    } else {
      super.submit(event, log);
    }
  }
  protected Boolean shadowLoading;

  /**
   * Get the value of shadowLoading
   *
   * @return the value of shadowLoading
   */
  public boolean isShadowLoading() {
    if (shadowLoading == null) {
      this.shadowLoading = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(DbConnection.DB_SHADOW_LOADING, "false"));
    }
    return shadowLoading;
  }

  /**
   * Set the value of shadowLoading
   *
   * @param shadowLoading new value of shadowLoading
   */
  public void setShadowLoading(boolean shadowLoading) {
    this.shadowLoading = shadowLoading;
  }
  protected boolean loading = false;

  /**
   * Get the value of loading
   *
   * @return the value of loading
   */
  public boolean isLoading() {
    return loading;
  }

  protected void load() {
    if (isShadowLoading()) {
      loadCopy();
    } else {
      if (event.isOnEventQueue() && !EventQueue.isDispatchThread()) {
        System.out.println("trying to load on EQ:" + event.dataSource);
        try {
          EventQueue.invokeAndWait(new Runnable() {

            public void run() {
              loadOriginal();
            }
          });
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).info("Thread interrupted [" + event.dataSource + "]");
        }
      } else {
        loadOriginal();
      }
    }
  }

  protected void loadOriginal() {
    System.out.println("LOADING:" + event.dataSource);
    event.dataSource.lock(true, true);
    try {
      if (filterChange) {
        try {
          event.dataSource.filterChanged();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error resetting [" + event.dataSource + "]", ex);
        }
      }
      if (this.defaults != null) {
        event.dataSource.setDefaultValues(defaults);
      }
      if (this.parameters != null) {
        event.dataSource.setParameters(parameters, false);
      }
    } finally {
      event.dataSource.unlock();
    }
    setBusy(event.dataSource.getBusyLabel());
    tasks.remove(event);
    try {
      if (event.dataSource.isDataLoaded()) {
        event.dataSource.reload(event.dataSource.getRow());
      } else {
        event.dataSource.reload();
      }
    } catch (SQLException ex) {
      event.dataSource.reload();
//    } catch (IllegalStateException ex) {
//      Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Error reloading ["+event.dataSource+"]:"+ex.getMessage());
//      if (isLastInQueue()) {
//        resubmit();
//      }
    } catch (Throwable thw) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error reloading [" + event.dataSource + "]", thw);
    }
    setReady();
  }

  protected void loadCopy() {
    try {
      loading = true;
      setBusy();
      final DbDataSource dataSource = event.dataSource.copy();

      dataSource.setSafeMode(false);

      System.out.println("LOADING:" + dataSource);
      if (filterChange) {
        try {
          dataSource.filterChanged();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error resetting [" + dataSource + "]", ex);
        }
      }
      if (this.defaults != null) {
        dataSource.setDefaultValues(defaults);
      }
      if (this.parameters != null) {
        dataSource.setParameters(parameters, false);
      }
      int row = 0;
      try {
        boolean reload = false;
        if (dataSource.isDataLoaded()) {
          reload = dataSource.reload(dataSource.getRow());
        } else {
          reload = dataSource.reload();
        }
        if (reload) {
          row = dataSource.getRow();
        } else {
          //Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Error refreshing [" + dataSource + "]");
        }
      } catch (SQLException ex) {
        dataSource.reload();
      } catch (Throwable thw) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error reloading [" + dataSource + "]", thw);
      }
      loading = false;
      tasks.remove(event);

      if (!Thread.interrupted()&&isLastInQueue()) {
        if (event.isOnEventQueue() && !EventQueue.isDispatchThread()) {
          final int r = row;
          System.out.println("trying to load on EQ:" + event.dataSource);
          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                event.dataSource.loadData(dataSource, r);
              }
            });
          } catch (Exception ex) {
            Logger.getLogger(Settings.LOGGER).info("Thread interrupted [" + event.dataSource + "]");
          }
        } else {
          event.dataSource.loadData(dataSource, row);
        }
      }
    } finally {
      loading = false;
      tasks.remove(event);
      setReady();
    }

  }

  public static void timestamp(DbDataSource dataSource) {
    DataSourceEvent.timestamp(new Event(dataSource, Event.Type.REFRESH));
  }

  @Override
  public final Object clone() {
    return new RefreshDataSource(this);
  }

  protected boolean isLastInQueue() {
    return timestamps.get(event).longValue() == timestamp.longValue();
  }
}
