package com.openitech.ref;

import com.openitech.Settings;
import java.awt.EventQueue;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.lang.ref.Reference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @version $Revision: 1.2 $
 */
public final class WeakListenerList
    implements List<Object> {
  private List<Object> listeners;
  private ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
  public WeakListenerList() {
    this(3);
  }

  public WeakListenerList(int capacity) {
    this(capacity, true);
  }

  public WeakListenerList(WeakListenerList c) {
    listeners = new ArrayList<Object>(c.size());
    addAll(c);
    CleanUpThread.getInstance().register(this);
  }

  public WeakListenerList(boolean register) {
    this(3, true);
  }

  public WeakListenerList(int capacity, boolean register) {
    listeners = new ArrayList<Object>(capacity);
    if (register)
      CleanUpThread.getInstance().register(this);
  }

  public WeakListenerList(WeakListenerList c, boolean register) {
    listeners = new ArrayList<Object>(c.size());
    addAll(c);
    if (register)
      CleanUpThread.getInstance().register(this);
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in this list.
   */
  public int size() {
    this.cleanUp();
    return listeners.size();
  }

  /**
   * Returns <tt>true</tt> if this list contains no elements.
   *
   * @return <tt>true</tt> if this list contains no elements.
   */
  public boolean isEmpty() {
    this.cleanUp();
    return listeners.isEmpty();
  }

  /**
   * Returns <tt>true</tt> if this list contains the specified element.
   *
   * @param o element whose presence in this list is to be tested.
   * @return <tt>true</tt> if this list contains the specified element.
   */
  public boolean contains(Object o) {
    this.cleanUp();
    return listeners.contains(o);
  }

  public List<Object> elementsList() {
    this.cleanUp();
    List<Object> v = new ArrayList<Object>(listeners.size());
    Object element;
    for (Iterator i=listeners.iterator(); i.hasNext(); ) {
      element = i.next();
      if ((element instanceof WeakMethodReference) && element!=null && ((WeakMethodReference) element).isProxy()) {
        element = ((WeakMethodReference) element).get()==null?null:element;
      } else if ((element instanceof Reference) && element!=null) {
        element = ((Reference) element).get();
      }
      if (element!=null)
        v.add(element);
    }
    return v;
  }

  /**
   * Returns an iterator over the elements in this list in proper sequence.
   *
   * @return an iterator over the elements in this list in proper sequence.
   */
  public Iterator<Object> iterator() {
    return elementsList().iterator();
  }

  /**
   * Returns an array containing all of the elements in this list in proper sequence.
   *
   * @return an array containing all of the elements in this list in proper sequence.
   */
  public Object[] toArray() {
    return elementsList().toArray();
  }

  /**
   * Returns an array containing all of the elements in this list in proper sequence; the runtime type of the returned
   * array is that of the specified array.
   *
   * @param a the array into which the elements of this list are to be stored, if it is big enough; otherwise, a new
   *   array of the same runtime type is allocated for this purpose.
   * @return an array containing the elements of this list.
   */
  public <Object> Object[] toArray(Object[] a) {
    return elementsList().toArray(a);
  }



  private WeakObjectReference getWeakObjectReference(Object referent) {
    if (referent instanceof WeakObjectReference) {
      WeakObjectReference wor = (WeakObjectReference) referent;
      if (wor!=null && wor.get()!=null) {
        try {
          return (WeakObjectReference) wor.clone(queue);
        }
        catch (CloneNotSupportedException ex) {
          System.err.println(ex.getMessage());
        }
      }
    } else if (referent instanceof WeakReference) {
      WeakReference wr = (WeakReference) referent;
      if (wr!=null && wr.get()!=null) {
        return new WeakObjectReference(wr.get(), queue);
      }
    } else {
      return new WeakObjectReference(referent, queue);
    }
    return null;
  }

  /**
   * Appends the specified element to the end of this list (optional operation).
   *
   * @param o element to be appended to this list.
   * @return <tt>true</tt> (as per the general contract of the <tt>Collection.add</tt> method).
   */
  public boolean add(Object o) {
    int csize = this.size();
    this.add(csize, o);
    return this.size()==csize;
  }

  /**
   * Removes the first occurrence in this list of the specified element (optional operation).
   *
   * @param o element to be removed from this list, if present.
   * @return <tt>true</tt> if this list contained the specified element.
   */
  public boolean remove(Object o) {
    return listeners.remove(o);
  }

  /**
   * Returns <tt>true</tt> if this list contains all of the elements of the specified collection.
   *
   * @param c collection to be checked for containment in this list.
   * @return <tt>true</tt> if this list contains all of the elements of the specified collection.
   */
  public boolean containsAll(Collection c) {
    cleanUp();
    return listeners.containsAll(c);
  }

  /**
   * Appends all of the elements in the specified collection to the end of this list, in the order that they are
   * returned by the specified collection's iterator (optional operation).
   *
   * @param c collection whose elements are to be added to this list.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  public boolean addAll(Collection c) {
    return addAll(this.size(), c);
  }

  /**
   * Inserts all of the elements in the specified collection into this list at the specified position (optional
   * operation).
   *
   * @param index index at which to insert first element from the specified collection.
   * @param c elements to be inserted into this list.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  public boolean addAll(int index, Collection c) {
    int csize = this.size();
    List cl = new ArrayList(c);
    for (Iterator i=cl.iterator(); i.hasNext(); index++)
      this.add(index, i.next());

    return this.size()==csize;
  }

  /**
   * Removes from this list all the elements that are contained in the specified collection (optional operation).
   *
   * @param c collection that defines which elements will be removed from this list.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  public boolean removeAll(Collection c) {
    this.cleanUp();
    return listeners.removeAll(c);
  }

  /**
   * Retains only the elements in this list that are contained in the specified collection (optional operation).
   *
   * @param c collection that defines which elements this set will retain.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  public boolean retainAll(Collection c) {
    this.cleanUp();
    return listeners.retainAll(c);
  }

  /**
   * Removes all of the elements from this list (optional operation).
   */
  public void clear() {
    listeners.clear();
  }

  /**
   * Compares the specified object with this list for equality.
   *
   * @param o the object to be compared for equality with this list.
   * @return <tt>true</tt> if the specified object is equal to this list.
   */
  public boolean equals(Object o) {
    return super.equals(o);
  }

  /**
   * Returns the hash code value for this list.
   *
   * @return the hash code value for this list.
   */
  public int hashCode() {
    return listeners.hashCode();
  }

  /**
   * Returns the element at the specified position in this list.
   *
   * @param index index of element to return.
   * @return the element at the specified position in this list.
   */
  public Object get(int index) {
    Object result = listeners.get(index);
    if ((result instanceof Reference) && result!=null) {
      result = ((Reference) result).get();
    }
    return result;
  }

  /**
   * Replaces the element at the specified position in this list with the specified element (optional operation).
   *
   * @param index index of element to replace.
   * @param element element to be stored at the specified position.
   * @return the element previously at the specified position.
   */
  public Object set(int index, Object element) {
    Object result = remove(index);
    this.add(index, element);
    return result;
  }

  /**
   * Inserts the specified element at the specified position in this list (optional operation).
   *
   * @param index index at which the specified element is to be inserted.
   * @param element element to be inserted.
   */
  public void add(int index, Object element) {
    if (element!=null && !listeners.contains(element)) {
      listeners.add(index, element);
    }
  }

  /**
   * Inserts the specified element at the specified position in this list (optional operation).
   *
   * @param index index at which the specified element is to be inserted.
   * @param element element to be inserted.
   */
  public void addWeak(int index, Object element) {
    WeakObjectReference wor = getWeakObjectReference(element);
    if (wor!=null && !listeners.contains(wor)) {
      listeners.add(index, wor);
    }
  }

  /**
   * Removes the element at the specified position in this list (optional operation).
   *
   * @param index the index of the element to removed.
   * @return the element previously at the specified position.
   */
  public Object remove(int index) {
    return listeners.remove(index);
  }

  /**
   * Returns the index in this list of the first occurrence of the specified element, or -1 if this list does not
   * contain this element.
   *
   * @param o element to search for.
   * @return the index in this list of the first occurrence of the specified element, or -1 if this list does not
   *   contain this element.
   */
  public int indexOf(Object o) {
    cleanUp();
    return listeners.indexOf(o);
  }

  /**
   * Returns the index in this list of the last occurrence of the specified element, or -1 if this list does not
   * contain this element.
   *
   * @param o element to search for.
   * @return the index in this list of the last occurrence of the specified element, or -1 if this list does not
   *   contain this element.
   */
  public int lastIndexOf(Object o) {
    cleanUp();
    return listeners.lastIndexOf(o);
  }

  /**
   * Returns a list iterator of the elements in this list (in proper sequence).
   *
   * @return a list iterator of the elements in this list (in proper sequence).
   */
  public ListIterator<Object> listIterator() {
    return elementsList().listIterator();
  }

  /**
   * Returns a list iterator of the elements in this list (in proper sequence), starting at the specified position in
   * this list.
   *
   * @param index index of first element to be returned from the list iterator (by a call to the <tt>next</tt> method).
   * @return a list iterator of the elements in this list (in proper sequence), starting at the specified position in
   *   this list.
   */
  public ListIterator<Object> listIterator(int index) {
    return elementsList().listIterator(index);
  }

  /**
   * Returns a view of the portion of this list between the specified <tt>fromIndex</tt>, inclusive, and
   * <tt>toIndex</tt>, exclusive.
   *
   * @param fromIndex low endpoint (inclusive) of the subList.
   * @param toIndex high endpoint (exclusive) of the subList.
   * @return a view of the specified range within this list.
   */
  public List<Object> subList(int fromIndex, int toIndex) {
    return elementsList().subList(fromIndex, toIndex);
  }

  private void cleanUp() {
    int count = 0;
    WeakReference wr = (WeakReference) queue.poll();
    while (wr!=null) {
      this.remove(wr);
      count ++;
      wr = (WeakReference) queue.poll();
    }
    if (count>0) {
      Logger.getLogger(Settings.LOGGER).info(getClass().getName()+" "+Integer.toString(count)+" weak references removed");
    }
  }


  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support the <code>Cloneable</code> interface.
   *   Subclasses that override the <code>clone</code> method can also throw this exception to indicate that an
   *   instance cannot be cloned.
   */
  public Object clone() {
    return new WeakListenerList(this);
  }

  /**
   * Adds the specified component to the end of this vector,
   * increasing its size by one. The capacity of this vector is
   * increased if its size becomes greater than its capacity. <p>
   *
   * This method is identical in functionality to the add(Object) method
   * (which is part of the List interface).
   *
   * @param   obj   the component to be added.
   * @see	   #add(Object)
   * @see	   List
   */
  public void addElement(Object obj) {
    this.add(obj);
  }

  /**
   * Removes the first (lowest-indexed) occurrence of the argument
   * from this vector. If the object is found in this vector, each
   * component in the vector with an index greater or equal to the
   * object's index is shifted downward to have an index one smaller
   * than the value it had previously.<p>
   *
   * This method is identical in functionality to the remove(Object)
   * method (which is part of the List interface).
   *
   * @param   obj   the component to be removed.
   * @return  <code>true</code> if the argument was a component of this
   *          vector; <code>false</code> otherwise.
   * @see	List#remove(Object)
   * @see	List
   */
  public synchronized boolean removeElement(Object obj) {
    return this.remove(obj);
  }

  /**
   * Returns the component at the specified index.<p>
   *
   * This method is identical in functionality to the get method
   * (which is part of the List interface).
   *
   * @param      index   an index into this vector.
   * @return     the component at the specified index.
   * @exception  ArrayIndexOutOfBoundsException  if the <tt>index</tt>
   *             is negative or not less than the current size of this
   *             <tt>Vector</tt> object.
   *             given.
   * @see	   #get(int)
   * @see	   List
   */
  public Object elementAt(int index) {
    return this.get(index);
  }
  /**
   * Returns the last component of the vector.
   *
   * @return  the last component of the vector, i.e., the component at index
   *          <code>size()&nbsp;-&nbsp;1</code>.
   * @exception  NoSuchElementException  if this vector is empty.
   */
  public Object lastElement() {
      if (this.size()==0) {
          throw new NoSuchElementException();
      }
      return this.get(this.size() - 1);
  }
  /**
   * Sets the component at the specified <code>index</code> of this
   * vector to be the specified object. The previous component at that
   * position is discarded.<p>
   *
   * The index must be a value greater than or equal to <code>0</code>
   * and less than the current size of the vector. <p>
   *
   * This method is identical in functionality to the set method
   * (which is part of the List interface). Note that the set method reverses
   * the order of the parameters, to more closely match array usage.  Note
   * also that the set method returns the old value that was stored at the
   * specified position.
   *
   * @param      obj     what the component is to be set to.
   * @param      index   the specified index.
   * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
   * @see        #size()
   * @see        List
   * @see	   #set(int, java.lang.Object)
   */
  public void setElementAt(Object obj, int index) {
    this.set(index, obj);
  }

  /**
   * Called by the garbage collector on an object when garbage collection determines that there are no more references
   * to the object.
   *
   * @throws Throwable the <code>Exception</code> raised by this method
   */
  protected void finalize() throws Throwable {
    super.finalize();
    CleanUpThread.getInstance().unregister(this);
  }

  private static class WeakListenerListReference extends WeakObjectReference<WeakListenerList> {
    public WeakListenerListReference(WeakListenerList referent) {
      super(referent, CleanUpThread.getInstance().weakListenerQueue);
    }
  }

  public static class CleanUpThread extends Thread {
    private static CleanUpThread instance = null;
    private ReferenceQueue<WeakListenerList> weakListenerQueue = new ReferenceQueue<WeakListenerList>();
    private List<WeakListenerListReference> weakListenerList = new ArrayList<WeakListenerListReference>();
    private ReentrantLock lock = new ReentrantLock();


    private CleanUpThread() {
      setName("WeakListenerList-CleanUpThread");
      setDaemon(true);
    }
    public static CleanUpThread getInstance() {
      if (instance==null) {
        instance = new CleanUpThread();
        instance.start();
      }
      return instance;
    }


    public synchronized void register(WeakListenerList instance) {
      lock.lock();
      try {
        WeakListenerListReference wr = new WeakListenerListReference(instance);
        if (!weakListenerList.contains(wr))
          weakListenerList.add(wr);
      } finally {
        lock.unlock();
      }
    }

    public synchronized void unregister(WeakListenerList instance) {
      lock.lock();
      try {
        weakListenerList.remove(instance);
      } finally {
        lock.unlock();
      }
    }

    private void cleanUp() {
      lock.lock();
      try {
        int count = 0;
        WeakReference wr = (WeakReference) weakListenerQueue.poll();
        while (wr!=null) {
          this.weakListenerList.remove(wr);
          count ++;
          wr = (WeakReference) weakListenerQueue.poll();
        }
        if (count>0) {
          Logger.getLogger(Settings.LOGGER).info(getClass().getName()+" "+Integer.toString(count)+" weak listener references removed");
        }
      } finally {
        lock.unlock();
      }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     */
    public void run() {
      try {
        while (true) {
          sleep(9000);
          EventQueue.invokeLater(new Runnable() {
            public void run() {
              lock.lock();
              try {
                cleanUp();
              for (WeakListenerListReference wr : weakListenerList) {
                if (wr!=null && wr.get()!=null) {
                  ((WeakListenerList) wr.get()).cleanUp();
                  }
                }
              } finally {
                lock.unlock();
              }
            }
          });
        }
      } catch (InterruptedException ex) {
        weakListenerList.clear();
        instance = null;
      }
    }
  }
}
