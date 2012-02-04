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
 * PatternTest.java
 * JUnit based test
 *
 * Created on Torek, 23 september 2008, 13:42
 */

package com.openitech.util;

import com.openitech.db.model.DbDataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.*;

/**
 *
 * @author uros
 */
public class PatternTest extends TestCase {
  
  public PatternTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }
  
  public void testSetName() {
    DbDataSource dsNaselja = new DbDataSource();
    
    String selectSql = com.openitech.io.ReadInputStream.getResourceAsString(com.openitech.db.components.JPIzbiraNaslova.class, "sql/mssql/sifrant_ns.sql", "cp1250")+"ORDER BY na_ime";
    String match = selectSql.toLowerCase().replaceAll("[\\r|\\n]"," ");
    
    Pattern namePattern = Pattern.compile(".*from\\W*(\\w*)\\W.*");

    Matcher matcher = namePattern.matcher(match);
    String name = selectSql.substring(0,Math.min(selectSql.length(),9));
    
    assertTrue(matcher.matches());
    
    name = matcher.group(1);
  }

}
