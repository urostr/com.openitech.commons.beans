/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.value;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author domenbasic
 */
public class StringValue {

  private final static Pattern numbers = Pattern.compile("(\\p{Space}*)(\\p{Alpha}*)(\\p{Digit}*)(\\p{Space}*)");

  public static String getNextSifra(String sifra) {
    Matcher compare = numbers.matcher(sifra);
    if (compare.matches()) {
      String sdigit = compare.group(3);
      String sstring = compare.group(2);
      if (sdigit.length() > 0) {
        String ndigit = Integer.toString(Integer.parseInt(sdigit) + 1);
        if (ndigit.length() > sdigit.length() && sstring.length() > 0) {
          ndigit = "1";
          try {
            sstring = getNextString(sstring);
          } catch (UnsupportedEncodingException ex) {
            sdigit = "0" + sdigit;
            Logger.getLogger(StringValue.class.getName()).log(Level.WARNING, null, ex);
          }
        }
        ndigit = getZero(sdigit.length()) + ndigit;
        sdigit = ndigit.substring(ndigit.length() - sdigit.length());
      }
      sifra = compare.group(1) + sstring + sdigit + compare.group(4);
    }
    return sifra;
  }

  public static String getNextString(String sstring) throws UnsupportedEncodingException {
    return getNextString(sstring, sstring.length() - 1);
  }

  public static String getNextString(String sstring, int at) throws UnsupportedEncodingException {
    byte[] bytes = sstring.substring(at).getBytes("UTF-8");
    bytes[bytes.length - 1]++;
    sstring = sstring.substring(0, sstring.length() - 1) + (new String(bytes, "UTF-8"));
    return sstring;
  }

  public static String getZero(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append("0");
    }
    return sb.toString();
  }
}
