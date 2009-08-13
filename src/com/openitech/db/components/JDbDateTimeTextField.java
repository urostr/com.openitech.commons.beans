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
public class JDbDateTimeTextField extends JDbFormattedTextField {
  
  /** Creates a new instance of JDbDateTextField */
  public JDbDateTimeTextField() {
    super();
    setFormat(FormatFactory.DATETIME_FORMAT);
    setColumns(12);
  }
  
}
