/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.util;

import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class RazdeliTelefonskoStevilkoTest extends TestCase {
    
    public RazdeliTelefonskoStevilkoTest(String testName) {
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

  /**
   * Test of razbij method, of class RazdeliTelefonskoStevilko.
   */
  public void testRazbij() {
    System.out.println("razbij");
    String telefonskaStevilka = "";
    String[] expResult = null;
    String[] result = RazdeliTelefonskoStevilko.razbij(telefonskaStevilka);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

}
