/*
 * AssociatedTasks.java
 *
 * Created on Sreda, 30 januar 2008, 13:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.framework.context;

/**
 *
 * @author uros
 */
public interface AssociatedTasks {
  public java.util.List<org.jdesktop.swingx.JXTaskPane> getTaskPanes();
  public boolean add(org.jdesktop.swingx.JXTaskPane taskPane);
  public boolean remove(org.jdesktop.swingx.JXTaskPane taskPane);
}
