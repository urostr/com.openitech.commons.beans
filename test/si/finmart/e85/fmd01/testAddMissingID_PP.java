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


///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package si.finmart.e85.fmd01;
//
//import com.openitech.db.connection.ConnectionManager;
//import com.openitech.db.model.factory.DataSourceConfig;
//import com.openitech.db.model.factory.DataSourceParametersFactory;
//import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
//import com.openitech.db.model.xml.config.TemporaryTable;
//import com.openitech.text.CaseInsensitiveString;
//import com.openitech.value.events.Event;
//import com.openitech.value.fields.Field;
//import java.sql.SQLException;
//import java.text.ParseException;
//import java.util.Map;
//import com.openitech.sql.util.SqlUtilities;
//import com.openitech.value.fields.FieldValue;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import junit.framework.TestCase;
//
///**
// *
// * @author uros
// */
//public class testAddMissingID_PP extends TestCase {
//
//  final java.sql.Connection connection;
//  final SqlUtilities sqlUtilites;
//  final Map<CaseInsensitiveString, Field> preparedFields;
//  final TemporaryTable ttE_85_FMD01;
//
//  public testAddMissingID_PP(String testName) throws ParseException, SQLException {
//    super(testName);
//    DbConnection.register();
//    com.openitech.db.connection.DbConnection dbConnection = ConnectionManager.getInstance();
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_JDBC_NET, "jdbc:jtds:sqlserver://192.168.63.22;DatabaseName=PonudbePreverjanje");
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_USER, "sa");
//    dbConnection.setProperty(com.openitech.db.connection.DbConnection.DB_PASS, "bruSwaqe");
//    connection = dbConnection.getConnection();
//    sqlUtilites = SqlUtilities.getInstance();
//    preparedFields = sqlUtilites.getPreparedFields();
//    ttE_85_FMD01 = sqlUtilites.getCachedTemporaryTable("[MViewCache].[dbo].[CACHE:E_85_FMD01]");
//  }
//
//  @Override
//  protected void setUp() throws Exception {
//    super.setUp();
//  }
//
//  @Override
//  protected void tearDown() throws Exception {
//    super.tearDown();
//  }
//
//  /**
//   * Test of saveAktivneEvents method, of class ZTAktivne.
//   */
//  public void testSaveAktivneEvents() throws Exception {
//    ResultSet e_85_missing_id_pp = connection.createStatement().executeQuery(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "e_85_find_missing_id_pp.sql", "cp1250"));
//
//    while (e_85_missing_id_pp.next()) {
//      Event event = sqlUtilites.findEvent(e_85_missing_id_pp.getLong("Id"));
//      FieldValue ID_PP = new FieldValue(new Field(preparedFields.get(new CaseInsensitiveString("ID_PP")), 1), e_85_missing_id_pp.getInt("ppid"));
//
//      event.addValue(ID_PP);
//
//      sqlUtilites.updateEvent(event);
//    }
//
//    assertNotNull(ttE_85_FMD01);
//
//    TemporaryParametersFactory ttFactory = new TemporaryParametersFactory();
//
//    TemporarySubselectSqlParameter createTemporaryTable = ttFactory.createTemporaryTable(ttE_85_FMD01);
//    createTemporaryTable.executeQuery(connection, new ArrayList<Object>());
//
//    ResultSet e_51_missing_id_pp = connection.createStatement().executeQuery(com.openitech.io.ReadInputStream.getResourceAsString(getClass(), "e_51_missing_id_pp.sql", "cp1250"));
//
//    while (e_51_missing_id_pp.next()) {
//      Event event = sqlUtilites.findEvent(e_51_missing_id_pp.getLong("Id"));
//      FieldValue ID_PP = new FieldValue(new Field(preparedFields.get(new CaseInsensitiveString("ID_PP")), 1), e_51_missing_id_pp.getInt("E_85_ID_PP"));
//
//      event.addValue(ID_PP);
//
//      sqlUtilites.updateEvent(event);
//    }
//
//  }
//
//
//
//  private static class TemporaryParametersFactory extends DataSourceParametersFactory<DataSourceConfig> {
//
//    public TemporaryParametersFactory() {
//    }
//
//    @Override
//    protected TemporarySubselectSqlParameter createTemporaryTable(TemporaryTable tt) {
//      return super.createTemporaryTable(tt);
//    }
//
//    @Override
//    protected TemporarySubselectSqlParameter createTemporaryTableParameter(TemporaryTable tt) {
//      return super.createTemporaryTableParameter(tt);
//    }
//  }
//}
