/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.value;

import com.openitech.value.fields.ValueType;

/**
 *
 * @author domenbasic
 */
public class VariousValue {

  private long id;

  public VariousValue(Long valueId, int type, Object value) {
    this.id = valueId;
    this.type = ValueType.getType(type);
    this.value = value;
  }

  /**
   * Get the value of id
   *
   * @return the value of id
   */
  public long getId() {
    return id;
  }

  /**
   * Set the value of id
   *
   * @param id new value of id
   */
  public void setId(long id) {
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

}
