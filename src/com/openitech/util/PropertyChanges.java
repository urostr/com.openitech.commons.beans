/*
 * PropertyChanges.java
 *
 * Created on Torek, 20 marec 2007, 11:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

import com.openitech.Settings;
import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class PropertyChanges {
  
  // <editor-fold defaultstate="collapsed" desc=" PropertyChangeListener support ">
  /**
   * If any <code>PropertyChangeListeners</code> have been registered,
   * the <code>changeSupport</code> field describes them.
   *
   * @serial
   * @since 1.2
   * @see #addPropertyChangeListener
   * @see #removePropertyChangeListener
   * @see #firePropertyChange
   */
  private PropertyChangeSupport changeSupport;
  /**
   * Adds a PropertyChangeListener to the listener list. The listener is
   * registered for all bound properties of this class, including the
   * following:
   * <ul>
   *    <li>this Component's font ("font")</li>
   *    <li>this Component's background color ("background")</li>
   *    <li>this Component's foreground color ("foreground")</li>
   *    <li>this Component's focusability ("focusable")</li>
   *    <li>this Component's focus traversal keys enabled state
   *        ("focusTraversalKeysEnabled")</li>
   *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
   *        ("forwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
   *        ("backwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
   *        ("upCycleFocusTraversalKeys")</li>
   *    <li>this Component's preferred size ("preferredSize")</li>
   *    <li>this Component's minimum size ("minimumSize")</li>
   *    <li>this Component's maximum size ("maximumSize")</li>
   *    <li>this Component's name ("name")</li>
   * </ul>
   * Note that if this <code>Component</code> is inheriting a bound property, then no
   * event will be fired in response to a change in the inherited property.
   * <p>
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
   * property. The specified property may be user-defined, or one of the
   * following:
   * <ul>
   *    <li>this Component's font ("font")</li>
   *    <li>this Component's background color ("background")</li>
   *    <li>this Component's foreground color ("foreground")</li>
   *    <li>this Component's focusability ("focusable")</li>
   *    <li>this Component's focus traversal keys enabled state
   *        ("focusTraversalKeysEnabled")</li>
   *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
   *        ("forwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
   *        ("backwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
   *        ("upCycleFocusTraversalKeys")</li>
   * </ul>
   * Note that if this <code>Component</code> is inheriting a bound property, then no
   * event will be fired in response to a change in the inherited property.
   * <p>
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
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
    if (EventQueue.isDispatchThread())
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    else
      try {
        java.awt.EventQueue.invokeAndWait(new FirePropertyChanged(this, propertyName, oldValue, newValue));
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't firePropertyChange for '"+propertyName+"'", ex);;
      }
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
    firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
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
    firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
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
    firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
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
    firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
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
    firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
  }
  
  private final static class FirePropertyChanged implements Runnable {
    PropertyChanges owner;
    String propertyName;
    Object oldValue;
    Object newValue;
    
    FirePropertyChanged(PropertyChanges owner, String propertyName,
            Object oldValue, Object newValue) {
      this.owner = owner;
      this.propertyName = propertyName;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }
    
    public void run() {
      owner.changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
  }
  // </editor-fold>
}
