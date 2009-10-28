/*
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author uros
 */
public class JDbMonthPickerTextField extends JDbFormattedTextField {

  public static final java.text.DateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyyMM");
  private JXDatePicker jxDatePicker;

  /** Creates a new instance of JDbDateTextField */
  public JDbMonthPickerTextField() {
    super();
    setFormat(DATE_FORMAT);
    setColumns(6);
  }

  @Override
  public void setValue(Object value) {
    if (value != null) {
      super.setValue(value);
      if (value instanceof java.util.Date && jxDatePicker != null) {
        jxDatePicker.setDate((java.util.Date) value);
      }
    }
  }

  /**
   * Get the value of jxDatePicker
   *
   * @return the value of jxDatePicker
   */
  public JXDatePicker getJxDatePicker() {
    return jxDatePicker;
  }

  /**
   * Set the value of jxDatePicker
   *
   * @param jxDatePicker new value of jxDatePicker
   */
  public void setJxDatePicker(JXDatePicker jxDatePicker) {
    this.jxDatePicker = jxDatePicker;
  }
}
