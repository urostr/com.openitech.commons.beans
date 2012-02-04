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
 * @author domenbasic
 */
public abstract class AbstractSourceColumnFactory implements SourceColumnFactory {

  protected DbDataSource dataSource;
  protected String[] sourceColumnNames = null;
  protected Field[] resultFields = null;

  /**
   * Get the value of sourceColumnNames
   *
   * @return the value of sourceColumnNames
   */
  public String[] getSourceColumnNames() {
    return sourceColumnNames;
  }

  /**
   * Set the value of sourceColumnNames
   *
   * @param sourceColumnNames new value of sourceColumnNames
   */
  public void setSourceColumnNames(String... sourceColumnNames) {
    this.sourceColumnNames = sourceColumnNames;
  }

  @Override
  public String getSourceColumnName() {
    if (getSourceColumnNames() != null
            && getSourceColumnNames().length > 0) {
      return getSourceColumnNames()[0];
    } else {
      return null;
    }
  }

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }

  @Override
  public Field[] getResultFields(String columnName) {
    if (resultFields == null) {
      resultFields = new Field[]{Field.getField(columnName)};
    }

    return resultFields;
  }

  /**
   * Set the value of dataSource
   *
   * @param dataSource new value of dataSource
   */
  @Override
  public void setDataSource(DbDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<Object> getColumnValue(SourceColumnFactoryParameter parameter) throws SQLException {
    List<Object> result = new ArrayList<Object>();
    List<Object> values = parameter.getValues(getSourceColumnNames());

    for (Object value : values) {
      result.add(this.getColumnValue(value, parameter));
    }

    return result;
  }
}
