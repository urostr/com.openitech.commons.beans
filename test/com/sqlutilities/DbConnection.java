/*
 * DbConnection.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.1 $
 */

package com.sqlutilities;

import com.openitech.db.model.*;
import com.openitech.spring.*;
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
