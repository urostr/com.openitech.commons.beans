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
import java.sql.SQLException;
import java.util.EventObject;

/**
 *
 * @author uros
 */
public class ActiveRowChangeEvent extends EventObject {
  public static final int ROW_CHANGED = 0;
  public static final int FIELD_CHANGED = 1;
  
  private int type;
  private int newRowNumber = -1;
  private int oldRowNumber = -1;
  private String columnName = null;
  private int columnIndex = -1;
  private DbDataSource source = null;
  private int hash = 17;
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public ActiveRowChangeEvent(DbDataSource source, int newRowNumber, int oldRowNumber) {
    super(source);
    type = ROW_CHANGED;
    this.source = source;
    this.newRowNumber = newRowNumber;
    this.oldRowNumber = oldRowNumber;
    this.hash = 37*source.hashCode();
  }
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public ActiveRowChangeEvent(DbDataSource source, String columnName, int columnIndex) {
    super(source);
    type = FIELD_CHANGED;
    this.source = source;
    this.columnName = columnName;
    this.columnIndex = columnIndex;
    this.hash = 17+37*source.hashCode();
  }
  
  public int getType() {
    return this.type;
  }
  
  public int getOldRowNumber() {
    return oldRowNumber;
  }
  
  public int getNewRowNumber() {
    return newRowNumber;
  }
  
  public String getColumnName() {
    return columnName;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }
  
  public DbDataSource getSource() {
    return source;
  }

  public Object getValue() throws SQLException {
    if (columnName!=null&&source!=null) {
      return source.getObject(columnName);
    } else if (columnIndex>0&&source!=null) {
      return source.getObject(columnIndex);
    } else
      return null;
  }

  public int hashCode() {
    return this.hash;
  }
}
