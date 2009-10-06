/*
 * DbConnection.java
 *
 * Created on March 26, 2006, 11:49 AM
 *
 * $Revision: 1.2 $
 */

package com.openitech.db;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class RemoteDbConnection extends AbstractConnection {
  private static final String propertiesName="remote.connection.properties";

  /**
   * Creates a new instance of DbConnection
   */
  public RemoteDbConnection(String port,String host) {
    settings = loadProperites("connection.properties");
    String DB_URL = settings.getProperty(ConnectionManager.DB_JDBC_NET);
    settings.setProperty(ConnectionManager.DB_JDBC_NET, MessageFormat.format(DB_URL, port, host));
  }
  
  public Properties loadProperites(String ignored) {
    Properties result = new Properties();
    try {
      result.load(this.getClass().getResourceAsStream(propertiesName));
    } catch (IOException ex) {
      Logger.getLogger(RemoteDbConnection.class.getName()).log(Level.SEVERE, "Can't load properties file '"+propertiesName+"'", ex);
    }
    return result;
  }

  protected void createSchema(java.sql.Connection conn) throws SQLException {
  }
  
}
