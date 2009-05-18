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
public class SpringApplicationContextPath {
  protected static final String CONTEXT_KEY="com.openitech.spring.context";
  protected static final String[] CONTEXT_DEFINITION = new String[] {
    "classpath:**/applicationContext*.xml",
    "classpath*:com/openitech/db/spring/applicationContext-*.xml",
    "classpath*:**/spring-*.xml"
  };
  
  private static Class<? extends SpringApplicationContextPath> clazz = SpringApplicationContextPath.class;

  public SpringApplicationContextPath() {
  }

  public static void register(Class<? extends SpringApplicationContextPath> springContextPathClass) {
    clazz = springContextPathClass;
  }

  public static String[] getContextDefinition() throws InstantiationException, IllegalAccessException {
    return clazz.newInstance().getSpringApplicationContextPath();
  }

  protected String[] getSpringApplicationContextPath() {
    return CONTEXT_DEFINITION;
  }
    
}
