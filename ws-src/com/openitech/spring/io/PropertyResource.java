/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring.io;

import com.openitech.spring.beans.factory.config.PropertyType;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;

/**
 *
 * @author uros
 */
public class PropertyResource extends AbstractResource {

  protected PropertyType propertyType;
  protected String propertyName;
  protected Object value;

  public PropertyResource(PropertyType propertyType, String propertyName, Object value) {
    this.propertyType = propertyType;
    this.propertyName = propertyName;
    this.value = value;
  }

  /**
   * Get the value of value
   *
   * @return the value of value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Get the value of propertyName
   *
   * @return the value of propertyName
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Get the value of propertyType
   *
   * @return the value of propertyType
   */
  public PropertyType getPropertyType() {
    return propertyType;
  }

  @Override
  public String getDescription() {
    return "remote:"+propertyType.toString()+":"+propertyName;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    if (propertyType.getTypeClass().equals(String.class)) {
      return new ByteArrayInputStream(getValue().toString().getBytes());
    } else {
      throw new UnsupportedOperationException("Not supported for "+propertyType.getTypeClass().getSimpleName());
    }
  }

}
