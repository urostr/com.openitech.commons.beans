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
package com.openitech.value.fields;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbFieldObserver;

/**
 *
 * @author uros
 */
public class FieldValueProxy extends FieldValue implements ActiveRowChangeListener {

  private DbFieldObserver fieldObserver;
  private ActiveRowChangeWeakListener listener = new ActiveRowChangeWeakListener(this);

  public FieldValueProxy(String name, int type) {
    this(new Field(name, type), null, null);
  }

  public FieldValueProxy(String name, int type, DbFieldObserver fieldObserver) {
    this(new Field(name, type), fieldObserver, null);
  }

  public FieldValueProxy(String name, int type, int fieldValueIndex, DbFieldObserver fieldObserver) {
    this(new Field(name, type, fieldValueIndex), fieldObserver, null);
  }

  public FieldValueProxy(String name, int type, DbFieldObserver fieldObserver, Object value) {
    this(new Field(name, type), fieldObserver, value);
  }

  public FieldValueProxy(Integer idPolja, String name, int type, int fieldValueIndex, DbFieldObserver fieldObserver) {
    this(new Field(idPolja, name, type, fieldValueIndex), fieldObserver, null);
  }

  public FieldValueProxy(Integer idPolja, String name, int type, int fieldValueIndex, DbFieldObserver fieldObserver, Object value) {
    this(new Field(idPolja, name, type, fieldValueIndex), fieldObserver, value);
  }

  public FieldValueProxy(Field field, DbFieldObserver fieldObserver) {
    this(field, fieldObserver, null);
  }

  public FieldValueProxy(Field field, DbFieldObserver fieldObserver, Object value) {
    super(field, value);
    setFieldObserver(fieldObserver);
  }
  
  

  /**
   * Get the value of fieldObserver
   *
   * @return the value of fieldObserver
   */
  public DbFieldObserver getFieldObserver() {
    return fieldObserver;
  }

  /**
   * Set the value of fieldObserver
   *
   * @param fieldObserver new value of fieldObserver
   */
  public void setFieldObserver(DbFieldObserver fieldObserver) {
    if (this.fieldObserver != null) {
      this.fieldObserver.removeActiveRowChangeListener(listener);
    }
    this.fieldObserver = fieldObserver;
    if (this.fieldObserver != null) {
      this.fieldObserver.addActiveRowChangeListener(listener);
      fieldValueChanged(null);
    }
  }

  @Override
  public void activeRowChanged(ActiveRowChangeEvent event) {
    //throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void fieldValueChanged(ActiveRowChangeEvent event) {
    Object value = fieldObserver.getValue();
    if (value != null && (value instanceof java.sql.Clob)) {
      value = fieldObserver.getValueAsText();
    }
    super.setValue(value);
  }
  private boolean identityField;

  /**
   * Get the value of indentity
   *
   * @return the value of indentity
   */
  public boolean isIndentityField() {
    return identityField;
  }

  /**
   * Set the value of indentity
   *
   * @param indentity new value of indentity
   */
  public void setIndentityField(boolean identityField) {
    this.identityField = identityField;
  }
}
