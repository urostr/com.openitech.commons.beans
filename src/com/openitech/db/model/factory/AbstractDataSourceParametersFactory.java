/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.swing.framework.context.AssociatedFilter;
import com.openitech.swing.framework.context.AssociatedTasks;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;

/**
 *
 * @author uros
 */
public abstract class AbstractDataSourceParametersFactory implements DataSourceObserver, DataSourceFiltersMap.MapReader, AssociatedFilter, AssociatedTasks {

  public AbstractDataSourceParametersFactory(DbDataSource dataSource) {
    this.dataSource = dataSource;
  }
  protected DbDataSource dataSource;

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  @Override
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

  /**
   * Configures the data source parameters factory
   */
  public abstract void configure() throws SQLException;

  /**
   * Get the value of parameters
   *
   * @return the value of parameters
   */
  public abstract List<? extends Object> getParameters();

  
  protected List<JMenu> viewMenuItems = new ArrayList<JMenu>();

  /**
   * Get the value of viewMenuItems
   *
   * @return the value of viewMenuItems
   */
  public List<JMenu> getViewMenuItems() {
    return viewMenuItems;
  }

  protected DataSourceFiltersMap filtersMap = new DataSourceFiltersMap();

  /**
   * Get the value of filtersMap
   *
   * @return the value of filtersMap
   */
  @Override
  public DataSourceFiltersMap getFiltersMap() {
    return filtersMap;
  }

  protected DataSourceParametersFactory dataSourceParametersFactory;

  /**
   * Get the value of dataSourceParametersFactory
   *
   * @return the value of dataSourceParametersFactory
   */
  public DataSourceParametersFactory getDataSourceParametersFactory() {
    return dataSourceParametersFactory;
  }

  /**
   * Set the value of dataSourceParametersFactory
   *
   * @param dataSourceParametersFactory new value of dataSourceParametersFactory
   */
  public void setDataSourceParametersFactory(DataSourceParametersFactory dataSourceParametersFactory) {
    this.dataSourceParametersFactory = dataSourceParametersFactory;
  }
}
