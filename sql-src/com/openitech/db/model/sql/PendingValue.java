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

package com.openitech.db.model.sql;

/**
 *
 * @author uros
 */
public class PendingValue {

  public PendingValue(String fieldName, Object value) {
    this.fieldName = fieldName;
    this.value = value;
  }

  private String fieldName;

  /**
   * Get the value of fieldName
   *
   * @return the value of fieldName
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Set the value of fieldName
   *
   * @param fieldName new value of fieldName
   */
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
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
}
