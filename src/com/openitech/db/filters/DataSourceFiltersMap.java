/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.filters;

import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author uros
 */
public class DataSourceFiltersMap implements java.util.Map<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> {

  private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
  private final java.util.Map<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> seekTypes = new java.util.LinkedHashMap<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>>();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, DataSourceFilters> filters = new java.util.HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, DataSourceFilters>();

  public int size() {
    return seekTypes.size();
  }

  private void updateFilters() {
    filters.clear();
    for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : seekTypes.entrySet()) {
      java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

      for (int i = 0; i < seekTypeList.size(); i++) {
        filters.put(seekTypeList.get(i), entry.getKey());
      }
    }

  }

  public DataSourceFilters getFilterFor(DataSourceFilters.AbstractSeekType<? extends Object> seekType) {
    return filters.get(seekType);
  }

  @Override
  public boolean isEmpty() {
    return seekTypes.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return seekTypes.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return seekTypes.containsValue(value);
  }

  @Override
  public List<AbstractSeekType<? extends Object>> get(Object key) {
    return seekTypes.get(key);
  }

  public void put(DataSourceFilters key, DataSourceFilters.AbstractSeekType<? extends Object> value) {
    List<AbstractSeekType<? extends Object>> list;
    if (seekTypes.containsKey(key)) {
      list = seekTypes.get(key);
    } else {
      list = new ArrayList<AbstractSeekType<? extends Object>>();
    }
    if (!list.contains(value)) {
      list.add(value);
    }
    put(key, list);
  }

  @Override
  public List<AbstractSeekType<? extends Object>> put(DataSourceFilters key, List<AbstractSeekType<? extends Object>> value) {
    List<AbstractSeekType<? extends Object>> result = seekTypes.put(key, value);
    updateFilters();
    firePropertyChange("model", false, true);
    return result;
  }

  @Override
  public List<AbstractSeekType<? extends Object>> remove(Object key) {
    List<AbstractSeekType<? extends Object>> result = null;
    if (seekTypes.containsKey(key)) {
      result = seekTypes.remove(key);
      updateFilters();
      firePropertyChange("removed", key, null);
      firePropertyChange("model", result, null);
    }
    return result;
  }

  @Override
  public void putAll(Map<? extends DataSourceFilters, ? extends List<AbstractSeekType<? extends Object>>> m) {
    seekTypes.putAll(m);
    updateFilters();
    firePropertyChange("model", false, true);
  }

  @Override
  public void clear() {
    firePropertyChange("clear", false, true);
    seekTypes.clear();
    updateFilters();
    firePropertyChange("model", false, true);
  }

  @Override
  public Set<DataSourceFilters> keySet() {
    return seekTypes.keySet();
  }

  @Override
  public Collection<List<AbstractSeekType<? extends Object>>> values() {
    return seekTypes.values();
  }

  @Override
  public Set<Entry<DataSourceFilters, List<AbstractSeekType<? extends Object>>>> entrySet() {
    return seekTypes.entrySet();
  }

  /**
   * Adds a PropertyChangeListener to the listener list.
   * If <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is performed.
   *
   * @param    listener  the property change listener to be added
   *
   * @see #removePropertyChangeListener
   * @see #getPropertyChangeListeners
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public synchronized void addPropertyChangeListener(
          PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Removes a PropertyChangeListener from the listener list. This method
   * should be used to remove PropertyChangeListeners that were registered
   * for all bound properties of this class.
   * <p>
   * If listener is null, no exception is thrown and no action is performed.
   *
   * @param listener the PropertyChangeListener to be removed
   *
   * @see #addPropertyChangeListener
   * @see #getPropertyChangeListeners
   * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
   */
  public synchronized void removePropertyChangeListener(
          PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(listener);
  }

  /**
   * Returns an array of all the property change listeners
   * registered on this component.
   *
   * @return all of this component's <code>PropertyChangeListener</code>s
   *         or an empty array if no property change
   *         listeners are currently registered
   *
   * @see      #addPropertyChangeListener
   * @see      #removePropertyChangeListener
   * @see      #getPropertyChangeListeners(java.lang.String)
   * @see      java.beans.PropertyChangeSupport#getPropertyChangeListeners
   * @since    1.4
   */
  public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }

  /**
   * Adds a PropertyChangeListener to the listener list for a specific
   * property. If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName one of the property names listed above
   * @param listener the property change listener to be added
   *
   * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners(java.lang.String)
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public synchronized void addPropertyChangeListener(
          String propertyName,
          PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a <code>PropertyChangeListener</code> from the listener
   * list for a specific property. This method should be used to remove
   * <code>PropertyChangeListener</code>s
   * that were registered for a specific bound property.
   * <p>
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName a valid property name
   * @param listener the PropertyChangeListener to be removed
   *
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners(java.lang.String)
   * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public synchronized void removePropertyChangeListener(
          String propertyName,
          PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Returns an array of all the listeners which have been associated
   * with the named property.
   *
   * @return all of the <code>PropertyChangeListener</code>s associated with
   *         the named property; if no such listeners have been added or
   *         if <code>propertyName</code> is <code>null</code>, an empty
   *         array is returned
   *
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners
   * @since 1.4
   */
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(
          String propertyName) {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners(propertyName);
  }

  /**
   * Support for reporting bound property changes for Object properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   */
  protected void firePropertyChange(String propertyName,
          Object oldValue, Object newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null ||
            (oldValue != null && newValue != null && oldValue.equals(newValue))) {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Support for reporting bound property changes for boolean properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   * @since 1.4
   */
  protected void firePropertyChange(String propertyName,
          boolean oldValue, boolean newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Support for reporting bound property changes for integer properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   * @since 1.4
   */
  protected void firePropertyChange(String propertyName,
          int oldValue, int newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a byte)
   * @param newValue the new value of the property (as a byte)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, Byte.valueOf(oldValue), Byte.valueOf(newValue));
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a char)
   * @param newValue the new value of the property (as a char)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a short)
   * @param newValue the old value of the property (as a short)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, Short.valueOf(oldValue), Short.valueOf(newValue));
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a long)
   * @param newValue the new value of the property (as a long)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, Long.valueOf(oldValue), Long.valueOf(newValue));
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a float)
   * @param newValue the new value of the property (as a float)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, Float.valueOf(oldValue), Float.valueOf(newValue));
  }

  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a double)
   * @param newValue the new value of the property (as a double)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
  }

  public static interface MapReader {

    /**
     * Get the value of filtersMap
     *
     * @return the value of filtersMap
     */
    public DataSourceFiltersMap getFiltersMap();
  }

  public static interface MapProperty extends MapReader {

    /**
     * Set the value of filtersMap
     *
     */
    public void setFiltersMap(DataSourceFiltersMap filtersMap);
  }
}
