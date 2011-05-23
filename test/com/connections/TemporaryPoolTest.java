/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connections;

import com.openitech.db.connection.ConnectionManager;
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
public class TemporaryPoolTest extends TestCase {

  public TemporaryPoolTest(String testName) {
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
    
    Connection temporaryConnection = connectionManager.getTemporaryConnection();
    PreparedStatement prepareStatement = temporaryConnection.prepareStatement("select 1");
    prepareStatement.execute();
    temporaryConnection.close();

    assertEquals(true, temporaryConnection.isClosed());

    List<Thread> threadList = new ArrayList<Thread>(300);
    for (int i = 0; i < 300; i++) {
      Thread th = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            Connection temporaryConnection1 = connectionManager.getTemporaryConnection();
            PreparedStatement prepareStatement = temporaryConnection1.prepareStatement("select 1");
            try {
              Thread.sleep(100);
            } catch (InterruptedException ex) {
              Logger.getLogger(TemporaryPoolTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            prepareStatement.execute();
            temporaryConnection1.close();

            assertEquals(true, temporaryConnection1.isClosed());
          } catch (SQLException ex) {
            Logger.getLogger(TemporaryPoolTest.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
      threadList.add(th);
      th.start();
      
    }

    for (Thread thread : threadList) {
      thread.join();
    }
  }
}
