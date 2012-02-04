/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author domenbasic
 */
public abstract class AbstractDataSourceImpl implements DbDataSourceFactory.DbDataSourceImpl {

  @Override
  public void setSource(Object source){
    throw new UnsupportedOperationException();
  }

  @Override
  public void setColumnReader(ColumnNameReader columnReader) {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract void clearSharedResults();

  @Override
  public abstract void doDeleteRow() throws SQLException;

  @Override
  public abstract int getType(int columnIndex) throws SQLException;

  @Override
  public abstract int getType(String columnName) throws SQLException;

  @Override
  public abstract void startUpdate() throws SQLException;

  @Override
  public abstract void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException;

  @Override
  public abstract void setCountSql(String countSql) throws SQLException;

  @Override
  public abstract String getCountSql();

  @Override
  public abstract Connection getConnection();

  @Override
  public abstract void setConnection(Connection connection) throws SQLException;

  @Override
  public abstract String getSelectSql();

  @Override
  public abstract void setSelectSql(String selectSql) throws SQLException;

  @Override
  public abstract String getUpdateTableName();

  @Override
  public abstract void setUpdateTableName(String updateTableName);

  @Override
  public abstract int getColumnIndex(String columnName) throws SQLException;

  @Override
  public abstract int getRowCount();

  @Override
  public abstract boolean isDataLoaded();

  @Override
  public abstract boolean loadData(boolean reload, int oldRow);

  @Override
  public abstract int getColumnCount() throws SQLException;

  @Override
  public abstract Object getValueAt(int rowIndex, int columnIndex) throws SQLException;

  @Override
  public abstract Object getValueAt(int rowIndex, String columnName) throws SQLException;

  @Override
  public abstract Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException;

  @Override
  public abstract ResultSet getResultSet() throws SQLException;

  @Override
  public abstract String getColumnName(int columnIndex) throws SQLException;

  @Override
  public abstract boolean isUpdating() throws SQLException;

  @Override
  public abstract void updateRefreshPending();

  @Override
  public abstract Map<Integer, Map<String, Object>> getStoredUpdates();

  @Override
  public abstract boolean hasChanged(int columnIndex) throws SQLException;

  @Override
  public abstract boolean hasChanged(String columnName) throws SQLException;

  @Override
  public abstract Object getOldValue(int columnIndex) throws SQLException;

  @Override
  public abstract Object getOldValue(String columnName) throws SQLException;

  @Override
  public abstract boolean wasUpdated(int row, String columnName) throws SQLException;

  @Override
  public abstract boolean isPending(String columnName, int row) throws SQLException;

  @Override
  public abstract void storeUpdates(boolean insert) throws SQLException;

  @Override
  public abstract String getCatalogName();

  @Override
  public abstract void setCatalogName(String catalogName);

  @Override
  public abstract String getSchemaName();

  @Override
  public abstract void setSchemaName(String schemaName);

  @Override
  public abstract String[] getUniqueID();

  @Override
  public abstract void setUniqueID(String[] uniqueID);

  @Override
  public abstract String getDelimiterLeft();

  @Override
  public abstract void setDelimiterLeft(String delimiterLeft);

  @Override
  public abstract String getDelimiterRight();

  @Override
  public abstract void setDelimiterRight(String delimiterRight);

  @Override
  public abstract Set<String> getUpdateColumnNames();

  @Override
  public abstract void addUpdateColumnName(String... fieldNames);

  @Override
  public abstract void removeUpdateColumnName(String... fieldNames);

  @Override
  public abstract String[] getGetValueColumns();

  @Override
  public abstract void setGetValueColumns(String[] columns);

  @Override
  public abstract void setReadOnly(boolean readOnly);

  @Override
  public abstract boolean isReadOnly();

  @Override
  public abstract boolean isRefreshPending();

  @Override
  public abstract void filterChanged() throws SQLException;

  @Override
  public void setProvider(String providerClassName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public abstract boolean fireEvents();

  @Override
  public abstract CachedRowSet getCachedRowSet() throws SQLException;

  @Override
  public abstract DbDataSourceImpl copy(DbDataSource owner);

  @Override
  public abstract void loadData(DbDataSourceImpl dataSource, int oldRow);

  @Override
  public abstract void destroy();

  @Override
  public abstract String getDataSourceName();

  @Override
  public abstract void setDataSourceName(String name) throws SQLException;

  @Override
  public String getUsername() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setUsername(String name) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getPassword() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setPassword(String password) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getTransactionIsolation() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getCommand() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCommand(String cmd) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getMaxRows() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean getEscapeProcessing() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setType(int type) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setConcurrency(int concurrency) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
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
  public void setObject(int parameterIndex, Object x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setRef(int i, Ref x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int i, Blob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int i, Clob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setArray(int i, Array x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clearParameters() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void execute() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addRowSetListener(RowSetListener listener) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void removeRowSetListener(RowSetListener listener) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNString(String parameterName, String value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public abstract boolean next() throws SQLException;

  @Override
  public abstract void close() throws SQLException;

  @Override
  public abstract boolean wasNull() throws SQLException;

  @Override
  public abstract String getString(int columnIndex) throws SQLException;

  @Override
  public abstract boolean getBoolean(int columnIndex) throws SQLException;

  @Override
  public abstract byte getByte(int columnIndex) throws SQLException;

  @Override
  public abstract short getShort(int columnIndex) throws SQLException;

  @Override
  public abstract int getInt(int columnIndex) throws SQLException;

  @Override
  public abstract long getLong(int columnIndex) throws SQLException;

  @Override
  public abstract float getFloat(int columnIndex) throws SQLException;

  @Override
  public abstract double getDouble(int columnIndex) throws SQLException;

  @Override
  public abstract BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException;

  @Override
  public abstract byte[] getBytes(int columnIndex) throws SQLException;

  @Override
  public abstract Date getDate(int columnIndex) throws SQLException;

  @Override
  public abstract Time getTime(int columnIndex) throws SQLException;

  @Override
  public abstract Timestamp getTimestamp(int columnIndex) throws SQLException;

  @Override
  public abstract InputStream getAsciiStream(int columnIndex) throws SQLException;

  @Override
  public abstract InputStream getBinaryStream(int columnIndex) throws SQLException;

  @Override
  public abstract String getString(String columnLabel) throws SQLException;

  @Override
  public abstract boolean getBoolean(String columnLabel) throws SQLException;

  @Override
  public abstract byte getByte(String columnLabel) throws SQLException;

  @Override
  public abstract short getShort(String columnLabel) throws SQLException;

  @Override
  public abstract int getInt(String columnLabel) throws SQLException;

  @Override
  public abstract long getLong(String columnLabel) throws SQLException;

  @Override
  public abstract float getFloat(String columnLabel) throws SQLException;

  @Override
  public abstract double getDouble(String columnLabel) throws SQLException;

  @Override
  public abstract BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException;

  @Override
  public abstract byte[] getBytes(String columnLabel) throws SQLException;

  @Override
  public abstract Date getDate(String columnLabel) throws SQLException;

  @Override
  public abstract Time getTime(String columnLabel) throws SQLException;

  @Override
  public abstract Timestamp getTimestamp(String columnLabel) throws SQLException;

  @Override
  public abstract InputStream getAsciiStream(String columnLabel) throws SQLException;

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a stream of two-byte
   * Unicode characters. The first byte is the high byte; the second
   * byte is the low byte.
   *
   * The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <code>LONGVARCHAR</code> values.
   * The JDBC technology-enabled driver will
   * do any necessary conversion from the database format into Unicode.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream.
   * Also, a stream may return <code>0</code> when the method
   * <code>InputStream.available</code> is called, whether there
   * is data available or not.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value
   *         as a stream of two-byte Unicode characters.
   *         If the value is SQL <code>NULL</code>, the value returned
   *         is <code>null</code>.
   * @exception SQLException if a database access error occurs
   * @deprecated use <code>getCharacterStream</code> instead
   */
  @Deprecated
  @Override
  public InputStream getUnicodeStream(String columnName) throws SQLException {
    throw new RuntimeException("Unsupported operation");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * as a stream of two-byte Unicode characters. The first byte is
   * the high byte; the second byte is the low byte.
   *
   * The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <code>LONGVARCHAR</code>values.  The
   * JDBC driver will do any necessary conversion from the database
   * format into Unicode.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream.
   * Also, a stream may return <code>0</code> when the method
   * <code>InputStream.available</code>
   * is called, whether there is data available or not.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value
   *         as a stream of two-byte Unicode characters;
   *         if the value is SQL <code>NULL</code>, the value returned is
   *         <code>null</code>
   * @exception SQLException if a database access error occurs
   * @deprecated use <code>getCharacterStream</code> in place of
   *              <code>getUnicodeStream</code>
   */
  @Deprecated
  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new RuntimeException("Unsupported operation");
  }

  @Override
  public abstract InputStream getBinaryStream(String columnLabel) throws SQLException;

  @Override
  public abstract SQLWarning getWarnings() throws SQLException;

  @Override
  public abstract void clearWarnings() throws SQLException;

  @Override
  public abstract String getCursorName() throws SQLException;

  @Override
  public abstract ResultSetMetaData getMetaData() throws SQLException;

  @Override
  public abstract Object getObject(int columnIndex) throws SQLException;

  @Override
  public abstract Object getObject(String columnLabel) throws SQLException;

  @Override
  public abstract int findColumn(String columnLabel) throws SQLException;

  @Override
  public abstract Reader getCharacterStream(int columnIndex) throws SQLException;

  @Override
  public abstract Reader getCharacterStream(String columnLabel) throws SQLException;

  @Override
  public abstract BigDecimal getBigDecimal(int columnIndex) throws SQLException;

  @Override
  public abstract BigDecimal getBigDecimal(String columnLabel) throws SQLException;

  @Override
  public abstract boolean isBeforeFirst() throws SQLException;

  @Override
  public abstract boolean isAfterLast() throws SQLException;

  @Override
  public abstract boolean isFirst() throws SQLException;

  @Override
  public abstract boolean isLast() throws SQLException;

  @Override
  public abstract void beforeFirst() throws SQLException;

  @Override
  public abstract void afterLast() throws SQLException;

  @Override
  public abstract boolean first() throws SQLException;

  @Override
  public abstract boolean last() throws SQLException;

  @Override
  public abstract int getRow() throws SQLException;

  @Override
  public abstract boolean absolute(int row) throws SQLException;

  @Override
  public abstract boolean relative(int rows) throws SQLException;

  @Override
  public abstract boolean previous() throws SQLException;

  @Override
  public abstract void setFetchDirection(int direction) throws SQLException;

  @Override
  public abstract int getFetchDirection() throws SQLException;

  @Override
  public abstract void setFetchSize(int rows) throws SQLException;

  @Override
  public abstract int getFetchSize() throws SQLException;

  @Override
  public abstract int getType() throws SQLException;

  @Override
  public abstract int getConcurrency() throws SQLException;

  @Override
  public abstract boolean rowUpdated() throws SQLException;

  @Override
  public abstract boolean rowInserted() throws SQLException;

  @Override
  public abstract boolean rowDeleted() throws SQLException;

  @Override
  public abstract void updateNull(int columnIndex) throws SQLException;

  @Override
  public abstract void updateBoolean(int columnIndex, boolean x) throws SQLException;

  @Override
  public abstract void updateByte(int columnIndex, byte x) throws SQLException;

  @Override
  public abstract void updateShort(int columnIndex, short x) throws SQLException;

  @Override
  public abstract void updateInt(int columnIndex, int x) throws SQLException;

  @Override
  public abstract void updateLong(int columnIndex, long x) throws SQLException;

  @Override
  public abstract void updateFloat(int columnIndex, float x) throws SQLException;

  @Override
  public abstract void updateDouble(int columnIndex, double x) throws SQLException;

  @Override
  public abstract void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException;

  @Override
  public abstract void updateString(int columnIndex, String x) throws SQLException;

  @Override
  public abstract void updateBytes(int columnIndex, byte[] x) throws SQLException;

  @Override
  public abstract void updateDate(int columnIndex, Date x) throws SQLException;

  @Override
  public abstract void updateTime(int columnIndex, Time x) throws SQLException;

  @Override
  public abstract void updateTimestamp(int columnIndex, Timestamp x) throws SQLException;

  @Override
  public abstract void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException;

  @Override
  public abstract void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException;

  @Override
  public abstract void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException;

  @Override
  public abstract void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException;

  @Override
  public abstract void updateObject(int columnIndex, Object x) throws SQLException;

  @Override
  public abstract void updateNull(String columnLabel) throws SQLException;

  @Override
  public abstract void updateBoolean(String columnLabel, boolean x) throws SQLException;

  @Override
  public abstract void updateByte(String columnLabel, byte x) throws SQLException;

  @Override
  public abstract void updateShort(String columnLabel, short x) throws SQLException;

  @Override
  public abstract void updateInt(String columnLabel, int x) throws SQLException;

  @Override
  public abstract void updateLong(String columnLabel, long x) throws SQLException;

  @Override
  public abstract void updateFloat(String columnLabel, float x) throws SQLException;

  @Override
  public abstract void updateDouble(String columnLabel, double x) throws SQLException;

  @Override
  public abstract void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException;

  @Override
  public abstract void updateString(String columnLabel, String x) throws SQLException;

  @Override
  public abstract void updateBytes(String columnLabel, byte[] x) throws SQLException;

  @Override
  public abstract void updateDate(String columnLabel, Date x) throws SQLException;

  @Override
  public abstract void updateTime(String columnLabel, Time x) throws SQLException;

  @Override
  public abstract void updateTimestamp(String columnLabel, Timestamp x) throws SQLException;

  @Override
  public abstract void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException;

  @Override
  public abstract void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException;

  @Override
  public abstract void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException;

  @Override
  public abstract void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException;

  @Override
  public abstract void updateObject(String columnLabel, Object x) throws SQLException;

  @Override
  public abstract void insertRow() throws SQLException;

  @Override
  public abstract void updateRow() throws SQLException;

  @Override
  public abstract void deleteRow() throws SQLException;

  @Override
  public abstract void refreshRow() throws SQLException;

  @Override
  public abstract void cancelRowUpdates() throws SQLException;

  @Override
  public abstract void moveToInsertRow() throws SQLException;

  @Override
  public abstract void moveToCurrentRow() throws SQLException;

  @Override
  public abstract Statement getStatement() throws SQLException;

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as an <code>Object</code>
   * in the Java programming language.
   * If the value is an SQL <code>NULL</code>,
   * the driver returns a Java <code>null</code>.
   * This method uses the specified <code>Map</code> object for
   * custom mapping if appropriate.
   *
   * @param colName the name of the column from which to retrieve the value
   * @param map a <code>java.util.Map</code> object that contains the mapping
   * from SQL type names to classes in the Java programming language
   * @return an <code>Object</code> representing the SQL value in the
   *         specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  @Override
  public abstract Ref getRef(int columnIndex) throws SQLException;

  @Override
  public abstract Blob getBlob(int columnIndex) throws SQLException;

  @Override
  public abstract Clob getClob(int columnIndex) throws SQLException;

  @Override
  public abstract Array getArray(int columnIndex) throws SQLException;

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as an <code>Object</code>
   * in the Java programming language.
   * If the value is an SQL <code>NULL</code>,
   * the driver returns a Java <code>null</code>.
   * This method uses the specified <code>Map</code> object for
   * custom mapping if appropriate.
   *
   * @param colName the name of the column from which to retrieve the value
   * @param map a <code>java.util.Map</code> object that contains the mapping
   * from SQL type names to classes in the Java programming language
   * @return an <code>Object</code> representing the SQL value in the
   *         specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Object getObject(String columnName, Map<String, Class<?>> map) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  @Override
  public abstract Ref getRef(String columnLabel) throws SQLException;

  @Override
  public abstract Blob getBlob(String columnLabel) throws SQLException;

  @Override
  public abstract Clob getClob(String columnLabel) throws SQLException;

  @Override
  public abstract Array getArray(String columnLabel) throws SQLException;

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Date</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the date if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the date
   * @return the column value as a <code>java.sql.Date</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Date getDate(String columnName, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Date</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the date if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the date
   * @return the column value as a <code>java.sql.Date</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Time</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the time if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the time
   * @return the column value as a <code>java.sql.Time</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Time getTime(String columnName, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Time</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the time if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the time
   * @return the column value as a <code>java.sql.Time</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the timestamp if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the timestamp
   * @return the column value as a <code>java.sql.Timestamp</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object
   * in the Java programming language.
   * This method uses the given calendar to construct an appropriate millisecond
   * value for the timestamp if the underlying database does not store
   * timezone information.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object
   * to use in constructing the timestamp
   * @return the column value as a <code>java.sql.Timestamp</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  @Override
  public abstract URL getURL(int columnIndex) throws SQLException;

  @Override
  public abstract URL getURL(String columnLabel) throws SQLException;

  @Override
  public abstract void updateRef(int columnIndex, Ref x) throws SQLException;

  @Override
  public abstract void updateRef(String columnLabel, Ref x) throws SQLException;

  @Override
  public abstract void updateBlob(int columnIndex, Blob x) throws SQLException;

  @Override
  public abstract void updateBlob(String columnLabel, Blob x) throws SQLException;

  @Override
  public abstract void updateClob(int columnIndex, Clob x) throws SQLException;

  @Override
  public abstract void updateClob(String columnLabel, Clob x) throws SQLException;

  @Override
  public abstract void updateArray(int columnIndex, Array x) throws SQLException;

  @Override
  public abstract void updateArray(String columnLabel, Array x) throws SQLException;

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getHoldability() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isClosed() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNString(int columnIndex, String nString) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNString(String columnLabel, String nString) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getUrl() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setUrl(String url) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
