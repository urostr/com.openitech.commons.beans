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
public class InsertTask extends JXTaskPane {

  private String title;
  private final Boolean dontMerge;

  public InsertTask(String title, Boolean dontMerge) {
    this.title = title;
    this.dontMerge = dontMerge;
  }

  public boolean getDontMerge() {
    return dontMerge == null ? false : dontMerge.booleanValue();
  }

  public String getTaskTitle() {
    return title;
  }
}
