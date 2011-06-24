/*
 * FormatFactory.java
 *
 * Created on April 21, 2006, 5:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author tomaz
 */
public class FormatFactory {

  public static final DateFormat TIME_FORMAT = new SimpleTimestampFormater("HH:mm:ss");
  public static final DateFormat DATETIME_FORMAT = new SimpleTimestampFormater("dd.MM.yyyy HH:mm:ss");
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
  public static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("d.M.yy");
  public static final DateFormat DATE_PICKER_FORMAT = new SimpleDateFormat("EEE dd.MM.yyyy");
  public static final DateFormat JDBC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final DecimalFormatSymbols sl_SI_DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(new Locale("sl", "SI"));

  /** Creates a new instance of FormatFactory */
  private FormatFactory() {
  }

  public static NumberFormat getIntegerNumberFormat(int length) {
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    if (length > 0) {
      nf.setMaximumIntegerDigits(length);
    }
    return nf;
  }

  public static NumberFormat getLongNumberFormat() {
    NumberFormat nf = NumberFormat.getNumberInstance();
    nf.setGroupingUsed(false);
    nf.setMaximumFractionDigits(0);

    return nf;
  }

  public static NumberFormat getDecimalNumberFormat(int length, int decimals) {
    DecimalFormat nf = new DecimalFormat("#,##0.00", sl_SI_DECIMAL_FORMAT_SYMBOLS);
    //nf.setMaximumIntegerDigits(length);
    nf.setMaximumFractionDigits(decimals);
    nf.setDecimalSeparatorAlwaysShown(true);
    return nf;
  }

  public static NumberFormat getPercentageNumberFormat(int length, int decimals) {
    DecimalFormat nf = new DecimalFormat("#,##0.00%", sl_SI_DECIMAL_FORMAT_SYMBOLS);
    //nf.setMaximumIntegerDigits(length);
    nf.setMaximumFractionDigits(decimals);
    nf.setDecimalSeparatorAlwaysShown(true);
    return nf;
  }

  public static DateFormat getDateFormat() {
    return DATE_FORMAT;
  }

  public static JFormattedTextField.AbstractFormatterFactory getFormatFactory(Format format) {
    JFormattedTextField.AbstractFormatterFactory af;
    if (format instanceof DateFormat) {
      af = new DefaultFormatterFactory(new DateFormatter((DateFormat) format));
    } else if (format instanceof NumberFormat) {
      af = new DefaultFormatterFactory(new NumberFormatter(
              (NumberFormat) format));
    } else if (format instanceof Format) {
      af = new DefaultFormatterFactory(new InternationalFormatter(
              (Format) format));
    } else {
      af = new DefaultFormatterFactory(new DefaultFormatter());
    }
    return af;
  }
}
