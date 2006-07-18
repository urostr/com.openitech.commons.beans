package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1 $
 */

public class ActionWeakListener extends WeakMethodReference<Object> implements ActionListener {
  public ActionWeakListener(ActionListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "actionPerformed", new Class[] {ActionEvent.class});
    }
    catch (NoSuchMethodException ex) {
      ex.printStackTrace();
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
      ex.printStackTrace();
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
