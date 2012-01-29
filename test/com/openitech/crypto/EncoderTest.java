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
 * EncoderTest.java
 * JUnit based test
 *
 * Created on Nedelja, 16 julij 2006, 13:26
 */

package com.openitech.crypto;

import junit.framework.*;
import com.openitech.Settings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author uros
 */
public class EncoderTest extends TestCase {
  
  public EncoderTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(EncoderTest.class);
    
    return suite;
  }
  
  private byte[] getDefaultKey() throws IOException {
    InputStream is = Encoder.class.getResourceAsStream("DefaultKey.bytes");
    ByteArrayOutputStream out = new ByteArrayOutputStream(is.available());
    byte[] b = new byte[is.available()];
    int in;
    
    while ((in=is.read(b))==b.length)
      out.write(b,0,in);
    
    return out.toByteArray();
  }
  
  public void testDumpKey() throws IOException {
    byte[] keys = getDefaultKey();
    
    for (byte key:keys) {
      System.out.println(key+",");
    }
    
    System.out.println(new String(keys));
  }

  /**
   * Test of encrypt method, of class com.openitech.crypto.Encoder.
   */
  public void testEncrypt() {
    System.out.println("encrypt");
    
    String string = "HELLO";
    
    byte[] data = string.getBytes();
    byte[] result = Encoder.encrypt(data);
    assertNotNull(result);
    
    byte[] original = Encoder.decrypt(result);
    
    assertEquals(string, new String(original));
   }

  
}
