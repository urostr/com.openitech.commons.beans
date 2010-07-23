/*
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components;

import com.openitech.text.FormatFactory;
import java.util.Calendar;


/**
 *
 * @author uros
 */
public class JDbDatePickerTextField extends JDbFormattedTextField {
  
  /** Creates a new instance of JDbDateTextField */
  public JDbDatePickerTextField() {
    super();
    setFormat(FormatFactory.DATE_PICKER_FORMAT);
    setValue(getNextMonth());
  }
  
  public static java.sql.Date getNextMonth() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH,1);
    calendar.set(Calendar.HOUR,0);
    calendar.set(Calendar.MINUTE,0);
    calendar.set(Calendar.SECOND,0);
    
    return new java.sql.Date(calendar.getTimeInMillis());
  }
  
}
