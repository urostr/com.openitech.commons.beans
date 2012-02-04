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
package com.openitech.db.model.factory;

import com.openitech.db.model.DbDataSource;
import com.openitech.value.fields.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author uros
 */
public interface SourceColumnFactory {
  public void setDataSource(DbDataSource dataSource);
  public String getSourceColumnName();
  public Field[] getResultFields(String columnName);
  public Object getColumnValue(Object value, SourceColumnFactoryParameter parameter) throws SQLException;
  public List<Object> getColumnValue(SourceColumnFactoryParameter parameter) throws SQLException;

  public static class SourceColumnFactoryParameter {


    public SourceColumnFactoryParameter(DbDataSource dataSource, String columnName, int row) {
      this.dataSource = dataSource;
      this.columnName = columnName;
      this.row = row;
    }
    
    private DbDataSource dataSource;

    /**
     * Get the value of dataSource
     *
     * @return the value of dataSource
     */
    public DbDataSource getDataSource() {
      return dataSource;
    }

    private String columnName;

    /**
     * Get the value of columnName
     *
     * @return the value of columnName
     */
    public String getColumnName() {
      return columnName;
    }

    private int row;

    /**
     * Get the value of row
     *
     * @return the value of row
     */
    public int getRow() {
      return row;
    }
    
    public List<Object> getValues(String... columnNames) throws SQLException {
      Object result = null;
      boolean hasColumn = false;

      if (columnNames != null) {
        for (String columnNameVariation : columnNames) {
          if (hasColumn = (dataSource.findColumn(columnNameVariation) > 0)) {
            result = dataSource.getValueAt(row, columnNameVariation);
            break;
          }
        }
      }

      if (!hasColumn) {
        result = dataSource.getValueAt(row, columnName);
      }

      List<Object> values = new ArrayList<Object>();
      values.add(result);
      
      return values;
    }
  }
}
