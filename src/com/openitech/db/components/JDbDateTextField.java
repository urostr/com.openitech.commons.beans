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
public class JDbDateTextField extends JDbFormattedTextField {
  
  /** Creates a new instance of JDbDateTextField */
  public JDbDateTextField() {
    super();
    setFormat(FormatFactory.DATE_FORMAT);
    setColumns(10);
  }
  
}
