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
 * DbAuthService.java
 *
 * Created on Ponedeljek, 9 april 2007, 9:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;
import com.openitech.auth.jaas.LocalGroupPrincipal;


/**
 *
 * @author uros
 */
public class DbAuthService {
  private static final String CLASS_NAME=DbAuthService.class.getCanonicalName();
  private static DbAuthService instance = null;
   
  /**
   * Creates a new instance of DbAuthService
   */
  private DbAuthService() {
    
  }
  
  public static DbAuthService getInstance() throws ClassNotFoundException {
    if (instance==null) {
      instance = new DbAuthService();
    }
    
    return instance;
  }
  
  public boolean authenticate(String username, char[] password, List<LocalGroupPrincipal> groups, boolean create) {
    try {
      com.openitech.db.connection.DbConnection dbConnection = ConnectionManager.getInstance();
      
      dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_USER, username);
      dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_PASS, new String(password));
      
      Connection conn = dbConnection.getConnection();
      return conn!=null;
    } catch (Exception ex) {
      Logger.getLogger(CLASS_NAME).info(ex.getMessage());
      return false;
    }
  }
  
  
  
}
