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

  /**
   *
   * @param name fieldName
   * @param type fieldType java.sql.Type
   */
  public Field(String name, int type) {
    super();
    this.name = name;
    this.type = type;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!this.getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    final Field other = (Field) obj;
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

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.name != null ? this.name.toUpperCase().hashCode() : 0);
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
}
