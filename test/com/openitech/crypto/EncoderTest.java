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