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
public class JDbDecimalTextField extends JDbIntegerTextField {
  private int decimals=2;
  
  /** Creates a new instance of JDbDecimalTextField */
  public JDbDecimalTextField() {
    super();
    updateFormat();
  }
  
  /** Creates a new instance of JDbIntegerTextField */
  public JDbDecimalTextField(boolean autosize) {
    super(autosize);
    updateFormat();
  }

  /** Creates a new instance of JDbIntegerTextField */
  public JDbDecimalTextField(int length, boolean autosize) {
    super(length,autosize);
  }

  /** Creates a new instance of JDbIntegerTextField */
  public JDbDecimalTextField(int length, int decimals, boolean autosize) {
    super(autosize);
    this.decimals=decimals;
    setLength(length);
  }

  protected void updateFormat() {
    setFormat(FormatFactory.getDecimalNumberFormat(getLength(),decimals));
    if (isAutosize())
      setColumns(getLength()+decimals+1+(getLength()/1000));
  }

  public void setDecimals(int decimals) {
    this.decimals = decimals;
    updateFormat();
  }

  public int getDecimals() {
    return decimals;
  }
  
}
