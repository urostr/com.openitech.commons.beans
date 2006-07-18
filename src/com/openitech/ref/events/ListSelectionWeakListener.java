package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1 $
 */

public class ListSelectionWeakListener extends WeakMethodReference<Object> implements ListSelectionListener {
  public ListSelectionWeakListener(ListSelectionListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "tableChanged", new Class[] {ListSelectionEvent.class});
    }
    catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  public ListSelectionWeakListener(Object owner, String methodName) throws
      NoSuchMethodException {
    super(owner.getClass(), owner, methodName, new Class[] { ListSelectionEvent.class } );
  }

  public void valueChanged(ListSelectionEvent e) {
    try {
      execute(e);
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
