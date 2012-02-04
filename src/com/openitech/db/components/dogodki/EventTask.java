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
package com.openitech.db.components.dogodki;

import java.util.List;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author domenbasic
 */
public class EventTask extends JXTaskPane {

  protected String title;
  protected String taskTitle;
  protected boolean dontMerge;
  protected boolean hide;
  protected Integer workSpaceId;
  protected Integer workAreaId;
  protected Boolean openDataEntry;
  protected TaskType type;
  protected List<String> reportName;

  public EventTask(String taskTitle, Boolean dontMerge) {
    this(null, taskTitle, dontMerge, false, null, null, null);
  }

  public EventTask(String title, String taskTitle, boolean hide) {
    this(title, taskTitle, null, hide, null, null, null);
  }

  public EventTask(String taskTitle, Integer workSpaceId, Integer workAreaId, Boolean openDataEntry, Boolean dontMerge) {
    this(null, taskTitle, dontMerge, false, workSpaceId, workAreaId, openDataEntry);
  }

  public EventTask(String title, String taskTitle, Boolean dontMerge, boolean hide, Integer workSpaceId, Integer workAreaId, Boolean openDataEntry) {
    this.title = title;
    this.taskTitle = taskTitle;
    this.dontMerge = dontMerge == null ? false : dontMerge.booleanValue();
    this.hide = hide;
    this.workSpaceId = workSpaceId;
    this.workAreaId = workAreaId;
    this.openDataEntry = openDataEntry;
  }

  public TaskType getType() {
    return type;
  }

  public void setType(TaskType type) {
    this.type = type;
  }

  public boolean getDontMerge() {
    return dontMerge;
  }

  public String getTaskPaneTitle() {
    return this.title;
  }

  public String getTaskTitle() {
    return taskTitle;
  }

  public boolean isHide() {
    return hide;
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

  public void setReportName(List<String> name) {
    this.reportName = name;
  }

  public List<String> getReportName() {
    return reportName;
  }

  

  public static enum TaskType {

    DEFAULT,
    BACK,
    INSERT,
    OPEN_WORK_AREA,
    REPORT;
  }
}
