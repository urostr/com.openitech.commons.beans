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
import java.util.Calendar;


/**
 *
 * @author uros
 */
public class JDbDatePickerTextField extends JDbFormattedTextField {
  
  /** Creates a new instance of JDbDateTextField */
  public JDbDatePickerTextField() {
    super();
    setFormat(FormatFactory.DATE_PICKER_FORMAT);
    setValue(getNextMonth());
  }
  
  public static java.sql.Date getNextMonth() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH,1);
    calendar.set(Calendar.HOUR,0);
    calendar.set(Calendar.MINUTE,0);
    calendar.set(Calendar.SECOND,0);
    
    return new java.sql.Date(calendar.getTimeInMillis());
  }
  
}
