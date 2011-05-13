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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  private transient Map<Integer, DbIndexKey> r_index = new LinkedHashMap<Integer, DbIndexKey>();
  private transient List<Integer> r_rows = new ArrayList<Integer>();
  private transient List<DbFieldObserver> fields = new ArrayList<DbFieldObserver>();
  private Map<String, Collection<Object>> keyFilters = new LinkedHashMap<String, Collection<Object>>();
  private String id = "";

  /** Creates a new instance of DbDataSourceIndex */
  public DbDataSourceIndex() {
  }

  @Override
  public void setDataSource(DbDataSource dataSource) throws SQLException {
    if (this.dataSource != null) {
      this.dataSource.removeListDataListener(listDataListener);
    }
    this.dataSource = dataSource;
    updateIndexFields();
    if (this.dataSource != null) {
      this.dataSource.addListDataListener(listDataListener);
    }
  }

  @Override
  public DbDataSource getDataSource() {
    return dataSource;
  }

  public void addKeys(List<String> columns) throws SQLException {
    keys.addAll(columns);
    updateIndexFields();
  }

  public void addKeys(String... columns) throws SQLException {
    keys.addAll(Arrays.asList(columns));
    updateIndexFields();
  }

  public void setKeys(List<String> columns) throws SQLException {
    keys.clear();
    addKeys(columns);
  }

  @Override
  public void setKeys(String... columns) throws SQLException {
    keys.clear();
    addKeys(columns);
  }

  public void addKeysFilter(Map<String, Collection<Object>> keyFilters) throws SQLException {
    List<String> columns = new ArrayList<String>(keyFilters.size());
    for (String column : keyFilters.keySet()) {
      columns.add(column);
    }
    this.keyFilters.putAll(keyFilters);
    addKeys(columns);
  }

  public void setKeysFilter(Map<String, Collection<Object>> keyFilters) throws SQLException {
    List<String> columns = new ArrayList<String>(keyFilters.size());
    for (String column : keyFilters.keySet()) {
      columns.add(column);
    }
    this.keyFilters.clear();
    this.keyFilters.putAll(keyFilters);
    setKeys(columns);
  }

  public int size() {
    return r_rows.size();
  }

  public int getRowAt(int index) {
    if (index > r_rows.size()) {
      return -1;
    } else {
      return r_rows.get(index - 1);
    }
  }

  private void updateIndexFields() throws SQLException {
    for (DbFieldObserver fo : fields) {
      fo.removeActiveRowChangeListener(fieldListener);
    }
    fields.clear();
    StringBuilder key = new StringBuilder(54);
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
    if (fields.size() > 0) {
      reindex();
    }
  }

  private void reindex() throws SQLException {
    Logger.getAnonymousLogger().log(Level.INFO, "Reindexing: " + dataSource.toString());
    reindex(-1, -1);
  }

  private void reindex(int min, int max) throws SQLException {
    if ((fields.size() > 0) && dataSource.isDataLoaded()) {
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
          DbIndexKey key;

          if (keyFilters.size() == keys.size()) {
            key = new DbIndexKey(dataSource, keyFilters, row);
          } else {
            key = new DbIndexKey(dataSource, keys, row);
          }

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

      r_rows.clear();
      for (Integer row : r_index.keySet()) {
        r_rows.add(row);
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

  @Override
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

  @Override
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

  @Override
  public void intervalAdded(ListDataEvent e) {
    try {
      reindex(e.getIndex0() + 1, e.getIndex1() + 1);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void intervalRemoved(ListDataEvent e) {
    try {
      remove(e.getIndex0() + 1, e.getIndex1() + 1);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    try {
      reindex();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void activeRowChanged(ActiveRowChangeEvent event) {
  }

  @Override
  public void fieldValueChanged(ActiveRowChangeEvent event) {
    if (dataSource.isDataLoaded()) {
      int row;
      try {
        row = dataSource.getRow();
        reindex(row, row);
      } catch (SQLException ex) {
        Logger.getLogger(DbDataSourceIndex.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  public synchronized void removeListDataListener(ListDataListener l) {
    if (listDataListeners != null && listDataListeners.contains(l)) {
      listDataListeners.removeElement(l);
    }
  }

  @Override
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

  @Override
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

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public static class DbIndexKey {

    private static final String NULL = "NULL";
    private boolean valid = false;
    private int row = -1;
    private List<String> keyValuesList = new ArrayList<String>();
    private String key = "";

    public DbIndexKey(Object... keyValues) {
      StringBuilder sbKey = new StringBuilder(27);
      for (Object value : keyValues) {
        if (value == null) {
          this.keyValuesList.add(NULL);
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(NULL);
        } else if (value instanceof java.util.Date) {
          keyValuesList.add(Long.toString(((java.util.Date) value).getTime()));
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(Long.toString(((java.util.Date) value).getTime()));
        } else {
          this.keyValuesList.add(value.toString());
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(value.toString());
        }
      }
      this.key = sbKey.toString();
      valid = true;
    }

    public DbIndexKey(DbDataSource dataSource, List<String> keys, int row) throws SQLException {
      StringBuilder sbKey = new StringBuilder(27);
      for (String column : keys) {
        Object value = row > dataSource.getRowCount() ? null : dataSource.getValueAt(row, column, keys.toArray(new String[keys.size()]));
        if (value == null) {
          keyValuesList.add(NULL);
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(NULL);
        } else if (value instanceof java.util.Date) {
          keyValuesList.add(Long.toString(((java.util.Date) value).getTime()));
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(Long.toString(((java.util.Date) value).getTime()));
        } else {
          keyValuesList.add(value.toString());
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(value.toString());
        }
      }
      this.key = sbKey.toString();
      this.row = row;
      valid = true;
    }

    public DbIndexKey(DbDataSource dataSource, Map<String, Collection<Object>> keyFilters, int row) throws SQLException {
      List<String> keys = new ArrayList<String>(keyFilters.size());
      for (String column : keyFilters.keySet()) {
        keys.add(column);
      }
      StringBuilder sbKey = new StringBuilder(27);
      valid = true;
      for (String column : keys) {
        Object value = null;
        try {
          value = row > dataSource.getRowCount() ? null : dataSource.getValueAt(row, column, keys.toArray(new String[keyFilters.size()]));
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
        }
        for (Object allowedValue : keyFilters.get(column)) {
          valid = valid && Equals.equals(value, allowedValue);
        }

        if (value == null) {
          keyValuesList.add(NULL);
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(NULL);
        } else if (value instanceof java.util.Date) {
          keyValuesList.add(Long.toString(((java.util.Date) value).getTime()));
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(Long.toString(((java.util.Date) value).getTime()));
        } else {
          keyValuesList.add(value.toString());
          sbKey.append(sbKey.length() > 0 ? ";" : "").append(value.toString());
        }
      }
      this.key = sbKey.toString();
      this.row = row;
    }

    public Integer getRow() {
      return new Integer(this.row);
    }

    public boolean isValid() {
      return this.valid;
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if ((obj == null) || !(obj instanceof DbIndexKey)) {
        return false;
      } else {
        return Equals.equals(key, ((DbIndexKey) obj).key);
      }
    }

    @Override
    public String toString() {
      return key;
    }
  }
}
