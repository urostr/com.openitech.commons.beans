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
 * JPIzbiraNaslovaTest.java
 * JUnit based test
 *
 * Created on Torek, 23 september 2008, 12:22
 */

package com.openitech.db.components;

import junit.framework.*;
import com.openitech.auth.LoginContextManager;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.filters.DataSourceFilters.IntegerSeekType;
import com.openitech.db.filters.DataSourceFilters.SeekType;
import com.openitech.db.filters.FilterDocumentCaretListener;
import com.openitech.db.filters.FilterDocumentListener;
import com.openitech.db.filters.DataSourceFilterScheduler;
import com.openitech.db.model.DbDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.security.auth.login.LoginException;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author uros
 */
public class JPIzbiraNaslovaTest extends TestCase {
  
  public JPIzbiraNaslovaTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }

  /**
   * Test of getDataSource method, of class com.openitech.db.components.JPIzbiraNaslova.
   */
  public void testCreateInstance() throws SQLException {
    System.out.println("createInstance");
    
    JPIzbiraNaslova instance = new JPIzbiraNaslova();
    assertNotNull(instance);
  }

}
