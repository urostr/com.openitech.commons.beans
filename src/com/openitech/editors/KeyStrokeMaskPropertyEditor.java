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

import java.util.EnumSet;

/**
 *
 * @author uros
 */
public class KeyStrokeMaskPropertyEditor extends EnumPropertyEditor<KeyStrokeMaskPropertyEditor.Masks> {
  
  /** Creates a new instance of KeyStrokeConditionPropertyEditor */
  public KeyStrokeMaskPropertyEditor() {
    super(KeyStrokeMaskPropertyEditor.Masks.class, false);
  }

  public String getJavaInitializationString() {
    Object value = getValue();
    return value!=null?value.toString():"0";
  }

  public void setAsText(String text) throws IllegalArgumentException {
      if (!((text==null)||(text.equals(NULL)))) {
        setValue(Masks.valueOf(text).intValue());
      } else
        setValue(Masks.NO_MASK.intValue());
  }

  public String getAsText() {
    Object value = getValue();
    return (value!=null)?Masks.valueOf(((Integer) value).intValue()).toString():Masks.NO_MASK.toString();
  }
  
  /**
   * The modifiers consist of any combination of:<ul>
   * <li>java.awt.event.InputEvent.SHIFT_MASK (1)
   * <li>java.awt.event.InputEvent.CTRL_MASK (2)
   * <li>java.awt.event.InputEvent.META_MASK (4)
   * <li>java.awt.event.InputEvent.ALT_MASK (8)
   * </ul>
   * Since these numbers are all different powers of two, any combination of
   * them is an integer in which each bit represents a different modifier
   * key. Use 0 to specify no modifiers.
   */
  public static enum Masks {
    NO_MASK {
      public int intValue() {
        return 0;
      }
    }, 
    SHIFT_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.SHIFT_MASK;
      }
    }, 
    CTRL_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.CTRL_MASK;
      }
    }, 
    ALT_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.ALT_MASK;
      }
    },
    CTRL_SHIFT_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.SHIFT_MASK|java.awt.event.InputEvent.CTRL_MASK;
      }
    },
    CTRL_ALT_SHIFT_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.SHIFT_MASK|java.awt.event.InputEvent.CTRL_MASK|java.awt.event.InputEvent.ALT_MASK;
      }
    },
    ALT_CTRL_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.CTRL_MASK|java.awt.event.InputEvent.ALT_MASK;
      }
    },
    ALT_SHIFT_MASK {
      public int intValue() {
        return java.awt.event.InputEvent.SHIFT_MASK|java.awt.event.InputEvent.ALT_MASK;
      }
    };
    
    public abstract int intValue();
    
    private static final EnumSet<Masks> set = EnumSet.allOf(Masks.class);
    
    public static Masks valueOf(int condition) {
      Masks result = null;
      for (Masks mask:set) {
        if (mask.intValue()==condition) {
          result = mask; break;
        }
      }
      
      return result;
    }
  }
  
}
