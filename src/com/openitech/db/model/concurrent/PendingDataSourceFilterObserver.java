/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model.concurrent;

import com.openitech.db.filters.DataSourceFilters;
import com.openitech.ref.events.PropertyChangeWeakListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author uros
 */
public class PendingDataSourceFilterObserver implements PropertyChangeListener {

  private DataSourceFilters dataSourceFilter;

  /**
   * Get the value of dataSourceFilter
   *
   * @return the value of dataSourceFilter
   */
  public DataSourceFilters getDataSourceFilter() {
    return dataSourceFilter;
  }

  private PendingSqlParameter pendingSqlParameter;

  /**
   * Get the value of pendingSqlParameter
   *
   * @return the value of pendingSqlParameter
   */
  public PendingSqlParameter getPendingSqlParameter() {
    return pendingSqlParameter;
  }

  public PendingDataSourceFilterObserver(DataSourceFilters dataSourceFilter, PendingSqlParameter pendingSqlParameter) {
    this.dataSourceFilter = dataSourceFilter;
    this.pendingSqlParameter = pendingSqlParameter;

    dataSourceFilter.addPropertyChangeListener("query", new PropertyChangeWeakListener(this));
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    pendingSqlParameter.setImmediate(dataSourceFilter.hasValue());
  }
}
