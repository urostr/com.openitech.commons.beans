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
