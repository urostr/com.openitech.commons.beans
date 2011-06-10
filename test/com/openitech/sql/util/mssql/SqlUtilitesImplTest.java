/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.util.mssql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class SqlUtilitesImplTest extends TestCase {

  public SqlUtilitesImplTest(String testName) {
    super(testName);
    DbConnection.register();
    com.openitech.db.connection.DbConnection dbConnection = ConnectionManager.getInstance();
//    dbConnection.setProperty(DbConnection.DB_JDBC_NET, "jdbc:jtds:sqlserver://192.168.63.22;DatabaseName=PonudbePreverjanje");
//    dbConnection.setProperty(DbConnection.DB_USER, "sa");
//    dbConnection.setProperty(DbConnection.DB_PASS, "bruSwaqe");
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_JDBC_NET, "jdbc:jtds:sqlserver://192.168.167.131;DatabaseName=Uvoz");
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_USER, "sa");
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_PASS, "admin");
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testGetNextIdentity() throws Exception {
    System.out.println("getNextIdentity");
    Field field = new Field(39, "ID_TERMINA", java.sql.Types.VARCHAR, 1);
    Object initValue = "TA008000";
    SqlUtilitesImpl instance = new SqlUtilitesImpl();
    FieldValue expResult = new FieldValue(field, initValue);
    FieldValue result = instance.getNextIdentity(field, initValue);
    assertEquals(expResult.getValue(), result.getValue());

    expResult = new FieldValue(field, "TA008001");
    result = instance.getNextIdentity(field);
    assertEquals(expResult.getValue(), result.getValue());
  }
}
