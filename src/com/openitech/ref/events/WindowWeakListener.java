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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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

public class WindowWeakListener extends WeakMethodReference<Object> implements WindowListener {
  Method windowOpenedMethod;
  Method windowClosingMethod;
  Method windowClosedMethod;
  Method windowIconifiedMethod;
  Method windowDeiconifiedMethod;
  Method windowActivatedMethod;
  Method windowDeactivatedMethod;

  public WindowWeakListener(WindowListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.windowOpenedMethod = clazz.getMethod("windowOpened",
                                                new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowOpenedMethod = null;
    }
    try {
      this.windowClosingMethod = clazz.getMethod("windowClosing",
                                                 new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowClosingMethod = null;
    }
    try {
      this.windowClosedMethod = clazz.getMethod("windowClosed",
                                                new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowClosedMethod = null;
    }
    try {
      this.windowIconifiedMethod = clazz.getMethod("windowIconified",
          new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowIconifiedMethod = null;
    }
    try {
      this.windowDeiconifiedMethod = clazz.getMethod("windowDeiconified",
          new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowDeiconifiedMethod = null;
    }
    try {
      this.windowActivatedMethod = clazz.getMethod("windowActivated",
          new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowActivatedMethod = null;
    }
    try {
      this.windowDeactivatedMethod = clazz.getMethod("windowDeactivated",
          new Class[] {WindowEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.windowDeactivatedMethod = null;
    }
  }

  public WindowWeakListener(Object owner,
                            String windowOpenedMethod,
                            String windowClosingMethod,
                            String windowClosedMethod,
                            String windowIconifiedMethod,
                            String windowDeiconifiedMethod,
                            String windowActivatedMethod,
                            String windowDeactivatedMethod
      ) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.windowOpenedMethod = windowOpenedMethod==null?null:clazz.getMethod(windowOpenedMethod, new Class[] { WindowEvent.class });
    this.windowClosingMethod = windowClosingMethod==null?null:clazz.getMethod(windowClosingMethod, new Class[] { WindowEvent.class });
    this.windowClosedMethod = windowClosedMethod==null?null:clazz.getMethod(windowClosedMethod, new Class[] { WindowEvent.class });
    this.windowIconifiedMethod = windowIconifiedMethod==null?null:clazz.getMethod(windowIconifiedMethod, new Class[] { WindowEvent.class });
    this.windowDeiconifiedMethod = windowDeiconifiedMethod==null?null:clazz.getMethod(windowDeiconifiedMethod, new Class[] { WindowEvent.class });
    this.windowActivatedMethod = windowActivatedMethod==null?null:clazz.getMethod(windowActivatedMethod, new Class[] { WindowEvent.class });
    this.windowDeactivatedMethod = windowDeactivatedMethod==null?null:clazz.getMethod(windowDeactivatedMethod, new Class[] { WindowEvent.class });
  }


  public void windowOpened(WindowEvent e) {
    try {
      if (this.isValid() && windowOpenedMethod!=null && this.isEnabled())
        windowOpenedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowClosing(WindowEvent e) {
    try {
      if (this.isValid() && windowClosingMethod!=null && this.isEnabled())
        windowClosingMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowClosed(WindowEvent e) {
    try {
      if (this.isValid() && windowClosedMethod!=null && this.isEnabled())
        windowClosedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowIconified(WindowEvent e) {
    try {
      if (this.isValid() && windowIconifiedMethod!=null && this.isEnabled())
        windowIconifiedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowDeiconified(WindowEvent e) {
    try {
      if (this.isValid() && windowDeiconifiedMethod!=null && this.isEnabled())
        windowDeiconifiedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowActivated(WindowEvent e) {
    try {
      if (this.isValid() && windowActivatedMethod!=null && this.isEnabled())
        windowActivatedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void windowDeactivated(WindowEvent e) {
    try {
      if (this.isValid() && windowDeactivatedMethod!=null && this.isEnabled())
        windowDeactivatedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
