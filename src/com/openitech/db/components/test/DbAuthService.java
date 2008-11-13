/*
 * DbAuthService.java
 *
 * Created on Ponedeljek, 9 april 2007, 9:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components.test;

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
      com.openitech.db.DbConnection dbConnection = DbDataModel.getDbConnection();
      
      dbConnection.setProperty(com.openitech.db.DbConnection.DB_USER, username);
      dbConnection.setProperty(com.openitech.db.DbConnection.DB_PASS, new String(password));
      
      Connection conn = dbConnection.getConnection();
      return conn!=null;
    } catch (Exception ex) {
      Logger.getLogger(CLASS_NAME).info(ex.getMessage());
      return false;
    }
  }
  
  
  
}
