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
import java.text.ParseException;
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
  private static final java.text.SimpleDateFormat dfsql = new java.text.SimpleDateFormat("yyyy-MM-dd");
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

  public final void setValue(String value) throws ParseException {
    DataColumn parsedValue = parseValue(value, nullable, type);

    this.values.clear();
    this.value = parsedValue.value;
    this.wasNull = parsedValue.wasNull;
    this.values.putAll(parsedValue.values);
  }

  public static DataColumn parseValue(String value, boolean nullable, Class type) throws ParseException {
    DataColumn result = new DataColumn(type, nullable);

    if (type.equals(java.lang.Double.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : 0;
        result.wasNull = true;
      } else {
        result.value = java.lang.Double.valueOf(nf.parse(value, new java.text.ParsePosition(0)).doubleValue());
      }
    } else if (type.equals(java.math.BigInteger.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : 0;
        result.wasNull = true;
      } else {
        result.value = new java.math.BigInteger(value);
      }
    } else if (type.equals(java.lang.Integer.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : 0;
        result.wasNull = true;
      } else {
        result.value = java.lang.Integer.valueOf(value);
      }
    } else if (type.equals(java.util.Date.class) || type.equals(java.sql.Date.class)) {
      if ((value.length() == 0) || value.equalsIgnoreCase("NaN") || value.equalsIgnoreCase("(null)")) {
        result.value = null;
      } else if (value.contains(".")) {
        result.value = new java.sql.Date(df.parse(value).getTime());
      } else if (value.contains("-")) {
        result.value = new java.sql.Date(dfsql.parse(value).getTime());
      } else {
        result.value = new java.sql.Date(dfplain.parse(value).getTime());
      }
    } else if (type.equals(java.lang.String.class)) {
      if (value.equalsIgnoreCase("(null)")) {
        result.value = nullable ? null : "";
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
