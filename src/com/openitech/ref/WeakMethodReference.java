package com.openitech.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Weak reference objects, which do not prevent their referents from being
 * made finalizable, finalized, and then reclaimed.  Weak references are most
 * often used to implement canonicalizing mappings.
 *
 * <p> Suppose that the garbage collector determines at a certain point in time
 * that an object is <a href="package-summary.html#reachability">weakly
 * reachable</a>.  At that time it will atomically clear all weak references to
 * that object and all weak references to any other weakly-reachable objects
 * from which that object is reachable through a chain of strong and soft
 * references.  At the same time it will declare all of the formerly
 * weakly-reachable objects to be finalizable.  At the same time or at some
 * later time it will enqueue those newly-cleared weak references that are
 * registered with reference queues.

 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Odprte Informacijske Tehnologije Uro� Trojar s.p.</p>
 * @author Uro� Trojar
 * @version $Revision: 1.1.1.1 $
 */

public class WeakMethodReference<T>  extends WeakObjectReference<T> {
  Method method;
  protected boolean proxy;
  private boolean enabled = true;
  
  public WeakMethodReference(T referent) {
    super(referent);
    method  = null;
  }

  public WeakMethodReference(T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
    method  = null;
  }

  public WeakMethodReference(Class clazz, T referent, String methodName, Class[] methodParameters) throws SecurityException,
      NoSuchMethodException {
    super(referent);
    proxy = true;
    init(clazz, methodName, methodParameters);
  }

  public WeakMethodReference(Class clazz, T referent, ReferenceQueue<? super T> q, String methodName, Class[] methodParameters) throws SecurityException,
      NoSuchMethodException {
    super(referent,q);
    proxy = true;
    init(clazz, methodName, methodParameters);
  }

  protected void init(Class clazz, String methodName, Class[] methodParameters)  throws SecurityException,
      NoSuchMethodException {
    method = clazz.getMethod(methodName, methodParameters);
  }

  public Object execute(Object parameter) throws InvocationTargetException,
      IllegalArgumentException, IllegalAccessException {
    return execute(new Object[] { parameter });
  }

  public Object execute(Object[] parameters) throws InvocationTargetException,
      IllegalArgumentException, IllegalAccessException {
    Object referent = this.get();
    if (referent!=null && method!=null && isEnabled()) {
      return method.invoke(referent, parameters);
    } else
      return null;
  }

  public Object invoke(Object parameter) {
    try {
      return execute(new Object[] {parameter});
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
      return null;
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex);
    }
  }

  public Object invoke(Object[] parameters) {
    try {
      return execute(parameters);
    }
    catch (IllegalAccessException ex) {
      ex.printStackTrace();
      return null;
    }
    catch (InvocationTargetException ex) {
      throw (RuntimeException) new RuntimeException().initCause(ex);
    }
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support the <code>Cloneable</code> interface.
   *   Subclasses that override the <code>clone</code> method can also throw this exception to indicate that an
   *   instance cannot be cloned.
   * @param q ReferenceQueue
   */
  public Object clone(ReferenceQueue<? super T> q) throws CloneNotSupportedException {
    if (isValid()) {
      WeakMethodReference wmr = new WeakMethodReference<T>(this.get(), q);
      wmr.method = this.method;
      return wmr;
    } else
      throw new CloneNotSupportedException("Referent object is null.");
  }

  public boolean isProxy() {
    return proxy;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
