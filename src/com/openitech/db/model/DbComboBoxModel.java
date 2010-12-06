/*
 * DbComboBoxModel.java
 *
 * Created on Ponedeljek, 1 maj 2006, 7:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.Settings;
import com.openitech.util.Equals;
import com.openitech.ref.events.ListDataWeakListener;
import com.openitech.ref.events.PropertyChangeWeakListener;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class DbComboBoxModel<K> extends AbstractListModel implements ComboBoxModel, ListDataListener, PropertyChangeListener, Iterable<DbComboBoxModel.DbComboBoxEntry<K, String>> {

  private final Vector<DbComboBoxEntry<K, String>> entries = new Vector<DbComboBoxEntry<K, String>>();
  private String keyColumnName = null;
  private String[] valueColumnNames = null;
  private String[] extendedValueColumnNames = null;
  private String[] separator = new String[]{" "};
  private int selectedIndex = -1;
  private Object selectedItem;
  private transient DbDataSource dataSource = null;
  private transient ListDataWeakListener listDataWeakListener = new ListDataWeakListener(this);
  private transient PropertyChangeListener propertyChangeWeakListener = new PropertyChangeWeakListener(this);
  private boolean updatingEntries = false;
  private boolean waitForEventQueue = true;

  /** Creates a new instance of DbComboBoxModel */
  public DbComboBoxModel() {
  }

  public DbComboBoxModel(List<DbComboBoxEntry<K, String>> entries) {
    this.entries.addAll(entries);
  }

  public DbComboBoxModel(DbComboBoxModel model) {
    this(model.entries);
  }

  public DbComboBoxModel(DbDataSource dataSource, String keyColumnName, String[] valueColumnNames) {
    this(dataSource, keyColumnName, valueColumnNames, " ");
  }

  public DbComboBoxModel(DbDataSource dataSource, String keyColumnName, String[] valueColumnNames, String... separator) {
    this.keyColumnName = keyColumnName;
    this.valueColumnNames = valueColumnNames;
    this.separator = separator;
    setDataSource(dataSource);
  }

  public void setKeyColumnName(String keyColumnName) {
    String oldvalue = this.keyColumnName;
    this.keyColumnName = keyColumnName;
    if ((oldvalue != null && !oldvalue.equals(keyColumnName)) || (oldvalue != keyColumnName)) {
      updateEntries(false);
    }
  }

  public String getKeyColumnName() {
    return keyColumnName;
  }

  public void setValueColumnNames(String[] valueColumnNames) {
    this.valueColumnNames = valueColumnNames;
    updateEntries(false);
  }

  public String[] getValueColumnNames() {
    return this.valueColumnNames;
  }

  public void setExtendedValueColumnNames(String[] extendedValueColumnNames) {
    this.extendedValueColumnNames = extendedValueColumnNames;
    updateEntries(false);
  }

  public String[] getExtendedValueColumnNames() {
    return extendedValueColumnNames;
  }
  protected List<DbComboBoxEntry> beforeEntriesValues = new ArrayList<DbComboBoxEntry>();

  /**
   * Get the value of beforeEntriesValues
   *
   * @return the value of beforeEntriesValues
   */
  public List<DbComboBoxEntry> getBeforeEntriesValues() {
    return beforeEntriesValues;
  }

  /**
   * Set the value of beforeEntriesValues
   *
   * @param beforeEntriesValues new value of beforeEntriesValues
   */
  public void setBeforeEntriesValues(List<DbComboBoxEntry> beforeEntriesValues) {
    this.beforeEntriesValues = beforeEntriesValues;
    updateEntries(false);
  }

  public void setSeparator(String... separator) {
    if (separator.length == 0) {
      throw new IllegalArgumentException("Undefined separator");
    }
    String[] oldvalue = this.separator;
    this.separator = separator;
    if (oldvalue != null && !java.util.Arrays.equals(oldvalue, separator)) {
      updateEntries(false);
    }
  }

  public String[] getSeparator() {
    return separator;
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public int indexOf(Object item) {
    return entries.indexOf(item);
  }

  public void setDataSource(DbDataSource dataSource) {
    DbDataSource oldvalue = this.dataSource;
    if (oldvalue != null) {
      oldvalue.removeListDataListener(listDataWeakListener);
      oldvalue.removePropertyChangeListener("selectSql", propertyChangeWeakListener);
    }
    this.dataSource = dataSource;
    try {
      waitForEventQueue = false;
      if (dataSource != null) {
        dataSource.setReloadsOnEventQueue(true);
        dataSource.addListDataListener(listDataWeakListener);
        dataSource.addPropertyChangeListener("selectSql", propertyChangeWeakListener);
      }
      if (oldvalue != dataSource) {
        updateEntries();
      }
    } finally {
      waitForEventQueue = true;
    }
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  private void updateEntries() {
    updateEntries(waitForEventQueue);
  }

  private void updateEntries(boolean wait) {
    try {
      if (EventQueue.isDispatchThread()) {
        updateEntries(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      } else if (wait) {
        EventQueue.invokeAndWait(new Runnable() {

          @Override
          public void run() {
            try {
              updateEntries(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
            } catch (Exception ex) {
              Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries.", ex);
            }
          }
        });
      } else {
        EventQueue.invokeLater(new Runnable() {

          @Override
          public void run() {
            try {
              updateEntries(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
            } catch (Exception ex) {
              Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries.", ex);
            }
          }
        });
      }
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries.", ex);
    }
  }

  protected void updateEntries(ListDataEvent e) {
    if (dataSource != null
            && keyColumnName != null
            && valueColumnNames != null) {
      dataSource.lock();
      boolean safeMode = dataSource.isSafeMode();
      if (!dataSource.isDataLoaded()) {
        dataSource.setSafeMode(false);
      }
      try {
        int size = dataSource.getRowCount();
        StringBuilder result;
        Set<String> columns;
        java.util.Map<String, Integer> columnIndex;
        List values;
        K key;
        Object value;
        int min;
        int max;

        if (e.getType() == ListDataEvent.INTERVAL_REMOVED) {
          min = 1;
          max = size;
        } else {
          min = Math.max(Math.min(e.getIndex0(), e.getIndex1()), 0) + 1;
          max = Math.max(e.getIndex0(), e.getIndex1()) + 1;
          if (max < 1) {
            max = size;
          }
        }
        entries.setSize(Math.max(0, size + beforeEntriesValues.size()));

        columns = new HashSet<String>();
        columns.add(keyColumnName);
        for (String column : valueColumnNames) {
          columns.add(column);
        }
        if (extendedValueColumnNames != null) {
          for (String column : extendedValueColumnNames) {
            columns.add(column);
          }
        }

        String[] valueColumns = new String[columns.size()];
        columns.toArray(valueColumns);

        int index = 0;
        for (DbComboBoxEntry dbComboBoxEntry : beforeEntriesValues) {
          entries.set(index++, dbComboBoxEntry);
        }

        for (int row = min; row <= max; row++) {
          key = (K) dataSource.getValueAt(row, keyColumnName, valueColumns);
          result = new StringBuilder();
          values = new ArrayList();
          columnIndex = new java.util.HashMap<String, Integer>();

          for (int f = 0; f < valueColumnNames.length; f++) {
            value = this.dataSource.getValueAt(row, valueColumnNames[f], valueColumns);
            values.add(value);
            columnIndex.put(valueColumnNames[f].toUpperCase(), values.size() - 1);
            if (result.length() > 0 && value != null && value.toString().length() > 0) {
              result.append(separator[Math.min(Math.max(f - 1, 0), separator.length - 1)]);
            }
            result.append(value == null ? "" : value);
          }
          if (extendedValueColumnNames != null) {
            for (String column : extendedValueColumnNames) {
              values.add(this.dataSource.getValueAt(row, column, valueColumns));
              columnIndex.put(column.toUpperCase(), values.size() - 1);
            }
          }
          entries.set(row - 1 + beforeEntriesValues.size(), new DbComboBoxEntry<K, String>(key, values, columnIndex, result.toString().trim()));
        }
        if ((selectedItem instanceof DbComboBoxEntry) || (selectedItem == null)) {
          selectedIndex = max > 0 ? 0 : -1;
          selectedItem = max > 0 ? entries.elementAt(0) : null;
        } else {
          selectedIndex = -1;
        }

        updatingEntries = true;
        try {
          fireContentsChanged(this, min - 1, max - 1);
        } finally {
          updatingEntries = false;
        }
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries from the dataSource (" + dataSource.getName() + ").", ex);
      } finally {
        dataSource.setSafeMode(safeMode);
        dataSource.unlock();
      }
    }
  }

  public boolean isUpdatingEntries() {
    return updatingEntries;
  }

  /**
   *
   * Set the selected item. The implementation of this  method should notify
   * all registered <code>ListDataListener</code>s that the contents
   * have changed.
   *
   *
   * @param anItem the list object to select or <code>null</code>
   *        to clear the selection
   */
  public void setSelectedItem(Object anItem) {
    Object selectItem = null;
    if ((entries != null) && (anItem != null) && (anItem instanceof String)) {
      for (DbComboBoxEntry<K, String> entry : entries) {
        if (entry.value.equals((String) anItem)) {
          selectItem = entry;
          break;
        }
      }
    } else {
      selectItem = anItem;
    }
    selectedItem = selectItem;
    if (entries != null) {
      selectedIndex = entries.indexOf(selectItem);
    }
  }

  /**
   *
   * Returns the selected item
   *
   * @return The selected item or <code>null</code> if there is no selection
   */
  public Object getSelectedItem() {
    return ((selectedIndex < 0) || (selectedIndex >= entries.size())) ? selectedItem : entries.get(selectedIndex);
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index the requested index
   * @return the value at <code>index</code>
   */
  public Object getElementAt(int index) {
    return entries.get(index);
  }

  /**
   *
   * Returns the length of the list.
   *
   * @return the length of the list
   */
  public int getSize() {
    return entries.size();
  }

  /**
   * Sent after the indices in the index0,index1 interval
   * have been removed from the data model.  The interval
   * includes both index0 and index1.
   *
   *
   * @param e  a <code>ListDataEvent</code> encapsulating the
   *    event information
   */
  public void intervalRemoved(ListDataEvent e) {
    updateEntries();
  }

  /**
   *
   * Sent after the indices in the index0,index1
   * interval have been inserted in the data model.
   * The new interval includes both index0 and index1.
   *
   *
   * @param e  a <code>ListDataEvent</code> encapsulating the
   *    event information
   */
  public void intervalAdded(ListDataEvent e) {
    updateEntries();
  }

  /**
   *
   * Sent when the contents of the list has changed in a way
   * that's too complex to characterize with the previous
   * methods. For example, this is sent when an item has been
   * replaced. Index0 and index1 bracket the change.
   *
   *
   * @param e  a <code>ListDataEvent</code> encapsulating the
   *    event information
   */
  public void contentsChanged(ListDataEvent e) {
    updateEntries();
  }

  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    updateEntries();
  }

  @Override
  public Iterator<DbComboBoxEntry<K, String>> iterator() {
    return entries.iterator();
  }

  public boolean isValidEntry(DbComboBoxEntry entry) {
    boolean result = false;
    try {
      for (DbComboBoxEntry<K, String> dbComboBoxEntry : entries) {
        if (dbComboBoxEntry != null) {
          K key = dbComboBoxEntry.getKey();
          if (key instanceof String) {
            String sifra = ((String) key);
            if (entry.getKey() instanceof String) {
              if (sifra.startsWith((String) entry.getKey())) {
                return true;
              }
            }
          }
        }
      }

      if (entries.contains(entry)) {
        result = true;
      }
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, null, ex);
      result = true;
    }
    return result;
  }

  public static class DbComboBoxEntry<K, V> {

    K key;
    V value;
    List values;
    java.util.Map<String, Integer> columnIndex;

    public DbComboBoxEntry(K key, List values, V value) {
      this(key, values, null, value);
    }

    public DbComboBoxEntry(K key, List values, java.util.Map<String, Integer> columnIndex, V value) {
      this.key = key;
      this.values = values;
      this.value = value;
      this.columnIndex = columnIndex == null ? new java.util.HashMap<String, Integer>() : columnIndex;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj != null && (obj instanceof DbComboBoxEntry)) {
        if ((key != null) && (key instanceof Number) && (((DbComboBoxEntry) obj).key instanceof Number)) {
          return ((Number) this.key).doubleValue() == ((Number) ((DbComboBoxEntry) obj).key).doubleValue();
        } else {
          return Equals.equals(this.key, ((DbComboBoxEntry) obj).key);
        }
      } else {
        return Equals.equals(this.key, obj);
      }
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 79 * hash + (this.key != null ? this.key.hashCode() : 0);
      return hash;
    }

    @Override
    public String toString() {
      return value == null ? "" : value.toString();
    }

    public K getKey() {
      return key;
    }

    public Object getValue(String column) {
      column = column.toUpperCase();
      if (columnIndex.containsKey(column)) {
        return values.get(columnIndex.get(column));
      } else {
        return null;
      }
    }

    public List getValues() {
      return Collections.unmodifiableList(values);
    }
  }
}
