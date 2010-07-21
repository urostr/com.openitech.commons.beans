/*
 * DbConnection.java
 *
 * Created on Sobota, 6 maj 2006, 17:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db;

import java.sql.Connection;

/**
 *
 * @author uros
 */
public interface DbConnection {
  String DB_AUTOCOMMIT = "db.autocommit";
  String DB_CACHEROWSET = "db.cache.rowset";
  String DB_TEST = "db.test";
  String DB_POOL_SIZE = "db.pool.size";
  String DB_POOL_NAME = "db.pool.name";
  String DB_MAX_POOL_SIZE="db.max.pool.size";
  String DB_MAX_ACTIVE_TIME="db.max.active.time";
  String DB_CONNECT_ON_DEMAND="db.connect.on.demand";
  String DB_DRIVER_EMBEDDED = "db.driver.embedded";
  String DB_DRIVER_NET = "db.driver.net";
  String DB_JDBC_EMBEDDED = "db.jdbc.embedded";
  String DB_JDBC_NET = "db.jdbc.net";
  String DB_PASS = "db.pwd";
  String DB_SHUTDOWN_HOOK = "db.shutdown.embedded";
  String DB_USER = "db.user";
  String DB_DELIMITER_LEFT = "db.delimiter.left";
  String DB_DELIMITER_RIGHT = "db.delimiter.right";
  String DB_POOL_EXECUTE_ON_CREATE = "db.pool.execute.on.create";
  String DB_USE_POOL="db.pool.use";
  String DB_CASE_INSESITIVE = "db.case.insensitive";
  String DB_USE_WEBROWSET = "db.useWebRowSet";
  String DB_USE_SECONDARY_WEBROWSET = "db.useSecondaryWebRowSet";
  String DB_PRIMARY_WS = "ws.primary";
  String DB_SECONDARY_WS = "ws.secondary";
  String DB_CREATE_SECONDARYS_WS = "ws.createsecondarys";
  
  String DB_CONNECT_PREFIX="db.connect.";
  int DB_CONNECT_PREFIX_LENGTH=DB_CONNECT_PREFIX.length();


  Connection getTemporaryConnection();
  Connection getConnection();
  Connection getTxConnection();

  String getProperty(String key);

  String getProperty(String key, String defaultValue);

  Object setProperty(String key, String value);

  void setServerConnect(boolean connecttoserver);

  boolean containsKey(String key);
  boolean isPooled();
  boolean isConnectOnDemand();
  boolean isCacheRowSet();
  boolean isCaseInsensitive();
  String getDialect();
  String getUrl();
}
