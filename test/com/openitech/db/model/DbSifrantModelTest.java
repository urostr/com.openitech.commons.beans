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
package com.openitech.db.model;

import com.openitech.db.connection.ConnectionManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class DbSifrantModelTest extends TestCase {

  final java.sql.Connection connection;

  public DbSifrantModelTest(String testName) {
    super(testName);
    DbConnection.register();
    com.openitech.db.connection.DbConnection dbConnection = ConnectionManager.getInstance();
    connection = dbConnection.getConnection();
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
   * Test of setTextNotDefined method, of class DbSifrantModel.
   */
  public void testDbSifrantModel() throws SQLException {
    DbDataSource.DUMP_SQL = true;
    List<String> allowedValues = new ArrayList<String>();
    allowedValues.add("BK01");
    allowedValues.add("BK02");
    allowedValues.add("BK03");

    DbSifrantModel dbSifrantModel = new DbSifrantModel(null, "[ChangeLog].[dbo]", allowedValues, null);
    dbSifrantModel.setSifrantSkupina("Dogodki");
    dbSifrantModel.setSifrantOpis("REZULTAT_KLICA");


    assertTrue(dbSifrantModel.getSize() > 0);

    List<String> excludedValues = new ArrayList<String>();
    excludedValues.add("BK01");
    excludedValues.add("BK02");
    excludedValues.add("BK03");

    DbSifrantModel dbSifrantModel2 = new DbSifrantModel(null, "[ChangeLog].[dbo]", null, excludedValues);
    dbSifrantModel2.setSifrantSkupina("Dogodki");
    dbSifrantModel2.setSifrantOpis("REZULTAT_KLICA");

    assertTrue(dbSifrantModel2.getSize() > 0);


  }
}
