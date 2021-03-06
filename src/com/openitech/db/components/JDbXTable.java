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
 * JDbTable.java
 *
 * Created on Ponedeljek, 3 april 2006, 19:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbNavigatorDataSource;
import com.openitech.db.model.DbTableModel;
import com.openitech.ref.WeakListenerList;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author uros
 */
public class JDbXTable extends JXTable implements ListSelectionListener, DbNavigatorDataSource {
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = null;
  private transient WeakListenerList actionListeners;
  private boolean selectionChanged = false;
  private final UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
  
  /** Creates a new instance of JDbTable */
  public JDbXTable() {
    super(new DbTableModel());
    setSelectionForeground(Color.black);
    setSelectionBackground(new Color(204,204,255));
    setBackground(Color.white);
    putClientProperty("Quaqua.Table.style","striped");
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, null, "tableModel_activeRowChanged");
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't initialize the JDbTable activeRowChangeListener.", ex);
    }
  }

  /**
   * Sets the data model for this table to <code>newModel</code> and registers
   * with it for listener notifications from the new data model.
   * 
   * 
   * @param dataModel        the new data source for this table
   * @exception IllegalArgumentException      if <code>newModel</code> is <code>null</code>
   * @see #getModel
   * @beaninfo bound: true
   *  description: The model that is the source of the data for this view.
   */
  @Override
  public void setModel(TableModel dataModel) {
    if (this.getModel() instanceof DbTableModel)
      ((DbTableModel) this.getModel()).removeActiveRowChangeListener(activeRowChangeWeakListener);
    if (dataModel instanceof DbTableModel) {
      try {
        super.setModel(dataModel);
        super.setColumnModel(((DbTableModel) dataModel).getTableColumnModel());
      } catch (java.lang.IndexOutOfBoundsException e) {
        //ignore it #netbeans fora
      }
      ((DbTableModel) this.getModel()).addActiveRowChangeListener(activeRowChangeWeakListener);
    }/* else
      throw new IllegalArgumentException("The data model for JDbTable must be a DbTableModel.");//*/
  }
  
  public void tableModel_activeRowChanged(ActiveRowChangeEvent event) {
    if (!selectionChanged) {
      DbTableModel dbTableModel = (DbTableModel) this.getModel();
      int newPos = dbTableModel.getTableModelRow(convertRowIndexToView(event.getNewRowNumber()));
      if (newPos>=0 && newPos<getRowCount()) {
       try {
          if (getSelectedRow()!=newPos && newPos>=0 && newPos<dbTableModel.getRowCount())
            setRowSelectionInterval(newPos,newPos);
          updateViewPosition();
        }
        catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Can't adjust the selection.", ex);
        }
     }
   }
  }
  
  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (this.getModel() != null) {
      if (!e.getValueIsAdjusting()) {
        DbTableModel dbTableModel = (DbTableModel) this.getModel();
        int newRowNumber = dbTableModel.getDataSourceRow(convertRowIndexToModel(getSelectedRow()));
        try {
          selectionChanged = true;
          if ((newRowNumber>=1) &&
                  (newRowNumber<=dbTableModel.getRowCount()) &&
                  (dbTableModel.getDataSource().getRow()!=newRowNumber) &&
                  !dbTableModel.getDataSource().rowInserted()) {
            dbTableModel.getDataSource().absolute(newRowNumber);
          }
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Can't read from the tableModel.", ex);
        } finally {
          selectionChanged = false;
        }
      }
    }
    super.valueChanged(e);
  }
  
  private void updateViewPosition() {
    int newPos = getSelectedRow();
    if (newPos>=0) {
      updateViewRunnable.position = getCellRect(newPos,getSelectedColumn(), true);
      EventQueue.invokeLater(updateViewRunnable);
    }
  }
  
  public void setDataSource(DbDataSource dataSource) {
    if (this.getModel() instanceof DbTableModel)
      ((DbTableModel) this.getModel()).setDataSource(dataSource);
    else
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
  }
  
  @Override
  public DbDataSource getDataSource() {
    if (this.getModel() instanceof DbTableModel)
      return ((DbTableModel) this.getModel()).getDataSource();
    else
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    if (getDataSource()!=null)
      getDataSource().cancelRowUpdates();
  }

  @Override
  public void deleteRow() throws SQLException {
    if (getDataSource()!=null)
      getDataSource().deleteRow();
  }

  @Override
  public boolean first() throws SQLException {
    if (getRowCount()>0) {
      setRowSelectionInterval(0,0);
      updateViewPosition();
      return true;
    } else
      return false;
  }

  @Override
  public boolean isCanAddRows() {
    return (getDataSource()!=null)?getDataSource().isCanAddRows():false;
  }

  @Override
  public boolean isCanDeleteRows() {
    return (getDataSource()!=null)?getDataSource().isCanDeleteRows():false;
  }

  @Override
  public boolean isFirst() throws SQLException {
    return getSelectedRow()<=0;
  }

  @Override
  public boolean isLast() throws SQLException {
    return getSelectedRow()==(getRowCount()-1);
  }

  @Override
  public boolean last() throws SQLException {
    if (getRowCount()>0) {
      setRowSelectionInterval(getRowCount()-1,getRowCount()-1);
      updateViewPosition();
      return true;
    } else
      return false;
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    if (getDataSource()!=null)
      getDataSource().moveToInsertRow();
  }

  @Override
  public boolean next() throws SQLException {
    final int selectedRow = getSelectedRow();
    final int selectRow = Math.min(getRowCount()-1,selectedRow+1);
    setRowSelectionInterval(selectRow,selectRow);
    updateViewPosition();
    return selectRow==(selectedRow+1);
  }

  @Override
  public boolean previous() throws SQLException {
    if (getRowCount()>0) {
      final int selectedRow = getSelectedRow();
      final int selectRow = Math.max(0,selectedRow-1);
      setRowSelectionInterval(selectRow,selectRow);
      updateViewPosition();
      return selectRow==(selectedRow-1);
    } else
      return false;
  }

  @Override
  public boolean reload() {
    return (getDataSource()!=null)?getDataSource().reload():false;
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return (getDataSource()!=null)?getDataSource().rowInserted():false;
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return (getDataSource()!=null)?getDataSource().rowUpdated():false;
  }

  @Override
  public void updateRow() throws SQLException {
    if (getDataSource()!=null)
      getDataSource().updateRow();
  }

  @Override
  public void addActiveRowChangeListener(ActiveRowChangeListener l) {
    if (getDataSource()!=null)
      getDataSource().addActiveRowChangeListener(l);
  }

  @Override
  public void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (getDataSource()!=null)
      getDataSource().removeActiveRowChangeListener(l);
  }

  @Override
  public boolean canLock() {
    return getDataSource().canLock();
  }

  @Override
  public boolean lock() {
    return getDataSource().lock();
  }

  @Override
  public boolean lock(boolean fatal) {
    return getDataSource().lock(fatal);
  }

  @Override
  public void unlock() {
    getDataSource().unlock();
  }

  @Override
  public boolean isDataLoaded() {
    return (getDataSource()!=null)?getDataSource().isDataLoaded():false;
  }

  @Override
  public boolean loadData() {
    return (getDataSource()!=null)?getDataSource().loadData():false;
  }

  @Override
  public int getRow() throws SQLException {
    return (getDataSource()!=null)?getDataSource().getRow():0;
  }

  @Override
  public boolean hasCurrentRow() {
    return (getDataSource() != null) ? getDataSource().hasCurrentRow() : false;
  }

  @Override
  public boolean reload(int row) {
    return (getDataSource()!=null)?getDataSource().reload(row):false;
  }

  @Override
  public synchronized void removeActionListener(ActionListener l) {
    if (actionListeners != null && actionListeners.contains(l)) {
      actionListeners.removeElement(l);
    }
  }

  @Override
  public synchronized void addActionListener(ActionListener l) {
    WeakListenerList v = actionListeners == null ? new WeakListenerList(2) : actionListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      actionListeners = v;
    }
  }

  
  private class UpdateViewRunnable implements Runnable {
    java.awt.Rectangle position;
    
    UpdateViewRunnable() {
    }

    public void run() {
      scrollRectToVisible(position);
    }
  }
}
