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
