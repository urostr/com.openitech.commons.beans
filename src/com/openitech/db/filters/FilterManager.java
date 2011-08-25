/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class FilterManager {

  private static FilterManager instance;

  public static FilterManager getInstance() {
    if (instance == null) {
      instance = new FilterManager();
    }
    return instance;
  }

  private FilterManager() {
  }
  private List<DataSourceFilters> filters = new ArrayList<DataSourceFilters>();

  public void addFilter(DataSourceFilters dataSourceFilters) {
    if (!filters.contains(dataSourceFilters)) {
      filters.add(dataSourceFilters);
    }
  }

  public synchronized void doSeek() {
    for (DataSourceFilters dataSourceFilters : filters) {
      dataSourceFilters.setParameters(false);
    }

    for (DataSourceFilters dataSourceFilters : filters) {
      dataSourceFilters.reloadDataSources();
    }

    filters.clear();
  }
}
