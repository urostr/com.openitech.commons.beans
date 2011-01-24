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
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Calendar;

/**
 *
 * @author domenbasic
 */
public abstract class SQLValue<T> {

  protected int parameterIndex;
  protected T value;

  public abstract void setParameter(PreparedStatement preparedStatement) throws SQLException;

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
  }

  public static class SQLInteger extends SQLValue<Integer> {

    public SQLInteger(int parameterIndex, int value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setInt(parameterIndex, (int) value);
    }
  }

  public static class SQLString extends SQLValue<String> {

    public SQLString(int parameterIndex, String value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (value != null) {
        preparedStatement.setString(parameterIndex, (String) value);
      } else {
        preparedStatement.setNull(parameterIndex, java.sql.Types.VARCHAR);
      }
    }
  }

  public static class SQLNull extends SQLValue<Object> {

    private final int targetSqlType;

    public SQLNull(int parameterIndex, int targetSqlType) {
      this.parameterIndex = parameterIndex;
      this.value = null;
      this.targetSqlType = targetSqlType;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setNull(parameterIndex, targetSqlType);
    }
  }

  public static class SQLLong extends SQLValue<Long> {

    public SQLLong(int parameterIndex, long value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setLong(parameterIndex, (long) value);
    }
  }

  public static class SQLBoolean extends SQLValue<Boolean> {

    public SQLBoolean(int parameterIndex, boolean value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setBoolean(parameterIndex, (boolean) value);
    }
  }

  public static class SQLFloat extends SQLValue<Float> {

    public SQLFloat(int parameterIndex, float value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setFloat(parameterIndex, (float) value);
    }
  }

  public static class SQLDouble extends SQLValue<Double> {

    public SQLDouble(int parameterIndex, double value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setDouble(parameterIndex, (double) value);
    }
  }

  public static class SQLDate extends SQLValue<java.sql.Date> {

    private final Calendar cal;

    public SQLDate(int parameterIndex, java.sql.Date value) {
      this(parameterIndex, value, null);
    }

    public SQLDate(int parameterIndex, java.sql.Date value, Calendar cal) {
      this.parameterIndex = parameterIndex;
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
  }

  public static class SQLTimeStamp extends SQLValue<java.sql.Timestamp> {

    private final Calendar cal;

    public SQLTimeStamp(int parameterIndex, java.sql.Timestamp value) {
      this(parameterIndex, value, null);
    }

    public SQLTimeStamp(int parameterIndex, java.sql.Timestamp value, Calendar cal) {
      this.parameterIndex = parameterIndex;
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
  }

  public static class SQLTime extends SQLValue<java.sql.Time> {

    private final Calendar cal;

    public SQLTime(int parameterIndex, java.sql.Time value) {
      this(parameterIndex, value, null);
    }

    public SQLTime(int parameterIndex, java.sql.Time value, Calendar cal) {
      this.parameterIndex = parameterIndex;
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
  }

  public static class SQLByte extends SQLValue<Byte> {

    public SQLByte(int parameterIndex, Byte value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setByte(parameterIndex, value);
    }
  }

  public static class SQLBytes extends SQLValue<Byte[]> {

    public SQLBytes(int parameterIndex, byte[] value) {
      this.parameterIndex = parameterIndex;
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
  }

  public static class SQLShort extends SQLValue<Short> {

    public SQLShort(int parameterIndex, short value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setShort(parameterIndex, (short) value);
    }
  }

  public static class SQLBigDecimal extends SQLValue<BigDecimal> {

    public SQLBigDecimal(int parameterIndex, BigDecimal value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setBigDecimal(parameterIndex, value);
    }
  }

  public static class SQLAsciiStream extends SQLValue<InputStream> {

    private final Long length;

    public SQLAsciiStream(int parameterIndex, InputStream value) {
      this(parameterIndex, value, null);
    }

    public SQLAsciiStream(int parameterIndex, InputStream value, Long length) {
      this.parameterIndex = parameterIndex;
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

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setBinaryStream(parameterIndex, value);
      } else {
        preparedStatement.setBinaryStream(parameterIndex, value, length);
      }
    }
  }

  public static class SQLCharacterStream extends SQLValue<Reader> {

    private final Long length;

    public SQLCharacterStream(int parameterIndex, Reader value) {
      this(parameterIndex, value, null);
    }

    public SQLCharacterStream(int parameterIndex, Reader value, Long length) {
      this.parameterIndex = parameterIndex;
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

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      if (length == null) {
        preparedStatement.setNCharacterStream(parameterIndex, value);
      } else {
        preparedStatement.setNCharacterStream(parameterIndex, value, length);
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
  }

  public static class SQLUrl extends SQLValue<URL> {

    public SQLUrl(int parameterIndex, URL value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setURL(parameterIndex, value);
    }
  }

  public static class SQLRowId extends SQLValue<RowId> {

    public SQLRowId(int parameterIndex, RowId value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setRowId(parameterIndex, value);
    }
  }

  public static class SQLNString extends SQLValue<String> {

    public SQLNString(int parameterIndex, String value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setNString(parameterIndex, value);
    }
  }

  public static class SQLSQLXML extends SQLValue<SQLXML> {

    public SQLSQLXML(int parameterIndex, SQLXML value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setSQLXML(parameterIndex, value);
    }
  }
}
