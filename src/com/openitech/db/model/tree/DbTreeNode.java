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


package com.openitech.db.model.tree;

import com.openitech.text.CaseInsensitiveString;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

public class DbTreeNode extends DefaultMutableTreeNode implements java.util.Map<CharSequence, Object> {

  protected DbTreeNodeType type;
  protected java.util.List<Object> key;
  private java.util.Map<CharSequence, Object> values = new java.util.HashMap<CharSequence, Object>();

  public DbTreeNode(DbTreeNodeType type, List<Object> key) {
    super(type==null?"NULL":type.getName(), true);
    this.type = type;
    this.key = key == null ? new java.util.ArrayList<Object>(): key;
  }

  /**
   * Get the value of key
   *
   * @return the value of key
   */
  public java.util.List<Object> getKey() {
    return key;
  }

  /**
   * Get the value of type
   *
   * @return the value of type
   */
  public DbTreeNodeType getType() {
    return type;
  }

  public Object getValue(CharSequence key) {
    return values.get(CaseInsensitiveString.valueOf(key));
  }

  public void putValue(CharSequence key, Object value) {
    values.put(CaseInsensitiveString.valueOf(key), value);
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
  public boolean containsKey(Object key) {
    return values.containsKey(CaseInsensitiveString.valueOf(key));
  }

  @Override
  public boolean containsValue(Object value) {
    return values.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return values.get(CaseInsensitiveString.valueOf(key));
  }

  @Override
  public Object put(CharSequence key, Object value) {
    return values.put(CaseInsensitiveString.valueOf(key), value);
  }

  @Override
  public Object remove(Object key) {
    return values.remove(CaseInsensitiveString.valueOf(key));
  }

  @Override
  public void putAll(Map<? extends CharSequence, ? extends Object> m) {
    for (java.util.Map.Entry<? extends CharSequence, ? extends Object> entry:m.entrySet()) {
      values.put(CaseInsensitiveString.valueOf(entry.getKey()), entry.getValue());
    }
  }

  @Override
  public void clear() {
    values.clear();
  }

  @Override
  public Set<CharSequence> keySet() {
    return values.keySet();
  }

  @Override
  public Collection<Object> values() {
    return values.values();
  }

  @Override
  public Set<Entry<CharSequence, Object>> entrySet() {
    return values.entrySet();
  }

}
