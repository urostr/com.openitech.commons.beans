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

package com.openitech.db.model.sql;

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
