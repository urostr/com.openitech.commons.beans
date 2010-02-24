package com.openitech.sql;

import com.openitech.util.Equals;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FieldValue extends Field {

  public FieldValue(Field field) {
    this(field.name, field.type, field.fieldIndex, null);
  }

  public FieldValue(Field field, Object value) {
    this(field.name, field.type, field.fieldIndex, value);
  }

  public FieldValue(String name, int type) {
    this(name, type, 1, null);
  }

  public FieldValue(String name, int type, int fieldValueIndex) {
    this(name, type, fieldValueIndex, null);
  }

  public FieldValue(String name, int type, Object value) {
    this(name, type, 1, value);
  }

  public FieldValue(String name, int type, int fieldValueIndex, Object value) {
    super(name, type, fieldValueIndex);
    this.value = value;
  }
  private Object value;

  /**
   * Get the value of value
   *
   * @return the value of value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Set the value of value
   *
   * @param value new value of value
   */
  public void setValue(Object value) {
    if (!Equals.equals(this.value, value)) {
      Object oldValue = this.value;
      this.value = value;
      propertyChangeSupport.firePropertyChange("value", oldValue, value);
    }
  }

  public boolean isNull() {
    return value == null;
  }
  private boolean logAlways;

  /**
   * Get the value of logAlways
   *
   * @return the value of logAlways
   */
  public boolean isLogAlways() {
    return logAlways;
  }

  /**
   * Set the value of logAlways
   *
   * @param logAlways new value of logAlways
   */
  public void setLogAlways(boolean logAlways) {
    this.logAlways = logAlways;
  }

  public ValueType getValueType() {
    return ValueType.getType(type, value);
  }

  public static FieldValue createFieldValue(ResultSet source, Field field, String columnName) throws SQLException {
    FieldValue fieldValue = new FieldValue(field, source.getObject(columnName));
    if (source.wasNull()) {
      fieldValue.setValue(null);
    }
    return fieldValue;
  }

  @Override
  public String toString() {
    return name + ":" + type + ":" + ValueType.getType(type) + ":" + value;
  }
}
