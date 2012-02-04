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
package com.openitech.importer;

import com.openitech.db.model.factory.ClassInstanceFactory;
import com.openitech.db.model.factory.SourceColumnFactory;
import com.openitech.db.model.xml.config.Importer.Destination.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class SourceColumn {

  private String columnName;
  private Integer columnIndex;
  private String factoryClassName;
  private SourceColumnFactory factory;

  public SourceColumn(Column.SourceColumn sourceColumn) {
    this.columnName = sourceColumn.getName();
    this.columnIndex = sourceColumn.getIndex();
    if (sourceColumn.getFactory() != null && sourceColumn.getFactory().getClassName() != null) {
      this.factoryClassName = sourceColumn.getFactory().getClassName();
      try {
        this.factory = (SourceColumnFactory) ClassInstanceFactory.getInstance(factoryClassName).newInstance();
      } catch (Exception ex) {
        Logger.getLogger(SourceColumn.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  }

  public Integer getColumnIndex() {
    return columnIndex;
  }

  public String getColumnName() {
    return columnName;
  }

  public SourceColumnFactory getFactory() {
    return factory;
  }
}
