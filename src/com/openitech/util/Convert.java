/*
 * Convert.java
 *
 * Created on Nedelja, 17 september 2006, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

/**
 *
 * @author uros
 */
public class Convert {
  
  /** Creates a new instance of Convert */
  private Convert() {
  }
  
  
  public static String convertYuAsciiTo1250(String convert) {
    if (convert==null)
      return null ;
    else {
      StringBuilder ret = new StringBuilder(convert.length());
      char add;

      for (int i=0; i<convert.length(); i++) {
        switch (convert.charAt(i)) {
          case '[' : add = '�'; break;
          case ']' : add = '�'; break;
          case '\\' : add = '�'; break;
          case '{' : add = '�'; break;
          case '}' : add = '�'; break;
          case '|' : add = '�'; break;
          case '@' : add = '�'; break;
          case '`' : add = '�'; break;
          case '^' : add = '�'; break;
          case '~' : add = '�'; break;
          default: add = convert.charAt(i);
        }
        ret.append(add);
      }
      return ret.toString();
    }
  }

  public static String convert1250toYuAscii(String convert) {
    if (convert==null)
      return null ;
    else {
      StringBuilder ret = new StringBuilder(convert.length());
      char add;

      for (int i=0; i<convert.length(); i++) {
        switch (convert.charAt(i)) {
          case '�' : add = '['; break;
          case '�' : add = ']'; break;
          case '�' : add = '\\'; break;
          case '�' : add = '{'; break;
          case '�' : add = '}'; break;
          case '�' : add = '|'; break;
          case '�' : add = '@'; break;
          case '�' : add = '`'; break;
          case '�' : add = '^'; break;
          case '�' : add = '~'; break;
          default: add = convert.charAt(i);
        }
        ret.append(add);
      }
      return ret.toString();
    }
  }

}
