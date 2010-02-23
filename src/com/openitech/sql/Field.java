package com.openitech.sql;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author uros
 */
public class Field {

  String name;
  int type;
  int fieldIndex;

  /**
   *
   * @param name fieldName
   * @param type fieldType java.sql.Type
   */
  public Field(String name, int type) {
    super();
    this.name = name;
    this.type = type;
    this.fieldIndex = 1;
  }


  /**
   *
   * @param name fieldName
   * @param type fieldType java.sql.Type
   */
  public Field(String name, int type, int fieldIndex) {
    super();
    this.name = name;
    this.type = type;
    this.fieldIndex = fieldIndex;
  }

  /**
   *
   * @param name fieldName
   * @param type fieldType java.sql.Type
   */
  public Field(Field field) {
    super();
    this.name = field.name;
    this.type = field.type;
    this.fieldIndex = field.fieldIndex;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Field)) {
      return false;
    }
    final Field other = (Field) obj;
    if (this.fieldIndex!=other.fieldIndex) {
      return false;
    }
    if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
      return false;
    }
    return true;
  }

  /**
   *
   * @return field name
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return field type
   */
  public int getType() {
    return type;
  }

  /**
   * Get the value of fieldIndex
   *
   * @return the value of fieldIndex
   */
  public int getFieldIndex() {
    return fieldIndex;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.name != null ? this.name.toUpperCase().hashCode() : 0) + fieldIndex;
    return hash;
  }
  
  protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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

  @Override
  public String toString() {
    return name+":"+type+":"+ValueType.getType(type);
  }
}
