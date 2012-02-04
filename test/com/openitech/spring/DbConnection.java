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
 * DbConnection.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.1 $
 */

package com.openitech.spring;

import com.openitech.db.connection.AbstractSQLConnection;
import com.openitech.db.connection.ConnectionManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class DbConnection extends AbstractSQLConnection {
  static {
    ConnectionManager.registerManagedConnection(DbConnection.class);
  }
  /**
   * Creates a new instance of DbConnection
   */
  public DbConnection() {
    ConnectionManager.getInstance().setConnection(this);
  }
  
  public Properties loadProperites(String propertiesName) {
    Properties result = new Properties();
    try {
      result.load(this.getClass().getResourceAsStream(propertiesName));
      java.io.File properties = new java.io.File(propertiesName);
      if (properties.exists()) {
        Properties external = new Properties();
        external.load(new FileInputStream(properties));
        result.putAll(external);
      }
    } catch (IOException ex) {
      Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, "Can't load properties file '"+propertiesName+"'", ex);
    }
    return result;
  }
  

  protected void createSchema(java.sql.Connection conn) throws SQLException {
  }
  
  public static void register() {
    ConnectionManager.registerManagedConnection(DbConnection.class);
  }
  
}
