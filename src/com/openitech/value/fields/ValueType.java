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

    @Override
    public String getValueColumn() {
      return "IntValue";
    }

    @Override
    public Class getSqlClass() {
      return Integer.class;
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

    @Override
    public String getValueColumn() {
      return "RealValue";
    }

    @Override
    public Class getSqlClass() {
      return Double.class;
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

    @Override
    public String getValueColumn() {
      return "StringValue";
    }

    @Override
    public Class getSqlClass() {
      return String.class;
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

    @Override
    public String getValueColumn() {
      return "DateValue";
    }

    @Override
    public Class getSqlClass() {
      return java.sql.Timestamp.class;
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

    @Override
    public String getValueColumn() {
      return "BlobValue";
    }

    @Override
    public Class getSqlClass() {
      return String.class;
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

    @Override
    public String getValueColumn() {
      return "ClobValue";
    }

    @Override
    public Class getSqlClass() {
      return String.class;
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

    @Override
    public String getValueColumn() {
      return "IntValue";
    }

    @Override
    public Class getSqlClass() {
      return Boolean.class;
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

    @Override
    public String getValueColumn() {
      return "DateValue";
    }

    @Override
    public Class getSqlClass() {
      return java.sql.Timestamp.class;
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

    @Override
    public String getValueColumn() {
      return "DateValue";
    }

    @Override
    public Class getSqlClass() {
      return java.sql.Timestamp.class;
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

    @Override
    public String getValueColumn() {
      return "DateValue";
    }

    @Override
    public Class getSqlClass() {
      return java.sql.Timestamp.class;
    }
  }, LongValue {

    @Override
    public int getTypeIndex() {
      return 11;
    }

    @Override
    public int getSqlType() {
      return Types.BIGINT;
    }

    @Override
    public String getValueColumn() {
      return "IntValue";
    }

    @Override
    public Class getSqlClass() {
      return Long.class;
    }
  }, BlobValue {

    @Override
    public int getTypeIndex() {
      return 5;
    }

    @Override
    public int getSqlType() {
      return Types.BLOB;
    }

    @Override
    public String getValueColumn() {
      return "BlobValue";
    }

    @Override
    public Class getSqlClass() {
      return Object.class;
    }
  }, FileValue {

    @Override
    public int getTypeIndex() {
      return 12;
    }

    @Override
    public int getSqlType() {
      return Types.BLOB;
    }

    @Override
    public String getValueColumn() {
      return "BlobValue";
    }

    @Override
    public Class getSqlClass() {
      return Object.class;
    }
  };

  public abstract int getTypeIndex();

  public abstract int getSqlType();
  
  public abstract Class getSqlClass();

  public abstract String getValueColumn();

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
      case Types.INTEGER:
        result = IntValue;
        break;
      case Types.BIGINT:
        result = LongValue;
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
      case Types.BLOB:
      case Types.VARBINARY:
        result = BlobValue;
        break;
      default:
        result = ObjectValue;
    }
    return result;
  }
}
