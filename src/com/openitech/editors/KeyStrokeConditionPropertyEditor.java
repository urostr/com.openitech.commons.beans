/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * KeyStrokeConditionPropertyEditor.java
 *
 * Created on Petek, 23 marec 2007, 13:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.editors;


/**
 *
 * @author uros
 */
public class KeyStrokeConditionPropertyEditor extends EnumPropertyEditor<KeyStrokeConditionPropertyEditor.Conditions> {
  
  /** Creates a new instance of KeyStrokeConditionPropertyEditor */
  public KeyStrokeConditionPropertyEditor() {
    super(KeyStrokeConditionPropertyEditor.Conditions.class, false);
  }

  public String getJavaInitializationString() {
    return "javax.swing.JComponent."+getAsText();
  }

  public void setAsText(String text) throws IllegalArgumentException {
      if (!((text==null)||(text.equals(NULL)))) {
        setValue(Conditions.valueOf(text).intValue());
      } else
        setValue(Conditions.UNDEFINED_CONDITION.intValue());
  }

  public String getAsText() {
    Object value = getValue();
    return (value!=null)?Conditions.valueOf(((Integer) value).intValue()).toString():Conditions.UNDEFINED_CONDITION.toString();
  }
  
  public static enum Conditions {
    UNDEFINED_CONDITION {
      public int intValue() {
        return javax.swing.JComponent.UNDEFINED_CONDITION;
      }
    }, 
    WHEN_FOCUSED {
      public int intValue() {
        return javax.swing.JComponent.WHEN_FOCUSED;
      }
    }, 
    WHEN_ANCESTOR_OF_FOCUSED_COMPONENT {
      public int intValue() {
        return javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
      }
    }, 
    WHEN_IN_FOCUSED_WINDOW {
      public int intValue() {
        return javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
      }
    };
    
    public abstract int intValue();
    
    public static Conditions valueOf(int condition) {
      Conditions result = null;
      switch (condition) {
        case javax.swing.JComponent.UNDEFINED_CONDITION:
          result = UNDEFINED_CONDITION; break;
        case javax.swing.JComponent.WHEN_FOCUSED:
          result = WHEN_FOCUSED; break;
        case javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
          result = WHEN_ANCESTOR_OF_FOCUSED_COMPONENT; break;
        case javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW:
          result = WHEN_IN_FOCUSED_WINDOW; break;
      }
      
      return result;
    }
  }
  
}
