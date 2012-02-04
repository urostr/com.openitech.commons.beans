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


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.pool.proxool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class Invocation<T> implements Comparable<Invocation>, Cloneable {
  long timestamp = System.currentTimeMillis();
  final Method method;
  Object[] arguments;

  protected Invocation(Method method, Object[] arguments) {
    if (method==null) {
      throw new NullPointerException("Method method is null");
    }
    this.method = method;
    this.arguments = arguments;
  }

  protected Object invoke(T target) throws InvocationTargetException {
    try {
      return method.invoke(target, arguments);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Invocation.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Override
  public int compareTo(Invocation o) {
    return (int) (timestamp - o.timestamp);
  }

  public void timestamp() {
    timestamp = System.currentTimeMillis();
  }

  public Method getMethod() {
    return method;
  }

  public Object[] getArguments() {
    return arguments;
  }

  public void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }

  @Override
  protected Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      Invocation<T> result = new Invocation<T>(method, arguments);
      result.timestamp = this.timestamp;
      return result;
    }
  }

}
