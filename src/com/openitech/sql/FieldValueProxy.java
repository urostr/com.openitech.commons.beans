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
    this.fieldObserver = fieldObserver;
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
    if (this.fieldObserver!=null) {
      this.fieldObserver.removeActiveRowChangeListener(listener);
    }
    this.fieldObserver = fieldObserver;
    if (this.fieldObserver!=null) {
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
    Object value;

    switch (ValueType.getType(getType())) {
      case IntValue: value = fieldObserver.getValueAsInt(); break;
      case DateValue: value = fieldObserver.getValueAsDate(); break;
      case StringValue:
      case ClobValue: value = fieldObserver.getValueAsText(); break;
      case RealValue: value = fieldObserver.getValueAsDouble(); break;
      case BitValue: value = fieldObserver.getValueAsBoolean(); break;
      default:
        value = fieldObserver.getValue(); break;
    }

    super.setValue(value);
  }
}
