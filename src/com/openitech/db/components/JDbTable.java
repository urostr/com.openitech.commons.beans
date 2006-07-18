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
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbTableModel;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

/**
 *
 * @author uros
 */
public class JDbTable extends JTable implements ListSelectionListener {
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = null;
  private boolean selectionChanged = false;
  private final UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
  
  /** Creates a new instance of JDbTable */
  public JDbTable() {
    setModel(new DbTableModel());
    setSelectionForeground(Color.black);
    setSelectionBackground(new Color(204,204,255));
    setBackground(Color.white);
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, null, "tableModel_activeRowChanged");
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't initialize the JDbTable activeRowChangeListener.", ex);
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
  public void setModel(TableModel dataModel) {
    if (this.getModel() instanceof DbTableModel)
      ((DbTableModel) this.getModel()).removeActiveRowChangeListener(activeRowChangeWeakListener);
    if (dataModel instanceof DbTableModel) {
      super.setModel(dataModel);
      ((DbTableModel) this.getModel()).addActiveRowChangeListener(activeRowChangeWeakListener);
    }/* else
      throw new IllegalArgumentException("The data model for JDbTable must be a DbTableModel.");//*/
  }
  
  public void tableModel_activeRowChanged(ActiveRowChangeEvent event) {
    if (!selectionChanged) {
      DbTableModel dbTableModel = (DbTableModel) this.getModel();
      int newPos = dbTableModel.getTableModelRow(event.getNewRowNumber());
      if (newPos>=0 && newPos<getRowCount()) {
       try {
          if (getSelectedRow()!=newPos && newPos>=0 && newPos<dbTableModel.getRowCount())
            setRowSelectionInterval(newPos,newPos);
          updateViewRunnable.position = getCellRect(newPos,getSelectedColumn(), true);
          SwingUtilities.invokeLater(updateViewRunnable);
        }
        catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Can't adjust the selection.", ex);
        }
     }
   }
  }
  
  public void valueChanged(ListSelectionEvent e) {
    if (this.getModel() != null) {
      if (!e.getValueIsAdjusting()) {
        DbTableModel dbTableModel = (DbTableModel) this.getModel();
        int newRowNumber = dbTableModel.getDataSourceRow(getSelectedRow());
        try {
          selectionChanged = true;
          if ((newRowNumber>=1) &&
                  (newRowNumber<=dbTableModel.getRowCount()) &&
                  (dbTableModel.getDataSource().getRow()!=newRowNumber)) {
            dbTableModel.getDataSource().absolute(newRowNumber);
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Can't read from the tableModel.", ex);
        } finally {
          selectionChanged = false;
        }
      }
    }
    super.valueChanged(e);
  }
  
  public void setDataSource(DbDataSource dataSource) {
    if (this.getModel() instanceof DbTableModel)
      ((DbTableModel) this.getModel()).setDataSource(dataSource);
    else
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
  }
  
  public DbDataSource getDataSource() {
    if (this.getModel() instanceof DbTableModel)
      return ((DbTableModel) this.getModel()).getDataSource();
    else
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
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
