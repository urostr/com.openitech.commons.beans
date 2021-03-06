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


package com.openitech.ref;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Odprte Informacijske Tehnologije Uroš Trojar s.p.</p>
 *
 * @author Uroš Trojar
 * @version $Revision: 1.1.1.1 $
 */
public class WeakObjectReference<T> extends WeakReference<T> implements ReferenceValidator {
  public WeakObjectReference(T referent) {
    super(referent);
  }

  public WeakObjectReference(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
  }
  
  @Override
  public boolean isValid() {
    return this.get()!=null;
  }
  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object object) {
    Object referent = this.get();
    if (referent!=null) {
      if ((object instanceof WeakReference) && (object!=null)) {
        return referent.equals( ( (WeakReference) object).get());
      } else if ((object instanceof WeakObjectReference) && (object!=null)) {
        return referent.equals( ( (WeakObjectReference) object).get());
      } else
        return referent.equals(object);
    } else
      return super.equals(object);
  }


  @Override
  public int hashCode() {
    Object referent = this.get();
    if (referent!=null) {
      return referent.hashCode();
    } else
      return super.hashCode();
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support the <code>Cloneable</code> interface.
   *   Subclasses that override the <code>clone</code> method can also throw this exception to indicate that an
   *   instance cannot be cloned.
   */
  public Object clone(ReferenceQueue<? super T> q) throws CloneNotSupportedException {
    if (isValid())
      return new WeakObjectReference<T>(this.get(), q);
    else
      throw new CloneNotSupportedException("Referent object is null.");
  }

}
