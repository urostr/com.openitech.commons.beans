/*
 * Copy.java
 *
 * Created on Nedelja, 6 maj 2007, 19:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

import java.io.*;

/**
 *
 * @author uros
 */
public class Copy {
  
  /** Creates a new instance of Copy */
  private Copy() {
  }
  
  public static void simpleCopy(File from, File to) throws IOException {
    InputStream in = new BufferedInputStream(new FileInputStream(from));
    OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
    
    // Transfer bytes from in to out
    byte[] buf = new byte[65536];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }
}
