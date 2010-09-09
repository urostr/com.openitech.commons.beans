/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.text;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author domenbasic
 */
public class SimpleTimestampFormater extends SimpleDateFormat{

  public SimpleTimestampFormater(String pattern, DateFormatSymbols formatSymbols) {
    super(pattern, formatSymbols);
  }

  public SimpleTimestampFormater(String pattern, Locale locale) {
    super(pattern, locale);
  }

  public SimpleTimestampFormater(String pattern) {
    super(pattern);
  }

  public SimpleTimestampFormater() {
    super();
  }

  @Override
  public Date parse(String source) throws ParseException {
    return new java.sql.Timestamp(super.parse(source).getTime());
  }






}
