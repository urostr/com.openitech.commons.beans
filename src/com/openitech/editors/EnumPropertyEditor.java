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
 * EnumPropertyEditor.java
 *
 * Created on Petek, 23 marec 2007, 2:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.editors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.jdesktop.swingx.painter.RectanglePainter;

/**
 *
 * @author joshy
 */
public abstract class EnumPropertyEditor<E extends Enum<E>> extends PropertyEditorSupport {
  public static final String NULL="[not defined]";
  
  private boolean addNull = true;
  
    private Class<E> en;
    private EnumSet<E> set;
    /** Creates a new instance of EnumPropertyEditor */
    public EnumPropertyEditor(Class<E> en) {
        this.en = en;
        set = EnumSet.allOf(en);
    }
    
    public EnumPropertyEditor(Class<E> en, boolean addNull) {
        this.en = en;
        this.addNull = addNull;
        set = EnumSet.allOf(en);
    }
    
    public String[] getTags() {
        List<String> strs = new ArrayList<String>();
        if (addNull)
          strs.add(NULL);
        for(E e : set) {
            strs.add(e.toString());
        }
        return strs.toArray(new String[0]);
    }
    
    public String getAsText() {
      Object value = getValue();
      return (value!=null)?getValue().toString():null;
    }
    
    public void setAsText(String text) throws IllegalArgumentException {
      if (!((text==null)||(text.equals(NULL)))) {
        Enum<E> e = Enum.valueOf(en, text);
        setValue(e);
      } else
        setValue(null);
    }

    public abstract String getJavaInitializationString();
}
