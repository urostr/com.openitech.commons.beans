/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.17 $
 */

package com.openitech.db.model;

import com.openitech.Settings;
import com.openitech.db.ConnectionManager;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.events.StoreUpdatesListener;
import com.openitech.db.model.concurrent.ConcurrentEvent;
import com.openitech.db.model.concurrent.DataSourceActiveRowChangeEvent;
import com.openitech.db.model.concurrent.DataSourceListDataEvent;
import com.openitech.formats.FormatFactory;
import com.openitech.util.Equals;
import com.openitech.ref.WeakListenerList;
import com.openitech.util.OwnerFrame;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class DbDataSource implements DbNavigatorDataSource {
  public final static String MOVE_TO_INSERT_ROW="moveToInsertRow";
  public final static String UPDATE_ROW="updateRow";
  public final static String ROW_UPDATED="rowUpdated";
  public final static String CANCEL_UPDATES="cancelUpdates";
  public final static String DELETE_ROW="deleteRow";
  public final static String ROW_DELETED="rowDeleted";
  public final static String STORE_UPDATES="storeUpdates";
  
  private String selectSql;
  private String countSql;
  
  private String preparedSelectSql;
  private String preparedCountSql;
  
  private String updateTableName;
  
  
  private transient WeakListenerList activeRowChangeListeners;
  private transient WeakListenerList storeUpdatesListeners;
  private transient WeakListenerList listDataListeners;
  private transient WeakListenerList actionListeners;
  /**
   * If any <code>PropertyChangeListeners</code> have been registered,
   * the <code>changeSupport</code> field describes them.
   *
   * @serial
   * @since 1.2
   * @see #addPropertyChangeListener
   * @see #removePropertyChangeListener
   * @see #firePropertyChange
   */
  private PropertyChangeSupport changeSupport;
  
  private transient PreparedStatement selectStatement;
  private transient PreparedStatement countStatement;
  private List<PrimaryKey> primaryKeys;
  
  private transient ResultSet selectResultSet = null;
  
  private int count = 0;
  
  private final List<Object> parameters = new ArrayList<Object>();
  private Map<String,Object> defaultValues = new HashMap<String,Object>();
  private Map<Integer,Map<String,Object>> storedUpdates = new HashMap<Integer,Map<String,Object>>();
  private boolean readOnly = false;
  private boolean inserting = false;
  private transient ResultSetMetaData metaData = null;
  private DbDataSourceHashMap<String,Integer> columnMapping = new DbDataSourceHashMap<String,Integer>();
  
  private boolean canAddRows = true;
  private boolean canDeleteRows = true;
  
  private Pattern namePattern = Pattern.compile(".*from (.*)\\s[where|for]?.*");
  private String name = "";
  private boolean[] storedResult = new boolean[] { false, false };
  
  private final ReentrantLock available = new ReentrantLock();
  private final Semaphore semaphore = new Semaphore(1);
  private transient Map<CacheKey,CacheEntry<String,Object>> cache = new HashMap<CacheKey,CacheEntry<String,Object>>();
  
  private final Runnable events = new RunnableEvents(this);
  private long queuedDelay = 108;
  
  private Connection connection = null;

  
  
  /**
   * Holds value of property uniqueID.
   */
  private String[] uniqueID;

  
  /** Creates a new instance of DbDataSource */
  public DbDataSource() {
  }
  
  public DbDataSource(String selectSql) {
    this(selectSql, null);
  }
  
  public DbDataSource(String selectSql, String countSql) {
    try {
      setCountSql(countSql);
      setSelectSql(selectSql);
    } catch (SQLException ex) {
      throw (IllegalArgumentException) (new IllegalArgumentException("Failed to create a DbDataSource instance")).initCause(ex);
    }
  }
  
  public String getName() {
    return name;
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnName,null);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,0l, Number.class);
      return value==null?null:value.longValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,0, Number.class);
      return value==null?null:value.intValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,0f, Number.class);
      return value==null?null:value.floatValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,0d, Number.class);
      return value==null?null:value.doubleValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Date.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),colName,null, Clob.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Reader.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return (byte[]) getStoredValue(getRow(),columnName,new byte[] {}, Object.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName, null, InputStream.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, BigDecimal.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, InputStream.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),colName,null, Blob.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,false, Boolean.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,(byte) 0, Number.class);
      return value==null?null:value.byteValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Object.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),colName,null, Ref.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnName,(short) 0, Number.class);
      return value==null?null:value.shortValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName, null, String.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Time.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Timestamp.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, URL.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Object.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnIndex,0l, Number.class);
      return value==null?null:value.longValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnIndex,0, Number.class);
      return value==null?null:value.intValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnIndex,0f, Number.class);
      return value==null?null:value.floatValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnIndex,0d, Number.class);
      return value==null?null:value.doubleValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Date.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),i,null, Clob.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Reader.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return (byte[]) getStoredValue(getRow(),columnIndex,new byte[] {}, Object.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, InputStream.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, Array.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Array.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.absolute(row);
      if (res) {
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, BigDecimal.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, InputStream.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),i,null, Blob.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,false, Boolean.class);
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
      Number value = getStoredValue(getRow(),columnIndex,(byte) 0, Number.class);
      return value==null?null:value.byteValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),i,null, Ref.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      Number value = getStoredValue(getRow(),columnIndex,(short) 0, Number.class);
      return value==null?null:value.shortValue();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, String.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Time.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, Timestamp.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, URL.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.relative(rows);
      if (res) {
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if (isDataLoaded()) {
      selectResultSet.setFetchSize(rows);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
    
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnName, new Scale( "updateObject", x, scale ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnName, new Scale( "updateCharacterStream", reader, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnName, new Scale( "updateBinaryStream", x, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnName,null, BigDecimal.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnName, new Scale( "updateAsciiStream", x, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return storedResult[0]?storedResult[1]:selectResultSet.wasNull();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public boolean canUpdateRow() {
    return (JOptionPane.showOptionDialog(OwnerFrame.getInstance().getOwner(),
            "Ali naj shranim spremembe ?",
            "Preveri",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[] {"Da","Ne"},
            "Ne")==JOptionPane.YES_OPTION);
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
      fireActionPerformed(new ActionEvent(this,1,UPDATE_ROW));
    } catch (Exception err) {
      storeUpdates = false;
    }
    if (storeUpdates)
      storeUpdates(rowInserted());
    try {
      fireActionPerformed(new ActionEvent(this,1,ROW_UPDATED));
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
      storeUpdate(columnIndex, new Scale( "updateObject", x, scale ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnIndex, new Scale( "updateCharacterStream", x, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnIndex, new Scale( "updateBinaryStream", x, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  /**
   * Retrieves the  number, types and properties of
   * this <code>ResultSet</code> object's columns.
   *
   * @return the description of this <code>ResultSet</code> object's columns
   * @exception SQLException if a database access error occurs
   */
  public ResultSetMetaData getMetaData() throws SQLException {
    if (this.metaData!=null) {
      return this.metaData;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if (loadData()) {
      return getOpenSelectResultSet().getFetchSize();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.first();
      if (res) {
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if (canDeleteRows) {
      if(isDataLoaded()) {
        boolean wasinserting = inserting;
        if (rowUpdated())
          cancelRowUpdates();
        if (!wasinserting) {
          boolean deleteRow = true;
          try {
            fireActionPerformed(new ActionEvent(this,1,DELETE_ROW));
          } catch (Exception err) {
            deleteRow = false;
          }
          if (deleteRow) {
            ResultSet resultSet = getOpenSelectResultSet();
            int oldRow = resultSet.getRow();
            if (isUpdateRowFireOnly()) {
              fireDeleteRow(new StoreUpdatesEvent(this, getRow(), false, null));
            } else {
              PreparedStatement delete;
              PrimaryKey key;
              for (Iterator<PrimaryKey> pk=primaryKeys.iterator(); pk.hasNext();) {
                key = pk.next();
                delete = key.getDeleteStatement(resultSet);
                delete.execute();
              }
            }
            try {
              fireActionPerformed(new ActionEvent(this,1,ROW_DELETED));
            } catch (Exception err) {
              //
            }
            reload(oldRow);
          }
        }
      } else
        throw new SQLException("Ni pripravljenih podatkov.");
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
    if(isDataLoaded()) {
      selectResultSet.close();
      selectResultSet=null;
      fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      fireActiveRowChange(new ActiveRowChangeEvent(this, -1, -1));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if(isDataLoaded()) {
      selectResultSet.clearWarnings();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if(isDataLoaded()) {
      boolean cancelUpdates = true;
      try {
        fireActionPerformed(new ActionEvent(this,1,CANCEL_UPDATES));
      } catch (Exception err) {
        cancelUpdates = false;
      }
      if (cancelUpdates) {
        storedUpdates.remove(new Integer(getRow()));
        if (inserting) {
          inserting = false;
          fireIntervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, getRowCount(), getRowCount()));
        }
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), selectResultSet.getRow()));
      }
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();;
      selectResultSet.beforeFirst();
      fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();;
      selectResultSet.afterLast();
      fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return getStoredValue(getRow(),columnIndex,null, BigDecimal.class);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return inserting?getRowCount():getOpenSelectResultSet().getRow();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public int getType(int columnIndex) throws SQLException {
    if (getMetaData()!=null) {
      return getMetaData().getColumnType(columnIndex);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public int getType(String columnName) throws SQLException {
    if (getMetaData()!=null) {
      return getMetaData().getColumnType(columnMapping.checkedGet(columnName));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return inserting?false:selectResultSet.isAfterLast();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return inserting?false:selectResultSet.isBeforeFirst();
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return inserting?false:selectResultSet.isFirst()||(getRowCount()==0);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      return inserting?true:selectResultSet.isLast()||(getRowCount()==0);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();
      boolean res = selectResultSet.last();
      if (res) {
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
      }
      return res;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      int oldRow = getOpenSelectResultSet().getRow();
      selectResultSet.moveToCurrentRow();
      if (selectResultSet.getRow()!=oldRow)
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    if (canAddRows) {
      if (loadData()) {
        if (rowUpdated()) {
          if (canUpdateRow())
            updateRow();
          else
            cancelRowUpdates();
        }
        
        boolean moveToInsertRow = true;
        try {
          fireActionPerformed(new ActionEvent(this,1,MOVE_TO_INSERT_ROW));
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
          for (int c=1; c<=columnCount; c++) {
            columnName = metaData.getColumnName(c);
            storeUpdate(columnName, defaultValues.containsKey(columnName)?defaultValues.get(columnName):null, false);
          }
          
          fireIntervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, getRowCount()-1, getRowCount()-1));
          fireActiveRowChange(new ActiveRowChangeEvent(this, getRow(), oldRow));
        }
      } else
        throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      if (!isLast()) {
        int oldRow = getOpenSelectResultSet().getRow();
        boolean res = selectResultSet.next();
        if (res) {
          fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
        }
        return res;
      } else
        return false;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
        if (canUpdateRow())
          updateRow();
        else
          cancelRowUpdates();
      }
      if (!isFirst()) {
        int oldRow = getOpenSelectResultSet().getRow();;
        boolean res = selectResultSet.previous();
        if (res) {
          fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), oldRow));
        }
        return res;
      } else
        return false;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      if (rowUpdated())
        cancelRowUpdates();
      getOpenSelectResultSet().refreshRow();
      int row = selectResultSet.getRow();
      fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, row-1, row-1));
      fireActiveRowChange(new ActiveRowChangeEvent(this, row, row));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public void startUpdate() throws SQLException {
    if (isDataLoaded()) {
      if ((getRowCount()>0)&&!rowUpdated()) {
        storedUpdates.put(new Integer(getRow()),new HashMap<String,Object>());
        fireFieldValueChanged(new ActiveRowChangeEvent(this, "", -1));
      }
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
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
      storeUpdate(columnIndex, new Scale( "updateAsciiStream", x, length ));
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public synchronized void removeListDataListener(ListDataListener l) {
    if (listDataListeners != null && listDataListeners.contains(l)) {
      listDataListeners.removeElement(l);
    }
  }
  
  public synchronized void addListDataListener(ListDataListener l) {
    WeakListenerList v = listDataListeners == null ? new WeakListenerList(2) : listDataListeners;
    if (l instanceof ConcurrentEvent && !(l instanceof DataSourceListDataEvent))
      l = new DataSourceListDataEvent(this,l);
    if (!v.contains(l)) {
      v.addElement(l);
      listDataListeners = v;
      if (getRowCount()>0)
        l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
    }
  }
  
  protected void fireIntervalAdded(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++)
          ((ListDataListener) listeners.get(i)).intervalAdded(e);//*/
      } else
        try {
          java.awt.EventQueue.invokeAndWait(new FireIntervalAdded(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireIntervalAdded from '"+selectSql+"'", ex);;
        }
    }
  }
  
  protected void fireIntervalRemoved(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++)
          ((ListDataListener) listeners.get(i)).intervalRemoved(e);//*/
      } else
        try {
          java.awt.EventQueue.invokeAndWait(new FireIntervalRemoved(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireIntervalRemoved from '"+selectSql+"'", ex);;
        }
    }
  }
  
  protected void fireContentsChanged(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++)
          ((ListDataListener) listeners.get(i)).contentsChanged(e);//*/
      } else
        try {
          java.awt.EventQueue.invokeAndWait(new FireContentsChanged(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireContentsChanged from '"+selectSql+"'", ex);;
        }
    }
  }
  
  public synchronized void removeActionListener(ActionListener l) {
    if (actionListeners != null && actionListeners.contains(l)) {
      actionListeners.removeElement(l);
    }
  }
  
  public synchronized void addActionListener(ActionListener l) {
    WeakListenerList v = actionListeners == null ? new WeakListenerList(2) : actionListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      actionListeners = v;
    }
  }
  
  protected void fireActionPerformed(ActionEvent e) {
    if (actionListeners != null) {
      java.util.List listeners = actionListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ActionListener) listeners.get(i)).actionPerformed(e);
    }
  }
  
  public synchronized void removeStoreUpdatesListener(StoreUpdatesListener l) {
    if (storeUpdatesListeners != null && storeUpdatesListeners.contains(l)) {
      storeUpdatesListeners.removeElement(l);
    }
  }
  
  public synchronized void addStoreUpdatesListener(StoreUpdatesListener l) {
    WeakListenerList v = storeUpdatesListeners == null ? new WeakListenerList(2) : storeUpdatesListeners;
    if (!v.contains(l)) {
      v.addElement(l);
      storeUpdatesListeners = v;
    }
  }
  
  protected void fireStoreUpdates(StoreUpdatesEvent e) throws SQLException {
    if (storeUpdatesListeners != null) {
      java.util.List listeners = storeUpdatesListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((StoreUpdatesListener) listeners.get(i)).storeUpdates(e);
    }
  }
  
  protected void fireDeleteRow(StoreUpdatesEvent e) throws SQLException {
    if (storeUpdatesListeners != null) {
      java.util.List listeners = storeUpdatesListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((StoreUpdatesListener) listeners.get(i)).deleteRow(e);
    }
  }
  
  public synchronized void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (activeRowChangeListeners != null && activeRowChangeListeners.contains(l)) {
      activeRowChangeListeners.removeElement(l);
    }
  }
  
  public synchronized void addActiveRowChangeListener(ActiveRowChangeListener l) {
    WeakListenerList v = activeRowChangeListeners == null ? new WeakListenerList(2) : activeRowChangeListeners;
    if (l instanceof ConcurrentEvent && !(l instanceof DataSourceActiveRowChangeEvent))
      l = new DataSourceActiveRowChangeEvent(this,l);
    if (!v.contains(l)) {
      v.addElement(l);
      activeRowChangeListeners = v;
    }
  }
  
  protected void fireActiveRowChange(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      if (java.awt.EventQueue.isDispatchThread()) {
        java.util.List listeners = activeRowChangeListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++)
          ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
      } else
        try {
          java.awt.EventQueue.invokeAndWait(new FireActiveRowChanged(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireActiveRowChange from '"+selectSql+"'", ex);;
        }
    }
  }
  
  protected void fireFieldValueChanged(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      java.util.List listeners = activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
    }
    
  }
  
  public long getQueuedDelay() {
    return queuedDelay;
  }
  
  public void setQueuedDelay(long queuedDelay) {
    this.queuedDelay = queuedDelay;
  }
  
  private void setName() {
    Matcher matcher = namePattern.matcher(selectSql);
    String name = selectSql;
    
    if (matcher.matches()) {
      name = matcher.group(1);
    }
    this.name = name;
  }
  
  public void filterChanged() throws SQLException {
    available.lock();
    try {
      setSelectSql(this.selectSql);
      setCountSql(this.countSql);
    } finally {
      available.unlock();
    }
  }
  
  public void setUpdateTableName(String updateTableName) {
    this.updateTableName = updateTableName;
    
  }
  
  
  
  
  public String getUpdateTableName() {
    return updateTableName;
  }
  
  public void setSelectSql(String selectSql) throws SQLException {
    String oldvalue = this.selectSql;
    try {
      semaphore.acquire();
      this.selectSql = selectSql;
      String sql = substParameters(selectSql, parameters);
      if (sql!=null && sql.length()>0 && getConnection()!=null) {
        if ( (this.selectStatement == null) || (!sql.equals(preparedSelectSql))) {
          if (this.selectStatement!=null)
            this.selectStatement.close();
          this.selectStatement = getConnection().prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY,
                  ResultSet.HOLD_CURSORS_OVER_COMMIT);
          this.selectStatement.setFetchSize(1008);
          preparedSelectSql = sql;
          this.metaData = null;
          this.columnMapping.clear();
          
          this.metaData = selectStatement.getMetaData();
          int columnCount = this.metaData!=null?this.metaData.getColumnCount():0;
          for (int c=1; c<=columnCount; c++)
            this.columnMapping.put(this.metaData.getColumnName(c), c);
          primaryKeys=PrimaryKey.getPrimaryKeys(this.selectStatement);
          
          setName();
          Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Successfully prepared the selectSql '"+sql+"'");
        }
      } else {
        this.selectStatement = null;
      }
      this.count = -1;
      if (this.selectResultSet!=null)
        this.selectResultSet.close();
      this.selectResultSet = null;
    } catch (InterruptedException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Interrupted while preparing '"+selectSql+"'", ex);
    } finally {
      semaphore.release();
      if (countSql==null) {
        setCountSql("SELECT COUNT(*) FROM ("+this.selectSql+") c");
      }
    }
    firePropertyChange("selectSql", oldvalue, this.selectSql);
  }
  
  public void setCountSql(String countSql) throws SQLException {
    String oldvalue = this.countSql;
    try {
      semaphore.acquire();
      this.countSql = countSql;
      String sql = substParameters(countSql, parameters);
      if (sql!=null && sql.length()>0 && getConnection()!=null) {
        if ( (this.countStatement == null) || (!sql.equals(preparedCountSql))) {
          if (this.countStatement!=null)
            this.countStatement.close();
          countStatement = getConnection().prepareStatement(sql,
                  ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY,ResultSet.HOLD_CURSORS_OVER_COMMIT);
          countStatement.setFetchSize(1);
          preparedCountSql = sql;
        }
      } else
        countStatement = null;
    } catch (InterruptedException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Interrupted while preparing '"+countSql+"'", ex);;
    } finally {
      semaphore.release();
    }
    firePropertyChange("countSql", oldvalue, this.countSql);
  }
  
  public String getSelectSql() {
    return selectSql;
  }
  
  public String getCountSql() {
    return countSql;
  }
  
  public Connection getConnection() {
    if (this.connection==null)
      return ConnectionManager.getInstance()!=null?ConnectionManager.getInstance().getConnection():null;
    else
      return this.connection;
  }
  
  public void setConnection(Connection connection) throws SQLException {
    this.connection = connection;
    if (selectStatement!=null&&!selectStatement.getConnection().equals(connection)) {
      if (this.selectStatement!=null) {
        this.selectStatement.close();
        this.selectStatement = null;
      }
      setSelectSql(this.selectSql);
    }
  }
  
  public int getRowCount() {
    int newCount = this.count;
    
    if (this.count==-1) {
      available.lock();
      try {
        if (this.count==-1) {
          if (countStatement!=null) {
            ResultSet rs=executeSql(countStatement, parameters);
            if (rs.first())
              newCount = rs.getInt(1);
          } else if (selectStatement!=null) {
            if (selectResultSet==null) {
              try {
                Logger.getLogger(Settings.LOGGER).fine("Executing '"+selectSql+"'");
                selectResultSet = executeSql(selectStatement, parameters);
                selectResultSet.first();
              } catch (SQLException ex) {
                Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a result for '"+selectSql+"'", ex);
                selectResultSet = null;
              }
            }
            
            if (selectResultSet!=null) {
              int row = selectResultSet.getRow();
              selectResultSet.last();
              newCount = selectResultSet.getRow();
              if (row>0)
                selectResultSet.absolute(row);
            }
          }
        }
      } catch (SQLException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get row count from '"+countSql+"'", ex);
        newCount = this.count;
      } finally {
        available.unlock();
        this.count = newCount;
      }
    }
    
    return newCount+(inserting?1:0);
  }
  
  public void lock() {
    available.lock();
  }
  
  public void unlock() {
    available.unlock();
  }
  
  public boolean isDataLoaded() {
    return selectResultSet!=null;
  }
  
  private boolean loadData(boolean reload) {
    return loadData(reload, Integer.MIN_VALUE);
  }
  
  private boolean loadData(boolean reload, int oldRow) {
    boolean reloaded = false;
    available.lock();
    try {
      if (reload) {
        try {
          if (selectResultSet!=null)
            selectResultSet.close();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't properly close the for '"+selectSql+"'", ex);
        }
        selectResultSet=null;
      }
      if ((selectResultSet==null) && selectStatement!=null) {
        try {
          Logger.getLogger(Settings.LOGGER).fine("Executing '"+preparedSelectSql+"'");
          selectResultSet = executeSql(selectStatement, parameters);
          selectResultSet.first();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't get a result for '"+selectSql+"'", ex);
          selectResultSet = null;
        } finally {
          inserting = false;
          count=-1;
          storedUpdates.clear();
          cache.clear();
          reloaded = true;
          getRowCount();
        }
        if (oldRow>0&&getRowCount()>0) {
          try {
            selectResultSet.absolute(Math.min(oldRow,getRowCount()));
          } catch (SQLException ex) {
            Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't change rowset position", ex);
          }
        }
      }
    } finally {
      available.unlock();
      Logger.getLogger(Settings.LOGGER).finer("Permit unlockd '"+selectSql+"'");
    }
    if (reloaded && selectResultSet!=null) {
      if (EventQueue.isDispatchThread()) {
        events.run();
      } else
        try {
          EventQueue.invokeAndWait(events);
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't notify loaddata results from '"+selectSql+"'", ex);;
        }
    }
    return selectResultSet!=null;
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
  
  private String substParameters(String sql, List<?> parameters) {
    if (sql!=null&&sql.length()>0) {
      Object value;
      Integer type;
      
      for (Iterator values = parameters.iterator(); values.hasNext(); ) {
        value = values.next();
        if (value instanceof SubstSqlParameter) {
          type = ((SubstSqlParameter) value).getType();
          if (type.equals(Types.SUBST_ALL)) {
            sql = sql.replaceAll(((SubstSqlParameter) value).getReplace(), ((SubstSqlParameter) value).getValue());
          } else if (type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST)) {
            sql = sql.replaceFirst(((SubstSqlParameter) value).getReplace(), ((SubstSqlParameter) value).getValue());
          }
        }
      }
    }
    return sql;
  }
  
  private int setParameters(PreparedStatement statement, List<?> parameters, int pos, boolean subset) throws SQLException {
    if (!subset)
      statement.clearParameters();
    
    ParameterMetaData metaData = statement.getParameterMetaData();
    int parameterCount = metaData.getParameterCount();
    Object value;
    Integer type;
    
    for (Iterator values = parameters.iterator(); (pos<=parameterCount)&&values.hasNext(); ) {
      value = values.next();
      if (value instanceof SqlParameter) {
        type = ((SqlParameter) value).getType();
        if (!(type.equals(Types.SUBST_ALL) || type.equals(Types.SUBST) || type.equals(Types.SUBST_FIRST))) {
          statement.setObject(pos++, ( (SqlParameter) value).getValue(),
                  ( (SqlParameter) value).getType());
        } else if ((value instanceof SubstSqlParameter) && (((SubstSqlParameter) value).getParameters().size()>0)) {
          pos = setParameters(statement, ((SubstSqlParameter) value).getParameters(), pos, true);
        }
      } else {
        if (value==null)
          statement.setNull(pos, metaData.getParameterType(pos++));
        else
          statement.setObject(pos++, value);
      }
    }
    while ((pos<=parameterCount) && !subset)
      statement.setNull(pos, metaData.getParameterType(pos++));
    
    return pos;
  }
  
  private ResultSet executeSql(PreparedStatement statement, List<?> parameters) throws SQLException {
    ResultSet rs = null;
    try {
      semaphore.acquireUninterruptibly();
      
      setParameters((PreparedStatement) statement, parameters, 1, false);
      
      /*if (((PreparedStatement) statement).execute()) {
        rs = statement.getResultSet();
      }//*/
      rs = ((PreparedStatement) statement).executeQuery();
      
      /*if (rs!=null)
        rs.setFetchSize(27);//*/
    } finally {
      semaphore.release();
    }
    
    
    return rs;
  }
  
  public boolean setParameters(Map<String,Object> parametersMap) {
    parametersMap = parametersMap==null?new HashMap():parametersMap;
    this.parameters.clear();
    for (Iterator<Map.Entry<String,Object>> v=parametersMap.entrySet().iterator(); v.hasNext();)
      this.parameters.add(v.next().getValue());
    return reload();
  }
  
  public boolean setParameters(List<Object> parameters) {
    return setParameters(parameters, true);
  }
  public boolean setParameters(List<Object> parameters, boolean reload) {
    this.parameters.clear();
    if (parameters!=null)
      this.parameters.addAll(parameters);
    
    if (reload)
      return reload();
    else
      return true;
  }
  
  public List<Object> getParameters() {
    return Collections.unmodifiableList(parameters);
  }
  
  public java.util.Map<String, Object> getDefaultValues() {
    return Collections.unmodifiableMap(defaultValues);
  }
  
  public Map<String,Object> addDefaultValues(Map<String,Object> values) {
    Map<String,Object> result = new HashMap<String,Object>(this.defaultValues);
    
    if (values!=null) {
      Map.Entry<String,Object> entry;
      for(Iterator<Map.Entry<String,Object>> i=values.entrySet().iterator(); i.hasNext(); ) {
        entry = i.next();
        this.defaultValues.put(entry.getKey().toUpperCase(), entry.getValue());
      }
    }
    
    return result;
  }
  
  public Map<String,Object> setDefaultValues(Map<String,Object> values) {
    Map<String,Object> result = this.defaultValues;
    this.defaultValues = new HashMap<String,Object>();
    
    if (values!=null) {
      Map.Entry<String,Object> entry;
      for(Iterator<Map.Entry<String,Object>> i=values.entrySet().iterator(); i.hasNext(); ) {
        entry = i.next();
        this.defaultValues.put(entry.getKey().toUpperCase(), entry.getValue());
      }
    }
    
    return result;
  }
  
  public int getColumnCount() throws SQLException {
    return getMetaData().getColumnCount();
  }
  
  public boolean isColumnReadOnly(String columnName) {
    return defaultValues.containsKey(columnName);
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    return getValueAt(rowIndex, getMetaData().getColumnName(columnIndex));
  }
  
  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    return getValueAt(rowIndex, columnName, new String[] {columnName});
  }
  public Object getValueAt(int rowIndex, String columnName, String[] columnNames) throws SQLException {
    if (loadData()) {
      Object result = null;
      columnName = columnName.toUpperCase();
      
      available.lock();
      try {
        if (wasUpdated(rowIndex, columnName))
          result = getStoredValue(rowIndex, columnName, null, Object.class);
        else {
          CacheKey ck = new CacheKey(rowIndex, columnName);
          CacheEntry ce;
          if (cache.containsKey(ck) && ((ce=cache.get(ck))!=null)) {
            result = ce.value;
          } else {
            int oldRow = getOpenSelectResultSet().getRow();
            
            int max = Math.min(rowIndex+getFetchSize(), getRowCount());
            int min = Math.max(max-getFetchSize(), 1);
            selectResultSet.absolute(min);
            String cn;
            Object value;
            for (int row=min; !selectResultSet.isAfterLast() && (row<=max); row++) {
              if (!cache.containsKey(new CacheKey(row, columnName))) {
                for (int c=0; c<columnNames.length; c++) {
                  cn = columnNames[c].toUpperCase();
                  value = selectResultSet.getObject(cn);
                  if (selectResultSet.wasNull())
                    value = null;
                  cache.put(new CacheKey(row, cn), new CacheEntry<String,Object>(this, cn, value));
                  
                  if ((row==rowIndex) && cn.equals(columnName))
                    result = value;
                }
              }
              selectResultSet.next();
            }
            selectResultSet.absolute(oldRow);
          }
        }
      } finally {
        available.unlock();
      }
      return result;
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  public ResultSet getResultSet() throws SQLException {
    java.sql.PreparedStatement resultStatement = getConnection().prepareStatement(preparedSelectSql,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY,
            ResultSet.CLOSE_CURSORS_AT_COMMIT);
    resultStatement.setFetchSize(1008);
    
    return executeSql(resultStatement, parameters);
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
        available.lock();
        try {
          selectResultSet.relative(0);
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "SelectResultSet seems closed. ["+ex.getMessage()+"]");
          selectResultSet = executeSql(selectStatement, parameters);
          selectResultSet.absolute(oldRow);
        } finally {
          available.unlock();
        }
      }
    }
    return selectResultSet;
  }
  
  public String getColumnName(int columnIndex) throws SQLException {
    if (loadData()) {
      return getOpenSelectResultSet().getMetaData().getColumnName(columnIndex);
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }
  
  
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  
  public boolean isReadOnly() {
    return readOnly;
  }
  
  public Map<Integer,Map<String,Object>> getStoredUpdates() {
    return java.util.Collections.unmodifiableMap(storedUpdates);
  }
  
  private void storeUpdate(int columnIndex, Object value) throws SQLException {
    storeUpdate(getMetaData().getColumnName(columnIndex), value);
  }
  
  private void storeUpdate(String columnName, Object value) throws SQLException {
    storeUpdate(columnName, value, true);
  }
  private void storeUpdate(String columnName, Object value, boolean notify) throws SQLException {
    if (getRow()>0 && !readOnly) {
      columnName = columnName.toUpperCase();
      Integer row = new Integer(getRow());
      if (inserting || storedUpdates.containsKey(row) || !Equals.equals(value, getOpenSelectResultSet().getObject(columnName))) {
        Map<String,Object> columnValues;
        if (storedUpdates.containsKey(row))
          columnValues = storedUpdates.get(row);
        else
          columnValues = new HashMap<String,Object>();
        
        columnValues.put(columnName, value);
        storedUpdates.put(row,columnValues);
        
        if (notify)
          fireFieldValueChanged(new ActiveRowChangeEvent(this, columnName, -1));
      }
    }
  }
  
  private boolean compareValues(ResultSet resultSet, Map<Integer,Object> values) throws SQLException {
    boolean equals = false;
    if (primaryKeys.size()==1) {
      equals = primaryKeys.get(0).compareValues(resultSet, values);
    } else if (uniqueID!=null && uniqueID.length>0) {
      equals = true;
      for (String column:uniqueID) {
        Integer c = new Integer(columnMapping.checkedGet(column).intValue());
        
        if (values.containsKey(c) && values.get(c)!=null)
          equals = equals && com.openitech.util.Equals.equals(values.get(c),resultSet.getObject(c));
        else {
          resultSet.getObject(c);
          equals = equals && resultSet.wasNull();
        }
        if (!equals)
          break;
      }
    } else {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      equals = columnCount==values.size();
      for (int c=1; c<=columnCount && equals; c++)
        if (values.containsKey(c) && values.get(c)!=null)
          equals = equals && com.openitech.util.Equals.equals(values.get(c),resultSet.getObject(c));
        else {
          resultSet.getObject(c);
          equals = equals && resultSet.wasNull();
        }
    }
    return equals;
  }
  
  public boolean hasChanged(int columnIndex) throws SQLException {
    if (wasUpdated(columnIndex))
      return !com.openitech.util.Equals.equals(getOpenSelectResultSet().getObject(columnIndex), getObject(columnIndex));
    else
      return false;
  }
  
  public boolean hasChanged(String columnName) throws SQLException {
    if (wasUpdated(columnName))
      return !com.openitech.util.Equals.equals(getOpenSelectResultSet().getObject(columnName), getObject(columnName));
    else
      return false;
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
  
  private boolean wasUpdated(int row,String columnName) throws SQLException {
    boolean result = false;
    Integer r = new Integer(row);
    if (storedUpdates.containsKey(r))
      result =  storedUpdates.get(r).containsKey(columnName.toUpperCase());
    
    if (!result)
      storedResult[0] = false;
    
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
        if (storedResult[1] = (result == null))
          result = nullValue;
        else if (type.equals(String.class))
          result = result.toString();
        
        return (T) result;
      }
    }
    
    if (row==0) {
      storedResult[0] = true;
    } else {
      storedResult[0] = false;
      
      int oldrow = selectResultSet.getRow();
      
      if (oldrow!=row) {
        getOpenSelectResultSet();
        selectResultSet.absolute(row);
      }
      
      if (String.class.isAssignableFrom(type)) {
        result = selectResultSet.getString(columnName);
      } else if (Number.class.isAssignableFrom(type)) {
        if (Integer.class.isAssignableFrom(type))
          result = selectResultSet.getInt(columnName);
        else if (Short.class.isAssignableFrom(type))
          result = selectResultSet.getShort(columnName);
        else if (Double.class.isAssignableFrom(type))
          result = selectResultSet.getDouble(columnName);
        else if (Byte.class.isAssignableFrom(type))
          result = selectResultSet.getByte(columnName);
        else if (Float.class.isAssignableFrom(type))
          result = selectResultSet.getFloat(columnName);
        else if (Long.class.isAssignableFrom(type))
          result = selectResultSet.getLong(columnName);
        else
          result = selectResultSet.getBigDecimal(columnName);
      } else if (Boolean.class.isAssignableFrom(type))
        result = selectResultSet.getBoolean(columnName);
      else if (Date.class.isAssignableFrom(type)) {
        if (Time.class.isAssignableFrom(type))
          result = selectResultSet.getTime(columnName);
        else if (Timestamp.class.isAssignableFrom(type))
          result = selectResultSet.getTimestamp(columnName);
        else
          result = selectResultSet.getDate(columnName);
      } else if (nullValue instanceof byte[]) {
        result = selectResultSet.getBytes(columnName);
      } else
        result = selectResultSet.getObject(columnName);
      
      if (oldrow!=row) {
        selectResultSet.absolute(oldrow);
      }
    }
    
    return result==null?nullValue:(T) result;
  }
  
  private void storeUpdates(boolean insert) throws SQLException {
    if (isDataLoaded()) {
      Integer row = new Integer(getRow());
      Map<String,Object> columnValues = storedUpdates.get(row);
      Map.Entry<String,Object> entry;
      Map<Integer,Object> oldValues = new HashMap<Integer,Object> ();
      
      if (insert) {
        for (Iterator<Map.Entry<String,Object>> i=defaultValues.entrySet().iterator();i.hasNext();) {
          entry = i.next();
          columnValues.put(entry.getKey(), entry.getValue());
        }
      }
      
      if (storedUpdates.containsKey(row)) {
        if (isUpdateRowFireOnly()) {
          lock();
          boolean readOnly = isReadOnly();
          setReadOnly(true);
          try {
            for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
              entry = i.next();
              oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(),entry.getValue());
            }
            if (uniqueID!=null)
              for (String column:uniqueID) {
                oldValues.put(columnMapping.checkedGet(column).intValue(),getObject(column));
              }
            fireStoreUpdates(new StoreUpdatesEvent(this,row,insert, columnValues));
          } finally {
            unlock();
            setReadOnly(readOnly);
          }
        } else {
          Logger.getLogger(Settings.LOGGER).entering(this.getClass().toString(),"storeUpdates", insert);
          Scale scaledValue;
          
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
            
            for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
              entry = i.next();
              columnIndex = columnMapping.checkedGet(entry.getKey()).intValue();
              if (!isSingleTableSelect()) {
                if (schemaName==null)
                  schemaName=metaData.getSchemaName(columnIndex);
                else
                  if (!schemaName.equalsIgnoreCase(metaData.getSchemaName(columnIndex)))
                    throw new SQLException("Insert on different schemas not supported.");
                if (tableName==null)
                  tableName=metaData.getTableName(columnIndex);
                else if (!tableName.equalsIgnoreCase(metaData.getTableName(columnIndex))) {
                  if (updateTableName==null)
                    throw new SQLException("Insert on different tables not supported.");
                  else if (!updateColumnNames.contains(entry.getKey())) {
                    skipValues.add(entry.getKey());
                    continue;
                  }
                }
              }
              if (entry.getValue()!=null || metaData.isNullable(columnIndex)!=ResultSetMetaData.columnNoNulls) {
                columns.append(columns.length()>0?",":"").append(delimiterLeft).append(entry.getKey()).append(delimiterRight);
                values.append(values.length()>0?",":"").append("?");
              } else {
                skipValues.add(entry.getKey());
                Logger.getLogger(Settings.LOGGER).info("Skipping null value: '"+entry.getKey()+"'");
              }
            }
            
            StringBuffer sql = new StringBuffer();
            
            sql.append("INSERT INTO ");
            if (schemaName.length()>0) {
              sql.append(delimiterLeft).append(schemaName).append(delimiterRight).append(".");
            }
            sql.append(delimiterLeft).append(tableName).append(delimiterRight)
               .append(" (").append(columns).append(") ");
            sql.append("VALUES (").append(values).append(")");
    
            PreparedStatement insertStatement = getConnection().prepareStatement(sql.toString());
            try {
              ParameterMetaData parameterMetaData = insertStatement.getParameterMetaData();
              
              int p=1;
              
              for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
                entry = i.next();
                if (skipValues.indexOf(entry.getKey())==-1) {
                /*if (entry.getValue()==null)
                  insertStatement.setNull(p, parameterMetaData.getParameterType(p++));
                else//*/
                  insertStatement.setObject(p++, entry.getValue());
                  oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(),entry.getValue());
                }
              }
              Logger.getLogger(Settings.LOGGER).info("Executing insert : '"+sql+"'");
              insertStatement.executeUpdate();
            } finally {
              insertStatement.close();
            }
          } else {
            ResultSetMetaData metaData = selectResultSet.getMetaData();
            List<String> skipColumns = new ArrayList<String>();
            for (int c=1; c<=columnCount; c++)
              if (updateTableName==null||updateColumnNames.contains(metaData.getColumnName(c))||(updateTableName!=null&&updateTableName.equalsIgnoreCase(metaData.getTableName(c)))) {
              try {
                Object value = selectResultSet.getObject(c);
                oldValues.put(c,value);
              } catch (Exception err) {
                Logger.getLogger(Settings.LOGGER).info("Skipping illegal value for: '"+metaData.getColumnName(c)+"'");
                skipColumns.add(metaData.getColumnName(c));
              }
              } else {
              skipColumns.add(metaData.getColumnName(c));
              }
            
            PrimaryKey key;
            for (Iterator<PrimaryKey> pk=primaryKeys.iterator(); pk.hasNext(); ) {
              key = pk.next();
              ResultSet updateResultSet = key.getUpdateResultSet(getOpenSelectResultSet());
              
              if (updateResultSet!=null) {
                try {
                  for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
                    entry = i.next();
                    if (skipColumns.indexOf(entry.getKey())==-1) {
                      if (key.isUpdateColumn(entry.getKey())) {
                        if (entry.getValue() instanceof Scale) {
                          scaledValue = (Scale) entry.getValue();
                          if (scaledValue.method.equals("updateAsciiStream") )
                            updateResultSet.updateAsciiStream(entry.getKey(), (InputStream) scaledValue.x, scaledValue.scale);
                          else if (scaledValue.method.equals("updateBinaryStream") )
                            updateResultSet.updateBinaryStream(entry.getKey(), (InputStream) scaledValue.x, scaledValue.scale);
                          else if (scaledValue.method.equals("updateCharacterStream") )
                            updateResultSet.updateCharacterStream(entry.getKey(), (Reader) scaledValue.x, scaledValue.scale);
                          else if (scaledValue.method.equals("updateObject") )
                            updateResultSet.updateObject(entry.getKey(), scaledValue.x, scaledValue.scale);
                        } else if (metaData.getColumnType(columnMapping.checkedGet(entry.getKey()).intValue()) == java.sql.Types.DATE) {
                          if (entry.getValue() instanceof java.util.Date)
                            updateResultSet.updateDate(entry.getKey(), new java.sql.Date(((java.util.Date) entry.getValue()).getTime()));
                          else if (entry.getValue()==null)
                            updateResultSet.updateObject(entry.getKey(), entry.getValue());
                          else
                            try {
                              updateResultSet.updateDate(entry.getKey(), new java.sql.Date((FormatFactory.DATE_FORMAT.parse(entry.getValue().toString())).getTime()));
                            } catch (ParseException ex) {
                              updateResultSet.updateObject(entry.getKey(), entry.getValue());
                            }
                        } else {
                          updateResultSet.updateObject(entry.getKey(), entry.getValue());
                        }
                        cache.remove(new CacheKey(row.intValue(), entry.getKey()));
                        oldValues.put(columnMapping.checkedGet(entry.getKey()),updateResultSet.getObject(entry.getKey()));
                      }
                    }
                  }
                  
                  updateResultSet.updateRow();
                } finally {
                  updateResultSet.close();
                }
              } else {
                StringBuffer set = new StringBuffer(540);
                for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
                  entry = i.next();
                  if ((skipColumns.indexOf(entry.getKey())==-1)&&
                          (metaData.getTableName(columnMapping.checkedGet(entry.getKey()).intValue()).equalsIgnoreCase(key.table))) {
                    set.append(set.length()>0?", ":"").append(delimiterLeft).append(entry.getKey()).append(delimiterRight).append(" = ?");
                  }
                }
                StringBuffer where = new StringBuffer();
                
                for (String c:key.getColumnNames())
                  where.append(where.length()>0?" AND ":"").append(c).append(" = ? ");
                
                String sql = "UPDATE "+delimiterLeft+key.table+delimiterRight+" SET "+set.toString()+" WHERE "+where.toString();
                
                PreparedStatement updateStatement = getConnection().prepareStatement(sql.toString());
                try {
                  ParameterMetaData parameterMetaData = updateStatement.getParameterMetaData();
                  
                  int p=1;
                  
                  for (Iterator<Map.Entry<String,Object>> i=columnValues.entrySet().iterator();i.hasNext();) {
                    entry = i.next();
                    if (skipColumns.indexOf(entry.getKey())==-1) {
                      if (entry.getValue()==null)
                        updateStatement.setNull(p, parameterMetaData.getParameterType(p++));
                      else
                        updateStatement.setObject(p++, entry.getValue());
                      oldValues.put(columnMapping.checkedGet(entry.getKey()).intValue(),entry.getValue());
                    }
                  }
                  for (String c:key.getColumnNames()) {
                    Object value = selectResultSet.getObject(c);
                    if (value==null)
                      updateStatement.setNull(p, parameterMetaData.getParameterType(p++));
                    else
                      updateStatement.setObject(p++, value);
                    oldValues.put(columnMapping.checkedGet(c).intValue(),value);
                  }
                  Logger.getLogger(Settings.LOGGER).info("Executing update : '"+sql+"'");
                  updateStatement.executeUpdate();
                } finally {
                  updateStatement.close();
                }
              }
            }
          }
        }
        
        storedUpdates.remove(row);
        cache.clear();
        inserting = false;
        
        int selectedrow = selectResultSet.getRow();
        
        if (selectResultSet!=null)
          selectResultSet.close();
        selectResultSet = executeSql(selectStatement, parameters);
        if (selectedrow>0)
          selectResultSet.absolute(selectedrow);
        else
          selectResultSet.first();
        
        if (!compareValues(selectResultSet, oldValues)) {
          selectResultSet.first();
          while (!compareValues(selectResultSet, oldValues)  && !selectResultSet.isLast()) {
            selectResultSet.next();
          }
        }
        
        count=-1; //reset row count
        
        fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        fireActiveRowChange(new ActiveRowChangeEvent(this, selectResultSet.getRow(), -1));
        Logger.getLogger(Settings.LOGGER).exiting(this.getClass().toString(),"storeUpdates", insert);
      }
    } else
      throw new SQLException("Ni pripravljenih podatkov.");
  }

  public boolean isCanAddRows() {
    return canAddRows;
  }
  
  public boolean isCanDeleteRows() {
    return canDeleteRows;
  }
  
  public void setCanAddRows(boolean canAddRows) {
    boolean oldValue = this.canAddRows;
    this.canAddRows = canAddRows;
    firePropertyChange("canAddRows", oldValue, canAddRows);
  }
  
  public void setCanDeleteRows(boolean canDeleteRows) {
    boolean oldValue = this.canDeleteRows;
    this.canDeleteRows = canDeleteRows;
    firePropertyChange("canDeleteRows", oldValue, canDeleteRows);
  }
  
  /**
   * Adds a PropertyChangeListener to the listener list. The listener is
   * registered for all bound properties of this class, including the
   * following:
   * <ul>
   *    <li>this Component's font ("font")</li>
   *    <li>this Component's background color ("background")</li>
   *    <li>this Component's foreground color ("foreground")</li>
   *    <li>this Component's focusability ("focusable")</li>
   *    <li>this Component's focus traversal keys enabled state
   *        ("focusTraversalKeysEnabled")</li>
   *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
   *        ("forwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
   *        ("backwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
   *        ("upCycleFocusTraversalKeys")</li>
   *    <li>this Component's preferred size ("preferredSize")</li>
   *    <li>this Component's minimum size ("minimumSize")</li>
   *    <li>this Component's maximum size ("maximumSize")</li>
   *    <li>this Component's name ("name")</li>
   * </ul>
   * Note that if this <code>Component</code> is inheriting a bound property, then no
   * event will be fired in response to a change in the inherited property.
   * <p>
   * If <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is performed.
   *
   * @param    listener  the property change listener to be added
   *
   * @see #removePropertyChangeListener
   * @see #getPropertyChangeListeners
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public synchronized void addPropertyChangeListener(
          PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(listener);
  }
  
  /**
   * Removes a PropertyChangeListener from the listener list. This method
   * should be used to remove PropertyChangeListeners that were registered
   * for all bound properties of this class.
   * <p>
   * If listener is null, no exception is thrown and no action is performed.
   *
   * @param listener the PropertyChangeListener to be removed
   *
   * @see #addPropertyChangeListener
   * @see #getPropertyChangeListeners
   * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
   */
  public synchronized void removePropertyChangeListener(
          PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(listener);
  }
  
  /**
   * Returns an array of all the property change listeners
   * registered on this component.
   *
   * @return all of this component's <code>PropertyChangeListener</code>s
   *         or an empty array if no property change
   *         listeners are currently registered
   *
   * @see      #addPropertyChangeListener
   * @see      #removePropertyChangeListener
   * @see      #getPropertyChangeListeners(java.lang.String)
   * @see      java.beans.PropertyChangeSupport#getPropertyChangeListeners
   * @since    1.4
   */
  public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  /**
   * Adds a PropertyChangeListener to the listener list for a specific
   * property. The specified property may be user-defined, or one of the
   * following:
   * <ul>
   *    <li>this Component's font ("font")</li>
   *    <li>this Component's background color ("background")</li>
   *    <li>this Component's foreground color ("foreground")</li>
   *    <li>this Component's focusability ("focusable")</li>
   *    <li>this Component's focus traversal keys enabled state
   *        ("focusTraversalKeysEnabled")</li>
   *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
   *        ("forwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
   *        ("backwardFocusTraversalKeys")</li>
   *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
   *        ("upCycleFocusTraversalKeys")</li>
   * </ul>
   * Note that if this <code>Component</code> is inheriting a bound property, then no
   * event will be fired in response to a change in the inherited property.
   * <p>
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName one of the property names listed above
   * @param listener the property change listener to be added
   *
   * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners(java.lang.String)
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   */
  public synchronized void addPropertyChangeListener(
          String propertyName,
          PropertyChangeListener listener) {
    if (listener == null) {
      return;
    }
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }
  
  /**
   * Removes a <code>PropertyChangeListener</code> from the listener
   * list for a specific property. This method should be used to remove
   * <code>PropertyChangeListener</code>s
   * that were registered for a specific bound property.
   * <p>
   * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
   * no exception is thrown and no action is taken.
   *
   * @param propertyName a valid property name
   * @param listener the PropertyChangeListener to be removed
   *
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners(java.lang.String)
   * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public synchronized void removePropertyChangeListener(
          String propertyName,
          PropertyChangeListener listener) {
    if (listener == null || changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(propertyName, listener);
  }
  
  /**
   * Returns an array of all the listeners which have been associated
   * with the named property.
   *
   * @return all of the <code>PropertyChangeListener</code>s associated with
   *         the named property; if no such listeners have been added or
   *         if <code>propertyName</code> is <code>null</code>, an empty
   *         array is returned
   *
   * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
   * @see #getPropertyChangeListeners
   * @since 1.4
   */
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(
          String propertyName) {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners(propertyName);
  }
  
  /**
   * Support for reporting bound property changes for Object properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   */
  protected void firePropertyChange(String propertyName,
          Object oldValue, Object newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null ||
            (oldValue != null && newValue != null && oldValue.equals(newValue))) {
      return;
    }
    if (EventQueue.isDispatchThread())
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    else
      try {
        java.awt.EventQueue.invokeAndWait(new FirePropertyChanged(this, propertyName, oldValue, newValue));
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't firePropertyChange from '"+selectSql+"'", ex);;
      }
  }
  
  /**
   * Support for reporting bound property changes for boolean properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   */
  protected void firePropertyChange(String propertyName,
          boolean oldValue, boolean newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }
  
  /**
   * Support for reporting bound property changes for integer properties.
   * This method can be called when a bound property has changed and it will
   * send the appropriate PropertyChangeEvent to any registered
   * PropertyChangeListeners.
   *
   * @param propertyName the property whose value has changed
   * @param oldValue the property's previous value
   * @param newValue the property's new value
   */
  protected void firePropertyChange(String propertyName,
          int oldValue, int newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a byte)
   * @param newValue the new value of the property (as a byte)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
  }
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a char)
   * @param newValue the new value of the property (as a char)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
  }
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a short)
   * @param newValue the old value of the property (as a short)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
  }
  
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a long)
   * @param newValue the new value of the property (as a long)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
  }
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a float)
   * @param newValue the new value of the property (as a float)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
  }
  
  /**
   * Reports a bound property change.
   *
   * @param propertyName the programmatic name of the property
   *          that was changed
   * @param oldValue the old value of the property (as a double)
   * @param newValue the new value of the property (as a double)
   * @see #firePropertyChange(java.lang.String, java.lang.Object,
   *          java.lang.Object)
   * @since 1.5
   */
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    if (changeSupport == null || oldValue == newValue) {
      return;
    }
    firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
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
  
  private class DbDataSourceHashMap<K,V> extends HashMap<K,V> {
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
      if ((key!=null) && (key instanceof String))
        key = ((String) key).toUpperCase();
      
      if (containsKey(key))
        return  get(key);
      else
        throw new SQLException("DbDataSource for '"+selectSql+"' does not contain '"+key.toString()+"'.");
    }
    
    public V put(K key, V value) {
      if (key instanceof String)
        return super.put((K) (((String) key).toUpperCase()), value);
      else
        return super.put(key,value);
    }
    
  }
  
  private static class PrimaryKey {
    String table = "NULL";
    List<String> columnNames = new ArrayList<String>();
    PreparedStatement delete = null;
    PreparedStatement update = null;
    Map<String,Integer> columnMapping = new HashMap<String,Integer>();
    int hashcode;
    boolean updateFailed = false;
    Connection connection;
    
    public PrimaryKey(Connection connection, String table) throws SQLException {
      this.table = table;
      this.connection = connection;
      hashcode = table.hashCode();
    }
    
    public List<String> getColumnNames() throws SQLException {
      if (this.columnNames.size()==0) {
        DatabaseMetaData metaData = connection.getMetaData();
        try {
          
          ResultSet rs = metaData.getPrimaryKeys(null,null,table);
          
          while (rs.next() && !rs.isAfterLast())
            if (rs.getString("TABLE_NAME").equalsIgnoreCase(table))
              columnNames.add(rs.getString("COLUMN_NAME").toUpperCase());
            else
              throw new SQLException("Couldn't retrieve primary keys for '"+table+"'");
        } catch (SQLException ex) {
          this.columnNames.clear();
          Logger.getLogger(Settings.LOGGER).log(Level.FINE, "Couldn't retrieve primary keys for '"+table+"'");
        }
      }
      return columnNames;
    }
    
    public PreparedStatement getDeleteStatement(ResultSet data) throws SQLException {
      if (delete==null) {
        StringBuffer sql = new StringBuffer();
        
        for (Iterator<String> c=columnNames.iterator();c.hasNext();)
          sql.append(sql.length()>0?" AND ":"").append(c.next()).append("=? ");
        
        sql.insert(0,"DELETE FROM "+table+" WHERE ");
        
        delete =  connection.prepareStatement(sql.toString());
      }
      
      delete.clearParameters();
      int p=1;
      for (Iterator<String> c=getColumnNames().iterator();c.hasNext();) {
        delete.setObject(p++, data.getObject(c.next()));
      }
      
      
      return delete;
    }
    
    public ResultSet getUpdateResultSet(ResultSet data) throws SQLException {
      if (update==null) {
        StringBuffer sql = new StringBuffer();
        
        for (String c:columnNames)
          sql.append(sql.length()>0?" AND ":"").append(c).append("=? ");
        
        sql.insert(0,"SELECT * FROM "+table+" WHERE ");
        sql.append(" FOR UPDATE");
        
        update =  connection.prepareStatement(sql.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        
        this.columnMapping.clear();
        ResultSetMetaData metaData = update.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int c=1; c<=columnCount; c++)
          this.columnMapping.put(metaData.getColumnName(c).toUpperCase(), c);
      }
      
      update.clearParameters();
      int p=1;
      for (String c:columnNames) {
        update.setObject(p++, data.getObject(c));
      }
      ResultSet result = null;
      if (!updateFailed)
        try {
          result = update.executeQuery();
          result.next();
        } catch (SQLException ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.INFO, "The table '"+table+"' can't be updated with through a resulSet");
          updateFailed = true;
        }
      
      return result;
    }
    
    public boolean isUpdateColumn(String columnName) {
      return columnMapping.containsKey(columnName.toUpperCase());
    }
    
    public <K> boolean compareValues(ResultSet resultSet, Map<K,Object> values) throws SQLException {
      boolean equals = values!=null && (values.size()>=getColumnNames().size());
      if (equals) {
        String columnName;
        Integer columnIndex;
        boolean columnValuesScan = false;
        boolean indexed = values.keySet().iterator().next() instanceof Integer;
        boolean[] primarysChecked = new boolean[getColumnNames().size()];
        Arrays.fill(primarysChecked, false);
        for (int  c=0;equals && c<primarysChecked.length; c++) {
          if (!primarysChecked[c]) {
            columnName  = getColumnNames().get(c);
            columnIndex = columnMapping.get(columnName);
            if (indexed&&columnIndex!=null) {
              if (values.containsKey(columnIndex) && values.get(columnIndex)!=null)
                equals = equals && (values.get(columnIndex).equals(resultSet.getObject(columnIndex)));
              else {
                resultSet.getObject(columnIndex);
                equals = equals && resultSet.wasNull();
              }
            } else if (indexed&&!columnValuesScan) {
              columnValuesScan = true;
              ResultSetMetaData metaData = resultSet.getMetaData();
              for (Iterator<Map.Entry<K,Object>> iterator=values.entrySet().iterator();iterator.hasNext()&&equals;) {
                Map.Entry<K,Object> entry = iterator.next();
                columnName = metaData.getColumnName(((Integer) entry.getKey()).intValue());
                int index = getColumnNames().indexOf(columnName);
                if (index>=0) {
                  primarysChecked[index] = true;
                  if (entry.getValue()!=null) {
                    equals = equals && (entry.getValue().equals(resultSet.getObject(columnName)));
                  } else {
                    resultSet.getObject(columnName);
                    equals = equals && resultSet.wasNull();
                  }
                }
              }
            } else {
              if (values.containsKey(columnName) && values.get(columnName)!=null)
                equals = equals && (values.get(columnName).equals(resultSet.getObject(columnName)));
              else {
                resultSet.getObject(columnName);
                equals = equals && resultSet.wasNull();
              }
            }
          }
        }
      }
      return equals;
    }
    
    
    public static List<PrimaryKey> getPrimaryKeys(PreparedStatement statement) throws SQLException {
      List<PrimaryKey> result = new ArrayList<PrimaryKey>();
      ResultSetMetaData metaData = statement.getMetaData();
      if (metaData!=null) {
        int columnCount = metaData.getColumnCount();
        
        List<PrimaryKey> keys = new ArrayList<PrimaryKey>();
        Map<String,String> columnTables = new HashMap<String,String>();
        String table;
        PrimaryKey key;
        
        for (int c=1; c<=columnCount; c++) {
          table = metaData.getTableName(c);
          if (table!=null) {
            columnTables.put(metaData.getColumnName(c).toUpperCase(), table);
            if (keys.indexOf(key=new PrimaryKey(statement.getConnection(), table))<0)
              keys.add(key);
          }
        }
        
        
        for (Iterator<PrimaryKey> pk=keys.iterator(); pk.hasNext();) {
          key = pk.next();
          boolean valid=!key.getColumnNames().isEmpty();
          for (Iterator<String> c=key.getColumnNames().iterator(); valid && c.hasNext();) {
            String columnName=c.next();
            valid=columnTables.containsKey(columnName)&&columnTables.get(columnName).equals(key.table);
          }
          if (valid)
            result.add(key);
        }
      }
      
      return result;
    }
    
    public boolean equals(Object obj) {
      if (obj instanceof PrimaryKey) {
        return Equals.equals(this.table,((PrimaryKey) obj).table);
      } else
        return Equals.equals(this.table,obj);
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
      this.hash = (""+row+"#"+this.columnName).hashCode();
    }
    
    public boolean equals(Object obj) {
      if (obj!=null && (obj instanceof CacheKey)) {
        return (((CacheKey) obj).row==row) && (((CacheKey) obj).columnName.equals(columnName));
      } else
        return super.equals(obj);
    }
    
    public int hashCode() {
      return this.hash;
    }
  }
  
  private static class CacheEntry<K,V> extends SoftReference<DbDataSource> {
    K key;
    V value;
    
    private CacheEntry(DbDataSource referent, K key, V value) {
      super(referent);
      this.key = key;
      this.value = value;
    }
    
    public boolean equals(Object obj) {
      if (obj!=null && (obj instanceof CacheEntry)) {
        if ((key!=null) && (key instanceof Number))
          return ((Number) this.key).doubleValue()==((Number) ((CacheEntry) obj).key).doubleValue();
        else
          return Equals.equals(this.key,((CacheEntry) obj).key);
      } else
        return Equals.equals(this.key,obj);
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
  
  public static class SqlParameter<T> {
    protected int type = Types.NULL;
    protected T value = null;
    private PropertyChangeSupport changeSupport;
    protected boolean automaticReload = false;
    
    public SqlParameter() {
    }
    
    public SqlParameter(int type, T value) {
      this.type = type;
      this.value = value;
    }
    
    public int getType() {
      return type;
    }
    
    public void setType(int type) {
      int oldType = this.type;
      this.type = type;
      firePropertyChange("type", oldType, type);
    }
    
    public T getValue() {
      return value;
    }
    
    public void setValue(T value) {
      T oldValue = this.value;
      this.value = value;
      firePropertyChange("value", oldValue, value);
    }
    
    public boolean isAutomaticReload() {
      return automaticReload;
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     *    <li>this Component's font ("font")</li>
     *    <li>this Component's background color ("background")</li>
     *    <li>this Component's foreground color ("foreground")</li>
     *    <li>this Component's focusability ("focusable")</li>
     *    <li>this Component's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     *    <li>this Component's preferred size ("preferredSize")</li>
     *    <li>this Component's minimum size ("minimumSize")</li>
     *    <li>this Component's maximum size ("maximumSize")</li>
     *    <li>this Component's name ("name")</li>
     * </ul>
     * Note that if this <code>Component</code> is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param    listener  the property change listener to be added
     *
     * @see #removePropertyChangeListener
     * @see #getPropertyChangeListeners
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
      if (listener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a PropertyChangeListener from the listener list. This method
     * should be used to remove PropertyChangeListeners that were registered
     * for all bound properties of this class.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
      if (listener == null || changeSupport == null) {
        return;
      }
      changeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Returns an array of all the property change listeners
     * registered on this component.
     *
     * @return all of this component's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered
     *
     * @see      #addPropertyChangeListener
     * @see      #removePropertyChangeListener
     * @see      #getPropertyChangeListeners(java.lang.String)
     * @see      java.beans.PropertyChangeSupport#getPropertyChangeListeners
     * @since    1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners();
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list for a specific
     * property. The specified property may be user-defined, or one of the
     * following:
     * <ul>
     *    <li>this Component's font ("font")</li>
     *    <li>this Component's background color ("background")</li>
     *    <li>this Component's foreground color ("foreground")</li>
     *    <li>this Component's focusability ("focusable")</li>
     *    <li>this Component's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Component's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Component's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Component's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     * </ul>
     * Note that if this <code>Component</code> is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName one of the property names listed above
     * @param listener the property change listener to be added
     *
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
      if (listener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Removes a <code>PropertyChangeListener</code> from the listener
     * list for a specific property. This method should be used to remove
     * <code>PropertyChangeListener</code>s
     * that were registered for a specific bound property.
     * <p>
     * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName a valid property name
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
      if (listener == null || changeSupport == null) {
        return;
      }
      changeSupport.removePropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @return all of the <code>PropertyChangeListener</code>s associated with
     *         the named property; if no such listeners have been added or
     *         if <code>propertyName</code> is <code>null</code>, an empty
     *         array is returned
     *
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners
     * @since 1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName) {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners(propertyName);
    }
    
    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null ||
              (oldValue != null && newValue != null && oldValue.equals(newValue))) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Support for reporting bound property changes for boolean properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Support for reporting bound property changes for integer properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a byte)
     * @param newValue the new value of the property (as a byte)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Byte(oldValue), new Byte(newValue));
    }
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a char)
     * @param newValue the new value of the property (as a char)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
    }
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a short)
     * @param newValue the old value of the property (as a short)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Short(oldValue), new Short(newValue));
    }
    
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a long)
     * @param newValue the new value of the property (as a long)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
    }
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a float)
     * @param newValue the new value of the property (as a float)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Float(oldValue), new Float(newValue));
    }
    
    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a double)
     * @param newValue the new value of the property (as a double)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
    }
    
  }
  
  public static class SubstSqlParameter extends SqlParameter<String> {
    public static final String ALIAS = "<%table_alias%>";
    protected List<Object> parameters = new ArrayList<Object>();
    protected String replace = "";
    protected String alias = "";
    protected String operator = "";
    
    public SubstSqlParameter() {
      super(Types.SUBST_FIRST, "");
    }
    
    public SubstSqlParameter(String replace) {
      this();
      this.replace = replace;
    }
    
    public String getReplace() {
      return replace;
    }
    
    public void setAlias(String alias) {
      if (!this.alias.equals(alias)) {
        String oldValue = this.alias;
        this.alias = alias;
        firePropertyChange("alias",oldValue, alias);
      }
    }
    
    public String getAlias() {
      return alias;
    }
    
    public String getValue() {
      String value = super.getValue().replaceAll(ALIAS, getAlias().length()>0?getAlias()+".":"");
      return (value.length()>0)&&(operator.length()>0)?" "+operator+" ("+value+") ":value;
    }
    
    public void setOperator(String operator) {
      this.operator = operator;
    }
    
    public String getOperator() {
      return operator;
    }
    
    public void setReplace(String replace) {
      String oldValue = this.replace;
      this.replace = replace;
      firePropertyChange("replace", oldValue, replace);
    }
    
    public java.util.List<Object> getParameters() {
      return Collections.unmodifiableList(parameters);
    }
    
    public void addParameter(Object parameter) {
      int size = parameters.size();
      parameters.add(parameter);
      firePropertyChange("parameters", size, parameters.size());
    }
    
    public void addParameter(int index, Object parameter) {
      int size = parameters.size();
      parameters.add(index, parameter);
      firePropertyChange("parameters", size, parameters.size());
    }
    
    public Object getParameter(int index) {
      return parameters.get(index);
    }
    
    public void setParameter(int index, Object parameter) {
      Object oldValue = parameters.set(index, parameter);
      firePropertyChange("parameters", oldValue, parameter);
    }
    
    public void removeParameter(Object parameter) {
      int size = parameters.size();
      parameters.remove(parameter);
      firePropertyChange("parameters", size, parameters.size());
    }
    
  }
  
  private final static class RunnableEvents implements Runnable {
    DbDataSource owner;
    RunnableEvents(DbDataSource owner) {
      this.owner = owner;
    }
    public void run() {
      Logger.getLogger(Settings.LOGGER).fine("Firing events '"+owner.selectSql+"'");
      owner.fireContentsChanged(new ListDataEvent(owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      int pos=0;
      if (owner.getRowCount()>0) {
        try {
          pos = owner.selectResultSet.getRow();
        } catch (SQLException err) {
          pos = 0;
        }
      }
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, pos, -1));
    }
  };
  
  private final static class FireFieldValueChanged implements Runnable {
    DbDataSource owner;
    ActiveRowChangeEvent e;
    
    FireFieldValueChanged(DbDataSource owner, ActiveRowChangeEvent e) {
      this.owner = owner;
      this.e = e;
    }
    public void run() {
      java.util.List listeners = owner.activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
    }
    
  }
  
  
  private final static class FireActiveRowChanged implements Runnable {
    DbDataSource owner;
    ActiveRowChangeEvent e;
    
    FireActiveRowChanged(DbDataSource owner, ActiveRowChangeEvent e) {
      this.owner = owner;
      this.e = e;
    }
    public void run() {
      java.util.List listeners = owner.activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
    }
  }
  
  private final static class FireIntervalRemoved implements Runnable {
    DbDataSource owner;
    ListDataEvent e;
    
    FireIntervalRemoved(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ListDataListener) listeners.get(i)).intervalRemoved(e);//*/
    }
  }
  
  private final static class FireIntervalAdded implements Runnable {
    DbDataSource owner;
    ListDataEvent e;
    
    FireIntervalAdded(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ListDataListener) listeners.get(i)).intervalAdded(e);//*/
    }
  }
  
  private final static class FireContentsChanged implements Runnable {
    DbDataSource owner;
    ListDataEvent e;
    
    FireContentsChanged(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++)
        ((ListDataListener) listeners.get(i)).contentsChanged(e);//*/
    }
  }
  
  private final static class FirePropertyChanged implements Runnable {
    DbDataSource owner;
    String propertyName;
    Object oldValue;
    Object newValue;
    
    FirePropertyChanged(DbDataSource owner, String propertyName,
            Object oldValue, Object newValue) {
      this.owner = owner;
      this.propertyName = propertyName;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }
    
    public void run() {
      owner.changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
  }
  
  /**
   * Holds value of property singleTableSelect.
   */
  private boolean singleTableSelect;
  
  /**
   * Getter for property singleTableSelect.
   * @return Value of property singleTableSelect.
   */
  public boolean isSingleTableSelect() {
    return this.singleTableSelect;
  }
  
  /**
   * Setter for property singleTableSelect.
   * @param singleTableSelect New value of property singleTableSelect.
   */
  public void setSingleTableSelect(boolean singleTableSelect) {
    this.singleTableSelect = singleTableSelect;
  }
  
  /**
   * Holds value of property updateRowFireOnly.
   */
  private boolean updateRowFireOnly;
  
  /**
   * Getter for property updateRowFireOnly.
   * @return Value of property updateRowFireOnly.
   */
  public boolean isUpdateRowFireOnly() {
    return this.updateRowFireOnly;
  }
  
  /**
   * Setter for property updateRowFireOnly.
   * @param updateRowFireOnly New value of property updateRowFireOnly.
   */
  public void setUpdateRowFireOnly(boolean updateRowFireOnly) {
    this.updateRowFireOnly = updateRowFireOnly;
  }

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
  }

  /**
   * Holds value of property busyLabel.
   */
  private String busyLabel = null;

  /**
   * Getter for property busyLabel.
   * @return Value of property busyLabel.
   */
  public String getBusyLabel() {
    return this.busyLabel;
  }

  /**
   * Setter for property busyLabel.
   * @param busyLabel New value of property busyLabel.
   */
  public void setBusyLabel(String busyLabel) {
    this.busyLabel = busyLabel;
  }

  /**
   * Holds value of property delimiterLeft.
   */
  private String delimiterLeft = null;

  /**
   * Getter for property leftDelimiter.
   * @return Value of property leftDelimiter.
   */
  public String getDelimiterLeft() {
    ConnectionManager cm = ConnectionManager.getInstance();
    if ((this.delimiterLeft==null)&&(cm!=null)) {
      return cm.getProperty(com.openitech.db.DbConnection.DB_DELIMITER_LEFT);
    } else
      return this.delimiterLeft;
  }

  /**
   * Setter for property leftDelimiter.
   * @param leftDelimiter New value of property leftDelimiter.
   */
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
  public String getDelimiterRight() {
    ConnectionManager cm = ConnectionManager.getInstance();
    if ((this.delimiterLeft==null)&&(cm!=null)) {
      return cm.getProperty(com.openitech.db.DbConnection.DB_DELIMITER_RIGHT);
    } else
      return this.delimiterRight;
  }

  /**
   * Setter for property rightDelimiter.
   * @param rightDelimiter New value of property rightDelimiter.
   */
  public void setDelimiterRight(String delimiterRight) {
    this.delimiterRight = delimiterRight;
  }

  /**
   * Holds value of property updateColumnNames.
   */
  private java.util.Set<String> updateColumnNames = new java.util.HashSet<String>();

  /**
   * Getter for property updateFieldNames.
   * @return Value of property updateFieldNames.
   */
  private java.util.Set<String> getUpdateColumnNames() {
    return this.updateColumnNames;
  }
  
  public void addUpdateColumnName(String... fieldNames) {
    for (String fieldName:fieldNames) {
      updateColumnNames.add(fieldName);
      updateColumnNames.add(fieldName.toUpperCase());
    }
  }
  
  public void removeUpdateColumnName(String... fieldNames) {
    for (String fieldName:fieldNames) {
      updateColumnNames.remove(fieldName);
      updateColumnNames.remove(fieldName.toUpperCase());
    }
  }
  
}
