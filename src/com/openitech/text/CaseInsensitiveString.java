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
package com.openitech.text;

/**
 *
 * @author uros
 */
public class CaseInsensitiveString implements CharSequence, Comparable<CharSequence> {

  final String value;

  public CaseInsensitiveString(CharSequence value) {
    this(value == null ? "" : value.toString());
  }

  public CaseInsensitiveString(String value) {
    this.value = value == null ? "" : value;
  }

  @Override
  public int length() {
    return value.length();
  }

  @Override
  public char charAt(int index) {
    return value.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return value.subSequence(start, end);
  }

  @Override
  public int compareTo(CharSequence o) {
    return value.compareToIgnoreCase(o == null ? "" : o.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CaseInsensitiveString other = (CaseInsensitiveString) obj;
    if ((this.value == null) ? (other.value != null) : !this.value.equalsIgnoreCase(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + (this.value != null ? this.value.toUpperCase().hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public static CaseInsensitiveString valueOf(Object value) {
    return new CaseInsensitiveString(value==null?"":value.toString());
  }
}
