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
    this.fieldObserver = fieldObserver;
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
