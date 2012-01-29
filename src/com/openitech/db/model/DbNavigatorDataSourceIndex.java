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
 * DbNavigatorDataSourceIndex.java
 *
 * Created on Sobota, 12 januar 2008, 12:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.sql.SQLException;
import java.util.Set;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public interface DbNavigatorDataSourceIndex<T extends DbNavigatorDataSource> {
  
  public void setDataSource(T dataSource)  throws SQLException;
  public T getDataSource();
  
  public void setKeys(String... columns)  throws SQLException;
  
  public String[] getKeys();
  
  /**
   * Finds the indexed row
   * @param values 
   * @return 
   */
  public Set<Integer> findRows(Object... values);

  public void removeListDataListener(ListDataListener l);
  public void addListDataListener(ListDataListener l);

}
