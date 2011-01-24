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

  protected  void storeParameter(int parameterIndex, SQLValue value) {
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
    ((PreparedStatement) getActiveStatement()).setNull(parameterIndex, sqlType);
    storeParameter(parameterIndex, new SQLValue.SQLNull(parameterIndex, sqlType));
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBoolean(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBoolean(parameterIndex, x));
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setByte(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLByte(parameterIndex, x));
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setShort(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLShort(parameterIndex, x));
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setInt(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLInteger(parameterIndex, x));
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setLong(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLLong(parameterIndex, x));
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setFloat(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLFloat(parameterIndex, x));
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setDouble(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLDouble(parameterIndex, x));
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBigDecimal(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBigDecimal(parameterIndex, x));
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setString(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLString(parameterIndex, x));
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBytes(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBytes(parameterIndex, x));
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setDate(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLDate(parameterIndex, x));
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setTime(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLTime(parameterIndex, x));
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setTimestamp(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLTimeStamp(parameterIndex, x));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setAsciiStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLAsciiStream(parameterIndex, x, (long) length));
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setUnicodeStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLUnicodeStream(parameterIndex, x, length));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBinaryStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLBinaryStream(parameterIndex, x, (long) length));
  }

  @Override
  public void clearParameters() throws SQLException {
    ((PreparedStatement) getActiveStatement()).clearParameters();
    parameters.clear();
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
    return ((PreparedStatement) getActiveStatement()).execute();
  }
  private boolean addBatch;

  @Override
  public void addBatch() throws SQLException {
    ((PreparedStatement) getActiveStatement()).addBatch();
    addBatch = true;
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setObject(parameterIndex, reader, length);
    storeParameter(parameterIndex, new SQLValue.SQLCharacterStream(parameterIndex, reader, (long) length));
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setRef(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLRef(parameterIndex, x));
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBlob(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBlob(parameterIndex, x));
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setClob(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLClob(parameterIndex, x));
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setArray(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLArray(parameterIndex, x));
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return ((PreparedStatement) getActiveStatement()).getMetaData();
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setDate(parameterIndex, x, cal);
    storeParameter(parameterIndex, new SQLValue.SQLDate(parameterIndex, x, cal));
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setTime(parameterIndex, x, cal);
    storeParameter(parameterIndex, new SQLValue.SQLTime(parameterIndex, x, cal));
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setTimestamp(parameterIndex, x, cal);
    storeParameter(parameterIndex, new SQLValue.SQLTimeStamp(parameterIndex, x, cal));
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNull(parameterIndex, sqlType);
    storeParameter(parameterIndex, new SQLValue.SQLNull(parameterIndex, sqlType));
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setURL(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLUrl(parameterIndex, x));
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return ((PreparedStatement) getActiveStatement()).getParameterMetaData();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setRowId(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLRowId(parameterIndex, x));
  }

  @Override
  public void setNString(int parameterIndex, String x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNString(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLNString(parameterIndex, x));
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNCharacterStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLNCharacterStream(parameterIndex, x, length));
  }

  @Override
  public void setNClob(int parameterIndex, NClob x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNClob(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLNClob(parameterIndex, x));
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setClob(parameterIndex, reader, length);
    storeParameter(parameterIndex, new SQLValue.SQLClob(parameterIndex, reader, length));
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBlob(parameterIndex, inputStream, length);
    storeParameter(parameterIndex, new SQLValue.SQLBlob(parameterIndex, inputStream, length));
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNClob(parameterIndex, reader, length);
    storeParameter(parameterIndex, new SQLValue.SQLNClob(parameterIndex, reader, length));
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setSQLXML(parameterIndex, xmlObject);
    storeParameter(parameterIndex, new SQLValue.SQLSQLXML(parameterIndex, xmlObject));
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    storeParameter(parameterIndex, new SQLValue.SQLObject(parameterIndex, x, targetSqlType, scaleOrLength));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setAsciiStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLAsciiStream(parameterIndex, x, length));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBinaryStream(parameterIndex, x, length);
    storeParameter(parameterIndex, new SQLValue.SQLBinaryStream(parameterIndex, x, length));
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setCharacterStream(parameterIndex, reader, length);
    storeParameter(parameterIndex, new SQLValue.SQLCharacterStream(parameterIndex, reader, length));
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setAsciiStream(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLAsciiStream(parameterIndex, x));
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBinaryStream(parameterIndex, x);
    storeParameter(parameterIndex, new SQLValue.SQLBinaryStream(parameterIndex, x));
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setCharacterStream(parameterIndex, reader);
    storeParameter(parameterIndex, new SQLValue.SQLCharacterStream(parameterIndex, reader));
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNCharacterStream(parameterIndex, value);
    storeParameter(parameterIndex, new SQLValue.SQLNCharacterStream(parameterIndex, value));
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setClob(parameterIndex, reader);
    storeParameter(parameterIndex, new SQLValue.SQLClob(parameterIndex, reader));
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setBlob(parameterIndex, inputStream);
    storeParameter(parameterIndex, new SQLValue.SQLBlob(parameterIndex, inputStream));
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    ((PreparedStatement) getActiveStatement()).setNClob(parameterIndex, reader);
    storeParameter(parameterIndex, new SQLValue.SQLNClob(parameterIndex, reader));
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
