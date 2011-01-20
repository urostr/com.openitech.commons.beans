/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.rowset;

import java.sql.SQLException;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class CachedRowSetImplTest extends TestCase {
    
    public CachedRowSetImplTest(String testName) {
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

  public void testSomeMethod() throws SQLException {
    // TODO review the generated test code and remove the default call to fail.
    assertNotNull(new CachedRowSetImpl());
  }

}
