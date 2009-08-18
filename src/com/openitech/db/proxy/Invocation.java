/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.proxy;

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
