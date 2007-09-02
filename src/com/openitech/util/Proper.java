/*
 * Proper.java
 *
 * Created on Nedelja, 2 september 2007, 13:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

/**
 *
 * @author uros
 */
public class Proper {
  private static final java.util.regex.Pattern formatter = java.util.regex.Pattern.compile("(([\\s\\p{Punct}]*([^\\s^\\p{Punct}]))([^\\s^\\p{Punct}]*))");
  
  /** Creates a new instance of Proper */
  private Proper() {
  }
  
  public static <T extends CharSequence> String format(T text) {
    if (text==null)
      return null;
    else {        
      StringBuffer result = new StringBuffer(((CharSequence) text).length());
      String veznik;
      
      java.util.regex.Matcher matcher = formatter.matcher(text);
      
      while (matcher.find()) {
        veznik=matcher.group(3).toLowerCase()+matcher.group(4).toLowerCase();
        if (veznik.equals("za")||veznik.equals("v")||veznik.equals("pri")||veznik.equals("nad")||veznik.equals("pod")||veznik.equals("vas")||veznik.equals("in"))
          result.append(matcher.group(2).toLowerCase()).append(matcher.group(4).toLowerCase());
        else
          result.append(matcher.group(2).toUpperCase()).append(matcher.group(4).toLowerCase());
      }
      
      return result.toString();
    }
  }
  
}
