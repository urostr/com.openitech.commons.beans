/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.ConnectionManager;
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
