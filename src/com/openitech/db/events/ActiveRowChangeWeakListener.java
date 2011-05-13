/*
 * ActiveRowChangeWeakListener.java
 *
 * Created on April 2, 2006, 7:36 PM
 *
 * $Revision $
 */

package com.openitech.db.events;

import com.openitech.ref.WeakMethodReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class ActiveRowChangeWeakListener extends WeakMethodReference<Object> implements ActiveRowChangeListener {
  Method fieldValueChangedMethod;
  Method activeRowChangedMethod;
  
  /** Creates a new instance of ActiveRowChangeWeakListener */
  public ActiveRowChangeWeakListener(ActiveRowChangeListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.fieldValueChangedMethod = clazz.getMethod("fieldValueChanged",
                                                 new Class[] {ActiveRowChangeEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.fieldValueChangedMethod = null;
    }
    try {
      this.activeRowChangedMethod = clazz.getMethod("activeRowChangeds",
          new Class[] {ActiveRowChangeEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.activeRowChangedMethod = null;
    }
  }

  public ActiveRowChangeWeakListener(Object owner, String fieldValueChangedMethod, String activeRowChangedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.fieldValueChangedMethod = fieldValueChangedMethod==null?null:clazz.getMethod(fieldValueChangedMethod, new Class[] { ActiveRowChangeEvent.class });
    this.activeRowChangedMethod = activeRowChangedMethod==null?null:clazz.getMethod(activeRowChangedMethod, new Class[] { ActiveRowChangeEvent.class });
  }

  public void fieldValueChanged(ActiveRowChangeEvent event) {
    try {
      if (this.isValid() && fieldValueChangedMethod!=null && isEnabled())
        fieldValueChangedMethod.invoke(this.get(), new Object[] {event});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void activeRowChanged(ActiveRowChangeEvent event) {
     try {
      if (this.isValid() && activeRowChangedMethod!=null && isEnabled())
        activeRowChangedMethod.invoke(this.get(), new Object[] {event});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
 }
}
