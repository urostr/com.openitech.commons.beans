/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.jdbc.proxy;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.model.AbstractDataSourceImpl;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.DbDataSourceIndex;
import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.db.model.sql.SQLNotificationException;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.util.Equals;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author domenbasic
 */
public class DataSourceProxy extends AbstractDataSourceImpl {

  private final DbDataSource owner;
  private final DbDataSourceIndex dataSourceIndex;
  private final DbDataSource dataSource;
  private int currentRow = 0;
  private String SELECT_1 = SQLDataSource.SELECT_1;
  private transient ResultSetMetaData metaData = null;
  private DbDataSourceHashMap<String, Integer> columnMapping = new DbDataSourceHashMap<String, Integer>();
  private boolean updating = false;
  private boolean readOnly = false;
  private boolean inserting = false;
  private String selectSql;
  private String countSql;
  private String preparedSelectSql;
  private String preparedCountSql;

  public DataSourceProxy(DbDataSource owner, DbDataSourceIndex dataSourceIndex) {
    this.owner = owner;
    this.dataSourceIndex = dataSourceIndex;
    this.dataSource = dataSourceIndex.getDataSource();
    this.dataSourceIndex.addListDataListener(new ListDataListener() {

      @Override
      public void intervalAdded(ListDataEvent e) {
        loadData(true, currentRow);
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
        loadData(true, currentRow);
      }

      @Override
      public void contentsChanged(ListDataEvent e) {
        Logger.getAnonymousLogger().log(Level.INFO, toString() + "contentsChanged");
        loadData(true, currentRow);
      }
    });
  }

  @Override
  public void clearSharedResults() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void doDeleteRow() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
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
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public int getType(int columnIndex) throws SQLException {
    if (getMetaData() != null) {
      return getMetaData().getColumnType(columnIndex);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
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
  @Override
  public SQLWarning getWarnings() throws SQLException {
    if (isDataLoaded()) {
      return dataSource.getWarnings();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public void startUpdate() throws SQLException {
    if (isDataLoaded()) {
      if ((getRowCount() > 0) && !rowUpdated()) {
        updating = true;
        storedUpdates.put(new Integer(getRow()), new HashMap<String, Object>());
        owner.fireFieldValueChanged(new ActiveRowChangeEvent(owner, "", -1));
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCountSql(String countSql) throws SQLException {
    this.countSql = countSql;
  }

  @Override
  public String getCountSql() {
    return this.countSql;
  }

  @Override
  public Connection getConnection() {
    return dataSource.getConnection();
  }

  @Override
  public void setConnection(Connection connection) throws SQLException {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public String getSelectSql() {
    return dataSource.getSelectSql();
  }

  @Override
  public void setSelectSql(String selectSql) throws SQLException {
    this.selectSql = selectSql;
  }

  @Override
  public String getUpdateTableName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setUpdateTableName(String updateTableName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getColumnIndex(String columnName) throws SQLException {
    if ((columnName != null) && (columnName instanceof String)) {
      columnName = ((String) columnName).toUpperCase();
    }

    if (columnMapping.containsKey(columnName)) {
      return columnMapping.checkedGet(columnName).intValue();
    } else {
      int ci = -1;
      try {
        ci = dataSource.findColumn(columnName);
      } finally {
        if (ci == -1) {
          Logger.getLogger(SQLDataSource.class.getName()).log(Level.WARNING, "Invalid column name [{0}]", columnName);
        }
      }
      columnMapping.put(columnName, ci);
      return ci;
    }
  }

  @Override
  public int getRowCount() {
    return dataSourceIndex.size() + (inserting ? 1 : 0);
  }

  @Override
  public boolean isDataLoaded() {
    return dataSource.isDataLoaded();
  }

  @Override
  public int getColumnCount() throws SQLException {
    return getMetaData().getColumnCount();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getColumnName(int columnIndex) throws SQLException {
    if (isDataLoaded()) {
      return getMetaData().getColumnName(columnIndex);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public boolean isUpdating() throws SQLException {
    return updating;
  }

  @Override
  public void updateRefreshPending() {
    dataSource.updateRefreshPending();
  }

  @Override
  public Map<Integer, Map<String, Object>> getStoredUpdates() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean hasChanged(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean hasChanged(String columnName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getOldValue(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getOldValue(String columnName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean wasUpdated(int row, String columnName) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isPending(String columnName, int row) throws SQLException {
    return false;
  }

  @Override
  public void storeUpdates(boolean insert) throws SQLException {
    storedUpdates.clear();
    updating = false;
    owner.fireContentsChanged(new ListDataEvent(owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, -1));

  }

  @Override
  public String getCatalogName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setCatalogName(String catalogName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getSchemaName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSchemaName(String schemaName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String[] getUniqueID() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setUniqueID(String[] uniqueID) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getDelimiterLeft() {
    return dataSource.getDelimiterLeft();
  }

  @Override
  public void setDelimiterLeft(String delimiterLeft) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getDelimiterRight() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDelimiterRight(String delimiterRight) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Set<String> getUpdateColumnNames() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addUpdateColumnName(String... fieldNames) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void removeUpdateColumnName(String... fieldNames) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String[] getGetValueColumns() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setGetValueColumns(String[] columns) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  @Override
  public boolean isReadOnly() {
    return readOnly || DataSourceEvent.isRefreshing(dataSource);
  }

  @Override
  public boolean isRefreshPending() {
    return dataSource.isRefreshPending();
  }

  @Override
  public void filterChanged() throws SQLException {
    //ignore
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
    if (isDataLoaded()) {
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
  @Override
  public boolean wasNull() throws SQLException {
    if (isDataLoaded()) {
      return storedResult[0] ? storedResult[1] : dataSource.wasNull();
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
      return getStoredValue(getRow(), columnName, null, URL.class);
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
      return getStoredValue(getRow(), columnIndex, null, Array.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return currentRow == 0;
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return currentRow == dataSourceIndex.size() + 1;
  }

  @Override
  public boolean isFirst() throws SQLException {
    return currentRow == 1;
  }

  @Override
  public boolean isLast() throws SQLException {
    return currentRow == dataSourceIndex.size();
  }

  @Override
  public void beforeFirst() throws SQLException {
    int oldRow = currentRow;
    currentRow = 0;
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));
  }

  @Override
  public void afterLast() throws SQLException {
    int oldRow = currentRow;
    currentRow = dataSourceIndex.size() + 1;
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));
  }

  @Override
  public boolean first() throws SQLException {
    return absolute(1);
  }

  @Override
  public boolean last() throws SQLException {
    return absolute(dataSourceIndex.size());
  }

  @Override
  public int getRow() throws SQLException {
    int result = 0;
    if (SELECT_1.equalsIgnoreCase(getCountSql())) {
      result = 1;
    } else {
      result = currentRow;
    }
    return result;
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    final int rowCount = dataSourceIndex.size();
    if (rowCount > 0
            && rowCount >= row
            && row > 0) {
      int oldRow = currentRow;
      currentRow = row;
      owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow, oldRow));
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    return absolute(currentRow + rows);
  }

  @Override
  public boolean previous() throws SQLException {
    return absolute(currentRow - 1);
  }

  @Override
  public boolean next() throws SQLException {
    return absolute(currentRow + 1);
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return updating;
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return false;
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return false;
  }

  @Override
  public void insertRow() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRow() throws SQLException {
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
    }
    try {
      owner.fireActionPerformed(new ActionEvent(owner, 1, DbDataSource.ROW_UPDATED));
    } catch (Exception err) {
      //
    }
  }

  @Override
  public void deleteRow() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void refreshRow() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

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
          updating = false;
          storedUpdates.remove(new Integer(getRow()));

          final int row = currentRow;
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
        }
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    inserting = true;
    updating = true;
    owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, currentRow + 1, currentRow));
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Statement getStatement() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getDataSourceName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDataSourceName(String name) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void close() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
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
    if (isDataLoaded()) {
      return getStoredValue(getRow(), columnIndex, null, URL.class);
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
    if (this.metaData != null) {
      return this.metaData;
    } else if ((this.metaData = dataSource.getMetaData()) != null) {
      int columnCount = this.metaData != null ? this.metaData.getColumnCount() : 0;
      for (int c = 1; c <= columnCount; c++) {
        this.columnMapping.put(this.metaData.getColumnName(c), c);
      }
      return this.metaData;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clearWarnings() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getCursorName() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNull(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean fireEvents() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public CachedRowSet getCachedRowSet() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public DbDataSourceImpl copy(DbDataSource owner) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void destroy() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getFetchDirection() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getFetchSize() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getConcurrency() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateNull(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
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
        throw new SQLException("DbDataSource for '" + getSelectSql() + "' does not contain '" + key.toString() + "'.");
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

  private <T> T getStoredValue(int row, int columnIndex, T nullValue, Class<? extends T> type) throws SQLException {
    return getStoredValue(row, getColumnName(columnIndex), nullValue, type);
  }
  private Map<Integer, Map<String, Object>> storedUpdates = new HashMap<Integer, Map<String, Object>>();
  private boolean[] storedResult = new boolean[]{false, false};

  private <T> T castValue(Object result, T nullValue, Class<? extends T> type) throws SQLException {
    if (result == null) {
      return nullValue;
    } else {
      if (result instanceof java.util.Date) {
        java.util.Date dv = ((java.util.Date) result);
        if (dv != null) {
          if (Time.class.isAssignableFrom(type)) {
            result = new java.sql.Time(dv.getTime());
          } else if (Timestamp.class.isAssignableFrom(type)) {
            result = new java.sql.Timestamp(dv.getTime());
          } else if (java.sql.Date.class.isAssignableFrom(type)) {
            result = new java.sql.Date(dv.getTime());
          } else if (!Object.class.isAssignableFrom(type)) {
            result = new java.sql.Date(dv.getTime());
          }
        }
      }
      if (type.equals(String.class)) {
        if (result instanceof java.sql.Clob) {
          if (((java.sql.Clob) result).length() > 0) {
            result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
          } else {
            result = "";
          }
        } else {
          result = result.toString();
        }
      } else if (result instanceof java.sql.Clob) {
        if (((java.sql.Clob) result).length() > 0) {
          result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
        } else {
          result = "";
        }
      } else if (result instanceof Scale) {
        result = ((Scale) result).x;
      }

      return (T) result;
    }
  }

  private <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException {
    columnName = columnName.toUpperCase();
    Object result = nullValue;
    Integer r = new Integer(row);
    if (storedUpdates.containsKey(r)) {
      if (storedUpdates.get(r).containsKey(columnName)) {
        result = storedUpdates.get(r).get(columnName);
        if (result instanceof java.util.Date) {
          java.util.Date value = ((java.util.Date) result);
          if (value != null) {
            if (Time.class.isAssignableFrom(type)) {
              result = new java.sql.Time(value.getTime());
            } else if (Timestamp.class.isAssignableFrom(type)) {
              result = new java.sql.Timestamp(value.getTime());
            } else if (java.sql.Date.class.isAssignableFrom(type)) {
              result = new java.sql.Date(value.getTime());
            } else if (!Object.class.isAssignableFrom(type)) {
              result = new java.sql.Date(value.getTime());
            }
          }
        }
        storedResult[0] = true;
        if (storedResult[1] = (result == null)) {
          result = nullValue;
        } else if (type.equals(String.class)) {
          if (result instanceof java.sql.Clob) {
            if (((java.sql.Clob) result).length() > 0) {
              result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
            } else {
              result = "";
            }
          } else {
            result = result.toString();
          }
        } else if (result instanceof java.sql.Clob) {
          if (((java.sql.Clob) result).length() > 0) {
            result = ((java.sql.Clob) result).getSubString(1L, (int) ((java.sql.Clob) result).length());
          } else {
            result = "";
          }
        } else if (result instanceof Scale) {
          result = ((Scale) result).x;
        }

        return (T) result;
      }
    }
    //TODO ne vem ?e je to uredu. mogo?e bi bilo potrebno dati napako
    if (row == 0) {
      storedResult[0] = true;
    } else {
      try {
        storedResult[0] = false;
        if ((dataSource.getRow() == 0) && SELECT_1.equalsIgnoreCase(getCountSql())) {
          storedResult[0] = true;
          Logger.getAnonymousLogger().log(Level.INFO, "getStoredValue: row=" + row + " columnName=" + columnName + " result=" + nullValue);
          return nullValue;
        }

//      int oldrow = dataSource.getRow();


        int ds_row = dataSourceIndex.getRowAt(row);
        if (ds_row == -1) {
          result = nullValue;
        } else {
//      if (oldrow != ds_row) {
//        dataSource.absolute(ds_row);
//      }

          result = castValue(dataSource.getValueAt(ds_row, columnName), nullValue, type);
        }
      } catch (Exception ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
      }
    }
    //Thread.dumpStack();
    Logger.getAnonymousLogger().log(Level.INFO, "getStoredValue: row=" + row + " columnName=" + columnName + " result=" + (result == null ? "null" : result.toString()));
    return result == null ? nullValue : (T) result;
  }

  public boolean loadData() {
    return loadData(false);
  }

  private boolean loadData(boolean reload) {
    return loadData(reload, Integer.MIN_VALUE);
  }
  private boolean loading = false;

  @Override
  public boolean loadData(boolean reload, int oldRow) {
    if (!loading) {
      loading = true;
      try {
        owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.LOAD_DATA));
        if (!dataSource.isDataLoaded()) {
          dataSource.loadData();
        }
        if (dataSource.isDataLoaded()) {
          if (oldRow > 0 && getRowCount() > 0) {
            currentRow = Math.min(oldRow, getRowCount());
          }
        }
        owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.DATA_LOADED));
        if (dataSource.isDataLoaded()) {
          if (EventQueue.isDispatchThread() || !owner.isSafeMode()) {
            events.run();
          } else {
            try {
              EventQueue.invokeAndWait(events);
            } catch (Exception ex) {
              Logger.getLogger(DataSourceProxy.class.getName()).log(Level.SEVERE, "Can't notify loaddata results from '" + getSelectSql() + "'", ex);
            }
          }
        }
      } finally {
        loading = false;
      }
    }
    return isDataLoaded();
  }

  @Override
  public void loadData(DbDataSourceImpl dataSource, int oldRow) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  private final Runnable events = new RunnableEvents(this);

  private final static class RunnableEvents implements Runnable {

    DataSourceProxy owner;

    RunnableEvents(DataSourceProxy owner) {
      this.owner = owner;
    }

    @Override
    public void run() {
      Logger.getLogger(DataSourceProxy.class.getName()).fine("Firing events '" + owner.getSelectSql() + "'");
      owner.owner.fireContentsChanged(new ListDataEvent(owner.owner, ListDataEvent.CONTENTS_CHANGED, -1, -1));
      int pos = 0;
      if (owner.isDataLoaded()) {
        pos = owner.currentRow;
      }
      owner.owner.fireActiveRowChange(new ActiveRowChangeEvent(owner.owner, pos, -1));
    }
  };

  private void storeUpdate(int columnIndex, Object value) throws SQLException {
    storeUpdate(getMetaData().getColumnName(columnIndex), value);
  }

  private void storeUpdate(String columnName, Object value) throws SQLException {
    storeUpdate(columnName, value, true);
  }

  private void storeUpdate(String columnName, Object value, boolean notify) throws SQLException {
    storeUpdate(columnName, value, notify, false);
  }

  private void storeUpdate(String columnName, Object value, boolean notify, boolean inserting) throws SQLException {
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

      final Object resultSetValue = dataSource.getObject(columnName);

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
  }
}
