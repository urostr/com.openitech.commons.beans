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
package com.openitech.db.model.factory;

import com.openitech.db.filters.DefaultFilterPanel;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.DataSourceFilter.Layout;
import com.openitech.db.model.xml.config.DataSourceParametersFactory.Filters;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
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
    
    List<DataSourceFilters> cparameters = new ArrayList<DataSourceFilters>();
    
    for (Map.Entry<String, DataSourceFilters> entry : cfilters.entrySet()) {
      if (!cparameters.contains(entry.getValue())) {
        cparameters.add(entry.getValue());
      }
    }
    
    Collections.sort(cparameters);
    
    for (DataSourceFilters parameter : cparameters) {
      if (!parameters.contains(parameter)) {
        parameters.add(parameter);
      }  
    }

    configure(viewMenuItems);

    filterPanel = new DefaultFilterPanel(config.getDataModel().getDocuments(), filtersMap);
    if (config.getDataModel() != null) {
      if (config.getDataModel().getDocuments() != null) {
        config.getDataModel().getDocuments().putAll(filterPanel.getJPDbDataSourceFilter().getNamedDocuments());
      }
    }
    viewMenuItems.add(filterPanel.getJPDbDataSourceFilter().getFilterMenuItem());
    if(dataSourceParametersFactory != null){
    Filters filters1 = dataSourceParametersFactory.getFilters();
      List<DataSourceFilter> dataSourceFilters = filters1.getDataSourceFilter();
      if(dataSourceFilters != null){
        for (DataSourceFilter dataSourceFilter : dataSourceFilters) {
          Layout layout = dataSourceFilter.getLayout();
          if(layout != null){
            if(layout.isHideAutoSeek()){
              filterPanel.hideAutoSeekPanel();
            }
            if(layout.isHideClear()){
              filterPanel.hideClearButton();
            }
            if(layout.isHidePanel()){
              filterPanel.hidePanel();
            }
            if(layout.isHideReload()){
              filterPanel.hideReloadButton();
            }
            if(layout.isHideSeek()){
              filterPanel.hideSeekButton();
            }
            if(layout.isHideTop()){
              filterPanel.hideTopPanel();
            }
            break;
          }
        }
      }
    }
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
