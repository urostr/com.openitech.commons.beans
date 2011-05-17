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
