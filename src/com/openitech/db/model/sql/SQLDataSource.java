/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.2 $
 */
package com.openitech.db.model.sql;

import com.openitech.db.model.*;
import com.openitech.Settings;
import com.openitech.db.ConnectionManager;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.concurrent.DataSourceEvent;
import com.openitech.db.model.concurrent.PendingSqlParameter;
import com.openitech.db.model.concurrent.PendingValue;
import com.openitech.formats.FormatFactory;
import com.openitech.util.Equals;
import com.openitech.util.OwnerFrame;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSetListener;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author uros
 */
public class SQLDataSource implements DbDataSourceImpl {

  private String selectSql;
  private String countSql;
  private String preparedSelectSql;
  private String preparedCountSql;
  private String updateTableName;
  private transient PreparedStatement selectStatement;
  private transient PreparedStatement countStatement;
  private List<PrimaryKey> primaryKeys;
  private transient ResultSet selectResultSet = null;
  private int count = 0;
  private int fetchSize = 54;
  private Map<Integer, Map<String, Object>> storedUpdates = new HashMap<Integer, Map<String, Object>>();
  private boolean inserting = false;
  private transient ResultSetMetaData metaData = null;
  private DbDataSourceHashMap<String, Integer> columnMapping = new DbDataSourceHashMap<String, Integer>();
  private boolean[] storedResult = new boolean[]{false, false};
  private final Semaphore semaphore = new Semaphore(1);
  private transient Map<CacheKey, CacheEntry<String, Object>> cache = new HashMap<CacheKey, CacheEntry<String, Object>>();
  private final Runnable events = new RunnableEvents(this);
  private Connection connection = null;
  private transient Map<String, PreparedStatement> cachedStatements = new HashMap<String, PreparedStatement>();
  /**
   * Holds value of property uniqueID.
   */
  private String[] uniqueID;
  private boolean refreshPending = false;
  DbDataSource owner;
  private boolean readOnly = false;

  /** Creates a new instance of DbDataSource */
  public SQLDataSource(DbDataSource owner) {
    this.owner = owner;
  }
  /**
   * Holds value of property updateColumnNames.
   */
  private java.util.Set<String> updateColumnNames = new java.util.HashSet<String>();
  private java.util.Set<String> updateColumnNamesCS = new java.util.HashSet<String>(); //case sensitive

  public SQLDataSource(DbDataSource owner, PreparedStatement psEvidenca, PreparedStatement psEvidencaCount, List<Object> params) {
    this.owner = owner;
    this.selectStatement = psEvidenca;
    this.countStatement = psEvidencaCount;
    owner.setParameters(params, false);
  }
  /**
   * Getter for property updateFieldNames.
   * @return Value of property updateFieldNames.
   */
  @Override
  public java.util.Set<String> getUpdateColumnNames() {
    return java.util.Collections.unmodifiableSet(updateColumnNamesCS);
  }

  @Override
  public void addUpdateColumnName(String... fieldNames) {
    for (String fieldName : fieldNames) {
      updateColumnNamesCS.add(fieldName);
      updateColumnNames.add(fieldName);
      updateColumnNames.add(fieldName.toUpperCase());
    }
  }

  @Override
  public void removeUpdateColumnName(String... fieldNames) {
    for (String fieldName : fieldNames) {
      updateColumnNamesCS.remove(fieldName);
      updateColumnNames.remove(fieldName);
      updateColumnNames.remove(fieldName.toUpperCase());
    }
  }
  /**
   * Holds value of property getValueColumns.
   */
  private List<String> getValueColumns = new ArrayList<String>();

  /**
   * Getter for property getValueColumns.
   * @return Value of property getValueColumns.
   */
  @Override
  public String[] getGetValueColumns() {
    return getValueColumns.toArray(new String[getValueColumns.size()]);
  }

  /**
   * Setter for property getValueColumns.
   * @param getValueColumns New value of property getValueColumns.
   */
  @Override
  public void setGetValueColumns(String[] columns) {
    getValueColumns.clear();
    for (String column : columns) {
      getValueColumns.add(column);
    }
  }

  /**
   * Updates the designated column with a <code>float	</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateFloat(String columnName, float x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>float</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateFloat(int columnIndex, float x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

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
  public Object getObject(String columnName, Map<String, Class<?>> map) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

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
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    throw new SQLException("Unsupported operation.");
  }

  /**
   * Updates the designated column with a <code>java.sql.Date</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateDate(String columnName, Date x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>short</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateShort(int columnIndex, short x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a <code>java.sql.Blob</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateBlob(String columnName, Blob x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a <code>java.sql.Array</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateArray(String columnName, Array x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>double</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateDouble(int columnIndex, double x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Timestamp</code>
   * value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Time</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateTime(int columnIndex, Time x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.BigDecimal</code>
   * value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>double</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateDouble(String columnName, double x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>null</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateNull(String columnName) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, null);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>long</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public long getLong(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, 0l, Number.class);
      return value == null ? null : value.longValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>int</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public int getInt(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, 0, Number.class);
      return value == null ? null : value.intValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>float</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public float getFloat(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, 0f, Number.class);
      return value == null ? null : value.floatValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>double</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public double getDouble(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, 0d, Number.class);
      return value == null ? null : value.doubleValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Date</code> object in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Date getDate(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Date.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Clob</code> object
   * in the Java programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @return a <code>Clob</code> object representing the SQL <code>CLOB</code>
   * value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Clob getClob(String colName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), colName, null, Clob.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>java.io.Reader</code> object.
   *
   * @param columnName the name of the column
   * @return a <code>java.io.Reader</code> object that contains the column
   * value; if the value is SQL <code>NULL</code>, the value returned is
   * <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Reader getCharacterStream(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Reader.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>byte</code> array in the Java programming language.
   * The bytes represent the raw values returned by the driver.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public byte[] getBytes(String columnName) throws SQLException {
    if (loadData()) {
      return (byte[]) getStoredValue(getRow(), columnName, new byte[]{}, Object.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a stream of
   * ASCII characters. The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <code>LONGVARCHAR</code> values.
   * The JDBC driver will
   * do any necessary conversion from the database format into ASCII.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream. Also, a
   * stream may return <code>0</code> when the method <code>available</code>
   * is called whether there is data available or not.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value
   * as a stream of one-byte ASCII characters.
   * If the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code>.
   * @exception SQLException if a database access error occurs
   */
  public InputStream getAsciiStream(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, InputStream.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Maps the given <code>ResultSet</code> column name to its
   * <code>ResultSet</code> column index.
   *
   * @param columnName the name of the column
   * @return the column index of the given column name
   * @exception SQLException if the <code>ResultSet</code> object
   * does not contain <code>columnName</code> or a database access error occurs
   */
  public int findColumn(String columnName) throws SQLException {
    if (loadData()) {
      return selectResultSet.findColumn(columnName);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>java.math.BigDecimal</code> with full precision.
   *
   * @param columnName the column name
   * @return the column value (full precision);
   * if the value is SQL <code>NULL</code>, the value returned is
   * <code>null</code> in the Java programming language.
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, BigDecimal.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a stream of uninterpreted
   * <code>byte</code>s.
   * The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <code>LONGVARBINARY</code>
   * values.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream. Also, a
   * stream may return <code>0</code> when the method <code>available</code>
   * is called whether there is data available or not.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value
   * as a stream of uninterpreted bytes;
   * if the value is SQL <code>NULL</code>, the result is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public InputStream getBinaryStream(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, InputStream.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Blob</code> object
   * in the Java programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @return a <code>Blob</code> object representing the SQL <code>BLOB</code>
   *         value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Blob getBlob(String colName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), colName, null, Blob.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>boolean</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>false</code>
   * @exception SQLException if a database access error occurs
   */
  public boolean getBoolean(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, false, Boolean.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>byte</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public byte getByte(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, (byte) 0, Number.class);
      return value == null ? null : value.byteValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * <p>Gets the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>Object</code> in the Java programming language.
   *
   * <p>This method will return the value of the given column as a
   * Java object.  The type of the Java object will be the default
   * Java object type corresponding to the column's SQL type,
   * following the mapping for built-in types specified in the JDBC
   * specification. If the value is an SQL <code>NULL</code>,
   * the driver returns a Java <code>null</code>.
   * <P>
   * This method may also be used to read database-specific
   * abstract data types.
   * <P>
   * In the JDBC 2.0 API, the behavior of the method
   * <code>getObject</code> is extended to materialize
   * data of SQL user-defined types.  When a column contains
   * a structured or distinct value, the behavior of this method is as
   * if it were a call to: <code>getObject(columnIndex,
   * this.getStatement().getConnection().getTypeMap())</code>.
   *
   * @param columnName the SQL name of the column
   * @return a <code>java.lang.Object</code> holding the column value
   * @exception SQLException if a database access error occurs
   */
  public Object getObject(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Object.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Ref</code> object
   * in the Java programming language.
   *
   * @param colName the column name
   * @return a <code>Ref</code> object representing the SQL <code>REF</code>
   *         value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Ref getRef(String colName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), colName, null, Ref.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>short</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public short getShort(String columnName) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnName, (short) 0, Number.class);
      return value == null ? null : value.shortValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>String</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public String getString(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, String.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Time</code> object in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Time getTime(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Time.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Timestamp</code> object.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Timestamp getTimestamp(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Timestamp.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.net.URL</code>
   * object in the Java programming language.
   *
   *
   * @param columnName the SQL name of the column
   * @return the column value as a <code>java.net.URL</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs
   *            or if a URL is malformed
   * @since 1.4
   */
  public URL getURL(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, URL.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

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
  public InputStream getUnicodeStream(String columnName) throws SQLException {
    throw new RuntimeException("Unsupported operation");
  }

  /**
   * Updates the designated column with a byte array value.
   *
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code>
   * or <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBytes(String columnName, byte[] x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a <code>java.sql.Ref</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateRef(String columnName, Ref x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Gives a nullable column a null value.
   *
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code>
   * or <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateNull(int columnIndex) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, null);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * <p>Gets the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>Object</code> in the Java programming language.
   *
   * <p>This method will return the value of the given column as a
   * Java object.  The type of the Java object will be the default
   * Java object type corresponding to the column's SQL type,
   * following the mapping for built-in types specified in the JDBC
   * specification. If the value is an SQL <code>NULL</code>,
   * the driver returns a Java <code>null</code>.
   *
   * <p>This method may also be used to read database-specific
   * abstract data types.
   *
   * In the JDBC 2.0 API, the behavior of method
   * <code>getObject</code> is extended to materialize
   * data of SQL user-defined types.  When a column contains
   * a structured or distinct value, the behavior of this method is as
   * if it were a call to: <code>getObject(columnIndex,
   * this.getStatement().getConnection().getTypeMap())</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a <code>java.lang.Object</code> holding the column value
   * @exception SQLException if a database access error occurs
   */
  public Object getObject(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Object.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>long</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public long getLong(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, 0l, Number.class);
      return value == null ? null : value.longValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * an <code>int</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public int getInt(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, 0, Number.class);
      return value == null ? null : value.intValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>float</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public float getFloat(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, 0f, Number.class);
      return value == null ? null : value.floatValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>double</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public double getDouble(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, 0d, Number.class);
      return value == null ? null : value.doubleValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Date</code> object in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Date getDate(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Date.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Clob</code> object
   * in the Java programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Clob</code> object representing the SQL
   *         <code>CLOB</code> value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Clob getClob(int i) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), i, null, Clob.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>java.io.Reader</code> object.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a <code>java.io.Reader</code> object that contains the column
   * value; if the value is SQL <code>NULL</code>, the value returned is
   * <code>null</code> in the Java programming language.
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Reader.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>byte</code> array in the Java programming language.
   * The bytes represent the raw values returned by the driver.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public byte[] getBytes(int columnIndex) throws SQLException {
    if (loadData()) {
      return (byte[]) getStoredValue(getRow(), columnIndex, new byte[]{}, Object.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a stream of ASCII characters. The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <char>LONGVARCHAR</char> values.
   * The JDBC driver will
   * do any necessary conversion from the database format into ASCII.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream.  Also, a
   * stream may return <code>0</code> when the method
   * <code>InputStream.available</code>
   * is called whether there is data available or not.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value
   * as a stream of one-byte ASCII characters;
   * if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, InputStream.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as an <code>Array</code> object
   * in the Java programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return an <code>Array</code> object representing the SQL
   *         <code>ARRAY</code> value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Array getArray(String columnName) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, Array.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as an <code>Array</code> object
   * in the Java programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return an <code>Array</code> object representing the SQL
   *         <code>ARRAY</code> value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Array getArray(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Array.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the given row number in
   * this <code>ResultSet</code> object.
   *
   * <p>If the row number is positive, the cursor moves to
   * the given row number with respect to the
   * beginning of the result set.  The first row is row 1, the second
   * is row 2, and so on.
   *
   * <p>If the given row number is negative, the cursor moves to
   * an absolute row position with respect to
   * the end of the result set.  For example, calling the method
   * <code>absolute(-1)</code> positions the
   * cursor on the last row; calling the method <code>absolute(-2)</code>
   * moves the cursor to the next-to-last row, and so on.
   *
   * <p>An attempt to position the cursor beyond the first/last row in
   * the result set leaves the cursor before the first row or after
   * the last row.
   *
   * <p><B>Note:</B> Calling <code>absolute(1)</code> is the same
   * as calling <code>first()</code>. Calling <code>absolute(-1)</code>
   * is the same as calling <code>last()</code>.
   *
   * @param row the number of the row to which the cursor should move.
   *        A positive number indicates the row number counting from the
   *        beginning of the result set; a negative number indicates the
   *        row number counting from the end of the result set
   * @return <code>true</code> if the cursor is on the result set;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error
   * occurs, or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public boolean absolute(int row) throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.absolute(row);
      if (res) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a
   * <code>java.math.BigDecimal</code> with full precision.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value (full precision);
   * if the value is SQL <code>NULL</code>, the value returned is
   * <code>null</code> in the Java programming language.
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, BigDecimal.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a binary stream of
   * uninterpreted bytes. The value can then be read in chunks from the
   * stream. This method is particularly
   * suitable for retrieving large <code>LONGVARBINARY</code> values.
   *
   * <P><B>Note:</B> All the data in the returned stream must be
   * read prior to getting the value of any other column. The next
   * call to a getter method implicitly closes the stream.  Also, a
   * stream may return <code>0</code> when the method
   * <code>InputStream.available</code>
   * is called whether there is data available or not.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value
   *         as a stream of uninterpreted bytes;
   *         if the value is SQL <code>NULL</code>, the value returned is
   *         <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, InputStream.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Blob</code> object
   * in the Java programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Blob</code> object representing the SQL
   *         <code>BLOB</code> value in the specified column
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Blob getBlob(int i) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), i, null, Blob.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>boolean</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>false</code>
   * @exception SQLException if a database access error occurs
   */
  public boolean getBoolean(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, false, Boolean.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>byte</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public byte getByte(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, (byte) 0, Number.class);
      return value == null ? null : value.byteValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>Ref</code> object
   * in the Java programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Ref</code> object representing an SQL <code>REF</code>
   *         value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Ref getRef(int i) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), i, null, Ref.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>short</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>0</code>
   * @exception SQLException if a database access error occurs
   */
  public short getShort(int columnIndex) throws SQLException {
    if (loadData()) {
      Number value = getStoredValue(getRow(), columnIndex, (short) 0, Number.class);
      return value == null ? null : value.shortValue();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>String</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public String getString(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, String.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Time</code> object in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Time getTime(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Time.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.Timestamp</code> object in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   */
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Timestamp.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as a <code>java.net.URL</code>
   * object in the Java programming language.
   *
   *
   * @param columnIndex the index of the column 1 is the first, 2 is the second,...
   * @return the column value as a <code>java.net.URL</code> object;
   * if the value is SQL <code>NULL</code>,
   * the value returned is <code>null</code> in the Java programming language
   * @exception SQLException if a database access error occurs,
   *            or if a URL is malformed
   * @since 1.4
   */
  public URL getURL(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, URL.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new RuntimeException("Unsupported operation");
  }

  /**
   * Moves the cursor a relative number of rows, either positive or negative.
   * Attempting to move beyond the first/last row in the
   * result set positions the cursor before/after the
   * the first/last row. Calling <code>relative(0)</code> is valid, but does
   * not change the cursor position.
   *
   * <p>Note: Calling the method <code>relative(1)</code>
   * is identical to calling the method <code>next()</code> and
   * calling the method <code>relative(-1)</code> is identical
   * to calling the method <code>previous()</code>.
   *
   * @param rows an <code>int</code> specifying the number of rows to
   *        move from the current row; a positive number moves the cursor
   *        forward; a negative number moves the cursor backward
   * @return <code>true</code> if the cursor is on a row;
   *         <code>false</code> otherwise
   * @exception SQLException if a database access error occurs,
   *            there is no current row, or the result set type is
   *            <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public boolean relative(int rows) throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.relative(rows);
      if (res) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Gives a hint as to the direction in which the rows in this
   * <code>ResultSet</code> object will be processed.
   * The initial value is determined by the
   * <code>Statement</code> object
   * that produced this <code>ResultSet</code> object.
   * The fetch direction may be changed at any time.
   *
   * @param direction an <code>int</code> specifying the suggested
   *        fetch direction; one of <code>ResultSet.FETCH_FORWARD</code>,
   *        <code>ResultSet.FETCH_REVERSE</code>, or
   *        <code>ResultSet.FETCH_UNKNOWN</code>
   * @exception SQLException if a database access error occurs or
   * the result set type is <code>TYPE_FORWARD_ONLY</code> and the fetch
   * direction is not <code>FETCH_FORWARD</code>
   * @see Statement#setFetchDirection
   * @see #getFetchDirection
   * @since 1.2
   */
  public void setFetchDirection(int direction) throws SQLException {
    if (isDataLoaded()) {
      selectResultSet.setFetchDirection(direction);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Gives the JDBC driver a hint as to the number of rows that should
   * be fetched from the database when more rows are needed for this
   * <code>ResultSet</code> object.
   * If the fetch size specified is zero, the JDBC driver
   * ignores the value and is free to make its own best guess as to what
   * the fetch size should be.  The default value is set by the
   * <code>Statement</code> object
   * that created the result set.  The fetch size may be changed at any time.
   *
   * @param rows the number of rows to fetch
   * @exception SQLException if a database access error occurs or the
   * condition <code>0 <= rows <= Statement.getMaxRows()</code> is not satisfied
   * @see #getFetchSize
   * @since 1.2
   */
  public void setFetchSize(int rows) throws SQLException {
    this.fetchSize = rows;
    if (isDataLoaded()) {
      selectResultSet.setFetchSize(rows);
    }
  }

  /**
   * Updates the designated column with a <code>boolean</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBoolean(String columnName, boolean x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateObject(String columnName, Object x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Date</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateDate(int columnIndex, Date x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a <code>java.sql.Clob</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateClob(String columnName, Clob x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Timestamp</code>
   * value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>byte</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateByte(String columnName, byte x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>short</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateShort(String columnName, short x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>long</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateLong(String columnName, long x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Ref</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Array</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateArray(int columnIndex, Array x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }

  }

  /**
   * Updates the designated column with a <code>java.math.BigDecimal</code>
   * value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Clob</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>long</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateLong(int columnIndex, long x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>byte</code> array value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>byte</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateByte(int columnIndex, byte x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Time</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateTime(String columnName, Time x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>java.sql.Blob</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.4
   */
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>String</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateString(int columnIndex, String x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param scale for <code>java.sql.Types.DECIMAL</code>
   *  or <code>java.sql.Types.NUMERIC</code> types,
   *  this is the number of digits after the decimal point.  For all other
   *  types this value will be ignored.
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateObject(String columnName, Object x, int scale) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, new Scale("updateObject", x, scale));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateObject(int columnIndex, Object x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with an <code>int</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateInt(String columnName, int x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a character stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param reader the <code>java.io.Reader</code> object containing
   *        the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, new Scale("updateCharacterStream", reader, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a binary stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, new Scale("updateBinaryStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.math.BigDecimal</code> in the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @param scale the number of digits to the right of the decimal point
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   * @deprecated
   */
  @Deprecated
  public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnName, null, BigDecimal.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with an ascii stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, new Scale("updateAsciiStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>boolean</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Reports whether
   * the last column read had a value of SQL <code>NULL</code>.
   * Note that you must first call one of the getter methods
   * on a column to try to read its value and then call
   * the method <code>wasNull</code> to see if the value read was
   * SQL <code>NULL</code>.
   *
   * @return <code>true</code> if the last column value read was SQL
   *         <code>NULL</code> and <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   */
  public boolean wasNull() throws SQLException {
    if (isDataLoaded()) {
      return storedResult[0] ? storedResult[1] : selectResultSet.wasNull();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a <code>String</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateString(String columnName, String x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public boolean canUpdateRow() {
    return (JOptionPane.showOptionDialog(OwnerFrame.getInstance().getOwner(),
            "Ali naj shranim spremembe ?",
            "Preveri",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Da", "Ne"},
            "Ne") == JOptionPane.YES_OPTION);
  }

  /**
   * Updates the underlying database with the new contents of the
   * current row of this <code>ResultSet</code> object.
   * This method cannot be called when the cursor is on the insert row.
   *
   * @exception SQLException if a database access error occurs or
   * if this method is called when the cursor is on the insert row
   * @since 1.2
   */
  public void updateRow() throws SQLException {
    boolean storeUpdates = true;
    try {
      owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.UPDATE_ROW));
    } catch (Exception err) {
      storeUpdates = false;
    }
    if (storeUpdates) {
      storeUpdates(rowInserted());
    }
    try {
      owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.ROW_UPDATED));
    } catch (Exception err) {
      //
    }
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param scale for <code>java.sql.Types.DECIMA</code>
   *  or <code>java.sql.Types.NUMERIC</code> types,
   *  this is the number of digits after the decimal point.  For all other
   *  types this value will be ignored.
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, new Scale("updateObject", x, scale));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with an <code>int</code> value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateInt(int columnIndex, int x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Updates the designated column with a character stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, new Scale("updateCharacterStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with a binary stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, new Scale("updateBinaryStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the  number, types and properties of
   * this <code>ResultSet</code> object's columns.
   *
   * @return the description of this <code>ResultSet</code> object's columns
   * @exception SQLException if a database access error occurs
   */
  public ResultSetMetaData getMetaData() throws SQLException {
    if (this.metaData != null) {
      return this.metaData;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the fetch size for this
   * <code>ResultSet</code> object.
   *
   * @return the current fetch size for this <code>ResultSet</code> object
   * @exception SQLException if a database access error occurs
   * @see #setFetchSize
   * @since 1.2
   */
  public int getFetchSize() throws SQLException {
    return this.fetchSize;
  }

  /**
   * Retrieves the fetch direction for this
   * <code>ResultSet</code> object.
   *
   * @return the current fetch direction for this <code>ResultSet</code> object
   * @exception SQLException if a database access error occurs
   * @see #setFetchDirection
   * @since 1.2
   */
  public int getFetchDirection() throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getFetchDirection();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the name of the SQL cursor used by this <code>ResultSet</code>
   * object.
   *
   * <P>In SQL, a result table is retrieved through a cursor that is
   * named. The current row of a result set can be updated or deleted
   * using a positioned update/delete statement that references the
   * cursor name. To insure that the cursor has the proper isolation
   * level to support update, the cursor's <code>SELECT</code> statement
   * should be of the form <code>SELECT FOR UPDATE</code>. If
   * <code>FOR UPDATE</code> is omitted, the positioned updates may fail.
   *
   * <P>The JDBC API supports this SQL feature by providing the name of the
   * SQL cursor used by a <code>ResultSet</code> object.
   * The current row of a <code>ResultSet</code> object
   * is also the current row of this SQL cursor.
   *
   * <P><B>Note:</B> If positioned update is not supported, a
   * <code>SQLException</code> is thrown.
   *
   * @return the SQL name for this <code>ResultSet</code> object's cursor
   * @exception SQLException if a database access error occurs
   */
  public String getCursorName() throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getCursorName();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the concurrency mode of this <code>ResultSet</code> object.
   * The concurrency used is determined by the
   * <code>Statement</code> object that created the result set.
   *
   * @return the concurrency type, either
   *         <code>ResultSet.CONCUR_READ_ONLY</code>
   *         or <code>ResultSet.CONCUR_UPDATABLE</code>
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public int getConcurrency() throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getConcurrency();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the first row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public boolean first() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.first();
      if (res) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  private void doDeleteRow(ResultSet resultSet) throws SQLException {
    PreparedStatement delete;
    PrimaryKey key;
    for (Iterator<PrimaryKey> pk = primaryKeys.iterator(); pk.hasNext();) {
      key = pk.next();
      delete = key.getDeleteStatement(resultSet);
      delete.execute();
    }
  }

  @Override
  public void doDeleteRow() throws SQLException {
    doDeleteRow(getOpenSelectResultSet());
  }

  /**
   * Deletes the current row from this <code>ResultSet</code> object
   * and from the underlying database.  This method cannot be called when
   * the cursor is on the insert row.
   *
   * @exception SQLException if a database access error occurs
   * or if this method is called when the cursor is on the insert row
   * @since 1.2
   */
  public void deleteRow() throws SQLException {
    if (owner.isCanDeleteRows()) {
      if (isDataLoaded()) {
        boolean wasinserting = inserting;
        if (rowUpdated()) {
          cancelRowUpdates();
        }
        if (!wasinserting) {
          boolean deleteRow = true;
          try {
            owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.DELETE_ROW));
          } catch (Exception err) {
            deleteRow = false;
          }
          if (deleteRow) {
            ResultSet resultSet = getOpenSelectResultSet();
            int oldRow = resultSet.getRow();
            if (owner.isUpdateRowFireOnly()) {
              owner.fireDeleteRow(new StoreUpdatesEvent(owner, getRow(), false, null, null));
            } else {
              doDeleteRow(resultSet);
            }
            try {
              owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.ROW_DELETED));
            } catch (Exception err) {
              //
            }
            reload(oldRow);
          }
        }
      } else {
        throw new SQLException("Ni pripravljenih podatkov.");
      }
    }
  }

  /**
   * unlocks this <code>ResultSet</code> object's database and
   * JDBC resources immediately instead of waiting for
   * this to happen when it is automatically closed.
   *
   * <P><B>Note:</B> A <code>ResultSet</code> object
   * is automatically closed by the
   * <code>Statement</code> object that generated it when
   * that <code>Statement</code> object is closed,
   * re-executed, or is used to retrieve the next result from a
   * sequence of multiple results. A <code>ResultSet</code> object
   * is also automatically closed when it is garbage collected.
   *
   * @exception SQLException if a database access error occurs
   */
  public void close() throws SQLException {
    if (isDataLoaded()) {
      selectResultSet.close();
      selectResultSet = null;
      owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, -1, -1));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Clears all warnings reported on this <code>ResultSet</code> object.
   * After this method is called, the method <code>getWarnings</code>
   * returns <code>null</code> until a new warning is
   * reported for this <code>ResultSet</code> object.
   *
   * @exception SQLException if a database access error occurs
   */
  public void clearWarnings() throws SQLException {
    if (isDataLoaded()) {
      selectResultSet.clearWarnings();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Cancels the updates made to the current row in this
   * <code>ResultSet</code> object.
   * This method may be called after calling an
   * updater method(s) and before calling
   * the method <code>updateRow</code> to roll back
   * the updates made to a row.  If no updates have been made or
   * <code>updateRow</code> has already been called, this method has no
   * effect.
   *
   * @exception SQLException if a database access error
   *            occurs or if this method is called when the cursor is
   *            on the insert row
   * @since 1.2
   */
  public void cancelRowUpdates() throws SQLException {
    if (isDataLoaded()) {
      boolean cancelUpdates = true;
      try {
        owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.CANCEL_UPDATES));
      } catch (Exception err) {
        cancelUpdates = false;
      }
      if (cancelUpdates) {
        storedUpdates.remove(new Integer(getRow()));
        if (inserting) {
          inserting = false;
          owner.fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, getRowCount(), getRowCount()));
        }
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), selectResultSet.getRow()));
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the front of
   * this <code>ResultSet</code> object, just before the
   * first row. This method has no effect if the result set contains no rows.
   *
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public void beforeFirst() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      ;
      selectResultSet.beforeFirst();
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the end of
   * this <code>ResultSet</code> object, just after the
   * last row. This method has no effect if the result set contains no rows.
   *
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public void afterLast() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      ;
      selectResultSet.afterLast();
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the value of the designated column in the current row
   * of this <code>ResultSet</code> object as
   * a <code>java.sql.BigDecimal</code> in the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param scale the number of digits to the right of the decimal point
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   * @exception SQLException if a database access error occurs
   * @deprecated
   */
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, BigDecimal.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the current row number.  The first row is number 1, the
   * second number 2, and so on.
   *
   * @return the current row number; <code>0</code> if there is no current row
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public int getRow() throws SQLException {
    if (loadData()) {
      return inserting ? getRowCount() : getOpenSelectResultSet().getRow();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the <code>Statement</code> object that produced this
   * <code>ResultSet</code> object.
   * If the result set was generated some other way, such as by a
   * <code>DatabaseMetaData</code> method, this method returns
   * <code>null</code>.
   *
   * @return the <code>Statment</code> object that produced
   * this <code>ResultSet</code> object or <code>null</code>
   * if the result set was produced some other way
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public Statement getStatement() throws SQLException {
    return this.selectStatement;
  }

  /**
   * Retrieves the type of this <code>ResultSet</code> object.
   * The type is determined by the <code>Statement</code> object
   * that created the result set.
   *
   * @return <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>,
   *         or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public int getType() throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getType();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public int getType(int columnIndex) throws SQLException {
    if (getMetaData() != null) {
      return getMetaData().getColumnType(columnIndex);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public int getType(String columnName) throws SQLException {
    if (getMetaData() != null) {
      return getMetaData().getColumnType(columnMapping.checkedGet(columnName));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves the first warning reported by calls on this
   * <code>ResultSet</code> object.
   * Subsequent warnings on this <code>ResultSet</code> object
   * will be chained to the <code>SQLWarning</code> object that
   * this method returns.
   *
   * <P>The warning chain is automatically cleared each time a new
   * row is read.  This method may not be called on a <code>ResultSet</code>
   * object that has been closed; doing so will cause an
   * <code>SQLException</code> to be thrown.
   * <P>
   * <B>Note:</B> This warning chain only covers warnings caused
   * by <code>ResultSet</code> methods.  Any warning caused by
   * <code>Statement</code> methods
   * (such as reading OUT parameters) will be chained on the
   * <code>Statement</code> object.
   *
   * @return the first <code>SQLWarning</code> object reported or
   *         <code>null</code> if there are none
   * @exception SQLException if a database access error occurs or this method is
   *            called on a closed result set
   */
  public SQLWarning getWarnings() throws SQLException {
    if (isDataLoaded()) {
      return getOpenSelectResultSet().getWarnings();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Inserts the contents of the insert row into this
   * <code>ResultSet</code> object and into the database.
   * The cursor must be on the insert row when this method is called.
   *
   * @exception SQLException if a database access error occurs,
   * if this method is called when the cursor is not on the insert row,
   * or if not all of non-nullable columns in
   * the insert row have been given a value
   * @since 1.2
   */
  public void insertRow() throws SQLException {
    updateRow();
  }

  /**
   * Retrieves whether the cursor is after the last row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is after the last row;
   * <code>false</code> if the cursor is at any other position or the
   * result set contains no rows
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public boolean isAfterLast() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : selectResultSet.isAfterLast();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves whether the cursor is before the first row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is before the first row;
   * <code>false</code> if the cursor is at any other position or the
   * result set contains no rows
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public boolean isBeforeFirst() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : selectResultSet.isBeforeFirst();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves whether the cursor is on the first row of
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on the first row;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public boolean isFirst() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : selectResultSet.isFirst() || (getRowCount() == 0);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves whether the cursor is on the last row of
   * this <code>ResultSet</code> object.
   * Note: Calling the method <code>isLast</code> may be expensive
   * because the JDBC driver
   * might need to fetch ahead one row in order to determine
   * whether the current row is the last row in the result set.
   *
   * @return <code>true</code> if the cursor is on the last row;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public boolean isLast() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? true : selectResultSet.isLast() || (getRowCount() == 0);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the last row in
   * this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public boolean last() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.last();
      if (res) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the remembered cursor position, usually the
   * current row.  This method has no effect if the cursor is not on
   * the insert row.
   *
   * @exception SQLException if a database access error occurs
   * or the result set is not updatable
   * @since 1.2
   */
  public void moveToCurrentRow() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = getOpenSelectResultSet().getRow();
      selectResultSet.moveToCurrentRow();
      if (selectResultSet.getRow() != oldRow) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the insert row.  The current cursor position is
   * remembered while the cursor is positioned on the insert row.
   *
   * The insert row is a special row associated with an updatable
   * result set.  It is essentially a buffer where a new row may
   * be constructed by calling the updater methods prior to
   * inserting the row into the result set.
   *
   * Only the updater, getter,
   * and <code>insertRow</code> methods may be
   * called when the cursor is on the insert row.  All of the columns in
   * a result set must be given a value each time this method is
   * called before calling <code>insertRow</code>.
   * An updater method must be called before a
   * getter method can be called on a column value.
   *
   * @exception SQLException if a database access error occurs
   * or the result set is not updatable
   * @since 1.2
   */
  public void moveToInsertRow() throws SQLException {
    if (owner.isCanAddRows()) {
      if (loadData()) {
        if (rowUpdated()) {
          if (canUpdateRow()) {
            updateRow();
          } else {
            cancelRowUpdates();
          }
        }

        boolean moveToInsertRow = true;
        try {
          owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.MOVE_TO_INSERT_ROW));
        } catch (Exception err) {
          moveToInsertRow = false;
        }
        if (moveToInsertRow) {
          int oldRow = getOpenSelectResultSet().getRow();
          inserting = true;
          ResultSetMetaData metaData = getMetaData();
          int columnCount = metaData.getColumnCount();
          String columnName;
          storedUpdates.remove(new Integer(getRow()));
          for (int c = 1; c <= columnCount; c++) {
            columnName = metaData.getColumnName(c);
            storeUpdate(columnName, owner.getDefaultValues().containsKey(columnName) ? owner.getDefaultValues().get(columnName) : null, false);
          }

          owner.fireIntervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, getRowCount() - 1, getRowCount() - 1));
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, getRow(), oldRow));
          owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.ROW_INSERTED));
        }
      } else {
        throw new SQLException("Ni pripravljenih podatkov.");
      }
    }
  }

  /**
   * Moves the cursor down one row from its current position.
   * A <code>ResultSet</code> cursor is initially positioned
   * before the first row; the first call to the method
   * <code>next</code> makes the first row the current row; the
   * second call makes the second row the current row, and so on.
   *
   * <P>If an input stream is open for the current row, a call
   * to the method <code>next</code> will
   * implicitly close it. A <code>ResultSet</code> object's
   * warning chain is cleared when a new row is read.
   *
   * @return <code>true</code> if the new current row is valid;
   * <code>false</code> if there are no more rows
   * @exception SQLException if a database access error occurs
   */
  public boolean next() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      if (!isLast()) {
        int oldRow = getOpenSelectResultSet().getRow();
        boolean res = selectResultSet.next();
        if (res) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
        }
        return res;
      } else {
        return false;
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Moves the cursor to the previous row in this
   * <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if it is off the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  public boolean previous() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (canUpdateRow()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      if (!isFirst()) {
        int oldRow = getOpenSelectResultSet().getRow();
        ;
        boolean res = selectResultSet.previous();
        if (res) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), oldRow));
        }
        return res;
      } else {
        return false;
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Refreshes the current row with its most recent value in
   * the database.  This method cannot be called when
   * the cursor is on the insert row.
   *
   * <P>The <code>refreshRow</code> method provides a way for an
   * application to
   * explicitly tell the JDBC driver to refetch a row(s) from the
   * database.  An application may want to call <code>refreshRow</code> when
   * caching or prefetching is being done by the JDBC driver to
   * fetch the latest value of a row from the database.  The JDBC driver
   * may actually refresh multiple rows at once if the fetch size is
   * greater than one.
   *
   * <P> All values are refetched subject to the transaction isolation
   * level and cursor sensitivity.  If <code>refreshRow</code> is called after
   * calling an updater method, but before calling
   * the method <code>updateRow</code>, then the
   * updates made to the row are lost.  Calling the method
   * <code>refreshRow</code> frequently will likely slow performance.
   *
   * @exception SQLException if a database access error
   * occurs or if this method is called when the cursor is on the insert row
   * @since 1.2
   */
  public void refreshRow() throws SQLException {
    if (isDataLoaded()) {
      if (rowUpdated()) {
        cancelRowUpdates();
      }
      getOpenSelectResultSet().refreshRow();
      int row = selectResultSet.getRow();
      owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, row - 1, row - 1));
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves whether a row has been deleted.  A deleted row may leave
   * a visible "hole" in a result set.  This method can be used to
   * detect holes in a result set.  The value returned depends on whether
   * or not this <code>ResultSet</code> object can detect deletions.
   *
   * @return <code>true</code> if a row was deleted and deletions are detected;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @see DatabaseMetaData#deletesAreDetected
   * @since 1.2
   */
  public boolean rowDeleted() throws SQLException {
    if (isDataLoaded()) {
      return false; //vedno bomo takoj zbrisali vrstico
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   * Retrieves whether the current row has had an insertion.
   * The value returned depends on whether or not this
   * <code>ResultSet</code> object can detect visible inserts.
   *
   * @return <code>true</code> if a row has had an insertion
   * and insertions are detected; <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @see DatabaseMetaData#insertsAreDetected
   * @since 1.2
   */
  public boolean rowInserted() throws SQLException {
    if (isDataLoaded()) {
      return inserting;
    } else {
      return false;
    }
  }

  /**
   * Retrieves whether the current row has been updated.  The value returned
   * depends on whether or not the result set can detect updates.
   *
   * @return <code>true</code> if both (1) the row has been visibly updated
   *         by the owner or another and (2) updates are detected
   * @exception SQLException if a database access error occurs
   * @see DatabaseMetaData#updatesAreDetected
   * @since 1.2
   */
  public boolean rowUpdated() throws SQLException {
    if (isDataLoaded()) {
      return storedUpdates.containsKey(new Integer(getRow()));
    } else {
      return false;
    }
  }

  public void startUpdate() throws SQLException {
    if (isDataLoaded()) {
      if ((getRowCount() > 0) && !rowUpdated()) {
        storedUpdates.put(new Integer(getRow()), new HashMap<String, Object>());
        owner.fireFieldValueChanged(new ActiveRowChangeEvent(owner, "", -1));
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /**
   *
   * Updates the designated column with an ascii stream value.
   * The updater methods are used to update column values in the
   * current row or the insert row.  The updater methods do not
   * update the underlying database; instead the <code>updateRow</code> or
   * <code>insertRow</code> methods are called to update the database.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, new Scale("updateAsciiStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException {
    Logger.getLogger(Settings.LOGGER).entering(this.getClass().toString(), "storeUpdates", insert);
    Scale scaledValue;
    Entry<String, Object> entry;

    int columnCount = getColumnCount();

    String delimiterLeft = getDelimiterLeft();
    String delimiterRight = getDelimiterRight();

    if (insert) {
      String schemaName = null;
      String tableName = updateTableName;

      StringBuffer columns = new StringBuffer();
      StringBuffer values = new StringBuffer();

      ResultSetMetaData metaData = getMetaData();
      List<String> skipValues = new ArrayList<String>();

      int columnIndex;

      for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
        entry = i.next();
        columnIndex = columnMapping.checkedGet(entry.getKey()).intValue();
        if (!owner.isSingleTableSelect()) {
          if (schemaName == null) {
            schemaName = metaData.getSchemaName(columnIndex);
          } else if (!schemaName.equalsIgnoreCase(metaData.getSchemaName(columnIndex))) {
            throw new SQLException("Insert on different schemas not supported.");
          }
          if (tableName == null) {
            tableName = metaData.getTableName(columnIndex);
          } else if (!tableName.equalsIgnoreCase(metaData.getTableName(columnIndex))) {
            if (updateTableName == null) {
              throw new SQLException("Insert on different tables not supported.");
            } else if (!updateColumnNames.contains(entry.getKey())) {
              skipValues.add(entry.getKey());
              continue;
            }
          }
        }
        if (entry.getValue() != null || metaData.isNullable(columnIndex) != ResultSetMetaData.columnNoNulls) {
          columns.append(columns.length() > 0 ? "," : "").append(delimiterLeft).append(entry.getKey()).append(delimiterRight);
          values.append(values.length() > 0 ? "," : "").append("?");
        } else {
          skipValues.add(entry.getKey());
          Logger.getLogger(Settings.LOGGER).info("Skipping null value: '" + entry.getKey() + "'");
        }
      }

      StringBuffer sql = new StringBuffer();

      sql.append("INSERT INTO ");
      if (schemaName.length() > 0) {
        sql.append(delimiterLeft).append(schemaName).append(delimiterRight).append(".");
      }
      sql.append(delimiterLeft).append(tableName).append(delimiterRight).append(" (").append(columns).append(") ");
      sql.append("VALUES (").append(values).append(")");

      PreparedStatement insertStatement = getConnection().prepareStatement(sql.toString());
      try {
        ParameterMetaData parameterMetaData = insertStatement.getParameterMetaData();

        int p = 1;

        for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
          entry = i.next();
          if (skipValues.indexOf(entry.getKey()) == -1) {
            /*if (entry.getValue()==null)
            insertStatement.setNull(p, parameterMetaData.getParameterType(p++));
            else//*/
            if (entry.getValue() instanceof Scale) {
              Scale scale = (Scale) entry.getValue();
              if (scale.method.equals("updateCharacterStream")) {
                insertStatement.setCharacterStream(p++, (Reader) scale.x, scale.scale);
              } else if (scale.method.equals("updateBinaryStream")) {
                insertStatement.setBinaryStream(p++, (InputStream) scale.x, scale.scale);
              } else if (scale.method.equals("updateAsciiStream")) {
                insertStatement.setAsciiStream(p++, (InputStream) scale.x, scale.scale);
              } else {
                insertStatement.setObject(p++, scale.x, scale.scale);
              }
            } else {
              insertStatement.setObject(p++, entry.getValue(), getType(entry.getKey()));

            }
            oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(), entry.getValue());
          }
        }
        Logger.getLogger(Settings.LOGGER).info("Executing insert : '" + sql + "'");
        insertStatement.executeUpdate();
      } finally {
        insertStatement.close();
      }
    } else {
      ResultSetMetaData metaData = selectResultSet.getMetaData();
      List<String> skipColumns = new ArrayList<String>();
      for (int c = 1; c <= columnCount; c++) {
        String columnName = metaData.getColumnName(c).toUpperCase();
        if ((updateTableName == null ||
                (updateTableName != null && updateTableName.equalsIgnoreCase(metaData.getTableName(c)))) &&
                (updateColumnNames.size() == 0 ||
                updateColumnNames.contains(columnName))) {
          try {
            Object value = selectResultSet.getObject(c);
            oldValues.put(c, value);
          } catch (Exception err) {
            Logger.getLogger(Settings.LOGGER).info("Skipping illegal value for: '" + columnName + "'");
            skipColumns.add(columnName);
          }
        } else {
          skipColumns.add(columnName);
        }
      }
      PrimaryKey key;
      for (Iterator<PrimaryKey> pk = primaryKeys.iterator(); pk.hasNext();) {
        key = pk.next();
        ResultSet updateResultSet = key.getUpdateResultSet(getOpenSelectResultSet());

        if (updateResultSet != null) {
          try {
            for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
              entry = i.next();
              if (skipColumns.indexOf(entry.getKey()) == -1) {
                if (key.isUpdateColumn(entry.getKey())) {
                  if (entry.getValue() instanceof Scale) {
                    scaledValue = (Scale) entry.getValue();
                    if (scaledValue.method.equals("updateAsciiStream")) {
                      updateResultSet.updateAsciiStream(entry.getKey(), (InputStream) scaledValue.x, scaledValue.scale);
                    } else if (scaledValue.method.equals("updateBinaryStream")) {
                      updateResultSet.updateBinaryStream(entry.getKey(), (InputStream) scaledValue.x, scaledValue.scale);
                    } else if (scaledValue.method.equals("updateCharacterStream")) {
                      updateResultSet.updateCharacterStream(entry.getKey(), (Reader) scaledValue.x, scaledValue.scale);
                    } else {
                      updateResultSet.updateObject(entry.getKey(), scaledValue.x, scaledValue.scale);
                    }
                  } else if (metaData.getColumnType(columnMapping.checkedGet(entry.getKey()).intValue()) == java.sql.Types.DATE) {
                    if (entry.getValue() instanceof java.util.Date) {
                      updateResultSet.updateDate(entry.getKey(), new java.sql.Date(((java.util.Date) entry.getValue()).getTime()));
                    } else if (entry.getValue() == null) {
                      updateResultSet.updateObject(entry.getKey(), entry.getValue());
                    } else {
                      try {
                        updateResultSet.updateDate(entry.getKey(), new java.sql.Date((FormatFactory.DATE_FORMAT.parse(entry.getValue().toString())).getTime()));
                      } catch (ParseException ex) {
                        updateResultSet.updateObject(entry.getKey(), entry.getValue());
                      }
                    }
                  } else {
                    updateResultSet.updateObject(entry.getKey(), entry.getValue());
                  }
                  cache.remove(new CacheKey(row.intValue(), entry.getKey()));
                  oldValues.put(columnMapping.checkedGet(entry.getKey()), updateResultSet.getObject(entry.getKey()));
                }
              }
            }

            updateResultSet.updateRow();
          } finally {
            updateResultSet.close();
          }
        } else {
          int updateCount = 0;
          StringBuffer set = new StringBuffer(540);
          for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
            entry = i.next();
            if ((skipColumns.indexOf(entry.getKey()) == -1) && (metaData.getTableName(columnMapping.checkedGet(entry.getKey()).intValue()).equalsIgnoreCase(key.table))) {
              set.append(set.length() > 0 ? ", " : "").append(delimiterLeft).append(entry.getKey()).append(delimiterRight).append(" = ?");
              updateCount++;
            }
          }

          if (updateCount > 0) {
            StringBuffer where = new StringBuffer();

            for (String c : key.getColumnNames()) {
              where.append(where.length() > 0 ? " AND " : "").append(delimiterLeft).append(c).append(delimiterRight).append(" = ? ");
            }
            String sql = "UPDATE " + delimiterLeft + key.table + delimiterRight + " SET " + set.toString() + " WHERE " + where.toString();

            PreparedStatement updateStatement = getConnection().prepareStatement(sql.toString());
            try {
              ParameterMetaData parameterMetaData = updateStatement.getParameterMetaData();

              int p = 1;

              for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
                entry = i.next();
                if (skipColumns.indexOf(entry.getKey()) == -1) {
                  if (entry.getValue() == null) {
                    updateStatement.setNull(p, parameterMetaData.getParameterType(p++));
                  } else if (entry.getValue() instanceof Scale) {
                    Scale scale = (Scale) entry.getValue();
                    if (scale.method.equals("updateCharacterStream")) {
                      updateStatement.setCharacterStream(p++, (Reader) scale.x, scale.scale);
                    } else if (scale.method.equals("updateBinaryStream")) {
                      updateStatement.setBinaryStream(p++, (InputStream) scale.x, scale.scale);
                    } else if (scale.method.equals("updateAsciiStream")) {
                      updateStatement.setAsciiStream(p++, (InputStream) scale.x, scale.scale);
                    } else {
                      updateStatement.setObject(p++, scale.x, scale.scale);
                    }
                  } else {
                    updateStatement.setObject(p++, entry.getValue());
                  }
                  oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(), entry.getValue());
                }
              }
              for (String c : key.getColumnNames()) {
                Object value = selectResultSet.getObject(c);
                if (value == null) {
                  updateStatement.setNull(p, parameterMetaData.getParameterType(p++));
                } else {
                  updateStatement.setObject(p++, value);
                }
                oldValues.put(columnMapping.checkedGet(c).intValue(), value);
              }
              Logger.getLogger(Settings.LOGGER).info("Executing update : '" + sql + "'");
              updateStatement.executeUpdate();
            } finally {
              updateStatement.close();
            }
          }
        }
      }
    }
  }

  public void filterChanged() throws SQLException {
    owner.lock();
    try {
      setSelectSql(this.selectSql);
      setCountSql(this.countSql);
    } finally {
      owner.unlock();
    }
  }

  public void setUpdateTableName(String updateTableName) {
    this.updateTableName = updateTableName;
    if ((getUniqueID() != null) && (getUniqueID().length > 0) && (getUpdateTableName() != null) && (getUpdateTableName().length() > 0)) {
      this.primaryKeys = this.getPrimaryKeys();
    }
  }

  private List<PrimaryKey> getPrimaryKeys() {
    List<PrimaryKey> result = new ArrayList<PrimaryKey>();
    if (this.selectStatement != null) {
      try {
        result = PrimaryKey.getPrimaryKeys(this.selectStatement);
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't getPrimaryKeys from '" + selectSql + "'", ex);
        result = new ArrayList<PrimaryKey>();
      }
    }
    if ((getUniqueID() != null) && (getUniqueID().length > 0) && (getUpdateTableName() != null) && (getUpdateTableName().length() > 0)) {
      PrimaryKey pk = new PrimaryKey(getUniqueID(), getUpdateTableName());
      pk.connection = this.getConnection();
      result.add(pk);
    }
    return result;
  }

  public String getUpdateTableName() {
    return updateTableName;
  }

  public void setSelectSql(String selectSql) throws SQLException {
    String oldvalue = this.selectSql;
    try {
      semaphore.acquire();
      this.selectSql = selectSql;
      String sql = substParameters(selectSql, owner.getParameters());
      Logger.getLogger(Settings.LOGGER).finest(
              "\n################# SELECT SQL #################\n" +
              sql +
              "\n################# ########## #################");
      if (sql != null && sql.length() > 0 && getConnection() != null) {
        if (cachedStatements.containsKey(sql)) {
          selectStatement = cachedStatements.get(sql);
          preparedSelectSql = sql;
        } else if ((this.selectStatement == null) || (!sql.equals(preparedSelectSql))) {
          if (this.selectStatement != null) {
            if (owner.isCacheStatements()) {
              cachedStatements.put(preparedSelectSql, selectStatement);
            } else {
              selectStatement.close();
            }
          }
          this.selectStatement = getConnection().prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY,
                  ResultSet.HOLD_CURSORS_OVER_COMMIT);
          this.selectStatement.setFetchSize(1008);
          preparedSelectSql = sql;
          this.metaData = null;
          this.columnMapping.clear();

          this.metaData = selectStatement.getMetaData();
          int columnCount = this.metaData != null ? this.metaData.getColumnCount() : 0;
          for (int c = 1; c <= columnCount; c++) {
            this.columnMapping.put(this.metaData.getColumnName(c), c);
          }
          primaryKeys = this.getPrimaryKeys();

          owner.setName();
          Logger.getLogger(Settings.LOGGER).info("Successfully prepared the selectSql.");
        }
      } else {
        this.selectStatement = null;
      }
      this.count = -1;
      if (this.selectResultSet != null) {
        this.selectResultSet.close();
      }
      this.selectResultSet = null;
    } catch (InterruptedException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Interrupted while preparing '" + selectSql + "'", ex);
    } finally {
      semaphore.release();
      if (countSql == null) {
        setCountSql("SELECT COUNT(*) FROM (" + this.selectSql + ") c");
      }
    }
    owner.firePropertyChange("selectSql", oldvalue, this.selectSql);
  }

  public int getColumnIndex(String columnName) throws SQLException {
    return columnMapping.checkedGet(columnName).intValue();
  }

  public void setCountSql(String countSql) throws SQLException {
    String oldvalue = this.countSql;
    try {
      semaphore.acquire();
      this.countSql = countSql;
      String sql = substParameters(countSql, owner.getParameters());
      Logger.getLogger(Settings.LOGGER).finest(
              "\n################# COUNT SQL #################\n" +
              sql +
              "\n################# ######### #################");
      if (sql != null && sql.length() > 0 && getConnection() != null) {
        if (cachedStatements.containsKey(sql)) {
          countStatement = cachedStatements.get(sql);
          preparedCountSql = sql;
        } else if ((this.countStatement == null) || (!sql.equals(preparedCountSql))) {
          if (this.countStatement != null) {
            if (owner.isCacheStatements()) {
              cachedStatements.put(preparedCountSql, countStatement);
            } else {
              countStatement.close();
            }
          }
          countStatement = getConnection().prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
          countStatement.setFetchSize(1);
          preparedCountSql = sql;
        }
      } else {
        countStatement = null;
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Interrupted while preparing '" + countSql + "'", ex);
      ;
    } finally {
      semaphore.release();
    }
    owner.firePropertyChange("countSql", oldvalue, this.countSql);
  }

  public String getSelectSql() {
    return selectSql;
  }

  public String getCountSql() {
    return countSql;
  }

  public Connection getConnection() {
    if (this.connection == null) {
      return ConnectionManager.getInstance() != null ? ConnectionManager.getInstance().getConnection() : null;
    } else {
      return this.connection;
    }
  }

  public void setConnection(Connection connection) throws SQLException {
    if (this.connection != connection) {
      for (PreparedStatement statement : cachedStatements.values()) {
        statement.close();
      }
      cachedStatements.clear();
    }
    this.connection = connection;
    if (selectStatement != null && !selectStatement.getConnection().equals(connection)) {
      if (this.selectStatement != null) {
        this.selectStatement.close();
        this.selectStatement = null;
      }
      if (this.countStatement != null) {
        this.countStatement.close();
        this.countStatement = null;
      }
      setSelectSql(this.selectSql);
      if ((this.countStatement == null) && (countSql != null)) {
        setCountSql(countSql);
      }
    }
  }

  public int getRowCount() {
    int newCount = this.count;
    if (!isDataLoaded() && refreshPending) {
      return -1;
    } else {
      if (this.count == -1) {
        if (owner.lock(false)) {
          try {
            if (this.count == -1) {
              if (countStatement != null) {
                Logger.getLogger(Settings.LOGGER).fine("Executing '" + preparedCountSql + "'");
                if (DbDataSource.DUMP_SQL) {
                  System.out.println("############## count(*) ");
                  System.out.println(preparedCountSql);
                }
                ResultSet rs = executeSql(countStatement, owner.getParameters());
                if (DbDataSource.DUMP_SQL) {
                  System.out.println("##############");
                }
                if (rs.first()) {
                  newCount = rs.getInt(1);
                }
              } else if (selectStatement != null) {
                if (selectResultSet == null) {
                  try {
                    Logger.getLogger(Settings.LOGGER).fine("Executing '" + preparedSelectSql + "'");
                    if (DbDataSource.DUMP_SQL) {
                      System.out.println("##############");
                      System.out.println(preparedSelectSql);
                    }
                    selectResultSet = executeSql(selectStatement, owner.getParameters());
                    if (DbDataSource.DUMP_SQL) {
                      System.out.println("##############");
                    }
                    selectResultSet.setFetchSize(getFetchSize());
                    selectResultSet.first();
                  } catch (SQLException ex) {
                    Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a result from \n:" + preparedSelectSql, ex);
                    selectResultSet = null;
                  }
                }

                if (selectResultSet != null) {
                  int row = selectResultSet.getRow();
                  selectResultSet.last();
                  newCount = selectResultSet.getRow();
                  if (row > 0) {
                    selectResultSet.absolute(row);
                  }
                }
              }
            }
          } catch (SQLException ex) {
            Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get row count from \n:" + preparedCountSql, ex);
            newCount = this.count;
          } finally {
            owner.unlock();
            this.count = newCount;
          }
        } else {
          return -1;
        }
      }

      return newCount + (inserting ? 1 : 0);
    }
  }

  public boolean isDataLoaded() {
    return selectResultSet != null;
  }

  private boolean loadData(boolean reload) {
    return loadData(reload, Integer.MIN_VALUE);
  }

  public boolean loadData(boolean reload, int oldRow) {
    boolean reloaded = false;
    if (reload) {
      owner.lock();
      try {
        if (selectResultSet != null) {
          selectResultSet.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't properly close the for '" + selectSql + "'", ex);
      } finally {
        selectResultSet = null;
        owner.unlock();
      }
    }
    if ((selectResultSet == null) && selectStatement != null) {
      owner.lock();
      try {
        owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.LOAD_DATA));
        try {
          Logger.getLogger(Settings.LOGGER).fine("Executing '" + preparedSelectSql + "'");
          if (DbDataSource.DUMP_SQL) {
            System.out.println("##############");
            System.out.println(preparedSelectSql);
          }
          long timer = System.currentTimeMillis();
          selectResultSet = executeSql(selectStatement, owner.getParameters());
          System.out.println(owner.getName() + ":select:" + (System.currentTimeMillis() - timer) + "ms");
          if (DbDataSource.DUMP_SQL) {
            System.out.println("##############");
          }
          selectResultSet.setFetchSize(getFetchSize());
          selectResultSet.first();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a result from \n:" + preparedSelectSql, ex);
          selectResultSet = null;
        } finally {
          inserting = false;
          count = -1;
          storedUpdates.clear();
          cache.clear();
          reloaded = true;
          getRowCount();
        }
      } finally {
        owner.unlock();
        Logger.getLogger(Settings.LOGGER).finer("Permit unlockd '" + selectSql + "'");
      }
      if (oldRow > 0 && getRowCount() > 0) {
        try {
          selectResultSet.absolute(Math.min(oldRow, getRowCount()));
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't change rowset position", ex);
        }
      }
      owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.DATA_LOADED));
    }
    if (reloaded && selectResultSet != null) {
      if (EventQueue.isDispatchThread() || !owner.isSafeMode()) {
        events.run();
      } else {
        try {
          EventQueue.invokeAndWait(events);
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't notify loaddata results from '" + selectSql + "'", ex);
        }
      }
    }
    return selectResultSet != null;

  }

  public boolean loadData() {
    return loadData(false);
  }

  public boolean reload() {
    return loadData(true);
  }

  public boolean reload(int oldRow) {
    return loadData(true, oldRow);
  }

  public static String substParameters(String sql, List<?> parameters) {
    if (sql != null && sql.length() > 0) {
      Object value;
      Integer type;

      for (Iterator values = parameters.iterator(); values.hasNext();) {
        value = values.next();
        if (value instanceof DbDataSource.SubstSqlParameter) {
          type = ((DbDataSource.SubstSqlParameter) value).getType();
          if (type.equals(Types.SUBST_ALL)) {
            sql = sql.replaceAll(((DbDataSource.SubstSqlParameter) value).getReplace(), ((DbDataSource.SubstSqlParameter) value).getValue());
          } else if (type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST)) {
            sql = sql.replaceFirst(((DbDataSource.SubstSqlParameter) value).getReplace(), ((DbDataSource.SubstSqlParameter) value).getValue());
          }
        }
      }
    }
    return sql;
  }

  private static int setParameters(PreparedStatement statement, List<?> parameters, int pos, boolean subset) throws SQLException {
    if (!subset) {
      statement.clearParameters();
    }
    ParameterMetaData metaData = statement.getParameterMetaData();
    int parameterCount = metaData.getParameterCount();
    Object value;
    Integer type;

    for (Iterator values = parameters.iterator(); (pos <= parameterCount) && values.hasNext();) {
      value = values.next();
      if (value instanceof DbDataSource.SqlParameter) {
        type = ((DbDataSource.SqlParameter) value).getType();
        if (!(type.equals(Types.SUBST_ALL) || type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST))) {
          if (((DbDataSource.SqlParameter) value).getValue() != null) {
            statement.setObject(pos++, ((DbDataSource.SqlParameter) value).getValue(),
                    ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              System.out.println("--[" + (pos - 1) + "]=" + ((DbDataSource.SqlParameter) value).getValue().toString());
            }
          } else {
            statement.setNull(pos++, ((DbDataSource.SqlParameter) value).getType());
            if (DbDataSource.DUMP_SQL) {
              System.out.println("--[" + (pos - 1) + "]=null");
            }
          }
        } else if ((value instanceof DbDataSource.SubstSqlParameter) && (((DbDataSource.SubstSqlParameter) value).getParameters().size() > 0)) {
          pos = setParameters(statement, ((DbDataSource.SubstSqlParameter) value).getParameters(), pos, true);
        }
      } else {
        if (value == null) {
          statement.setNull(pos, metaData.getParameterType(pos++));
          if (DbDataSource.DUMP_SQL) {
            System.out.println("--[" + (pos - 1) + "]=null");
          }
        } else {
          statement.setObject(pos++, value);
          if (DbDataSource.DUMP_SQL) {
            System.out.println("--[" + (pos - 1) + "]=" + value.toString());
          }
        }
      }
    }
    while ((pos <= parameterCount) && !subset) {
      statement.setNull(pos, metaData.getParameterType(pos++));
    }
    return pos;
  }

  private ResultSet executeSql(PreparedStatement statement, List<?> parameters) throws SQLException {
    ResultSet rs = null;
    try {
      semaphore.acquireUninterruptibly();

      rs = executeQuery(statement, parameters);
    } finally {
      semaphore.release();
    }


    return rs;
  }

  public static ResultSet executeQuery(String selectSQL, List<?> parameters) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = ConnectionManager.getInstance().getConnection().prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY,
            ResultSet.HOLD_CURSORS_OVER_COMMIT);

    try {
      return executeQuery(statement, parameters);
    } finally {
      statement.close();
    }
  }

  public static ResultSet executeQuery(PreparedStatement statement, List<?> parameters) throws SQLException {
    setParameters(statement, parameters, 1, false);

    return statement.executeQuery();
  }

  public static boolean execute(String selectSQL, List<?> parameters) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = ConnectionManager.getInstance().getConnection().prepareStatement(sql);

    return execute(statement, parameters);
  }

  public static boolean execute(PreparedStatement statement, List<?> parameters) throws SQLException {
    setParameters(statement, parameters, 1, false);

    return statement.execute();
  }

  public static int executeUpdate(String selectSQL, List<?> parameters) throws SQLException {
    String sql = substParameters(selectSQL, parameters);
    PreparedStatement statement = ConnectionManager.getInstance().getConnection().prepareStatement(sql);

    return executeUpdate(statement, parameters);
  }

  public static int executeUpdate(PreparedStatement statement, List<?> parameters) throws SQLException {
    setParameters(statement, parameters, 1, false);

    return statement.executeUpdate();
  }

  public int getColumnCount() throws SQLException {
    return getMetaData().getColumnCount();
  }

  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    return getValueAt(rowIndex, getMetaData().getColumnName(columnIndex));
  }

  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    String[] columns = this.getValueColumns.toArray(new String[this.getValueColumns.size() + 1]);
    columns[this.getValueColumns.size()] = columnName;
    return getValueAt(rowIndex, columnName, columns);
  }

  public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException {
    if (!isDataLoaded() && refreshPending) {
      return null;
    } else if (loadData()) {
      Object result = null;
      columnName = columnName.toUpperCase();

      if (wasUpdated(rowIndex, columnName)) {
        result = getStoredValue(rowIndex, columnName, null, Object.class);
      } else {
        CacheKey ck = new CacheKey(rowIndex, columnName);
        CacheEntry ce;
        if (cache.containsKey(ck) && ((ce = cache.get(ck)) != null)) {
          result = ce.value;
        } else {
          owner.lock();
          try {
            int oldRow = getOpenSelectResultSet().getRow();

            int max = Math.min(rowIndex + getFetchSize(), getRowCount());
            int min = Math.max(max - getFetchSize(), 1);
            selectResultSet.absolute(min);
            String cn;
            Object value;
            for (int row = min; !selectResultSet.isAfterLast() && (row <= max); row++) {
              if (!cache.containsKey(new CacheKey(row, columnName))) {
                java.util.Map<String, Boolean> pending = new HashMap<String, Boolean>();
                for (int c = 0; c < columnNames.length; c++) {
                  cn = columnNames[c].toUpperCase();
                  pending.put(cn, isPending(cn, row));
                }

                for (int c = 0; c < columnNames.length; c++) {
                  cn = columnNames[c].toUpperCase();
                  if (!pending.get(cn)) {
                    value = selectResultSet.getObject(cn);
                    if (selectResultSet.wasNull()) {
                      value = null;
                    }
                    cache.put(new CacheKey(row, cn), new CacheEntry<String, Object>(this, cn, value));

                    if ((row == rowIndex) && cn.equals(columnName)) {
                      result = value;
                    }
                  }
                }
                //get pending values
                for (int c = 0; c < columnNames.length; c++) {
                  cn = columnNames[c].toUpperCase();
                  if (pending.get(cn)) {
                    PendingSqlParameter pendingSqlParameter = getPendingSqlParameter(cn);
                    if (pendingSqlParameter != null) {
                      java.util.List<Object> parameters = new java.util.ArrayList<Object>();
                      for (String keyField : pendingSqlParameter.getParentKeyFields()) {
                        parameters.add(cache.get(new CacheKey(row, keyField)).value);
                      }
                      java.util.List<PendingValue> pendingValues = pendingSqlParameter.getPendingValues(parameters);
                      for (PendingValue pendingValue : pendingValues) {
                        cache.put(new CacheKey(row, pendingValue.getFieldName()), new CacheEntry<String, Object>(this, pendingValue.getFieldName(), pendingValue.getValue()));
                        pending.put(pendingValue.getFieldName(), false);
                      }
                    }
                  }

                  if ((row == rowIndex) && cn.equals(columnName)) {
                    if (cache.containsKey(ck) && ((ce = cache.get(ck)) != null)) {
                      result = ce.value;
                    }
                  }
                }
              }
              selectResultSet.next();
            }
            selectResultSet.absolute(oldRow);
          } finally {
            owner.unlock();
          }
        }
      }
      return result;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public ResultSet getResultSet() throws SQLException {
    java.sql.PreparedStatement resultStatement = getConnection().prepareStatement(preparedSelectSql,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY,
            ResultSet.HOLD_CURSORS_OVER_COMMIT);
    resultStatement.setFetchSize(1008);


    if (DbDataSource.DUMP_SQL) {
      System.out.println("##############");
      System.out.println(preparedSelectSql);
    }
    ResultSet result = executeSql(resultStatement, owner.getParameters());
    if (DbDataSource.DUMP_SQL) {
      System.out.println("##############");
    }

    return result;
  }

  private ResultSet getOpenSelectResultSet() throws SQLException {
    if (isDataLoaded()) {
      int oldRow = 1;
      boolean check = false;
      try {
        oldRow = selectResultSet.getRow();
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.WARNING, ex.getMessage());
        check = true;
      }
      if (check) {
        owner.lock();
        try {
          selectResultSet.relative(0);
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "SelectResultSet seems closed. [" + ex.getMessage() + "]");
          selectResultSet = executeSql(selectStatement, owner.getParameters());
          selectResultSet.setFetchSize(getFetchSize());
          selectResultSet.absolute(oldRow);
        } finally {
          owner.unlock();
        }
      }
    }
    return selectResultSet;
  }

  public String getColumnName(int columnIndex) throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getMetaData().getColumnName(columnIndex);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public boolean isUpdating() throws SQLException {
    return rowInserted() || rowUpdated();
  }

  public void updateRefreshPending() {
    boolean refreshPending = DataSourceEvent.isRefreshing(owner);
    if (this.refreshPending != refreshPending) {
      this.refreshPending = refreshPending;
      try {
        if (isDataLoaded()) {
          int row = getRow();
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
        }
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).warning("Couldn't update pending refresh.");
      }
    }
  }

  public Map<Integer, Map<String, Object>> getStoredUpdates() {
    return java.util.Collections.unmodifiableMap(storedUpdates);
  }

  private void storeUpdate(int columnIndex, Object value) throws SQLException {
    storeUpdate(getMetaData().getColumnName(columnIndex), value);
  }

  private void storeUpdate(String columnName, Object value) throws SQLException {
    storeUpdate(columnName, value, true);
  }

  private void storeUpdate(String columnName, Object value, boolean notify) throws SQLException {
    if (getRowCount() == 0 && !isReadOnly() && owner.isAutoInsert()) {
      moveToInsertRow();
    }
    if (getRow() > 0 && !isReadOnly()) {
      columnName = columnName.toUpperCase();
      Integer row = new Integer(getRow());
      boolean isUpdating = inserting || storedUpdates.containsKey(row);
      if (isUpdating || !Equals.equals(value, getOpenSelectResultSet().getObject(columnName))) {
        Map<String, Object> columnValues;
        if (storedUpdates.containsKey(row)) {
          columnValues = storedUpdates.get(row);
        } else {
          columnValues = new HashMap<String, Object>();
        }

        //if (!Equals.equals(value, columnValues.get(columnName))) {//ta pogoj ne vraca null-e, ce prej ni neke vrednosti, zato ga ne rabimo
        columnValues.put(columnName, value);
        storedUpdates.put(row, columnValues);

        if (notify) {
          owner.fireFieldValueChanged(new ActiveRowChangeEvent(owner, columnName, -1));
        }
      // }
      }
    }
  }

  private boolean compareValues(ResultSet resultSet, Map<Integer, Object> values) throws SQLException {
    boolean equals = false;
    if (primaryKeys.size() == 1) {
      equals = primaryKeys.get(0).compareValues(resultSet, values);
    } else if (uniqueID != null && uniqueID.length > 0) {
      equals = true;
      for (String column : uniqueID) {
        Integer c = new Integer(columnMapping.checkedGet(column).intValue());

        if (values.containsKey(c) && values.get(c) != null) {
          equals = equals && com.openitech.util.Equals.equals(values.get(c), resultSet.getObject(c));
        } else {
          resultSet.getObject(c);
          equals = equals && resultSet.wasNull();
        }
        if (!equals) {
          break;
        }
      }
    } else {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      equals = columnCount == values.size();
      for (int c = 1; c <= columnCount && equals; c++) {
        if (values.containsKey(c) && values.get(c) != null) {
          equals = equals && com.openitech.util.Equals.equals(values.get(c), resultSet.getObject(c));
        } else {
          resultSet.getObject(c);
          equals = equals && resultSet.wasNull();
        }
      }
    }
    return equals;
  }

  public boolean hasChanged(int columnIndex) throws SQLException {
    if (inserting) {
      return true;
    } else if (wasUpdated(columnIndex)) {
      return !com.openitech.util.Equals.equals(getOpenSelectResultSet().getObject(columnIndex), getObject(columnIndex));
    } else {
      return false;
    }
  }

  public boolean hasChanged(String columnName) throws SQLException {
    if (inserting) {
      return true;
    } else if (wasUpdated(columnName)) {
      return !com.openitech.util.Equals.equals(getOpenSelectResultSet().getObject(columnName), getObject(columnName));
    } else {
      return false;
    }
  }

  public Object getOldValue(int columnIndex) throws SQLException {
    return getOpenSelectResultSet().getObject(columnIndex);
  }

  public Object getOldValue(String columnName) throws SQLException {
    return getOpenSelectResultSet().getObject(columnName);
  }

  public boolean wasUpdated(int columnIndex) throws SQLException {
    return wasUpdated(getRow(), getMetaData().getColumnName(columnIndex));
  }

  public boolean wasUpdated(String columnName) throws SQLException {
    return wasUpdated(getRow(), columnName);
  }

  private boolean wasUpdated(int row, int columnIndex) throws SQLException {
    return wasUpdated(row, getMetaData().getColumnName(columnIndex));
  }

  public boolean wasUpdated(int row, String columnName) throws SQLException {
    boolean result = false;

    if (row == getRow() && inserting) {
      result = true;
    } else {
      Integer r = new Integer(row);
      if (storedUpdates.containsKey(r)) {
        result = storedUpdates.get(r).containsKey(columnName.toUpperCase());
      }

      if (!result) {
        storedResult[0] = false;
      }
    }

    return result;
  }

  private Object getStoredValue(int columnIndex) throws SQLException {
    return getStoredValue(getRow(), getMetaData().getColumnName(columnIndex), null, Object.class);
  }

  private Object getStoredValue(String columnName) throws SQLException {
    return getStoredValue(getRow(), columnName, null, Object.class);
  }

  private <T> T getStoredValue(int row, int columnIndex, T nullValue, Class<? extends T> type) throws SQLException {
    return getStoredValue(row, getMetaData().getColumnName(columnIndex), nullValue, type);
  }

  private <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException {
    columnName = columnName.toUpperCase();
    Object result = nullValue;
    Integer r = new Integer(row);
    if (storedUpdates.containsKey(r)) {
      if (storedUpdates.get(r).containsKey(columnName)) {
        result = storedUpdates.get(r).get(columnName);
        storedResult[0] = true;
        if (storedResult[1] = (result == null)) {
          result = nullValue;
        } else if (type.equals(String.class)) {
          result = result.toString();
        } else if (result instanceof Scale) {
          result = ((Scale) result).x;
        }

        return (T) result;
      }
    }

    if (row == 0) {
      storedResult[0] = true;
    } else {
      storedResult[0] = false;

      int oldrow = selectResultSet.getRow();

      if (oldrow != row) {
        getOpenSelectResultSet();
        selectResultSet.absolute(row);
      }

      if (isPending(columnName, row)) {
        return (T) getValueAt(row, columnName);
      }

      if (String.class.isAssignableFrom(type)) {
        result = selectResultSet.getString(columnName);
      } else if (Number.class.isAssignableFrom(type)) {
        if (Integer.class.isAssignableFrom(type)) {
          result = selectResultSet.getInt(columnName);
        } else if (Short.class.isAssignableFrom(type)) {
          result = selectResultSet.getShort(columnName);
        } else if (Double.class.isAssignableFrom(type)) {
          result = selectResultSet.getDouble(columnName);
        } else if (Byte.class.isAssignableFrom(type)) {
          result = selectResultSet.getByte(columnName);
        } else if (Float.class.isAssignableFrom(type)) {
          result = selectResultSet.getFloat(columnName);
        } else if (Long.class.isAssignableFrom(type)) {
          result = selectResultSet.getLong(columnName);
        } else {
          result = selectResultSet.getBigDecimal(columnName);
        }
      } else if (Boolean.class.isAssignableFrom(type)) {
        result = selectResultSet.getBoolean(columnName);
      } else if (Date.class.isAssignableFrom(type)) {
        if (Time.class.isAssignableFrom(type)) {
          result = selectResultSet.getTime(columnName);
        } else if (Timestamp.class.isAssignableFrom(type)) {
          result = selectResultSet.getTimestamp(columnName);
        } else {
          result = selectResultSet.getDate(columnName);
        }
      } else if (nullValue instanceof byte[]) {
        result = selectResultSet.getBytes(columnName);
      } else {
        result = selectResultSet.getObject(columnName);
      }

      if (oldrow != row) {
        selectResultSet.absolute(oldrow);
      }
    }

    return result == null ? nullValue : (T) result;
  }

  public boolean isPending(String columnName) throws SQLException {
    return isPending(columnName, getRow());
  }

  @Override
  public boolean isPending(String columnName, int row) throws SQLException {
    columnName = columnName.toUpperCase();
    CacheKey ck = new CacheKey(row, columnName);

    if (wasUpdated(row, columnName)) {
      return false;
    } else if (cache.containsKey(ck)) {
      return false;
    } else {
      boolean result = false;
      for (Object parameter : owner.getParameters()) {
        if (parameter instanceof PendingSqlParameter) {
          result = result || ((PendingSqlParameter) parameter).isPending(columnName);
        }
      }

      return result;
    }
  }

  private PendingSqlParameter getPendingSqlParameter(String columnName) {
    columnName = columnName.toUpperCase();
    PendingSqlParameter result = null;
    Iterator<Object> i = owner.getParameters().iterator();
    while (result == null && i.hasNext()) {
      Object parameter = i.next();
      if ((parameter instanceof PendingSqlParameter) &&
              ((PendingSqlParameter) parameter).isPending(columnName)) {
        result = (PendingSqlParameter) parameter;
      }
    }

    return result;
  }

  @Override
  public void storeUpdates(boolean insert) throws SQLException {
    if (isDataLoaded()) {
      Integer row = new Integer(getRow());
      Map<String, Object> columnValues = storedUpdates.get(row);
      Map.Entry<String, Object> entry;
      Map<Integer, Object> oldValues = new HashMap<Integer, Object>();

      if (insert) {
        for (Iterator<Map.Entry<String, Object>> i = owner.getDefaultValues().entrySet().iterator(); i.hasNext();) {
          entry = i.next();
          columnValues.put(entry.getKey(), entry.getValue());
        }
      }

      if (storedUpdates.containsKey(row)) {
        if (owner.isUpdateRowFireOnly()) {
          owner.lock();
          boolean readOnly = isReadOnly();
          setReadOnly(true);
          try {
            for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
              entry = i.next();
              oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(), entry.getValue());
            }
            if (uniqueID != null) {
              for (String column : uniqueID) {
                oldValues.put(columnMapping.checkedGet(column).intValue(), getObject(column));
              }
            }
            owner.fireStoreUpdates(new StoreUpdatesEvent(owner, row, insert, columnValues, oldValues));
          } finally {
            owner.unlock();
            setReadOnly(readOnly);
          }
        } else {
          doStoreUpdates(insert, columnValues, oldValues, row);
        } //if (storedUpdates.containsKey(row))

        storedUpdates.remove(row);
        cache.clear();
        inserting = false;

        int selectedrow = selectResultSet.getRow();

        if (selectResultSet != null) {
          selectResultSet.close();
        }
        selectResultSet = executeSql(selectStatement, owner.getParameters());
        selectResultSet.setFetchSize(getFetchSize());
        if (selectedrow > 0) {
          try {
            selectResultSet.absolute(selectedrow);
          } catch (SQLException ex) {
            selectResultSet.first();
          }
        } else {
          selectResultSet.first();
        }

        if (owner.isSeekUpdatedRow()) {
          if (!compareValues(selectResultSet, oldValues)) {
            if (selectResultSet.first()) {
              while (!compareValues(selectResultSet, oldValues) && !selectResultSet.isLast()) {
                selectResultSet.next();
              }
            }
          }
        }

        count = -1; //reset row count

        owner.fireContentsChanged(new ListDataEvent(owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, selectResultSet.getRow(), -1));
        Logger.getLogger(Settings.LOGGER).exiting(this.getClass().toString(), "storeUpdates", insert);
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

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
  public String getDataSourceName() {
    if (loadData()) {
      try {
        if (getOpenSelectResultSet() instanceof javax.sql.RowSet) {
          return ((javax.sql.RowSet) getOpenSelectResultSet()).getDataSourceName();
        }
      } catch (SQLException ex) {
        Logger.getLogger(SQLDataSource.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return owner.getName();
  }

  @Override
  public void setDataSourceName(String name) throws SQLException {
    if (isDataLoaded()) {
      if (getOpenSelectResultSet() instanceof javax.sql.RowSet) {
        ((javax.sql.RowSet) getOpenSelectResultSet()).setDataSourceName(name);
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

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
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  @Override
  public boolean isReadOnly() {
    return readOnly || DataSourceEvent.isRefreshing(owner);
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

  private class Scale {

    String method;
    Object x;
    int scale;

    Scale(String method, Object x, int scale) {
      this.method = method;
      this.x = x;
      this.scale = scale;
    }
  }

  private class DbDataSourceHashMap<K, V> extends HashMap<K, V> {

    /**
     * Returns the value to which the specified key is mapped in this identity
     * hash map, or <tt>null</tt> if the map contains no mapping for this key.
     * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
     * that the map contains no mapping for the key; it is also possible that
     * the map explicitly maps the key to <tt>null</tt>. The
     * <tt>containsKey</tt> method may be used to distinguish these two cases.
     *
     *
     * @param key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key.
     * @see #put(Object, Object)
     */
    public V checkedGet(Object key) throws SQLException {
      if ((key != null) && (key instanceof String)) {
        key = ((String) key).toUpperCase();
      }

      if (containsKey(key)) {
        return get(key);
      } else {
        throw new SQLException("DbDataSource for '" + selectSql + "' does not contain '" + key.toString() + "'.");
      }
    }

    public V put(K key, V value) {
      if (key instanceof String) {
        return super.put((K) (((String) key).toUpperCase()), value);
      } else {
        return super.put(key, value);
      }
    }
  }

  private static class PrimaryKey {

    String table = "NULL";
    List<String> columnNames = new ArrayList<String>();
    PreparedStatement delete = null;
    PreparedStatement update = null;
    Map<String, Integer> columnMapping = new HashMap<String, Integer>();
    int hashcode;
    boolean updateFailed = false;
    boolean virtual = false;
    Connection connection;

    public PrimaryKey(String[] uniqueID, String table) {
      this.virtual = true;
      this.table = table;
      this.connection = null;
      for (String s : uniqueID) {
        columnNames.add(s.toUpperCase());
      }
      hashcode = table.hashCode();
    }

    public PrimaryKey(Connection connection, String table) throws SQLException {
      this.virtual = false;
      this.table = table;
      this.connection = connection;
      hashcode = table.hashCode();
    }

    public List<String> getColumnNames() throws SQLException {
      if (this.columnNames.size() == 0) {
        DatabaseMetaData metaData = connection.getMetaData();
        try {

          ResultSet rs = metaData.getPrimaryKeys(null, null, table);

          while (rs.next() && !rs.isAfterLast()) {
            if (rs.getString("TABLE_NAME").equalsIgnoreCase(table)) {
              columnNames.add(rs.getString("COLUMN_NAME").toUpperCase());
            } else {
              throw new SQLException("Couldn't retrieve primary keys for '" + table + "'");
            }
          }
        } catch (SQLException ex) {
          this.columnNames.clear();
          Logger.getLogger(Settings.LOGGER).log(Level.FINE, "Couldn't retrieve primary keys for '" + table + "'");
        }
      }
      return columnNames;
    }

    public PreparedStatement getDeleteStatement(ResultSet data) throws SQLException {
      if (delete == null) {
        StringBuffer sql = new StringBuffer();

        for (Iterator<String> c = columnNames.iterator(); c.hasNext();) {
          sql.append(sql.length() > 0 ? " AND " : "").append(c.next()).append("=? ");
        }

        sql.insert(0, "DELETE FROM " + table + " WHERE ");

        delete = connection.prepareStatement(sql.toString());
      }

      delete.clearParameters();
      int p = 1;
      for (Iterator<String> c = getColumnNames().iterator(); c.hasNext();) {
        delete.setObject(p++, data.getObject(c.next()));
      }


      return delete;
    }

    public ResultSet getUpdateResultSet(ResultSet data) throws SQLException {
      ResultSet result = null;
      if (connection != null && !virtual) {
        if (update == null) {
          StringBuffer sql = new StringBuffer();

          for (String c : columnNames) {
            sql.append(sql.length() > 0 ? " AND " : "").append(c).append("=? ");
          }

          sql.insert(0, "SELECT * FROM " + table + " WHERE ");
          sql.append(" FOR UPDATE");

          update = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

          this.columnMapping.clear();
          ResultSetMetaData metaData = update.getMetaData();
          int columnCount = metaData.getColumnCount();
          for (int c = 1; c <= columnCount; c++) {
            this.columnMapping.put(metaData.getColumnName(c).toUpperCase(), c);
          }
        }

        update.clearParameters();
        int p = 1;
        for (String c : columnNames) {
          update.setObject(p++, data.getObject(c));
        }

        if (!updateFailed) {
          try {
            result = update.executeQuery();
            result.next();
          } catch (SQLException ex) {
            Logger.getLogger(Settings.LOGGER).log(Level.INFO, "The table '" + table + "' can't be updated with through a resulSet");
            updateFailed = true;
          }
        }
      }

      return result;
    }

    public boolean isUpdateColumn(String columnName) {
      return columnMapping.containsKey(columnName.toUpperCase());
    }

    public <K> boolean compareValues(ResultSet resultSet, Map<K, Object> values) throws SQLException {
      if (resultSet.isAfterLast() || resultSet.isBeforeFirst()) {
        return false;
      } else {
        boolean equals = values != null && (values.size() >= getColumnNames().size());
        if (equals) {
          try {
            String columnName;
            Integer columnIndex;
            boolean columnValuesScan = false;
            boolean indexed = values.keySet().iterator().next() instanceof Integer;
            boolean[] primarysChecked = new boolean[getColumnNames().size()];
            Arrays.fill(primarysChecked, false);
            for (int c = 0; equals && c < primarysChecked.length; c++) {
              if (!primarysChecked[c]) {
                columnName = getColumnNames().get(c);
                columnIndex = columnMapping.get(columnName);
                if (indexed && (columnIndex != null)) {
                  if (values.containsKey(columnIndex) && values.get(columnIndex) != null) {
                    equals = equals && Equals.equals(values.get(columnIndex), resultSet.getObject(columnIndex));
                  } else {
                    resultSet.getObject(columnIndex);
                    equals = equals && resultSet.wasNull();
                  }
                } else if (indexed && !columnValuesScan) {
                  columnValuesScan = true;
                  ResultSetMetaData metaData = resultSet.getMetaData();
                  for (Iterator<Map.Entry<K, Object>> iterator = values.entrySet().iterator(); iterator.hasNext() && equals;) {
                    Map.Entry<K, Object> entry = iterator.next();
                    columnName = metaData.getColumnName(((Integer) entry.getKey()).intValue()).toUpperCase();
                    int index = getColumnNames().indexOf(columnName);
                    if (index >= 0) {
                      primarysChecked[index] = true;
                      if (entry.getValue() != null) {
                        equals = equals && Equals.equals(entry.getValue(), resultSet.getObject(columnName));
                      } else {
                        resultSet.getObject(columnName);
                        equals = equals && resultSet.wasNull();
                      }
                    }
                  }
                } else {
                  if (values.containsKey(columnName) && values.get(columnName) != null) {
                    equals = equals && Equals.equals(values.get(columnName), resultSet.getObject(columnName));
                  } else {
                    resultSet.getObject(columnName);
                    equals = equals && resultSet.wasNull();
                  }
                }
              }
            }
          } catch (SQLException ex) {
            equals = false;
            Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Error comparing the primary key values.", ex);
          }
        }
        return equals;
      }
    }

    public static List<PrimaryKey> getPrimaryKeys(PreparedStatement statement) throws SQLException {
      List<PrimaryKey> result = new ArrayList<PrimaryKey>();
      ResultSetMetaData metaData = statement.getMetaData();
      if (metaData != null) {
        int columnCount = metaData.getColumnCount();

        List<PrimaryKey> keys = new ArrayList<PrimaryKey>();
        Map<String, String> columnTables = new HashMap<String, String>();
        String table;
        PrimaryKey key;

        for (int c = 1; c <= columnCount; c++) {
          table = metaData.getTableName(c);
          if (table != null) {
            columnTables.put(metaData.getColumnName(c).toUpperCase(), table);
            if (keys.indexOf(key = new PrimaryKey(statement.getConnection(), table)) < 0) {
              keys.add(key);
            }
          }
        }


        for (Iterator<PrimaryKey> pk = keys.iterator(); pk.hasNext();) {
          key = pk.next();
          boolean valid = !key.getColumnNames().isEmpty();
          for (Iterator<String> c = key.getColumnNames().iterator(); valid && c.hasNext();) {
            String columnName = c.next();
            valid = columnTables.containsKey(columnName) && columnTables.get(columnName).equals(key.table);
          }
          if (valid) {
            result.add(key);
          }
        }
      }

      return result;
    }

    public boolean equals(Object obj) {
      if (obj instanceof PrimaryKey) {
        return Equals.equals(this.table, ((PrimaryKey) obj).table);
      } else {
        return Equals.equals(this.table, obj);
      }
    }

    /**

     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     * <p>
     * The general contract of <code>hashCode</code> is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the <tt>hashCode</tt> method
     *     must consistently return the same integer, provided no information
     *     used in <tt>equals</tt> comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the <tt>equals(Object)</tt>
     *     method, then calling the <code>hashCode</code> method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the <tt>hashCode</tt> method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hashtables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class <tt>Object</tt> does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java<font size="-2"><sup>TM</sup></font> programming language.)
     *
     *
     * @return a hash code value for this object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.util.Hashtable
     */
    public int hashCode() {
      return hashcode;
    }
  }

  private static class CacheKey {

    int row;
    String columnName;
    int hash;

    private CacheKey(int row, String columnName) {
      this.row = row;
      this.columnName = columnName;
      //this.columnName = columnName.toUpperCase();
      this.hash = ("" + row + "#" + this.columnName).hashCode();
    }

    public boolean equals(Object obj) {
      if (obj != null && (obj instanceof CacheKey)) {
        return (((CacheKey) obj).row == row) && (((CacheKey) obj).columnName.equals(columnName));
      } else {
        return super.equals(obj);
      }
    }

    public int hashCode() {
      return this.hash;
    }
  }

  private static class CacheEntry<K, V> extends SoftReference<SQLDataSource> {

    K key;
    V value;

    private CacheEntry(SQLDataSource referent, K key, V value) {
      super(referent);
      this.key = key;
      this.value = value;
    }

    public boolean equals(Object obj) {
      if (obj != null && (obj instanceof CacheEntry)) {
        if ((key != null) && (key instanceof Number)) {
          return ((Number) this.key).doubleValue() == ((Number) ((CacheEntry) obj).key).doubleValue();
        } else {
          return Equals.equals(this.key, ((CacheEntry) obj).key);
        }
      } else {
        return Equals.equals(this.key, obj);
      }
    }

    public String toString() {
      return value.toString();
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }
  }

  private final static class RunnableEvents implements Runnable {

    SQLDataSource owner;

    RunnableEvents(SQLDataSource owner) {
      this.owner = owner;
    }

    public void run() {
      Logger.getLogger(Settings.LOGGER).fine("Firing events '" + owner.selectSql + "'");
      owner.refreshPending = DataSourceEvent.isRefreshing(owner.owner);
      owner.owner.fireContentsChanged(new ListDataEvent(owner.owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      int pos = 0;
      if (owner.getRowCount() > 0) {
        try {
          if (owner.selectResultSet != null) {
            pos = owner.selectResultSet.getRow();
          }
        } catch (SQLException err) {
          pos = 0;
        }
      }
      owner.owner.fireActiveRowChange(new ActiveRowChangeEvent(owner.owner, pos, -1));
    }
  };

  /**
   * Getter for property uniqueID.
   * @return Value of property uniqueID.
   */
  public String[] getUniqueID() {
    return this.uniqueID;
  }

  /**
   * Setter for property uniqueID.
   * @param uniqueID New value of property uniqueID.
   */
  public void setUniqueID(String[] uniqueID) {
    this.uniqueID = uniqueID;
    if ((getUniqueID() != null) && (getUniqueID().length > 0) && (getUpdateTableName() != null) && (getUpdateTableName().length() > 0)) {
      this.primaryKeys = this.getPrimaryKeys();
    }
  }
  /**
   * Holds value of property delimiterLeft.
   */
  private String delimiterLeft = null;

  /**
   * Getter for property leftDelimiter.
   * @return Value of property leftDelimiter.
   */
  @Override
  public String getDelimiterLeft() {
    ConnectionManager cm = ConnectionManager.getInstance();
    if ((this.delimiterLeft == null) && (cm != null) && (cm.getConnection() != null)) {
      return cm.getProperty(com.openitech.db.DbConnection.DB_DELIMITER_LEFT, "");
    } else {
      return this.delimiterLeft;
    }
  }

  /**
   * Setter for property leftDelimiter.
   * @param leftDelimiter New value of property leftDelimiter.
   */
  @Override
  public void setDelimiterLeft(String delimiterLeft) {
    this.delimiterLeft = delimiterLeft;
  }
  /**
   * Holds value of property delimiterRight.
   */
  private String delimiterRight;

  /**
   * Getter for property rightDelimiter.
   * @return Value of property rightDelimiter.
   */
  @Override
  public String getDelimiterRight() {
    ConnectionManager cm = ConnectionManager.getInstance();
    if ((this.delimiterLeft == null) && (cm != null) && (cm.getConnection() != null)) {
      return cm.getProperty(com.openitech.db.DbConnection.DB_DELIMITER_RIGHT, "");
    } else {
      return this.delimiterRight;
    }
  }

  /**
   * Setter for property rightDelimiter.
   * @param rightDelimiter New value of property rightDelimiter.
   */
  @Override
  public void setDelimiterRight(String delimiterRight) {
    this.delimiterRight = delimiterRight;
  }
}