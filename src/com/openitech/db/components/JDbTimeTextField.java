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

  @Override
  public String getText() {
    String text = super.getText();

    if (text != null) {
      StringBuilder sb = new StringBuilder(text);
      switch (text.length()) {
        case 1:
          text = sb.insert(0, "0").append(":00:00").toString();
          break;
        case 2:
          text = sb.append(":00:00").toString();
          break;
        case 3:
          if (sb.indexOf(":") < 0) {
            text = sb.insert(0, "0").insert(2, ":").append(":00").toString();
          } else {
            text = sb.append(":00").toString();
          }
          ;
          break;
        case 4:
          text = sb.insert(2, sb.indexOf(":") < 0 ? ":" : "").append(":00").toString();
          break;
        case 5:
          text = sb.append(":00").toString();
          break;
      }
    }
    return text;
  }

  @Override
  public Object getValue() {
    Object value = super.getValue();
    if (value instanceof java.sql.Timestamp) {
    } else if (value instanceof java.util.Date) {
      value = new java.sql.Timestamp(((java.util.Date) value).getTime());
    }
    return value;
  }
}
