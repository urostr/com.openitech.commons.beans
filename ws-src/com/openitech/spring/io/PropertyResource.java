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
