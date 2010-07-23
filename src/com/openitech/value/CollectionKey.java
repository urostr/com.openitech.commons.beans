/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author uros
 */
public class CollectionKey<E> implements Collection<E> {

  Collection<E> values = new ArrayList<E>();
  int hash;

  public CollectionKey() {
  }

  public CollectionKey(int size) {
    values = new ArrayList<E>(size);
  }

  public CollectionKey(Collection<E> values) {
    if (values != null) {
      values.addAll(values);
    }

    calculateHash();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CollectionKey other = (CollectionKey) obj;
    if (this.values.size() == other.values.size()) {
      return this.values.containsAll(other.values);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return values.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return values.iterator();
  }

  @Override
  public Object[] toArray() {
    return values.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return values.toArray(a);
  }

  @Override
  public boolean add(E e) {
    boolean result = values.add(e);
    calculateHash();
    return result;
  }

  @Override
  public boolean remove(Object o) {
    boolean result = values.remove(o);
    calculateHash();
    return result;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return values.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean result = values.addAll(c);
    calculateHash();
    return result;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean result = values.removeAll(c);
    calculateHash();
    return result;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean result = values.retainAll(c);
    calculateHash();
    return result;
  }

  @Override
  public void clear() {
    values.clear();
    calculateHash();
  }

  private final void calculateHash() {
    int hash = 7;
    for (E value : this.values) {
      hash = 23 * hash + (value == null ? 0 : value.hashCode());
    }
    this.hash = hash;
  }
}
