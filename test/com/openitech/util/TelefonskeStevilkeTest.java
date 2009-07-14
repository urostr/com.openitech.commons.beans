/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.util;

import com.openitech.util.TelefonskeStevilke.Telefon;
import junit.framework.TestCase;

/**
 *
 * @author uros
 */
public class TelefonskeStevilkeTest extends TestCase {
    
    public TelefonskeStevilkeTest(String testName) {
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
   * Test of getTelefon method, of class TelefonskeStevilke.
   */
  public void testGetTelefon() {
    System.out.println("getTelefon");
    Telefon expResult = new Telefon("386", "41", "765030");
    Telefon result = TelefonskeStevilke.getTelefon("041/765-030");
    assertEquals(expResult, result);

    result = TelefonskeStevilke.getTelefon("+38641765030");
    assertEquals(expResult, result);

    expResult = new Telefon("386", "1", "8323960");
    result = TelefonskeStevilke.getTelefon("+38618323960");
    assertEquals(expResult, result);
  }

}
