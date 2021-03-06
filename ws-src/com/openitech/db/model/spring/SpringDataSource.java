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
package com.openitech.db.model.spring;

import com.openitech.db.model.*;
import com.openitech.Settings;
import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.sql.pool.proxool.ResultSetProxy;
import com.openitech.awt.OwnerFrame;
import com.openitech.db.model.sql.PendingSqlParameter;
import com.openitech.db.model.sql.SQLDataSource;
import com.sun.rowset.CachedRowSetImpl;
import java.awt.event.ActionEvent;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author domenbasic
 */
public class SpringDataSource extends AbstractDataSourceImpl {

  public static final String SELECT_1 = "SELECT 1";
  private String selectSql;
  private String countSql;
  private String preparedSelectSql;
  private String preparedCountSql;
  private int count = -1;
  private int fetchSize = 54;
  private transient ResultSetMetaData metaData = null;
  private DbDataSourceHashMap<String, Integer> columnMapping = new DbDataSourceHashMap<String, Integer>();
  private final Semaphore semaphore = new Semaphore(1);
  private transient Connection connection = null;
  private boolean selectStatementReady = false;
  private boolean countStatementReady = false;
  private transient CurrentResultSet currentResultSet = null;
  private transient PreparedStatement selectStatement;
  private transient PreparedStatement countStatement;
  /**
   * Holds value of property uniqueID.
   */
  private String[] uniqueID;
  private boolean refreshPending = false;
  protected DbDataSource owner;
  private boolean readOnly = false;
  private boolean fireEvents = false;

  /** Creates a new instance of DbDataSource */
  public SpringDataSource(DbDataSource owner) {
    this.owner = owner;
  }
  /**
   * Holds value of property updateColumnNames.
   */
  private java.util.Set<String> updateColumnNames = new java.util.HashSet<String>();
  private java.util.Set<String> updateColumnNamesCS = new java.util.HashSet<String>(); //case sensitive

  public SpringDataSource(DbDataSource owner, PreparedStatement selectStatement, PreparedStatement countStatement, List<Object> params) {
    this.owner = owner;
    this.selectStatement = selectStatement;
    this.countStatement = countStatement;
    owner.setParameters(params, false);
  }

  protected void createCurrentResultSet() {
    owner.lock();
    try {
      Connection connection = getConnection();
      try {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.FINE, "Executing ''{0}''", preparedSelectSql);
        if (DbDataSource.DUMP_SQL) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "\n##############\n ''{0}''", preparedSelectSql);
        }
        long timer = System.currentTimeMillis();
        currentResultSet = new CurrentResultSet(executeSql(getSelectStatement(preparedSelectSql, connection), owner.getParameters()));
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "{0}:select:{1}ms", new Object[]{owner.getName(), System.currentTimeMillis() - timer});
        if (DbDataSource.DUMP_SQL) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("##############\n");
        }
        currentResultSet.currentResultSet.first();
      } finally {
        if (owner.isConnectOnDemand()) {
          connection.close();
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't get a result from " + owner.getName(), ex);
      currentResultSet = null;
    } finally {
      owner.unlock();
    }

  }

  // <editor-fold defaultstate="collapsed" desc="get* update*">
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
    //TODO float ni natan�en 7.45f se v bazo zapi�e 7.449999809265137
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
  @Override
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
  @Override
  public float getFloat(String columnName) throws SQLException {
    //TODO nenatan�no �e ro�no vnese� v bazo. �e gre pisanje in branje preko programa potem je uredu
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public int findColumn(String columnName) throws SQLException {
    if (loadData()) {
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public Timestamp getTimestamp(String columnName) throws SQLException {
    //TODO ne dela napa�no castanje. O�itno �e ho�em date potem dela
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public Array getArray(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, Array.class);
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public URL getURL(int columnIndex) throws SQLException {
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, URL.class);
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
    if (loadData()) {
      return getStoredValue(getRow(), columnIndex, null, BigDecimal.class);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="move cursor">


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
      return (openSelectResultSet().isFirst() || (getRowCount() == 0));
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
      return (openSelectResultSet().isLast() || (getRowCount() == 0));
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      boolean res = openSelectResultSet.last();
      if (res) {
        if (fireEvents) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
        }
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
  @Override
  public void moveToCurrentRow() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      openSelectResultSet.moveToCurrentRow();
      if (openSelectResultSet.getRow() != oldRow) {
        if (fireEvents) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
        }
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
  @Override
  public void moveToInsertRow() throws SQLException {
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      boolean res = openSelectResultSet.absolute(row);
      if (res) {
        if (fireEvents) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
        }
      }
      return res;
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      boolean res = openSelectResultSet.relative(rows);
      if (res) {
        if (fireEvents) {
          owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
        }
      }
      return res;
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
  @Override
  public boolean first() throws SQLException {
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      boolean res = openSelectResultSet.first();
      if (res) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
      }
      return res;
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();

      openSelectResultSet.beforeFirst();
      if (fireEvents) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
      }
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      int oldRow = openSelectResultSet.getRow();
      openSelectResultSet.afterLast();
      if (fireEvents) {
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
      return openSelectResultSet().isAfterLast();
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
      return openSelectResultSet().isBeforeFirst();
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      //TODO ali je to prav da ne gre na before first?
      if (!isFirst()) {
        final ResultSet openSelectResultSet = openSelectResultSet();
        int oldRow = openSelectResultSet.getRow();
        boolean res = openSelectResultSet.previous();
        if (res) {
          if (fireEvents) {
            owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
          }
        }
        return res;
      } else {
        return false;
      }
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }
  // </editor-fold>


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
      return openSelectResultSet().wasNull();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  //TODO a se lahko spremeni metodo v askToSaveChanges()? Bolj logi�no mi je
  public boolean askToSaveChanges() {
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
    } else if ((this.metaData = openSelectResultSet().getMetaData()) != null) {
      int columnCount = this.metaData != null ? this.metaData.getColumnCount() : 0;
      for (int c = 1; c <= columnCount; c++) {
        this.columnMapping.put(this.metaData.getColumnName(c), c);
      }
      return this.metaData;
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }


  protected PreparedStatement getSelectStatement(String sql, Connection connection) throws SQLException {
    //TODO ni logi�na koda. Ne uprablja se owner.isCacheStatements()
    if (this.selectStatement != null) {
      return this.selectStatement;
    } else {
      PreparedStatement selectStatement = null;
      if (sql != null && sql.length() > 0 && connection != null) {

        selectStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        selectStatement.setFetchSize(1008);
        this.selectStatement = selectStatement;
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Successfully prepared the selectSql.");

      }
      return selectStatement;
    }
  }

  protected PreparedStatement getCountStatement(String sql, Connection connection) throws SQLException {
    if (this.countStatement != null) {
      return this.countStatement;
    } else {
      PreparedStatement countStatement = null;

      if (sql != null && sql.length() > 0 && connection != null) {

        countStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        countStatement.setFetchSize(1);
        this.countStatement = countStatement;
      }

      return countStatement;
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
  @Override
  public void insertRow() throws SQLException {
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
  }

  @Override
  public void doDeleteRow() throws SQLException {
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
    if (isDataLoaded()) {

      currentResultSet.close();

      currentResultSet = null;
      if (fireEvents) {
        owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, -1, -1));
      }
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
  @Override
  public void cancelRowUpdates() throws SQLException {
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
    if (loadData()) {
      return openSelectResultSet().getRow();
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
    return getSelectStatement(preparedSelectSql, getTxConnection());
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
    if (loadData()) {
      if (rowUpdated()) {
        if (askToSaveChanges()) {
          updateRow();
        } else {
          cancelRowUpdates();
        }
      }
      if (!isLast()) {
        final ResultSet openSelectResultSet = openSelectResultSet();
        int oldRow = openSelectResultSet.getRow();
        boolean res = openSelectResultSet.next();
        if (res) {
          if (fireEvents) {
            owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, openSelectResultSet.getRow(), oldRow));
          }
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
  @Override
  public void refreshRow() throws SQLException {
    if (isDataLoaded()) {
      if (rowUpdated()) {
        cancelRowUpdates();
      }
      final ResultSet openSelectResultSet = openSelectResultSet();
      openSelectResultSet.refreshRow();
      int row = openSelectResultSet.getRow();
      if (fireEvents) {
        owner.fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, row - 1, row - 1));
        owner.fireActiveRowChange(new ActiveRowChangeEvent(owner, row, row));
      }
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

    return false;

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

    return false;

  }

  @Override
  public void startUpdate() throws SQLException {
  }

  @Override
  public void doStoreUpdates(boolean insert, Map<String, Object> columnValues, Map<Integer, Object> oldValues, Integer row) throws SQLException {
  }

  @Override
  public void filterChanged() throws SQLException {
    owner.lock();
    try {
      setSelectSql(this.selectSql, true);
      setCountSql(this.countSql);
    } finally {
      owner.unlock();
    }
  }

  @Override
  public void setUpdateTableName(String updateTableName) {
  }

  @Override
  public String getUpdateTableName() {
    return null;
  }

  @Override
  public void setSelectSql(String selectSql) throws SQLException {
    setSelectSql(selectSql, false);
  }

  private void setSelectSql(String selectSql, boolean filterChange) throws SQLException {
    if (selectSql != null) {
      String oldvalue = this.selectSql;
      try {
        semaphore.acquire();
        this.selectSql = selectSql;
        String sql = SQLDataSource.substParameters(selectSql, owner.getParameters());
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).finest(
                "\n################# SELECT SQL #################\n" + sql + "\n################# ########## #################");
        selectStatementReady = false;
        preparedSelectSql = null;

        final Connection connection = getConnection();
        try {
          PreparedStatement selectStatement = getSelectStatement(sql, connection);

          if (selectStatement != null) {
            selectStatementReady = true;
            preparedSelectSql = sql;

            this.metaData = null;
            this.columnMapping.clear();

            this.metaData = selectStatement.getMetaData();
            int columnCount = this.metaData != null ? this.metaData.getColumnCount() : 0;
            for (int c = 1; c <= columnCount; c++) {
              this.columnMapping.put(this.metaData.getColumnName(c), c);
            }


          }
        } finally {
          if (owner.isConnectOnDemand()) {
            connection.close();
          }
        }

        this.count = -1;
        if (currentResultSet != null) {
          currentResultSet.close();
        }
        currentResultSet = null;
      } catch (Exception ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Interrupted while preparing '" + selectSql + "'", ex);
      } finally {
        semaphore.release();
        if (countSql == null) {
          setCountSql("SELECT COUNT(*) FROM (" + this.selectSql + ") c");
        }
      }
      if (fireEvents) {
        owner.firePropertyChange("selectSql", oldvalue, this.selectSql);
      }
    }
  }

  @Override
  public int getColumnIndex(String columnName) throws SQLException {
    throw new UnsupportedOperationException();
  }


  @Override
  public void setCountSql(String countSql) throws SQLException {
    this.countSql = countSql;
  }

  @Override
  public String getSelectSql() {
    return selectSql;
  }

  @Override
  public String getCountSql() {
    return countSql;
  }

  @Override
  public Connection getConnection() {
    if (this.connection == null) {
      if (ConnectionManager.getInstance() != null) {
        if (owner.isConnectOnDemand()) {
          try {
            return ConnectionManager.getInstance().getTemporaryConnection();
          } catch (SQLException ex) {
            Logger.getLogger(SpringDataSource.class.getName()).log(Level.SEVERE, null, ex);
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
  }

  public Connection getTxConnection() {
    return (this.connection == null) ? ConnectionManager.getInstance().getTxConnection() : this.connection;
  }

  @Override
  public void setConnection(Connection connection) throws SQLException {
    if (this.connection != connection) {

      this.connection = connection;

      setSelectSql(this.selectSql);
      if (countSql != null) {
        setCountSql(countSql);
      }
    }
  }

  @Override
  public int getRowCount() {

    int newCount = -1;
    if (!isDataLoaded()) {
      return -1;
    } else {
      ResultSet openSelectResultSet = null;
      try {
        openSelectResultSet = openSelectResultSet();
      } catch (SQLException ex) {
        Logger.getLogger(SpringDataSource.class.getName()).log(Level.SEVERE, null, ex);
      }

      if (SELECT_1.equalsIgnoreCase(preparedCountSql)) {
        newCount = 1;
      } else if ((openSelectResultSet instanceof CachedRowSet)) {
        //TODO ne dela pravilno pri shared results
        newCount = ((CachedRowSet) currentResultSet.currentResultSet).size();

      } else if (owner.lock(false)) {
        try {

          int row = openSelectResultSet.getRow();
          if (openSelectResultSet.last()) {
            newCount = openSelectResultSet.getRow();
          } else {
            newCount = 0;
          }

          if (newCount > 0) {
            openSelectResultSet.absolute(row);
          } else {
            openSelectResultSet.first();
          }
          if (newCount <= 0) {
            if (preparedCountSql != null) {

              Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine("Executing '" + preparedCountSql + "'");
              if (DbDataSource.DUMP_SQL) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("############## count(*) \n" + preparedCountSql);

              }
              Connection connection = getConnection();
              try {
                ResultSet rs = executeSql(getCountStatement(preparedCountSql, connection), owner.getParameters());
                if (DbDataSource.DUMP_SQL) {
                  Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("##############");
                }
                if (rs.first()) {
                  newCount = rs.getInt(1);
                }
              } finally {
                if (owner.isConnectOnDemand()) {
                  connection.close();
                }
              }

            }
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't get row count for " + owner.getName(), ex);
          newCount = this.count;
        } finally {
          owner.unlock();
          this.count = newCount;
        }
      } else {
        return -1;
      }


      return newCount;
    }

  }

  @Override
  public void setProvider(String providerClassName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean fireEvents() {
    return fireEvents;
  }

  @Override
  public DbDataSourceImpl copy(DbDataSource owner) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void loadData(DbDataSourceImpl dataSource, int oldRow) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void destroy() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSource(Object source) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private class CurrentResultSet {

    ResultSet currentResultSet;

    public CurrentResultSet(ResultSet currentResultSet) throws SQLException {
      if ((currentResultSet != null) && (owner.isConnectOnDemand() || owner.isCacheRowSet())) {
        this.currentResultSet = new CachedRowSetImpl();
        ((CachedRowSet) this.currentResultSet).populate(currentResultSet);
      } else {
        this.currentResultSet = currentResultSet;
      }
    }

    private void close() throws SQLException {
      if (currentResultSet instanceof CachedRowSet) {
        ((CachedRowSet) currentResultSet).release();
      } else {
        currentResultSet.close();
      }
    }
  }

  @Override
  public boolean isDataLoaded() {
    return currentResultSet != null;
  }

  private boolean loadData(boolean reload) {
    return loadData(reload, Integer.MIN_VALUE);
  }

  @Override
  public boolean loadData(boolean reload, int oldRow) {
    boolean reloaded = false;
    if (reload) {
      try {
        owner.lock();
        if (currentResultSet != null) {
          currentResultSet.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "Can't properly close the for '" + selectSql + "'", ex);
      } finally {
        currentResultSet = null;
        owner.unlock();
      }
    }
    if (currentResultSet == null && selectStatementReady) {
      owner.lock();
      try {
        if(fireEvents){
        owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.LOAD_DATA));
        }
        createCurrentResultSet();
      } finally {
        count = -1;
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
      if (fireEvents) {
        owner.fireActionPerformed(new ActionEvent(this, 1, DbDataSource.DATA_LOADED));
      }
    }

    return currentResultSet != null;

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

  private ResultSet executeSql(PreparedStatement statement, List<?> parameters) throws SQLException {
    ResultSet rs = null;
    try {
      semaphore.acquireUninterruptibly();

      rs = SQLDataSource.executeQuery(statement, parameters);
    } finally {
      semaphore.release();
    }


    return rs;
  }

  @Override
  public int getColumnCount() throws SQLException {
    return getMetaData().getColumnCount();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) throws SQLException {
    return getValueAt(rowIndex, getMetaData().getColumnName(columnIndex));
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName) throws SQLException {
    String[] columns = this.getValueColumns.toArray(new String[this.getValueColumns.size() + 1]);
    columns[this.getValueColumns.size()] = columnName;
    return getValueAt(rowIndex, columnName, columns);
  }

  @Override
  public Object getValueAt(int rowIndex, String columnName, String... columnNames) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public CachedRowSet getCachedRowSet() throws SQLException {
    CachedRowSet crs = new CachedRowSetImpl();
    if (isDataLoaded()) {
      crs.populate(openSelectResultSet());
    } else {
      crs.populate(getResultSet());
    }
    return crs;
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return openSelectResultSet();
  }

  private ResultSet openSelectResultSet() throws SQLException {
    if (currentResultSet == null) {
      createCurrentResultSet();
    } else {
      if ((currentResultSet.currentResultSet instanceof CachedRowSet) || (currentResultSet.currentResultSet instanceof ResultSetProxy)) {
        return currentResultSet.currentResultSet;
      } else {
        int oldRow = 1;
        boolean check = false;
        try {
          oldRow = currentResultSet.currentResultSet.getRow();
        } catch (Exception ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage());
          check = true;
        }
        if (check) {
          owner.lock();
          try {
            currentResultSet.currentResultSet.relative(0);
          } catch (SQLException ex) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, "SelectResultSet seems closed. [" + ex.getMessage() + "]");
            createCurrentResultSet();
          } finally {
            owner.unlock();
          }
        }
      }
    }

    return currentResultSet.currentResultSet;
  }

  @Override
  public String getColumnName(int columnIndex) throws SQLException {
    if (loadData()) {
      return openSelectResultSet().getMetaData().getColumnName(columnIndex);
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }

  @Override
  public boolean isUpdating() throws SQLException {
    return rowInserted() || rowUpdated();
  }

  @Override
  public boolean isRefreshPending() {
    return this.refreshPending;
  }

  @Override
  public void updateRefreshPending() {
   throw new UnsupportedOperationException();
  }

  @Override
  public Map<Integer, Map<String, Object>> getStoredUpdates() {
    return java.util.Collections.EMPTY_MAP;
  }

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
  }

  @Override
  public boolean hasChanged(int columnIndex) throws SQLException {
    if (wasUpdated(columnIndex)) {
      return !com.openitech.util.Equals.equals(openSelectResultSet().getObject(columnIndex), getObject(columnIndex));
    } else {
      return false;
    }
  }

  @Override
  public boolean hasChanged(String columnName) throws SQLException {
    if (wasUpdated(columnName)) {
      return !com.openitech.util.Equals.equals(openSelectResultSet().getObject(columnName), getObject(columnName));
    } else {
      return false;
    }
  }

  @Override
  public Object getOldValue(int columnIndex) throws SQLException {
    return openSelectResultSet().getObject(columnIndex);
  }

  @Override
  public Object getOldValue(String columnName) throws SQLException {
    return openSelectResultSet().getObject(columnName);
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

  @Override
  public boolean wasUpdated(int row, String columnName) throws SQLException {
    boolean result = false;

    return result;
  }

  private <T> T getStoredValue(int row, int columnIndex, T nullValue, Class<? extends T> type) throws SQLException {
    return getStoredValue(row, getColumnName(columnIndex), nullValue, type);
  }

  private <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException {
    columnName = columnName.toUpperCase();
    Object result = nullValue;
    Integer r = new Integer(row);

    //TODO ne vem �e je to uredu. mogo�e bi bilo potrebno dati napako

      final ResultSet openSelectResultSet = openSelectResultSet();

      int oldrow = openSelectResultSet.getRow();

      if (oldrow != row) {
        openSelectResultSet.absolute(row);
      }

      
      if (String.class.isAssignableFrom(type)) {
        result = openSelectResultSet.getString(columnName);
      } else if (Number.class.isAssignableFrom(type)) {
        if (Integer.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getInt(columnName);
        } else if (Short.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getShort(columnName);
        } else if (Double.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getDouble(columnName);
        } else if (Byte.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getByte(columnName);
        } else if (Float.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getFloat(columnName);
        } else if (Long.class.isAssignableFrom(type)) {
          result = openSelectResultSet.getLong(columnName);
        } else {
          result = openSelectResultSet.getBigDecimal(columnName);
        }
      } else if (Boolean.class.isAssignableFrom(type)) {
        result = openSelectResultSet.getBoolean(columnName);
      } else if (Date.class.isAssignableFrom(type)) {
        java.util.Date value = ((java.util.Date) openSelectResultSet.getObject(columnName));
        if (value != null) {
          if (Time.class.isAssignableFrom(type)) {
            result = new java.sql.Time(value.getTime());
          } else if (Timestamp.class.isAssignableFrom(type)) {
            result = new java.sql.Timestamp(value.getTime());
          } else {
            result = new java.sql.Date(value.getTime());
          }
        }
      } else if (nullValue instanceof byte[]) {
        result = openSelectResultSet.getBytes(columnName);
      } else {
        result = openSelectResultSet.getObject(columnName);
      }

      if (oldrow != row) {
        openSelectResultSet.absolute(oldrow);
      }


    return result == null ? nullValue : (T) result;
  }

  public boolean isPending(String columnName) throws SQLException {
    return isPending(columnName, getRow());
  }

  @Override
  public boolean isPending(String columnName, int row) throws SQLException {
    return false;
  }


  @Override
  public void storeUpdates(boolean insert) throws SQLException {
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
        if (openSelectResultSet() instanceof javax.sql.RowSet) {
          return ((javax.sql.RowSet) openSelectResultSet()).getDataSourceName();
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
      if (openSelectResultSet() instanceof javax.sql.RowSet) {
        ((javax.sql.RowSet) openSelectResultSet()).setDataSourceName(name);
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

    @Override
    public V put(K key, V value) {
      if (key instanceof String) {
        return super.put((K) (((String) key).toUpperCase()), value);
      } else {
        return super.put(key, value);
      }
    }
  }

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
  }

  /**
   * Getter for property uniqueID.
   * @return Value of property uniqueID.
   */
  @Override
  public String[] getUniqueID() {
    return this.uniqueID;
  }

  /**
   * Setter for property uniqueID.
   * @param uniqueID New value of property uniqueID.
   */
  @Override
  public void setUniqueID(String[] uniqueID) {
    this.uniqueID = uniqueID;

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
    if (isDataLoaded()) {
      openSelectResultSet().setFetchDirection(direction);
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
  @Override
  public void setFetchSize(int rows) throws SQLException {
    this.fetchSize = rows;
    if (isDataLoaded()) {
      openSelectResultSet().setFetchSize(rows);
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
    if (loadData()) {
      return openSelectResultSet().getFetchDirection();
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
  @Override
  public String getCursorName() throws SQLException {
    if (loadData()) {
      return openSelectResultSet().getCursorName();
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
  @Override
  public int getConcurrency() throws SQLException {
    if (loadData()) {
      return openSelectResultSet().getConcurrency();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
  }


  @Override
  public void clearSharedResults() {
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
    if (isDataLoaded()) {
      openSelectResultSet().clearWarnings();
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
      return openSelectResultSet().getWarnings();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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
    if (loadData()) {
      return openSelectResultSet().getType();
    } else {
      throw new SQLException("Ni pripravljenih podatkov.");
    }
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

}
