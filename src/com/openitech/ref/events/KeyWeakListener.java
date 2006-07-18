package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.lang.reflect.Method;
import java.lang.reflect.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1 $
 */

public class KeyWeakListener extends WeakMethodReference<Object> implements KeyListener {
  Method keyTypedMethod;
  Method keyPressedMethod;
  Method keyReleasedMethod;

  public KeyWeakListener(KeyListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.keyTypedMethod = clazz.getMethod("keyTyped",
                                                 new Class[] {KeyEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.keyTypedMethod = null;
   }
   try {
      this.keyPressedMethod = clazz.getMethod("keyPressed",
          new Class[] {KeyEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.keyPressedMethod = null;
   }
   try {
      this.keyReleasedMethod = clazz.getMethod("keyReleased",
          new Class[] {KeyEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.keyReleasedMethod = null;
   }
  }

  public KeyWeakListener(Object owner, String keyTypedMethod, String keyPressedMethod, String keyReleasedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.keyTypedMethod = keyTypedMethod==null?null:clazz.getMethod(keyTypedMethod, new Class[] { KeyEvent.class });
    this.keyPressedMethod = keyPressedMethod==null?null:clazz.getMethod(keyPressedMethod, new Class[] { KeyEvent.class });
    this.keyReleasedMethod = keyReleasedMethod==null?null:clazz.getMethod(keyReleasedMethod, new Class[] { KeyEvent.class });
  }

  public void keyTyped(KeyEvent e) {
    try {
      if (this.isValid() && keyTypedMethod!=null && this.isEnabled())
        keyTypedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void keyPressed(KeyEvent e) {
    try {
      if (this.isValid() && keyPressedMethod!=null && this.isEnabled())
        keyPressedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void keyReleased(KeyEvent e) {
    try {
      if (this.isValid() && keyReleasedMethod!=null && this.isEnabled())
        keyReleasedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
