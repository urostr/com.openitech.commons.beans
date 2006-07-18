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

public class TreeSelectionWeakListener extends WeakMethodReference<Object> implements TreeSelectionListener {
  public TreeSelectionWeakListener(TreeSelectionListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "valueChanged", new Class[] {TreeSelectionEvent.class});
    }
    catch (NoSuchMethodException ex) {
      ex.printStackTrace();
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
      ex.printStackTrace();
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
