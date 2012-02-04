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
package com.openitech.db.filters;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class JWorkAreaFilter implements ActiveRowChangeListener {

  private Integer workAreaId;
  private DbDataSource dsWorkArea;
  private final Integer[] workSpaceId;
  private DataSourceFilters dataSourceFilters;
  private DataSourceFilters.AbstractSeekType seekType;
  private final String otherColumName;

  public JWorkAreaFilter(Integer workAreaId, Integer[] workSpaceId, DataSourceFilters dataSourceFilters, AbstractSeekType seekType, String otherColumName) {
    this.workAreaId = workAreaId;
    this.workSpaceId = workSpaceId;
    this.dataSourceFilters = dataSourceFilters;
    this.seekType = seekType;
    this.otherColumName = otherColumName;
  }

  public Integer getWorkAreaId() {
    return workAreaId;
  }

  public void setWorkAreaDataSource(DbDataSource dataSource) {
    this.dsWorkArea = dataSource;

    this.dsWorkArea.addActiveRowChangeListener(this);
  }

  @Override
  public void activeRowChanged(ActiveRowChangeEvent event) {
    try {
      if (seekType instanceof DataSourceFilters.IntegerSeekType) {
        dataSourceFilters.setSeekValue(seekType, dsWorkArea.getInt(otherColumName));
      } else if (seekType instanceof DataSourceFilters.SeekType) {
        dataSourceFilters.setSeekValue(seekType, dsWorkArea.getString(otherColumName));
      } else {
        dataSourceFilters.setSeekValue(seekType, dsWorkArea.getString(otherColumName));
      }
    } catch (SQLException ex) {
      Logger.getLogger(JWorkAreaFilter.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void fieldValueChanged(ActiveRowChangeEvent event) {
  }

  public Integer[] getWorkSpaceId() {
    return workSpaceId;
  }
}
