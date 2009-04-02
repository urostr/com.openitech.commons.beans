/*
 * Context.java
 *
 * Created on Torek, 3 april 2007, 15:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.spring;

/**
 *
 * @author uros
 */
public interface SpringApplicationContextPath {
  public final String CONTEXT_KEY="com.openitech.spring.context";
  public final String[] CONTEXT_DEFINITION = new String[] {
    "classpath:**/applicationContext*.xml",
    "classpath*:com/openitech/db/spring/applicationContext-*.xml",
    "classpath*:**/spring-*.xml"
  };
    
}
