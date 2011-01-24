/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.values;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public SQLDate(int parameterIndex, java.sql.Date value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setDate(parameterIndex, (java.sql.Date) value);
    }
  }

  public static class SQLTimeStamp extends SQLValue<java.sql.Timestamp> {

    public SQLTimeStamp(int parameterIndex, java.sql.Timestamp value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setTimestamp(parameterIndex, (java.sql.Timestamp) value);
    }
  }
  public static class SQLClob extends SQLValue<java.sql.Clob> {

    public SQLClob(int parameterIndex, java.sql.Clob value) {
      this.parameterIndex = parameterIndex;
      this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement preparedStatement) throws SQLException {
      preparedStatement.setClob(parameterIndex, (java.sql.Clob) value);
    }
  }
}
