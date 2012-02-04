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
 * DataColumn.java
 *
 * Created on Sreda, 10 september 2008, 12:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.value.fields.Field;
import com.openitech.value.fields.FieldValue;
import com.openitech.value.fields.ValueType;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author uros
 */
public class DataColumn {

  /** Creates a new instance of DataColumn */
  private static final java.text.DecimalFormatSymbols sl_SI_DECIMAL_FORMAT_SYMBOLS = new java.text.DecimalFormatSymbols(new java.util.Locale("sl", "SI"));
  private static final java.text.DecimalFormat nf = new java.text.DecimalFormat("#,###.######", sl_SI_DECIMAL_FORMAT_SYMBOLS);
  private static final java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("d.M.yyyy");
  private static final java.text.SimpleDateFormat dfts = new java.text.SimpleDateFormat("d.M.yyyy hh:mm");
  private static final java.text.SimpleDateFormat dftss = new java.text.SimpleDateFormat("d.M.yyyy hh:mm:ss");
  private static final java.text.SimpleDateFormat dfEN = new java.text.SimpleDateFormat("d/M/y");
  private static final java.text.SimpleDateFormat dfENts = new java.text.SimpleDateFormat("d/M/y hh:mm");
  private static final java.text.SimpleDateFormat dfENtss = new java.text.SimpleDateFormat("d/M/y hh:mm:ss");
  private static final java.text.SimpleDateFormat dfsql = new java.text.SimpleDateFormat("yyyy-MM-dd");
  private static final java.text.SimpleDateFormat dfsqlts = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm");
  private static final java.text.SimpleDateFormat dfsqltss = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  private static final java.text.SimpleDateFormat dfsqltsss = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
  private static final java.text.SimpleDateFormat dfplaints = new java.text.SimpleDateFormat("yyyyMMddhhmm");
  private static final java.text.SimpleDateFormat dfplaintss = new java.text.SimpleDateFormat("yyyyMMddhhmmdd");
  private static final java.text.SimpleDateFormat dfplain = new java.text.SimpleDateFormat("yyyyMMdd");
  private java.lang.Class type;
  private Object value;
  private boolean nullable = true;
  private boolean optional = false;
  private boolean wasNull = false;
  private Map<java.lang.Class, Object> values = new HashMap<Class, Object>();

  protected DataColumn(java.lang.Class type) {
    this.type = type;
  }

  protected DataColumn(java.lang.Class type, boolean nullable) {
    this.type = type;
    this.nullable = nullable;
  }

  protected DataColumn(java.lang.Class type, boolean nullable, boolean optional) {
    this.type = type;
    this.nullable = nullable;
    this.optional = optional;
  }

  public void setValue(double value) {
    this.values.clear();
    if (type.equals(java.lang.Double.class)) {
      this.value = java.lang.Double.valueOf(value);
      this.values.put(java.lang.Double.class, this.value);
    } else {
      this.value = java.lang.Double.toString(value);
      this.values.put(java.lang.String.class, this.value);
    }
  }

  public DataColumn() throws ParseException {
    this(java.lang.String.class);
  }
  
  public DataColumn(String value) throws ParseException {
    this(java.lang.String.class);
    setValue(value);
  }

  public DataColumn(Field field) {
    this(ValueType.getType(field.getType()).getSqlClass());

    this.field = field;
  }

  public boolean wasNull() {
    return wasNull;
  }

  public final void setValue(Object value, Class type) throws ParseException {

    this.values.clear();
    this.value = value;
    this.wasNull = value == null;
    this.values.put(type, value);
  }

  public final void setValue(String value) throws ParseException {
    DataColumn parsedValue = parseValue(value, nullable, type);

    this.values.clear();
    this.value = parsedValue.value;
    this.wasNull = parsedValue.wasNull;
    this.values.putAll(parsedValue.values);
  }

  private static Object getDefualtValue(boolean nullable, Class type) {

    if (nullable) {
      Object result;
      if (type.equals(java.lang.Double.class)) {
        result = 0;
      } else if (type.equals(java.math.BigInteger.class)) {
        result = 0;
      } else if (type.equals(java.lang.Integer.class) || type.equals(java.lang.Number.class)) {
        result = 0;
      } else if (type.equals(java.util.Date.class) || type.equals(java.sql.Date.class) || type.equals(java.sql.Timestamp.class)) {
        result = Calendar.getInstance().getTime();
      } else if (type.equals(java.lang.String.class)) {
        result = "";
      } else {
        throw new IllegalArgumentException("Unknown default value for " + type.getName());
      }

      return result;
    } else {
      throw new IllegalArgumentException("Value cannot be null");
    }
  }

  public static DataColumn parseValue(String value, boolean nullable, Class type) throws ParseException {
    DataColumn result = new DataColumn(type, nullable);

    if (type.equals(java.lang.Double.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
        result.wasNull = true;
      } else {
        result.value = java.lang.Double.valueOf(nf.parse(value, new java.text.ParsePosition(0)).doubleValue());
      }
    } else if (type.equals(java.math.BigInteger.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
        result.wasNull = true;
      } else {
        result.value = new java.math.BigInteger(value);
      }
    } else if (type.equals(java.lang.Integer.class) || type.equals(java.lang.Number.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
        result.wasNull = true;
      } else {
        result.value = java.lang.Integer.valueOf(value);
      }
    } else if (type.equals(java.sql.Timestamp.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
      } else if (value.contains(".")) {
        result.value = new java.sql.Timestamp(parseDate(value, dftss, dfts, df).getTime());
      } else if (value.contains("-")) {
        result.value = new java.sql.Timestamp(parseDate(value, dfsqltsss, dfsqltss, dfsqlts, dfsql).getTime());
      } else if (value.contains("/")) {
        result.value = new java.sql.Timestamp(parseDate(value, dfENtss, dfENts, dfEN).getTime());
      } else {
        result.value = new java.sql.Timestamp(parseDate(value, dfplaintss, dfplaints, dfplain).getTime());
      }
    } else if (type.equals(java.util.Date.class) || type.equals(java.sql.Date.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
      } else if (value.contains(".")) {
        result.value = new java.sql.Date(df.parse(value).getTime());
      } else if (value.contains("-")) {
        result.value = new java.sql.Date(dfsql.parse(value).getTime());
      } else {
        result.value = new java.sql.Date(dfplain.parse(value).getTime());
      }
    } else if (type.equals(java.lang.String.class)) {
      if (value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : getDefualtValue(nullable, type);
        result.wasNull = true;
      } else {
        result.value = value;
      }
    } else if (type.isInstance(value)) {
      result.value = value;
    } else {
      throw new IllegalArgumentException();
    }

    if (value == null) {
      result.wasNull = true;
    }

    result.values.put(result.type, result.value);

    return result;
  }
  
  private static java.util.Date parseDate(String value, DateFormat... validFormats) {
    java.util.Date result = null;
    
    for (DateFormat dateFormat : validFormats) {
      try {
        result = dateFormat.parse(value);
        break;
      } catch (ParseException ex) {
        result = null;
      }
    }
    
    return result;
  }
  
  private Field field = null;

  /**
   * Get the value of field
   *
   * @return the value of field
   */
  public Field getField() {
    return field;
  }

  /**
   * Set the value of field
   *
   * @param field new value of field
   */
  public void setField(Field field) {
    this.field = field;
  }

  public FieldValue getFieldValue() {
    if (field != null) {
      return new FieldValue(field, getValue());
    } else {
      throw new IllegalStateException("Field is not set");
    }
  }

  public void reset() {
    value = null;
  }

  public Object getValue() {
    return value;
  }

  public Object getValue(Class type) throws ParseException {
    if (values.containsKey(type)) {
      return values.get(type);
    } else {
      DataColumn parseValue = parseValue(this.wasNull || this.value == null ? null : this.value.toString(), nullable, type);

      values.put(type, parseValue.value);

      return parseValue.value;
    }
  }

  public boolean isOptional() {
    return this.optional;
  }

  public Class getType() {
    return type;
  }
}
