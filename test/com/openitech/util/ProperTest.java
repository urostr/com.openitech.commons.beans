/*
 * ProperTest.java
 * JUnit based test
 *
 * Created on Nedelja, 2 september 2007, 13:47
 */

package com.openitech.util;

import junit.framework.*;

/**
 *
 * @author uros
 */
public class ProperTest extends TestCase {
  
  public ProperTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }

  /**
   * Test of format method, of class com.openitech.util.Proper.
   */
  public void testFormat() {
    System.out.println("format");
    
    String text = "ALOJZ MIÈKaVEC-TONI";
    
    String expResult = "Alojz Mièkavec-Toni";
    String result = Proper.format(text);
    System.out.println(result);
    assertEquals(expResult, result);

    text = "DOL PRI lJUbLJANI";
    
    expResult = "Dol pri Ljubljani";
    result = Proper.format(text);
    System.out.println(result);
    assertEquals(expResult, result);
    
    text = "UL. H. MARINCLJA 8";
    
    expResult = "Ul. H. Marinclja 8";
    result = Proper.format(text);
    System.out.println(result);
    assertEquals(expResult, result);
  }
  
}
