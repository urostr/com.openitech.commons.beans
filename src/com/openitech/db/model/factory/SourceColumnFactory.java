/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface SourceColumnFactory {
  public void setDataSource(DbDataSource dataSource);
  public Object getColumnValue(Object value) throws SQLException;
  public Object getColumnValue(SourceColumnFactoryParameter parameter) throws SQLException;

  public static class SourceColumnFactoryParameter {


    public SourceColumnFactoryParameter(DbDataSource dataSource, String columnName, int row) {
      this.columnName = columnName;
      this.row = row;
    }
    
    private DbDataSource dataSource;

    /**
     * Get the value of dataSource
     *
     * @return the value of dataSource
     */
    public DbDataSource getDataSource() {
      return dataSource;
    }

    private String columnName;

    /**
     * Get the value of columnName
     *
     * @return the value of columnName
     */
    public String getColumnName() {
      return columnName;
    }

    private int row;

    /**
     * Get the value of row
     *
     * @return the value of row
     */
    public int getRow() {
      return row;
    }
    
    public Object getValue() throws SQLException {
      return dataSource.getValueAt(row, columnName);
    }

  }
}
