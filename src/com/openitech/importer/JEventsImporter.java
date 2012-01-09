/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.xml.config.ImportSelection;
import com.openitech.db.model.xml.config.ImportSource;
import com.openitech.db.model.xml.config.Importer;
import com.openitech.db.model.xml.config.Importer.Destination;
import com.openitech.db.model.xml.config.Importer.Destination.Column;
import com.openitech.db.model.xml.config.Importer.Options;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.ActivityEvent;
import com.openitech.value.fields.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class JEventsImporter implements JImporter {

  private DbDataSource dataSource;
  private Integer idSifranta;
  private String idSifre;
  private Integer activityId;
  Set<Field> defultFields;
  private Importer eventImporter;
  private Map<Field, SourceColumn> sourceColumnsMap = new HashMap<Field, SourceColumn>();

  public JEventsImporter(Importer eventImporter, DbDataSource dataSource, Set<Field> eventColumnsList) {
    this.eventImporter = eventImporter;
    this.dataSource = dataSource;
    Destination destination = eventImporter.getDestination();
    if (destination != null) {
      this.idSifranta = eventImporter.getDestination().getIdSifranta();
      this.idSifre = destination.getIdSifre();
      this.activityId = destination.getActivityId();
    }
    this.defultFields = eventColumnsList;



    if (activityId != null) {
      if (idSifranta == null) {
        try {
          ActivityEvent activityEvent = SqlUtilities.getInstance().getActivityEvent(activityId);
          this.idSifranta = activityEvent.getIdSifranta();
          this.idSifre = activityEvent.getIdSifre();
        } catch (SQLException ex) {
          Logger.getLogger(JEventsImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    if (destination != null) {

      List<Column> eventColumns = destination.getColumn();

      for (Column column : eventColumns) {
        String imePolja = column.getColumnName();
       
        Field field = Field.getField(imePolja);
        Column.SourceColumn sourceColumn = column.getSourceColumn();
        if (sourceColumn != null) {

          sourceColumnsMap.put(field, new com.openitech.importer.SourceColumn(sourceColumn));
        }
      }
    }
  }

  @Override
  public String getTitle() {
    return eventImporter.getTitle();
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
    boolean result = false;
    Importer.Options options = eventImporter.getOptions();
    if (options != null) {
      Boolean hideUI = options.isHideUI();
      result = hideUI == null ? false : hideUI;
    }
    return result;
  }

  public List<IdentityGroupBy> getIdentityGroupBys() {
    List<IdentityGroupBy> result = new ArrayList<IdentityGroupBy>();
    final Destination destination = eventImporter.getDestination();
    if (destination != null) {
      for (Destination.Column column : destination.getColumn()) {
        String identityGroupBy = column.getIdentityGroupBy();
        if (identityGroupBy != null && !identityGroupBy.equals("")) {
          result.add(new IdentityGroupBy(column.getColumnName(), identityGroupBy));
        }
      }
    }

    return result;
  }

  public List<Options.ReloadWorkSpace> getReloadWorkSpaces() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.getReloadWorkSpace();
    }
    return null;
  }

  public boolean isReloadAllWorkSpaces() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.isReloadAllWorkSpaces() == null ? false : options.isReloadAllWorkSpaces();
    }
    return false;
  }

  public boolean isAutoPreview() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.isAutoPreview() == null ? false : options.isAutoPreview();
    }
    return false;
  }

  public ImportSelection getDefaultSelection() {
    Options options = eventImporter.getOptions();
    if (options != null) {
      return options.getDefaultSelection();

    }
    return null;
  }

  public boolean isCSV() {
    ImportSource source = eventImporter.getSource();
    if (source != null && source.equals(ImportSource.CSV)) {
      return true;
    }
    return false;
  }

  public Map<Field, SourceColumn> getSourceColumnsMap() {
    return sourceColumnsMap;
  }
}
