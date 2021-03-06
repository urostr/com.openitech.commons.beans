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

import com.openitech.value.StringValue;
import junit.framework.TestCase;

/**
 *
 * @author domenbasic
 */
public class TestSifre extends TestCase {
    
    public TestSifre(String testName) {
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
     public void testHello() {

       assertEquals("A002", StringValue.getNextSifra("A001"));
       assertEquals("A010", StringValue.getNextSifra("A009"));
       assertEquals("AB4", StringValue.getNextSifra("AB3"));
       assertEquals("AAA00000002", StringValue.getNextSifra("AAA00000001"));
       assertEquals("ASB2", StringValue.getNextSifra("ASB1"));

       assertEquals("2", StringValue.getNextSifra("1"));
       assertEquals("10", StringValue.getNextSifra("9"));
       assertEquals("11516", StringValue.getNextSifra("11515"));

       assertEquals("AB", StringValue.getNextSifra("AB"));
     }

}
