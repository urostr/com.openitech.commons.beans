/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.filters.DefaultFilterPanel;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author uros
 */
public class DefaultFilterFactory extends AbstractDataSourceParametersFactory implements ClassInstanceFactory.Custom {

  protected List<JXTaskPane> taskPanes = new ArrayList<JXTaskPane>();
  protected List<DbDataSource.SqlParameter> parameters = new ArrayList<DbDataSource.SqlParameter>();
  protected DefaultFilterPanel filterPanel;
  protected final DataSourceConfig<? extends DbDataModel> config;

  public DefaultFilterFactory(DbDataSource dataSource, DataSourceConfig<? extends DbDataModel> config) {
    super(dataSource);
    this.config = config;
  }
  protected Map<String, DataSourceFilters> filters;

  /**
   * Get the value of filters
   *
   * @return the value of filters
   */
  public Map<String, DataSourceFilters> getFilters() {
    return filters;
  }

  /**
   * Set the value of filters
   *
   * @param filters new value of filters
   */
  public void setFilters(Map<String, DataSourceFilters> filters) {
    this.filters = filters;
  }

  @Override
  public void configure() {
    Map<String, DataSourceFilters> cfilters = filters == null ? new HashMap<String, DataSourceFilters>() : filters;

    configure(cfilters);
    for (Map.Entry<String, DataSourceFilters> entry : cfilters.entrySet()) {
      if (!parameters.contains(entry.getValue())) {
        parameters.add(entry.getValue());
      }
    }

    configure(viewMenuItems);

    filterPanel = new DefaultFilterPanel(config.getDataModel().getDocuments(), filtersMap);
    config.getDataModel().getDocuments().putAll(filterPanel.getJPDbDataSourceFilter().getNamedDocuments());

    viewMenuItems.add(filterPanel.getJPDbDataSourceFilter().getFilterMenuItem());

  }

  @Override
  public List<? extends Object> getParameters() {
    return parameters;
  }

  @Override
  public Component getFilterPanel() {
    return filterPanel;
  }

  @Override
  public void setFilterPanel(Component filter) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<JXTaskPane> getTaskPanes() {
    return taskPanes;
  }

  @Override
  public boolean add(JXTaskPane taskPane) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean remove(JXTaskPane taskPane) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
