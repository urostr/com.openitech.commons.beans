package com.openitech.value.fields;

import com.openitech.text.CaseInsensitiveString;
import com.openitech.util.Equals;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

public class FieldValue extends Field implements Cloneable{

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
    this(null, name, type, fieldValueIndex, value);
  }

  public FieldValue(Integer idPolja, String name, int type, int fieldValueIndex, Object value) {
    this(idPolja, name, type, fieldValueIndex, value, null);
  }

  public FieldValue(Integer idPolja, String name, int type, int fieldValueIndex, Object value, Long valueId) {
    super(idPolja, name, type, fieldValueIndex);
    this.value = value;
    this.valueId = valueId;
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
    setValue(value, null);
  }

  /**
   * Set the value of value
   *
   * @param value new value of value
   */
  public void setValue(Object value, Long valueId) {
    if (!Equals.equals(this.value, value)) {
      Object oldValue = this.value;
      this.value = value;
      this.valueId = valueId;

      propertyChangeSupport.firePropertyChange("value", oldValue, value);
    }
  }
  Long valueId;

  public Long getValueId() {
    return valueId;
  }

  public void setValueId(Long valueId) {
    this.valueId = valueId;
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
    fieldValueIndex = field.fieldIndex == 1 ? fieldValueIndex : field.fieldIndex;
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
    return name + ":" + fieldIndex + ":" + type + ":" + ValueType.getType(type) + (lookupType != null ? (":" + lookupType.toString()) : "") + ":" + value;
  }

  @Override
  public FieldValue clone() {
    FieldValue result = new FieldValue(name, type, fieldIndex, value);
    result.setLookupType(lookupType);
    result.setLogAlways(logAlways);
    result.setIdPolja(idPolja);
    result.setOpis(opis);
    result.setValueId(valueId);
    return result;
  }


  public com.openitech.sql.events.xml.Field getField() throws DatatypeConfigurationException, IOException {
    final ValueType valueType = ValueType.getType(type);

    com.openitech.sql.events.xml.Field result = new com.openitech.sql.events.xml.Field();

    result.setFieldId(idPolja);
    result.setValueId(valueId);
    result.setFieldName(name);
    result.setFieldValueIndex(fieldIndex);
    result.setFieldType(valueType.getTypeIndex());
    
    if (value != null) {

      switch (valueType) {
        case BitValue:
          if (value instanceof Boolean) {
            result.setBitValue((Boolean) value);
          } else if (value instanceof Number) {
            result.setBitValue(((Number) value).doubleValue() > 0);
          } else {
            result.setBitValue(Boolean.valueOf(value.toString()));
          }
          break;
        case IntValue:
          if (value instanceof Number) {
            result.setIntValue(((Number) value).intValue());
          } else {
            result.setIntValue(Integer.parseInt(value.toString()));
          }
          break;
        case LongValue:
          if (value instanceof Number) {
            result.setLongValue(((Number) value).longValue());
          } else {
            result.setLongValue(Long.parseLong(value.toString()));
          }
          break;
        case RealValue:
          if (value instanceof Number) {
            result.setRealValue(((Number) value).floatValue());
          } else {
            result.setRealValue(Float.parseFloat(value.toString()));
          }
          break;
        case MonthValue:
        case DateTimeValue:
        case TimeValue:
        case DateValue:
          GregorianCalendar calendar = new GregorianCalendar();
          if (value instanceof Date) {
            calendar.setGregorianChange((Date) value);
          } else if (value instanceof Number) {
            calendar.setTimeInMillis(((Number) value).longValue());
          }
          final XMLGregorianCalendar xmlGregorianCalendar = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

          switch (valueType) {
            case MonthValue:
              result.setMonthValue(xmlGregorianCalendar); break;
            case DateTimeValue:
              result.setDateTimeValue(xmlGregorianCalendar); break;
            case TimeValue:
              result.setTimeValue(xmlGregorianCalendar); break;
            case DateValue:
              result.setDateValue(xmlGregorianCalendar);
              break;
          }
          break;
        case StringValue:
          result.setStringValue(value.toString());
          break;
        case ClobValue:
          result.setClobValue(value.toString());
          break;
        case BlobValue:
        case ObjectValue:
          if (value instanceof byte[]) {
            result.setObjectValue((byte[]) value);
          } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(value);
            result.setObjectValue(bos.toByteArray());
          }

          break;
      }
    }


    return result;
  }
}
