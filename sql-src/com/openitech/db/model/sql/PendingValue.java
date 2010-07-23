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
