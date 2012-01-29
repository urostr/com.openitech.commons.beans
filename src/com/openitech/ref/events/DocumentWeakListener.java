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

import javax.swing.event.*;
import com.openitech.ref.WeakMethodReference;
import javax.swing.event.DocumentEvent;
import java.lang.reflect.*;
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

public class DocumentWeakListener extends WeakMethodReference<Object> implements DocumentListener {
  Method insertUpdateMethod;
  Method removeUpdateMethod;
  Method changedUpdateMethod;

  public DocumentWeakListener(DocumentListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.insertUpdateMethod = clazz.getMethod("insertUpdate",
                                                new Class[] {DocumentEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.insertUpdateMethod = null;
    }
    try {
      this.removeUpdateMethod = clazz.getMethod("removeUpdate",
          new Class[] {DocumentEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.removeUpdateMethod = null;
    }
    try {
      this.changedUpdateMethod = clazz.getMethod("changedUpdate",
          new Class[] {DocumentEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.changedUpdateMethod = null;
    }
  }

  public DocumentWeakListener(Object owner, String insertUpdateMethod, String removeUpdateMethod, String changedUpdateMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.insertUpdateMethod = insertUpdateMethod==null?null:clazz.getMethod(insertUpdateMethod, new Class[] { DocumentEvent.class });
    this.removeUpdateMethod = removeUpdateMethod==null?null:clazz.getMethod(removeUpdateMethod, new Class[] { DocumentEvent.class });
    this.changedUpdateMethod = changedUpdateMethod==null?null:clazz.getMethod(changedUpdateMethod, new Class[] { DocumentEvent.class });
  }

  public void insertUpdate(DocumentEvent e) {
    try {
      if (this.isValid() && insertUpdateMethod!=null && this.isEnabled())
        insertUpdateMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void removeUpdate(DocumentEvent e) {
    try {
      if (this.isValid() && removeUpdateMethod!=null && this.isEnabled())
        removeUpdateMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void changedUpdate(DocumentEvent e) {
    try {
      if (this.isValid() && changedUpdateMethod!=null && this.isEnabled())
        changedUpdateMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
