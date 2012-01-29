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

package com.openitech.db.filters;

import javax.swing.JMenu;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class JPDbDataSourceFilterTest extends TestCase {
    
    public JPDbDataSourceFilterTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

  /**
   * Test of getFilters method, of class JPDbDataSourceFilter.
   */
  public void testGetFilters() {
    System.out.println("getFilters");
    JPDbDataSourceFilter instance = new JPDbDataSourceFilter();
    DataSourceFiltersMap expResult = null;
    DataSourceFiltersMap result = instance.getFilters();
    assertNotNull(result);
  }

  /**
   * Test of getFilterMenuItem method, of class JPDbDataSourceFilter.
   */
  public void testGetFilterMenuItem() {
    System.out.println("getFilterMenuItem");
    JPDbDataSourceFilter instance = new JPDbDataSourceFilter();
    JMenu expResult = null;
    JMenu result = instance.getFilterMenuItem();
    assertNotNull(result);
  }

}
