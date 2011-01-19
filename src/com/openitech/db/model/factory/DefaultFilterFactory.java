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

  @Override
  public void configure() {
    Map<String, DataSourceFilters> filters = new HashMap<String, DataSourceFilters>();

    configure(filters);
    for (Map.Entry<String, DataSourceFilters> entry : filters.entrySet()) {
      parameters.add(entry.getValue());
    }
    
    configure(viewMenuItems);

    filterPanel = new DefaultFilterPanel(config.getDataModel().getDocuments(), filtersMap);
    config.getDataModel().getDocuments().putAll(filterPanel.getJPDbDataSourceFilter().getNamedDocuments());
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
