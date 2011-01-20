/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.datasource;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

/**
 *
 * @author uros
 */
public class DataSourceFactory {
  private static Map<Class<? extends Driver>, DataSourceFactory.Implementation> dataSources = new HashMap<Class<? extends Driver>, DataSourceFactory.Implementation>();

  static {
    for (DataSources dataSource : DataSources.values()) {
      if (dataSource.getDriverClass()!=null) {
        dataSources.put(dataSource.getDriverClass(), dataSource.create());
      }
    }
  }

  private DataSourceFactory() {
  }

  public static DataSource getDataSource(String url, Properties properties) throws SQLException {
    Driver driver = DriverManager.getDriver(url);
    if (dataSources.containsKey(driver.getClass())) {
      return dataSources.get(driver.getClass()).getDataSource(url, properties);
    } else {
      return null;
    }
  }

  public static void register(Class<? extends Driver> driver, DataSourceFactory.Implementation implementation) {
    dataSources.put(driver, implementation);
  }

  public static interface Implementation {
    public DataSource getDataSource(String url, Properties properties);
  }

  private static enum DataSources {
    jTDS {


      @Override
      public DataSourceFactory.Implementation create() {
        if (implementation==null) {
          implementation = new com.openitech.sql.datasource.jtds.JtdsDataSourceFactory();
        }

        return implementation;
      }

      @Override
      public Class<? extends Driver> getDriverClass() {
        try {
          return (Class<? extends Driver>) Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
          return null;
        }
      }

    };

    DataSourceFactory.Implementation implementation;

    public abstract DataSourceFactory.Implementation create();
    public abstract Class<? extends Driver> getDriverClass();
  }
}
