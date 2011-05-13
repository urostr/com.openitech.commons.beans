package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uros Trojar s.p.</p>
 * @author Uros Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class ChangeWeakListener extends WeakMethodReference<Object> implements ChangeListener {
  public ChangeWeakListener(ChangeListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "stateChanged", new Class[] {ChangeEvent.class});
    }
    catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public ChangeWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { ChangeEvent.class } );
  }

  public void stateChanged(ChangeEvent changeEvent) {
    try {
      execute(changeEvent);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
