package com.openitech.ref.events;

import com.openitech.ref.WeakMethodReference;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uro?° Trojar s.p.</p>
 * @author Uro?° Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class PropertyChangeWeakListener extends WeakMethodReference<Object> implements PropertyChangeListener {
  public PropertyChangeWeakListener(PropertyChangeListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "propertyChange", new Class[] {PropertyChangeEvent.class});
    }
    catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public PropertyChangeWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { PropertyChangeEvent.class } );
  }

  public void propertyChange(PropertyChangeEvent evt) {
    try {
      execute(evt);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
