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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
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

public class MouseMotionWeakListener extends WeakMethodReference<Object> implements MouseMotionListener {
  Method mouseDraggedMethod;
  Method mouseMovedMethod;

  public MouseMotionWeakListener(MouseMotionListener owner) {
    super(owner);
    Class clazz = owner.getClass();
    try {
      this.mouseDraggedMethod = clazz.getMethod("mouseDragged",
                                                 new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseDraggedMethod = null;
   }
   try {
      this.mouseMovedMethod = clazz.getMethod("mouseMoved",
          new Class[] {MouseEvent.class});
   }
   catch (NoSuchMethodException ex) {
     this.mouseMovedMethod = null;
   }
  }

  public MouseMotionWeakListener(Object owner, String mouseDraggedMethod, String mouseMovedMethod) throws
      NoSuchMethodException {
    super(owner);
    this.proxy = true;
    Class clazz = owner.getClass();
    this.mouseDraggedMethod = mouseDraggedMethod==null?null:clazz.getMethod(mouseDraggedMethod, new Class[] { MouseEvent.class });
    this.mouseMovedMethod = mouseMovedMethod==null?null:clazz.getMethod(mouseMovedMethod, new Class[] { MouseEvent.class });
  }

  public void mouseDragged(MouseEvent e) {
    try {
      if (this.isValid() && mouseDraggedMethod!=null && this.isEnabled())
        mouseDraggedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

  public void mouseMoved(MouseEvent e) {
    try {
      if (this.isValid() && mouseMovedMethod!=null && this.isEnabled())
        mouseMovedMethod.invoke(this.get(), new Object[] {e});
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex.getTargetException());
    }
    catch (IllegalAccessException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }
}
