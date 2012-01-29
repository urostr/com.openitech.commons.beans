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
 * ConnectionManager.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.7 $
 */
package com.openitech.db.connection;

import com.openitech.sql.logger.SQLLogger;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author uros
 */
public class ConnectionManager implements DbConnection {

  private static ConnectionManager instance = null;
  private static Class managedConnectionClass;
  private DbConnection connection = null;

  /**
   * Creates a new instance of ConnectionManager
   */
  protected ConnectionManager() {
  }

  public static void registerManagedConnection(Class newManagedConnectionClass) {
    managedConnectionClass = newManagedConnectionClass;
  }

  public static ConnectionManager getInstance() {
    if (instance == null) {
      instance = new ConnectionManager();
      if (Boolean.parseBoolean(instance.getProperty(DbConnection.DB_LOG_ACTIONS, "false"))) {
        SQLLogger.init();
      }
    }
    return instance;
  }

  public void setConnection(DbConnection connection) {
    if (this.connection == null) {
      this.connection = connection;
    }
  }

  //TODO kaj pa èe je managedConnectionClass null in connection null?
  private DbConnection getDbConnection() {
    if ((connection == null) && (managedConnectionClass != null)) {
      try {
        connection = (DbConnection) managedConnectionClass.newInstance();
      } catch (Exception ex) {
        throw (IllegalStateException) (new IllegalStateException("Can't instantiate a managed connection")).initCause(ex);
      }
    }

    return connection;
  }

  @Override
  public void setServerConnect(boolean connecttoserver) {
    getDbConnection().setServerConnect(connecttoserver);
  }

  @Override
  public java.sql.Connection getConnection() {
    return getDbConnection() == null ? null : getDbConnection().getConnection();
  }

  @Override
  public String getProperty(String key) {
    return getDbConnection()==null?null:getDbConnection().getProperty(key);
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    return getDbConnection()==null?defaultValue:getDbConnection().getProperty(key, defaultValue);
  }

  @Override
  public Object setProperty(String key, String value) {
    return getDbConnection().setProperty(key, value);
  }

  @Override
  public boolean containsKey(String key) {
    return getDbConnection().containsKey(key);
  }

  public String getDriverClassName() {
    return getDbConnection().getProperty(DbConnection.DB_DRIVER_EMBEDDED, getDbConnection().getProperty(DbConnection.DB_DRIVER_NET, null));
  }

  @Override
  public String getUrl() {
    return getDbConnection() == null ? null : getDbConnection().getUrl();
  }

  @Override
  public String getDialect() {
    return getDbConnection() == null ? null : getDbConnection().getDialect();
  }

  public String getHibernateDialect() {
    String dialect = "";
    try {
      String url = this.getUrl().toLowerCase();
      if (url.startsWith("jdbc:jtds:sqlserver:")) {
        dialect = "org.hibernate.dialect.SQLServerDialect";
      }
    } catch (NullPointerException ex) {
      //ignore
    }
    return dialect;
  }

  @Override
  public Connection getTxConnection() {
    return getDbConnection() == null ? null : getDbConnection().getTxConnection();
  }

  @Override
  public Connection getTemporaryConnection() throws SQLException {
    return getDbConnection() == null ? null : getDbConnection().getTemporaryConnection();
  }

  @Override
  public boolean isPooled() {
    return getDbConnection() == null ? false : getDbConnection().isPooled();
  }

  @Override
  public boolean isConnectOnDemand() {
    return getDbConnection() == null ? false : getDbConnection().isConnectOnDemand();
  }

  @Override
  public boolean isCacheRowSet() {
    return getDbConnection() == null ? false : getDbConnection().isCacheRowSet();
  }

  @Override
  public boolean isCaseInsensitive() {
    return getDbConnection() == null ? false : getDbConnection().isCaseInsensitive();
  }

  @Override
  public void addActionListener(ActionListener l) {
    getDbConnection().addActionListener(l);
  }

  @Override
  public void removeActionListener(ActionListener l) {
    getDbConnection().removeActionListener(l);
  }

  @Override
  public boolean isConvertToVarchar() {
    return getDbConnection() == null ? false : getDbConnection().isConvertToVarchar();
  }
}
