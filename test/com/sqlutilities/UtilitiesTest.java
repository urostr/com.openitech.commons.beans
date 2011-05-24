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
