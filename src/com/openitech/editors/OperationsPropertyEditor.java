/*
 * OperationsPropertyEditor.java
 *
 * Created on Petek, 23 marec 2007, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.editors;

import com.openitech.db.components.JDbControlButton;


/**
 *
 * @author uros
 */
public class OperationsPropertyEditor extends EnumPropertyEditor<JDbControlButton.Operation> {
  
  /** Creates a new instance of OperationsPropertyEditor */
  public OperationsPropertyEditor() {
    super(JDbControlButton.Operation.class, false);
  }

  public String getJavaInitializationString() {
    String value = getAsText();
    return value!=null?"com.openitech.db.components.JDbControlButton.Operation."+value:"null";
  }
  
}
