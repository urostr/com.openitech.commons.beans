/*
 * FormatFactory.java
 *
 * Created on April 21, 2006, 5:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.formats;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author tomaz
 */
public class FormatFactory {
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
  public static final DateFormat JDBC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final DecimalFormatSymbols sl_SI_DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(new Locale("sl","SI"));
    
    /** Creates a new instance of FormatFactory */
    private FormatFactory() {
    }
    
  public static NumberFormat getIntegerNumberFormat(int length) {
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    if (length>0) {
      nf.setMaximumIntegerDigits(length);
    }
    return nf;
  }

  public static NumberFormat getDecimalNumberFormat(int length, int decimals) {
    DecimalFormat nf = new DecimalFormat("#,##0.00", sl_SI_DECIMAL_FORMAT_SYMBOLS);
    //nf.setMaximumIntegerDigits(length);
    nf.setMaximumFractionDigits(decimals);
    nf.setDecimalSeparatorAlwaysShown(true);
    return nf;
  }
  
  public static DateFormat getDateFormat() {
    return DATE_FORMAT;
  }
    
    
}
