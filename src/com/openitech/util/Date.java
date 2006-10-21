/*
 * Date.java
 *
 * Created on Sobota, 14 oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.util;

import java.util.Calendar;

/**
 *
 * @author uros
 */
public class Date {
  
  /** Creates a new instance of Date */
  private Date() {
  }
  
  public static int getMonth() {
    return Calendar.getInstance().get(Calendar.MONTH);
  }
  
  public static int getYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }
  
}
