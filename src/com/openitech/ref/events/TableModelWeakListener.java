package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class TableModelWeakListener extends WeakMethodReference<Object> implements TableModelListener {
  public TableModelWeakListener(TableModelListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "tableChanged", new Class[] {TableModelEvent.class});
    }
    catch (NoSuchMethodException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public TableModelWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { TableModelEvent.class } );
  }

  public void tableChanged(TableModelEvent e) {
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
