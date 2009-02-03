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

  /**
   * Calculates the difference between two dates
   * @param from
   * @param to
   * @return
   */
  public static java.util.Map<DatePart, Integer> dateDifference(java.util.Date from, java.util.Date to) {
    if (from == null || to == null) {
      return null;
    }

    Calendar calendar_zacetek = Calendar.getInstance();
    calendar_zacetek.setTime(from);

    Calendar calendar_konec = Calendar.getInstance();
    calendar_konec.setTime(to);
    calendar_konec.add(Calendar.DAY_OF_MONTH, 1);

    int leto_konec = calendar_konec.get(Calendar.YEAR);
    int leta = leto_konec - calendar_zacetek.get(Calendar.YEAR);

    calendar_zacetek.set(Calendar.YEAR, leto_konec);

    int dnevi = 0;
    int meseci = 0;

    if (calendar_konec.getTimeInMillis() < calendar_zacetek.getTimeInMillis()) {
      leta--;
      calendar_zacetek.set(Calendar.YEAR, leto_konec - 1);
    }

    long l_zacetek = calendar_zacetek.getTimeInMillis();

    while (calendar_konec.getTimeInMillis() >= l_zacetek) {
      calendar_konec.add(Calendar.MONTH, -1);
      if (calendar_konec.getTimeInMillis() >= l_zacetek) {
        meseci++;
      }
    }
    calendar_konec.add(Calendar.MONTH, 1);

    while (calendar_konec.getTimeInMillis() > l_zacetek) {
      calendar_konec.add(Calendar.DAY_OF_MONTH, -1);
      if (calendar_konec.getTimeInMillis() >= l_zacetek) {
        dnevi++;
      }
    }

    java.util.Map<DatePart, Integer> result = new java.util.HashMap<DatePart, Integer>();

    result.put(DatePart.YEAR, leta);
    result.put(DatePart.MONTH, meseci);
    result.put(DatePart.DAY, dnevi);

    return result;
  }

  public enum DatePart {

    YEAR,
    MONTH,
    DAY
  }
}
