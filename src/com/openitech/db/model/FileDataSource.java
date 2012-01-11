/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.8 $
 */
package com.openitech.db.model;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.awt.OwnerFrame;
import com.openitech.importer.DataColumn;
import com.openitech.io.LogWriter;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author uros
 */
public abstract class FileDataSource extends AbstractDataSourceImpl {

  public static final String SELECT_1 = "SELECT 1";
  protected int count = 0;
  protected int fetchSize = 54;
  protected Map<Integer, Map<String, Object>> storedUpdates = new HashMap<Integer, Map<String, Object>>();
  protected Map<Integer, Map<String, DataColumn>> rowValues = new HashMap<Integer, Map<String, DataColumn>>();
  protected boolean inserting = false;
  protected DbDataSourceHashMap<String, Integer> columnMapping = new DbDataSourceHashMap<String, Integer>();
  protected DbDataSourceHashMap<Integer, String> columnMappingIndex = new DbDataSourceHashMap<Integer, String>();
  protected boolean[] storedResult = new boolean[]{false, false};
  protected final Semaphore semaphore = new Semaphore(1);
  protected final Runnable events = new RunnableEvents(this);
  protected boolean fireEvents = true;
  protected DbDataSource owner;
  protected boolean readOnly = false;
  protected final LogWriter logWriter = new LogWriter(Logger.getLogger(FileDataSource.class.getName()), Level.INFO);
  /**
   * Holds value of property updateColumnNames.
   */
  protected java.util.Set<String> updateColumnNames = new java.util.HashSet<String>();
  protected java.util.Set<String> updateColumnNamesCS = new java.util.HashSet<String>(); //case sensitive
  protected int currentRow = -1;
  protected boolean isDataLoaded = false;
  protected int columnCount;
  protected File sourceFile;
  protected ColumnNameReader columnReader;

  /** Creates a new instance of DbDataSource */
  public FileDataSource(DbDataSource owner) {
    this.owner = owner;
  }

  @Override
  public void setSource(Object source) {
    if (source != null && source instanceof File) {
      this.sourceFile = (File) source;
    } else {
      throw new IllegalArgumentException("Source must be a file!");
    }
  }

  public void setColumnReader(ColumnNameReader columnReader) {
    this.columnReader = columnReader;
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
  protected List<String> getValueColumns = new ArrayList<String>();

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

  @Override
  public DbDataSourceImpl copy(DbDataSource owner) {
    throw new UnsupportedOperationException();
    /* CSVDataSource result = new CSVDataSource(owner);

    result.selectSql = this.selectSql;
    result.countSql = this.countSql;
    result.preparedSelectSql = this.preparedSelectSql;
    result.preparedCountSql = this.preparedCountSql;
    result.updateTableName = this.updateTableName;
    if (this.primaryKeys != null) {
    result.primaryKeys = new ArrayList<PrimaryKey>();
    result.primaryKeys.addAll(this.primaryKeys);
    }
    result.count = this.count;
    result.fetchSize = this.fetchSize;
    result.columnMapping.putAll(this.columnMapping);
    result.connection = this.connection;
    result.selectStatementReady = this.selectStatementReady;
    result.countStatementReady = this.countStatementReady;
    result.selectStatement = this.selectStatement;
    result.countStatement = this.countStatement;
    //    result.cachedStatements.putAll(this.cachedStatements);
    result.sqlCache = this.sqlCache;
    result.fireEvents = this.fireEvents;
    result.uniqueID = this.uniqueID;
    result.refreshPending = false;

    result.updateColumnNames.addAll(this.updateColumnNames);
    result.updateColumnNamesCS.addAll(this.updateColumnNamesCS);

    return result;
     */
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
  @Override
  public void updateFloat(String columnName, float x) throws SQLException {
    //TODO float ni natan?en 7.45f se v bazo zapi?e 7.449999809265137
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
  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, x);
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
   * @param columnName the name of the column
   * @param x the new column value
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public long getLong(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public int getInt(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public float getFloat(String columnName) throws SQLException {
    //TODO nenatan?no ?e ro?no vnese? v bazo. ?e gre pisanje in branje preko programa potem je uredu
    if (checkedLoadData()) {
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
  @Override
  public double getDouble(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Date getDate(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Clob getClob(String colName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Reader getCharacterStream(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public byte[] getBytes(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public InputStream getAsciiStream(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public int findColumn(String columnName) throws SQLException {
    if (checkedLoadData()) {
      return getColumnIndex(columnName);
//      return openSelectResultSet().findColumn(columnName);
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
  @Override
  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public InputStream getBinaryStream(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Blob getBlob(String colName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public boolean getBoolean(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public byte getByte(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Object getObject(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Ref getRef(String colName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public short getShort(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public String getString(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Time getTime(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Timestamp getTimestamp(String columnName) throws SQLException {
    //TODO ne dela napa?no castanje. O?itno ?e ho?em date potem dela
    if (checkedLoadData()) {
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
  @Override
  public URL getURL(String columnName) throws SQLException {
    if (checkedLoadData()) {
      return getStoredValue(getRow(), columnName, null, URL.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public Object getObject(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public long getLong(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public int getInt(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public float getFloat(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public double getDouble(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Date getDate(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Clob getClob(int i) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Array getArray(String columnName) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Array getArray(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public boolean absolute(int row) throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }

      int oldRow = currentRow;
      currentRow = row;
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

      return true;
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
  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Blob getBlob(int i) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public byte getByte(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Ref getRef(int i) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public short getShort(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public String getString(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Time getTime(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public URL getURL(int columnIndex) throws SQLException {
    if (checkedLoadData()) {
      return getStoredValue(getRow(), columnIndex, null, URL.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
  @Override
  public boolean relative(int rows) throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }

      int oldRow = currentRow;
      currentRow = currentRow + rows;
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

      return true;
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
  @Override
  public void setFetchDirection(int direction) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (isDataLoaded()) {
    openSelectResultSet().setFetchDirection(direction);
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
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
  @Override
  public void setFetchSize(int rows) throws SQLException {
    this.fetchSize = rows;
    if (isDataLoaded()) {
      //openSelectResultSet().setFetchSize(rows);
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
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
  @Override
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
  protected boolean wasNull = false;

  @Override
  public boolean wasNull() throws SQLException {
    if (isDataLoaded()) {
      return wasNull;
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
  @Override
  public void updateString(String columnName, String x) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnName, x);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  public boolean shouldSaveChanges() {
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
  @Override
  public void updateRow() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    boolean storeUpdates = true;
    try {
    owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.UPDATE_ROW));
    } catch (Exception err) {
    if ((err instanceof SQLNotificationException)
    || (err.getCause() instanceof SQLNotificationException)) {
    if (err instanceof SQLNotificationException) {
    throw (SQLNotificationException) err;
    } else {
    throw (SQLNotificationException) err.getCause();
    }
    } else {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, err.getMessage(), err);
    }
    storeUpdates = false;
    }
    if (storeUpdates) {
    storeUpdates(rowInserted());
    removeSharedResult();
    }
    try {
    owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.ROW_UPDATED));
    } catch (Exception err) {
    //
    }
     *
     */
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (this.metaData != null) {
    return this.metaData;
    } else if ((this.metaData = openSelectResultSet().getMetaData()) != null) {
    int columnCount = this.metaData != null ? this.metaData.getColumnCount() : 0;
    for (int c = 1; c <= columnCount; c++) {
    this.columnMapping.put(this.metaData.getColumnName(c), c);
    }
    return this.metaData;
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
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
  @Override
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
  @Override
  public int getFetchDirection() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (checkedLoadData()) {
    return openSelectResultSet().getFetchDirection();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
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
  @Override
  public String getCursorName() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (checkedLoadData()) {
    return openSelectResultSet().getCursorName();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
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
  @Override
  public int getConcurrency() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (checkedLoadData()) {
    return openSelectResultSet().getConcurrency();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
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
  @Override
  public boolean first() throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }

      int oldRow = currentRow;
      currentRow = 1;
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

      return true;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  /*
  protected SQLCache getSqlCache() {
  if ((owner.getSharing() & DbDataSource.SHARING_GLOBAL) == DbDataSource.SHARING_GLOBAL) {
  return SQLCache.getInstance();
  } else if (sqlCache == null) {
  sqlCache = new SQLCache();
  }
  return sqlCache;
  }
   *
   */
  @Override
  public void clearSharedResults() {
    /*
    if ((owner.getSharing() & DbDataSource.SHARING_LOCAL) == DbDataSource.SHARING_LOCAL) {
    getSqlCache().clearSharedResults();
    }
     *
     */
  }

  private void doDeleteRow(ResultSet resultSet) throws SQLException {
    /*
    PreparedStatement delete;
    PrimaryKey key;
    for (Iterator<PrimaryKey> pk = primaryKeys.iterator(); pk.hasNext();) {
    key = pk.next();
    delete = key.getDeleteStatement(resultSet, getTxConnection());
    delete.execute();
    }
     *
     */
  }

  @Override
  public void doDeleteRow() throws SQLException {
    throw new UnsupportedOperationException();
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
  @Override
  public void deleteRow() throws SQLException {
    throw new UnsupportedOperationException();
    /*
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
    ResultSet resultSet = openSelectResultSet();
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
     *
     */
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
  @Override
  public void close() throws SQLException {
    /*
    if (isDataLoaded()) {
    if (!owner.isShareResults()) {
    currentResultSet.close();
    }
    currentResultSet = null;
    owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, -1, -1));
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
  }

  /**
   * Clears all warnings reported on this <code>ResultSet</code> object.
   * After this method is called, the method <code>getWarnings</code>
   * returns <code>null</code> until a new warning is
   * reported for this <code>ResultSet</code> object.
   *
   * @exception SQLException if a database access error occurs
   */
  @Override
  public void clearWarnings() throws SQLException {
    /*
    if (isDataLoaded()) {
    openSelectResultSet().clearWarnings();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
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
  @Override
  public void cancelRowUpdates() throws SQLException {
    if (isDataLoaded()) {
      if (isUpdating()) {
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
          final int row = currentRow;
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
        }
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
  @Override
  public void beforeFirst() throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }

      int oldRow = currentRow;
      currentRow = -1;

      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));
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
  @Override
  public void afterLast() throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      int oldRow = currentRow;
      currentRow = count + 1;

      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));
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
  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    if (checkedLoadData()) {
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
  @Override
  public int getRow() throws SQLException {
    //TODO tukaj pa?e motoda isDataLodaded() in ?e ni mogo?e load data oz. napaka
    if (checkedLoadData()) {
      return inserting ? getRowCount() : currentRow;

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
  @Override
  public Statement getStatement() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    return getSelectStatement(preparedSelectSql, getTxConnection());
     * 
     */
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
  @Override
  public int getType() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (checkedLoadData()) {
    return openSelectResultSet().getType();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
  }

  @Override
  public int getType(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (getMetaData() != null) {
    return getMetaData().getColumnType(columnIndex);
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
  }

  @Override
  public int getType(String columnName) throws SQLException {
    //TODO
    return java.sql.Types.VARCHAR;
    /*
    if (getMetaData() != null) {
    return getMetaData().getColumnType(columnMapping.checkedGet(columnName));
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
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
  @Override
  public SQLWarning getWarnings() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (isDataLoaded()) {
    return openSelectResultSet().getWarnings();
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
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
  @Override
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
  @Override
  public boolean isAfterLast() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : currentRow == (count + 1);
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
  @Override
  public boolean isBeforeFirst() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : currentRow == -1;
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
  @Override
  public boolean isFirst() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? false : currentRow == 1 || (getRowCount() == 0);
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
  @Override
  public boolean isLast() throws SQLException {
    if (isDataLoaded()) {
      return inserting ? true : currentRow == count || (getRowCount() == 0);
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
  @Override
  public boolean last() throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }

      int oldRow = currentRow;
      currentRow = count;
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

      return true;
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
  @Override
  public void moveToCurrentRow() throws SQLException {
    /*
    if (loadData()) {
    if (owner.isSaveChangesOnMove() && rowUpdated()) {
    if (shouldSaveChanges()) {
    updateRow();
    } else {
    cancelRowUpdates();
    }
    }

    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));

    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
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
  @Override
  public void moveToInsertRow() throws SQLException {
    //TODO isreadOnly()?  zakaj ne uporabljamo moveToInsertRow() od resultset?
    if (owner.isCanAddRows()) {
      if (checkedLoadData()) {
        if (owner.isSaveChangesOnMove() && rowUpdated()) {
          if (shouldSaveChanges()) {
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
          int oldRow = currentRow;
          inserting = true;

          int columnCount = getColumnCount();
          String columnName;
          storedUpdates.remove(new Integer(getRow()));
          for (int c = 1; c <= columnCount; c++) {
            columnName = getColumnName(c);
            storeUpdate(columnName, owner.getDefaultValues().containsKey(columnName) ? owner.getDefaultValues().get(columnName) : null, false, true);
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
  @Override
  public boolean next() throws SQLException {
    if (checkedLoadData()) {
      if (owner.isSaveChangesOnMove() && rowUpdated()) {
        if (shouldSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      if (!isLast()) {
        int oldRow = currentRow;
        currentRow++;
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

        return true;
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
  @Override
  public boolean previous() throws SQLException {
    if (checkedLoadData()) {
      if (rowUpdated()) {
        if (owner.isSaveChangesOnMove() && rowUpdated()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      //TODO ali je to prav da ne gre na before first?
      if (!isFirst()) {
        int oldRow = currentRow;
        currentRow--;
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));

        return true;
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
  @Override
  public void refreshRow() throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (isDataLoaded()) {
    if (rowUpdated()) {
    cancelRowUpdates();
    }
    final ResultSet openSelectResultSet = openSelectResultSet();
    openSelectResultSet.refreshRow();
    int row = openSelectResultSet.getRow();
    owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, row - 1, row - 1));
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     * 
     */
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
  @Override
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
  @Override
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
  @Override
  public boolean rowUpdated() throws SQLException {
    if (isDataLoaded()) {
      return storedUpdates.containsKey(new Integer(getRow()));
    } else {
      return false;
    }
  }

  @Override
  public void startUpdate() throws SQLException {
    if (isDataLoaded()) {
      if ((getRowCount() > 0) && !rowUpdated()) {
        storedUpdates.put(new Integer(getRow()), new HashMap<String, Object>());
        owner.fireActionPerformed(new ActionEvent(owner, 0, DbDataSource.UPDATING_STARTED));
//        owner.fireFieldValueChanged(new ActiveRowChangeEvent(owner, "", -1));
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
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    if (isDataLoaded()) {
      storeUpdate(columnIndex, new Scale("updateAsciiStream", x, length));
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).entering(this.getClass().toString(), "storeUpdates", insert);
    Scale scaledValue;
    Entry<String, Object> entry;

    final Connection connection = getTxConnection();

    int columnCount = getColumnCount();

    String delimiterLeft = getDelimiterLeft();
    String delimiterRight = getDelimiterRight();

    if (insert) {
    String catalogName = getCatalogName();
    String schemaName = getSchemaName();
    String tableName = getUpdateTableName();

    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();

    ResultSetMetaData metaData = getMetaData();
    List<String> skipValues = new ArrayList<String>();

    int columnIndex;

    for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
    entry = i.next();
    columnIndex = columnMapping.checkedGet(entry.getKey()).intValue();

    if (updateColumnNames.size() > 0) {
    if (!updateColumnNames.contains(entry.getKey())) {
    skipValues.add(entry.getKey());
    continue;
    }
    }

    if (!owner.isSingleTableSelect()) {
    if (catalogName == null) {
    catalogName = metaData.getCatalogName(columnIndex);
    } else if ((updateTableName == null) && (!catalogName.equalsIgnoreCase(metaData.getCatalogName(columnIndex)))) {
    throw new SQLException("Insert on different catalogs not supported. Shema: " + catalogName + " != " + metaData.getCatalogName(columnIndex));
    }
    if (schemaName == null) {
    schemaName = metaData.getSchemaName(columnIndex);
    } else if ((updateTableName == null) && (!schemaName.equalsIgnoreCase(metaData.getSchemaName(columnIndex)))) {
    throw new SQLException("Insert on different schemas not supported. Shema: " + schemaName + " != " + metaData.getSchemaName(columnIndex));
    }
    if (tableName == null) {
    //TODO ne dela. vedno vra?a null
    tableName = metaData.getTableName(columnIndex);
    } else if (!tableName.equalsIgnoreCase(metaData.getTableName(columnIndex))) {
    if (updateTableName == null) {
    throw new SQLException("Insert on different tables not supported.");
    }
    }
    }
    if (entry.getValue() != null || metaData.isNullable(columnIndex) != ResultSetMetaData.columnNoNulls) {
    columns.append(columns.length() > 0 ? "," : "").append(delimiterLeft).append(entry.getKey()).append(delimiterRight);
    values.append(values.length() > 0 ? "," : "").append("?");
    } else {
    skipValues.add(entry.getKey());
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Skipping null value: '" + entry.getKey() + "'");
    }
    }

    catalogName = catalogName == null ? "" : catalogName;
    schemaName = schemaName == null ? "" : schemaName;

    StringBuilder sql = new StringBuilder();

    sql.append("INSERT INTO ");
    if (catalogName.length() > 0 && schemaName.length() > 0) {
    sql.append(delimiterLeft).append(catalogName).append(delimiterRight).append(".");
    }
    if (schemaName.length() > 0) {
    sql.append(delimiterLeft).append(schemaName).append(delimiterRight).append(".");
    }
    sql.append(delimiterLeft).append(tableName).append(delimiterRight).append(" (").append(columns).append(") ");
    sql.append("VALUES (").append(values).append(")");

    PreparedStatement insertStatement = getTxConnection().prepareStatement(sql.toString());
    try {
    ParameterMetaData parameterMetaData = insertStatement.getParameterMetaData();

    int p = 1;

    for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
    entry = i.next();
    if (skipValues.indexOf(entry.getKey()) == -1) {
    /*if (entry.getValue()==null)
    insertStatement.setNull(p, parameterMetaData.getParameterType(p++));
    else//
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
    //TODO preveriti ?e je timestamp in dati setTimestamp
    insertStatement.setObject(p++, entry.getValue(), getType(entry.getKey()));

    }
    oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(), entry.getValue());
    }
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Executing insert : '" + sql + "'");
    insertStatement.setQueryTimeout(15);
    insertStatement.executeUpdate();
    } finally {
    insertStatement.close();
    }
    } else {
    final ResultSet openSelectResultSet = openSelectResultSet();
    ResultSetMetaData metaData = openSelectResultSet.getMetaData();
    List<String> skipColumns = new ArrayList<String>();
    for (int c = 1; c <= columnCount; c++) {
    String columnName = metaData.getColumnName(c).toUpperCase();
    if ((updateTableName == null || (updateTableName != null && updateTableName.equalsIgnoreCase(metaData.getTableName(c)))) && (updateColumnNames.size() == 0 || updateColumnNames.contains(columnName))) {
    try {
    Object value = openSelectResultSet.getObject(c);
    oldValues.put(c, value);
    } catch (Exception err) {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Skipping illegal value for: '" + columnName + "'");
    skipColumns.add(columnName);
    }
    } else {
    skipColumns.add(columnName);
    }
    }
    PrimaryKey key;
    for (Iterator<PrimaryKey> pk = primaryKeys.iterator(); pk.hasNext();) {
    key = pk.next();
    ResultSet updateResultSet = key.getUpdateResultSet(openSelectResultSet, connection);

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
    updateResultSet.updateTimestamp(entry.getKey(), new java.sql.Timestamp(((java.util.Date) entry.getValue()).getTime()));
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
    //TODO timestamp
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
    StringBuilder set = new StringBuilder(540);
    for (Iterator<Map.Entry<String, Object>> i = columnValues.entrySet().iterator(); i.hasNext();) {
    entry = i.next();
    if ((skipColumns.indexOf(entry.getKey()) == -1) && (metaData.getTableName(columnMapping.checkedGet(entry.getKey()).intValue()).equalsIgnoreCase(key.table))) {
    set.append(set.length() > 0 ? ", " : "").append(delimiterLeft).append(entry.getKey()).append(delimiterRight).append(" = ?");
    updateCount++;
    }
    }

    if (updateCount > 0) {
    StringBuilder where = new StringBuilder();

    for (String c : key.getColumnNames(connection)) {
    where.append(where.length() > 0 ? " AND " : "").append(delimiterLeft).append(c).append(delimiterRight).append(" = ? ");
    }
    String sql = "UPDATE " + (key.catalogName != null && key.schemaName != null ? delimiterLeft + key.catalogName + delimiterRight + "." : "") + (key.schemaName != null ? delimiterLeft + key.schemaName + delimiterRight + "." : "") + delimiterLeft + key.table + delimiterRight + " SET " + set.toString() + " WHERE " + where.toString();

    PreparedStatement updateStatement = getTxConnection().prepareStatement(sql.toString());
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
    //TODO timestamp
    updateStatement.setObject(p++, entry.getValue());
    }
    oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(), entry.getValue());
    }
    }
    for (String c : key.getColumnNames(connection)) {
    Object value = openSelectResultSet.getObject(c);
    if (value == null) {
    updateStatement.setNull(p, parameterMetaData.getParameterType(p++));
    } else {
    updateStatement.setObject(p++, value);
    }
    oldValues.put(columnMapping.checkedGet(c).intValue(), value);
    }
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Executing update : '" + sql + "'");
    updateStatement.setQueryTimeout(15);
    updateStatement.executeUpdate();
    } finally {
    updateStatement.close();
    }
    }
    }
    }
    }
     */
  }

  @Override
  public void setUpdateTableName(String updateTableName) {
  }

  @Override
  public String getUpdateTableName() {
    throw new UnsupportedOperationException();
    /*
    return updateTableName;
     *
     */
  }

  @Override
  public void setSelectSql(String selectSql) throws SQLException {
    //setSelectSql(selectSql, false);
  }

  @Override
  public int getColumnIndex(String columnName) throws SQLException {
    if ((columnName != null) && (columnName instanceof String)) {
      columnName = ((String) columnName).toUpperCase();
    }

    if (columnMapping.containsKey(columnName)) {
      return columnMapping.checkedGet(columnName).intValue();
    } else {
      return -1;
      /* int ci = -1;
      try {
      ci = openSelectResultSet().findColumn(columnName);
      } finally {
      if (ci == -1) {
      Logger.getLogger(CSVDataSource.class.getName()).log(Level.WARNING, "Invalid column name [" + columnName + "]");
      }
      }
      columnMapping.put(columnName, ci);
      return ci;
       *
       */
    }
  }

  @Override
  public void setCountSql(String countSql) throws SQLException {
    /*String oldvalue = this.countSql;
    try {
    semaphore.acquire();
    this.countSql = countSql;
    String sql = substParameters(countSql, getCountParameters(owner.getParameters()));
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).finest(
    "\n################# COUNT SQL #################\n" + sql + "\n################# ######### #################");
    preparedCountSql = null;
    final Connection connection = getConnection();
    try {
    if (getCountStatement(sql, connection) != null) {
    preparedCountSql = sql;
    }
    } finally {
    if (owner.isConnectOnDemand()) {
    connection.close();
    }
    }
    } catch (InterruptedException ex) {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Interrupted while preparing ''{0}''", countSql);
    } finally {
    semaphore.release();
    }
    owner.firePropertyChange("countSql", oldvalue, this.countSql);
     *
     */
  }

  @Override
  public String getSelectSql() {
    return null;
    /*
    return selectSql;
     *
     */
  }

  @Override
  public String getCountSql() {
    return null;
    /*
    return countSql;
     * 
     */
  }

  @Override
  public Connection getConnection() {
    throw new UnsupportedOperationException();
    /*
    if (this.connection == null) {
    if (ConnectionManager.getInstance() != null) {
    if (owner.isConnectOnDemand()) {
    try {
    return ConnectionManager.getInstance().getTemporaryConnection();
    } catch (SQLException ex) {
    Logger.getLogger(CSVDataSource.class.getName()).log(Level.SEVERE, null, ex);
    return null;
    }
    } else {
    return ConnectionManager.getInstance().getConnection();
    }
    } else {
    return null;
    }
    } else {
    return this.connection;

    }
     *
     */
  }

  @Override
  public void setConnection(Connection connection) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (this.connection != connection) {
    for (PreparedStatement statement : cachedStatements.values()) {
    statement.close();
    }
    cachedStatements.clear();

    this.connection = connection;

    setSelectSql(this.selectSql);
    if (countSql != null) {
    setCountSql(countSql);
    }
    }
     *
     */
  }

  @Override
  public int getRowCount() {

    return this.count + (inserting ? 1 : 0);

  }

  @Override
  public boolean fireEvents() {
    return fireEvents;
  }

  @Override
  public boolean isDataLoaded() {
    return isDataLoaded;
  }

  protected boolean checkedLoadData() {
    return isDataLoaded() || loadData();
  }

  private boolean loadData(boolean reload) {
    boolean loadData = loadData(reload, Integer.MIN_VALUE);

    if (loadData) {
      if (EventQueue.isDispatchThread() || !owner.isSafeMode()) {
        events.run();
      } else {
        try {
          EventQueue.invokeAndWait(events);
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't notify loaddata results from '" + sourceFile + "'", ex);
        }
      }
    }
    return loadData;
  }
  private boolean loadingData = false;

  @Override
  public abstract boolean loadData(boolean reload, int oldRow);

 
  public boolean loadData() {
    return loadData(false);
  }

  @Override
  public void loadData(DbDataSourceImpl dataSource, int oldRow) {
    /*
    CSVDataSource sqlDataSource = (CSVDataSource) dataSource;

    boolean reloaded = false;

    owner.lock();
    try {
    owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.LOAD_DATA));
    this.selectSql = sqlDataSource.selectSql;
    this.countSql = sqlDataSource.countSql;
    this.preparedSelectSql = sqlDataSource.preparedSelectSql;
    this.preparedCountSql = sqlDataSource.preparedCountSql;
    this.updateTableName = sqlDataSource.updateTableName;
    if (sqlDataSource.primaryKeys != null) {
    this.primaryKeys = new ArrayList<PrimaryKey>();
    this.primaryKeys.addAll(sqlDataSource.primaryKeys);
    }
    this.count = sqlDataSource.count;
    this.fetchSize = sqlDataSource.fetchSize;

    this.columnMapping.clear();
    this.columnMapping.putAll(sqlDataSource.columnMapping);

    this.connection = sqlDataSource.connection;
    this.selectStatementReady = sqlDataSource.selectStatementReady;
    this.countStatementReady = sqlDataSource.countStatementReady;
    this.selectStatement = sqlDataSource.selectStatement;
    this.countStatement = sqlDataSource.countStatement;

    //      this.cachedStatements.clear();
    //      this.cachedStatements.putAll(sqlDataSource.cachedStatements);
    this.sqlCache = sqlDataSource.sqlCache;
    this.fireEvents = sqlDataSource.fireEvents;
    this.uniqueID = sqlDataSource.uniqueID;
    this.refreshPending = false;

    this.updateColumnNames.clear();
    this.updateColumnNames.addAll(sqlDataSource.updateColumnNames);

    this.updateColumnNamesCS.clear();
    this.updateColumnNamesCS.addAll(sqlDataSource.updateColumnNamesCS);
    this.currentResultSet = sqlDataSource.currentResultSet;
    } finally {
    inserting = false;
    count = -1;
    storedUpdates.clear();
    cache.clear();
    pendingValuesCache.clear();
    for (Object parameter : owner.getParameters()) {
    if (parameter instanceof PendingSqlParameter) {
    ((PendingSqlParameter) parameter).emptyPendingValuesCache();
    }
    }
    reloaded = true;
    owner.unlock();
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).finer("Permit unlockd '" + selectSql + "'");
    }
    if (oldRow > 0 && getRowCount() > 0) {
    try {
    currentResultSet.currentResultSet.absolute(Math.min(oldRow, getRowCount()));
    } catch (SQLException ex) {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't change rowset position", ex);
    }
    }
    owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.DATA_LOADED));

    if (reloaded && currentResultSet != null) {
    if (EventQueue.isDispatchThread() || !owner.isSafeMode()) {
    events.run();
    } else {
    try {
    EventQueue.invokeAndWait(events);
    } catch (Exception ex) {
    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't notify loaddata results from '" + selectSql + "'", ex);
    }
    }
    }
     * 
     */
  }

  public boolean reload() {
    return loadData(true);
  }

  public boolean reload(int oldRow) {
    return loadData(true, oldRow);
  }

  @Override
  public int getColumnCount() throws SQLException {
    return columnCount;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    return getValueAt(rowIndex, getColumnName(columnIndex));
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    String[] columns = this.getValueColumns.toArray(new String[this.getValueColumns.size() + 1]);
    columns[this.getValueColumns.size()] = columnName;
    return getValueAt(rowIndex, columnName, columns);
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException {
    if (!isDataLoaded()) {
      return null;
    } else if (checkedLoadData()) {
      Object result = null;
      columnName = columnName.toUpperCase();

      if (wasUpdated(rowIndex, columnName)) {
        result = getStoredValue(rowIndex, columnName, null, Object.class);
      } else {
        if (rowIndex > getRowCount()) {
          throw new SQLException("Invalid row number " + rowIndex + " for " + toString() + "[" + rowIndex + ">" + getRowCount() + "] ");
        } else {
          owner.lock();
          try {
            //TODO
            result = getStoredValue(rowIndex, columnName, null, Object.class);
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

  @Override
  public String getColumnName(int columnIndex) throws SQLException {
    return columnMappingIndex.checkedGet(columnIndex);

  }

  @Override
  public boolean isUpdating() throws SQLException {
    return rowInserted() || rowUpdated();
  }

  @Override
  public boolean isRefreshPending() {
    return false;
  }

  @Override
  public void updateRefreshPending() {
  }

  @Override
  public Map<Integer, Map<String, Object>> getStoredUpdates() {
    return java.util.Collections.unmodifiableMap(storedUpdates);
  }

  private void storeUpdate(int columnIndex, Object value) throws SQLException {
    storeUpdate(getColumnName(columnIndex), value);
  }

  private void storeUpdate(String columnName, Object value) throws SQLException {
    storeUpdate(columnName, value, true);
  }

  private void storeUpdate(String columnName, Object value, boolean notify) throws SQLException {
    storeUpdate(columnName, value, notify, false);
  }

  private void storeUpdate(String columnName, Object value, boolean notify, boolean inserting) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (!inserting) {
    //TODO zakaj rowCount==0?
    if (getRowCount() == 0 && !isReadOnly() && owner.isAutoInsert()) {
    moveToInsertRow();
    }
    }
    //TODO obvestilo o napaki?
    if (getRow() > 0 && !isReadOnly()) {
    columnName = columnName.toUpperCase();
    Integer row = new Integer(getRow());
    boolean isUpdating = inserting || storedUpdates.containsKey(row);

    final ResultSet openSelectResultSet = openSelectResultSet();
    final Object resultSetValue = (openSelectResultSet.getRow() == 0) || inserting ? null : openSelectResultSet.getObject(columnName);

    if (isUpdating || !Equals.equals(value, resultSetValue)) {
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
     * 
     */
  }

  @Override
  public boolean hasChanged(int columnIndex) throws SQLException {
    return false;
    /*
    if (inserting) {
    return true;
    } else if (wasUpdated(columnIndex)) {
    return !com.openitech.util.Equals.equals(openSelectResultSet().getObject(columnIndex), getObject(columnIndex));
    } else {
    return false;
    }
     *
     */
  }

  @Override
  public boolean hasChanged(String columnName) throws SQLException {
    return false;
    /*
    if (inserting) {
    return true;
    } else if (wasUpdated(columnName)) {
    return !com.openitech.util.Equals.equals(openSelectResultSet().getObject(columnName), getObject(columnName));
    } else {
    return false;
    }
     */
  }

  @Override
  public Object getOldValue(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException();
    //return openSelectResultSet().getObject(columnIndex);
  }

  @Override
  public Object getOldValue(String columnName) throws SQLException {
    throw new UnsupportedOperationException();
    //return openSelectResultSet().getObject(columnName);
  }

  public boolean wasUpdated(int columnIndex) throws SQLException {
    return wasUpdated(getRow(), getColumnName(columnIndex));
  }

  public boolean wasUpdated(String columnName) throws SQLException {
    return wasUpdated(getRow(), columnName);
  }

  @Override
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

  protected <T> T getStoredValue(int row, int columnIndex, T nullValue, Class<? extends T> type) throws SQLException {
    return getStoredValue(row, getColumnName(columnIndex), nullValue, type);
  }

  protected abstract <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException;

  public boolean isPending(String columnName) throws SQLException {
    return isPending(columnName, getRow());
  }

  @Override
  public boolean isPending(String columnName, int row) throws SQLException {
    return false;

  }

  @Override
  public void storeUpdates(boolean insert) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDataSourceName() {
    return owner.getName();
  }

  @Override
  public void setDataSourceName(String name) throws SQLException {
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
  public ResultSet getResultSet() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public CachedRowSet getCachedRowSet() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void filterChanged() throws SQLException {
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

  protected class DbDataSourceHashMap<K, V> extends HashMap<K, V> {

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
        throw new SQLException("DbDataSource for 'File"+ (sourceFile == null ? "null" : sourceFile.getName()) + "' does not contain '" + key.toString() + "'.");
      }
    }

    @Override
    public V put(K key, V value) {
      if (key instanceof String) {
        return super.put((K) (((String) key).toUpperCase()), value);
      } else {
        return super.put(key, value);
      }
    }
  }

  private final static class RunnableEvents implements Runnable {

    FileDataSource owner;

    RunnableEvents(FileDataSource owner) {
      this.owner = owner;
    }

    @Override
    public void run() {
      owner.owner.fireContentsChanged(new ListDataEvent(owner.owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      int pos = 0;
      if (owner.isDataLoaded()) {
        try {
          pos = owner.getRow();
        } catch (SQLException ex) {
          Logger.getLogger(FileDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }

      }
      owner.owner.fireActiveRowChange(new ActiveRowChangeEvent(owner.owner, pos, -1));
    }
  };
  private String catalogName;

  /**
   * Get the value of catalogName
   *
   * @return the value of catalogName
   */
  @Override
  public String getCatalogName() {
    return catalogName;
  }

  /**
   * Set the value of catalogName
   *
   * @param catalogName new value of catalogName
   */
  @Override
  public void setCatalogName(String catalogName) {
    if (catalogName != null) {
      if (catalogName.startsWith(getDelimiterLeft())) {
        catalogName = catalogName.substring(getDelimiterLeft().length());
      }
      if (catalogName.endsWith(getDelimiterRight())) {
        catalogName = catalogName.substring(0, catalogName.length() - getDelimiterRight().length());
      }
    }
    this.catalogName = catalogName;

  }
  private String schemaName;

  /**
   * Get the value of schemaName
   *
   * @return the value of schemaName
   */
  @Override
  public String getSchemaName() {
    return schemaName;
  }

  /**
   * Set the value of schemaName
   *
   * @param schemaName new value of schemaName
   */
  @Override
  public void setSchemaName(String schemaName) {
    if (schemaName != null) {
      if (schemaName.startsWith(getDelimiterLeft())) {
        schemaName = schemaName.substring(getDelimiterLeft().length());
      }
      if (catalogName.endsWith(getDelimiterRight())) {
        schemaName = schemaName.substring(0, schemaName.length() - getDelimiterRight().length());
      }
    }
    this.schemaName = schemaName;

  }

  /**
   * Getter for property uniqueID.
   * @return Value of property uniqueID.
   */
  @Override
  public String[] getUniqueID() {
    return null;
  }

  /**
   * Setter for property uniqueID.
   * @param uniqueID New value of property uniqueID.
   */
  @Override
  public void setUniqueID(String[] uniqueID) {
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
    if ((this.delimiterLeft == null) && (cm != null) && (cm.getTxConnection() != null)) {
      return cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_LEFT, "");
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
    if ((this.delimiterLeft == null) && (cm != null) && (cm.getTxConnection() != null)) {
      return cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_RIGHT, "");
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

  @Override
  public void destroy() {
  }
}
