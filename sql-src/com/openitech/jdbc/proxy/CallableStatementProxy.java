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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
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

  @Override
  protected void initStatement() throws SQLException {
    super.initStatement();
    for (SQLValue sqlValue : parametersMap.values()) {
      sqlValue.setParameter((CallableStatement) statement);
    }
    for (SQLValue.SQLRegisteredParameter sqlRegisteredParameter : registeredValuesMap.values()) {
      sqlRegisteredParameter.registerParameter((CallableStatement) statement);
    }
    for (SQLValue.SQLRegisteredParameter sqlRegisteredParameter : registeredValues) {
      sqlRegisteredParameter.registerParameter((CallableStatement) statement);
    }
  }

  protected Map<CaseInsensitiveString, SQLValue> parametersMap = new LinkedHashMap<CaseInsensitiveString, SQLValue>();
  protected Map<CaseInsensitiveString, SQLValue.SQLRegisteredParameter> registeredValuesMap = new LinkedHashMap<CaseInsensitiveString, SQLValue.SQLRegisteredParameter>();
  protected List<SQLValue.SQLRegisteredParameter> registeredValues = new ArrayList<SQLValue.SQLRegisteredParameter>();

  protected  void registerParameter(int parameterIndex, SQLValue.SQLRegisteredParameter value) {
    if (registeredValues.size() <= parameterIndex) {
      registeredValues.add(parameterIndex, value);
    } else {
      registeredValues.set(parameterIndex, value);
    }
  }

  protected void storeParameter(String parameterName, SQLValue value) {
    parametersMap.put(new CaseInsensitiveString(parameterName), value);
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterIndex, sqlType);
    registerParameter(parameterIndex, new SQLValue.SQLRegisteredParameter(parameterIndex, sqlType));
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterIndex, sqlType, scale);
    registerParameter(parameterIndex, new SQLValue.SQLRegisteredParameter(parameterIndex, sqlType, scale));
  }

  @Override
  public boolean wasNull() throws SQLException {
    return ((CallableStatement) statement).wasNull();
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterIndex, sqlType, typeName);
    registerParameter(parameterIndex, new SQLValue.SQLRegisteredParameter(parameterIndex, sqlType, typeName));

  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterName, sqlType);
    registeredValuesMap.put(new CaseInsensitiveString(parameterName), new SQLValue.SQLRegisteredParameter(parameterName, sqlType));
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterName, sqlType, scale);
    registeredValuesMap.put(new CaseInsensitiveString(parameterName), new SQLValue.SQLRegisteredParameter(parameterName, sqlType, scale));
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
    ((CallableStatement) getActiveStatement()).registerOutParameter(parameterName, sqlType, typeName);
    registeredValuesMap.put(new CaseInsensitiveString(parameterName), new SQLValue.SQLRegisteredParameter(parameterName, sqlType, typeName));
  }

  @Override
  public String getString(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getString(parameterIndex);
  }

  @Override
  public boolean getBoolean(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBoolean(parameterIndex);
  }

  @Override
  public byte getByte(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getByte(parameterIndex);
  }

  @Override
  public short getShort(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getShort(parameterIndex);
  }

  @Override
  public int getInt(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getInt(parameterIndex);
  }

  @Override
  public long getLong(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getLong(parameterIndex);
  }

  @Override
  public float getFloat(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getFloat(parameterIndex);
  }

  @Override
  public double getDouble(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDouble(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBigDecimal(parameterIndex, scale);
  }

  @Override
  public byte[] getBytes(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBytes(parameterIndex);
  }

  @Override
  public Date getDate(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDate(parameterIndex);
  }

  @Override
  public Time getTime(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTime(parameterIndex);
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTimestamp(parameterIndex);
  }

  @Override
  public Object getObject(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getObject(parameterIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBigDecimal(parameterIndex);
  }

  @Override
  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getObject(parameterIndex, map);
  }

  @Override
  public Ref getRef(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getRef(parameterIndex);
  }

  @Override
  public Blob getBlob(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBlob(parameterIndex);
  }

  @Override
  public Clob getClob(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getClob(parameterIndex);
  }

  @Override
  public Array getArray(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getArray(parameterIndex);
  }

  @Override
  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDate(parameterIndex, cal);
  }

  @Override
  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTime(parameterIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTimestamp(parameterIndex, cal);
  }

  @Override
  public URL getURL(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getURL(parameterIndex);
  }

  @Override
  public String getString(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getString(parameterName);
  }

  @Override
  public boolean getBoolean(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBoolean(parameterName);
  }

  @Override
  public byte getByte(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getByte(parameterName);
  }

  @Override
  public short getShort(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getShort(parameterName);
  }

  @Override
  public int getInt(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getInt(parameterName);
  }

  @Override
  public long getLong(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getLong(parameterName);
  }

  @Override
  public float getFloat(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getFloat(parameterName);
  }

  @Override
  public double getDouble(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDouble(parameterName);
  }

  @Override
  public byte[] getBytes(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBytes(parameterName);
  }

  @Override
  public Date getDate(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDate(parameterName);
  }

  @Override
  public Time getTime(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTime(parameterName);
  }

  @Override
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTimestamp(parameterName);
  }

  @Override
  public Object getObject(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getObject(parameterName);
  }

  @Override
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBigDecimal(parameterName);
  }

  @Override
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getObject(parameterName, map);
  }

  @Override
  public Ref getRef(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getRef(parameterName);
  }

  @Override
  public Blob getBlob(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getBlob(parameterName);
  }

  @Override
  public Clob getClob(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getClob(parameterName);
  }

  @Override
  public Array getArray(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getArray(parameterName);
  }

  @Override
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getDate(parameterName, cal);
  }

  @Override
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTime(parameterName, cal);
  }

  @Override
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getTimestamp(parameterName, cal);
  }

  @Override
  public URL getURL(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getURL(parameterName);
  }

  @Override
  public RowId getRowId(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getRowId(parameterIndex);
  }

  @Override
  public RowId getRowId(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getRowId(parameterName);
  }

  @Override
  public NClob getNClob(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNClob(parameterIndex);
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNClob(parameterName);
  }

  @Override
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getSQLXML(parameterIndex);
  }

  @Override
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getSQLXML(parameterName);
  }

  @Override
  public String getNString(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNString(parameterIndex);
  }

  @Override
  public String getNString(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNString(parameterName);
  }

  @Override
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNCharacterStream(parameterIndex);
  }

  @Override
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getNCharacterStream(parameterName);
  }

  @Override
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getCharacterStream(parameterIndex);
  }

  @Override
  public Reader getCharacterStream(String parameterName) throws SQLException {
    return ((CallableStatement) getActiveStatement()).getCharacterStream(parameterName);
  }

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
}
