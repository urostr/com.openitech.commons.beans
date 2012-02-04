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

package com.openitech.spring;

import com.openitech.auth.LoginContextManager;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import junit.framework.TestCase;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author uros
 */
public class SpringContextManagerTest extends TestCase {
    
    public SpringContextManagerTest(String testName) {
        super(testName);

        DbConnection.register();
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
   * Test of getContext method, of class SpringContextManager.
   */
  public void testGetContext() throws LoginException {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("getContext");
    LoginContextManager.getInstance().logon();
    ConfigurableApplicationContext result = SpringContextManager.getInstance().getContext();
    assertNotNull(result);
  }

}
