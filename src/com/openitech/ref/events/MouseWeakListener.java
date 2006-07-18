package com.openitech.ref.events;

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import java.lang.reflect.Method;
import java.lang.reflect.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.1 $
 */

public class MouseWeakListener extends WeakMethodReference<Object> implements MouseListener {
  Method mouseClickedMethod;
  Method mousePressedMethod;
  Method mouseReleasedMethod;
  Method mouseEnteredMethod;
  Method mouseExitedMethod;

  public MouseWeakListener(MouseListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.mouseClickedMethod = clazz.getMethod("mouseClicked",
                                                 new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseClickedMethod = null;
   }
   try {
      this.mousePressedMethod = clazz.getMethod("mousePressed",
          new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mousePressedMethod = null;
   }
   try {
      this.mouseReleasedMethod = clazz.getMethod("mouseReleased",
          new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseReleasedMethod = null;
   }
   try {
      this.mouseEnteredMethod = clazz.getMethod("mouseEntered",
          new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseEnteredMethod = null;
   }
   try {
      this.mouseExitedMethod = clazz.getMethod("mouseExited",
          new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseExitedMethod = null;
   }
  }

  public MouseWeakListener(Object owner, String mouseClickedMethod, String mousePressedMethod, String mouseReleasedMethod, String mouseEnteredMethod, String mouseExitedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.mouseClickedMethod = mouseClickedMethod==null?null:clazz.getMethod(mouseClickedMethod, new Class[] { MouseEvent.class });
    this.mousePressedMethod = mousePressedMethod==null?null:clazz.getMethod(mousePressedMethod, new Class[] { MouseEvent.class });
    this.mouseReleasedMethod = mouseReleasedMethod==null?null:clazz.getMethod(mouseReleasedMethod, new Class[] { MouseEvent.class });
    this.mouseEnteredMethod = mouseEnteredMethod==null?null:clazz.getMethod(mouseEnteredMethod, new Class[] { MouseEvent.class });
    this.mouseExitedMethod = mouseExitedMethod==null?null:clazz.getMethod(mouseExitedMethod, new Class[] { MouseEvent.class });
  }

  public void mouseClicked(MouseEvent e) {
    try {
      if (this.isValid() && mouseClickedMethod!=null)
        mouseClickedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void mousePressed(MouseEvent e) {
    try {
      if (this.isValid() && mousePressedMethod!=null && this.isEnabled())
        mousePressedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void mouseReleased(MouseEvent e) {
    try {
      if (this.isValid() && mouseReleasedMethod!=null && this.isEnabled())
        mouseReleasedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void mouseEntered(MouseEvent e) {
    try {
      if (this.isValid() && mouseEnteredMethod!=null && this.isEnabled())
        mouseEnteredMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }

  public void mouseExited(MouseEvent e) {
    try {
      if (this.isValid() && mouseExitedMethod!=null && this.isEnabled())
        mouseExitedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
