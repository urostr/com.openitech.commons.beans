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
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

/**
 *
 * @author uros
 */
public class JDbTable extends JTable implements ListSelectionListener, DbNavigatorDataSource {

  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = null;
  private boolean selectionChanged = false;
  private final UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();


  private static Method convertRowIndexToModel;
  private static Method convertRowIndexToView;
  private static Method setRowSorter;
  private static Constructor constructRowSorter;
  private static Method setComparator;

  private static boolean sortable;


  static {
    try {
      convertRowIndexToModel = JDbTable.class.getMethod("convertRowIndexToModel", int.class);
      convertRowIndexToView = JDbTable.class.getMethod("convertRowIndexToView", int.class);
      setRowSorter = JDbTable.class.getMethod("setRowSorter", Class.forName("javax.swing.RowSorter"));
      constructRowSorter = Class.forName("javax.swing.table.TableRowSorter").getConstructor(javax.swing.table.TableModel.class);
      setComparator = Class.forName("javax.swing.table.TableRowSorter").getMethod("setComparator", int.class, java.util.Comparator.class);

      if (convertRowIndexToModel != null &&
              convertRowIndexToView != null) {
        System.out.println("com.openitech.db.components.JDbTable is sortable.");
        sortable = true;
      }
    } catch (Throwable ex) {
      sortable = false;
      convertRowIndexToModel = null;
      convertRowIndexToView = null;
      setRowSorter = null;
      constructRowSorter = null;
    }
  }

  /** Creates a new instance of JDbTable */
  public JDbTable() {
    super(new DbTableModel());
    setSelectionForeground(Color.black);
    setSelectionBackground(new Color(204, 204, 255));
    setBackground(Color.white);

    putClientProperty("Quaqua.Table.style", "striped");
    javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
    org.jdesktop.swingx.action.BoundAction aReload = new org.jdesktop.swingx.action.BoundAction("Osveži podatke", "RELOAD");
    aReload.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (getDataSource() != null) {
          getDataSource().reload();
        }
      }
    });
    menu.add(aReload);
    setComponentPopupMenu(menu);
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
    if (this.getModel() instanceof DbTableModel) {
      ((DbTableModel) this.getModel()).removeActiveRowChangeListener(activeRowChangeWeakListener);
    }
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
    if (sortable) {
      try {
        Object rowSorter = constructRowSorter.newInstance(dataModel);

        for (int column=0; column<dataModel.getColumnCount(); column++) {
          setComparator.invoke(rowSorter, column, DbTableModel.ColumnDescriptor.ValueMethodComparator.getInstance());
        }

        setRowSorter.invoke(this, rowSorter);
      } catch (Throwable ex) {
        Logger.getLogger(JDbTable.class.getName()).log(Level.WARNING, null, ex);
      }
    }
  }

  public static boolean isSortable() {
    return sortable;
  }

  public void tableModel_activeRowChanged(ActiveRowChangeEvent event) {
    if (!selectionChanged) {
      DbTableModel dbTableModel = (DbTableModel) this.getModel();
      int newPos = dbTableModel.getTableModelRow(event.getNewRowNumber());
      if (sortable) {
        try {
          newPos = (Integer) convertRowIndexToView.invoke(this, Integer.valueOf(newPos));
        } catch (Throwable ex) {
          Logger.getLogger(JDbTable.class.getName()).log(Level.WARNING, ex.getMessage());
          newPos = dbTableModel.getTableModelRow(event.getNewRowNumber());
        }
      }
      if (newPos >= 0 && newPos < getRowCount()) {
        try {
          if (getSelectedRow() != newPos && newPos >= 0 && newPos < dbTableModel.getRowCount()) {
            setRowSelectionInterval(newPos, newPos);
          }
          updateViewPosition();
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Can't adjust the selection.", ex);
        }
      }
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    if (this.getModel() != null) {
      if (!e.getValueIsAdjusting()) {
        DbTableModel dbTableModel = (DbTableModel) this.getModel();
        int selectedRow = getSelectedRow();
        if (sortable) {
          try {
            selectedRow = (Integer) convertRowIndexToModel.invoke(this, Integer.valueOf(selectedRow));
          } catch (Throwable ex) {
            Logger.getLogger(JDbTable.class.getName()).log(Level.WARNING, ex.getMessage());
            selectedRow = getSelectedRow();
          }
        }
        int newRowNumber = dbTableModel.getDataSourceRow(selectedRow);
        try {
          selectionChanged = true;
          if ((newRowNumber >= 1) &&
                  (newRowNumber <= dbTableModel.getRowCount()) &&
                  (dbTableModel.getDataSource().getRow() != newRowNumber) &&
                  !dbTableModel.getDataSource().rowInserted()) {
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

  private void updateViewPosition() {
    int newPos = getSelectedRow();
    if (newPos >= 0) {
      updateViewRunnable.position = getCellRect(newPos, getSelectedColumn(), true);
      EventQueue.invokeLater(updateViewRunnable);
    }
  }

  public void setDataSource(DbDataSource dataSource) {
    if (this.getModel() instanceof DbTableModel) {
      ((DbTableModel) this.getModel()).setDataSource(dataSource);
    } else {
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
    }
  }

  public DbDataSource getDataSource() {
    if (this.getModel() instanceof DbTableModel) {
      return ((DbTableModel) this.getModel()).getDataSource();
    } else {
      throw new IllegalArgumentException("The data model for JDbTable is not a DbTableModel.");
    }
  }

  public void cancelRowUpdates() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().cancelRowUpdates();
    }
  }

  public void deleteRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().deleteRow();
    }
  }

  public boolean first() throws SQLException {
    if (getRowCount() > 0) {
      setRowSelectionInterval(0, 0);
      updateViewPosition();
      return true;
    } else {
      return false;
    }
  }

  public boolean isCanAddRows() {
    return (getDataSource() != null) ? getDataSource().isCanAddRows() : false;
  }

  public boolean isCanDeleteRows() {
    return (getDataSource() != null) ? getDataSource().isCanDeleteRows() : false;
  }

  public boolean isFirst() throws SQLException {
    return getSelectedRow() <= 0;
  }

  public boolean isLast() throws SQLException {
    return getSelectedRow() == (getRowCount() - 1);
  }

  public boolean last() throws SQLException {
    if (getRowCount() > 0) {
      setRowSelectionInterval(getRowCount() - 1, getRowCount() - 1);
      updateViewPosition();
      return true;
    } else {
      return false;
    }
  }

  public void moveToInsertRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().moveToInsertRow();
    }
  }

  public boolean next() throws SQLException {
    final int selectedRow = getSelectedRow();
    final int selectRow = Math.min(getRowCount() - 1, selectedRow + 1);
    setRowSelectionInterval(selectRow, selectRow);
    updateViewPosition();
    return selectRow == (selectedRow + 1);
  }

  public boolean previous() throws SQLException {
    if (getRowCount() > 0) {
      final int selectedRow = getSelectedRow();
      final int selectRow = Math.max(0, selectedRow - 1);
      setRowSelectionInterval(selectRow, selectRow);
      updateViewPosition();
      return selectRow == (selectedRow - 1);
    } else {
      return false;
    }
  }

  public boolean reload() {
    return (getDataSource() != null) ? getDataSource().reload() : false;
  }

  public boolean rowInserted() throws SQLException {
    return (getDataSource() != null) ? getDataSource().rowInserted() : false;
  }

  public boolean rowUpdated() throws SQLException {
    return (getDataSource() != null) ? getDataSource().rowUpdated() : false;
  }

  public void updateRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().updateRow();
    }
  }

  public void addActiveRowChangeListener(ActiveRowChangeListener l) {
    if (getDataSource() != null) {
      getDataSource().addActiveRowChangeListener(l);
    }
  }

  public void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (getDataSource() != null) {
      getDataSource().removeActiveRowChangeListener(l);
    }
  }

  public boolean lock() {
    return getDataSource().lock();
  }

  public boolean lock(boolean fatal) {
    return getDataSource().lock(fatal);
  }

  public void unlock() {
    getDataSource().unlock();
  }

  public boolean isDataLoaded() {
    return (getDataSource() != null) ? getDataSource().isDataLoaded() : false;
  }

  public boolean loadData() {
    return (getDataSource() != null) ? getDataSource().loadData() : false;
  }

  public int getRow() throws SQLException {
    return (getDataSource() != null) ? getDataSource().getRow() : 0;
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
