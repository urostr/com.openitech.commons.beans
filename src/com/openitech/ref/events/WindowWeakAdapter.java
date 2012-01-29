/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
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
