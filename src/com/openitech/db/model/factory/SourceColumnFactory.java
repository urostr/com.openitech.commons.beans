/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.importer.DataColumn;
import java.util.List;

/**
 *
 * @author uros
 */
public interface SourceColumnFactory {
  public DataColumn getColumnValue(DataColumn column, DataColumn... row);
  public DataColumn getColumnValue(SourceColumnFactoryParameter parameter);

  public static class SourceColumnFactoryParameter {

    private DataColumn column;

    public SourceColumnFactoryParameter(DataColumn column, List<DataColumn> row) {
      this.column = column;
      this.row = row;
    }

    /**
     * Get the value of column
     *
     * @return the value of column
     */
    public DataColumn getColumn() {
      return column;
    }
    protected List<DataColumn> row;

    /**
     * Get the value of row
     *
     * @return the value of row
     */
    public List<DataColumn> getRow() {
      return row;
    }

  }
}
