/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.spring.beans.factory.config;

/**
 *
 * @author uros
 */
public interface PropertyRetriever {

  public Object getValue(PropertyType type, String properyName, String charsetName);
}
