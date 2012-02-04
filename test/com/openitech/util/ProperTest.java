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
 * ProperTest.java
 * JUnit based test
 *
 * Created on Nedelja, 2 september 2007, 13:47
 */

package com.openitech.util;

import com.openitech.text.Proper;
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
