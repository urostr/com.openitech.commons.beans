/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.properties;

import com.openitech.spring.beans.factory.config.AbstractPropertyRetriever;
import com.openitech.db.ConnectionManager;
import com.openitech.spring.beans.factory.config.PropertyRetriever;
import com.openitech.spring.beans.factory.config.PropertyType;
import com.openitech.util.ReadInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
