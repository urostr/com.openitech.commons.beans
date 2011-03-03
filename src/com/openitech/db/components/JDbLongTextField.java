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

/**
 *
 * @author uros
 */
public class JDbLongTextField extends JDbFormattedTextField {


  /** Creates a new instance of JDbIntegerTextField */
  public JDbLongTextField() {
    super();
    setColumns(10);
    setFormat(FormatFactory.getLongNumberFormat());
  }

  public void setLength(int length) {
    setColumns(length + 1);
  }
}
