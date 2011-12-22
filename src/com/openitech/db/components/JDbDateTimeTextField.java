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
import java.util.Calendar;

/**
 *
 * @author uros
 */
public class JDbDateTimeTextField extends JDbFormattedTextField {

  /** Creates a new instance of JDbDateTextField */
  public JDbDateTimeTextField() {
    super();
    setFormat(FormatFactory.DATETIME_FORMAT);
    setColumns(15);
  }

  private boolean isADataPickerSetCall() {
    boolean result = false;

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    for (int i = 1; i < stackTrace.length && i < 4; i++) {
      StackTraceElement element = stackTrace[i];
      if (element.getClassName().contains("DatePicker") && element.getMethodName().equals("updateEditorValue")) { //NOI18N
        result = true;
        break;
      }
    }


    return result;
  }
  
  private boolean updatingValue = false;

  @Override
  public void setValue(Object value) {
    if (!updatingValue) {
      try {
        updatingValue = true;

        if (isADataPickerSetCall()) {
          if (value instanceof java.util.Date && getValue() != null) {
            Calendar calendar_old = Calendar.getInstance();
            calendar_old.setTime((java.util.Date) getValue());

            Calendar calendar_new = Calendar.getInstance();
            calendar_new.setTime((java.util.Date) value);

            calendar_new.set(Calendar.HOUR_OF_DAY, calendar_old.get(Calendar.HOUR_OF_DAY));
            calendar_new.set(Calendar.MINUTE, calendar_old.get(Calendar.MINUTE));
            calendar_new.set(Calendar.SECOND, calendar_old.get(Calendar.SECOND));

            value = calendar_new.getTime();
          }
        }

        super.setValue(value);
      } finally {
        updatingValue = false;
      }
    }
  }
}
