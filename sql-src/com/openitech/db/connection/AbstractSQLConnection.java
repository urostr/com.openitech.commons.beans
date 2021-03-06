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
 * AbstractSQLConnection.java
 *
 * Created on Sobota, 6 maj 2006, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.connection;

import com.openitech.ref.WeakListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author uros
 */
public abstract class AbstractSQLConnection implements DbConnection {

  private transient WeakListenerList actionListeners;
  private final DbConnection implementation;
  protected final Properties settings = new Properties();
  private final static String SYSTEM_PREFIX = "system.";

  public AbstractSQLConnection() {
    settings.putAll(loadProperites("connection.properties"));
    settings.putAll(System.getProperties());

    for (Entry<Object, Object> entry : settings.entrySet()) {
      String key = entry.getKey().toString();

      if (key.startsWith(SYSTEM_PREFIX)) {
        System.getProperties().put(key.substring(SYSTEM_PREFIX.length()), entry.getValue());
      }
    }

    if (Boolean.parseBoolean(settings.getProperty(DB_USE_RECONNECT, "false"))) {
      implementation = new ReconnectableSQLConnection(this);
    } else {
      implementation = new SingleSQLConnection(this);
    }
  }

  /**
   * Creates a new instance of ConnectionManager
   */
  @Override
  public void setServerConnect(boolean connecttoserver) {
  }

  protected abstract Properties loadProperites(String propertiesName);

  @Override
  public boolean isPooled() {
    return implementation.isPooled();
  }

  @Override
  public boolean isConnectOnDemand() {
    return implementation.isConnectOnDemand();
  }

  @Override
  public boolean isCacheRowSet() {
    return implementation.isCacheRowSet();
  }

  @Override
  public String getUrl() {
    return implementation.getUrl();
  }

  @Override
  public String getDialect() {
    return implementation.getDialect();
  }

  @Override
  public boolean isCaseInsensitive() {
    return implementation.isCaseInsensitive();
  }

  @Override
  public boolean isConvertToVarchar() {
    return implementation.isConvertToVarchar();
  }

  @Override
  public Connection getTemporaryConnection()  throws SQLException{
    return implementation.getTemporaryConnection();
  }

  @Override
  public Connection getTxConnection() {
    return implementation.getTxConnection();
  }

  @Override
  public java.sql.Connection getConnection() {
    return implementation.getConnection();
  }

  protected abstract void createSchema(Connection conn) throws SQLException;

  @Override
  public String getProperty(String key) {
    return settings.getProperty(key);
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    return settings.getProperty(key, defaultValue);
  }

  @Override
  public Object setProperty(String key, String value) {
    return settings.setProperty(key, value);
  }

  @Override
  public boolean containsKey(String key) {
    return settings.containsKey(key);
  }

  @Override
  public synchronized void removeActionListener(ActionListener l) {
    if (actionListeners != null && actionListeners.contains(l)) {
      actionListeners.removeElement(l);
    }
  }

  @Override
  public synchronized void addActionListener(ActionListener l) {
    WeakListenerList v = actionListeners == null ? new WeakListenerList(2) : actionListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      actionListeners = v;
    }
  }

  public void fireActionPerformed(ActionEvent e) {
    if (actionListeners != null) {
      java.util.List listeners = actionListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActionListener) listeners.get(i)).actionPerformed(e);
      }
    }
  }
}
