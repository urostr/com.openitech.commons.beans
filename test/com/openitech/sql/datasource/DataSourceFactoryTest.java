/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.datasource;

import com.openitech.db.connection.DbConnection;
import com.openitech.sql.datasource.DataSourceFactory.Implementation;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Driver;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class DataSourceFactoryTest extends TestCase {

  public DataSourceFactoryTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  private Properties loadProperites(String propertiesName) {
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
      Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, "Can't load properties file '" + propertiesName + "'", ex);
    }
    return result;
  }

  /**
   * Test of getDataSource method, of class DataSourceFactory.
   */
  public void testGetDataSource() throws Exception {
    System.out.println("getDataSource");
    Properties connection = loadProperites("connection.properties");

    Class.forName(connection.getProperty(DbConnection.DB_DRIVER_NET));

    String url = connection.getProperty(DbConnection.DB_JDBC_NET);
    Properties properties = new Properties();

    for (Iterator<Map.Entry<Object, Object>> s = connection.entrySet().iterator(); s.hasNext();) {
      Map.Entry<Object, Object> entry = s.next();
      if (((String) entry.getKey()).startsWith(DbConnection.DB_CONNECT_PREFIX)) {
        properties.put(((String) entry.getKey()).substring(DbConnection.DB_CONNECT_PREFIX_LENGTH), entry.getValue());
      }
    }
    
    DataSource result = DataSourceFactory.getDataSource(url, properties);
    
    assertNotNull(result);
  }

}
