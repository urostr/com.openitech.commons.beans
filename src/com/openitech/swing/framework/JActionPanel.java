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
 * JActionPanel.java
 *
 * Created on Sreda, 30 januar 2008, 13:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.swing.framework;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXTaskPane;

import com.openitech.swing.framework.context.AssociatedFilter;
import com.openitech.swing.framework.context.AssociatedSuspends;
import com.openitech.swing.framework.context.AssociatedTasks;
import com.openitech.ref.WeakMethodReference;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class JActionPanel extends JTabbedPane implements AssociatedTasks, AssociatedFilter, AssociatedSuspends {

  private ActionPanelModelModelListener changeListener;

  /** Creates a new instance of JActionPanel */
  public JActionPanel() {
    super(BOTTOM);
  }

  /**
   * Subclasses that want to handle <code>ChangeEvents</code> differently
   * can override this to return a subclass of <code>ModelListener</code> or
   * another <code>ChangeListener</code> implementation.
   * 
   * 
   * @see #fireStateChanged
   */
  @Override
  protected ChangeListener createChangeListener() {
    if (changeListener == null) {
      changeListener = new ActionPanelModelModelListener(this);
    }
    return changeListener;
  }

  protected class ActionPanelModelModelListener extends WeakMethodReference<Object> implements ChangeListener {

    protected ActionPanelModelModelListener(JActionPanel owner) {
      super(owner);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      Object referent = this.get();
      if (referent != null && isEnabled()) {
        try {

          ((JActionPanel) referent).updateRefreshSuspends();
          ((JActionPanel) referent).updatePanels();
          ((JActionPanel) referent).fireStateChanged();

        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
          Logger.getAnonymousLogger().warning(ex.getMessage());
        }
      }
    }
  }

  private boolean isSelected() {
    boolean result = true;
    if (getParent() instanceof com.openitech.swing.framework.JActionPanel) {
      result = ((com.openitech.swing.framework.JActionPanel) getParent()).isSelected(this);
    }

    return result;
  }

  private boolean isSelected(com.openitech.swing.framework.JActionPanel component) {
    boolean result = false;
    if (getParent() instanceof com.openitech.swing.framework.JActionPanel) {
      result = ((com.openitech.swing.framework.JActionPanel) getParent()).isSelected();
      if (component != null) {
        result = result && component.equals(((com.openitech.swing.framework.JActionPanel) getParent()).getSelectedComponent());
      }
    } else {
      result = true;
    }

    return result;
  }

  @Override
  public void updateRefreshSuspends(boolean isParentSelected) {
    if (getTabCount() > 0) {
      Component selected = getSelectedComponent();

      for (Component c : getComponents()) {
        if (c instanceof com.openitech.swing.framework.context.AssociatedSuspends) {
          ((com.openitech.swing.framework.context.AssociatedSuspends) c).updateRefreshSuspends(isParentSelected && c.equals(selected));
        }
      }
    }
  }

  protected void updateRefreshSuspends() {
    updateRefreshSuspends(isSelected(this));
  }

  protected void updatePanels() {
    updatePanels(getFilterPanelContainer(), getTaskPaneContainer(), getInformationPaneContainer());
  }

  protected void updatePanels(java.awt.Container filterPanelContainer, java.awt.Container taskPaneContainer, java.awt.Container informationPaneContainer) {
    if (getTabCount() > 0) {
//      if (isChildUpdatesPanels() && (getSelectedComponent() instanceof com.openitech.framework.JActionPanel)) {
//        ((com.openitech.framework.JActionPanel) getSelectedComponent()).updatePanels(filterPanelContainer, taskPaneContainer);
//      } else {
      Component selected = getSelectedComponent();

      if (filterPanelContainer != null && getFilterPanelContainer() == null) {
        setFilterPanelContainer(filterPanelContainer);
      }

      filterPanelContainer = filterPanelContainer == null ? getFilterPanelContainer() : filterPanelContainer;


      if (filterPanelContainer != null) {
        java.awt.Component filterPanel = (selected instanceof com.openitech.swing.framework.context.AssociatedFilter) ? ((com.openitech.swing.framework.context.AssociatedFilter) selected).getFilterPanel() : null;
        filterPanelContainer.removeAll();
        if (filterPanel != null) {
          if (filterPanelContainer.getLayout() instanceof java.awt.BorderLayout) {
            filterPanelContainer.add(filterPanel, java.awt.BorderLayout.CENTER);
          } else {
            filterPanelContainer.add(filterPanel);
          }
        }
        filterPanelContainer.invalidate();
        filterPanelContainer.repaint(500);
      }

      if (informationPaneContainer != null && getInformationPaneContainer() == null) {
        setInformationPaneContainer(informationPaneContainer);
      }

      if (informationPaneContainer != null) {
        if (selected instanceof com.openitech.swing.framework.context.AssociatedInformationPanel) {
          List<JPanel> informationPanels = ((com.openitech.swing.framework.context.AssociatedInformationPanel) selected).getInformationPanels();
          informationPaneContainer.removeAll();
          if (informationPanels != null) {
            for (JPanel infoPanel : informationPanels) {
              informationPaneContainer.add(infoPanel);
            }

          }
          informationPaneContainer.invalidate();
          informationPaneContainer.repaint(500);
        }
      }


      if (taskPaneContainer != null && getTaskPaneContainer() == null) {
        setTaskPaneContainer(taskPaneContainer);
      }

      taskPaneContainer = taskPaneContainer == null ? getTaskPaneContainer() : taskPaneContainer;

      if (taskPaneContainer != null) {
        taskPaneContainer.removeAll();
        java.util.List<org.jdesktop.swingx.JXTaskPane> taskPanes = (selected instanceof com.openitech.swing.framework.context.AssociatedTasks) ? ((com.openitech.swing.framework.context.AssociatedTasks) selected).getTaskPanes() : null;
        if (taskPanes != null) {
          for (org.jdesktop.swingx.JXTaskPane c : taskPanes) {
            taskPaneContainer.add(c);
          }
        }
        taskPaneContainer.invalidate();
        taskPaneContainer.repaint(500);
//        }
      }
    }
  }
  /**
   * Holds value of property taskPaneContainer.
   */
  private java.awt.Container taskPaneContainer;

  /**
   * Getter for property taskPaneContainer.
   * @return Value of property taskPaneContainer.
   */
  public java.awt.Container getTaskPaneContainer() {
    if ((this.taskPaneContainer == null) && (getParent() instanceof JActionPanel)) {
      return ((JActionPanel) getParent()).getTaskPaneContainer();
    } else {
      return this.taskPaneContainer;
    }
  }

  /**
   * Setter for property taskPaneContainer.
   * @param taskPaneContainer New value of property taskPaneContainer.
   */
  public void setTaskPaneContainer(java.awt.Container taskPaneContainer) {
    this.taskPaneContainer = taskPaneContainer;
  }
  /**
   * Holds value of property filterPanelContainer.
   */
  private java.awt.Container filterPanelContainer;

  /**
   * Getter for property filterPanelContainer.
   * @return Value of property filterPanelContainer.
   */
  public java.awt.Container getFilterPanelContainer() {
    if ((this.filterPanelContainer == null) && (getParent() instanceof JActionPanel)) {
      return ((JActionPanel) getParent()).getFilterPanelContainer();
    } else {
      return this.filterPanelContainer;
    }
  }

  /**
   * Setter for property filterPanelContainer.
   * @param filterPanelContainer New value of property filterPanelContainer.
   */
  public void setFilterPanelContainer(java.awt.Container filterPanelContainer) {
    if (!com.openitech.util.Equals.equals(this.filterPanelContainer, filterPanelContainer)) {
      java.awt.Container oldValue = this.filterPanelContainer;
      this.filterPanelContainer = filterPanelContainer;
      updatePanels();
      firePropertyChange("filterPanelContainer", oldValue, this.filterPanelContainer);
    }
  }
  private java.awt.Container informationPaneContainer;

  /**
   * Getter for property taskPaneContainer.
   * @return Value of property taskPaneContainer.
   */
  public java.awt.Container getInformationPaneContainer() {
    if ((this.informationPaneContainer == null) && (getParent() instanceof JActionPanel)) {
      return ((JActionPanel) getParent()).getInformationPaneContainer();
    } else {
      return this.informationPaneContainer;
    }
  }

  /**
   * Setter for property taskPaneContainer.
   * @param taskPaneContainer New value of property taskPaneContainer.
   */
  public void setInformationPaneContainer(java.awt.Container informationPaneContainer) {
    this.informationPaneContainer = informationPaneContainer;
  }
  /**
   * Holds value of property taskPanes.
   */
  private java.util.List<org.jdesktop.swingx.JXTaskPane> taskPanes;

  /**
   * Getter for property taskPanes.
   * @return Value of property taskPanes.
   */
  @Override
  public java.util.List<org.jdesktop.swingx.JXTaskPane> getTaskPanes() {
    if (isChildUpdatesPanels() || this.taskPanes == null) {
      Component selected = getSelectedComponent();

      return (selected instanceof com.openitech.swing.framework.context.AssociatedTasks) ? ((com.openitech.swing.framework.context.AssociatedTasks) selected).getTaskPanes() : null;
    } else {
      return this.taskPanes;
    }
  }

  public void setTaskPane(JXTaskPane taskPane) {
    if (this.taskPanes == null) {
      this.taskPanes = new java.util.LinkedList<org.jdesktop.swingx.JXTaskPane>();
    } else {
      this.taskPanes.clear();
    }
    this.taskPanes.add(taskPane);
  }

  public JXTaskPane getTaskPane() {
    java.util.List<org.jdesktop.swingx.JXTaskPane> taskPanes = getTaskPanes();

    if ((taskPanes == null) || (taskPanes.isEmpty())) {
      return null;
    } else {
      return taskPanes.get(0);
    }
  }

  @Override
  public boolean add(JXTaskPane taskPane) {
    if (this.taskPanes == null) {
      this.taskPanes = new java.util.LinkedList<org.jdesktop.swingx.JXTaskPane>();
    }
    return this.taskPanes.add(taskPane);
  }

  @Override
  public boolean remove(JXTaskPane taskPane) {
    if (this.taskPanes != null) {
      return this.taskPanes.remove(taskPane);
    } else {
      return false;
    }
  }
  /**
   * Holds value of property filterPanel.
   */
  private java.awt.Component filterPanel;

  /**
   * Getter for property filterPanel.
   * @return Value of property filterPanel.
   */
  @Override
  public java.awt.Component getFilterPanel() {
    if (isChildUpdatesPanels() || this.filterPanel == null) {
      Component selected = getSelectedComponent();

      return (selected instanceof com.openitech.swing.framework.context.AssociatedFilter) ? ((com.openitech.swing.framework.context.AssociatedFilter) selected).getFilterPanel() : null;
    } else {
      return this.filterPanel;
    }
  }

  /**
   * Setter for property filterPanel.
   * @param filterPanel New value of property filterPanel.
   */
  @Override
  public void setFilterPanel(java.awt.Component filterPanel) {
    if (!com.openitech.util.Equals.equals(this.filterPanel, filterPanel)) {
      java.awt.Component oldValue = this.filterPanel;
      this.filterPanel = filterPanel;
      updatePanels();
      firePropertyChange("filterPanel", oldValue, this.filterPanel);
    }
  }
  /**
   * Holds value of property childUpdatesPanels.
   */
  private boolean childUpdatesPanels;

  /**
   * Getter for property childUpdatesPanels.
   * @return Value of property childUpdatesPanels.
   */
  public boolean isChildUpdatesPanels() {
    return this.childUpdatesPanels;
  }

  /**
   * Setter for property childUpdatesPanels.
   * @param childUpdatesPanels New value of property childUpdatesPanels.
   */
  public void setChildUpdatesPanels(boolean childUpdatesPanels) {
    if (this.childUpdatesPanels != childUpdatesPanels) {
      boolean oldValue = this.childUpdatesPanels;
      this.childUpdatesPanels = childUpdatesPanels;

      firePropertyChange("childUpdatesPanels", oldValue, childUpdatesPanels);
    }
  }

  /**
   * Inserts a <code>component</code>, at <code>index</code>,
   * represented by a <code>title</code> and/or <code>icon</code>,
   * either of which may be <code>null</code>.
   * Uses java.util.Vector internally, see <code>insertElementAt</code>
   * for details of insertion conventions. 
   * 
   * 
   * @param title the title to be displayed in this tab
   * @param icon the icon to be displayed in this tab
   * @param component The component to be displayed when this tab is clicked.
   * @param tip the tooltip to be displayed for this tab
   * @param index the position to insert this new tab
   * @see #addTab
   * @see #removeTabAt
   */
  @Override
  public void insertTab(String title, Icon icon, Component component, String tip, int index) {
//    changeListener.setEnabled(isChildUpdatesPanels());
//    try {
//      super.insertTab(title, icon, component, tip, index);
//    } finally {
//      changeListener.setEnabled(true);
//    }
    super.insertTab(title, icon, component, tip, index);
    updatePanels();
    updateRefreshSuspends();
  }

  /**
   * Removes the tab at <code>index</code>.
   * After the component associated with <code>index</code> is removed,
   * its visibility is reset to true to ensure it will be visible
   * if added to other containers.
   * 
   * @param index the index of the tab to be removed
   * @exception IndexOutOfBoundsException if index is out of range 
   *            (index < 0 || index >= tab count)
   * @see #addTab
   * @see #insertTab
   */
  @Override
  public void removeTabAt(int index) {
    super.removeTabAt(index);
    updatePanels();
    updateRefreshSuspends();
  }

  /**
   * Sets the model to be used with this tabbedpane.
   * 
   * 
   * @param model the model to be used
   * @see #getModel
   * @beaninfo bound: true
   * description: The tabbedpane's SingleSelectionModel.
   */
  @Override
  public void setModel(SingleSelectionModel model) {
    super.setModel(model);
    updatePanels();
    updateRefreshSuspends();
  }
}
