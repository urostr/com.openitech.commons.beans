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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.text;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author domenbasic
 */
public class SimpleTimestampFormater extends SimpleDateFormat{

  public SimpleTimestampFormater(String pattern, DateFormatSymbols formatSymbols) {
    super(pattern, formatSymbols);
  }

  public SimpleTimestampFormater(String pattern, Locale locale) {
    super(pattern, locale);
  }

  public SimpleTimestampFormater(String pattern) {
    super(pattern);
  }

  public SimpleTimestampFormater() {
    super();
  }

  @Override
  public Date parse(String source) throws ParseException {
    return new java.sql.Timestamp(super.parse(source).getTime());
  }






}
