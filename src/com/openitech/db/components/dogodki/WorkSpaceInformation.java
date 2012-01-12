/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components.dogodki;

import com.openitech.db.model.xml.config.Workarea.WorkSpaceInformation.Panels;
import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author domenbasic
 */
public class WorkSpaceInformation extends JPanel{

 private final Panels panel;
 private final Component component;

  public WorkSpaceInformation(Panels panel, Component component) {
    this.panel = panel;
    this.component = component;
  }

 public WorkSpaceInformation(Component component) {
    this(null, component);
  }

  public WorkSpaceInformation(Panels panel) {
    this(panel, null);
  }

  public Component getComponent() {
    return component;
  }


  public Panels getPanel() {
    return panel;
  }


}
