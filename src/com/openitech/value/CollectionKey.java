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
      this.values.addAll(values);
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
