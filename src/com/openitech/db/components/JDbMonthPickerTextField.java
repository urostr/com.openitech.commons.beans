/*
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components;

import com.openitech.formats.FormatFactory;


/**
 *
 * @author uros
 */
public class JDbMonthPickerTextField extends JDbFormattedTextField {
  public static final java.text.DateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyyMM");
  
  /** Creates a new instance of JDbDateTextField */
  public JDbMonthPickerTextField() {
    super();
    setFormat(DATE_FORMAT);
    setColumns(6);
  }
  
}
