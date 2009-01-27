/*
 * DbDataSourceIndex.java
 *
 * Created on Sobota, 12 januar 2008, 12:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.ref.WeakListenerList;
import com.openitech.ref.events.ListDataWeakListener;
import com.openitech.util.Equals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class DbDataSourceIndex implements DbNavigatorDataSourceIndex<DbDataSource>, ListDataListener, ActiveRowChangeListener {

  private final ListDataWeakListener listDataListener = new ListDataWeakListener(this);
  private final ActiveRowChangeWeakListener fieldListener = new ActiveRowChangeWeakListener(this);
  private DbDataSource dataSource;
  private List<String> keys = new ArrayList<String>();
  private transient WeakListenerList listDataListeners;
  private transient Map<DbIndexKey, SortedSet<Integer>> index = new LinkedHashMap<DbIndexKey, SortedSet<Integer>>();
  private transient Map<Integer, DbIndexKey> r_index = new HashMap<Integer, DbIndexKey>();
  private transient List<DbFieldObserver> fields = new ArrayList<DbFieldObserver>();
  private String id = "";

  /** Creates a new instance of DbDataSourceIndex */
  public DbDataSourceIndex() {
  }

  public <T> void setDataSource(T dataSource) throws SQLException {
    if (this.dataSource != null) {
      this.dataSource.removeListDataListener(listDataListener);
    }
    this.dataSource = (DbDataSource) dataSource;
    updateIndexFields();
    if (this.dataSource != null) {
      this.dataSource.addListDataListener(listDataListener);
    }
  }

  public <T> T getDataSource() {
    return (T) dataSource;
  }

  public void addKeys(List<String> columns) throws SQLException {
    keys.addAll(columns);
    updateIndexFields();
  }

  public void addKeys(String... columns) throws SQLException {
    for (String column : columns) {
      keys.add(column);
    }
    updateIndexFields();
  }

  public void setKeys(List<String> columns) throws SQLException {
    keys.clear();
    addKeys(columns);
  }

  public void setKeys(String... columns) throws SQLException {
    keys.clear();
    addKeys(columns);
  }

  private void updateIndexFields() throws SQLException {
    for (DbFieldObserver fo : fields) {
      fo.removeActiveRowChangeListener(fieldListener);
    }
    fields.clear();
    StringBuffer key = new StringBuffer(54);
    if (dataSource != null) {
      for (String column : keys) {
        DbFieldObserver fo = new DbFieldObserver();
        fo.setColumnName(column);
        fo.setDataSource(dataSource);
        fo.addActiveRowChangeListener(fieldListener);
        fields.add(fo);
        key.append(key.length() > 0 ? ";" : "").append(column);
      }
    }
    id = key.toString();
    if ((fields.size() > 0)&&(dataSource.isDataLoaded())) {
      reindex();
    }
  }

  private void reindex() throws SQLException {
    reindex(-1, -1);
  }

  private void reindex(int min, int max) throws SQLException {
    if (fields.size() > 0) {
      min = Math.min(min, max);
      max = Math.max(min, max);

      if (min == -1 && max == -1) {
        index.clear();
        r_index.clear();
      }

      min = min == -1 ? 1 : min;
      max = max == -1 ? dataSource.getRowCount() : max;

      if (r_index.size() > 0) {
        for (int row = min; row <= max; row++) {
          Integer k_row = new Integer(row);
          if (r_index.containsKey(k_row)) {
            DbIndexKey key = r_index.remove(k_row);
            SortedSet rowSet = index.get(key);
            rowSet.remove(k_row);
            if (rowSet.size() == 0) {
              index.remove(key);
            } else {
              index.put(key, rowSet);
            }
          }
        }
      }

      Set<DbIndexKey> changes = new HashSet<DbIndexKey>();
      dataSource.lock();
      try {
        for (int row = min; row <= max; row++) {
          DbIndexKey key = new DbIndexKey(dataSource, keys, row);

          if (key.isValid()) {
            SortedSet rowSet;
            if (index.containsKey(key)) {
              rowSet = index.get(key);
            } else {
              rowSet = new TreeSet<Integer>();
            }

            rowSet.add(key.getRow());
            index.put(key, rowSet);
            r_index.put(key.getRow(), key);

            changes.add(key);
          }
        }
      } finally {
        dataSource.unlock();
      }

      if (changes.size() == 1) {
        for (DbIndexKey key : changes) {
          fireContentsChanged(new ListDataEvent(key, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        }
      } else {
        fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      }
    }
  }

  private void remove(int min, int max) throws SQLException {
    min = Math.min(min, max);
    max = Math.max(min, max);

    if (min == -1 && max == -1) {
      index.clear();
      r_index.clear();
    }

    int count = dataSource.getRowCount();
    min = min == -1 ? 1 : min;
    max = max == -1 ? count : max;

    Set<DbIndexKey> changes = new HashSet<DbIndexKey>();

    for (int row = min; row <= max; row++) {
      Integer k_row = new Integer(row);
      if (r_index.containsKey(k_row)) {
        DbIndexKey key = r_index.remove(k_row);
        SortedSet rowSet = index.get(key);
        rowSet.remove(k_row);
        if (rowSet.size() == 0) {
          index.remove(key);
        } else {
          index.put(key, rowSet);
        }

        changes.add(key);
      }
    }

    if (max < count) {
      reindex(min + 1, count);
    } else if (changes.size() == 1) {
      for (DbIndexKey key : changes) {
        fireContentsChanged(new ListDataEvent(key, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      }
    } else {
      fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
    }
  }

  public String[] getKeys() {
    return keys.toArray(new String[keys.size()]);
  }

  public DbIndexKey getRowKey(Integer row) {
    return r_index.get(row);
  }

  public Set<DbIndexKey> getRowKeys(Set<Integer> rows) {
    Set<DbIndexKey> result = new LinkedHashSet<DbIndexKey>();

    if (rows != null) {
      for (Integer row : rows) {
        if (r_index.containsKey(row)) {
          result.add(r_index.get(row));
        }
      }
    }

    return result;
  }

  public Set<DbIndexKey> getUniqueKeys() {
    return index.keySet();
  }

  public Set<Integer> findRows(Object... values) {
    return findRows(new DbIndexKey(values));
  }

  public Set<Integer> findRows(DbIndexKey key) {
    if (index.containsKey(key)) {
      return index.get(key);
    } else {
      return null;
    }
  }

  public Integer findRow(Object... values) {
    return findRow(new DbIndexKey(values));
  }

  public Integer findRow(DbIndexKey key) {
    Set<Integer> rows = findRows(key);
    if (rows != null && !rows.isEmpty()) {
      return rows.iterator().next();
    } else {
      return null;
    }
  }

  public void intervalAdded(ListDataEvent e) {
    try {
      reindex(e.getIndex0(), e.getIndex1());
    } catch (SQLException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex);
    }
  }

  public void intervalRemoved(ListDataEvent e) {
    try {
      remove(e.getIndex0(), e.getIndex1());
    } catch (SQLException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex);
    }
  }

  public void contentsChanged(ListDataEvent e) {
    try {
      reindex();
    } catch (SQLException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex);
    }
  }

  public void activeRowChanged(ActiveRowChangeEvent event) {
  }

  public void fieldValueChanged(ActiveRowChangeEvent event) {
    if (dataSource.isDataLoaded()) {
      try {
        int row = dataSource.getRow();
        reindex(row, row);
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  public synchronized void removeListDataListener(ListDataListener l) {
    if (listDataListeners != null && listDataListeners.contains(l)) {
      listDataListeners.removeElement(l);
    }
  }

  public synchronized void addListDataListener(ListDataListener l) {
    WeakListenerList v = listDataListeners == null ? new WeakListenerList(2) : listDataListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      listDataListeners = v;
    }
  }

  protected void fireIntervalAdded(ListDataEvent e) {
    if (listDataListeners != null) {
      List listeners = listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).intervalAdded(e);//*/
      }
    }
  }

  protected void fireIntervalRemoved(ListDataEvent e) {
    if (listDataListeners != null) {
      List listeners = listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).intervalRemoved(e);//*/
      }
    }
  }

  protected void fireContentsChanged(ListDataEvent e) {
    if (listDataListeners != null) {
      List listeners = listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).contentsChanged(e);//*/
      }
    }
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof DbDataSourceIndex) {
      DbDataSourceIndex c = (DbDataSourceIndex) obj;

      return id.equals(c.id) && Equals.equals(this.dataSource, c.dataSource);
    } else {
      return false;
    }
  }

  //public int hashCode() {
  //  return id.hashCode();
  //}

  public static class DbIndexKey {

    private static final String NULL = "NULL";
    private boolean valid = false;
    private int row = -1;
    private List<String> keyValuesList = new ArrayList<String>();
    private String key = "";

    public DbIndexKey(Object... keyValues) {
      StringBuffer key = new StringBuffer(27);
      for (Object value : keyValues) {
        if (value == null) {
          this.keyValuesList.add(NULL);
          key.append(key.length() > 0 ? ";" : "").append(NULL);
        } else {
          this.keyValuesList.add(value.toString());
          key.append(key.length() > 0 ? ";" : "").append(value.toString());
        }
      }
      this.key = key.toString();
      valid = true;
    }

    public DbIndexKey(DbDataSource dataSource, List<String> keys, int row) throws SQLException {
      StringBuffer key = new StringBuffer(27);
      for (String column : keys) {
        Object value = dataSource.getValueAt(row, column, keys.toArray(new String[keys.size()]));
        if (value == null) {
          keyValuesList.add(NULL);
          key.append(key.length() > 0 ? ";" : "").append(NULL);
        } else {
          keyValuesList.add(value.toString());
          key.append(key.length() > 0 ? ";" : "").append(value.toString());
        }
      }
      this.key = key.toString();
      this.row = row;
      valid = true;
    }

    public Integer getRow() {
      return new Integer(this.row);
    }

    public boolean isValid() {
      return this.valid;
    }

    public int hashCode() {
      return key.hashCode();
    }

    public boolean equals(Object obj) {
      if ((obj == null) || !(obj instanceof DbIndexKey)) {
        return false;
      } else {
        return Equals.equals(key, ((DbIndexKey) obj).key);
      }
    }

    public String toString() {
      return key;
    }
  }
}
