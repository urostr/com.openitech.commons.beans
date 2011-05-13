package com.openitech.ref.events;

import com.openitech.ref.WeakMethodReference;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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

public class ActionWeakListener extends WeakMethodReference<Object> implements ActionListener {
  public ActionWeakListener(ActionListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "actionPerformed", new Class[] {ActionEvent.class});
    }
    catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public ActionWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { ActionEvent.class } );
  }

  public void actionPerformed(ActionEvent e) {
    try {
      execute(e);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
