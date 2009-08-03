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
          case '[' : add = 'Š'; break;
          case ']' : add = 'Æ'; break;
          case '\\' : add = 'Ð'; break;
          case '{' : add = 'š'; break;
          case '}' : add = 'æ'; break;
          case '|' : add = 'ð'; break;
          case '@' : add = 'Ž'; break;
          case '`' : add = 'ž'; break;
          case '^' : add = 'È'; break;
          case '~' : add = 'è'; break;
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
          case 'Š' : add = '['; break;
          case 'Æ' : add = ']'; break;
          case 'Ð' : add = '\\'; break;
          case 'š' : add = '{'; break;
          case 'æ' : add = '}'; break;
          case 'ð' : add = '|'; break;
          case 'Ž' : add = '@'; break;
          case 'ž' : add = '`'; break;
          case 'È' : add = '^'; break;
          case 'è' : add = '~'; break;
          default: add = convert.charAt(i);
        }
        ret.append(add);
      }
      return ret.toString();
    }
  }

}
