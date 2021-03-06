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
package com.sqlutilities;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class UtilitiesTest extends TestCase {

  public UtilitiesTest(String testName) {
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

  // TODO add test methods here. The name must begin with 'test'. For example:
  public void testTempPool() throws SQLException, InterruptedException {
    DbConnection.register();
    final ConnectionManager connectionManager = ConnectionManager.getInstance();
    connectionManager.getConnection();


    SqlUtilities sqlUtilities = SqlUtilities.getInstance();

    sqlUtilities.beginTransaction();
    try {
      FieldValue nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_KONTAKTA", java.sql.Types.VARCHAR));
      System.out.println(nextIdentity.getValue());
      assertEquals("AAA000003553", nextIdentity.getValue());
      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_KONTAKTA", java.sql.Types.VARCHAR));
      System.out.println(nextIdentity.getValue());
      assertEquals("AAA000003554", nextIdentity.getValue());

      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_NASLOVA", java.sql.Types.INTEGER));
      System.out.println(nextIdentity.getValue());
      assertEquals(717L, nextIdentity.getValue());

      
      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_TEST", java.sql.Types.VARCHAR));
      System.out.println(nextIdentity.getValue());
      assertEquals("AAA000000001", nextIdentity.getValue());

      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_TEST", java.sql.Types.VARCHAR));
      System.out.println(nextIdentity.getValue());
      assertEquals("AAA000000002", nextIdentity.getValue());

      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_TEST2", java.sql.Types.INTEGER));
      System.out.println(nextIdentity.getValue());
      assertEquals(1L, nextIdentity.getValue());

      nextIdentity = sqlUtilities.getNextIdentity(new Field("ID_TEST2", java.sql.Types.INTEGER));
      System.out.println(nextIdentity.getValue());
      assertEquals(2L, nextIdentity.getValue());

    } finally {
      sqlUtilities.endTransaction(false, true);
    }
  }
}
