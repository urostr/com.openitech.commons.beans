package com.openitech.ref.events;

import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
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

public class WindowWeakAdapter extends WindowWeakListener implements WindowListener, WindowStateListener, WindowFocusListener {
  Method windowGainedFocusMethod;
  Method windowLostFocusMethod;
  Method windowStateChangedMethod;

  public WindowWeakAdapter(Object owner,
                            String windowOpenedMethod,
                            String windowClosingMethod,
                            String windowClosedMethod,
                            String windowIconifiedMethod,
                            String windowDeiconifiedMethod,
                            String windowActivatedMethod,
                            String windowDeactivatedMethod,
                            String windowGainedFocusMethod,
                            String windowLostFocusMethod,
                            String windowStateChangedMethod
) throws
      NoSuchMethodException {
    super(owner, windowOpenedMethod, windowClosingMethod, windowClosedMethod, windowIconifiedMethod, windowDeiconifiedMethod, windowActivatedMethod,  windowDeactivatedMethod);
    Class clazz = owner.getClass();
    this.windowGainedFocusMethod = windowGainedFocusMethod==null?null:clazz.getMethod(windowGainedFocusMethod, new Class[] { WindowEvent.class });
    this.windowLostFocusMethod = windowLostFocusMethod==null?null:clazz.getMethod(windowLostFocusMethod, new Class[] { WindowEvent.class });
    this.windowStateChangedMethod = windowStateChangedMethod==null?null:clazz.getMethod(windowStateChangedMethod, new Class[] { WindowEvent.class });
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

  public void windowGainedFocus(WindowEvent e) {
    try {
     if (this.isValid() && windowGainedFocusMethod!=null && this.isEnabled())
       windowGainedFocusMethod.invoke(this.get(), new Object[] {e});
   }
   catch (InvocationTargetException ex) {
     throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
   }
   catch (IllegalAccessException ex) {
     Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
   }
 }

  public void windowLostFocus(WindowEvent e) {
    try {
      if (this.isValid() && windowLostFocusMethod!=null && this.isEnabled())
        windowLostFocusMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowStateChanged(WindowEvent e) {
    try {
      if (this.isValid() && windowStateChangedMethod!=null && this.isEnabled())
        windowStateChangedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
