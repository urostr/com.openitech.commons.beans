/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.values;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author domenbasic
 */
public abstract class SQLValue<T> {

  protected String parameterName;
  protected int parameterIndex;
  protected T value;

  public abstract void setParameter(PreparedStatement preparedStatement) throws SQLException;

  public abstract void setParameter(CallableStatement preparedStatement) throws SQLException;

  @Override
  public String toString() {
    return value != null ? value.toString() : "null";
  }

  public static class SQLRegisteredParameter extends SQLValue<java.lang.Object> {

    int sqlType;
    Integer scale;
    String typeName;

    public SQLRegisteredParameter(String parameterName, int sqlType) {
      this.parameterName = parameterName;
      this.sqlType = sqlType;
    }

    public SQLRegisteredParameter(String parameterName, int sqlType, String typeName) {
      this.parameterName = parameterName;
      this.sqlType = sqlType;
      this.typeName = typeName;
    }

    public SQLRegisteredParameter(String parameterName, int sqlType, int scale) {
      this.parameterName = parameterName;
      this.sqlType = sqlType;
      this.scale = scale;
    }

    public SQLRegisteredParameter(int parameterIndex, int sqlType) {
      this.parameterIndex = parameterIndex;
      this.sqlType = sqlType;
    }

    public SQLRegisteredParameter(int parameterIndex, int sqlType, int scale) {
      this.parameterIndex = parameterIndex;
      this.sqlType = sqlType;
      this.scale = scale;
    }

    public SQLRegisteredParameter(int parameterIndex, int sqlType, String typeName) {
      this.parameterIndex = parameterIndex;
      this.sqlType = sqlType;
      this.typeName = typeName;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setParameter(CallableStatement callableStatement) throws SQLException {
      throw new UnsupportedOperationException("Not supported.");
    }

    public void registerParameter(CallableStatement callableStatement) throws SQLException {
      if (parameterName == null) {
        if (scale != null) {
          callableStatement.registerOutParameter(parameterIndex, sqlType, sqlType);
        } else if (typeName != null) {
          callableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
        } else {
          callableStatement.registerOutParameter(parameterIndex, sqlType);
        }
      } else {
        if (scale != null) {
          callableStatement.registerOutParameter(parameterName, sqlType, sqlType);
        } else if (typeName != null) {
          callableStatement.registerOutParameter(parameterName, sqlType, typeName);
        } else {
          callableStatement.registerOutParameter(parameterName, sqlType);
        }
      }
    }
  }

  public static class SQLObject extends SQLValue<java.lang.Object> {

    private Integer scaleOrLength = null;
    private Integer targetSqlType = null;

    public SQLObject(int parameterIndex, java.lang.Object value) {
      this(parameterIndex, value, null, null);
    }

    public SQLObject(int parameterIndex, java.lang.Object value, Integer targetSqlType) {
      this(parameterIndex, value, targetSqlType, null);
    }

    public SQLObject(int parameterIndex, java.lang.Object value, Integer targetSqlType, Integer scaleOrLength) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.targetSqlType = targetSqlType;
      this.scaleOrLength = scaleOrLength;
    }

    public SQLObject(String parameterName, java.lang.Object value) {
      this(parameterName, value, null, null);
    }

    public SQLObject(String parameterName, java.lang.Object value, Integer targetSqlType) {
      this(parameterName, value, targetSqlType, null);
    }

    public SQLObject(String parameterName, java.lang.Object value, Integer targetSqlType, Integer scaleOrLength) {
      this.parameterName = parameterName;
      this.value = value;
      this.targetSqlType = targetSqlType;
      this.scaleOrLength = scaleOrLength;
    }

    /**
     * Get the value of targetSqlType
     *
     * @return the value of targetSqlType
     */
    public Integer getTargetSqlType() {
      return targetSqlType;
    }

    /**
     * Set the value of targetSqlType
     *
     * @param targetSqlType new value of targetSqlType
     */
    public void setTargetSqlType(Integer targetSqlType) {
      this.targetSqlType = targetSqlType;
    }

    /**
     * Get the value of scaleOrLength
     *
     * @return the value of scaleOrLength
     */
    public Integer getScaleOrLength() {
      return scaleOrLength;
    }

    /**
     * Set the value of scaleOrLength
     *
     * @param scaleOrLength new value of scaleOrLength
     */
    public void setScaleOrLength(Integer scaleOrLength) {
      this.scaleOrLength = scaleOrLength;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (targetSqlType == null) {
        preparedStatement.setObject(parameterIndex, value);
      } else if (scaleOrLength == null) {
        preparedStatement.setObject(parameterIndex, value, targetSqlType);
      } else {
        preparedStatement.setObject(parameterIndex, value, targetSqlType, scaleOrLength);
      }
    }

    @Override
    public void setParameter(CallableStatement callableStatement) throws SQLException {
      if (targetSqlType == null) {
        callableStatement.setObject(parameterName, value);
      } else if (scaleOrLength == null) {
        callableStatement.setObject(parameterName, value, targetSqlType);
      } else {
        callableStatement.setObject(parameterName, value, targetSqlType, scaleOrLength);
      }
    }
  }

  public static class SQLInteger extends SQLValue<Integer> {

    public SQLInteger(int parameterIndex, int value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLInteger(String parameterName, int value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setInt(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setInt(parameterName, value);
    }
  }

  public static class SQLString extends SQLValue<String> {

    public SQLString(int parameterIndex, String value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLString(String parameterName, String value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setString(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setString(parameterName, value);
    }
  }

  public static class SQLNull extends SQLValue<Object> {

    private int targetSqlType;
    private String typeName;

    public SQLNull(int parameterIndex, int targetSqlType) {
      this.parameterIndex = parameterIndex;
      this.value = null;
      this.targetSqlType = targetSqlType;
    }

    public SQLNull(int parameterIndex, int sqlType, String typeName) {
      this.parameterIndex = parameterIndex;
      this.value = null;
      this.targetSqlType = sqlType;
      this.typeName = typeName;
    }

    public SQLNull(String parameterName, int targetSqlType) {
      this.parameterName = parameterName;
      this.value = null;
      this.targetSqlType = targetSqlType;
    }

    public SQLNull(String parameterName, int sqlType, String typeName) {
      this.parameterName = parameterName;
      this.value = null;
      this.targetSqlType = sqlType;
      this.typeName = typeName;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (typeName == null) {
        preparedStatement.setNull(parameterIndex, targetSqlType);
      } else {
        preparedStatement.setNull(parameterIndex, targetSqlType, typeName);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (typeName == null) {
        preparedStatement.setNull(parameterName, targetSqlType);
      } else {
        preparedStatement.setNull(parameterName, targetSqlType, typeName);
      }
    }
  }

  public static class SQLLong extends SQLValue<Long> {

    public SQLLong(int parameterIndex, long value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLLong(String parameterName, long value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setLong(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setLong(parameterName, value);
    }
  }

  public static class SQLBoolean extends SQLValue<Boolean> {

    public SQLBoolean(int parameterIndex, boolean value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLBoolean(String parameterName, boolean value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setBoolean(parameterIndex, (boolean) value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setBoolean(parameterName, value);
    }
  }

  public static class SQLFloat extends SQLValue<Float> {

    public SQLFloat(int parameterIndex, float value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLFloat(String parameterName, float value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setFloat(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setFloat(parameterName, value);
    }
  }

  public static class SQLDouble extends SQLValue<Double> {

    public SQLDouble(int parameterIndex, double value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLDouble(String parameterName, double value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setDouble(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setDouble(parameterName, value);
    }
  }

  public static class SQLDate extends SQLValue<java.sql.Date> {

    private Calendar cal;

    public SQLDate(int parameterIndex, java.sql.Date value) {
      this(parameterIndex, value, null);
    }

    public SQLDate(int parameterIndex, java.sql.Date value, Calendar cal) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.cal = cal;
    }

    public SQLDate(String parameterName, java.sql.Date value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLDate(String parameterName, Date value, Calendar cal) {
      this.parameterName = parameterName;
      this.value = value;
      this.cal = cal;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setDate(parameterIndex, value);
      } else {
        preparedStatement.setDate(parameterIndex, value, cal);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setDate(parameterName, value);
      } else {
        preparedStatement.setDate(parameterName, value, cal);
      }
    }
  }

  public static class SQLTimeStamp extends SQLValue<java.sql.Timestamp> {

    private Calendar cal;

    public SQLTimeStamp(int parameterIndex, java.sql.Timestamp value) {
      this(parameterIndex, value, null);
    }

    public SQLTimeStamp(int parameterIndex, java.sql.Timestamp value, Calendar cal) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.cal = cal;
    }

    public SQLTimeStamp(String parameterName, java.sql.Timestamp value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLTimeStamp(String parameterName, Timestamp value, Calendar cal) {
      this.parameterName = parameterName;
      this.value = value;
      this.cal = cal;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setTimestamp(parameterIndex, value);
      } else {
        preparedStatement.setTimestamp(parameterIndex, value, cal);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setTimestamp(parameterName, value);
      } else {
        preparedStatement.setTimestamp(parameterName, value, cal);
      }
    }
  }

  public static class SQLTime extends SQLValue<java.sql.Time> {

    private Calendar cal;

    public SQLTime(int parameterIndex, java.sql.Time value) {
      this(parameterIndex, value, null);
    }

    public SQLTime(int parameterIndex, java.sql.Time value, Calendar cal) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.cal = cal;
    }

    public SQLTime(String parameterName, java.sql.Time value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLTime(String parameterName, Time value, Calendar cal) {
      this.parameterName = parameterName;
      this.value = value;
      this.cal = cal;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setTime(parameterIndex, value);
      } else {
        preparedStatement.setTime(parameterIndex, value, cal);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (cal == null) {
        preparedStatement.setTime(parameterName, value);
      } else {
        preparedStatement.setTime(parameterName, value, cal);
      }
    }
  }

  public static class SQLClob extends SQLValue<Object> {

    private Long length = null;

    public SQLClob(int parameterIndex, Reader value) {
      this(parameterIndex, value, null);
    }

    public SQLClob(int parameterIndex, Reader value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLClob(int parameterIndex, java.sql.Clob value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLClob(String parameterName, java.sql.Clob value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLClob(String parameterName, Reader x) {
      this(parameterName, x, null);
    }

    public SQLClob(String parameterName, Reader value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (value instanceof Clob) {
        preparedStatement.setClob(parameterIndex, (Clob) value);
      } else if (value instanceof Reader) {
        if (length == null) {
          preparedStatement.setClob(parameterIndex, (Reader) value);
        } else {
          preparedStatement.setClob(parameterIndex, (Reader) value, length.longValue());
        }
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (value instanceof Clob) {
        preparedStatement.setClob(parameterName, (Clob) value);
      } else if (value instanceof Reader) {
        if (length == null) {
          preparedStatement.setClob(parameterName, (Reader) value);
        } else {
          preparedStatement.setClob(parameterName, (Reader) value, length.longValue());
        }
      }
    }
  }

  public static class SQLNClob extends SQLValue<Object> {

    private Long length = null;

    public SQLNClob(int parameterIndex, Reader value) {
      this(parameterIndex, value, null);
    }

    public SQLNClob(int parameterIndex, Reader value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLNClob(int parameterIndex, java.sql.NClob value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLNClob(String parameterName, java.sql.NClob value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLNClob(String parameterName, Reader value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    public SQLNClob(String parameterName, Reader value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (value instanceof NClob) {
        preparedStatement.setNClob(parameterIndex, (NClob) value);
      } else if (value instanceof Reader) {
        if (length == null) {
          preparedStatement.setNClob(parameterIndex, (Reader) value);
        } else {
          preparedStatement.setNClob(parameterIndex, (Reader) value, length.longValue());
        }
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (value instanceof NClob) {
        preparedStatement.setNClob(parameterName, (NClob) value);
      } else if (value instanceof Reader) {
        if (length == null) {
          preparedStatement.setNClob(parameterName, (Reader) value);
        } else {
          preparedStatement.setNClob(parameterName, (Reader) value, length.longValue());
        }
      }
    }
  }

  public static class SQLByte extends SQLValue<Byte> {

    public SQLByte(int parameterIndex, byte value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLByte(String parameterName, byte value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setByte(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setByte(parameterIndex, value);
    }
  }

  public static class SQLBytes extends SQLValue<Byte[]> {

    public SQLBytes(int parameterIndex, byte[] value) {
      this.parameterIndex = parameterIndex;
      Byte[] bytes = new Byte[value.length];
      for (int i = 0; i < value.length; i++) {
        bytes[i] = value[i];
      }
      //copy ne dela
//      System.arraycopy(value, 0, bytes, 0, bytes.length);
      this.value = bytes;
    }

    public SQLBytes(String parameterName, byte[] value) {
      this.parameterName = parameterName;
      Byte[] bytes = new Byte[value.length];
      System.arraycopy(value, 0, bytes, 0, bytes.length);
      this.value = bytes;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      byte[] bytes = new byte[value.length];
      System.arraycopy(value, 0, bytes, 0, bytes.length);
      preparedStatement.setBytes(parameterIndex, bytes);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      byte[] bytes = new byte[value.length];
      System.arraycopy(value, 0, bytes, 0, bytes.length);
      preparedStatement.setBytes(parameterName, bytes);
    }
  }

  public static class SQLShort extends SQLValue<Short> {

    public SQLShort(int parameterIndex, short value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLShort(String parameterName, short value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setShort(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setShort(parameterName, value);
    }
  }

  public static class SQLBigDecimal extends SQLValue<BigDecimal> {

    public SQLBigDecimal(int parameterIndex, BigDecimal value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLBigDecimal(String parameterName, BigDecimal value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setBigDecimal(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setBigDecimal(parameterName, value);
    }
  }

  public static class SQLAsciiStream extends SQLValue<InputStream> {

    private Long length;

    public SQLAsciiStream(int parameterIndex, InputStream value) {
      this(parameterIndex, value, null);
    }

    public SQLAsciiStream(int parameterIndex, InputStream value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLAsciiStream(String parameterName, InputStream value) {
      this(parameterName, value, null);
    }

    public SQLAsciiStream(String parameterName, InputStream value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setAsciiStream(parameterIndex, value);
      } else {
        preparedStatement.setAsciiStream(parameterIndex, value, length);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setAsciiStream(parameterName, value);
      } else {
        preparedStatement.setAsciiStream(parameterName, value, length);
      }
    }
  }

  public static class SQLUnicodeStream extends SQLValue<InputStream> {

    private final int length;

    public SQLUnicodeStream(int parameterIndex, InputStream value, int length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setUnicodeStream(parameterIndex, value, length);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      throw new UnsupportedOperationException("Not supported.");
    }
  }

  public static class SQLBinaryStream extends SQLValue<InputStream> {

    private final Long length;

    public SQLBinaryStream(int parameterIndex, InputStream value) {
      this(parameterIndex, value, null);
    }

    public SQLBinaryStream(int parameterIndex, InputStream value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLBinaryStream(String parameterName, InputStream x) {
      this(parameterName, x, null);
    }

    public SQLBinaryStream(String parameterName, InputStream value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setBinaryStream(parameterIndex, value);
      } else {
        preparedStatement.setBinaryStream(parameterIndex, value, length);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setBinaryStream(parameterName, value);
      } else {
        preparedStatement.setBinaryStream(parameterName, value, length);
      }
    }
  }

  public static class SQLCharacterStream extends SQLValue<Reader> {

    private Long length;

    public SQLCharacterStream(int parameterIndex, Reader value) {
      this(parameterIndex, value, null);
    }

    public SQLCharacterStream(int parameterIndex, Reader value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLCharacterStream(String parameterName, Reader x) {
      this(parameterName, x, null);
    }

    public SQLCharacterStream(String parameterName, Reader value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setCharacterStream(parameterIndex, value);
      } else {
        preparedStatement.setCharacterStream(parameterIndex, value, length);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setCharacterStream(parameterName, value);
      } else {
        preparedStatement.setCharacterStream(parameterName, value, length);
      }
    }
  }

  public static class SQLNCharacterStream extends SQLValue<Reader> {

    private final Long length;

    public SQLNCharacterStream(int parameterIndex, Reader value) {
      this(parameterIndex, value, null);
    }

    public SQLNCharacterStream(int parameterIndex, Reader value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLNCharacterStream(String parameterName, Reader value) {
      this(parameterName, value, null);
    }

    public SQLNCharacterStream(String parameterName, Reader value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setNCharacterStream(parameterIndex, value);
      } else {
        preparedStatement.setNCharacterStream(parameterIndex, value, length);
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setNCharacterStream(parameterName, value);
      } else {
        preparedStatement.setNCharacterStream(parameterName, value, length);
      }
    }
  }

  public static class SQLRef extends SQLValue<Ref> {

    public SQLRef(int parameterIndex, Ref value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setRef(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      throw new UnsupportedOperationException("Not supported.");
    }
  }

  public static class SQLBlob extends SQLValue<Object> {

    private Long length = null;

    public SQLBlob(int parameterIndex, InputStream value) {
      this(parameterIndex, value, null);
    }

    public SQLBlob(int parameterIndex, InputStream value, Long length) {
      this.parameterIndex = parameterIndex;
      this.value = value;
      this.length = length;
    }

    public SQLBlob(int parameterIndex, Blob value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLBlob(String parameterName, InputStream x) {
      this(parameterName, x, null);
    }

    public SQLBlob(String parameterName, InputStream value, Long length) {
      this.parameterName = parameterName;
      this.value = value;
      this.length = length;
    }

    public SQLBlob(String parameterName, Blob value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (value instanceof Blob) {
        preparedStatement.setBlob(parameterIndex, (Blob) value);
      } else if (value instanceof InputStream) {
        if (length == null) {
          preparedStatement.setBlob(parameterIndex, (InputStream) value);
        } else {
          preparedStatement.setBlob(parameterIndex, (InputStream) value, length.longValue());
        }
      }
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      if (value instanceof Blob) {
        preparedStatement.setBlob(parameterName, (Blob) value);
      } else if (value instanceof InputStream) {
        if (length == null) {
          preparedStatement.setBlob(parameterName, (InputStream) value);
        } else {
          preparedStatement.setBlob(parameterName, (InputStream) value, length.longValue());
        }
      }
    }
  }

  public static class SQLArray extends SQLValue<Array> {

    public SQLArray(int parameterIndex, Array value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setArray(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      throw new UnsupportedOperationException("Not supported.");
    }
  }

  public static class SQLUrl extends SQLValue<URL> {

    public SQLUrl(int parameterIndex, URL value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLUrl(String parameterName, URL value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setURL(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setURL(parameterName, value);
    }
  }

  public static class SQLRowId extends SQLValue<RowId> {

    public SQLRowId(int parameterIndex, RowId value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLRowId(String parameterName, RowId value) {
      this.parameterName = parameterName;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setRowId(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setRowId(parameterName, value);
    }
  }

  public static class SQLNString extends SQLValue<String> {

    public SQLNString(int parameterIndex, String value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLNString(String parameterName, String value) {
      this.parameterName = parameterName;
      this.value = value;

    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setNString(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setNString(parameterName, value);
    }
  }

  public static class SQLSQLXML extends SQLValue<SQLXML> {

    public SQLSQLXML(int parameterIndex, SQLXML value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    public SQLSQLXML(String parameterName, SQLXML value) {
      this.parameterName = parameterName;
      this.value = value;

    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setSQLXML(parameterIndex, value);
    }

    @Override
    public void setParameter(CallableStatement preparedStatement) throws SQLException {
      preparedStatement.setSQLXML(parameterName, value);
    }
  }
}
