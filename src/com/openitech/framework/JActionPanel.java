/*
 * JActionPanel.java
 *
 * Created on Sreda, 30 januar 2008, 13:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.framework;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXTaskPane;

import com.openitech.framework.context.AssociatedFilter;
import com.openitech.framework.context.AssociatedSuspends;
import com.openitech.framework.context.AssociatedTasks;
import com.openitech.ref.WeakMethodReference;

/**
 *
 * @author uros
 */
public class JActionPanel extends JTabbedPane implements AssociatedTasks, AssociatedFilter, AssociatedSuspends {
  private ChangeListener changeListener;
  
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
  protected ChangeListener createChangeListener() {
    if (changeListener==null)
      changeListener = new ActionPanelModelModelListener(this);
    return changeListener;
  }
  
  protected class ActionPanelModelModelListener extends WeakMethodReference<Object> implements ChangeListener {
    protected ActionPanelModelModelListener(JActionPanel owner) {
        super(owner);
    }
    
    public void stateChanged(ChangeEvent e) {
      Object referent = this.get();
      if (referent!=null && isEnabled()) {
        ((JActionPanel) referent).updateRefreshSuspends();
        ((JActionPanel) referent).updatePanels();
        ((JActionPanel) referent).fireStateChanged();
      }
    }
  }
  

  public void updateRefreshSuspends(boolean selected) {
    if (isChildUpdatesPanels()) {
      if (getTabCount()>0) {
        if (getSelectedComponent() instanceof com.openitech.framework.context.AssociatedSuspends) {
          ((com.openitech.framework.JActionPanel) getSelectedComponent()).updateRefreshSuspends(selected);
        }
      }
    } else {
      for (Component c:getComponents()) {
        if (c instanceof com.openitech.framework.context.AssociatedSuspends) {
          ((        com.openitech.framework.context.AssociatedSuspends) c).updateRefreshSuspends(selected);
        }
      }
    }
  }
  
  
  protected void updateRefreshSuspends() {
    if (getTabCount()>0) {
      Component selected = getSelectedComponent();

      for (Component c:getComponents()) {
        if (c instanceof com.openitech.framework.context.AssociatedSuspends) {
          ((      com.openitech.framework.context.AssociatedSuspends) c).updateRefreshSuspends(c.equals(selected));
        }
      }
    }
  }

  
  protected void updatePanels() {
    if (getTabCount()>0) {
      if (isChildUpdatesPanels()) {
        if (getSelectedComponent() instanceof com.openitech.framework.JActionPanel) {
          ((com.openitech.framework.JActionPanel) getSelectedComponent()).updatePanels();
        }
      } else  {
        Component selected = getSelectedComponent();
        
        if (selected instanceof com.openitech.framework.context.AssociatedFilter) {
          java.awt.Container filterPanelContainer = getFilterPanelContainer();
          java.awt.Component filterPanel = ((com.openitech.framework.context.AssociatedFilter) getSelectedComponent()).getFilterPanel();
          
          if ((filterPanelContainer!=null)&&(filterPanel!=null)) {
            filterPanelContainer.removeAll();
            if (filterPanelContainer.getLayout() instanceof java.awt.BorderLayout)
              filterPanelContainer.add(filterPanel, java.awt.BorderLayout.CENTER);
            else
              filterPanelContainer.add(filterPanel);
            filterPanelContainer.invalidate();
            filterPanelContainer.repaint(500);
          }
        }
        
        if (selected instanceof com.openitech.framework.context.AssociatedTasks) {
          java.awt.Container taskPaneContainer = getTaskPaneContainer();
          java.util.List<org.jdesktop.swingx.JXTaskPane> taskPanes = ((com.openitech.framework.context.AssociatedTasks) getSelectedComponent()).getTaskPanes();
          
          if ((taskPaneContainer!=null)&&(taskPanes!=null)) {
            taskPaneContainer.removeAll();
            for (org.jdesktop.swingx.JXTaskPane c:taskPanes)
              taskPaneContainer.add(c);
            taskPaneContainer.invalidate();
            taskPaneContainer.repaint(500);
          }
        }
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
    return this.taskPaneContainer;
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
    return this.filterPanelContainer;
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

  /**
   * Holds value of property taskPanes.
   */
  private java.util.List<org.jdesktop.swingx.JXTaskPane> taskPanes;

  /**
   * Getter for property taskPanes.
   * @return Value of property taskPanes.
   */
  public java.util.List<org.jdesktop.swingx.JXTaskPane> getTaskPanes() {
    return this.taskPanes;
  }
  
  public void setTaskPane(JXTaskPane taskPane) {
    if (this.taskPanes==null)
      this.taskPanes = new java.util.LinkedList<org.jdesktop.swingx.JXTaskPane>();
    else
      this.taskPanes.clear();
    this.taskPanes.add(taskPane);
  }

  public JXTaskPane getTaskPane() {
    if ((this.taskPanes==null)||(this.taskPanes.isEmpty()))
      return null;
    else
      return this.taskPanes.get(0);
  }
  
  public boolean add(JXTaskPane taskPane) {
    if (this.taskPanes==null)
      this.taskPanes = new java.util.LinkedList<org.jdesktop.swingx.JXTaskPane>();
    return this.taskPanes.add(taskPane);
  }

  public boolean remove(JXTaskPane taskPane) {
    if (this.taskPanes!=null) {
      return this.taskPanes.remove(taskPane);
    } else
      return false;
  }

  /**
   * Holds value of property filterPanel.
   */
  private java.awt.Component filterPanel;

  /**
   * Getter for property filterPanel.
   * @return Value of property filterPanel.
   */
  public java.awt.Component getFilterPanel() {
    return this.filterPanel;
  }

  /**
   * Setter for property filterPanel.
   * @param filterPanel New value of property filterPanel.
   */
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
    this.childUpdatesPanels = childUpdatesPanels;
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
  public void insertTab(String title, Icon icon, Component component, String tip, int index) {
    super.insertTab(title, icon, component, tip, index);
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
  public void removeTabAt(int index) {
    super.removeTabAt(index);
    updateRefreshSuspends();
  }
}
