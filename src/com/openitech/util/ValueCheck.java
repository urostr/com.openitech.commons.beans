/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author domenbasic
 */
public class ValueCheck {

  public static boolean isNumeric(String string) {
    boolean result = false;
    // \\d=stevilka +=ponovitev sstevilke |=OR -=negativen predznak |=or +=+ d+=stevilke
    Pattern pattern = Pattern.compile("\\d+|-\\d+|\\+\\d+");
    Matcher matcher = pattern.matcher(string);
    result = matcher.matches();
    return result;
  }
}
