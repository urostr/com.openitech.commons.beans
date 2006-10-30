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
import com.openitech.formats.FormatFactory;
import com.openitech.ref.WeakListenerList;
import com.openitech.ref.events.ListDataWeakListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author uros
 */
public class DbTableModel extends AbstractTableModel implements ListDataListener, ActiveRowChangeListener {
  private static final Pattern columnPattern = Pattern.compile("\\$C\\{(.*)\\}");
  private static final Pattern functionPattern = Pattern.compile("\\$F\\{(.*)\\}");
  private static final Pattern separatorPattern = Pattern.compile("\\$S\\{(.*)\\}");
  private static final Pattern rendererPattern = Pattern.compile("\\$R\\{(.*)\\}");
  private static final Pattern editorPattern = Pattern.compile("\\$E\\{(.*)\\}");
  private static final Pattern anyPattern = Pattern.compile("(\\$.\\{([^\\}]*)\\})");
  
  private String[][] columns = new String[][] {};
  private String[] rowColumnNames = new String[] {};
  private transient DbDataSource dataSource = null;
  private Map<String,Method> functionsMap = new HashMap<String,Method>();
  private Map<String,Class<? extends TableCellRenderer>> renderersMap = new HashMap<String,Class<? extends TableCellRenderer>>();
  private Map<String,Class<? extends TableCellEditor>> editorsMap = new HashMap<String,Class<? extends TableCellEditor>>();
  
  private ColumnDescriptor[] columnDescriptors = new ColumnDescriptor[] {};
  
  private transient ListDataWeakListener listDataWeakListener = new ListDataWeakListener(this);
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this);
  
  private transient WeakListenerList activeRowChangeListeners;
  
  private String separator = " ";
  
  /** Creates a new instance of DbTableModel */
  public DbTableModel() {
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
    try {
      if (this.dataSource!=null) {
        dataSource.lock();
        try {
          return columnDescriptors[columnIndex].getValueAt(rowIndex, columnIndex);
        } finally {
          dataSource.unlock();
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't getValueAt("+Integer.toString(rowIndex)+","+Integer.toString(columnIndex)+") from the dataSource. ["+ex.getMessage()+"]");
    }
    return null;
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
  public int getRowCount() {
    return this.dataSource==null?0:this.dataSource.getRowCount();
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
  public int getColumnCount() {
    return columns.length;
  }
  
  public void setColumns(String[][] headers) {
    this.columns = headers==null?new String[][] {}:headers;
    List<String> columnList = new ArrayList<String>();
    List<String> parameters = new ArrayList<String>();
    columnDescriptors = new ColumnDescriptor[columns.length];
    
    for (int h=0; h<columns.length; h++) {
      ColumnDescriptor descriptor = new ColumnDescriptor(this);
      
      for (int c=1; c<columns[h].length; c++) {
        parameters.clear();
        Matcher matcher = anyPattern.matcher(columns[h][c]);
        String column = columns[h][c];
        if (matcher.find()) {
          do {
            parameters.add(matcher.group(0));
          } while (matcher.find());
          column = matcher.replaceAll("");
        }
        for (String parameter:parameters) {
          matcher = functionPattern.matcher(parameter);
          
          if (descriptor.getFunctionKey()==null)
            descriptor.setFunctionKey(matcher.lookingAt()?matcher.group(1):null);

          matcher = rendererPattern.matcher(parameter);

          if (descriptor.getRendererKey()==null)
            descriptor.setRendererKey(matcher.lookingAt()?matcher.group(1):null);

          matcher = editorPattern.matcher(parameter);

          if (descriptor.getEditorKey()==null)
            descriptor.setEditorKey(matcher.lookingAt()?matcher.group(1):null);

          matcher = columnPattern.matcher(parameter);
          if (matcher.lookingAt()) {
              descriptor.getColumnNames().add(matcher.group(1));
              columnList.add(matcher.group(1));
            }
          
          matcher = separatorPattern.matcher(parameter);
          if (matcher.lookingAt()) {
             descriptor.getSeparators().add(matcher.group(1));
          }
        }
        
        if (column.length()>0) {
          columnList.add(column);
          descriptor.getColumnNames().add(column);
        }
      }
      
      if (descriptor.getSeparators().size()==0)
        descriptor.getSeparators().add(getSeparator());
      
      columnDescriptors[h] = descriptor;
    }
    
    this.rowColumnNames = new String[columnList.size()];
    columnList.toArray(this.rowColumnNames);
    
    fireTableStructureChanged();
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
  
  public void putAllFunctions(Map<String,Method> map) {
    functionsMap.putAll(map);
  }
  
  public Method putFunction(String key, Method method) {
    return functionsMap.put(key, method);
  }
  
  public Method removeFunction(String key) {
    return functionsMap.remove(key);
  }
  
  public void putAllRenderers(Map<String,Class<? extends TableCellRenderer>> map) {
    renderersMap.putAll(map);
  }
  
  public Class<? extends TableCellRenderer> putRenderer(String key, Class<? extends TableCellRenderer> method) {
    return renderersMap.put(key, method);
  }
  
  public Class<? extends TableCellRenderer> removeRenderer(String key) {
    return renderersMap.remove(key);
  }
  
  public void putAllEditors(Map<String,Class<? extends TableCellEditor>> map) {
    editorsMap.putAll(map);
  }
  
  public Class<? extends TableCellEditor> putEditor(String key, Class<? extends TableCellEditor> method) {
    return editorsMap.put(key, method);
  }
  
  public Class<? extends TableCellEditor> removeEditor(String key) {
    return editorsMap.remove(key);
  }
  
  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource!=null) {
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.removeListDataListener(listDataWeakListener);
    }
    this.dataSource = dataSource;
    if (this.dataSource!=null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.addListDataListener(listDataWeakListener);
    }
    fireTableDataChanged();
  }
  
  public DbDataSource getDataSource() {
    return dataSource;
  }
  
  public void intervalAdded(ListDataEvent e) {
    fireTableRowsInserted(e.getIndex0(), e.getIndex1());
  }
  
  public void intervalRemoved(ListDataEvent e) {
    fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
  }
  
  public void contentsChanged(ListDataEvent e) {
    dataSource.lock();
    try {
      if (e.getIndex0()==-1 ||
              e.getIndex1()==-1) {
        fireTableDataChanged();
      } else {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
      }
    } finally {
      dataSource.unlock();
    }
  }
  
  public void activeRowChanged(ActiveRowChangeEvent event) {
    dataSource.lock();
    try {
      fireActiveRowChange(event);
    } finally {
      dataSource.unlock();
    }
  }
  
  public void fieldValueChanged(ActiveRowChangeEvent event) {
    dataSource.lock();
    try {
      int row = dataSource.getRow();
      fireTableRowsUpdated(row, row);
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't read from the dataSource.", ex);
    } finally {
      dataSource.unlock();
    }
  }
  
  public int getDataSourceRow(int selectedRow) {
    return selectedRow+1;
  }
  
  public int getTableModelRow(int dataSourceRow) {
    return dataSourceRow-1;
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
      for (int i = 0; i < count; i++)
        ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
    }
  }
  
  public String getColumnName(int column) {
    return columns[column][0];
  }
  
  public Class<?> getColumnClass(int columnIndex) {
    return ColumnDescriptor.class;
  }
  
  public static class ColumnDescriptor {
    private static final Class<? extends TableCellRenderer> defaultTableCellRendererClass = DefaultTableCellRenderer.class;
    
    private final List<String> columnNames = new ArrayList<String>();
    private final List<String> separators = new ArrayList<String>();
    private String functionKey;
    private String rendererKey;
    private String editorKey;
    private boolean returnValueMethod = false;
    private boolean editable = false;
    private DbTableModel owner;
    
    public ColumnDescriptor(DbTableModel owner) {
      if (owner==null)
        throw new IllegalArgumentException("ColumnDescriptors owner can't be null.");
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
    
    public void setEditorKey(String editorKey) {
      this.editorKey = editorKey;
    }
    
    public String getEditorKey() {
      return editorKey;
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
      if (owner.dataSource!=null) {
        Map<String,Method> functionsMap = owner.functionsMap;
        
        Map<String,Class<? extends TableCellRenderer>> renderersMap = owner.renderersMap;
        Map<String,Class<? extends TableCellEditor>> editorsMap = owner.editorsMap;
        
        Class<? extends TableCellRenderer> renderer = defaultTableCellRendererClass;
        Class<? extends TableCellEditor> editor = null;
        Method function = null;
        
        if (rendererKey!=null && renderersMap.containsKey(rendererKey))
          renderer = renderersMap.get(rendererKey);
        
        if (editorKey!=null && editorsMap.containsKey(editorKey))
          editor = editorsMap.get(editorKey);
        
        if ((functionKey!=null) && functionsMap.containsKey(functionKey))
          function = functionsMap.get(functionKey);
        
        ValueMethod method = new ValueMethod(renderer, editor, function,
                owner.dataSource,
                rowIndex, columnIndex,
                columnNames,
                separators,
                owner.rowColumnNames
                );
        return returnValueMethod && (method.function!=null)?method:method.getValue();
      }
      return null;
    }
    
    public static class ValueMethod {
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
      
      protected ValueMethod(Class<? extends TableCellRenderer> renderer, Class<? extends TableCellEditor> editor, Method function, DbDataSource dataSource, int rowIndex, int columnIndex, List<String> columnNames, List<String> separators, String[] rowColumnNames) {
        this.function = function;
        this.dataSource = dataSource;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.columnNames = columnNames;
        this.separators = separators;
        this.rowColumnNames = rowColumnNames;
        this.renderer = renderer;
        this.editor = editor;
      }
      
      public Method getFunction() {
        return function;
      }
      
      public Object getFunctionValue() {
        try {
          return function.invoke(owner, new Object[] { this });
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't invoke function "+function.getName()+"("+this.getClass().getName()+"). ["+ex.getMessage()+"]");
          return "";
        }
      }
      
      public Object getValue() {
        try {
          if (dataSource!=null) {
            StringBuffer result = new StringBuffer();
            String lastSeparator = separators.size()>0?separators.get(separators.size()-1):" ";
            Iterator<String> separator = separators.iterator();
            
            for (String columnName:columnNames) {
              if (result.length()>0)
                result.append(separator.hasNext()?separator.next():lastSeparator);
              
              Object value = dataSource.getValueAt(rowIndex+1,columnName, rowColumnNames);
              
              if (value instanceof java.util.Date) {
                value = FormatFactory.DATE_FORMAT.format((java.util.Date) value);
              }
              
              result.append(value==null?"":value);
            }
            if (function!=null) {
              Object value = getFunctionValue();
              if (value!=null) {
                if (result.length()>0)
                  result.append(separator.hasNext()?separator.next():lastSeparator);
                result.append(value==null?"":value);
              }
            }
            return result.toString();
          }
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't getValueAt("+Integer.toString(rowIndex)+","+Integer.toString(columnIndex)+") from the dataSource. ["+ex.getMessage()+"]");
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
    }
  }
}
