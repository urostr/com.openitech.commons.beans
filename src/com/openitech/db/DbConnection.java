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
  String DB_DRIVER_EMBEDDED = "db.driver.embedded";
  String DB_DRIVER_NET = "db.driver.net";
  String DB_JDBC_EMBEDDED = "db.jdbc.embedded";
  String DB_JDBC_NET = "db.jdbc.net";
  String DB_PASS = "db.pwd";
  String DB_SHUTDOWN_HOOK = "db.shutdown.embedded";
  String DB_USER = "db.user";
  String DB_DELIMITER_LEFT = "db.delimiter.left";
  String DB_DELIMITER_RIGHT = "db.delimiter.right";
  
  String DB_CONNECT_PREFIX="db.connect.";
  int DB_CONNECT_PREFIX_LENGTH=DB_CONNECT_PREFIX.length();


  Connection getConnection();

  String getProperty(String key);

  String getProperty(String key, String defaultValue);

  Object setProperty(String key, String value);

  void setServerConnect(boolean connecttoserver);

  boolean containsKey(String key);
  
}
