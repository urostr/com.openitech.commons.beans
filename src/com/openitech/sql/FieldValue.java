package com.openitech.sql;

import com.openitech.util.Equals;
import java.sql.Types;

public class FieldValue extends Field {


  public FieldValue(Field field) {
    this(field.name, field.type);
  }

  public FieldValue(Field field, Object value) {
    this(field.name, field.type, value);
  }

  public FieldValue(String name, int type) {
    super(name, type);
  }

  public FieldValue(String name, int type, Object value) {
    super(name, type);
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

  /**
   *
   * @author uros
   */
  public static enum ValueType {

    IntValue {

      @Override
      public int getTypeIndex() {
        return 1;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.INTEGER;
      }
    },
    RealValue {

      @Override
      public int getTypeIndex() {
        return 2;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.DOUBLE;
      }
    },
    StringValue {

      @Override
      public int getTypeIndex() {
        return 3;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.VARCHAR;
      }
    },
    DateValue {

      @Override
      public int getTypeIndex() {
        return 4;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.DATE;
      }

    },
    ObjectValue {

      @Override
      public int getTypeIndex() {
        return 5;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.BLOB;
      }
    },
    ClobValue {

      @Override
      public int getTypeIndex() {
        return 6;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.CLOB;
      }


    },
    BitValue {

      @Override
      public int getTypeIndex() {
        return 7;
      }

      @Override
      public int getSqlType() {
        return java.sql.Types.BOOLEAN;
      }


    };

    public abstract int getTypeIndex();
    public abstract int getSqlType();

    public static ValueType valueOf(int valueType) {
      ValueType result = null;
      for (ValueType vt:values()) {
        if (vt.getTypeIndex()==valueType) {
          result = vt;
        }
      }
      return result;
    }

    public static ValueType getType(int type) {
      return getType(type, null);
    }

    public static ValueType getType(int type, Object value) {
      ValueType result = null;
      switch (type) {
        case Types.BIT:
        case Types.BOOLEAN:
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.BIGINT:
        case Types.INTEGER:
          result = IntValue;
          break;
        case Types.FLOAT:
        case Types.REAL:
        case Types.DOUBLE:
        case Types.DECIMAL:
        case Types.NUMERIC:
          result = RealValue;
          break;
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
        case Types.NCHAR:
        case Types.NVARCHAR:
          if (value != null && value.toString().length() > 108) {
            result = ClobValue;
          } else {
            result = StringValue;
          }
          break;
        case Types.CLOB:
        case Types.NCLOB:
          result = ClobValue;
          break;
        case Types.DATE:
        case Types.TIME:
        case Types.TIMESTAMP:
          result = DateValue;
          break;
        default:
          result = ObjectValue;
      }
      return result;
    }
  }
}
