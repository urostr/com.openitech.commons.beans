/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components.dogodki;

import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author domenbasic
 */
public class OpenWorkAreaTask extends JXTaskPane {

  private String title;
  private Integer workSpaceId;
  private Integer workAreaId;
  private Boolean openDataEntry;
  private final Boolean dontMerge;

  public OpenWorkAreaTask(String title, Integer workSpaceId, Integer workAreaId, Boolean openDataEntry, Boolean dontMerge) {
    this.title = title;
    this.workSpaceId = workSpaceId;
    this.workAreaId = workAreaId;
    this.openDataEntry = openDataEntry;
    this.dontMerge = dontMerge;
  }

  public String getTaskTitle() {
    return title;
  }

  public boolean getDontMerge() {
    return dontMerge == null ? false : dontMerge.booleanValue();
  }

  public Boolean getOpenDataEntry() {
    return openDataEntry;
  }

  public Integer getWorkAreaId() {
    return workAreaId;
  }

  public Integer getWorkSpaceId() {
    return workSpaceId;
  }
}
