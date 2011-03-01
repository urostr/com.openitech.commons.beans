/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.datasource;

import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class Blob extends TestCase {

  public Blob(String testName) {
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
  public void testBlob() throws SQLException {
    DbConnection.register();
    String columnName = "ObjectValue";
    String sql = "select ObjectValue from [dbo].[VariousValues] where [Id] = 439763";
    DbDataSource dataSource = new DbDataSource();
    dataSource.setSelectSql(sql);

    dataSource.reload();
    java.sql.Blob blob = dataSource.getBlob(columnName);
    if(blob != null){
      byte[] bytes = blob.getBytes(1, (int) blob.length());
      for (byte b : bytes) {
        System.out.println(b);
      }
    }
    System.out.println(blob);
  }
}
