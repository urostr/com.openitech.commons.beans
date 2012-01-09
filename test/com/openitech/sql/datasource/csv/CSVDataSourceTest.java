/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.datasource.csv;

import com.openitech.db.model.DbDataSource;
import java.io.File;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class CSVDataSourceTest extends TestCase {

  public CSVDataSourceTest(String testName) {
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
  public void testHello() throws Exception {
    File file = new File("test/myfile.csv");
    System.out.println(file.getAbsolutePath());
    DbDataSource dataSource = new DbDataSource(file, DbDataSource.SourceType.CSV);
    dataSource.loadData();

    dataSource.first();
    System.out.println(dataSource.getString("Ime"));
    System.out.println(dataSource.getString("Priimek"));
    dataSource.next();
    System.out.println(dataSource.getString("Priimek"));
    System.out.println(dataSource.getString("Ime"));
    System.out.println(dataSource.getString("Naslov"));
    System.out.println(dataSource.getString("KrNeki"));
    dataSource.next();
    System.out.println(dataSource.getString("Priimek"));
    System.out.println(dataSource.getString("Ime"));
    System.out.println(dataSource.getString("Naslov"));
    System.out.println(dataSource.getString("KrNeki"));
  }

  public void testHello2() throws Exception {
    File file = new File("test/myfile.csv");
    System.out.println(file.getAbsolutePath());
    DbDataSource dataSource = new DbDataSource(file, DbDataSource.SourceType.CSV);
    dataSource.loadData();

    dataSource.first();
    System.out.println(dataSource.getDate("Datum"));
    dataSource.next();
    System.out.println(dataSource.getDate("Datum"));

  }
}
