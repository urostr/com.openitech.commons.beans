package com.openitech;

import com.openitech.util.Equals;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;

public class ValueObserver extends Observable {

  private Object value;
  private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  /**
   * Get the value of treeNode
   *
   * @return the value of treeNode
   */
  public Object getValue() {
    return value;
  }

  /**
   * Set the value of treeNode
   *
   * @param treeNode new value of treeNode
   */
  public void setValue(Object value) {
    if (!Equals.equals(this.value, value)) {
      Object oldValue = this.value;
      this.value = value;
      propertyChangeSupport.firePropertyChange("value", oldValue, value);
      hasChanged(); notifyObservers(value);
    }
  }

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
