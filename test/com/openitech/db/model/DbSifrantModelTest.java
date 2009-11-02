/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.ConnectionManager;
import java.sql.SQLException;
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
    com.openitech.db.DbConnection dbConnection = ConnectionManager.getInstance();
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
    DbSifrantModel dbSifrantModel = new DbSifrantModel(null, "[ChangeLog].[dbo]", null, null);
    dbSifrantModel.setSifrantSkupina("Dogodki");
    dbSifrantModel.setSifrantOpis("REZULTAT_KLICA");

    assertTrue(dbSifrantModel.getSize()>0);
  }
}
