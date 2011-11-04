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

  public FieldJoin(Field field, Field otherField) {
    this.field = field;
    this.otherField = otherField;
  }

  public Field getField() {
    return field;
  }

  public Field getOtherField() {
    return otherField;
  }
}
