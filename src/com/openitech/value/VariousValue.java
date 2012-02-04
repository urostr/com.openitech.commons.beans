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

import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.ValueType;
import java.sql.SQLException;

/**
 *
 * @author domenbasic
 */
public class VariousValue {

  private Long id;

  public VariousValue(Long valueId, int type, Object value) {
    this.id = valueId;
    this.type = ValueType.valueOf(type);
    this.value = value;
  }

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public Long getId() {
    return id;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(Long id) {
    this.id = id;
  }
  private ValueType type;

  /**
   * Get the value of type
   *
   * @return the value of type
   */
  public ValueType getType() {
    return type;
  }

  /**
   * Set the value of type
   *
   * @param type new value of type
   */
  public void setType(ValueType type) {
    this.type = type;
  }
  private Object value;

  /**
   * Get the value of value
   *
   * @return the value of value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Set the value of value
   *
   * @param value new value of value
   */
  public void setValue(Object value) {
    this.value = value;
  }

  public static VariousValue newVariousValue(ValueType type, Object value) throws SQLException {
    return new VariousValue(SqlUtilities.getInstance().storeValue(type, value), type.getTypeIndex(), value);
  }

  public static VariousValue newVariousValue(long valueId) throws SQLException {
    return SqlUtilities.getInstance().findValue(valueId);
  }
}
