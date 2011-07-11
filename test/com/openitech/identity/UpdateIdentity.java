/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.identity;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.Event;
import com.openitech.value.fields.FieldValue;
import java.sql.SQLException;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class UpdateIdentity extends TestCase {

  public UpdateIdentity(String testName) {
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


//    SqlUtilities sqlUtilities = SqlUtilities.getInstance();
//
//    sqlUtilities.beginTransaction();
//    try {
//      Event result = new Event(0, "ID01");
//        result.setId(997566L);
//        result.setEventSource(-1);
//        result.setDatum(new java.sql.Date(System.currentTimeMillis()));
//        FieldValue fv = new FieldValue("ID_RPP_OSEBE", java.sql.Types.VARCHAR, "AAA002993607");
//
//        result.addValue(fv);
//
//        sqlUtilities.storeEvent(result);
////sqlUtilities.endTransaction(false, true);
//    } finally {
//      sqlUtilities.endTransaction(true, true);
//    }
    
  }
}
