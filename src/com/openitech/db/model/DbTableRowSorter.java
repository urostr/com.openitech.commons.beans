/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

/**
 *
 * @author uros
 */
public class DbTableRowSorter extends RowSorter<DbTableModel> {

  private final DbTableModel model;

  public DbTableRowSorter(DbTableModel model) {
    this.model = model;
  }

  private DbSortable getSortable() {
    return model.getDataSource().getSortable();
  }

  @Override
  public DbTableModel getModel() {
    return this.model;
  }

  @Override
  public void toggleSortOrder(int column) {
    List<String> dataSourceColumns = model.getDataSourceColumns(column);
    final DbSortable sortable = getSortable();
    for (int pos = dataSourceColumns.size()-1; pos>=0; pos--) {
      sortable.toggleSortOrder(column, dataSourceColumns.get(pos));
    }
  }

  @Override
  public int convertRowIndexToModel(int index) {
    return index;
  }

  @Override
  public int convertRowIndexToView(int index) {
    return index;
  }

  @Override
  public void setSortKeys(List<? extends RowSorter.SortKey> keys) {
    getSortable().setSortKeys(keys);
  }

  @Override
  public List<? extends RowSorter.SortKey> getSortKeys() {
    return getSortable().getSortKeys();
  }

  @Override
  public int getViewRowCount() {
    return getModelRowCount();
  }

  @Override
  public int getModelRowCount() {
    return model.getRowCount();
  }

  @Override
  public void modelStructureChanged() {
  }

  @Override
  public void allRowsChanged() {
  }

  @Override
  public void rowsInserted(int firstRow, int endRow) {
  }

  @Override
  public void rowsDeleted(int firstRow, int endRow) {
  }

  @Override
  public void rowsUpdated(int firstRow, int endRow) {
  }

  @Override
  public void rowsUpdated(int firstRow, int endRow, int column) {
  }

  /**
   * SortKey describes the sort order for a particular column.  The
   * column index is in terms of the underlying model, which may differ
   * from that of the view.
   *
   * @since 1.6
   */
  public static class SortKey extends RowSorter.SortKey {

    private String columnName;

    /**
     * Creates a <code>SortKey</code> for the specified column with
     * the specified sort order.
     *
     * @param column index of the column, in terms of the model
     * @param sortOrder the sorter order
     * @throws IllegalArgumentException if <code>sortOrder</code> is
     *         <code>null</code>
     */
    public SortKey(int columnIndex, String columnName, SortOrder sortOrder) {
      super(columnIndex, sortOrder);
      this.columnName = columnName;
    }

    /**
     * Returns the index of the column.
     *
     * @return index of column
     */
    public final String getColumnName() {
      return columnName;
    }

    /**
     * Returns the hash code for this <code>SortKey</code>.
     *
     * @return hash code
     */
    public int hashCode() {
      int result = 17;
      result = 37 * result + getColumn();
      result = 37 * result + columnName.toUpperCase().hashCode();
      result = 37 * result + getSortOrder().hashCode();
      return result;
    }

    /**
     * Returns true if this object equals the specified object.
     * If the specified object is a <code>SortKey</code> and
     * references the same column and sort order, the two objects
     * are equal.
     *
     * @param o the object to compare to
     * @return true if <code>o</code> is equal to this <code>SortKey</code>
     */
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof SortKey) {
        return (((SortKey) o).getColumn() == getColumn() &&
                ((SortKey) o).columnName.equalsIgnoreCase(columnName)
                && ((SortKey) o).getSortOrder() == getSortOrder());
      }
      return false;
    }
  }
}
