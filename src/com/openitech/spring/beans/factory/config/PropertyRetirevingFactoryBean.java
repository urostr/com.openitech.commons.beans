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
    StringBuffer message = new StringBuffer();
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
