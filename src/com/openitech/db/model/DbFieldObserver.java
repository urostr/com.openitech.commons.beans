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
import com.openitech.util.Equals;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Clob;
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
public class DbFieldObserver implements com.openitech.db.FieldObserver, java.io.Serializable {

  private transient DbDataSource dataSource = null;
  private String columnName = null;
  private transient WeakListenerList activeRowChangeListeners;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private boolean updatingActiveRow = false;
  private boolean updatingFieldValue = false;
  private transient Object oldValue = null;
  private boolean wasNull = false;

  /** Creates a new instance of DbFieldObserver */
  public DbFieldObserver() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", "dataSource_activeRowChanged");
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
  }

  public boolean isNotEmptyValue() {
    boolean result = false;
    if (dataSource != null && getColumnName() != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      try {
        if (dataSource.getRowCount() > 0) {
          int type = dataSource.getType(getColumnName());
          switch (type) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.NUMERIC:
              BigDecimal nvalue = dataSource.getBigDecimal(getColumnName());
              result = !dataSource.wasNull();
              if (result) //ni bil null
              {
                result = !nvalue.equals(BigDecimal.ZERO);
              }
              break;
            case Types.INTEGER:
            case Types.BIT:
              int ivalue = dataSource.getInt(getColumnName());
              result = !dataSource.wasNull();
              if (result) //ni bil null
              {
                result = ivalue != 0;
              }
              break;
            case Types.BOOLEAN:
              result = dataSource.getBoolean(getColumnName());
              break;
            case Types.CHAR:
            case Types.VARCHAR:
              String svalue = dataSource.getString(getColumnName());
              result = !dataSource.wasNull();
              if (result) { //ni bil null
                svalue = svalue.trim().toUpperCase();
                result = svalue.length() > 0;
              }
              break;
            default:
              dataSource.getObject(getColumnName());
              result = !dataSource.wasNull();
              break;
          }
        }
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + getColumnName() + "' from the dataSource '" + dataSource.getName() + "'. [" + ex.getMessage() + "]");
        result = false;
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  private boolean hasValueChanged() {
    if (oldValue != null) {
      return !oldValue.equals(getValue());
    } else {
      return true; //always check null values
    }
  }

  public Object getValue() {
    Object result = null;
    wasNull = true;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          if (dataSource.getRowCount() > 0) {
            result = dataSource.getObject(columnName);
            if (wasNull = dataSource.wasNull()) {
              result = null;
            }
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'. " + ex.getMessage());
          result = null;
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  public String getValueAsText() {
    Object result = getValue();
    if ((result instanceof Clob) || (result instanceof javax.sql.rowset.serial.SerialClob)) {
      try {
        if (((Clob) result).length() > 0) {
          result = ((Clob) result).getSubString(1L, (int) ((Clob) result).length());
        } else {
          result = "";
        }
      } catch (SQLException ex) {
        Logger.getLogger(DbFieldObserver.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return (result == null) ? "" : result.toString();
  }

  public int getValueAsInt() {
    int result = 0;
    wasNull = true;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          if (dataSource.getRowCount() > 0) {
            result = dataSource.getInt(columnName);
            wasNull = dataSource.wasNull();
          }

          //TODO zakaj pa to? èe da datasource 0 potem bi moral tudi fieldObserver vrniti 0
          if (wasNull) {
            result = Integer.MIN_VALUE;
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'.  [" + ex.getMessage() + "]");
          result = 0;
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  public double getValueAsDouble() {
    double result = 0;
    wasNull = true;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          if (dataSource.getRowCount() > 0) {
            result = dataSource.getDouble(columnName);
            wasNull = dataSource.wasNull();
          }

          if (wasNull) {
            result = Double.MIN_VALUE;
          }
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'.  [" + ex.getMessage() + "]");
          result = 0;
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }
  //TODO motoda ki vraèa date in ne timestamp
  public Date getValueAsDate() {
    Date result = null;
    wasNull = true;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          if (dataSource.getRowCount() > 0) {
            try {
              result = dataSource.getTimestamp(columnName);
              wasNull = dataSource.wasNull();
            } catch (Exception ex) {
              result = FormatFactory.JDBC_DATE_FORMAT.parse(dataSource.getString(columnName));
            }
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'.  [" + ex.getMessage() + "]");
          result = null;
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  public boolean getValueAsBoolean() {
    boolean result = false;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          int type = dataSource.getType(columnName);
          if (dataSource.getRowCount() > 0) {
            switch (type) {
              case Types.BIGINT:
              case Types.DECIMAL:
              case Types.DOUBLE:
              case Types.FLOAT:
              case Types.NUMERIC:
              case Types.INTEGER:
              case Types.BIT:
                Object value = dataSource.getObject(columnName);
                wasNull = dataSource.wasNull();
                if (value instanceof Boolean) {
                  result = (Boolean) value;
                } else {
                  result = !wasNull;
                  if (result) //ni bil null
                  {
                    result = !Equals.equals(value, 0);
                  }
                }
                break;
              case Types.BOOLEAN:
                result = dataSource.getBoolean(columnName);
                wasNull = dataSource.wasNull();
                break;
              case Types.CHAR:
              case Types.VARCHAR:
                String svalue = dataSource.getString(columnName);
                result = !(wasNull = dataSource.wasNull());
                if (result) { //ni bil null
                  svalue = svalue.trim().toUpperCase();
                  result = svalue.length() > 0 && !(svalue.equals("0") || svalue.startsWith("N") || svalue.startsWith("F"));
                }
                break;
              default:
                dataSource.getObject(columnName);
                result = !(wasNull = dataSource.wasNull());
                break;
            }
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'. [" + ex.getMessage() + "]", ex);
          result = false;
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  public byte[] getValueAsByteArray() {
    byte[] result = new byte[]{};
    wasNull = true;
    if (dataSource != null && columnName != null) {
      //dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      if (dataSource.isDataLoaded()) {
        try {
          if (dataSource.getRowCount() > 0) {
            result = dataSource.getBytes(columnName);
            wasNull = dataSource.wasNull();
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't read the value '" + columnName + "' from the dataSource '" + dataSource.getSelectSql() + "'. " + ex.getMessage());
          result = new byte[]{};
        }
      }
      //dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
    }
    return result;
  }

  public void updateValue(byte[] value) throws SQLException {
    if (dataSource != null && columnName != null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        dataSource.updateBytes(columnName, value);
        byte[] newvalue = dataSource.getBytes(columnName);
        if ((newvalue == null && value != null) || (!Arrays.equals(newvalue, value))) {
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
        }
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  public void updateValue(boolean value) throws SQLException {
    if (dataSource != null && columnName != null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if (dataSource.getRowCount() > 0) {
          int type = dataSource.getType(columnName);
          switch (type) {
            case Types.BIT:
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
              if (dataSource.getObject(columnName) instanceof Boolean) {
                dataSource.updateBoolean(columnName, value);
              } else {
                dataSource.updateInt(columnName, value ? 1 : 0);
              }
              break;
            case Types.BOOLEAN:
              dataSource.updateBoolean(columnName, value);
              break;
            case Types.CHAR:
            case Types.VARCHAR:
              dataSource.updateString(columnName, value ? "1" : "0");
              break;
            default:
              if (value) {
                dataSource.updateObject(columnName, "1");
              } else {
                dataSource.updateNull(columnName);
              }
              break;
          }
        }
        boolean newvalue = this.getValueAsBoolean();
        if (newvalue != value) {
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
        }
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  public void updateValue(Object value) throws SQLException {
    if (dataSource != null && columnName != null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if (value != null && (value instanceof java.util.Date)) {
          value = new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        dataSource.updateObject(columnName, value);
        Object newvalue = dataSource.getObject(columnName);
//        if (!((newvalue==null && value==null) || (value!=null && value.equals(newvalue))))
        if (!Equals.equals(newvalue, value)) {
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
        }
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  public void updateValue(int value) throws SQLException {
    if (dataSource != null && columnName != null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        dataSource.updateInt(columnName, value);
        int newvalue = dataSource.getInt(columnName);
        if (newvalue != value) {
          fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
        }
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  public void updateInputStream(BufferedInputStream value) throws SQLException {
    if (dataSource != null && columnName != null) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        try {
          if (value == null) {
            dataSource.updateObject(columnName, null, java.sql.Types.VARBINARY);
          } else {
            dataSource.updateBinaryStream(columnName, value, value.available());
          }
        } catch (IOException ex) {
          Logger.getLogger(DbFieldObserver.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (value != null) {
          InputStream newvalue = dataSource.getBinaryStream(columnName);
          if (newvalue != value) {
            fireLaterFieldValueChanged(new ActiveRowChangeEvent(dataSource, columnName, -1));
          }
        }
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
    }
  }

  public boolean wasNull() {
    return wasNull;
//    boolean result = true;
//    if (this.dataSource != null && dataSource.getRowCount() > 0) {
//      try {
//        result = this.dataSource.wasNull();
//      } catch (SQLException ex) {
//        result = true;
//      }
//    }
//    return result;
  }

  public void startUpdate() {
    if (this.dataSource != null) {
      try {
        this.dataSource.startUpdate();
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).warning("can't start updating the row");
      }
    }
  }

  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource != null) {
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
    }
    this.dataSource = dataSource;
    if (this.dataSource != null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      if (columnName != null) {
        fireFieldValueChanged(new ActiveRowChangeEvent(dataSource, this.columnName, -1));
      }
    }
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  public void setColumnName(String columnName) {
    if (columnName != null && columnName.trim().length() == 0) {
      columnName = null;
    }
    this.columnName = columnName;
    if (dataSource != null && columnName != null) {
      fireFieldValueChanged(new ActiveRowChangeEvent(dataSource, this.columnName, -1));
    }
  }

  public String getColumnName() {
    return columnName;
  }

  //TODO fireactiveRowChanged?
  public void dataSource_activeRowChanged(ActiveRowChangeEvent event) {
    if (!updatingActiveRow) {
      if (hasValueChanged()) {
        updatingActiveRow = true;
        try {
          oldValue = getValue();
          fireFieldValueChanged(new ActiveRowChangeEvent(event.getSource(), columnName, -1));
        } finally {
          updatingActiveRow = false;
        }
      }
    }
  }

  public boolean isUpdatingFieldValue() {
    return updatingFieldValue;
  }

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    if (!updatingFieldValue) {
      if (hasValueChanged()) {
        String columnName = event.getColumnName();
        if (columnName == null && event.getColumnIndex() != -1) {
          try {
            columnName = dataSource.getColumnName(event.getColumnIndex());
          } catch (SQLException ex) {
            columnName = null;
          }
        }
        if ((columnName != null) && (columnName.equalsIgnoreCase(this.columnName))) {
          oldValue = getValue();
          fireFieldValueChanged(new ActiveRowChangeEvent(event.getSource(), columnName, -1));
        }
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
      updatingFieldValue = true;
      try {
        java.util.List listeners = activeRowChangeListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
          ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
        }
      } finally {
        updatingFieldValue = false;
      }
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
