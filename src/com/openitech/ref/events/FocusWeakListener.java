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
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 * @author Uroš Trojar
 * @version $Revision: 1.2 $
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
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void focusLost(FocusEvent e) {//mogoce
    try {
      if (this.isValid() && focusLostMethod!=null && this.isEnabled())
        focusLostMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
