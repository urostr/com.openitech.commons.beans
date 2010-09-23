package com.openitech.value.fields;

import java.sql.Types;

/**
 *
 * @author uros
 */
public enum ValueType {

  IntValue {

    @Override
    public int getTypeIndex() {
      return 1;
    }

    @Override
    public int getSqlType() {
      return Types.INTEGER;
    }
  }, RealValue {

    @Override
    public int getTypeIndex() {
      return 2;
    }

    @Override
    public int getSqlType() {
      return Types.DOUBLE;
    }
  }, StringValue {

    @Override
    public int getTypeIndex() {
      return 3;
    }


    @Override
    public int getSqlType() {
      return Types.VARCHAR;
    }
  }, DateValue {

    @Override
    public int getTypeIndex() {
      return 4;
    }


    @Override
    public int getSqlType() {
      return Types.DATE;
    }
  }, ObjectValue {

    @Override
    public int getTypeIndex() {
      return 5;
    }

    @Override
    public int getSqlType() {
      return Types.BLOB;
    }
  }, ClobValue {

    @Override
    public int getTypeIndex() {
      return 6;
    }

    @Override
    public int getSqlType() {
      return Types.CLOB;
    }
  }, BitValue {

    @Override
    public int getTypeIndex() {
      return 7;
    }

    @Override
    public int getSqlType() {
      return Types.BOOLEAN;
    }
  }, DateTimeValue {

    @Override
    public int getTypeIndex() {
      return 8;
    }

    @Override
    public int getSqlType() {
      return Types.TIMESTAMP;
    }
  }, TimeValue {

    @Override
    public int getTypeIndex() {
      return 9;
    }

    @Override
    public int getSqlType() {
      return Types.TIMESTAMP;
    }
  }, MonthValue {

    @Override
    public int getTypeIndex() {
      return 10;
    }

    @Override
    public int getSqlType() {
      return Types.DATE;
    }
  };

  public abstract int getTypeIndex();

  public abstract int getSqlType();

  public static ValueType valueOf(int valueType) {
    ValueType result = null;
    for (ValueType vt : values()) {
      if (vt.getTypeIndex() == valueType) {
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
      case Types.TIME:
        result = TimeValue;
        break;
      case Types.DATE:
      case Types.TIMESTAMP:
        result = DateValue;
        break;
      default:
        result = ObjectValue;
    }
    return result;
  }
}
