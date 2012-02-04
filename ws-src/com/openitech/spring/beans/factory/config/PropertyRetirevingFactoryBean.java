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

package com.openitech.spring.beans.factory.config;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author uros
 */
public class PropertyRetirevingFactoryBean implements FactoryBean, BeanNameAware, InitializingBean {

  protected String beanName;
  protected PropertyRetriever remotePropertyRetriever;
  protected String propertyName;
  protected PropertyType propertyType = PropertyType.SQL;
  protected String charSet = null;

  /**
   * Set the value of charSet
   *
   * @param charSet new value of charSet
   */
  public void setCharSet(String charSet) {
    this.charSet = charSet;
  }

  /**
   * Get the value of propertyType
   *
   * @return the value of propertyType
   */
  public PropertyType getPropertyType() {
    return propertyType;
  }

  /**
   * Set the value of propertyType
   *
   * @param propertyType new value of propertyType
   */
  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
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
   * Set the value of propertyName
   *
   * @param propertyName new value of propertyName
   */
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  /**
   * Get the value of remotePropertyRetriever
   *
   * @return the value of remotePropertyRetriever
   */
  public PropertyRetriever getRemotePropertyRetriever() {
    return remotePropertyRetriever;
  }

  /**
   * Set the value of remotePropertyRetriever
   *
   * @param remotePropertyRetriever new value of remotePropertyRetriever
   */
  public void setRemotePropertyRetriever(PropertyRetriever remotePropertyRetriever) {
    this.remotePropertyRetriever = remotePropertyRetriever;
  }

  /**
   * Set the value of beanName
   *
   * @param beanName new value of beanName
   */
  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @Override
  public Object getObject() throws Exception {
    return remotePropertyRetriever.getValue(propertyType, propertyName, charSet);
  }

  @Override
  public Class getObjectType() {
    return propertyType.getTypeClass();
  }

  @Override
  public boolean isSingleton() {
    return false;
  }

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    StringBuilder message = new StringBuilder();
		if (this.remotePropertyRetriever == null) {
      message.append("remotePropertyRetriever is required");
    }
		if (this.propertyType == null) {
      message.append(", propertyType is required");
    }
		if (this.propertyName == null) {
      message.append(", propertyName is required");
		}

    if (message.length()>0) {
      throw new IllegalArgumentException(message.toString());
    }
  }

}
