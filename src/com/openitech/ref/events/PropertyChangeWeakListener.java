package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class PropertyChangeWeakListener extends WeakMethodReference<Object> implements PropertyChangeListener {
  public PropertyChangeWeakListener(PropertyChangeListener owner) {
    super(owner);
    try {
      init(owner.getClass(), "propertyChange", new Class[] {PropertyChangeEvent.class});
    }
    catch (NoSuchMethodException ex) {
      ex.printStackTrace();
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
      ex.printStackTrace();
    }
    catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
  }
}
