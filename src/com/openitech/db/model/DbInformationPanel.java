/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

/**
 *
 * @author domenbasic
 */
public interface DbInformationPanel {

  public void setCollapsed(boolean collapsed);

  public boolean isCollapsed();

  public void setAnimated(boolean collapsed);

  public boolean isAnimated();

  public void setIndex(int index);

  public int getIndex();

  public void setTitle(String title);

  public String getTitle();
}
