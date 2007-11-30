package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.lang.reflect.Method;
import java.lang.reflect.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class FocusWeakListener extends WeakMethodReference<Object> implements FocusListener {
  Method focusGainedMethod;
  Method focusLostMethod;

  public FocusWeakListener(FocusListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.focusGainedMethod = clazz.getMethod("focusGained",
                                                 new Class[] {FocusEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.focusGainedMethod = null;
   }
   try {
      this.focusLostMethod = clazz.getMethod("focusLost",
          new Class[] {FocusEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.focusLostMethod = null;
   }
  }

  public FocusWeakListener(Object owner, String focusGainedMethod, String focusLostMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.focusGainedMethod = focusGainedMethod==null?null:clazz.getMethod(focusGainedMethod, new Class[] { FocusEvent.class });
    this.focusLostMethod = focusLostMethod==null?null:clazz.getMethod(focusLostMethod, new Class[] { FocusEvent.class });
  }

  public void focusGained(FocusEvent e) {
    try {
      if (this.isValid() && focusGainedMethod!=null && this.isEnabled())
        focusGainedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void focusLost(FocusEvent e) {
    try {
      if (this.isValid() && focusLostMethod!=null && this.isEnabled())
        focusLostMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
