/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.DbDataSource;
import com.openitech.importer.DataColumn;
import java.sql.SQLException;

/**
 *
 * @author domenbasic
 */
public abstract class AbstractSourceColumnFactory implements SourceColumnFactory {

  protected DbDataSource dataSource;

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }

  /**
   * Set the value of dataSource
   *
   * @param dataSource new value of dataSource
   */
  @Override
  public void setDataSource(DbDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Object getColumnValue(SourceColumnFactoryParameter parameter) throws SQLException {
    return this.getColumnValue(parameter.getValue());
  }
}
