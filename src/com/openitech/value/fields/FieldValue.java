package com.openitech.value.fields;

import com.openitech.text.CaseInsensitiveString;
import com.openitech.util.Equals;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldValue extends Field {

  public FieldValue(Field field) {
    this(field.idPolja, field.name, field.type, field.fieldIndex, null);
  }

  public FieldValue(Field field, Object value) {
    this(field.idPolja, field.name, field.type, field.fieldIndex, value);
  }

  public FieldValue(String name, int type) {
    this(name, type, 1, null);
  }

  public FieldValue(String name, int type, Object value) {
    this(name, type, 1, value);
  }

  public FieldValue(String name, int type, int fieldValueIndex, Object value) {
    this(-1, name, type, fieldValueIndex, value);
  }

  public FieldValue(Integer idPolja, String name, int type, int fieldValueIndex, Object value) {
    super(idPolja, name, type, fieldValueIndex);
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

  public static FieldValue createFieldValue(final ResultSet source, final String fieldName, int fieldValueIndex, final String columnName, final java.util.Map<CaseInsensitiveString, Field> fields) throws SQLException {
    Field field = Field.getField(fieldName, fieldValueIndex, fields);
    fieldValueIndex = field.fieldIndex==1?fieldValueIndex:field.fieldIndex;
    return createFieldValue(source, new Field(field.idPolja, fieldName, field.type, fieldValueIndex), columnName);
  }
  
  public static FieldValue createFieldValue(final ResultSet source, final Field field, final String columnName) throws SQLException {
    FieldValue fieldValue = new FieldValue(field, source.getObject(columnName));
    if (source.wasNull()) {
      fieldValue.setValue(null);
    }
    return fieldValue;
  }

  @Override
  public String toString() {
    return name + ":" + fieldIndex + ":" + type + ":" + ValueType.getType(type) + ":" + value;
  }
}
