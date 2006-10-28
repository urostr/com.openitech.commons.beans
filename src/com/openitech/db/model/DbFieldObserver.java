/*
 * DbFieldObserver.java
 *
 * Created on Èetrtek, 6 april 2006, 18:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.formats.FormatFactory;
import com.openitech.ref.WeakListenerList;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Types;
import javax.swing.SwingUtilities;

/**
 *
 * @author uros
 */
public class DbFieldObserver {
  private transient DbDataSource dataSource = null;
  private String columnName = null;
  
  private transient WeakListenerList activeRowChangeListeners;
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  
  private boolean updatingActiveRow = false;
  private boolean updatingFieldValue = false;
  
  /** Creates a new instance of DbFieldObserver */
  public DbFieldObserver() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged","dataSource_activeRowChanged");
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
  }
  
  public Object getValue() {
    Object result = null;
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0)
          result = dataSource.getObject(columnName);
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'. "+ex.getMessage());
        result = null;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public String getValueAsText() {
    Object result = getValue();
    return (result==null)?"":result.toString();
  }
  
  public int getValueAsInt() {
    int result = 0;
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0)
          result = dataSource.getInt(columnName);
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'.  ["+ex.getMessage()+"]");
        result = 0;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public double getValueAsDouble() {
    double result = 0;
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0)
          result = dataSource.getDouble(columnName);
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'.  ["+ex.getMessage()+"]");
        result = 0;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public Date getValueAsDate() {
    Date result = null;
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0)
          result = FormatFactory.JDBC_DATE_FORMAT.parse(dataSource.getString(columnName));
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'.  ["+ex.getMessage()+"]");
        result = null;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public boolean getValueAsBoolean() {
    boolean result = false;
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0) {
          int type = dataSource.getType(columnName);
          switch (type) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.NUMERIC:
              BigDecimal nvalue = dataSource.getBigDecimal(columnName);
              result=!dataSource.wasNull();
              if (result) //ni bil null
                result=!nvalue.equals(BigDecimal.ZERO);
              break;
            case Types.INTEGER:
            case Types.BIT:
              int ivalue = dataSource.getInt(columnName);
              result=!dataSource.wasNull();
              if (result) //ni bil null
                result=ivalue!=0;
              break;
            case Types.BOOLEAN:
              result=dataSource.getBoolean(columnName);
              break;
            case Types.CHAR:
            case Types.VARCHAR:
              String svalue = dataSource.getString(columnName);
              result=!dataSource.wasNull();
              if (result) { //ni bil null
                svalue = svalue.trim().toUpperCase();
                result = svalue.length()>0 && !(svalue.equals("0") || svalue.startsWith("N") || svalue.startsWith("F"));
              }
              break;
            default: dataSource.getObject(columnName);
            result=!dataSource.wasNull();
            break;
          }
        }
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'. ["+ex.getMessage()+"]");
        result = false;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public byte[] getValueAsByteArray() {
    byte[] result = new byte[] {};
    if (dataSource!=null && columnName!=null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount()>0)
          result = dataSource.getBytes(columnName);
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '"+columnName+"' from the dataSource '"+dataSource.getSelectSql()+"'. "+ex.getMessage());
        result = new byte[] {};
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  
  public void updateValue(byte[] value) throws SQLException {
    if (dataSource!=null && columnName!=null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        dataSource.updateBytes(columnName, value);
        byte[] newvalue = dataSource.getBytes(columnName);
        if ((newvalue==null && value!=null) || (!Arrays.equals(newvalue,value)))
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }
  
  public void updateValue(boolean value) throws SQLException {
    if (dataSource!=null && columnName!=null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if (dataSource.getRowCount()>0) {
          int type = dataSource.getType(columnName);
          switch (type) {
            case Types.BIT:
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
              dataSource.updateInt(columnName, value?1:0);
              break;
            case Types.BOOLEAN:
              dataSource.updateBoolean(columnName, value);
              break;
            case Types.CHAR:
            case Types.VARCHAR:
              dataSource.updateString(columnName,value?"1":"0");
              break;
            default: if (value) {
              dataSource.updateObject(columnName,"1");
            } else
              dataSource.updateNull(columnName);
            break;
          }
        }
        boolean newvalue = this.getValueAsBoolean();
        if (newvalue!=value)
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }
  
  public void updateValue(Object value) throws SQLException {
    if (dataSource!=null && columnName!=null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if (value!=null && (value instanceof java.util.Date)) {
          value = new java.sql.Date( ((java.util.Date) value).getTime() );
        }
        dataSource.updateObject(columnName, value);
        Object newvalue = dataSource.getObject(columnName);
        if (!((newvalue==null && value==null) || (value!=null && value.equals(newvalue))))
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }
  
  public void updateValue(int value) throws SQLException {
    if (dataSource!=null && columnName!=null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        dataSource.updateInt(columnName, value);
        int newvalue = dataSource.getInt(columnName);
        if (newvalue!=value)
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }
  
  public boolean wasNull() {
    boolean result=true;
    if (this.dataSource!=null&&dataSource.getRowCount()>0)
      try {
        result = this.dataSource.wasNull();
      } catch (SQLException ex) {
        result=true;
      }
    
    return result;
  }
  
  public void startUpdate() {
    if (this.dataSource!=null)
      try {
        this.dataSource.startUpdate();
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).warning("can't start updating the row");
      }
  }
  
  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource!=null)
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
    this.dataSource = dataSource;
    if (this.dataSource!=null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      if (columnName!=null)
        fireFieldValueChanged(new ActiveRowChangeEvent(dataSource, this.columnName, -1));
    }
  }
  
  public DbDataSource getDataSource() {
    return dataSource;
  }
  
  public void setColumnName(String columnName) {
    if (columnName!=null && columnName.trim().length()==0)
      columnName = null;
    this.columnName = columnName;
    if (dataSource!=null && columnName!=null)
      fireFieldValueChanged(new ActiveRowChangeEvent(dataSource, this.columnName, -1));
  }
  
  public String getColumnName() {
    return columnName;
  }
  
  public void dataSource_activeRowChanged(ActiveRowChangeEvent event) {
    if (!updatingActiveRow) {
      updatingActiveRow = true;
      try {
        fireFieldValueChanged(new ActiveRowChangeEvent(event.getSource(), columnName, -1));
      } finally {
        updatingActiveRow = false;
      }
    }
  }

  public boolean isUpdatingFieldValue() {
    return updatingFieldValue;
  }
  
  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    if (!updatingFieldValue) {
      updatingFieldValue = true;
      try {
        String columnName = event.getColumnName();
        if (columnName==null && event.getColumnIndex()!=-1) {
          try {
            columnName = dataSource.getColumnName(event.getColumnIndex());
          } catch (SQLException ex) {
            columnName = null;
          }
        }
        if ((columnName!=null) && (columnName.equalsIgnoreCase(this.columnName)))
          fireFieldValueChanged(new ActiveRowChangeEvent(event.getSource(), columnName, -1));
      } finally {
        updatingFieldValue = false;
      }
    }
  }
  
  public synchronized void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (activeRowChangeListeners != null && activeRowChangeListeners.contains(l)) {
      activeRowChangeListeners.removeElement(l);
    }
  }
  
  public synchronized void addActiveRowChangeListener(ActiveRowChangeListener l) {
    WeakListenerList v = activeRowChangeListeners == null ? new WeakListenerList(2) : activeRowChangeListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      activeRowChangeListeners = v;
    }
  }
  
  protected void fireFieldValueChanged(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      java.util.List listeners = activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
    }
  }
  
  private void fireLaterFieldValueChanged(final ActiveRowChangeEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        fireFieldValueChanged(e);
      }
    });
  }
}
