/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbFieldObserver;

/**
 *
 * @author uros
 */
public class FieldValueProxy extends FieldValue implements ActiveRowChangeListener {

  private ActiveRowChangeWeakListener listener = new ActiveRowChangeWeakListener(this);

  public FieldValueProxy(String name, int type) {
    super(name, type);
  }

  public FieldValueProxy(String name, int type, DbFieldObserver fieldObserver) {
    super(name, type);
    setFieldObserver(fieldObserver);
  }

  public FieldValueProxy(String name, int type, int fieldValueIndex, DbFieldObserver fieldObserver) {
    super(name, type, fieldValueIndex);
    setFieldObserver(fieldObserver);
  }
  private DbFieldObserver fieldObserver;

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
    super.setValue(fieldObserver.getValue());
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
