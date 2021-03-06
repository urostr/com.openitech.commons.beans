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
package com.openitech.importer;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.xml.config.ImportSelection;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter.Options;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter.Options.IdentityGroupBy;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.ActivityEvent;
import com.openitech.value.fields.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class JImportEventsModel implements JImporter{

  private final String title;
  private DbDataSource dataSource;
  private Integer idSifranta;
  private String idSifre;
  private Integer activityId;
  Set<Field> defultFields;
  private Boolean hideUI;
  private EventImporter eventImporter;

  /**
   *
   * @param eventImporter
   * @param dataSource
   * @param eventColumnsList
   * @deprecated Use JEventsImporter class instead
   */
  @Deprecated
  public JImportEventsModel(EventImporter eventImporter, DbDataSource dataSource, Set<Field> eventColumnsList) {
    this.eventImporter = eventImporter;
    this.title = eventImporter.getTitle();
    this.dataSource = dataSource;
    this.idSifranta = eventImporter.getIdSifranta();
    this.idSifre = eventImporter.getIdSifre();
    this.activityId = eventImporter.getActivityId();
    this.defultFields = eventColumnsList;
    this.hideUI = eventImporter.isHideUI();



    if (activityId != null) {
      if (idSifranta == null || idSifre == null) {
        try {
          ActivityEvent activityEvent = SqlUtilities.getInstance().getActivityEvent(activityId);
          this.idSifranta = activityEvent.getIdSifranta();
          this.idSifre = activityEvent.getIdSifre();
        } catch (SQLException ex) {
          Logger.getLogger(JImportEventsModel.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  @Override
  public String getTitle() {
    return title;
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  public Integer getActivityId() {
    return activityId;
  }

  public Set<Field> getDefultFields() {
    return defultFields;
  }

  public int getIdSifranta() {
    return idSifranta;
  }

  public String getIdSifre() {
    return idSifre;
  }

  public boolean isHideUI() {
    return hideUI == null ? false : hideUI;
  }

  public List<IdentityGroupBy> getIdentityGroupBys() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.getIdentityGroupBy();
    }
    return null;
  }

  public List<Options.ReloadWorkSpace> getReloadWorkSpaces() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.getReloadWorkSpace();
    }
    return null;
  }

  public boolean isReloadAllWorkSpaces(){
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.isReloadAllWorkSpaces() == null ? false : options.isReloadAllWorkSpaces();
    }
    return false;
  }

  public boolean isAutoPreview(){
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.isAutoPreview() == null ? false : options.isAutoPreview();
    }
    return false;
  }

  public ImportSelection getDefaultSelection(){
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.getDefaultSelection();

    }
    return null;
  }
}
