/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.value;

/**
 *
 * @author uros
 */
public class NamedValue<T> {

  public NamedValue(String name, T value) {
    this.name = name;
    this.value = value;
  }

  private String name;

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name) {
    this.name = name;
  }

  private T value;

  /**
   * Get the value of value
   *
   * @return the value of value
   */
  public T getValue() {
    return value;
  }

  /**
   * Set the value of value
   *
   * @param value new value of value
   */
  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final NamedValue<T> other = (NamedValue<T>) obj;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }
}
