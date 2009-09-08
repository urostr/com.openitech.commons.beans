/*
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

/**
 *
 * @author uros
 */
public class JDbYearPickerTextField extends JDbFormattedTextField {

  public static final java.text.DateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyy");

  /** Creates a new instance of JDbDateTextField */
  public JDbYearPickerTextField() {
    super();
    setFormat(DATE_FORMAT);
    setColumns(4);
  }
}
