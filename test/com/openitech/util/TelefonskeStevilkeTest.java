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

package com.openitech.util;

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
