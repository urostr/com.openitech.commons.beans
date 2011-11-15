/*
 * JDbTable.java
 *
 * Created on Ponedeljek, 3 april 2006, 19:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbNavigatorDataSource;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.DbTableRowSorter;
import com.openitech.ref.WeakListenerList;
import java.awt.Color;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author uros
 */
public class JDbTable extends JTable implements ListSelectionListener, DbNavigatorDataSource {

  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = null;
  private transient WeakListenerList activeRowChangeListeners;
  private transient WeakListenerList actionListeners;
  private boolean selectionChanged = false;
  private final UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
  private static Method convertRowIndexToModel;
  private static Method convertRowIndexToView;
  private static Method setRowSorter;
  private static Constructor constructRowSorter;
  private static Method setComparator;
  private static boolean sortable;
  private boolean enableSorting = false;
  private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle");

  static {
    try {
      convertRowIndexToModel = JDbTable.class.getMethod("convertRowIndexToModel", int.class); //NOI18N
      convertRowIndexToView = JDbTable.class.getMethod("convertRowIndexToView", int.class); //NOI18N
      setRowSorter = JDbTable.class.getMethod("setRowSorter", Class.forName("javax.swing.RowSorter")); //NOI18N
      constructRowSorter = Class.forName("javax.swing.table.TableRowSorter").getConstructor(javax.swing.table.TableModel.class); //NOI18N
      setComparator = Class.forName("javax.swing.table.TableRowSorter").getMethod("setComparator", int.class, java.util.Comparator.class); //NOI18N

      if (convertRowIndexToModel != null && convertRowIndexToView != null) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("com.openitech.db.components.JDbTable is sortable."); //NOI18N
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
  final javax.swing.JCheckBoxMenuItem miSorting = new javax.swing.JCheckBoxMenuItem();

  /** Creates a new instance of JDbTable */
  public JDbTable() {
    super(new DbTableModel());
    setSelectionForeground(Color.black);
    setSelectionBackground(new Color(204, 204, 255));
    setBackground(Color.white);

    InputMap editorInputMap = getInputMap();
    ActionMap actionMap = getActionMap();
    Action copyAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    };
    editorInputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK), copyAction);
    actionMap.put(copyAction, copyAction);

    putClientProperty("Quaqua.Table.style", "striped"); //NOI18N
    javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
    if (sortable) {


      miSorting.setText(resourceBundle.getString("SORT_TABLE_DATA"));
      miSorting.setSelected(enableSorting);

      miSorting.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          setEnableSorting(!isEnableSorting());
          miSorting.setSelected(isEnableSorting());
        }
      });
      menu.add(miSorting);
    }

    try {
      Class.forName("org.apache.poi.hssf.usermodel.HSSFWorkbook"); //NOI18N

      org.jdesktop.swingx.action.BoundAction aExport = new org.jdesktop.swingx.action.BoundAction(resourceBundle.getString("EXPORT_TO_XLS"), "EXPORT"); //NOI18N

      final JTable owner = this;
      aExport.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
              com.openitech.util.HSSFWrapper.openWorkbook(owner);
            }
          });
        }
      });

      this.exportTableMenu = menu.add(aExport);
    } catch (ClassNotFoundException ex) {
      //ignore it;
    }

    

    org.jdesktop.swingx.action.BoundAction copy = new org.jdesktop.swingx.action.BoundAction(resourceBundle.getString("COPY"), "COPY"); //NOI18N
    copy.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    });

    menu.add(copy);

    org.jdesktop.swingx.action.BoundAction aReload = new org.jdesktop.swingx.action.BoundAction(resourceBundle.getString("RELOAD_DATA"), "RELOAD"); //NOI18N
    aReload.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getDataSource() != null) {
          getDataSource().reload();
        }
      }
    });

    menu.add(aReload);

    setComponentPopupMenu(menu);
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, null, "tableModel_activeRowChanged"); //NOI18N
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't initialize the JDbTable activeRowChangeListener.", ex); //NOI18N
    }
  }

  public void copy() {
    try {
      String value = getValueAt(getSelectedRow(), getSelectedColumn()).toString();
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(value), null);
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Copy Pressed. Value={0}", value); //NOI18N
    } catch (Exception ex) {
      //ignore
    }
  }
  private JMenuItem activeFiltersMenu;

  public void setActiveFiltersMenu(JMenuItem miActiveFilters) {
    this.activeFiltersMenu = addTableMenuItem(miActiveFilters, true);
  }

  public JMenuItem getActiveFiltersMenu() {
    return activeFiltersMenu;
  }
  private JMenuItem exportTableMenu = null;

  public JMenuItem getExportTableMenu() {
    return exportTableMenu;
  }

  public void setExportTableMenu(JMenuItem exportTableMenu) {
    if (isCanExportData()) {
      this.exportTableMenu = addTableMenuItem(exportTableMenu, false);
    }
  }
  protected boolean canExportData = true;

  /**
   * Get the value of canExportData
   *
   * @return the value of canExportData
   */
  public boolean isCanExportData() {
    return canExportData;
  }

  /**
   * Set the value of canExportData
   *
   * @param canExportData new value of canExportData
   */
  public void setCanExportData(boolean canExportData) {
    if (this.canExportData != canExportData) {
      this.canExportData = canExportData;
      if (exportTableMenu != null) {
        javax.swing.JPopupMenu menu = getComponentPopupMenu();
        int index = -1;
        if (exportTableMenu != null) {
          index = menu.getComponentIndex(exportTableMenu);
          if (index >= 0) {
            menu.remove(index);
          }
        }
      }
    }
  }

  public JMenuItem addTableMenuItem(JMenuItem jMenuItem, boolean separator) {
    JMenuItem result = null;
    javax.swing.JPopupMenu menu = getComponentPopupMenu();
    int index = -1;
    if (jMenuItem != null) {
      index = menu.getComponentIndex(jMenuItem);
      if (index >= 0) {
        menu.remove(index);

        if (separator && (menu.getComponent(index) instanceof javax.swing.JPopupMenu.Separator)) {
          menu.remove(index);
        }
      }

      result = null;
    }
    if (jMenuItem != null) {

      if (separator) {
        menu.insert(new javax.swing.JPopupMenu.Separator(), index > 0 ? index : 0);
      }
      menu.insert(jMenuItem, index > 0 ? index : 0);

      result = jMenuItem;
    }

    return result;
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
      if (activeRowChangeListeners!=null &&
          getDataSource()!=null) {
        for (Object l : activeRowChangeListeners) {
          getDataSource().addActiveRowChangeListener((ActiveRowChangeListener) l);
        }
      }
      ((DbTableModel) this.getModel()).addActiveRowChangeListener(activeRowChangeWeakListener);
    }/* else
    throw new IllegalArgumentException("The data model for JDbTable must be a DbTableModel.");//*/
    activateRowSorter();
  }

  private void activateRowSorter() {
    if (sortable) {
      try {
        if (isEnableSorting()) {
          boolean dbSortable = true;
          if (dataModel instanceof DbTableModel) {
            dbSortable = ((DbTableModel) dataModel).getDataSource().isSortable();
          }
          if (dbSortable) {
            DbTableRowSorter rowSorter = new DbTableRowSorter((DbTableModel) dataModel);

            setRowSorter.invoke(this, rowSorter);
          } else {
            Object rowSorter = constructRowSorter.newInstance(dataModel);

            for (int column = 0; column < dataModel.getColumnCount(); column++) {
              setComparator.invoke(rowSorter, column, DbTableModel.ColumnDescriptor.ValueMethodComparator.getInstance());
            }

            setRowSorter.invoke(this, rowSorter);
          }
        } else {
          setRowSorter.invoke(this, new Object[]{null});
        }
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
            if (isEditing()) {
              removeEditor();
            }
            setRowSelectionInterval(newPos, newPos);
          }
          updateViewPosition();
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Can't adjust the selection.", ex); //NOI18N
        }
      }
    }
  }

  @Override
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
          if ((newRowNumber >= 1) && (newRowNumber <= dbTableModel.getRowCount()) && (dbTableModel.getDataSource().getRow() != newRowNumber) && !dbTableModel.getDataSource().rowInserted()) {
            dbTableModel.getDataSource().absolute(newRowNumber);
          }
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Can't read from the tableModel.", ex); //NOI18N
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
      throw new IllegalArgumentException(resourceBundle.getString("THE DATA MODEL FOR JDBTABLE IS NOT A DBTABLEMODEL"));
    }
    checkDataSource();
  }

  @Override
  public DbDataSource getDataSource() {
    if (this.getModel() instanceof DbTableModel) {
      return ((DbTableModel) this.getModel()).getDataSource();
    } else {
      throw new IllegalArgumentException(resourceBundle.getString("THE DATA MODEL FOR JDBTABLE IS NOT A DBTABLEMODEL"));
    }
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().cancelRowUpdates();
    }
  }

  @Override
  public void deleteRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().deleteRow();
    }
  }

  @Override
  public boolean first() throws SQLException {
    if (getRowCount() > 0) {
      setRowSelectionInterval(0, 0);
      updateViewPosition();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isCanAddRows() {
    return (getDataSource() != null) ? getDataSource().isCanAddRows() : false;
  }

  @Override
  public boolean isCanDeleteRows() {
    return (getDataSource() != null) ? getDataSource().isCanDeleteRows() : false;
  }

  @Override
  public boolean isFirst() throws SQLException {
    return getSelectedRow() <= 0;
  }

  @Override
  public boolean isLast() throws SQLException {
    return getSelectedRow() == (getRowCount() - 1);
  }

  @Override
  public boolean last() throws SQLException {
    if (getRowCount() > 0) {
      setRowSelectionInterval(getRowCount() - 1, getRowCount() - 1);
      updateViewPosition();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().moveToInsertRow();
    }
  }

  @Override
  public boolean next() throws SQLException {
    final int selectedRow = getSelectedRow();
    final int selectRow = Math.min(getRowCount() - 1, selectedRow + 1);
    setRowSelectionInterval(selectRow, selectRow);
    updateViewPosition();
    return selectRow == (selectedRow + 1);
  }

  @Override
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

  @Override
  public boolean reload() {
    return (getDataSource() != null) ? getDataSource().reload() : false;
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return (getDataSource() != null) ? getDataSource().rowInserted() : false;
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return (getDataSource() != null) ? getDataSource().rowUpdated() : false;
  }

  @Override
  public void updateRow() throws SQLException {
    if (getDataSource() != null) {
      getDataSource().updateRow();
    }
  }

  @Override
  public void addActiveRowChangeListener(ActiveRowChangeListener l) {
    WeakListenerList v = activeRowChangeListeners == null ? new WeakListenerList(2) : activeRowChangeListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      activeRowChangeListeners = v;
    }
    if (getDataSource() != null) {
      getDataSource().addActiveRowChangeListener(l);
    }
  }

  @Override
  public void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (activeRowChangeListeners != null && activeRowChangeListeners.contains(l)) {
      activeRowChangeListeners.removeElement(l);
    }
    if (getDataSource() != null) {
      getDataSource().removeActiveRowChangeListener(l);
    }
  }

  @Override
  public boolean canLock() {
    return getDataSource()==null?true:getDataSource().canLock();
  }

  @Override
  public boolean lock() {
    return getDataSource()==null?true:getDataSource().lock();
  }

  @Override
  public boolean lock(boolean fatal) {
    return getDataSource()==null?true:getDataSource().lock(fatal);
  }

  @Override
  public void unlock() {
    if (getDataSource() != null) {
      getDataSource().unlock();
    }
  }

  @Override
  public boolean isDataLoaded() {
    return (getDataSource() != null) ? getDataSource().isDataLoaded() : false;
  }

  @Override
  public boolean loadData() {
    return (getDataSource() != null) ? getDataSource().loadData() : false;
  }

  @Override
  public int getRow() throws SQLException {
    return (getDataSource() != null) ? getDataSource().getRow() : 0;
  }

  @Override
  public boolean hasCurrentRow() {
    return (getDataSource() != null) ? getDataSource().hasCurrentRow() : false;
  }

  public boolean isEnableSorting() {
    return enableSorting;
  }

  public void setEnableSorting(boolean enableSorting) {
    if (sortable) {
      this.enableSorting = enableSorting;
      miSorting.setSelected(isEnableSorting());
      activateRowSorter();
    }
  }

  @Override
  public boolean reload(int row) {
    return (getDataSource() != null) ? getDataSource().reload(row) : false;
  }

  @Override
  public void tableChanged(TableModelEvent e) {
    super.tableChanged(e);
    checkDataSource();
  }

  private void checkDataSource() {
    if (getDataSource() != null) {
      setCanExportData(getDataSource().isCanExportData());
    }
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
  
  /*
   * @see http://www.javalobby.org/java/forums/t45536.html
   */
  @Override
  public boolean getScrollableTracksViewportHeight() {
    return getPreferredSize().height < getParent().getHeight();
  }
  /*
   * @see http://www.javalobby.org/java/forums/t45536.html
   */
  @Override
  public boolean getScrollableTracksViewportWidth() {
    return getPreferredSize().width < getParent().getWidth();
  }

  private class UpdateViewRunnable implements Runnable {

    java.awt.Rectangle position;

    UpdateViewRunnable() {
    }

    @Override
    public void run() {
      scrollRectToVisible(position);
    }
  }
}
