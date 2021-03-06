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
 * DbTableModel.java
 *
 * Created on April 2, 2006, 11:42 AM
 *
 * $Revision $
 */
package com.openitech.db.model;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.text.FormatFactory;
import com.openitech.ref.WeakListenerList;
import com.openitech.ref.WeakObjectReference;
import com.openitech.ref.events.ListDataWeakListener;
import com.openitech.ref.events.PropertyChangeWeakListener;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;

/**
 *
 * @author uros
 */
public class DbTableModel extends AbstractTableModel implements ListDataListener, ActiveRowChangeListener, PropertyChangeListener {

  private static final Pattern columnPattern = Pattern.compile("\\$C\\{(.*)\\}");
  private static final Pattern functionPattern = Pattern.compile("\\$F\\{(.*)\\}");
  private static final Pattern formatPattern = Pattern.compile("\\$T\\{(.*)\\}");
  private static final Pattern separatorPattern = Pattern.compile("\\$S\\{(.*)\\}");
  private static final Pattern rendererPattern = Pattern.compile("\\$R\\{(.*)\\}");
  private static final Pattern editorPattern = Pattern.compile("\\$E\\{(.*)\\}");
  private static final Pattern widthPattern = Pattern.compile("\\$W\\{(.*)\\}");
  private static final Pattern maxWidthPattern = Pattern.compile("\\$X\\{(.*)\\}");
  private static final Pattern minWidthPattern = Pattern.compile("\\$N\\{(.*)\\}");
  private static final Pattern anyPattern = Pattern.compile("(\\$.\\{([^\\}]*)\\})");
  private String[][] columns = new String[][]{};
  private String[] rowColumnNames = new String[]{};
  private transient DbDataSource dataSource = null;
  private Map<String, DbTableModel.ColumnDescriptor.ValueMethod.Method> functionsMap = new HashMap<String, DbTableModel.ColumnDescriptor.ValueMethod.Method>();
  private Map<String, Class<? extends TableCellRenderer>> renderersMap = new HashMap<String, Class<? extends TableCellRenderer>>();
  private Map<String, Class<? extends TableCellEditor>> editorsMap = new HashMap<String, Class<? extends TableCellEditor>>();
  private Map<String, TableCellRenderer> rendererInstances = new HashMap<String, TableCellRenderer>();
  private Map<String, TableCellEditor> editorInstances = new HashMap<String, TableCellEditor>();
  protected ColumnDescriptor[] columnDescriptors = new ColumnDescriptor[]{};
  private transient ListDataWeakListener listDataWeakListener = new ListDataWeakListener(this);
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this);
  private transient PropertyChangeWeakListener propertyChangeWeakListener = new PropertyChangeWeakListener(this);
  private transient WeakListenerList activeRowChangeListeners;
  private String separator = " ";
  private boolean valuesAsString = false;
  private boolean autoInsertRow = false;

  /** Creates a new instance of DbTableModel */
  public DbTableModel() {
    renderersMap.put("DATE_TIME", DbTableModel.DateTimeRenderer.class);
    //rendererInstances.put("DATE_TIME", new DbTableModel.DateTimeRenderer());
  }

  /**
   * Get the value of valuesAsString
   *
   * @return the value of valuesAsString
   */
  public boolean isValuesAsString() {
    return valuesAsString;
  }

  /**
   * Set the value of valuesAsString
   *
   * @param valuesAsString new value of valuesAsString
   */
  public void setValuesAsString(boolean valuesAsString) {
    this.valuesAsString = valuesAsString;
  }

  public List<String> getDataSourceColumns(int columnIndex) {
    return Collections.unmodifiableList(columnDescriptors[columnIndex].columnNames);
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param rowIndex	the row whose value is to be queried
   * @param columnIndex 	the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      if (this.dataSource != null) {
//        dataSource.lock();
        boolean safeMode = dataSource.isSafeMode();
        if (!dataSource.isDataLoaded()) {
          dataSource.setSafeMode(false);
        }
        try {
          Object result = columnDescriptors[columnIndex].getValueAt(rowIndex, columnIndex);

          if (result != null) {
            return isValuesAsString() ? result.toString() : result;
          }
        } finally {
          dataSource.setSafeMode(safeMode);
        }
      }
    } catch (Exception ex) {
      StringBuilder sb = new StringBuilder();
      for (String column : columnDescriptors[columnIndex].columnNames) {
        sb.append(sb.length() > 0 ? ":" : "").append(column);
      }
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't getValueAt(" + Integer.toString(rowIndex) + "," + Integer.toString(columnIndex) + ") from the dataSource. [" + ex.getMessage() + "] " + sb.toString());
    }
    return null;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (getDataSource() == null) {
      return false;
    } else {
      boolean editable = columnDescriptors[columnIndex].isEditable() && (columnDescriptors[columnIndex].getCellEditor() != null);
      if ((rowIndex + 1) > getDataSource().getRowCount()) {
        editable = editable && getDataSource().isAutoInsert();
      }
      return editable;
    }
  }

  /**
   * Get the value of autoInsertRow
   *
   * @return the value of autoInsertRow
   */
  public boolean isAutoInsertRow() {
    return autoInsertRow;
  }

  /**
   * Set the value of autoInsertRow
   *
   * @param autoInsertRow new value of autoInsertRow
   */
  public void setAutoInsertRow(boolean autoInsertRow) {
    this.autoInsertRow = autoInsertRow;
  }

  /**
   * Returns the number of rows in the model. A
   * <code>JTable</code> uses this function to determine how many rows it
   * should display.  This function should be quick, as it
   * is called frequently during rendering.
   *
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  @Override
  public int getRowCount() {
    int result = this.dataSource == null ? 0 : this.dataSource.getRowCount();

    if ((autoInsertRow) && (result == 0)) {
      result = 1;
    }

    return result;
  }

  /**
   * Returns the number of columns in the model. A
   * <code>JTable</code> uses this function to determine how many columns it
   * should create and display by default.
   *
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  @Override
  public int getColumnCount() {
    return columns.length;
  }

  public void setColumns(String[][] headers) {
    this.columns = headers == null ? new String[][]{} : headers;
    List<String> columnList = new ArrayList<String>();
    List<String> parameters = new ArrayList<String>();
    columnDescriptors = new ColumnDescriptor[columns.length];

    for (int h = 0; h < columns.length; h++) {
      ColumnDescriptor descriptor = new ColumnDescriptor(this);

      for (int c = 1; c < columns[h].length; c++) {
        parameters.clear();
        Matcher matcher = anyPattern.matcher(columns[h][c]);
        String column = columns[h][c];
        if (matcher.find()) {
          do {
            parameters.add(matcher.group(0));
          } while (matcher.find());
          column = matcher.replaceAll("");
        }
        for (String parameter : parameters) {
          matcher = functionPattern.matcher(parameter);

          if (descriptor.getFunctionKey() == null) {
            descriptor.setFunctionKey(matcher.lookingAt() ? matcher.group(1) : null);
          }

          matcher = rendererPattern.matcher(parameter);

          if (descriptor.getRendererKey() == null) {
            descriptor.setRendererKey(matcher.lookingAt() ? matcher.group(1) : null);
          }

          matcher = formatPattern.matcher(parameter);

          if (descriptor.getFormatKey() == null) {
            descriptor.setFormatKey(matcher.lookingAt() ? matcher.group(1) : null);
          }

          matcher = editorPattern.matcher(parameter);

          if (descriptor.getEditorKey() == null) {
            descriptor.setEditorKey(matcher.lookingAt() ? matcher.group(1) : null);
            descriptor.setEditable(true);
          }

          matcher = columnPattern.matcher(parameter);
          if (matcher.lookingAt()) {
            descriptor.getColumnNames().add(matcher.group(1));
            columnList.add(matcher.group(1));
          }

          matcher = separatorPattern.matcher(parameter);
          if (matcher.lookingAt()) {
            descriptor.getSeparators().add(matcher.group(1));
          }

          matcher = widthPattern.matcher(parameter);
          if (matcher.lookingAt()) {
            descriptor.setWidth(Integer.parseInt(matcher.group(1)));
          }

          matcher = maxWidthPattern.matcher(parameter);
          if (matcher.lookingAt()) {
            descriptor.setMaxWidth(Integer.parseInt(matcher.group(1)));
          }

          matcher = minWidthPattern.matcher(parameter);
          if (matcher.lookingAt()) {
            descriptor.setMinWidth(Integer.parseInt(matcher.group(1)));
          }
        }

        if (column.length() > 0) {
          columnList.add(column);
          descriptor.getColumnNames().add(column);
        }
      }

      if (descriptor.getSeparators().size() == 0) {
        descriptor.getSeparators().add(getSeparator());
      }

      columnDescriptors[h] = descriptor;
    }

    this.rowColumnNames = new String[columnList.size()];
    columnList.toArray(this.rowColumnNames);

    fireTableStructureChanged();
  }

  public TableColumnModel getTableColumnModel() {
    DefaultTableColumnModelExt model = new DefaultTableColumnModelExt();
    ColumnFactory factory = ColumnFactory.getInstance();

    for (int column = 0; column < columns.length; column++) {
      javax.swing.table.TableColumn c = factory.createAndConfigureTableColumn(this, column);
      c.setCellRenderer(columnDescriptors[column].getCellRenderer());
      c.setCellEditor(columnDescriptors[column].getCellEditor());
      if (columnDescriptors[column].getWidth() > 0) {
        c.setWidth(columnDescriptors[column].getWidth());
      }
      if (columnDescriptors[column].getMaxWidth() > 0) {
        c.setMaxWidth(columnDescriptors[column].getMaxWidth());
      }
      if (columnDescriptors[column].getMinWidth() > 0) {
        c.setMinWidth(columnDescriptors[column].getMinWidth());
      }
      model.addColumn(c);
    }

    return model;
  }

  public String[][] getColumns() {
    return this.columns;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public void putAllFunctions(Map<String, DbTableModel.ColumnDescriptor.ValueMethod.Method> map) {
    functionsMap.putAll(map);
  }

  public DbTableModel.ColumnDescriptor.ValueMethod.Method putFunction(String key, DbTableModel.ColumnDescriptor.ValueMethod.Method method) {
    return functionsMap.put(key, method);
  }

  public DbTableModel.ColumnDescriptor.ValueMethod.Method removeFunction(String key) {
    return functionsMap.remove(key);
  }

  public void putAllRenderers(Map<String, Class<? extends TableCellRenderer>> map) {
    renderersMap.putAll(map);
  }

  public Class<? extends TableCellRenderer> putRenderer(String key, Class<? extends TableCellRenderer> method) {
    return renderersMap.put(key, method);
  }

  public TableCellRenderer putRendererInstance(String key, TableCellRenderer renderer) {
    return rendererInstances.put(key, renderer);
  }

  public Class<? extends TableCellRenderer> removeRenderer(String key) {
    rendererInstances.remove(key);
    return renderersMap.remove(key);
  }

  public void putAllEditors(Map<String, Class<? extends TableCellEditor>> map) {
    editorsMap.putAll(map);
  }

  public TableCellEditor putEditorInstance(String key, TableCellEditor editor) {
    return editorInstances.put(key, editor);
  }

  public Class<? extends TableCellEditor> putEditor(String key, Class<? extends TableCellEditor> method) {
    return editorsMap.put(key, method);
  }

  public Class<? extends TableCellEditor> removeEditor(String key) {
    return editorsMap.remove(key);
  }

  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource != null) {
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.removeListDataListener(listDataWeakListener);
      this.dataSource.removePropertyChangeListener(propertyChangeWeakListener);
    }
    this.dataSource = dataSource;
    if (this.dataSource != null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.addListDataListener(listDataWeakListener);
      this.dataSource.addPropertyChangeListener(propertyChangeWeakListener);
    }
    fireTableDataChanged();
  }

  public void populate() throws SQLException {
    if (!this.dataSource.isDataLoaded()) {
      this.dataSource.loadData();
    }

    String[][] columns = new String[this.dataSource.getColumnCount()][2];

    for (int c = 1; c <= columns.length; c++) {
      columns[c - 1][0] = this.dataSource.getColumnName(c);
      columns[c - 1][1] = columns[c - 1][0];
    }

    setColumns(columns);
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  @Override
  public void intervalAdded(ListDataEvent e) {
    fireTableRowsInserted(e.getIndex0(), e.getIndex1());
  }

  @Override
  public void intervalRemoved(ListDataEvent e) {
    fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
//    dataSource.lock();
//    try {
    if (e.getIndex0() == -1
            || e.getIndex1() == -1) {
      fireTableDataChanged();
    } else {
      fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }
//    } finally {
//      dataSource.unlock();
//    }
  }

  @Override
  public void activeRowChanged(ActiveRowChangeEvent event) {
//    dataSource.lock();
//    try {
    fireActiveRowChange(event);
//    } finally {
//      dataSource.unlock();
//    }
  }

  @Override
  public void fieldValueChanged(ActiveRowChangeEvent event) {
//    dataSource.lock();
    try {
      int row = dataSource.getRow();
      fireTableRowsUpdated(row, row);
    } catch (SQLException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't read from the dataSource.", ex);
    }
//    finally {
//      dataSource.unlock();
//    }
  }

  public int getDataSourceRow(int selectedRow) {
    return selectedRow + 1;
  }

  public int getTableModelRow(int dataSourceRow) {
    return dataSourceRow - 1;
  }

  public synchronized void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (activeRowChangeListeners != null && activeRowChangeListeners.contains(l)) {
      activeRowChangeListeners.removeElement(l);
    }
  }

  public synchronized void addActiveRowChangeListener(ActiveRowChangeListener l) {
    WeakListenerList v = activeRowChangeListeners == null ? new WeakListenerList(2) : activeRowChangeListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      activeRowChangeListeners = v;
    }
  }

  protected void fireActiveRowChange(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      java.util.List listeners = activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
      }
    }
  }

  @Override
  public String getColumnName(int column) {
    return columns[column][0];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return ColumnDescriptor.class;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    fireTableDataChanged();
  }

  public static class DateTimeRenderer extends javax.swing.table.DefaultTableCellRenderer {

    /**
     *
     * Returns the default table cell renderer.
     *
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     * 			<code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof java.util.Date) {
        value = FormatFactory.DATETIME_FORMAT.format((java.util.Date) value);
      } else if (value != null) {
        value = value.toString();
      }

      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    /**
     * Defines the single line of text this component will display.  If
     * the value of text is null or empty string, nothing is displayed.
     * <p>
     * The default value of this property is null.
     * <p>
     * This is a JavaBeans bound property.
     *
     *
     * @see #setVerticalTextPosition
     * @see #setHorizontalTextPosition
     * @see #setIcon
     * @beaninfo preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Defines the single line of text this component will display.
     */
    @Override
    public void setText(String text) {
      super.setText(text);
      if (text != null && text.length() > 12) {
        setToolTipText(text);
      } else {
        setToolTipText(null);
      }
    }
  }

  public static class ColumnDescriptor {

    private static class TooltipTableCellRenderer extends DefaultTableCellRenderer {

      /**
       * Defines the single line of text this component will display.  If
       * the value of text is null or empty string, nothing is displayed.
       * <p>
       * The default value of this property is null.
       * <p>
       * This is a JavaBeans bound property.
       *
       *
       * @see #setVerticalTextPosition
       * @see #setHorizontalTextPosition
       * @see #setIcon
       * @beaninfo preferred: true
       *        bound: true
       *    attribute: visualUpdate true
       *  description: Defines the single line of text this component will display.
       */
      @Override
      public void setText(String text) {
        super.setText(text);
        if (text != null && text.length() > 12) {
          setToolTipText(text);
        } else {
          setToolTipText(null);
        }
      }
    }
    private static final TableCellRenderer defaultTableCellRenderer = new TooltipTableCellRenderer();
    private static final Class<? extends TableCellRenderer> defaultTableCellRendererClass = defaultTableCellRenderer.getClass();
    private final List<String> columnNames = new ArrayList<String>();
    private final List<String> separators = new ArrayList<String>();
    private String functionKey;
    private String rendererKey;
    private String editorKey;
    private ValueMethod.Formats format;
    private boolean returnValueMethod = false;
    private boolean editable = false;
    private DbTableModel owner;

    public ColumnDescriptor(DbTableModel owner) {
      if (owner == null) {
        throw new IllegalArgumentException("ColumnDescriptors owner can't be null.");
      }
      this.owner = owner;
    }

    public List<String> getColumnNames() {
      return columnNames;
    }

    public List<String> getSeparators() {
      return separators;
    }

    public void setReturnValueMethod(boolean returnValueMethod) {
      this.returnValueMethod = returnValueMethod;
    }

    public boolean isReturnValueMethod() {
      return returnValueMethod;
    }

    public void setFunctionKey(String functionKey) {
      this.functionKey = functionKey;
    }

    public String getFunctionKey() {
      return functionKey;
    }

    public void setRendererKey(String rendererKey) {
      this.rendererKey = rendererKey;
    }

    public String getRendererKey() {
      return rendererKey;
    }

    public String getFormatKey() {
      return format==null?null:format.toString();
    }

    public void setFormatKey(String formatKey) {
      this.format = formatKey==null?null:ValueMethod.Formats.valueOf(formatKey);
    }

    public TableCellRenderer getCellRenderer() {
      TableCellRenderer renderer = null;
      String rendererKey = getRendererKey();
      if (rendererKey == null) {
        renderer = defaultTableCellRenderer;
      } else {
        Map<String, Class<? extends TableCellRenderer>> renderersMap = owner.renderersMap;
        Map<String, TableCellRenderer> rendererInstances = owner.rendererInstances;

        if (rendererInstances.containsKey(rendererKey)) {
          renderer = rendererInstances.get(rendererKey);
        } else if (renderersMap.containsKey(rendererKey)) {
          try {
            renderer = renderersMap.get(rendererKey).newInstance();
            rendererInstances.put(rendererKey, renderer);
          } catch (Exception err) {
            renderer = null;
          }
        }
      }
      return renderer;
    }

    public void setEditorKey(String editorKey) {
      this.editorKey = editorKey;
    }

    public String getEditorKey() {
      return editorKey;
    }

    public TableCellEditor getCellEditor() {
      TableCellEditor editor = null;
      String editorKey = getEditorKey();

      Map<String, Class<? extends TableCellEditor>> editorsMap = owner.editorsMap;
      Map<String, TableCellEditor> editorInstances = owner.editorInstances;

      if (editorInstances.containsKey(editorKey)) {
        editor = editorInstances.get(editorKey);
      } else if (editorsMap.containsKey(editorKey)) {
        try {
          editor = editorsMap.get(editorKey).newInstance();
          editorInstances.put(editorKey, editor);
        } catch (Exception err) {
          editor = null;
        }
      }
      return editor;
    }

    public void setEditable(boolean editable) {
      this.editable = editable;
    }

    public boolean isEditable() {
      return editable;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (owner.dataSource != null && rowIndex < owner.dataSource.getRowCount()) {
        Map<String, DbTableModel.ColumnDescriptor.ValueMethod.Method> functionsMap = owner.functionsMap;

        Map<String, Class<? extends TableCellRenderer>> renderersMap = owner.renderersMap;
        Map<String, Class<? extends TableCellEditor>> editorsMap = owner.editorsMap;

        Class<? extends TableCellRenderer> renderer = null; //defaultTableCellRendererClass;
        Class<? extends TableCellEditor> editor = null;
        DbTableModel.ColumnDescriptor.ValueMethod.Method function = null;

        if (rendererKey != null && renderersMap.containsKey(rendererKey)) {
          renderer = renderersMap.get(rendererKey);
        }

        if (editorKey != null && editorsMap.containsKey(editorKey)) {
          editor = editorsMap.get(editorKey);
        }

        if ((functionKey != null) && functionsMap.containsKey(functionKey)) {
          function = functionsMap.get(functionKey);
        }

        ValueMethod method = new ValueMethod(renderer, editor,
                format, 
                function,
                owner.dataSource,
                rowIndex, columnIndex,
                columnNames,
                separators,
                owner.rowColumnNames);
        //return returnValueMethod && (method.function!=null)?method:method.getValue();
        return method;
      }
      return null;
    }

    public static class ValueMethodComparator implements java.util.Comparator<ValueMethod> {

      private static ValueMethodComparator instance;

      private ValueMethodComparator() {
      }

      public static ValueMethodComparator getInstance() {
        if (instance == null) {
          instance = new ValueMethodComparator();
        }
        return instance;
      }

      @Override
      public int compare(ValueMethod o1, ValueMethod o2) {
        if (o1 == null && o2 != null) {
          return -1;
        } else if (o1 == null && o2 == null) {
          return 0;
        } else if (o1 != null && o2 == null) {
          return 1;
        } else {
          return o1.compareTo(o2);
        }
      }
    }

    public static class ValueMethod implements Comparable<ValueMethod> {

      protected static enum Formats {
        DECIMAL_FORMAT {
          private final java.text.NumberFormat format = FormatFactory.getDecimalNumberFormat(0, 2);

          @Override
          public Format getFormat() {
            return format;
          }

          @Override
          public String getCellFormat() {
            return "#,##0.00";
          }
        },
        INTEGER_FORMAT {
          private final java.text.NumberFormat format = FormatFactory.getIntegerNumberFormat(0);

          @Override
          public Format getFormat() {
            return format;
          }

          @Override
          public String getCellFormat() {
            return "#,##0";
          }
        },
        PROCENT_FORMAT {
          private final java.text.NumberFormat format = FormatFactory.getPercentageNumberFormat(0, 2);

          @Override
          public Format getFormat() {
            return format;
          }

          @Override
          public String getCellFormat() {
            return "#,##0.00%";
          }
        },
        DATE_FORMAT {
          private final java.text.DateFormat format = FormatFactory.DATE_FORMAT;

          @Override
          public Format getFormat() {
            return format;
          }

          @Override
          public String getCellFormat() {
            return "d.m.yyyy";
          }
        },
        DATETIME_FORMAT {
          private final java.text.DateFormat format = FormatFactory.DATETIME_FORMAT;

          @Override
          public Format getFormat() {
            return format;
          }

          @Override
          public String getCellFormat() {
            return "d.mm.yyyy hh:mm";
          }
        };

        public abstract Format getFormat();
        public abstract String getCellFormat();


        public String format(Object... values) {
          StringBuilder sb = new StringBuilder();

            for (Object object : values) {
              sb.append(getFormat().format(object));
            }
            return sb.toString();
        }
      }

//      private static final java.text.NumberFormat DECIMAL_FORMAT = FormatFactory.getDecimalNumberFormat(0, 2);
//      private static final java.text.NumberFormat INTEGER_FORMAT = FormatFactory.getIntegerNumberFormat(0);
      private Method function;
      private DbDataSource dataSource;
      private int rowIndex;
      private int columnIndex;
      private List<String> columnNames;
      private String[] rowColumnNames;
      private Class<? extends TableCellRenderer> renderer;
      private Class<? extends TableCellEditor> editor;
      private List<String> separators;
      private Object owner = null;
      private Formats formater = null;

      protected ValueMethod(Class<? extends TableCellRenderer> renderer, Class<? extends TableCellEditor> editor, Formats formater, Method function, DbDataSource dataSource, int rowIndex, int columnIndex, List<String> columnNames, List<String> separators, String[] rowColumnNames) {
        this.function = function;
        this.dataSource = dataSource;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.columnNames = columnNames;
        this.separators = separators;
        this.rowColumnNames = rowColumnNames;
        this.renderer = renderer;
        this.editor = editor;
        this.formater = formater;
      }

      public ValueMethod(Method function, DbDataSource dataSource, int rowIndex, int columnIndex, List<String> columnNames, List<String> separators, String[] rowColumnNames) {
        this(null, null, null, function, dataSource, rowIndex, columnIndex, columnNames, separators, rowColumnNames);
      }

      public Method getFunction() {
        return function;
      }

      public Object getFunctionValue() {
        return function.invoke(this);
      }

      public Object getValue(String columnName) {
        int pos = columnNames.indexOf(columnName);

        return pos >= 0 ? getValues().get(pos) : null;
      }

      public java.util.List<Object> getValues() {
        java.util.List<Object> result = new java.util.ArrayList<Object>();
        try {
          for (String columnName : columnNames) {
            result.add(dataSource.getValueAt(rowIndex + 1, columnName, rowColumnNames));
          }
        } catch (SQLException ex) {
          Logger.getLogger(DbTableModel.class.getName()).log(Level.SEVERE, null, ex);
          result.clear();
        }
        return result;
      }

      public List<String> getCellFormats() {

        java.util.List<String> result = new java.util.ArrayList<String>();
        try {
          for (String columnName : columnNames) {
            Object value = dataSource.getValueAt(rowIndex + 1, columnName, rowColumnNames);
            String format = null;

            if (formater != null) {
              format = formater.getCellFormat();
            } else if (value instanceof java.util.Date) {
              format = Formats.DATE_FORMAT.getCellFormat();
            } else if (value instanceof java.lang.Number) {
              if ((value instanceof java.math.BigDecimal)
                      || (value instanceof java.lang.Double)
                      || (value instanceof java.lang.Float)) {
                format = Formats.DECIMAL_FORMAT.getCellFormat();
              } else {
                format = Formats.INTEGER_FORMAT.getCellFormat();
              }
            } else {
              format = null;
            }

            result.add(format);
          }
        } catch (SQLException ex) {
          Logger.getLogger(DbTableModel.class.getName()).log(Level.SEVERE, null, ex);
          result.clear();
        }
        return result;
      }

      private String getValueAsString() {
        try {
          if (dataSource != null) {
            StringBuilder result = new StringBuilder();
            String lastSeparator = separators.size() > 0 ? separators.get(separators.size() - 1) : " ";
            Iterator<String> separator = separators.iterator();

            for (String columnName : columnNames) {
              if (result.length() > 0) {
                result.append(separator.hasNext() ? separator.next() : lastSeparator);
              }


              Object value = null;
              if ((rowIndex + 1) <= dataSource.getRowCount()) {
                value = dataSource.getValueAt(rowIndex + 1, columnName, rowColumnNames);
              }

              if (renderer == null) {
                if (formater != null) {
                  value = formater.format(value);
                } else if (value instanceof java.util.Date) {
                  value = Formats.DATE_FORMAT.format((java.util.Date) value);
                } else if (value instanceof java.lang.Number) {
                  if ((value instanceof java.math.BigDecimal)
                          || (value instanceof java.lang.Double)
                          || (value instanceof java.lang.Float)) {
                    value = Formats.DECIMAL_FORMAT.format((java.lang.Number) value);
                  } else {
                    value = Formats.INTEGER_FORMAT.format((java.lang.Number) value);
                  }
                } else if (((value instanceof java.sql.Clob) || (value instanceof javax.sql.rowset.serial.SerialClob)) && (value != null)) {
                  try {
                    if (((java.sql.Clob) value).length() > 0) {
                      value = ((java.sql.Clob) value).getSubString(1L, (int) ((java.sql.Clob) value).length());
                    } else {
                      value = "";
                    }
                  } catch (SQLException ex) {
                    value = "";
                    Logger.getLogger(DbFieldObserver.class.getName()).log(Level.SEVERE, null, ex);
                  }
                }
              } else if ((DateTimeRenderer.class.isAssignableFrom(renderer)) && (value instanceof java.util.Date)) {
                value = FormatFactory.DATETIME_FORMAT.format((java.util.Date) value);
              }

              result.append(value == null ? "" : value);
            }
            if (function != null) {
              Object value = getFunctionValue();
              if (value != null) {
                if (result.length() > 0) {
                  result.append(separator.hasNext() ? separator.next() : lastSeparator);
                }
                result.append(value == null ? "" : value);
              }
            }
            return result.toString();
          }
        } catch (Exception ex) {
          StringBuilder sb = new StringBuilder();
          for (String column : columnNames) {
            sb.append(sb.length() > 0 ? ":" : "").append(column);
          }
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't getValueAt(" + Integer.toString(rowIndex) + "," + Integer.toString(columnIndex) + ") from the dataSource. [" + ex.getMessage() + "] " + sb.toString());
        }
        return null;
      }

      public int getRowIndex() {
        return rowIndex;
      }

      public int getColumnIndex() {
        return columnIndex;
      }

      public DbDataSource getDataSource() {
        return dataSource;
      }

      public String[] getRowColumnNames() {
        return rowColumnNames;
      }

      public List<String> getColumnNames() {
        return columnNames;
      }

      public List<String> getSeparators() {
        return separators;
      }

      public Class<? extends TableCellRenderer> getRenderer() {
        return renderer;
      }

      public Class<? extends TableCellEditor> getEditor() {
        return editor;
      }

      public void setOwner(Object owner) {
        this.owner = owner;
      }

      public Object getOwner() {
        return owner;
      }

      @Override
      public String toString() {
        return getValueAsString();
      }

      @Override
      public int compareTo(ValueMethod that) {
        if (this.columnNames.size() == that.columnNames.size()) {
          int result = 0;

          for (int pos = 0; (pos < columnNames.size()) && result == 0; pos++) {
            try {
              Object this_value = this.dataSource.getValueAt(this.rowIndex + 1, columnNames.get(pos), rowColumnNames);
              Object that_value = that.dataSource.getValueAt(that.rowIndex + 1, columnNames.get(pos), rowColumnNames);

              if (this_value == null && that_value != null) {
                result = -1;
              } else if (this_value == null && that_value == null) {
                result = 0;
              } else if (this_value != null && that_value == null) {
                result = 1;
              } else if ((this_value instanceof Comparable)
                      && (that_value instanceof Comparable)) {
                result = ((Comparable) this_value).compareTo(that_value);
              } else {
                result = this_value.toString().compareTo(that_value.toString());
              }
            } catch (SQLException ex) {
              result = 0;
            }
          }

          return result;
        } else {
          return this.toString().compareTo(that.toString());
        }
      }

      public static class Method {

        private WeakObjectReference owner;
        private java.lang.reflect.Method method;

        public Method(Object owner, java.lang.reflect.Method method) {
          this.owner = new WeakObjectReference(owner);
          this.method = method;
        }

        public Object invoke(DbTableModel.ColumnDescriptor.ValueMethod value) {
          try {
            return method.invoke(owner.get(), new Object[]{value});
          } catch (Exception ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can't invoke function " + method.getName() + "(" + owner.getClass().getName() + "). [" + ex.getMessage() + "]");
            return "";
          }
        }
      }
    }
    /**
     * Holds value of property maxWidth.
     */
    private int maxWidth;

    /**
     * Getter for property maxWidth.
     * @return Value of property maxWidth.
     */
    public int getMaxWidth() {
      return this.maxWidth;
    }

    /**
     * Setter for property maxWidth.
     * @param maxWidth New value of property maxWidth.
     */
    public void setMaxWidth(int maxWidth) {
      this.maxWidth = maxWidth;
    }
    /**
     * Holds value of property minWidth.
     */
    private int minWidth;

    /**
     * Getter for property minWidth.
     * @return Value of property minWidth.
     */
    public int getMinWidth() {
      return this.minWidth;
    }

    /**
     * Setter for property minWidth.
     * @param minWidth New value of property minWidth.
     */
    public void setMinWidth(int minWidth) {
      this.minWidth = minWidth;
    }
    /**
     * Holds value of property width.
     */
    private int width;

    /**
     * Getter for property width.
     * @return Value of property width.
     */
    public int getWidth() {
      return this.width;
    }

    /**
     * Setter for property width.
     * @param width New value of property width.
     */
    public void setWidth(int width) {
      this.width = width;
    }
  }
}
