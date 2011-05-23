/*
 * DbConnection.java
 *
 * Created on Sobota, 6 maj 2006, 17:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.connection;

import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 *
 * @author uros
 */
public interface DbConnection {
  String DB_AUTOCOMMIT = "db.autocommit";
  String DB_CACHEROWSET = "db.cache.rowset";
  String DB_CACHEFIELDS = "db.cache.fields";
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
  String DB_USE_RECONNECT="db.reconnect.use";
  String DB_USE_POOL="db.pool.use";
  String DB_USE_TEMPORARY_POOL="db.pool.temporary.use";
  String DB_TEMP_POOL_SIZE = "db.pool.temporary.size";
  String DB_CASE_INSENSITIVE = "db.case.insensitive";
  String DB_USE_WEBROWSET = "db.useWebRowSet";
  String DB_USE_SECONDARY_WEBROWSET = "db.useSecondaryWebRowSet";
  String DB_SHADOW_LOADING= "db.shadow.loading";
  String DB_SHADOW_INTERRUPT= "db.shadow.interrupt";
  String DB_STOP_LOADING_THREAD= "db.thread.kill";
  String DB_OVERRIDE_CACHED_VIEWS= "db.override.cached";
  String DB_CONVERT_TO_VARCHAR= "db.convert.varchar";
  String DB_USE_VALUEID= "events.use.valueid";
  String DB_LOG_ACTIONS= "db.log.actions";
  
  String DB_ENTRY_SERVICE = "ws.entry";
  
  String DB_CONNECT_PREFIX="db.connect.";
  int DB_CONNECT_PREFIX_LENGTH=DB_CONNECT_PREFIX.length();
  
  int ACTION_DB_CONNECT = 1;
  String ACTION_GET_CONNECTION = "getConnection";
  String ACTION_GET_TEMP_CONNECTION = "getTemporaryConnection";

  String DB_DUMP_STATMENTS = "db.dumpstatments";
  String DB_MERGE_SECONDARY = "db.merge.secondary";
  String DB_OPTIMISE_RELOADING = "db.optimiseReloading";


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
  boolean isConvertToVarchar();
  String getDialect();
  String getUrl();

  public void addActionListener(ActionListener l);
  public void removeActionListener(ActionListener l);
}
