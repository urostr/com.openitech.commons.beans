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
public class JDbIntegerTextField extends JDbFormattedTextField {
  private boolean autosize=true;
  private int length=0;
  
  /** Creates a new instance of JDbIntegerTextField */
  public JDbIntegerTextField() {
    super();
    setFormat(FormatFactory.getIntegerNumberFormat(0));
  }
  
  /** Creates a new instance of JDbIntegerTextField */
  public JDbIntegerTextField(boolean autosize) {
    super();
    this.autosize=autosize;
    updateFormat();
  }

  /** Creates a new instance of JDbIntegerTextField */
  public JDbIntegerTextField(int length, boolean autosize) {
    super();
    this.autosize=autosize;
    this.length=length;
    updateFormat();
  }

  public void setAutosize(boolean autosize) {
    this.autosize = autosize;
  }
  
  public boolean isAutosize() {
    return autosize;
  }

  protected void updateFormat() {
    setFormat(FormatFactory.getIntegerNumberFormat(length));
    if (isAutosize())
      setColumns(length+1);
  }

  public void setLength(int length) {
    this.length=length;
    updateFormat();
  }

  public int getLength() {
    return length;
  }
  
}
