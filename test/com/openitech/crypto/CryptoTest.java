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
 * CryptoTest.java
 * JUnit based test
 *
 * Created on Nedelja, 16 julij 2006, 11:06
 */

package com.openitech.crypto;

import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import junit.framework.*;

/**
 *
 * @author uros
 */
public class CryptoTest extends TestCase {
  
  public CryptoTest(String testName) {
    super(testName);
  }
  
  public void testGenerateKey() {
    try {
      KeyGenerator kgen = KeyGenerator.getInstance("DES");
      SecretKey skey = kgen.generateKey();
      byte[] raw = skey.getEncoded();
      
      FileOutputStream out = new FileOutputStream("DefaultKey.bytes");
      
      out.write(raw);
      out.close();
      assertTrue(true);
    } catch (Exception ex) {
      assertTrue(false);
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
    }
  }

}
