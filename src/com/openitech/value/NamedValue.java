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

/**
 *
 * @author uros
 */
public class NamedValue<T> {

  public NamedValue(String name, T value) {
    this.name = name;
    this.value = value;
  }

  private String name;

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

  private T value;

  /**
   * Get the value of value
   *
   * @return the value of value
   */
  public T getValue() {
    return value;
  }

  /**
   * Set the value of value
   *
   * @param value new value of value
   */
  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final NamedValue<T> other = (NamedValue<T>) obj;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }
}
