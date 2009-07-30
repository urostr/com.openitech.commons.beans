package com.openitech.db.model.tree;

import com.openitech.db.model.DbDataSource;
import com.openitech.util.Equals;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public final class DbTreeNodeType implements CharSequence, Comparable<CharSequence> {

  private String name = "";
  private DbTreeNodeType nextType;
  private List<String> keyColumns = new ArrayList<String>();
  private List<String> columnNames = new ArrayList<String>();
  private List<String> separators = new ArrayList<String>();
  private DbDataSource dataSource;

  public DbTreeNodeType() {
    this("ROOT");
  }

  public DbTreeNodeType(String name) {
    setName(name);
    separators.add(" ");
  }

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }

  /**
   * Set the value of dataSource
   *
   * @param dataSource new value of dataSource
   */
  public void setDataSource(DbDataSource dataSource) {
    if (!Equals.equals(this.dataSource, dataSource)) {
      DbDataSource oldValue = this.dataSource;
      this.dataSource = dataSource;
      propertyChangeSupport.firePropertyChange("dataSource", oldValue, dataSource);
    }
  }

  /**
   * Get the value of keyColumns
   *
   * @return the value of keyColumns
   */
  public List<String> getKeyColumns() {
    return keyColumns;
  }

  /**
   * Set the value of keyColumns
   *
   * @param keyColumns new value of keyColumns
   */
  public void setKeyColumns(String... keyColumns) {
    this.keyColumns.clear();
    for (String keyColumn : keyColumns) {
      this.keyColumns.add(keyColumn);
    }
    propertyChangeSupport.firePropertyChange("keyColumns", null, keyColumns);
  }

  /**
   * Get the value of separators
   *
   * @return the value of separators
   */
  public List<String> getSeparators() {
    return separators;
  }

  /**
   * Set the value of separators
   *
   * @param separators new value of separators
   */
  public void setSeparators(String... separators) {
    this.separators.clear();
    for (String separator : separators) {
      this.separators.add(separator);
    }
    propertyChangeSupport.firePropertyChange("separators", null, separators);
  }

  /**
   * Get the value of columnNames
   *
   * @return the value of columnNames
   */
  public List<String> getColumnNames() {
    return columnNames;
  }

  /**
   * Set the value of columnNames
   *
   * @param columnNames new value of columnNames
   */
  public void setColumnNames(String... columnNames) {
    for (String columnName : columnNames) {
      this.columnNames.add(columnName);
    }
    propertyChangeSupport.firePropertyChange("columnNames", null, columnNames);
  }

  /**
   * Get the value of nextType
   *
   * @return the value of nextType
   */
  public DbTreeNodeType getNextType() {
    return nextType;
  }

  /**
   * Set the value of nextType
   *
   * @param nextType new value of nextType
   */
  public void setNextType(DbTreeNodeType nextType) {
    DbTreeNodeType oldValue = this.nextType;
    this.nextType = nextType;
    propertyChangeSupport.firePropertyChange("nextType", oldValue, nextType);
  }

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  @Override
  public int length() {
    return name.length();
  }

  @Override
  public char charAt(int index) {
    return name.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return name.subSequence(start, end);
  }

  @Override
  public int compareTo(CharSequence o) {
    return name.compareToIgnoreCase(o == null ? "" : o.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DbTreeNodeType other = (DbTreeNodeType) obj;
    if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 61 * hash + (this.name != null ? this.name.toUpperCase().hashCode() : 0);
    return hash;
  }

  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  /**
   * Add PropertyChangeListener.
   *
   * @param listener
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Remove PropertyChangeListener.
   *
   * @param listener
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
}
