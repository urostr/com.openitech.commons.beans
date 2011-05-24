package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uro� Trojar s.p.</p>
 * @author Uro� Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class TreeSelectionWeakListener extends WeakMethodReference<Object> implements TreeSelectionListener {
  public TreeSelectionWeakListener(TreeSelectionListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "valueChanged", new Class[] {TreeSelectionEvent.class});
    }
    catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public TreeSelectionWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { TreeSelectionEvent.class } );
  }

  public void valueChanged(TreeSelectionEvent e) {
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
