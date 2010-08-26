/*
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.text.FormatFactory;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

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

  @Override
  public String getText() {
    String text = super.getText();

    if (text != null) {
      StringBuilder sb = new StringBuilder(text);
      if (text.length() == 1) {
        text = sb.insert(0, "0").append(":00:00").toString();
      } else if (text.length() == 2) {
        text = sb.append(":00:00").toString();
      } else if (text.length() == 3) {
        if (sb.indexOf(":") < 0) {
          text = sb.insert(0, "0").insert(2, ":").append(":00").toString();
        }
      } else if (text.length() == 4) {
        if (sb.indexOf(":") < 0) {
          text = sb.insert(2, ":").append(":00").toString();
        }
      } else if (text.length() == 5) {
        text = sb.append(":00").toString();
      }
    }
    return text;
  }
  
}
