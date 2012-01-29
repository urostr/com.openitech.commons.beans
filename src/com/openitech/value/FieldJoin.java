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

import com.openitech.value.fields.Field;

/**
 *
 * @author domenbasic
 */
public class FieldJoin {

  private Field field;
  private Field otherField;
  private boolean required;
  private boolean ignoreIfEmpty;

  public FieldJoin(Field field, Field otherField) {
    this.field = field;
    this.otherField = otherField;
  }

  public FieldJoin(Field field, Field otherField, boolean required, boolean ignoreIfEmpty) {
    this.field = field;
    this.otherField = otherField;
    this.required = required;
    this.ignoreIfEmpty = ignoreIfEmpty;
  }

  public Field getField() {
    return field;
  }

  public Field getOtherField() {
    return otherField;
  }

  public boolean isIgnoreIfEmpty() {
    return ignoreIfEmpty;
  }

  public boolean isRequired() {
    return required;
  }

  public void setIgnoreIfEmpty(boolean ignoreIfEmpty) {
    this.ignoreIfEmpty = ignoreIfEmpty;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }
}
