/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import com.openitech.jdbc.values.SQLValue;
import com.openitech.text.CaseInsensitiveString;
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
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author domenbasic
 */
public class CallableStatementProxy extends PreparedStatementProxy implements CallableStatement {

  protected CallableStatementProxy(AbstractConnection connection, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    super(connection, sql, resultSetType, resultSetConcurrency, null);
  }

  @Override
  protected Statement createStatement() throws SQLException {
    return connection.prepareCall(cursorName, resultSetType, resultSetConcurrency);
  }

  protected void storeParameter(String parameterName, SQLValue value) {
    parametersMap.put(new CaseInsensitiveString(parameterName), value);
  }

  @Override
  protected void initStatement() throws SQLException {
    super.initStatement();
    for (SQLValue sqlValue : parametersMap.values()) {
      sqlValue.setParameter((CallableStatement) this);
    }
  }
  protected Map<CaseInsensitiveString, SQLValue> parametersMap = new LinkedHashMap<CaseInsensitiveString, SQLValue>();

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
////////////////////////////
  /////////////////////////

  @Override
  public void setURL(String parameterName, URL x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setURL(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLUrl(parameterName, x));
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNull(parameterName, sqlType);
    storeParameter(parameterName, new SQLValue.SQLNull(parameterName, sqlType));
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBoolean(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBoolean(parameterName, x));
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setByte(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLByte(parameterName, x));
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setShort(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLShort(parameterName, x));
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setInt(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLInteger(parameterName, x));
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setLong(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLLong(parameterName, x));
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setFloat(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLFloat(parameterName, x));
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setDouble(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLDouble(parameterName, x));
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBigDecimal(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBigDecimal(parameterName, x));
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setString(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLString(parameterName, x));
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBytes(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBytes(parameterName, x));
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setDate(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLDate(parameterName, x));
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setTime(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLTime(parameterName, x));
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setTimestamp(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLTimeStamp(parameterName, x));
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setAsciiStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLAsciiStream(parameterName, x, (long) length));
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBinaryStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLBinaryStream(parameterName, x, (long) length));
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    ((CallableStatement) getActiveStatement()).setObject(parameterName, x, targetSqlType, scale);
    storeParameter(parameterName, new SQLValue.SQLObject(parameterName, x, targetSqlType, scale));
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    ((CallableStatement) getActiveStatement()).setObject(parameterName, x, targetSqlType);
    storeParameter(parameterName, new SQLValue.SQLObject(parameterName, x, targetSqlType));
  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setObject(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLObject(parameterName, x));
  }

  @Override
  public void setCharacterStream(String parameterName, Reader x, int length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setCharacterStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLCharacterStream(parameterName, x, (long) length));
  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    ((CallableStatement) getActiveStatement()).setDate(parameterName, x, cal);
    storeParameter(parameterName, new SQLValue.SQLDate(parameterName, x, cal));
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    ((CallableStatement) getActiveStatement()).setTime(parameterName, x, cal);
    storeParameter(parameterName, new SQLValue.SQLTime(parameterName, x, cal));
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    ((CallableStatement) getActiveStatement()).setTimestamp(parameterName, x, cal);
    storeParameter(parameterName, new SQLValue.SQLTimeStamp(parameterName, x, cal));
  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNull(parameterName, sqlType, typeName);
    storeParameter(parameterName, new SQLValue.SQLNull(parameterName, sqlType, typeName));
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setRowId(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLRowId(parameterName, x));
  }

  @Override
  public void setNString(String parameterName, String x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNString(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLNString(parameterName, x));
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNCharacterStream(parameterName, value, length);
    storeParameter(parameterName, new SQLValue.SQLNCharacterStream(parameterName, value, length));
  }

  @Override
  public void setNClob(String parameterName, NClob x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNClob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLNClob(parameterName, x));
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setClob(parameterName, reader, length);
    storeParameter(parameterName, new SQLValue.SQLClob(parameterName, reader, length));
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBlob(parameterName, inputStream, length);
    storeParameter(parameterName, new SQLValue.SQLBlob(parameterName, inputStream, length));
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNClob(parameterName, reader, length);
    storeParameter(parameterName, new SQLValue.SQLNClob(parameterName, reader, length));
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setSQLXML(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLSQLXML(parameterName, x));
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBlob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBlob(parameterName, x));
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setClob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLClob(parameterName, x));
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setAsciiStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLAsciiStream(parameterName, x, length));
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBinaryStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLBinaryStream(parameterName, x, length));
  }

  @Override
  public void setCharacterStream(String parameterName, Reader x, long length) throws SQLException {
    ((CallableStatement) getActiveStatement()).setCharacterStream(parameterName, x, length);
    storeParameter(parameterName, new SQLValue.SQLCharacterStream(parameterName, x, length));
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setAsciiStream(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLAsciiStream(parameterName, x));
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBinaryStream(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBinaryStream(parameterName, x));
  }

  @Override
  public void setCharacterStream(String parameterName, Reader x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setCharacterStream(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLCharacterStream(parameterName, x));
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNCharacterStream(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLNCharacterStream(parameterName, x));
  }

  @Override
  public void setClob(String parameterName, Reader x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setClob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLClob(parameterName, x));
  }

  @Override
  public void setBlob(String parameterName, InputStream x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setBlob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLBlob(parameterName, x));
  }

  @Override
  public void setNClob(String parameterName, Reader x) throws SQLException {
    ((CallableStatement) getActiveStatement()).setNClob(parameterName, x);
    storeParameter(parameterName, new SQLValue.SQLNClob(parameterName, x));
  }
  ////////////////////
  ///////////////////

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
  public NClob getNClob(int parameterIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
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
}
