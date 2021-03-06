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
 * AssociatedFilter.java
 *
 * Created on Ponedeljek, 4 februar 2008, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.swing.framework.context;

/**
 *
 * @author uros
 */
public interface AssociatedFilter {
  /**
   * Getter for property filter.
   * @return Value of property filter.
   */
  public java.awt.Component getFilterPanel();

  /**
   * Setter for property filter.
   * @param filter New value of property filter.
   */
  public void setFilterPanel(java.awt.Component filter);
  
}
