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
public class JDbTimeTextField extends JDbFormattedTextField {
  
  /** Creates a new instance of JDbDateTextField */
  public JDbTimeTextField() {
    super();
    setFormat(FormatFactory.TIME_FORMAT);
    setColumns(8);
  }
  
}
