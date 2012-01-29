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
package com.openitech.sql.properties;

import com.openitech.spring.beans.factory.config.AbstractPropertyRetriever;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.spring.beans.factory.config.PropertyType;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public final class SqlPropertyRetriever extends AbstractPropertyRetriever {

  private static final Map<String, Class<? extends AbstractPropertyRetriever>> implementations = new HashMap<String, Class<? extends AbstractPropertyRetriever>>();
  private static AbstractPropertyRetriever instance;

  public SqlPropertyRetriever() {
    instance = getInstance();
  }

  private static void register() {
    if (implementations.isEmpty()) {
      register("mssql", com.openitech.sql.properties.mssql.SqlPropertyRetrieverImpl.class);
    }
  }

  public static void register(String dialect, Class<? extends AbstractPropertyRetriever> implementation) {
    if (!implementation.equals(SqlPropertyRetriever.class)) {
      implementations.put(dialect, implementation);
    }
  }

  public static AbstractPropertyRetriever getInstance() {
    if (instance == null) {
      register();
      Class clazz = implementations.get(ConnectionManager.getInstance().getDialect());
      if (clazz != null) {
        try {
          instance = (AbstractPropertyRetriever) clazz.newInstance();
        } catch (InstantiationException ex) {
          Logger.getLogger(SqlPropertyRetriever.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
          Logger.getLogger(SqlPropertyRetriever.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return instance;
  }

  @Override
  public Object getRemoteValue(PropertyType type, String properyName, String charsetName) {
    return instance.getRemoteValue(type, properyName, charsetName);
  }
}
