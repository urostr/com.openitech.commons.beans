/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.datasource.jtds;

import com.openitech.sql.datasource.DataSourceFactory;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import java.util.Properties;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

/**
 *
 * @author uros
 */
public class JtdsDataSourceFactory implements DataSourceFactory.Implementation {

  private static BeanInfo jtdsDataSourceBeanInfo;

  @Override
  public DataSource getDataSource(String url, Properties properties) {
    try {
      Driver driver = DriverManager.getDriver(url);

      JtdsDataSource dataSource = new JtdsDataSource();

      if (jtdsDataSourceBeanInfo == null) {
        jtdsDataSourceBeanInfo = Introspector.getBeanInfo(JtdsDataSource.class);
      }

      for (DriverPropertyInfo propertyInfo : driver.getPropertyInfo(url, properties)) {
        if (propertyInfo.value != null) {
          for (PropertyDescriptor propertyDescriptor : jtdsDataSourceBeanInfo.getPropertyDescriptors()) {
            if (propertyDescriptor.getName().equalsIgnoreCase(propertyInfo.name)) {
//              Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(propertyInfo.name+"="+propertyInfo.value);
              Object value;
              if (int.class.equals(propertyDescriptor.getPropertyType())) {
                value = Integer.parseInt(propertyInfo.value);
              } else if (boolean.class.equals(propertyDescriptor.getPropertyType())) {
                value = Boolean.parseBoolean(propertyInfo.value);
              } else if (long.class.equals(propertyDescriptor.getPropertyType())) {
                value = Long.parseLong(propertyInfo.value);
              } else
                value = propertyInfo.value;
              propertyDescriptor.getWriteMethod().invoke(dataSource, value);
              break;
            }
          }
        }
      }

      return dataSource;
    } catch (Exception ex) {
      Logger.getLogger(JtdsDataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }
}
