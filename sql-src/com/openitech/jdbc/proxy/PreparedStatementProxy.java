/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import com.openitech.jdbc.values.SQLValue;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author uros
 */
public class PreparedStatementProxy extends StatementProxy implements PreparedStatement {

  private final PreparedStatementFactory factory;

  public PreparedStatementProxy(AbstractConnection connection, String sql) throws SQLException {
    this(connection, sql, ResultSet.TYPE_FORWARD_ONLY);
  }

  public PreparedStatementProxy(AbstractConnection connection, String sql, String[] columnNames) throws SQLException {
    super(connection, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    this.factory = new PreparedStatementFactory.PreparedStatementType2(sql, columnNames);
  }

  public PreparedStatementProxy(AbstractConnection connection, String sql, int resultSetType) throws SQLException {
    this(connection, sql, resultSetType, ResultSet.CONCUR_READ_ONLY);
  }

  public PreparedStatementProxy(AbstractConnection connection, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    super(connection, resultSetType, resultSetConcurrency);
    this.factory = new PreparedStatementFactory.PreparedStatementType1(sql, resultSetType, resultSetConcurrency);
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    return ((PreparedStatement) getActiveStatement()).executeQuery();
  }

  @Override
  public int executeUpdate() throws SQLException {
    return ((PreparedStatement) getActiveStatement()).executeUpdate();
  }
  List<SQLValue> parameters = new ArrayList<SQLValue>();

  private void storeParameter(int parameterIndex, SQLValue value) {
    if (parameters.size() <= parameterIndex) {
      parameters.add(parameterIndex, value);
    } else {
      parameters.set(parameterIndex, value);
    }
  }

  @Override
  protected Statement createStatement() throws SQLException {
    return factory.createPreparedStatement(connection);
  }

  @Override
  protected void initStatement() throws SQLException {
    super.initStatement();
    if (parameters.size() > 0) {
      for (SQLValue sqlValue : parameters) {
        sqlValue.setParameter((PreparedStatement) statement);
      }
    }
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    ((PreparedStatement) statement).setNull(parameterIndex, sqlType);
    storeParameter(parameterIndex, new SQLValue.SQLNull(parameterIndex, sqlType));
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    ((PreparedStatement) statement).setBoolean(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBoolean(parameterIndex, x));
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    ((PreparedStatement) statement).setInt(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLInteger(parameterIndex, x));
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    ((PreparedStatement) statement).setLong(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLLong(parameterIndex, x));
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    ((PreparedStatement) statement).setFloat(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLFloat(parameterIndex, x));
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    ((PreparedStatement) statement).setDouble(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLDouble(parameterIndex, x));
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    ((PreparedStatement) statement).setString(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLString(parameterIndex, x));
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    ((PreparedStatement) statement).setDate(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLDate(parameterIndex, x));
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    ((PreparedStatement) statement).setTimestamp(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLTimeStamp(parameterIndex, x));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clearParameters() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setObject(parameterIndex, x, targetSqlType);
    storeParameter(parameterIndex, new SQLValue.SQLObject(parameterIndex, x, targetSqlType));
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setObject(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLObject(parameterIndex, x));
  }

  @Override
  public boolean execute() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addBatch() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setClob(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLClob(parameterIndex, x));
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return ((PreparedStatement) statement).getMetaData();
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNull(parameterIndex, sqlType);
    storeParameter(parameterIndex, new SQLValue.SQLNull(parameterIndex, sqlType));
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return ((PreparedStatement) statement).getParameterMetaData();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private static abstract class PreparedStatementFactory {

    public abstract PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException;

    private static class PreparedStatementType1 extends PreparedStatementFactory {

      private final String sql;
      private final int resultSetType;
      private final int resultSetConcurrency;

      public PreparedStatementType1(String sql, int resultSetType, int resultSetConcurrency) {
        this.sql = sql;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
      }

      @Override
      public PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
      }
    }

    private static class PreparedStatementType2 extends PreparedStatementFactory {

      String sql;
      private final String[] columnNames;

      public PreparedStatementType2(String sql, String[] columnNames) {
        this.sql = sql;

        this.columnNames = columnNames;
      }

      @Override
      public PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
      }
    }
  }
}
