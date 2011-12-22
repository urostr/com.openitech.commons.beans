/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.db.model.DbDataSource;
import com.openitech.value.fields.Field;
import java.util.Set;

/**
 *
 * @author domenbasic
 */
public class JImportEventsModel {

  private final String title;
  private DbDataSource dataSource;
  private int idSifranta;
  private String idSifre;
  private Integer activityId;
  Set<Field> defultFields;
  private Boolean hideUI;

  public JImportEventsModel(String title, DbDataSource dataSource, int idSifranta, String idSifre, Integer activityId, Set<Field> defultFields, Boolean hideUI) {
    this.title = title;
    this.dataSource = dataSource;
    this.idSifranta = idSifranta;
    this.idSifre = idSifre;
    this.activityId = activityId;
    this.defultFields = defultFields;
    this.hideUI = hideUI;
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
}
