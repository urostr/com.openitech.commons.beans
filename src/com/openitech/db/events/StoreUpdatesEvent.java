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
 * ActiveRowChangeEvent.java
 *
 * Created on April 2, 2006, 12:12 PM
 *
 * $Revision $
 */

package com.openitech.db.events;

import com.openitech.db.model.DbDataSource;
import java.util.Collections;
import java.util.EventObject;
import java.util.Map;

/**
 *
 * @author uros
 */
public class StoreUpdatesEvent extends EventObject {
  public static final int ROW_CHANGED = 0;
  public static final int FIELD_CHANGED = 1;
  private int row = -1;
  private int hash = 17;
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public StoreUpdatesEvent(DbDataSource source, int row, boolean insert, Map<String,Object> columnValues, Map<Integer, Object> oldColumnValues) {
    super(source);
    this.row = row;
    this.insert = insert;
    if (columnValues!=null)
      this.columnValues = Collections.unmodifiableMap(columnValues);
//    if (oldColumnValues!=null)
//      this.oldColumnValues = Collections.unmodifiableMap(oldColumnValues);
    this.oldColumnValues = oldColumnValues;
    this.hash = 37*source.hashCode();
  }
    
  public int getRow() {
    return row;
  }
  
  public DbDataSource getSource() {
    return (DbDataSource) super.getSource();
  }

  public int hashCode() {
    return this.hash;
  }

  /**
   * Holds value of property insert.
   */
  private boolean insert;

  /**
   * Getter for property insert.
   * @return Value of property insert.
   */
  public boolean isInsert() {
    return this.insert;
  }

  /**
   * Holds value of property columnValues.
   */
  private Map<String,Object> columnValues;

  /**
   * Getter for property columnValues.
   * @return Value of property columnValues.
   */
  public Map<String,Object> getColumnValues() {
    return this.columnValues;
  }

  /**
   * Holds value of property oldColumnValues.
   */
  private Map<Integer, Object> oldColumnValues;

  /**
   * Getter for property oldColumnValues.
   * @return Value of property oldColumnValues.
   */
  public Map<Integer, Object> getOldColumnValues() {
    return this.oldColumnValues;
  }
}
