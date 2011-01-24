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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class CallableStatementProxy extends PreparedStatementProxy implements CallableStatement {

  public CallableStatementProxy(AbstractConnection connection, String sql) throws SQLException {
    super(connection, sql);
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean wasNull() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
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

  @Override
  public String getString(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getBoolean(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte getByte(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public short getShort(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getInt(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public long getLong(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public float getFloat(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double getDouble(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte[] getBytes(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Date getDate(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Time getTime(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getObject(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Ref getRef(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Blob getBlob(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Clob getClob(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Array getArray(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public URL getURL(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setURL(String parameterName, URL val) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getString(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getBoolean(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte getByte(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public short getShort(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getInt(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public long getLong(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public float getFloat(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double getDouble(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte[] getBytes(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Date getDate(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Time getTime(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getObject(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Ref getRef(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Blob getBlob(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Clob getClob(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Array getArray(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public URL getURL(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public RowId getRowId(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public RowId getRowId(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNString(String parameterName, String value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public NClob getNClob(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getNString(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getNString(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getCharacterStream(String parameterName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
