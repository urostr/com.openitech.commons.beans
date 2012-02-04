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
