/*
 * Encoder.java
 *
 * Created on Nedelja, 16 julij 2006, 11:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.crypto;

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
public final class Encoder {
  
  /** Creates a new instance of Encoder */
  private Encoder() {
  }
  
  private static byte[] getDefaultKey() throws IOException {
    InputStream is = Encoder.class.getResourceAsStream("DefaultKey.bytes");
    ByteArrayOutputStream out = new ByteArrayOutputStream(is.available());
    byte[] b = new byte[is.available()];
    int in;
    
    while ((in=is.read(b))==b.length)
      out.write(b,0,in);
    
    return out.toByteArray();
  }
  
  private static byte[] cipher(byte[] data, byte[] key, int cipherMode) {
    try {
      SecretKey desKey = new SecretKeySpec(key, "DES");
      
      Cipher desCipher;
      
      // Create the cipher
      desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      desCipher.init(cipherMode, desKey);
      
      return desCipher.doFinal(data);
    } catch (Exception ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't encrypt data", ex);
      return null;
    }
  }
  
  public static byte[] encrypt(byte[] data) {
    try {
      return encrypt(data, getDefaultKey());
    } catch (IOException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't encrypt data", ex);
      return null;
    }
  }
  
  public static byte[] encrypt(byte[] data, byte[] key) {
    return cipher(data, key, Cipher.ENCRYPT_MODE);
  }
  
  public static byte[] decrypt(byte[] data) {
    try {
      return decrypt(data, getDefaultKey());
    } catch (IOException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't encrypt data", ex);
      return null;
    }
  }
  
  private static byte[] decrypt(byte[] data, byte[] key) {
    return cipher(data, key, Cipher.DECRYPT_MODE);
  }
  
  
}