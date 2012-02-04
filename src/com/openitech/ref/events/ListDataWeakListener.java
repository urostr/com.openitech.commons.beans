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
import javax.swing.event.ListDataEvent;
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

public class ListDataWeakListener extends WeakMethodReference<Object> implements ListDataListener {
  Method intervalAddedMethod;
  Method intervalRemovedMethod;
  Method contentsChangedMethod;

  public ListDataWeakListener(ListDataListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.intervalAddedMethod = clazz.getMethod("intervalAdded",
                                                 new Class[] {ListDataEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.intervalAddedMethod = null;
    }
    try {
      this.intervalRemovedMethod = clazz.getMethod("intervalRemoved",
          new Class[] {ListDataEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.intervalRemovedMethod = null;
    }
    try {
      this.contentsChangedMethod = clazz.getMethod("contentsChanged",
          new Class[] {ListDataEvent.class});
    }
    catch (NoSuchMethodException ex) {
      this.contentsChangedMethod = null;
    }
  }

  public ListDataWeakListener(Object owner, String intervalAddedMethod, String intervalRemovedMethod, String contentsChangedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.intervalAddedMethod = intervalAddedMethod==null?null:clazz.getMethod(intervalAddedMethod, new Class[] { ListDataEvent.class });
    this.intervalRemovedMethod = intervalRemovedMethod==null?null:clazz.getMethod(intervalRemovedMethod, new Class[] { ListDataEvent.class });
    this.contentsChangedMethod = contentsChangedMethod==null?null:clazz.getMethod(contentsChangedMethod, new Class[] { ListDataEvent.class });
  }

  public void intervalAdded(ListDataEvent listDataEvent) {
    try {
      if (this.isValid() && intervalAddedMethod!=null && this.isEnabled())
        intervalAddedMethod.invoke(this.get(), new Object[] {listDataEvent});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void intervalRemoved(ListDataEvent listDataEvent) {
    try {
      if (this.isValid() && intervalRemovedMethod!=null && this.isEnabled())
        intervalRemovedMethod.invoke(this.get(), new Object[] {listDataEvent});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void contentsChanged(ListDataEvent listDataEvent) {
    try {
      if (this.isValid() && contentsChangedMethod!=null && this.isEnabled())
        contentsChangedMethod.invoke(this.get(), new Object[] {listDataEvent});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
