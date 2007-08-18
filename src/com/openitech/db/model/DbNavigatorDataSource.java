/*
 * DbNavigatorDataSource.java
 *
 * Created on Torek, 22 maj 2007, 13:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model;

import com.openitech.db.events.ActiveRowChangeListener;
import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface DbNavigatorDataSource {
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
   * 
   * @exception SQLException if a database access error
   *            occurs or if this method is called when the cursor is
   *            on the insert row
   * @since 1.2
   */
  void cancelRowUpdates() throws SQLException;

  /**
   * Deletes the current row from this <code>ResultSet</code> object
   * and from the underlying database.  This method cannot be called when
   * the cursor is on the insert row.
   * 
   * 
   * @exception SQLException if a database access error occurs
   * or if this method is called when the cursor is on the insert row
   * @since 1.2
   */
  void deleteRow() throws SQLException;

  /**
   * Moves the cursor to the first row in
   * this <code>ResultSet</code> object.
   * 
   * 
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  boolean first() throws SQLException;

  boolean isCanAddRows();

  boolean isCanDeleteRows();

  /**
   * Retrieves whether the cursor is on the first row of
   * this <code>ResultSet</code> object.
   * 
   * 
   * @return <code>true</code> if the cursor is on the first row;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  boolean isFirst() throws SQLException;

  /**
   * Retrieves whether the cursor is on the last row of
   * this <code>ResultSet</code> object.
   * Note: Calling the method <code>isLast</code> may be expensive
   * because the JDBC driver
   * might need to fetch ahead one row in order to determine
   * whether the current row is the last row in the result set.
   * 
   * 
   * @return <code>true</code> if the cursor is on the last row;
   * <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @since 1.2
   */
  boolean isLast() throws SQLException;

  /**
   * Moves the cursor to the last row in
   * this <code>ResultSet</code> object.
   * 
   * 
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if there are no rows in the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  boolean last() throws SQLException;

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
   * 
   * @exception SQLException if a database access error occurs
   * or the result set is not updatable
   * @since 1.2
   */
  void moveToInsertRow() throws SQLException;

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
   * 
   * @return <code>true</code> if the new current row is valid;
   * <code>false</code> if there are no more rows
   * @exception SQLException if a database access error occurs
   */
  boolean next() throws SQLException;

  /**
   * Moves the cursor to the previous row in this
   * <code>ResultSet</code> object.
   * 
   * 
   * @return <code>true</code> if the cursor is on a valid row;
   * <code>false</code> if it is off the result set
   * @exception SQLException if a database access error
   * occurs or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @since 1.2
   */
  boolean previous() throws SQLException;

  boolean reload();

  /**
   * Retrieves whether the current row has had an insertion.
   * The value returned depends on whether or not this
   * <code>ResultSet</code> object can detect visible inserts.
   * 
   * 
   * @return <code>true</code> if a row has had an insertion
   * and insertions are detected; <code>false</code> otherwise
   * @exception SQLException if a database access error occurs
   * @see DatabaseMetaData#insertsAreDetected
   * @since 1.2
   */
  boolean rowInserted() throws SQLException;

  /**
   * Retrieves whether the current row has been updated.  The value returned
   * depends on whether or not the result set can detect updates.
   * 
   * 
   * @return <code>true</code> if both (1) the row has been visibly updated
   *         by the owner or another and (2) updates are detected
   * @exception SQLException if a database access error occurs
   * @see DatabaseMetaData#updatesAreDetected
   * @since 1.2
   */
  boolean rowUpdated() throws SQLException;

  /**
   * Updates the underlying database with the new contents of the
   * current row of this <code>ResultSet</code> object.
   * This method cannot be called when the cursor is on the insert row.
   * 
   * 
   * @exception SQLException if a database access error occurs or
   * if this method is called when the cursor is on the insert row
   * @since 1.2
   */
  void updateRow() throws SQLException;
  
  public void addActiveRowChangeListener(ActiveRowChangeListener l);
  
  public void removeActiveRowChangeListener(ActiveRowChangeListener l);
  
  public void lock();
  
  public void unlock();
  
  public boolean isDataLoaded();

  public boolean loadData();
  
  public int getRowCount();
}
