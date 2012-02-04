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
package com.openitech.sql.datasource;

import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;
import java.util.logging.Logger;
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
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Byte.toString(b));
      }
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(blob.toString());
  }
}
