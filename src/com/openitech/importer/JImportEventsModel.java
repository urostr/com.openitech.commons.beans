/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.db.model.DbDataSource;
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
public class JImportEventsModel {

  private final String title;
  private DbDataSource dataSource;
  private Integer idSifranta;
  private String idSifre;
  private Integer activityId;
  Set<Field> defultFields;
  private Boolean hideUI;
  private EventImporter eventImporter;

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
}
