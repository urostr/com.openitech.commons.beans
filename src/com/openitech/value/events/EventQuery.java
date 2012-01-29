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
package com.openitech.value.events;

import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.value.fields.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author uros
 */
public interface EventQuery {

  /**
   * Get the value of parameters
   *
   * @return the value of parameters
   */
  public List<Object> getParameters();

  /**
   * Get the value of query
   *
   * @return the value of query
   */
  public String getQuery();

  /**
   * Get the value of namedParameters
   *
   * @return the value of namedParameters
   */
  public Map<Field, SqlParameter<Object>> getNamedParameters();

  /**
   * Get the value of valuesSet
   *
   * @return the value of valuesSet
   */
  public int getValuesSet();

  public int getSifrant();

  public String[] getSifra();

  /**
   * Get the value of resultFields
   *
   * @return the value of resultFields
   */
  public Set<Field> getResultFields();

  /**
   * Get the value of searchFields
   *
   * @return the value of searchFields
   */
  public Set<Field> getSearchFields();

  /**
   * Check if we're searching by the event's primary key
   * 
   */
  public boolean isSearchByEventPK();
}
