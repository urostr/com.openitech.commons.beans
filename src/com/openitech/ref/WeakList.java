/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class WeakList<E> extends AbstractList<E> implements java.util.List<E> {

  private java.util.List<WeakObjectReference<E>> elements;
  private ReferenceQueue<E> queue = new ReferenceQueue<E>();

  /**
   * Constructs an empty list with the specified initial capacity.
   *
   * @param   initialCapacity   the initial capacity of the list
   * @exception IllegalArgumentException if the specified initial capacity
   *            is negative
   */
  public WeakList(int initialCapacity) {
    super();
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal Capacity: "
              + initialCapacity);
    }
    this.elements = new java.util.ArrayList<WeakObjectReference<E>>();
  }

  /**
   * Constructs an empty list with an initial capacity of ten.
   */
  public WeakList() {
    this(10);
  }

  /**
   * Constructs a list containing the elements of the specified
   * collection, in the order they are returned by the collection's
   * iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public WeakList(Collection<? extends E> c) {
    elements = new java.util.ArrayList<WeakObjectReference<E>>(c.size());

    for (E e : c) {
      elements.add(new WeakObjectReference<E>(e));
    }
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in this list.
   */
  @Override
  public int size() {
    this.cleanUp();
    return elements.size();
  }

  /**
   * Returns <tt>true</tt> if this list contains no elements.
   *
   * @return <tt>true</tt> if this list contains no elements.
   */
  @Override
  public boolean isEmpty() {
    this.cleanUp();
    return elements.isEmpty();
  }

  /**
   * Returns <tt>true</tt> if this list contains the specified element.
   *
   * @param o element whose presence in this list is to be tested.
   * @return <tt>true</tt> if this list contains the specified element.
   */
  @Override
  @SuppressWarnings("element-type-mismatch")
  public boolean contains(Object o) {
    this.cleanUp();
    return elements.contains(o);
  }

  public java.util.List<E> elementsList() {
    this.cleanUp();
    java.util.List<E> v = new java.util.ArrayList<E>(elements.size());

    for (WeakObjectReference<E> element : elements) {
      if (element.isValid()) {
        v.add(element.get());
      }
    }
    return v;
  }

  /**
   * Returns an iterator over the elements in this list in proper sequence.
   *
   * @return an iterator over the elements in this list in proper sequence.
   */
  @Override
  public Iterator<E> iterator() {
    return elementsList().iterator();
  }

  /**
   * Returns an array containing all of the elements in this list in proper sequence.
   *
   * @return an array containing all of the elements in this list in proper sequence.
   */
  @Override
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
  @Override
  public <T> T[] toArray(T[] a) {
    return elementsList().toArray(a);
  }

  private WeakObjectReference getWeakObjectReference(Object referent) {
    if (referent instanceof WeakObjectReference) {
      WeakObjectReference wor = (WeakObjectReference) referent;
      if (wor != null && wor.get() != null) {
        try {
          return (WeakObjectReference) wor.clone(queue);
        } catch (CloneNotSupportedException ex) {
          System.err.println(ex.getMessage());
        }
      }
    } else if (referent instanceof WeakReference) {
      WeakReference wr = (WeakReference) referent;
      if (wr != null && wr.get() != null) {
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
  @Override
  public boolean add(E o) {
    int csize = this.size();
    this.add(csize, o);
    return this.size() != csize;
  }

  /**
   * Removes the first occurrence in this list of the specified element (optional operation).
   *
   * @param o element to be removed from this list, if present.
   * @return <tt>true</tt> if this list contained the specified element.
   */
  @Override
  @SuppressWarnings("element-type-mismatch")
  public boolean remove(Object o) {
    return elements.remove(o);
  }

  /**
   * Returns <tt>true</tt> if this list contains all of the elements of the specified collection.
   *
   * @param c collection to be checked for containment in this list.
   * @return <tt>true</tt> if this list contains all of the elements of the specified collection.
   */
  @Override
  public boolean containsAll(Collection c) {
    cleanUp();
    return elements.containsAll(c);
  }

  /**
   * Appends all of the elements in the specified collection to the end of this list, in the order that they are
   * returned by the specified collection's iterator (optional operation).
   *
   * @param c collection whose elements are to be added to this list.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  @Override
  public boolean addAll(Collection<? extends E> c) {
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
  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    int csize = this.size();
    for (E e : c) {
      this.add(index++, e);
    }

    return this.size() != csize;
  }

  /**
   * Removes from this list all the elements that are contained in the specified collection (optional operation).
   *
   * @param c collection that defines which elements will be removed from this list.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  @Override
  public boolean removeAll(Collection c) {
    this.cleanUp();
    return elements.removeAll(c);
  }

  /**
   * Retains only the elements in this list that are contained in the specified collection (optional operation).
   *
   * @param c collection that defines which elements this set will retain.
   * @return <tt>true</tt> if this list changed as a result of the call.
   */
  @Override
  public boolean retainAll(Collection c) {
    this.cleanUp();
    return elements.retainAll(c);
  }

  /**
   * Removes all of the elements from this list (optional operation).
   */
  @Override
  public void clear() {
    elements.clear();
  }

  /**
   * Returns the element at the specified position in this list.
   *
   * @param index index of element to return.
   * @return the element at the specified position in this list.
   */
  @Override
  public E get(int index) {
    WeakObjectReference<E> result = elements.get(index);

    return result.get();
  }

  /**
   * Replaces the element at the specified position in this list with the specified element (optional operation).
   *
   * @param index index of element to replace.
   * @param element element to be stored at the specified position.
   * @return the element previously at the specified position.
   */
  @Override
  public E set(int index, E element) {
    E result = remove(index);
    this.add(index, element);
    return result;
  }

  /**
   * Inserts the specified element at the specified position in this list (optional operation).
   *
   * @param index index at which the specified element is to be inserted.
   * @param element element to be inserted.
   */
  @Override
  @SuppressWarnings("element-type-mismatch")
  public void add(int index, E element) {
    if (element != null) {
      elements.add(index, getWeakObjectReference(element));
    }
  }

  /**
   * Removes the element at the specified position in this list (optional operation).
   *
   * @param index the index of the element to removed.
   * @return the element previously at the specified position.
   */
  @Override
  public E remove(int index) {
    return elements.remove(index).get();
  }

  /**
   * Returns the index in this list of the first occurrence of the specified element, or -1 if this list does not
   * contain this element.
   *
   * @param o element to search for.
   * @return the index in this list of the first occurrence of the specified element, or -1 if this list does not
   *   contain this element.
   */
  @Override
  public int indexOf(Object o) {
    cleanUp();
    return elements.indexOf(o);
  }

  /**
   * Returns the index in this list of the last occurrence of the specified element, or -1 if this list does not
   * contain this element.
   *
   * @param o element to search for.
   * @return the index in this list of the last occurrence of the specified element, or -1 if this list does not
   *   contain this element.
   */
  @Override
  public int lastIndexOf(Object o) {
    cleanUp();
    return elements.lastIndexOf(o);
  }

  /**
   * Returns a list iterator of the elements in this list (in proper sequence).
   *
   * @return a list iterator of the elements in this list (in proper sequence).
   */
  @Override
  public java.util.ListIterator<E> listIterator() {
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
  @Override
  public java.util.ListIterator<E> listIterator(int index) {
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
  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return elementsList().subList(fromIndex, toIndex);
  }

  private void cleanUp() {
    int count = 0;
    WeakObjectReference<E> wr = (WeakObjectReference<E>) queue.poll();
    while (wr != null) {
      elements.remove(wr);
      count++;
      wr = (WeakObjectReference<E>) queue.poll();
    }
    if (count > 0) {
      Logger.getLogger(WeakList.class.getName()).info(getName() + " " + Integer.toString(count) + " weak references removed");
    }
  }
  
  protected String name = WeakList.class.getName();

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support the <code>Cloneable</code> interface.
   *   Subclasses that override the <code>clone</code> method can also throw this exception to indicate that an
   *   instance cannot be cloned.
   */
  @Override
  public Object clone() {
    return new WeakList<E>(this);
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
  public E lastElement() {
    if (this.size() == 0) {
      throw new java.util.NoSuchElementException();
    }
    return this.get(this.size() - 1);
  }
}
