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
public class DefaultTask extends JXTaskPane{

  private String title;
  private String taskTitle;
  private final boolean hide;

  public DefaultTask(String title, String taskTitle, boolean hide) {
    this.title = title;
    this.taskTitle = taskTitle;
    this.hide = hide;
  }

  public String getTaskPaneTitle() {
    return title;
  }
  public String getTaskTitle() {
    return taskTitle;
  }

  

  public boolean isHide() {
    return hide;
  }



  
}
