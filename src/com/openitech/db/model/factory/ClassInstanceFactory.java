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
package com.openitech.db.model.factory;

import com.openitech.db.model.xml.config.Factory;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author uros
 */
public class ClassInstanceFactory<T> {

  public ClassInstanceFactory(Factory factory, Class... constructorClass) {
    this("", factory, constructorClass);
  }

  public ClassInstanceFactory(String name, Factory factory, Class[] constructorClass) {
    this.factory = factory;
    this.constructorClass = constructorClass;
    this.name = name;
  }

  public static ClassInstanceFactory getInstance(Factory factory, Class... constructorClass) {
    return new ClassInstanceFactory(factory, constructorClass);
  }

  public static ClassInstanceFactory getInstance(String name, Factory factory, Class... constructorClass) {
    return new ClassInstanceFactory(name, factory, constructorClass);
  }

  public static ClassInstanceFactory getInstance(String name, String groovy, String className, Class... constructorClass) {
    Factory factory = new Factory();
    factory.setGroovy(groovy);
    factory.setClassName(className);
    return new ClassInstanceFactory(name, factory, constructorClass);
  }

  public static ClassInstanceFactory getInstance(String className) {
    Factory factory = new Factory();
    factory.setClassName(className);
    return new ClassInstanceFactory(null, factory, new Class[]{});
  }

  protected Factory factory;

  /**
   * Get the value of factory
   *
   * @return the value of factory
   */
  public Factory getFactory() {
    return factory;
  }

  /**
   * Set the value of factory
   *
   * @param factory new value of factory
   */
  public void setFactory(Factory factory) {
    this.factory = factory;
  }
  protected Class[] constructorClass;

  /**
   * Get the value of constructorClass
   *
   * @return the value of constructorClass
   */
  public Class[] getConstructorClass() {
    return constructorClass;
  }

  /**
   * Set the value of constructorClass
   *
   * @param constructorClass new value of constructorClass
   */
  public void setConstructorClass(Class... constructorClass) {
    this.constructorClass = constructorClass;
  }
  protected String name;

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

  public T newInstance(Object... parameters) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
    Object newInstance = null;
    Class clazz = null;
    if (getFactory().getGroovy() != null) {
      GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
      clazz = gcl.parseClass(getFactory().getGroovy(), (getName() == null ? "classInstance_" + System.currentTimeMillis() : getName()));
    } else if (getFactory().getClassName() != null) {
      clazz = ClassInstanceFactory.class.forName(getFactory().getClassName());
    }

    Constructor constructor;
    if (Custom.class.isAssignableFrom(clazz)) {
      if (clazz.getConstructors().length == 1) {
        constructor = clazz.getConstructors()[0];
      } else {
        Method method = clazz.getMethod("getDefaultConstructor", (Class[]) null);
        constructor = (Constructor) method.invoke(null, (Object[]) null);
      }
    } else {
      constructor = clazz.getConstructor(constructorClass);
    }

    newInstance = constructor.newInstance(parameters);

    return (T) newInstance;
  }

  public static interface Custom {
  }
}
