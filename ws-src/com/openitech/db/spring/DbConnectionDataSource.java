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
package com.openitech.db.spring;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.connection.DbConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.util.ObjectUtils;

/**
 *
 * @author uros
 */
public class DbConnectionDataSource extends DriverManagerDataSource implements SmartDataSource, DisposableBean {

  public final Connection connection;
  ConnectionManager manager = ConnectionManager.getInstance();

  public DbConnectionDataSource() {
    this.hibernateDialect = manager.getHibernateDialect();
    this.connection = manager.getConnection();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return manager.getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    if (ObjectUtils.nullSafeEquals(username, getUsername()) &&
            ObjectUtils.nullSafeEquals(password, getPassword())) {
      return getConnection();
    } else {
      throw new SQLException("DbConnectionDataSource does not support custom username and password");
    }
  }

  @Override
  public Properties getConnectionProperties() {
    return new java.util.Properties();
  }

  public String getDriverClassName() {
    return manager.getProperty(DbConnection.DB_DRIVER_EMBEDDED, manager.getProperty(DbConnection.DB_DRIVER_NET, null));
  }

  @Override
  public String getPassword() {
    return manager.getProperty(DbConnection.DB_PASS, null);
  }

  @Override
  public String getUrl() {
    return manager.getProperty(DbConnection.DB_JDBC_EMBEDDED, manager.getProperty(DbConnection.DB_JDBC_NET, null));
  }

  @Override
  public String getUsername() {
    return manager.getProperty(DbConnection.DB_USER, null);
  }

  @Override
  public void setConnectionProperties(Properties connectionProperties) {
    throw new UnsupportedOperationException("DbConnectionDataSource does not support custom connection propertites");
  }

  @Override
  public void setDriverClassName(String driverClassName) throws CannotGetJdbcConnectionException {
    throw new UnsupportedOperationException("DbConnectionDataSource does not support custom driverClassName");
  }

  @Override
  public void setPassword(String password) {
    throw new UnsupportedOperationException("DbConnectionDataSource does not support custom password");
  }

  @Override
  public void setUrl(String url) {
    throw new UnsupportedOperationException("DbConnectionDataSource does not support custom url");
  }

  @Override
  public void setUsername(String username) {
    throw new UnsupportedOperationException("DbConnectionDataSource does not support custom username");
  }

  @Override
  public boolean shouldClose(Connection connection) {
    return false;
  }

  @Override
  public void destroy() throws Exception {
  }
  public final String hibernateDialect;

  /**
   * Get the value of hibernateDialect
   *
   * @return the value of hibernateDialect
   */
  public String getHibernateDialect() {
    return hibernateDialect;
  }
}
