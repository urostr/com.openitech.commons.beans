/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.58 $
 */
package com.openitech.db.model;

import com.openitech.Settings;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.StoreUpdatesEvent;
import com.openitech.db.events.StoreUpdatesListener;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.events.concurrent.ConcurrentEvent;
import com.openitech.events.concurrent.DataSourceActiveRowChangeEvent;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.events.concurrent.DataSourceListDataEvent;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.ref.WeakListenerList;
import com.openitech.db.model.sql.SQLNotificationException;
import com.openitech.events.concurrent.Locking;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import java.sql.NClob;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.RowSet;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public class DbDataSource implements DbNavigatorDataSource, Locking, RowSet {

  public static boolean DUMP_SQL = false;
  /**
   * Pred moveToInsertRow
   */
  public final static String MOVE_TO_INSERT_ROW = "moveToInsertRow";
  public final static String CAN_UPDATE_ROW = "canUpdateRow";
  /**
   * Pred shranjevanjem
   */
  public final static String UPDATE_ROW = "updateRow";
  /**
   * Po moveToInsertRow
   */
  public final static String ROW_INSERTED = "rowInserted";

  /**
   * Po shranjevanju
   */
  public final static String ROW_UPDATED = "rowUpdated";
  public final static String CANCEL_UPDATES = "cancelUpdates";
  public final static String DELETE_ROW = "deleteRow";
  public final static String ROW_DELETED = "rowDeleted";
  public final static String STORE_UPDATES = "storeUpdates";
  public final static String LOAD_DATA = "loadData";
  public final static String DATA_LOADED = "dataLoaded";
  public final static String UPDATING_STARTED = "updatingStarted";
  public final static long DEFAULT_QUEUED_DELAY = 108;
  public final static int SHARING_OFF = 0;
  public final static int SHARING_GLOBAL = 1;
  public final static int SHARING_LOCAL = 2;
  public final static int DISABLE_COUNT_CACHING = 8;
  public final static int SHARING_SELECT_GLOBAL = SHARING_GLOBAL + DISABLE_COUNT_CACHING;
  public final static int SHARING_SELECT_LOCAL = SHARING_LOCAL + DISABLE_COUNT_CACHING;
  private String componentName;
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
  protected final List<Object> parameters = new ArrayList<Object>();
  private Map<String, Object> defaultValues = new HashMap<String, Object>();
  private boolean canAddRows = true;
  private boolean canDeleteRows = true;
  private Pattern namePattern = Pattern.compile(".*from\\W*(\\w*)\\W.*");
  private String name = "";
  private final ReentrantLock available = new ReentrantLock();
  private long queuedDelay = DEFAULT_QUEUED_DELAY;
  private boolean reloadsOnEventQueue = false;
  private boolean cacheStatements = true;
  private boolean cacheRowSet = true;
  private boolean autoInsert = false;
  private boolean connectOnDemand = false;
  private DbDataSourceFactory.DbDataSourceImpl implementation;

  /** Creates a new instance of DbDataSource */
  public DbDataSource() {
    this(null, null);
  }

  public DbDataSource(String selectSql) {
    this(selectSql, null);
  }

  public DbDataSource(String selectSql, String countSql) {
    this(selectSql, countSql, DbDataSourceFactory.getInstance().getDbDataSourceClass());
  }

  public DbDataSource(PreparedStatement selectStatement, PreparedStatement countStatement, List<Object> parameters) {
    implementation = new SQLDataSource(this, selectStatement, countStatement, parameters);
  }

  public DbDataSource(String selectSql, String countSql, Class<? extends DbDataSourceFactory.DbDataSourceImpl> dbDataSourceClass) {
    init(DbDataSourceFactory.getInstance().createDbDataSource(this, dbDataSourceClass), countSql, selectSql);
  }

  private DbDataSource(final String selectSql, final String countSql, final DbDataSourceFactory.DbDataSourceImpl implementation) {
    init(implementation, countSql, selectSql);
  }

  private void init(final DbDataSourceImpl implementation, final String countSql, final String selectSql) throws IllegalArgumentException {
    //TODO ko spreminaniš eno spremenljivko, npr. cacheRowSet, ne veš, kako druge vplivajo. Še vedno se lahko dela cache.
    this.connectOnDemand = ConnectionManager.getInstance().isPooled() && ConnectionManager.getInstance().isConnectOnDemand();
    this.cacheRowSet = ConnectionManager.getInstance().isCacheRowSet();
    this.implementation = implementation;
    try {
      if (countSql != null) {
        setCountSql(countSql);
      }
      if (selectSql != null) {
        setSelectSql(selectSql);
      }
    } catch (SQLException ex) {
      throw (IllegalArgumentException) (new IllegalArgumentException("Failed to create a DbDataSource instance")).initCause(ex);
    }
  }

  protected boolean canExportData = true;

  /**
   * Get the value of canExportData
   *
   * @return the value of canExportData
   */
  public boolean isCanExportData() {
    return canExportData;
  }

  /**
   * Set the value of canExportData
   *
   * @param canExportData new value of canExportData
   */
  public void setCanExportData(boolean canExportData) {
    if (this.canExportData!=canExportData) {
      this.canExportData = canExportData;
      firePropertyChange("canExportData", !canExportData, canExportData);
    }
  }

  /**
   * Get the value of cacheRowSet
   *
   * @return the value of cacheRowSet
   */
  public boolean isCacheRowSet() {
    return cacheRowSet;
  }

  /**
   * Set the value of cacheRowSet
   *
   * @param cacheRowSet new value of cacheRowSet
   */
  public void setCacheRowSet(boolean cacheRowSet) {
    this.cacheRowSet = cacheRowSet;
  }

  /**
   * Get the value of connectOnDemand
   *
   * @return the value of connectOnDemand
   */
  public boolean isConnectOnDemand() {
    return connectOnDemand;
  }

  /**
   * Set the value of connectOnDemand
   *
   * @param connectOnDemand new value of connectOnDemand
   */
  public void setConnectOnDemand(boolean connectOnDemand) {
    this.connectOnDemand = connectOnDemand;
  }

  public void clearSharedResults() {
    implementation.clearSharedResults();
  }
  private int sharing;

  /**
   * Get the value of sharing type (SHARING_OFF = 0, SHARING_GLOBAL = 1, SHARING_LOCAL = 2)
   *
   * @return the value of sharing
   */
  public int getSharing() {
    return sharing;
  }

  /**
   * Set the value of sharing type (SHARING_OFF = 0, SHARING_GLOBAL = 1, SHARING_LOCAL = 2)
   *
   * @param sharing new value of sharing
   */
  public void setSharing(int sharing) {
    this.sharing = sharing;
  }

  /**
   * Get the value of shareResults
   *
   * @return the value of shareResults
   */
  public boolean isShareResults() {
    return sharing > 0;
  }

  /**
   * Set the value of shareResults
   *
   * @param shareResults new value of shareResults
   */
  public void setShareResults(boolean shareResults) {
    sharing = shareResults ? SHARING_GLOBAL : SHARING_OFF;
  }
  private boolean safeMode = true;

  /**
   * Get the value of safeMode
   *
   * @return the value of safeMode
   */
  public boolean isSafeMode() {
    return safeMode;
  }

  /**
   * Set the value of safeMode
   *
   * @param safeMode new value of safeMode
   */
  public void setSafeMode(boolean safeMode) {
    this.safeMode = safeMode;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name == null ? super.toString() : name;
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
    implementation.updateFloat(columnName, x);
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
    implementation.updateFloat(columnIndex, x);
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
  public Date getDate(String columnName, Calendar cal) throws SQLException {
    return implementation.getDate(columnName, cal);
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
    return implementation.getDate(columnIndex, cal);
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
    return implementation.getTime(columnName, cal);
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
    return implementation.getTime(columnIndex, cal);
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
    return implementation.getTimestamp(columnName, cal);
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
    return implementation.getTimestamp(columnIndex, cal);
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
  @Override
  public Object getObject(String columnName, Map<String, Class<?>> map) throws SQLException {
    return implementation.getObject(columnName, map);
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
  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    return implementation.getObject(columnIndex, map);
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
    implementation.updateDate(columnName, x);
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
    implementation.updateShort(columnIndex, x);
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
    implementation.updateBlob(columnName, x);
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
    implementation.updateArray(columnName, x);
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
    implementation.updateDouble(columnIndex, x);
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
    implementation.updateTimestamp(columnName, x);
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
    implementation.updateTime(columnIndex, x);
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
    implementation.updateBigDecimal(columnName, x);
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
    implementation.updateDouble(columnName, x);
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
    implementation.updateNull(columnName);
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
    return implementation.getLong(columnName);
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
    return implementation.getInt(columnName);
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
    return implementation.getFloat(columnName);
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
    return implementation.getDouble(columnName);
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
    return implementation.getDate(columnName);
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
    return implementation.getClob(colName);
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
    return implementation.getCharacterStream(columnName);
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
    return implementation.getBytes(columnName);
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
    return implementation.getAsciiStream(columnName);
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
    return implementation.findColumn(columnName);
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
    return implementation.getBigDecimal(columnName);
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
    return implementation.getBinaryStream(columnName);
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
    return implementation.getBlob(colName);
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
    return implementation.getBoolean(columnName);
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
    return implementation.getByte(columnName);
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
    return implementation.getObject(columnName);
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
    return implementation.getRef(colName);
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
    return implementation.getShort(columnName);
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
    return implementation.getString(columnName);
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
    return implementation.getTime(columnName);
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
    return implementation.getTimestamp(columnName);
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
    return implementation.getURL(columnName);
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
    return implementation.getUnicodeStream(columnName);
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
    implementation.updateBytes(columnName, x);
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
    implementation.updateRef(columnName, x);
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
    implementation.updateNull(columnIndex);
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
    return implementation.getObject(columnIndex);
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
    return implementation.getLong(columnIndex);
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
    return implementation.getInt(columnIndex);
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
    return implementation.getFloat(columnIndex);
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
    return implementation.getDouble(columnIndex);
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
    return implementation.getDate(columnIndex);
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
    return implementation.getClob(i);
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
    return implementation.getCharacterStream(columnIndex);
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
    return implementation.getBytes(columnIndex);
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
    return implementation.getAsciiStream(columnIndex);
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
    return implementation.getArray(columnName);
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
    return implementation.getArray(columnIndex);
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
    return implementation.absolute(row);
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
    return getBigDecimal(columnIndex);
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
    return implementation.getBinaryStream(columnIndex);
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
    return implementation.getBlob(i);
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
    return implementation.getBoolean(columnIndex);
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
    return implementation.getByte(columnIndex);
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
    return implementation.getRef(i);
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
    return implementation.getShort(columnIndex);
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
    return implementation.getString(columnIndex);
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
    return implementation.getTime(columnIndex);
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
    return implementation.getTimestamp(columnIndex);
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
    return implementation.getURL(columnIndex);
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
    return implementation.getUnicodeStream(columnIndex);
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
    return implementation.relative(rows);
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
    implementation.setFetchDirection(direction);
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
    implementation.setFetchSize(rows);
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
    implementation.updateBoolean(columnName, x);
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
    implementation.updateObject(columnName, x);
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
    implementation.updateDate(columnIndex, x);
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
    implementation.updateClob(columnName, x);
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
    implementation.updateTimestamp(columnIndex, x);
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
    implementation.updateByte(columnName, x);
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
    implementation.updateShort(columnName, x);
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
    implementation.updateLong(columnName, x);
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
    implementation.updateRef(columnIndex, x);
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
    implementation.updateArray(columnIndex, x);
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
    implementation.updateBigDecimal(columnIndex, x);
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
    implementation.updateClob(columnIndex, x);
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
    implementation.updateLong(columnIndex, x);
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
    implementation.updateBytes(columnIndex, x);
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
    implementation.updateByte(columnIndex, x);
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
    implementation.updateTime(columnName, x);
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
    implementation.updateBlob(columnIndex, x);
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
    implementation.updateString(columnIndex, x);
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
    implementation.updateObject(columnName, x, scale);
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
    implementation.updateObject(columnIndex, x);
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
    implementation.updateInt(columnName, x);
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
    implementation.updateCharacterStream(columnName, reader, length);
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
    implementation.updateBinaryStream(columnName, x, length);
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
    return implementation.getBigDecimal(columnName, scale);
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
    implementation.updateAsciiStream(columnName, x, length);
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
    implementation.updateBoolean(columnIndex, x);
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
  @Override
  public boolean wasNull() throws SQLException {
    return implementation.wasNull();
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
    implementation.updateString(columnName, x);
  }

  public boolean canUpdateRow() throws SQLException {
    boolean canUpdateRow = true;
    try {
      fireActionPerformed(new ActionEvent(this, 1, DbDataSource.CAN_UPDATE_ROW));
    } catch (Exception err) {
      if ((err instanceof SQLNotificationException)
              || (err.getCause() instanceof SQLNotificationException)) {
        if (err instanceof SQLNotificationException) {
          throw (SQLNotificationException) err;
        } else {
          throw (SQLNotificationException) err.getCause();
        }
      } else {
        Logger.getAnonymousLogger().log(Level.WARNING, "Can't update row", err);
      }
      canUpdateRow = false;
    }

    return canUpdateRow;
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
    if (canUpdateRow()) {
      implementation.updateRow();
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
  @Override
  public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
    implementation.updateObject(columnIndex, x, scale);
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
    implementation.updateInt(columnIndex, x);
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
    implementation.updateCharacterStream(columnIndex, x, length);
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
    implementation.updateBinaryStream(columnIndex, x, length);
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
    return implementation.getMetaData();
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
    return implementation.getFetchSize();
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
    return implementation.getFetchDirection();
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
    return implementation.getCursorName();
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
    return implementation.getConcurrency();
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
    return implementation.first();
  }

  protected void doDeleteRow() throws SQLException {
    implementation.doDeleteRow();
  }

  public static void deleteRow(StoreUpdatesEvent storeUpdatesEvent) throws SQLException {
    storeUpdatesEvent.getSource().doDeleteRow();
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
    if (canDeleteRows) {
      implementation.deleteRow();
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
  @Override
  public void close() throws SQLException {
    implementation.close();
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
    implementation.clearWarnings();
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
    implementation.cancelRowUpdates();
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
    implementation.beforeFirst();
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
    implementation.afterLast();
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
    return getBigDecimal(columnIndex, scale);
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
    return implementation.getRow();
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
    return implementation.getStatement();
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
    return implementation.getType();
  }

  public int getType(int columnIndex) throws SQLException {
    return implementation.getType(columnIndex);
  }

  public int getType(String columnName) throws SQLException {
    return implementation.getType(columnName);
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
    return implementation.getWarnings();
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
    implementation.insertRow();
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
    return implementation.isAfterLast();
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
    return implementation.isBeforeFirst();
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
    return implementation.isFirst();
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
    return implementation.isLast();
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
    return implementation.last();
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
    implementation.moveToCurrentRow();
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
    implementation.moveToInsertRow();
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
    return implementation.next();
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
    return implementation.previous();
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
    implementation.refreshRow();
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
    return implementation.rowDeleted();
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
    return implementation.rowInserted();
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
    return implementation.rowUpdated();
  }

  public void startUpdate() throws SQLException {
    implementation.startUpdate();
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
    implementation.updateAsciiStream(columnIndex, x, length);
  }

  public synchronized void removeListDataListener(ListDataListener l) {
    if (listDataListeners != null && listDataListeners.contains(l)) {
      listDataListeners.removeElement(l);
    }
  }

  public synchronized void addListDataListener(ListDataListener l) {
    WeakListenerList v = listDataListeners == null ? new WeakListenerList(2) : listDataListeners;
    if (l instanceof ConcurrentEvent && !(l instanceof DataSourceListDataEvent)) {
      l = new DataSourceListDataEvent(this, l);
    }
    if (!v.contains(l)) {
      v.addElement(l);
      listDataListeners = v;
//      if (getRowCount() > 0) {
//        l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
//      }
    }
  }

  public void fireIntervalAdded(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread() || !isSafeMode()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
          ((ListDataListener) listeners.get(i)).intervalAdded(e);//*/
        }
      } else {
        try {
          java.awt.EventQueue.invokeAndWait(new FireIntervalAdded(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireIntervalAdded from '" + componentName + "'", ex);
        }
      }
    }
  }

  public void fireIntervalRemoved(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread() || !isSafeMode()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
          ((ListDataListener) listeners.get(i)).intervalRemoved(e);//*/
        }
      } else {
        try {
          java.awt.EventQueue.invokeAndWait(new FireIntervalRemoved(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireIntervalRemoved from '" + componentName + "'", ex);
        }
      }
    }
  }

  public void fireContentsChanged(ListDataEvent e) {
    if (listDataListeners != null) {
      if (java.awt.EventQueue.isDispatchThread() || !isSafeMode()) {
        java.util.List listeners = listDataListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
          ((ListDataListener) listeners.get(i)).contentsChanged(e);//*/
        }
      } else {
        try {
          java.awt.EventQueue.invokeAndWait(new FireContentsChanged(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireContentsChanged from '" + componentName + "'", ex);
        }
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

  public void fireActionPerformed(ActionEvent e) {
    if (actionListeners != null) {
      java.util.List listeners = actionListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActionListener) listeners.get(i)).actionPerformed(e);
      }
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

  public void fireStoreUpdates(StoreUpdatesEvent e) throws SQLException {
    if (storeUpdatesListeners != null) {
      java.util.List listeners = storeUpdatesListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((StoreUpdatesListener) listeners.get(i)).storeUpdates(e);
      }
    }
  }

  public void fireDeleteRow(StoreUpdatesEvent e) throws SQLException {
    if (storeUpdatesListeners != null) {
      java.util.List listeners = storeUpdatesListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((StoreUpdatesListener) listeners.get(i)).deleteRow(e);
      }
    }
  }

  public synchronized void removeActiveRowChangeListener(ActiveRowChangeListener l) {
    if (activeRowChangeListeners != null && activeRowChangeListeners.contains(l)) {
      activeRowChangeListeners.removeElement(l);
    }
  }

  public synchronized void addActiveRowChangeListener(ActiveRowChangeListener l) {
    WeakListenerList v = activeRowChangeListeners == null ? new WeakListenerList(2) : activeRowChangeListeners;
    if (l instanceof ConcurrentEvent && !(l instanceof DataSourceActiveRowChangeEvent)) {
      l = new DataSourceActiveRowChangeEvent(this, l);
    }
    if (!v.contains(l)) {
      v.addElement(l);
      activeRowChangeListeners = v;
    }
  }

  public void fireActiveRowChange(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      if (java.awt.EventQueue.isDispatchThread() || !isSafeMode()) {
        java.util.List listeners = activeRowChangeListeners.elementsList();
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
          ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
        }
      } else {
        try {
          java.awt.EventQueue.invokeAndWait(new FireActiveRowChanged(this, e));
        } catch (Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't fireActiveRowChange from '" + componentName + "'", ex);
          ;
        }
      }
    }
  }

  public void fireFieldValueChanged(ActiveRowChangeEvent e) {
    if (activeRowChangeListeners != null) {
      java.util.List listeners = activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
      }
    }

  }

  public long getQueuedDelay() {
    return queuedDelay;
  }

  public void setQueuedDelay(long queuedDelay) {
    this.queuedDelay = queuedDelay;
  }

  public static void storeUpdates(StoreUpdatesEvent storeUpdatesEvent) throws SQLException {
    Map<String, Object> columnValues = new HashMap<String, Object>();
    columnValues.putAll(storeUpdatesEvent.getColumnValues());

    Map<Integer, Object> oldValues = new HashMap<Integer, Object>();
    oldValues.putAll(storeUpdatesEvent.getOldColumnValues());
    storeUpdatesEvent.getSource().doStoreUpdates(storeUpdatesEvent.isInsert(), columnValues, oldValues, storeUpdatesEvent.getRow());
  }

  protected void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException {
    implementation.doStoreUpdates(insert, columnValues, oldValues, row);
  }

  public void setName() {
    if (componentName == null || componentName.length() == 0) {
      String selectSql = implementation.getSelectSql();
      if (selectSql != null) {
        Matcher matcher = namePattern.matcher(selectSql.toLowerCase().replaceAll("[\\r|\\n]", " "));
        String name = selectSql.substring(0, Math.min(selectSql.length(), 9));

        if (matcher.matches()) {
          name = matcher.group(1);
        }
        this.name = name;
      }
    } else {
      this.name = componentName;
    }
  }

  public void setName(String name) {
    componentName = name;
    setName();
  }

  public void filterChanged() throws SQLException {
    implementation.filterChanged();
  }

  public void setUpdateTableName(String updateTableName) {
    implementation.setUpdateTableName(updateTableName);
  }

  public String getUpdateTableName() {
    return implementation.getUpdateTableName();
  }

  public void setSelectSql(String selectSql) throws SQLException {
    implementation.setSelectSql(selectSql);
  }

  public int getColumnIndex(String columnName) throws SQLException {
    return implementation.getColumnIndex(columnName);
  }

  public void setCacheStatements(boolean cacheStatements) {
    this.cacheStatements = cacheStatements;
  }

  public boolean isCacheStatements() {
    return cacheStatements;
  }

  public void setCountSql(String countSql) throws SQLException {
    implementation.setCountSql(countSql);
  }

  public String getSelectSql() {
    return implementation.getSelectSql();
  }

  public String getCountSql() {
    return implementation.getCountSql();
  }

  public Connection getConnection() {
    return implementation.getConnection();
  }

  public void setConnection(Connection connection) throws SQLException {
    implementation.setConnection(connection);
  }

  @Override
  public int getRowCount() {
    return implementation.getRowCount();
  }

  @Override
  public boolean lock() {
    return lock(true);
  }

  @Override
  public boolean canLock() {
    boolean result = false;
    try {
      result = available.tryLock() || available.tryLock(10L, TimeUnit.MILLISECONDS);
      if (result) {
        available.unlock();
      }
    } catch (InterruptedException ex) {
      //ignore it;
    }
    return result;
  }

  @Override
  public boolean lock(boolean fatal) {
    return lock(fatal, false);
  }

  public boolean lock(boolean fatal, boolean force) {
    long begin = System.currentTimeMillis();
    boolean result = false;
    try {
      if (DUMP_SQL) {
        System.out.println(getName() + ":locking:[" + Thread.currentThread().getName() + "]:" + (available.isHeldByCurrentThread() ? "owner:current:" + available.getHoldCount() : "queued:" + available.getQueueLength()));

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        if (stackTrace.getMethodName().equals("lock")) {
          stackTrace = Thread.currentThread().getStackTrace()[4];
        }
        System.out.println(getName() + ":locking:[" + Thread.currentThread().getName() + "]:" + stackTrace.getClassName() + "." + stackTrace.getMethodName() + ":" + stackTrace.getLineNumber());
      }
      if (force) {
        available.lock();
        result = true;
      } else {
        if (!(result = (available.tryLock() || available.tryLock(3L, TimeUnit.SECONDS)))) {
          if (fatal) {
            throw new IllegalStateException("Can't obtain lock on: " + toString());
          } else {
            Logger.getLogger(DbDataSource.class.getName()).log(Level.WARNING, null, new IllegalStateException("Can't obtain lock on: " + toString()));
          }
        }
      }
    } catch (InterruptedException ex) {
      throw (IllegalStateException) (new IllegalStateException("Can't obtain lock")).initCause(ex);
    }
    if (DUMP_SQL) {
      long end = System.currentTimeMillis();
      System.out.println(getName() + " :locking time: " + (end - begin) + " ms.");
    }
    return result;
  }

  @Override
  public void unlock() {
    available.unlock();
    if (false) {//if (DUMP_SQL) {
      StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
      System.out.println(getName() + ":unlocking:[" + Thread.currentThread().getName() + "]:" + stackTrace.getClassName() + "." + stackTrace.getMethodName() + ":" + stackTrace.getLineNumber());
      System.out.println(getName() + ":unlocking:[" + Thread.currentThread().getName() + "]:" + available.getHoldCount());
    }
  }

  public boolean isReloadsOnEventQueue() {
    return reloadsOnEventQueue;
  }

  public void setReloadsOnEventQueue(boolean reloadsOnEventQueue) {
    this.reloadsOnEventQueue = reloadsOnEventQueue;
  }

  public boolean isSortable() {
    boolean result = false;

    for (Object parameter : parameters) {
      result = (parameter instanceof DbSortable);
      if (result) {
        break;
      }
    }

    return result;
  }

  public DbSortable getSortable() {
    DbSortable result = null;

    for (Object parameter : parameters) {
      if (parameter instanceof DbSortable) {
        result = (DbSortable) parameter;
        break;
      }
    }

    return result;
  }

  @Override
  public boolean isDataLoaded() {
    return implementation.isDataLoaded();
  }

  private boolean loadData(boolean reload) {
    return loadData(reload, Integer.MIN_VALUE);
  }

  private boolean loadData(boolean reload, int oldRow) {
    return implementation.loadData(reload, oldRow);
  }

  @Override
  public boolean loadData() {
    return loadData(false);
  }

  @Override
  public boolean reload() {
    return loadData(true);
  }

  public boolean reload(int oldRow) {
    return loadData(true, oldRow);
  }

  public boolean reload(boolean queued) {
    com.openitech.events.concurrent.RefreshDataSource.timestamp(this);
    if (queued) {
      com.openitech.events.concurrent.DataSourceEvent.submit(new com.openitech.events.concurrent.RefreshDataSource(this, true));
      return true;
    } else {
      return reload();
    }
  }

  public static String substParameters(String sql, List<?> parameters) {
    return SQLDataSource.substParameters(sql, parameters);
  }

  public static ResultSet executeQuery(String selectSQL, List<?> parameters) throws SQLException {
    return SQLDataSource.executeQuery(selectSQL, parameters);
  }

  public static ResultSet executeQuery(PreparedStatement statement, List<?> parameters) throws SQLException {
    return SQLDataSource.executeQuery(statement, parameters);
  }

  public static int executeUpdate(String selectSQL, List<?> parameters) throws SQLException {
    return SQLDataSource.executeUpdate(selectSQL, parameters);
  }

  public static int executeUpdate(PreparedStatement statement, List<?> parameters) throws SQLException {
    return SQLDataSource.executeUpdate(statement, parameters);
  }

  public static boolean execute(String selectSQL, List<?> parameters) throws SQLException {
    return SQLDataSource.execute(selectSQL, parameters);
  }

  public static boolean execute(PreparedStatement statement, List<?> parameters) throws SQLException {
    return SQLDataSource.execute(statement, parameters);
  }

  public boolean setParameters(Map<String, Object> parametersMap) {
    parametersMap = parametersMap == null ? new HashMap() : parametersMap;
    this.parameters.clear();
    for (Iterator<Map.Entry<String, Object>> v = parametersMap.entrySet().iterator(); v.hasNext();) {
      this.parameters.add(v.next().getValue());
    }
    return reload();
  }

  public boolean setParameters(List<Object> parameters) {
    return setParameters(parameters, true);
  }

  public boolean setParameters(List<Object> parameters, boolean reload) {
    this.parameters.clear();
    if (parameters != null) {
      this.parameters.addAll(parameters);
    }

    if (reload) {
      return reload();
    } else {
      return true;
    }
  }

  public List<Object> getParameters() {
    return Collections.unmodifiableList(parameters);
  }

  public java.util.Map<String, Object> getDefaultValues() {
    return Collections.unmodifiableMap(defaultValues);
  }

  public Map<String, Object> addDefaultValues(Map<String, Object> values) {
    Map<String, Object> result = new HashMap<String, Object>(this.defaultValues);

    if (values != null) {
      Map.Entry<String, Object> entry;
      for (Iterator<Map.Entry<String, Object>> i = values.entrySet().iterator(); i.hasNext();) {
        entry = i.next();
        this.defaultValues.put(entry.getKey().toUpperCase(), entry.getValue());
      }
    }

    return result;
  }

  public Map<String, Object> setDefaultValues(Map<String, Object> values) {
    Map<String, Object> result = this.defaultValues;
    this.defaultValues = new HashMap<String, Object>();

    if (values != null) {
      Map.Entry<String, Object> entry;
      for (Iterator<Map.Entry<String, Object>> i = values.entrySet().iterator(); i.hasNext();) {
        entry = i.next();
        this.defaultValues.put(entry.getKey().toUpperCase(), entry.getValue());
      }
    }

    return result;
  }

  public int getColumnCount() throws SQLException {
    return implementation.getColumnCount();
  }

  public boolean isColumnReadOnly(String columnName) {
    return defaultValues.containsKey(columnName);
  }

  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    return implementation.getValueAt(rowIndex, columnIndex);
  }

  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    return implementation.getValueAt(rowIndex, columnName);
  }

  public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException {
    return implementation.getValueAt(rowIndex, columnName, columnNames);
  }


  public CachedRowSet getCachedRowSet() throws SQLException {
    return implementation.getCachedRowSet();
  }

  public ResultSet getResultSet() throws SQLException {
    return implementation.getResultSet();
  }

  public String getColumnName(int columnIndex) throws SQLException {
    return implementation.getColumnName(columnIndex);
  }

  public void setAutoInsert(boolean autoInsert) {
    this.autoInsert = autoInsert;
  }

  public boolean isAutoInsert() {
    return autoInsert;
  }

  public void setReadOnly(boolean readOnly) {
    implementation.setReadOnly(readOnly);
  }

  public boolean isReadOnly() {
    return implementation.isReadOnly();
  }

  public boolean isUpdating() throws SQLException {
    return implementation.isUpdating();
  }

  public void updateRefreshPending() {
    implementation.updateRefreshPending();
  }

  public boolean isSuspended() {
    return DataSourceEvent.isSuspended(this);
  }

  public boolean isRefreshPending() {
    return implementation.isRefreshPending();
  }

  public Map<Integer, Map<String, Object>> getStoredUpdates() {
    return implementation.getStoredUpdates();
  }

  public boolean hasChanged(int columnIndex) throws SQLException {
    return implementation.hasChanged(columnIndex);
  }

  public boolean hasChanged(String columnName) throws SQLException {
    return implementation.hasChanged(columnName);
  }

  public Object getOldValue(int columnIndex) throws SQLException {
    return implementation.getOldValue(columnIndex);
  }

  public Object getOldValue(String columnName) throws SQLException {
    return implementation.getOldValue(columnName);
  }

  public boolean wasUpdated(int columnIndex) throws SQLException {
    return wasUpdated(getRow(), getMetaData().getColumnName(columnIndex));
  }

  public boolean wasUpdated(String columnName) throws SQLException {
    return wasUpdated(getRow(), columnName);
  }

  private boolean wasUpdated(int row, String columnName) throws SQLException {
    return implementation.wasUpdated(row, columnName);
  }

  public boolean isPending(String columnName) throws SQLException {
    return isPending(columnName, getRow());
  }

  public boolean isPending(String columnName, int row) throws SQLException {
    return implementation.isPending(columnName, row);
  }

  public void storeUpdates(boolean insert) throws SQLException {
    implementation.storeUpdates(insert);
  }

  //TODO a ne paše zraven tudi isreadOnly()?
  public boolean isCanAddRows() {
    return canAddRows && !DataSourceEvent.isRefreshing(this);
  }

  public boolean isCanDeleteRows() {
    return canDeleteRows && !DataSourceEvent.isRefreshing(this);
  }

  public void setCanAddRows(boolean canAddRows) {
    boolean oldValue = this.canAddRows;
    this.canAddRows = canAddRows;
    if (implementation.fireEvents()) {
      firePropertyChange("canAddRows", oldValue, canAddRows);
    }
  }

  public void setCanDeleteRows(boolean canDeleteRows) {
    boolean oldValue = this.canDeleteRows;
    this.canDeleteRows = canDeleteRows;
    if (implementation.fireEvents()) {
      firePropertyChange("canDeleteRows", oldValue, canDeleteRows);
    }
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
  public void firePropertyChange(String propertyName,
          Object oldValue, Object newValue) {
    PropertyChangeSupport changeSupport = this.changeSupport;
    if (changeSupport == null || (oldValue != null && newValue != null && oldValue.equals(newValue))) {
      return;
    }
    if (EventQueue.isDispatchThread() || !isSafeMode()) {
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    } else {
      try {
        java.awt.EventQueue.invokeAndWait(new FirePropertyChanged(this, propertyName, oldValue, newValue));
      } catch (Exception ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't firePropertyChange from '" + componentName + "'", ex);
        ;
      }
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
  public void firePropertyChange(String propertyName,
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
  public void firePropertyChange(String propertyName,
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

  @Override
  public String getUrl() throws SQLException {
    return implementation.getUrl();
  }

  @Override
  public void setUrl(String url) throws SQLException {
    implementation.setUrl(url);
  }

  @Override
  public String getDataSourceName() {
    return implementation.getDataSourceName();
  }

  @Override
  public void setDataSourceName(String name) throws SQLException {
    setName(name);
    implementation.setDataSourceName(name);
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
    //TODO zakaj ni veè setNull?
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
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void setProvider(String providerClassName) {
    implementation.setProvider(providerClassName);
  }

  public DbDataSource copy() {
    DbDataSource result = new DbDataSource();

    result.autoInsert = this.autoInsert;
    result.cacheRowSet = this.cacheRowSet;
    result.cacheStatements = this.cacheStatements;
    result.canAddRows = this.canAddRows;
    result.canDeleteRows = this.canDeleteRows;
    result.canExportData = this.canExportData;
    result.name = this.name;
    result.sharing = this.sharing;
    result.safeMode = this.safeMode;

    result.parameters.addAll(this.parameters);
    result.defaultValues.putAll(this.defaultValues);

    result.implementation = implementation.copy(result);

    return result;
  }

  public void loadData(DbDataSource dataSource, int oldRow) {
    implementation.loadData(dataSource.implementation, oldRow);
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

    public com.openitech.db.model.xml.config.SqlParameter getSqlParameter() {
      com.openitech.db.model.xml.config.SqlParameter sqlParameter = new com.openitech.db.model.xml.config.SqlParameter();

      sqlParameter.setType(getType());
      sqlParameter.setValue(getValue());

      return sqlParameter;
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
      if (changeSupport == null || (oldValue != null && newValue != null && oldValue.equals(newValue))) {
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

    public interface Reader {

      public SubstSqlParameter getSubstSqlParameter(String name);
    }
    public static final String ALIAS = "<%table_alias%>";
    protected List<Object> parameters = new ArrayList<Object>();
    protected String replace = "";
    protected String alias = "";
    protected String operator = "";
    private java.util.List<DbDataSource> dataSources = new ArrayList<DbDataSource>();
    private PropertyChangeListener queryChangeListener;

    public SubstSqlParameter() {
      super(Types.SUBST_FIRST, "");
    }

    public SubstSqlParameter(String replace) {
      this();
      this.replace = replace;
    }

    public void reloadDataSources() {
      for (DbDataSource dataSource : dataSources) {
        com.openitech.events.concurrent.RefreshDataSource.timestamp(dataSource);

//        dataSource.lock();
//        try {
        com.openitech.events.concurrent.DataSourceEvent.submit(new com.openitech.events.concurrent.RefreshDataSource(dataSource, true));
//        } finally {
//          dataSource.unlock();
//        }
      }
    }

    public void addDataSource(DbDataSource... dataSources) {
      this.dataSources.addAll(Arrays.asList(dataSources));

      if ((this.dataSources.size() > 0) && (queryChangeListener == null)) {
        addPropertyChangeListener("query", queryChangeListener = new PropertyChangeListener() {

          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            reloadDataSources();
          }
        });
      }
    }

    public void removeDataSource(DbDataSource... dataSources) {
      for (DbDataSource dataSource : dataSources) {
        this.dataSources.remove(dataSource);
      }

      if (this.dataSources.isEmpty() && (queryChangeListener != null)) {
        removePropertyChangeListener("query", queryChangeListener);
        queryChangeListener = null;
      }
    }

    public String getReplace() {
      return replace;
    }

    public void setAlias(String alias) {
      if (!this.alias.equals(alias)) {
        String oldValue = this.alias;
        this.alias = alias;
        firePropertyChange("alias", oldValue, alias);
      }
    }

    public String getAlias() {
      return alias;
    }

    public String getValue() {
      String value = super.getValue().replaceAll(ALIAS, getAlias().length() > 0 ? getAlias() + "." : "");
      return (value.length() > 0) && (operator.length() > 0) ? " " + operator + " (" + value + ") " : value;
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

    public void clearParameters() {
      int size = parameters.size();
      parameters.clear();
      firePropertyChange("parameters", size, 0);
    }

    @Override
    public String toString() {
      return "" + getReplace() + " " + getValue();
    }
  }

  protected final static class FireFieldValueChanged implements Runnable {

    DbDataSource owner;
    ActiveRowChangeEvent e;

    FireFieldValueChanged(DbDataSource owner, ActiveRowChangeEvent e) {
      this.owner = owner;
      this.e = e;
    }

    @Override
    public void run() {
      java.util.List listeners = owner.activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActiveRowChangeListener) listeners.get(i)).fieldValueChanged(e);
      }
    }
  }

  protected final static class FireActiveRowChanged implements Runnable {

    DbDataSource owner;
    ActiveRowChangeEvent e;

    FireActiveRowChanged(DbDataSource owner, ActiveRowChangeEvent e) {
      this.owner = owner;
      this.e = e;
    }

    @Override
    public void run() {
      java.util.List listeners = owner.activeRowChangeListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActiveRowChangeListener) listeners.get(i)).activeRowChanged(e);
      }
    }
  }

  protected final static class FireIntervalRemoved implements Runnable {

    DbDataSource owner;
    ListDataEvent e;

    FireIntervalRemoved(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }

    @Override
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).intervalRemoved(e);//*/
      }
    }
  }

  protected final static class FireIntervalAdded implements Runnable {

    DbDataSource owner;
    ListDataEvent e;

    FireIntervalAdded(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }

    @Override
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).intervalAdded(e);//*/
      }
    }
  }

  protected final static class FireContentsChanged implements Runnable {

    DbDataSource owner;
    ListDataEvent e;

    FireContentsChanged(DbDataSource owner, ListDataEvent e) {
      this.owner = owner;
      this.e = e;
    }

    @Override
    public void run() {
      java.util.List listeners = owner.listDataListeners.elementsList();
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ListDataListener) listeners.get(i)).contentsChanged(e);//*/
      }
    }
  }

  protected final static class FirePropertyChanged implements Runnable {

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

    @Override
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
   * Get the value of catalogName
   *
   * @return the value of catalogName
   */
  public String getCatalogName() {
    return implementation.getCatalogName();
  }

  /**
   * Set the value of catalogName
   *
   * @param catalogName new value of catalogName
   */
  public void setCatalogName(String catalogName) {
    implementation.setCatalogName(catalogName);
  }

  /**
   * Get the value of schemaName
   *
   * @return the value of schemaName
   */
  public String getSchemaName() {
    return implementation.getSchemaName();
  }

  /**
   * Set the value of schemaName
   *
   * @param schemaName new value of schemaName
   */
  public void setSchemaName(String schemaName) {
    implementation.setSchemaName(schemaName);
  }

  /**
   * Getter for property uniqueID.
   * @return Value of property uniqueID.
   */
  public String[] getUniqueID() {
    return implementation.getUniqueID();
  }

  /**
   * Setter for property uniqueID.
   * @param uniqueID New value of property uniqueID.
   */
  public void setUniqueID(String[] uniqueID) {
    implementation.setUniqueID(uniqueID);
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
   * Getter for property leftDelimiter.
   * @return Value of property leftDelimiter.
   */
  public String getDelimiterLeft() {
    return implementation.getDelimiterLeft();
  }

  /**
   * Setter for property leftDelimiter.
   * @param leftDelimiter New value of property leftDelimiter.
   */
  public void setDelimiterLeft(String delimiterLeft) {
    implementation.setDelimiterLeft(delimiterLeft);
  }

  /**
   * Getter for property rightDelimiter.
   * @return Value of property rightDelimiter.
   */
  public String getDelimiterRight() {
    return implementation.getDelimiterRight();
  }

  /**
   * Setter for property rightDelimiter.
   * @param rightDelimiter New value of property rightDelimiter.
   */
  public void setDelimiterRight(String delimiterRight) {
    implementation.setDelimiterRight(delimiterRight);
  }

  /**
   * Getter for property updateFieldNames.
   * @return Value of property updateFieldNames.
   */
  public java.util.Set<String> getUpdateColumnNames() {
    return implementation.getUpdateColumnNames();
  }

  public void addUpdateColumnName(String... fieldNames) {
    implementation.addUpdateColumnName(fieldNames);
  }

  public void removeUpdateColumnName(String... fieldNames) {
    implementation.removeUpdateColumnName(fieldNames);
  }

  /**
   * Getter for property getValueColumns.
   * @return Value of property getValueColumns.
   */
  public String[] getGetValueColumns() {
    return implementation.getGetValueColumns();
  }

  /**
   * Setter for property getValueColumns.
   * @param getValueColumns New value of property getValueColumns.
   */
  public void setGetValueColumns(String[] columns) {
    implementation.setGetValueColumns(columns);
  }
  /**
   * Holds value of property seekUpdatedRow.
   */
  private boolean seekUpdatedRow = true;

  /**
   * Getter for property seekUpdatedRow.
   *
   * @return Value of property seekUpdatedRow.
   */
  public boolean isSeekUpdatedRow() {
    return this.seekUpdatedRow;
  }

  /**
   * Setter for property seekUpdatedRow.
   *
   * @param seekUpdatedRow New value of property seekUpdatedRow.
   */
  public void setSeekUpdatedRow(boolean seekUpdatedRow) {
    this.seekUpdatedRow = seekUpdatedRow;
  }

  public boolean goToLastOnInsert = false;

  /**
   * Get the value of goToLastOnInsert
   *
   * @return the value of goToLastOnInsert
   */
  public boolean isGoToLastOnInsert() {
    return goToLastOnInsert;
  }

  /**
   * Set the value of goToLastOnInsert
   *
   * @param goToLastOnInsert new value of goToLastOnInsert
   */
  public void setGoToLastOnInsert(boolean goToLastOnInsert) {
    this.goToLastOnInsert = goToLastOnInsert;
  }

  public boolean goToFirstOnInsert = false;

  /**
   * Get the value of goToLastOnInsert
   *
   * @return the value of goToLastOnInsert
   */
  public boolean isGoToFirstOnInsert() {
    return goToFirstOnInsert;
  }

  /**
   * Set the value of goToLastOnInsert
   *
   * @param goToLastOnInsert new value of goToLastOnInsert
   */
  public void setGoToFirstOnInsert(boolean goToFirstOnInsert) {
    this.goToFirstOnInsert = goToFirstOnInsert;
  }


  @Override
  public DbDataSource getDataSource() {
    return this;
  }
}
