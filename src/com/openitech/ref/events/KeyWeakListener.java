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

import com.openitech.ref.WeakMethodReference;
import java.lang.reflect.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
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
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
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
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
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
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
