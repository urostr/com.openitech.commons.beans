/*
 * JDbComboBox.java
 *
 * Created on April 5, 2006, 10:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;

/**
 *
 * @author tomaz
 */
public class JDbComboBox extends JComboBox {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  /** Creates a new instance of JDbComboBox */
  public JDbComboBox() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null);
      actionWeakListener = new ActionWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addActionListener(actionWeakListener);
  }
  
  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
  }
  
  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
  }
  
  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }
  
  public void setColumnName(String columnName) {
    dbFieldObserver.setColumnName(columnName);
  }
  
  public String getColumnName() {
    return dbFieldObserver.getColumnName();
  }
  
  public void setToolTipColumnName(String columnName) {
    dbFieldObserverToolTip.setColumnName(columnName);
  }
  
  public String getToolTipColumnName() {
    return dbFieldObserverToolTip.getColumnName();
  }
  
  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    actionWeakListener.setEnabled(false);
    try {
      if (getModel() instanceof DbComboBoxModel) {
        this.setSelectedItem(new DbComboBoxModel.DbComboBoxEntry<Object,Object>(dbFieldObserver.getValue(),null));
      } else
        this.setSelectedIndex(dbFieldObserver.getValueAsInt());
    } finally {
      actionWeakListener.setEnabled(true);
    }
    repaint(27);
  }
  
  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    if (this.dbFieldObserverToolTip.getColumnName()!=null) {
      int tip  = dbFieldObserverToolTip.getValueAsInt();
      if (!dbFieldObserverToolTip.wasNull() && tip<this.getModel().getSize()) {
        this.setToolTipText("Pomo\u010d : "+this.getModel().getElementAt(tip));
      } else
        this.setToolTipText(null);
    } else
      this.setToolTipText(null);
  }
  
  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      if (getModel() instanceof DbComboBoxModel) {
        if (this.getSelectedItem()!=null)
          dbFieldObserver.updateValue(((DbComboBoxModel.DbComboBoxEntry) this.getSelectedItem()).getKey());
      } else
        dbFieldObserver.updateValue(this.getSelectedIndex());
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
    } finally {
      activeRowChangeWeakListener.setEnabled(true);
    }
  }
  
  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e) {
    updateColumn();
  }
}