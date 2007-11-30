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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.List;
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
public class DbComboBoxModel<K> extends AbstractListModel implements ComboBoxModel, ListDataListener, PropertyChangeListener {
  private final Vector<DbComboBoxEntry<K,String>> entries = new Vector<DbComboBoxEntry<K,String>>();
  private String keyColumnName = null;
  private String[] valueColumnNames = null;
  private String[] separator = new String[] {" "};
  private int selectedIndex = -1;
  private transient DbDataSource dataSource = null;
  
  private transient ListDataWeakListener   listDataWeakListener       = new ListDataWeakListener(this);
  private transient PropertyChangeListener propertyChangeWeakListener = new PropertyChangeWeakListener(this);
  
  private boolean updatingEntries = false;
  
  /** Creates a new instance of DbComboBoxModel */
  public DbComboBoxModel() {
  }
  
  public DbComboBoxModel(List<DbComboBoxEntry<K,String>> entries) {
    this.entries.addAll(entries);
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
    if ((oldvalue!=null && !oldvalue.equals(keyColumnName))||(oldvalue!=keyColumnName)) {
      UpdateEntries();
    }
  }
  
  public String getKeyColumnName() {
    return keyColumnName;
  }
  
  public void setValueColumnNames(String[] valueColumnNames) {
    this.valueColumnNames = valueColumnNames;
    UpdateEntries();
  }
  
  public String[] getValueColumnNames() {
    return this.valueColumnNames;
  }
  
  public void setSeparator(String... separator) {
    if (separator.length==0)
      throw new IllegalArgumentException("Undefined separator");
    String[] oldvalue = this.separator;
    this.separator = separator;
    if (oldvalue!=null && !java.util.Arrays.equals(oldvalue,separator)) {
      UpdateEntries();
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
    if (oldvalue!=null) {
      oldvalue.removeListDataListener(listDataWeakListener);
      oldvalue.removePropertyChangeListener("selectSql", propertyChangeWeakListener);
    }
    this.dataSource = dataSource;
    if (dataSource!=null) {
      dataSource.addListDataListener(listDataWeakListener);
      dataSource.addPropertyChangeListener("selectSql", propertyChangeWeakListener);
    }
    if (oldvalue!=dataSource) {
      UpdateEntries();
    }
  }
  
  public DbDataSource getDataSource() {
    return dataSource;
  }
  
  private void UpdateEntries() {
    try {
      UpdateEntries(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED, -1, -1));
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries.", ex);    
    }
  }
  private void UpdateEntries(ListDataEvent e) {
    if (dataSource!=null &&
            keyColumnName != null &&
            valueColumnNames != null) {
      dataSource.lock();
      try {
        int size = dataSource.getRowCount();
        StringBuffer result;
        K key;
        Object value;
        int min;
        int max;
        
        if  (e.getType()==ListDataEvent.INTERVAL_REMOVED) {
          min=1;
          max=size;
        } else {
          min = Math.max(Math.min(e.getIndex0(),e.getIndex1()),0)+1;
          max = Math.max(e.getIndex0(),e.getIndex1())+1;
          if (max<1)
            max=size;
        }
        entries.setSize(size);
        for (int row=min; row<=max; row++) {
          key = (K) dataSource.getValueAt(row, keyColumnName);
          result = new StringBuffer();
          for (int f=0; f<valueColumnNames.length; f++) {
            value = this.dataSource.getValueAt(row,valueColumnNames[f]);
            if (result.length()>0&&value!=null&&value.toString().length()>0)
              result.append(separator[Math.min(Math.max(f-1,0), separator.length-1)]);
            result.append(value==null?"":value);
          }
          entries.set(row-1,new DbComboBoxEntry<K,String>(key,result.toString()));
        }
        selectedIndex = max>0?0:-1;
        
        updatingEntries = true;
        try {
          fireContentsChanged(this,min-1,max-1);
        } finally {
          updatingEntries = false;
        }
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update combo box entries from the dataSource ("+dataSource.getName()+").", ex);
      } finally {
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
    selectedIndex = entries.indexOf(anItem);
  }
  
  /**
   *
   * Returns the selected item
   *
   * @return The selected item or <code>null</code> if there is no selection
   */
  public Object getSelectedItem() {
    return ((selectedIndex<0)||(selectedIndex>=entries.size()))?null:entries.get(selectedIndex);
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
    UpdateEntries();
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
    UpdateEntries();
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
    UpdateEntries();
  }
  
  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    UpdateEntries();
  }
  
  
  public static class DbComboBoxEntry<K,V> {
    K key;
    V value;
    
    public DbComboBoxEntry(K key,V value) {
      this.key = key;
      this.value = value;
    }
    
    public boolean equals(Object obj) {
      if (obj!=null && (obj instanceof DbComboBoxEntry)) {
        if ((key!=null) && (key instanceof Number) && (((DbComboBoxEntry) obj).key instanceof Number))
          return ((Number) this.key).doubleValue()==((Number) ((DbComboBoxEntry) obj).key).doubleValue();
        else
          return Equals.equals(this.key,((DbComboBoxEntry) obj).key);
      } else
        return Equals.equals(this.key,obj);
    }
    
    public String toString() {
      return value.toString();
    }
    
    public K getKey() {
      return key;
    }
  }
  
}
