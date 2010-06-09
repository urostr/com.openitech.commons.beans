/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbSortable;
import com.openitech.db.model.DbTableRowSorter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

/**
 *
 * @author uros
 */
public class SQLOrderByParameter extends DbDataSource.SubstSqlParameter implements DbSortable {

  private final List<RowSorter.SortKey> keys = new java.util.ArrayList<RowSorter.SortKey>();
  private final DbDataSource dataSource;
  /**
   * Maximum number of sort keys.
   */
  private int maxSortKeys = 9;

  public SQLOrderByParameter(String replace, DbDataSource dataSource) {
    super(replace);
    this.dataSource = dataSource;
    addDataSource(dataSource);
  }

  public SQLOrderByParameter(String replace, DbDataSource dataSource, List<? extends DbTableRowSorter.SortKey> keys) {
    this(replace, dataSource);
    setSortKeys(keys, false);
  }

  /**
   * Reverses the sort order from ascending to descending (or
   * descending to ascending) if the specified column is already the
   * primary sorted column; otherwise, makes the specified column
   * the primary sorted column, with an ascending sort order. 
   *
   * @param columnName index of the column to make the primary sorted column,
   *        in terms of the underlying model
   */
  @Override
  public void toggleSortOrder(int column, String columnName) {
    List<RowSorter.SortKey> keys = new ArrayList<RowSorter.SortKey>(getSortKeys());
    RowSorter.SortKey sortKey;
    int sortIndex;

    for (sortIndex = keys.size() - 1; sortIndex >= 0; sortIndex--) {
      final RowSorter.SortKey key = keys.get(sortIndex);
      if (key instanceof DbTableRowSorter.SortKey) {
        if (((DbTableRowSorter.SortKey) key).getColumnName().equalsIgnoreCase(columnName)) {
          break;
        }
      } else {
        int columnIndex;
        try {
          columnIndex = dataSource.getColumnIndex(columnName);
        } catch (SQLException ex) {
          Logger.getLogger(SQLOrderByParameter.class.getName()).log(Level.SEVERE, null, ex);
          columnIndex = Integer.MIN_VALUE;
        }
        if (key.getColumn() == columnIndex) {
          break;
        }
      }
    }
    if (sortIndex == -1) {
      // Key doesn't exist
      sortKey = new DbTableRowSorter.SortKey(column, columnName, SortOrder.ASCENDING);
      keys.add(0, sortKey);
    } else if (sortIndex == 0) {
      // It's the primary sorting key, toggle it
      keys.set(0, toggle(keys.get(0)));
    } else {
      // It's not the first, but was sorted on, remove old
      final RowSorter.SortKey key = toggle(keys.get(sortIndex));
      keys.remove(sortIndex);
      keys.add(0, key);
    }
    if (keys.size() > getMaxSortKeys()) {
      keys = keys.subList(0, getMaxSortKeys());
    }
    setSortKeys(keys);
  }

  @Override
  public String getValue() {
    StringBuilder result = new StringBuilder();

    try {
      for (RowSorter.SortKey key : keys) {
        if (!key.getSortOrder().equals(SortOrder.UNSORTED)) {
          result.append(result.length() > 0 ? ", " : "").append(dataSource.getDelimiterLeft());
          if (key instanceof DbTableRowSorter.SortKey) {
            result.append(((DbTableRowSorter.SortKey) key).getColumnName());
          } else {
            result.append(dataSource.getColumnName(key.getColumn()));
          }
          result.append(dataSource.getDelimiterRight()).append(" ").append(key.getSortOrder().equals(SortOrder.DESCENDING) ? " DESC" : "");
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(SQLOrderByParameter.class.getName()).log(Level.SEVERE, null, ex);
      result.setLength(0);
    }

    if (result.length() > 0) {
      return "ORDER BY " + result.toString();
    } else {
      return "";
    }
  }

  private RowSorter.SortKey toggle(RowSorter.SortKey key) {
    if (key instanceof DbTableRowSorter.SortKey) {
      if (key.getSortOrder() == SortOrder.ASCENDING) {
        return new DbTableRowSorter.SortKey(key.getColumn(), ((DbTableRowSorter.SortKey) key).getColumnName(), SortOrder.DESCENDING);
      }
      return new DbTableRowSorter.SortKey(key.getColumn(), ((DbTableRowSorter.SortKey) key).getColumnName(), SortOrder.ASCENDING);
    } else {
      if (key.getSortOrder() == SortOrder.ASCENDING) {
        return new RowSorter.SortKey(key.getColumn(), SortOrder.DESCENDING);
      }
      return new RowSorter.SortKey(key.getColumn(), SortOrder.ASCENDING);
    }
  }

  @Override
  public void setSortKeys(List<? extends RowSorter.SortKey> keys) {
    setSortKeys(keys, true);
  }

  public void setSortKeys(List<? extends RowSorter.SortKey> keys, boolean notify) {
    this.keys.clear();
    this.keys.addAll(keys);
    firePropertyChange("query", notify, false);
  }

  /**
   * Sets the maximum number of sort keys.  The number of sort keys
   * determines how equal values are resolved when sorting.  For
   * example, assume a table row sorter is created and
   * <code>setMaxSortKeys(2)</code> is invoked on it. The user
   * clicks the header for column 1, causing the table rows to be
   * sorted based on the items in column 1.  Next, the user clicks
   * the header for column 2, causing the table to be sorted based
   * on the items in column 2; if any items in column 2 are equal,
   * then those particular rows are ordered based on the items in
   * column 1. In this case, we say that the rows are primarily
   * sorted on column 2, and secondarily on column 1.  If the user
   * then clicks the header for column 3, then the items are
   * primarily sorted on column 3 and secondarily sorted on column
   * 2.  Because the maximum number of sort keys has been set to 2
   * with <code>setMaxSortKeys</code>, column 1 no longer has an
   * effect on the order.
   * <p>
   * The maximum number of sort keys is enforced by
   * <code>toggleSortOrder</code>.  You can specify more sort
   * keys by invoking <code>setSortKeys</code> directly and they will
   * all be honored.  However if <code>toggleSortOrder</code> is subsequently
   * invoked the maximum number of sort keys will be enforced.
   * The default value is 3.
   *
   * @param max the maximum number of sort keys
   * @throws IllegalArgumentException if <code>max</code> &lt; 1
   */
  public void setMaxSortKeys(int max) {
    if (max < 1) {
      throw new IllegalArgumentException("Invalid max");
    }
    maxSortKeys = max;
  }

  /**
   * Returns the maximum number of sort keys.
   *
   * @return the maximum number of sort keys
   */
  public int getMaxSortKeys() {
    return maxSortKeys;
  }

  @Override
  public List<? extends RowSorter.SortKey> getSortKeys() {
    return Collections.unmodifiableList(keys);
  }
}
