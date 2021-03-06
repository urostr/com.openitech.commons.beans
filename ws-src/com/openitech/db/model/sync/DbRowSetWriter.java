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
 * @(#)CachedRowSetWriter.java	1.17 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.openitech.db.model.sync;

import java.sql.*;
import java.util.logging.Level;
import javax.sql.*;
import java.util.*;
import java.io.*;

import com.sun.rowset.*;

import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.sql.rowset.*;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.*;

/**
 * The facility called on internally by the <code>RIOptimisticProvider</code> implementation to
 * propagate changes back to the data source from which the rowset got its data.
 * <P>
 * A <code>CachedRowSetWriter</code> object, called a writer, has the public
 * method <code>writeData</code> for writing modified data to the underlying data source.
 * This method is invoked by the rowset internally and is never invoked directly by an application.
 * A writer also has public methods for setting and getting
 * the <code>CachedRowSetReader</code> object, called a reader, that is associated
 * with the writer. The remainder of the methods in this class are private and
 * are invoked internally, either directly or indirectly, by the method
 * <code>writeData</code>.
 * <P>
 * Typically the <code>SyncFactory</code> manages the <code>RowSetReader</code> and
 * the <code>RowSetWriter</code> implementations using <code>SyncProvider</code> objects.
 * Standard JDBC RowSet implementations provide an object instance of this
 * writer by invoking the <code>SyncProvider.getRowSetWriter()</code> method.
 *
 * @version 0.2
 * @author Jonathan Bruce
 * @see javax.sql.rowset.spi.SyncProvider
 * @see javax.sql.rowset.spi.SyncFactory
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class DbRowSetWriter implements TransactionalWriter, Serializable {

  /**
   * The <code>Connection</code> object that this writer will use to make a
   * connection to the data source to which it will write data.
   *
   */
  private transient Connection con;
  /**
   * The SQL <code>SELECT</code> command that this writer will call
   * internally. The method <code>initSQLStatements</code> builds this
   * command by supplying the words "SELECT" and "FROM," and using
   * metadata to get the table name and column names .
   *
   * @serial
   */
  private String selectCmd;
  /**
   * The SQL <code>UPDATE</code> command that this writer will call
   * internally to write data to the rowset's underlying data source.
   * The method <code>initSQLStatements</code> builds this <code>String</code>
   * object.
   *
   * @serial
   */
  private String updateCmd;
  /**
   * The SQL <code>WHERE</code> clause the writer will use for update
   * statements in the <code>PreparedStatement</code> object
   * it sends to the underlying data source.
   *
   * @serial
   */
  private String updateWhere;
  /**
   * The SQL <code>DELETE</code> command that this writer will call
   * internally to delete a row in the rowset's underlying data source.
   *
   * @serial
   */
  private String deleteCmd;
  /**
   * The SQL <code>WHERE</code> clause the writer will use for delete
   * statements in the <code>PreparedStatement</code> object
   * it sends to the underlying data source.
   *
   * @serial
   */
  private String deleteWhere;
  /**
   * The SQL <code>INSERT INTO</code> command that this writer will internally use
   * to insert data into the rowset's underlying data source.  The method
   * <code>initSQLStatements</code> builds this command with a question
   * mark parameter placeholder for each column in the rowset.
   *
   * @serial
   */
  private String insertCmd;
  /**
   * An array containing the column numbers of the columns that are
   * needed to uniquely identify a row in the <code>CachedRowSet</code> object
   * for which this <code>CachedRowSetWriter</code> object is the writer.
   *
   * @serial
   */
  private int[] keyCols;
  /**
   * An array of the parameters that should be used to set the parameter
   * placeholders in a <code>PreparedStatement</code> object that this
   * writer will execute.
   *
   * @serial
   */
  private Object[] params;
  /**
   * The <code>CachedRowSetReader</code> object that has been
   * set as the reader for the <code>CachedRowSet</code> object
   * for which this <code>CachedRowSetWriter</code> object is the writer.
   *
   * @serial
   */
  private RowSetReader reader;
  /**
   * The <code>ResultSetMetaData</code> object that contains information
   * about the columns in the <code>CachedRowSet</code> object
   * for which this <code>CachedRowSetWriter</code> object is the writer.
   *
   * @serial
   */
  private ResultSetMetaData callerMd;
  /**
   * The number of columns in the <code>CachedRowSet</code> object
   * for which this <code>CachedRowSetWriter</code> object is the writer.
   *
   * @serial
   */
  private int callerColumnCount;
  /**
   * This <code>CachedRowSet<code> will hold the conflicting values
   *  retrieved from the db and hold it.
   */
  private DbChachedRowSetImpl crsResolve;
  /**
   * This <code>ArrayList<code> will hold the values of SyncResolver.*
   */
  private ArrayList status;
  /**
   * This will check whether the same field value has changed both
   * in database and CachedRowSet.
   */
  private int iChangedValsInDbAndCRS;
  /**
   * This will hold the number of cols for which the values have
   * changed only in database.
   */
  private int iChangedValsinDbOnly;
  private JdbcRowSetResourceBundle resBundle;

  public DbRowSetWriter() {
    try {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Propagates changes in the given <code>RowSet</code> object
   * back to its underlying data source and returns <code>true</code>
   * if successful. The writer will check to see if
   * the data in the pre-modified rowset (the original values) differ
   * from the data in the underlying data source.  If data in the data
   * source has been modified by someone else, there is a conflict,
   * and in that case, the writer will not write to the data source.
   * In other words, the writer uses an optimistic concurrency algorithm:
   * It checks for conflicts before making changes rather than restricting
   * access for concurrent users.
   * <P>
   * This method is called by the rowset internally when
   * the application invokes the method <code>acceptChanges</code>.
   * The <code>writeData</code> method in turn calls private methods that
   * it defines internally.
   * The following is a general summary of what the method
   * <code>writeData</code> does, much of which is accomplished
   * through calls to its own internal methods.
   * <OL>
   * <LI>Creates a <code>CachedRowSet</code> object from the given
   *     <code>RowSet</code> object
   * <LI>Makes a connection with the data source
   *   <UL>
   *      <LI>Disables autocommit mode if it is not already disabled
   *      <LI>Sets the transaction isolation level to that of the rowset
   *   </UL>
   * <LI>Checks to see if the reader has read new data since the writer
   *     was last called and, if so, calls the method
   *    <code>initSQLStatements</code> to initialize new SQL statements
   *   <UL>
   *       <LI>Builds new <code>SELECT</code>, <code>UPDATE</code>,
   *           <code>INSERT</code>, and <code>DELETE</code> statements
   *       <LI>Uses the <code>CachedRowSet</code> object's metadata to
   *           determine the table name, column names, and the columns
   *           that make up the primary key
   *   </UL>
   * <LI>When there is no conflict, propagates changes made to the
   *     <code>CachedRowSet</code> object back to its underlying data source
   *   <UL>
   *      <LI>Iterates through each row of the <code>CachedRowSet</code> object
   *          to determine whether it has been updated, inserted, or deleted
   *      <LI>If the corresponding row in the data source has not been changed
   *          since the rowset last read its
   *          values, the writer will use the appropriate command to update,
   *          insert, or delete the row
   *      <LI>If any data in the data source does not match the original values
   *          for the <code>CachedRowSet</code> object, the writer will roll
   *          back any changes it has made to the row in the data source.
   *   </UL>
   * </OL>
   *
   * @return <code>true</code> if changes to the rowset were successfully
   *         written to the rowset's underlying data source;
   *         <code>false</code> otherwise
   */
  public boolean writeData(RowSetInternal caller) throws SQLException {
    boolean conflict = false;
    boolean showDel = false;
    PreparedStatement pstmtIns = null;
    iChangedValsInDbAndCRS = 0;
    iChangedValsinDbOnly = 0;

    // We assume caller is a CachedRowSet
    WebRowSet wrs = (WebRowSet) caller;
    // crsResolve = new DbChachedRowSetImpl();
    this.crsResolve = new DbChachedRowSetImpl();
    ;

    // The reader is registered with the writer at design time.
    // This is not required, in general.  The reader has logic
    // to get a JDBC connection, so call it.

    //con = reader.connect(caller);
    con = caller.getConnection();

    if (con == null) {
      throw new SQLException(resBundle.handleGetObject("crswriter.connect").toString());
    }
    Writer writer = new StringWriter(200);
    wrs.writeXml(writer);
    try {
      writer.flush();
    } catch (IOException ex) {
      Logger.getLogger(DbRowSetWriter.class.getName()).log(Level.SEVERE, null, ex);
    }
    Logger.getLogger(DbChachedRowSetImpl.class.getName()).warning(writer.toString());

    executeStoreEvent(writer.toString());

    return true;
    /*
    // Fix 6200646.
    // Don't change the connection or transaction properties. This will fail in a
    // J2EE container.
    if (con.getAutoCommit() == true)  {
    con.setAutoCommit(false);
    }

    con.setTransactionIsolation(crs.getTransactionIsolation());
     */

//    initSQLStatements(wrs);
//    int iColCount;
//
//    RowSetMetaDataImpl rsmdWrite = (RowSetMetaDataImpl) wrs.getMetaData();
//    RowSetMetaDataImpl rsmdResolv = new RowSetMetaDataImpl();
//
//    iColCount = rsmdWrite.getColumnCount();
//    int sz = wrs.size() + 1;
//    status = new ArrayList(sz);
//
//    status.add(0, null);
//    rsmdResolv.setColumnCount(iColCount);
//
//    for (int i = 1; i <= iColCount; i++) {
//      rsmdResolv.setColumnType(i, rsmdWrite.getColumnType(i));
//      rsmdResolv.setColumnName(i, rsmdWrite.getColumnName(i));
//      rsmdResolv.setNullable(i, ResultSetMetaData.columnNullableUnknown);
//    }
//    this.crsResolve.setMetaData(rsmdResolv);
//
//    // moved outside the insert inner loop
//    //pstmtIns = con.prepareStatement(insertCmd);
//
//    if (callerColumnCount < 1) {
//      // No data, so return success.
//      if (reader.getCloseConnection() == true) {
//        con.close();
//      }
//      return true;
//    }
//    // We need to see rows marked for deletion.
//    showDel = wrs.getShowDeleted();
//    wrs.setShowDeleted(true);
//
//    // Look at all the rows.
//    wrs.beforeFirst();
//
//    int rows = 1;
//    while (wrs.next()) {
//      if (wrs.rowDeleted()) {
//        // The row has been deleted.
//        if (conflict = (deleteOriginalRow(wrs, this.crsResolve)) == true) {
//          status.add(rows, new Integer(SyncResolver.DELETE_ROW_CONFLICT));
//        } else {
//          // delete happened without any occurrence of conflicts
//          // so update status accordingly
//          status.add(rows, new Integer(SyncResolver.NO_ROW_CONFLICT));
//        }
//
//      } else if (wrs.rowInserted()) {
//        // The row has been inserted.
//
//        pstmtIns = con.prepareStatement(insertCmd);
//        if ((conflict = insertNewRow(wrs, pstmtIns, this.crsResolve)) == true) {
//          status.add(rows, new Integer(SyncResolver.INSERT_ROW_CONFLICT));
//        } else {
//          // insert happened without any occurrence of conflicts
//          // so update status accordingly
//          status.add(rows, new Integer(SyncResolver.NO_ROW_CONFLICT));
//        }
//      } else if (wrs.rowUpdated()) {
//        // The row has been updated.
//        if (conflict = (updateOriginalRow(wrs)) == true) {
//          status.add(rows, new Integer(SyncResolver.UPDATE_ROW_CONFLICT));
//        } else {
//          // update happened without any occurrence of conflicts
//          // so update status accordingly
//          status.add(rows, new Integer(SyncResolver.NO_ROW_CONFLICT));
//        }
//
//      } else {
//        /** The row is neither of inserted, updated or deleted.
//         *  So set nulls in the this.crsResolve for this row,
//         *  as nothing is to be done for such rows.
//         *  Also note that if such a row has been changed in database
//         *  and we have not changed(inserted, updated or deleted)
//         *  that is fine.
//         **/
//        int icolCount = wrs.getMetaData().getColumnCount();
//        status.add(rows, new Integer(SyncResolver.NO_ROW_CONFLICT));
//
//        this.crsResolve.moveToInsertRow();
//        for (int cols = 0; cols < iColCount; cols++) {
//          this.crsResolve.updateNull(cols + 1);
//        } //end for
//
//        this.crsResolve.insertRow();
//        this.crsResolve.moveToCurrentRow();
//
//      } //end if
//      rows++;
//    } //end while
//
//    // close the insert statement
//    if (pstmtIns != null) {
//      pstmtIns.close();
//    }
//    // reset
//    wrs.setShowDeleted(showDel);
//
//    boolean boolConf = false;
//    for (int j = 1; j < status.size(); j++) {
//      // ignore status for index = 0 which is set to null
//      if (!((status.get(j)).equals(new Integer(SyncResolver.NO_ROW_CONFLICT)))) {
//        // there is at least one conflict which needs to be resolved
//        boolConf = true;
//        break;
//      }
//    }
//
//    wrs.beforeFirst();
//    this.crsResolve.beforeFirst();
//
//    if (boolConf) {
//      SyncProviderException spe = new SyncProviderException(status.size() - 1 + resBundle.handleGetObject("crswriter.conflictsno").toString());
//      //SyncResolver syncRes = spe.getSyncResolver();
//
//      SyncResolverImpl syncResImpl = (SyncResolverImpl) spe.getSyncResolver();
//
//      syncResImpl.setCachedRowSet(wrs);
//      syncResImpl.setCachedRowSetResolver(this.crsResolve);
//
//      syncResImpl.setStatus(status);
//      syncResImpl.setCachedRowSetWriter(this);
//
//      throw spe;
//    } else {
//      return true;
//    }
//    /*
//    if (conflict == true) {
//    con.rollback();
//    return false;
//    } else {
//    con.commit();
//    if (reader.getCloseConnection() == true) {
//    con.close();
//    }
//    return true;
//    }
//     */


  } //end writeData

  /**
   * Calls Store Event procedure with given xml as parameter
   *
   * @param xml String representation of xml
   *
   * @throws NullPointerException if xml is null
   **/
  public void executeStoreEvent(String xml) throws SQLException {
    if (xml == null) {
      throw new NullPointerException("Procedura ne sprejema null stringe!");
    }
    con.createStatement().execute("EXECUTE ChangeLog.dbo.[StoreEvent] '" + xml + "'");
  }

  /**
   * Updates the given <code>CachedRowSet</code> object's underlying data
   * source so that updates to the rowset are reflected in the original
   * data source, and returns <code>false</code> if the update was successful.
   * A return value of <code>true</code> indicates that there is a conflict,
   * meaning that a value updated in the rowset has already been changed by
   * someone else in the underlying data source.  A conflict can also exist
   * if, for example, more than one row in the data source would be affected
   * by the update or if no rows would be affected.  In any case, if there is
   * a conflict, this method does not update the underlying data source.
   * <P>
   * This method is called internally by the method <code>writeData</code>
   * if a row in the <code>CachedRowSet</code> object for which this
   * <code>CachedRowSetWriter</code> object is the writer has been updated.
   *
   * @return <code>false</code> if the update to the underlying data source is
   *         successful; <code>true</code> otherwise
   * @throws SQLException if a database access error occurs
   */
  private boolean updateOriginalRow(CachedRowSet crs)
          throws SQLException {
    PreparedStatement pstmt;
    int i = 0;
    int idx = 0;

    // Select the row from the database.
    ResultSet origVals = crs.getOriginalRow();
    origVals.next();

    try {
      updateWhere = buildWhereClause(updateWhere, origVals);


      /**
       *  The following block of code is for checking a particular type of
       *  query where in there is a where clause. Without this block, if a
       *  SQL statement is built the "where" clause will appear twice hence
       *  the DB errors out and a SQLException is thrown. This code also
       *  considers that the where clause is in the right place as the
       *  CachedRowSet object would already have been populated with this
       *  query before coming to this point.
       **/
      String tempselectCmd = selectCmd.toLowerCase();

      int idxWhere = tempselectCmd.indexOf("where");

      if (idxWhere != -1) {
        String tempSelect = selectCmd.substring(0, idxWhere);
        selectCmd = tempSelect;
      }

      pstmt = con.prepareStatement(selectCmd + updateWhere,
              ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

      for (i = 0; i < keyCols.length; i++) {
        if (params[i] != null) {
          pstmt.setObject(++idx, params[i]);
        } else {
          continue;
        }
      }

      try {
        pstmt.setMaxRows(crs.getMaxRows());
        pstmt.setMaxFieldSize(crs.getMaxFieldSize());
        pstmt.setEscapeProcessing(crs.getEscapeProcessing());
        pstmt.setQueryTimeout(crs.getQueryTimeout());
      } catch (Exception ex) {
        // Older driver don't support these operations.
      }

      ResultSet rs = null;
      rs = pstmt.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();

      if (rs.next() == true) {
        if (rs.next()) {
          /**  More than one row conflict.
           *  If rs has only one row we are able to
           *  uniquely identify the row where update
           *  have to happen else if more than one
           *  row implies we cannot uniquely identify the row
           *  where we have to do updates.
           *  crs.setKeyColumns needs to be set to
           *  come out of this situation.
           */
          return true;
        }

        // don't close the rs
        // we require the record in rs to be used.
        // rs.close();
        //pstmt.close();
        rs.first();

        // how many fields need to be updated
        int colsNotChanged = 0;
        Vector cols = new Vector();
        String updateExec = new String(updateCmd);
        Object orig;
        Object curr;
        Object rsval;
        boolean boolNull = true;
        Object objVal = null;

        // There's only one row and the cursor
        // needs to be on that row.

        boolean first = true;
        boolean flag = true;

        this.crsResolve.moveToInsertRow();

        for (i = 1; i <= callerColumnCount; i++) {
          orig = origVals.getObject(i);
          curr = crs.getObject(i);
          rsval = rs.getObject(i);
          /*
           * the following block creates equivalent objects
           * that would have been created if this rs is populated
           * into a CachedRowSet so that comparison of the column values
           * from the ResultSet and CachedRowSet are possible
           */
          Map map = (crs.getTypeMap() == null) ? con.getTypeMap() : crs.getTypeMap();
          if (rsval instanceof Struct) {

            Struct s = (Struct) rsval;

            // look up the class in the map
            Class c = null;
            c = (Class) map.get(s.getSQLTypeName());
            if (c != null) {
              // create new instance of the class
              SQLData obj = null;
              try {
                obj = (SQLData) c.newInstance();
              } catch (java.lang.InstantiationException ex) {
                throw new SQLException(MessageFormat.format(resBundle.handleGetObject("DbCachedRowSetImpl.unableins").toString(),
                        ex.getMessage()));
              } catch (java.lang.IllegalAccessException ex) {
                throw new SQLException(MessageFormat.format(resBundle.handleGetObject("DbCachedRowSetImpl.unableins").toString(),
                        ex.getMessage()));
              }
              // get the attributes from the struct
              Object attribs[] = s.getAttributes(map);
              // create the SQLInput "stream"
              SQLInputImpl sqlInput = new SQLInputImpl(attribs, map);
              // read the values...
              obj.readSQL(sqlInput, s.getSQLTypeName());
              rsval = obj;
            }
          } else if (rsval instanceof SQLData) {
            rsval = new SerialStruct((SQLData) rsval, map);
          } else if (rsval instanceof Blob) {
            rsval = new SerialBlob((Blob) rsval);
          } else if (rsval instanceof Clob) {
            rsval = new SerialClob((Clob) rsval);
          } else if (rsval instanceof java.sql.Array) {
            rsval = new SerialArray((java.sql.Array) rsval, map);
          }

          // reset boolNull if it had been set
          boolNull = true;

          /** This addtional checking has been added when the current value
           *  in the DB is null, but the DB had a different value when the
           *  data was actaully fetched into the CachedRowSet.
           **/
          if (rsval == null && orig != null) {
            // value in db has changed
            // don't proceed with synchronization
            // get the value in db and pass it to the resolver.

            iChangedValsinDbOnly++;
            // Set the boolNull to false,
            // in order to set the actual value;
            boolNull = false;
            objVal = rsval;
          } /** Adding the checking for rsval to be "not" null or else
           *  it would through a NullPointerException when the values
           *  are compared.
           **/
          else if (rsval != null && (!rsval.equals(orig))) {
            // value in db has changed
            // don't proceed with synchronization
            // get the value in db and pass it to the resolver.

            iChangedValsinDbOnly++;
            // Set the boolNull to false,
            // in order to set the actual value;
            boolNull = false;
            objVal = rsval;
          } else if ((orig == null || curr == null)) {

            /** Adding the additonal condition of checking for "flag"
             *  boolean variable, which would otherwise result in
             *  building a invalid query, as the comma would not be
             *  added to the query string.
             **/
            if (first == false || flag == false) {
              updateExec += ", ";
            }
            updateExec += crs.getMetaData().getColumnName(i);
            cols.add(new Integer(i));
            updateExec += " = ? ";
            first = false;

            /** Adding the extra condition for orig to be "not" null as the
             *  condition for orig to be null is take prior to this, if this
             *  is not added it will result in a NullPointerException when
             *  the values are compared.
             **/
          } else if (orig.equals(curr)) {
            colsNotChanged++;
            //nothing to update in this case since values are equal

            /** Adding the extra condition for orig to be "not" null as the
             *  condition for orig to be null is take prior to this, if this
             *  is not added it will result in a NullPointerException when
             *  the values are compared.
             **/
          } else if (orig.equals(curr) == false) {
            // When values from db and values in CachedRowSet are not equal,
            // if db value is same as before updation for each col in
            // the row before fetching into CachedRowSet,
            // only then we go ahead with updation, else we
            // throw SyncProviderException.

            // if value has changed in db after fetching from db
            // for some cols of the row and at the same time, some other cols
            // have changed in CachedRowSet, no synchronization happens

            // Synchronization happens only when data when fetching is
            // same or at most has changed in cachedrowset

            // check orig value with what is there in crs for a column
            // before updation in crs.

            if (crs.columnUpdated(i)) {
              if (rsval.equals(orig)) {
                // At this point we are sure that
                // the value updated in crs was from
                // what is in db now and has not changed
                if (flag == false || first == false) {
                  updateExec += ", ";
                }
                updateExec += crs.getMetaData().getColumnName(i);
                cols.add(new Integer(i));
                updateExec += " = ? ";
                flag = false;
              } else {
                // Here the value has changed in the db after
                // data was fetched
                // Plus store this row from CachedRowSet and keep it
                // in a new CachedRowSet
                boolNull = false;
                objVal = rsval;
                iChangedValsInDbAndCRS++;
              }
            }
          }

          if (!boolNull) {
            this.crsResolve.updateObject(i, objVal);
          } else {
            this.crsResolve.updateNull(i);
          }
        } //end for

        rs.close();
        pstmt.close();

        this.crsResolve.insertRow();
        this.crsResolve.moveToCurrentRow();

        /**
         * if nothing has changed return now - this can happen
         * if column is updated to the same value.
         * if colsNotChanged == callerColumnCount implies we are updating
         * the database with ALL COLUMNS HAVING SAME VALUES,
         * so skip going to database, else do as usual.
         **/
        if ((first == false && cols.size() == 0) ||
                colsNotChanged == callerColumnCount) {
          return false;
        }

        if (iChangedValsInDbAndCRS != 0 || iChangedValsinDbOnly != 0) {
          return true;
        }


        updateExec += updateWhere;

        pstmt = con.prepareStatement(updateExec);

        // Comments needed here
        for (i = 0; i < cols.size(); i++) {
          Object obj = crs.getObject(((Integer) cols.get(i)).intValue());
          if (obj != null) {
            pstmt.setObject(i + 1, obj);
          } else {
            pstmt.setNull(i + 1, crs.getMetaData().getColumnType(i + 1));
          }
        }
        idx = i;

        // Comments needed here
        for (i = 0; i < keyCols.length; i++) {
          if (params[i] != null) {
            pstmt.setObject(++idx, params[i]);
          } else {
            continue;
          }
        }

        i = pstmt.executeUpdate();

        /**
         * i should be equal to 1(row count), because we update
         * one row(returned as row count) at a time, if all goes well.
         * if 1 != 1, this implies we have not been able to
         * do updations properly i.e there is a conflict in database
         * versus what is in CachedRowSet for this particular row.
         **/
        return false;

      } else {
        /**
         * Cursor will be here, if the ResultSet may not return even a single row
         * i.e. we can't find the row where to update because it has been deleted
         * etc. from the db.
         * Present the whole row as null to user, to force null to be sync'ed
         * and hence nothing to be synced.
         *
         * NOTE:
         * ------
         * In the database if a column that is mapped to java.sql.Types.REAL stores
         * a Double value and is compared with value got from ResultSet.getFloat()
         * no row is retrieved and will throw a SyncProviderException. For details
         * see bug Id 5053830
         **/
        return true;
      }
    } catch (SQLException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, ex.getMessage(), ex);
      // if executeUpdate fails it will come here,
      // update crsResolve with null rows
      this.crsResolve.moveToInsertRow();

      for (i = 1; i <= callerColumnCount; i++) {
        this.crsResolve.updateNull(i);
      }

      this.crsResolve.insertRow();
      this.crsResolve.moveToCurrentRow();

      return true;
    }
  }

  /**
   * Inserts a row that has been inserted into the given
   * <code>CachedRowSet</code> object into the data source from which
   * the rowset is derived, returning <code>false</code> if the insertion
   * was successful.
   *
   * @param crs the <code>CachedRowSet</code> object that has had a row inserted
   *            and to whose underlying data source the row will be inserted
   * @param pstmt the <code>PreparedStatement</code> object that will be used
   *              to execute the insertion
   * @return <code>false</code> to indicate that the insertion was successful;
   *         <code>true</code> otherwise
   * @throws SQLException if a database access error occurs
   */
  private boolean insertNewRow(CachedRowSet crs,
          PreparedStatement pstmt, DbChachedRowSetImpl crsRes) throws SQLException {
    int i = 0;
    int icolCount = crs.getMetaData().getColumnCount();

    boolean returnVal = false;
    PreparedStatement pstmtSel = con.prepareStatement(selectCmd,
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    ResultSet rs, rs2 = null;
    DatabaseMetaData dbmd = con.getMetaData();
    rs = pstmtSel.executeQuery();
    String table = crs.getTableName();
    rs2 = dbmd.getPrimaryKeys(null, null, table);
    String[] primaryKeys = new String[icolCount];
    int k = 0;
    while (rs2.next()) {
      String pkcolname = rs2.getString("COLUMN_NAME");
      primaryKeys[k] = pkcolname;
      k++;
    }

    if (rs.next()) {
      for (int j = 0; j < primaryKeys.length; j++) {
        if (primaryKeys[j] != null) {
          if (crs.getObject(primaryKeys[j]) == null) {
            break;
          }
          String crsPK = (crs.getObject(primaryKeys[j])).toString();
          String rsPK = (rs.getObject(primaryKeys[j])).toString();
          if (crsPK.equals(rsPK)) {
            returnVal = true;
            this.crsResolve.moveToInsertRow();
            for (i = 1; i <= icolCount; i++) {
              String colname = (rs.getMetaData()).getColumnName(i);
              if (colname.equals(primaryKeys[j])) {
                this.crsResolve.updateObject(i, rsPK);
              } else {
                this.crsResolve.updateNull(i);
              }
            }
            this.crsResolve.insertRow();
            this.crsResolve.moveToCurrentRow();
          }
        }
      }
    }
    if (returnVal) {
      return returnVal;
    }

    try {
      for (i = 1; i <= icolCount; i++) {
        Object obj = crs.getObject(i);
        if (obj != null) {
          pstmt.setObject(i, obj);
        } else {
          pstmt.setNull(i, crs.getMetaData().getColumnType(i));
        }
      }

      i = pstmt.executeUpdate();
      return false;

    } catch (SQLException ex) {
      /**
       * Cursor will come here if executeUpdate fails.
       * There can be many reasons why the insertion failed,
       * one can be violation of primary key.
       * Hence we cannot exactly identify why the insertion failed
       * Present the current row as a null row to the user.
       **/
      this.crsResolve.moveToInsertRow();

      for (i = 1; i <= icolCount; i++) {
        this.crsResolve.updateNull(i);
      }

      this.crsResolve.insertRow();
      this.crsResolve.moveToCurrentRow();

      return true;
    }
  }

  /**
   * Deletes the row in the underlying data source that corresponds to
   * a row that has been deleted in the given <code> CachedRowSet</code> object
   * and returns <code>false</code> if the deletion was successful.
   * <P>
   * This method is called internally by this writer's <code>writeData</code>
   * method when a row in the rowset has been deleted. The values in the
   * deleted row are the same as those that are stored in the original row
   * of the given <code>CachedRowSet</code> object.  If the values in the
   * original row differ from the row in the underlying data source, the row
   * in the data source is not deleted, and <code>deleteOriginalRow</code>
   * returns <code>true</code> to indicate that there was a conflict.
   *
   *
   * @return <code>false</code> if the deletion was successful, which means that
   *         there was no conflict; <code>true</code> otherwise
   * @throws SQLException if there was a database access error
   */
  private boolean deleteOriginalRow(CachedRowSet crs, DbChachedRowSetImpl crsRes) throws SQLException {
    PreparedStatement pstmt;
    int i;
    int idx = 0;
    String strSelect;
    // Select the row from the database.
    ResultSet origVals = crs.getOriginalRow();
    origVals.next();

    deleteWhere = buildWhereClause(deleteWhere, origVals);
    pstmt = con.prepareStatement(selectCmd + deleteWhere,
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

    for (i = 0; i < keyCols.length; i++) {
      if (params[i] != null) {
        pstmt.setObject(++idx, params[i]);
      } else {
        continue;
      }
    }

    try {
      pstmt.setMaxRows(crs.getMaxRows());
      pstmt.setMaxFieldSize(crs.getMaxFieldSize());
      pstmt.setEscapeProcessing(crs.getEscapeProcessing());
      pstmt.setQueryTimeout(crs.getQueryTimeout());
    } catch (Exception ex) {
      /*
       * Older driver don't support these operations...
       */
      ;
    }

    ResultSet rs = pstmt.executeQuery();

    if (rs.next() == true) {
      if (rs.next()) {
        // more than one row
        return true;
      }
      rs.first();

      // Now check all the values in rs to be same in
      // db also before actually going ahead with deleting
      boolean boolChanged = false;

      crsRes.moveToInsertRow();

      for (i = 1; i <= crs.getMetaData().getColumnCount(); i++) {

        Object original = origVals.getObject(i);
        Object changed = rs.getObject(i);

        if (original != null && changed != null) {
          if (!(original.toString()).equals(changed.toString())) {
            boolChanged = true;
            crsRes.updateObject(i, origVals.getObject(i));
          }
        } else {
          crsRes.updateNull(i);
        }
      }

      crsRes.insertRow();
      crsRes.moveToCurrentRow();

      if (boolChanged) {
        // do not delete as values in db have changed
        // deletion will not happen for this row from db
        // exit now returning true. i.e. conflict
        return true;
      } else {
        // delete the row.
        // Go ahead with deleting,
        // don't do anything here
      }

      String cmd = deleteCmd + deleteWhere;
      pstmt = con.prepareStatement(cmd);

      idx = 0;
      for (i = 0; i < keyCols.length; i++) {
        if (params[i] != null) {
          pstmt.setObject(++idx, params[i]);
        } else {
          continue;
        }
      }

      if (pstmt.executeUpdate() != 1) {
        return true;
      }
      pstmt.close();
    } else {
      // didn't find the row
      return true;
    }

    // no conflict
    return false;
  }

  /**
   * Sets the reader for this writer to the given reader.
   *
   * @throws SQLException if a database access error occurs
   */
  public void setReader(RowSetReader reader) throws SQLException {
    this.reader = reader;
  }

  /**
   * Gets the reader for this writer.
   *
   * @throws SQLException if a database access error occurs
   */
  public RowSetReader getReader() throws SQLException {
    return reader;
  }

  /**
   * Composes a <code>SELECT</code>, <code>UPDATE</code>, <code>INSERT</code>,
   * and <code>DELETE</code> statement that can be used by this writer to
   * write data to the data source backing the given <code>CachedRowSet</code>
   * object.
   *
   * @ param caller a <code>CachedRowSet</code> object for which this
   *                <code>CachedRowSetWriter</code> object is the writer
   * @throws SQLException if a database access error occurs
   */
  private void initSQLStatements(CachedRowSet caller) throws SQLException {

    int i;

    callerMd = caller.getMetaData();
    callerColumnCount = callerMd.getColumnCount();
    if (callerColumnCount < 1) // No data, so return.
    {
      return;
    }

    /*
     * If the RowSet has a Table name we should use it.
     * This is really a hack to get round the fact that
     * a lot of the jdbc drivers can't provide the tab.
     */
    String table = caller.getTableName();
    if (table == null) {
      /*
       * attempt to build a table name using the info
       * that the driver gave us for the first column
       * in the source result set.
       */
      table = callerMd.getTableName(1);
      if (table == null || table.length() == 0) {
        throw new SQLException(resBundle.handleGetObject("crswriter.tname").toString());
      }
    }
    String catalog = callerMd.getCatalogName(1);
    String schema = callerMd.getSchemaName(1);
    DatabaseMetaData dbmd = con.getMetaData();

    /*
     * Compose a SELECT statement.  There are three parts.
     */

    // Project List
    selectCmd = "SELECT ";
    for (i = 1; i <= callerColumnCount; i++) {
      selectCmd += callerMd.getColumnName(i);
      if (i < callerMd.getColumnCount()) {
        selectCmd += ", ";
      } else {
        selectCmd += " ";
      }
    }

    // FROM clause.
    selectCmd += "FROM " + buildTableName(dbmd, catalog, schema, table);

    /*
     * Compose an UPDATE statement.
     */
    updateCmd = "UPDATE " + buildTableName(dbmd, catalog, schema, table);


    /**
     *  The following block of code is for checking a particular type of
     *  query where in there is a where clause. Without this block, if a
     *  SQL statement is built the "where" clause will appear twice hence
     *  the DB errors out and a SQLException is thrown. This code also
     *  considers that the where clause is in the right place as the
     *  CachedRowSet object would already have been populated with this
     *  query before coming to this point.
     **/
    String tempupdCmd = updateCmd.toLowerCase();

    int idxupWhere = tempupdCmd.indexOf("where");

    if (idxupWhere != -1) {
      updateCmd = updateCmd.substring(0, idxupWhere);
    }
    updateCmd += "SET ";

    /*
     * Compose an INSERT statement.
     */
    insertCmd = "INSERT INTO " + buildTableName(dbmd, catalog, schema, table);
    // Column list
    insertCmd += "(";
    for (i = 1; i <= callerColumnCount; i++) {
      insertCmd += callerMd.getColumnName(i);
      if (i < callerMd.getColumnCount()) {
        insertCmd += ", ";
      } else {
        insertCmd += ") VALUES (";
      }
    }
    for (i = 1; i <= callerColumnCount; i++) {
      insertCmd += "?";
      if (i < callerColumnCount) {
        insertCmd += ", ";
      } else {
        insertCmd += ")";
      }
    }

    /*
     * Compose a DELETE statement.
     */
    deleteCmd = "DELETE FROM " + buildTableName(dbmd, catalog, schema, table);

    /*
     * set the key desriptors that will be
     * needed to construct where clauses.
     */
    buildKeyDesc(caller);
  }

  /**
   * Returns a fully qualified table name built from the given catalog and
   * table names. The given metadata object is used to get the proper order
   * and separator.
   *
   * @param dbmd a <code>DatabaseMetaData</code> object that contains metadata
   * 		about this writer's <code>CachedRowSet</code> object
   * @param catalog a <code>String</code> object with the rowset's catalog
   * 		name
   * @param table a <code>String</code> object with the name of the table from
   * 		which this writer's rowset was derived
   * @return a <code>String</code> object with the fully qualified name of the
   *		table from which this writer's rowset was derived
   * @throws SQLException if a database access error occurs
   */
  private String buildTableName(DatabaseMetaData dbmd,
          String catalog, String schema, String table) throws SQLException {

    // trim all the leading and trailing whitespaces,
    // white spaces can never be catalog, schema or a table name.

    String cmd = new String();

    catalog = catalog.trim();
    schema = schema.trim();
    table = table.trim();

    if (dbmd.isCatalogAtStart() == true) {
      if (catalog != null && catalog.length() > 0) {
        cmd += catalog + dbmd.getCatalogSeparator();
      }
      if (schema != null && schema.length() > 0) {
        cmd += schema + ".";
      }
      cmd += table;
    } else {
      if (schema != null && schema.length() > 0) {
        cmd += schema + ".";
      }
      cmd += table;
      if (catalog != null && catalog.length() > 0) {
        cmd += dbmd.getCatalogSeparator() + catalog;
      }
    }
    cmd += " ";
    return cmd;
  }

  /**
   * Assigns to the given <code>CachedRowSet</code> object's
   * <code>params</code>
   * field an array whose length equals the number of columns needed
   * to uniquely identify a row in the rowset. The array is given
   * values by the method <code>buildWhereClause</code>.
   * <P>
   * If the <code>CachedRowSet</code> object's <code>keyCols</code>
   * field has length <code>0</code> or is <code>null</code>, the array
   * is set with the column number of every column in the rowset.
   * Otherwise, the array in the field <code>keyCols</code> is set with only
   * the column numbers of the columns that are required to form a unique
   * identifier for a row.
   *
   * @param crs the <code>CachedRowSet</code> object for which this
   *     <code>CachedRowSetWriter</code> object is the writer
   *
   * @throws SQLException if a database access error occurs
   */
  private void buildKeyDesc(CachedRowSet crs) throws SQLException {

    keyCols = crs.getKeyColumns();
    ResultSetMetaData resultsetmd = crs.getMetaData();
    if (keyCols == null || keyCols.length == 0) {
      ArrayList<Integer> listKeys = new ArrayList<Integer>();

      for (int i = 0; i < callerColumnCount; i++) {
        if (resultsetmd.getColumnType(i + 1) != java.sql.Types.CLOB &&
                resultsetmd.getColumnType(i + 1) != java.sql.Types.STRUCT &&
                resultsetmd.getColumnType(i + 1) != java.sql.Types.SQLXML &&
                resultsetmd.getColumnType(i + 1) != java.sql.Types.BLOB &&
                resultsetmd.getColumnType(i + 1) != java.sql.Types.ARRAY &&
                resultsetmd.getColumnType(i + 1) != java.sql.Types.OTHER) {
          listKeys.add(i + 1);
        }
      }
      keyCols = new int[listKeys.size()];
      for (int i = 0; i < listKeys.size(); i++) {
        keyCols[i] = listKeys.get(i);
      }
    }
    params = new Object[keyCols.length];
  }

  /**
   * Constructs an SQL <code>WHERE</code> clause using the given
   * string as a starting point. The resulting clause will contain
   * a column name and " = ?" for each key column, that is, each column
   * that is needed to form a unique identifier for a row in the rowset.
   * This <code>WHERE</code> clause can be added to
   * a <code>PreparedStatement</code> object that updates, inserts, or
   * deletes a row.
   * <P>
   * This method uses the given result set to access values in the
   * <code>CachedRowSet</code> object that called this writer.  These
   * values are used to build the array of parameters that will serve as
   * replacements for the "?" parameter placeholders in the
   * <code>PreparedStatement</code> object that is sent to the
   * <code>CachedRowSet</code> object's underlying data source.
   *
   * @param whereClause a <code>String</code> object that is an empty
   *                    string ("")
   * @param rs a <code>ResultSet</code> object that can be used
   *           to access the <code>CachedRowSet</code> object's data
   * @return a <code>WHERE</code> clause of the form "<code>WHERE</code>
   *         columnName = ? AND columnName = ? AND columnName = ? ..."
   * @throws SQLException if a database access error occurs
   */
  private String buildWhereClause(String whereClause,
          ResultSet rs) throws SQLException {
    whereClause = "WHERE ";

    for (int i = 0; i < keyCols.length; i++) {
      if (i > 0) {
        whereClause += "AND ";
      }
      whereClause += callerMd.getColumnName(keyCols[i]);
      params[i] = rs.getObject(keyCols[i]);
      if (rs.wasNull() == true) {
        whereClause += " IS NULL ";
      } else {
        whereClause += " = ? ";
      }
    }
    return whereClause;
  }

  public void updateResolvedConflictToDB(CachedRowSet crs, Connection con) throws SQLException {
    //String updateExe = ;
    PreparedStatement pStmt;
    String strWhere = "WHERE ";
    String strExec = " ";
    String strUpdate = "UPDATE ";
    int icolCount = crs.getMetaData().getColumnCount();
    int keyColumns[] = crs.getKeyColumns();
    Object param[];
    String strSet = "";

    strWhere = buildWhereClause(strWhere, crs);

    if (keyColumns == null || keyColumns.length == 0) {
      keyColumns = new int[icolCount];
      for (int i = 0; i < keyColumns.length;) {
        keyColumns[i] = ++i;
      }
    }
    param = new Object[keyColumns.length];

    strUpdate = "UPDATE " + buildTableName(con.getMetaData(),
            crs.getMetaData().getCatalogName(1),
            crs.getMetaData().getSchemaName(1),
            crs.getTableName());

    // changed or updated values will become part of
    // set clause here
    strUpdate += "SET ";

    boolean first = true;

    for (int i = 1; i <= icolCount; i++) {
      if (crs.columnUpdated(i)) {
        if (first == false) {
          strSet += ", ";
        }
        strSet += crs.getMetaData().getColumnName(i);
        strSet += " = ? ";
        first = false;
      } //end if
      } //end for

    // keycols will become part of where clause
    strUpdate += strSet;
    strWhere = "WHERE ";

    for (int i = 0; i < keyColumns.length; i++) {
      if (i > 0) {
        strWhere += "AND ";
      }
      strWhere += crs.getMetaData().getColumnName(keyColumns[i]);
      param[i] = crs.getObject(keyColumns[i]);
      if (crs.wasNull() == true) {
        strWhere += " IS NULL ";
      } else {
        strWhere += " = ? ";
      }
    }
    strUpdate += strWhere;

    pStmt = con.prepareStatement(strUpdate);

    int idx = 0;
    for (int i = 0; i < icolCount; i++) {
      if (crs.columnUpdated(i + 1)) {
        Object obj = crs.getObject(i + 1);
        if (obj != null) {
          pStmt.setObject(++idx, obj);
        } else {
          pStmt.setNull(i + 1, crs.getMetaData().getColumnType(i + 1));
        } //end if ..else
      } //end if crs.column...
    } //end for

    // Set the key cols for after WHERE =? clause
    for (int i = 0; i < keyColumns.length; i++) {
      if (param[i] != null) {
        pStmt.setObject(++idx, param[i]);
      }
    }

    int id = pStmt.executeUpdate();
  }

  /**
   *
   */
  public void commit() throws SQLException {
    con.commit();
//    if (reader.getCloseConnection() == true) {
//      con.close();
//    }
  }

  public void commit(CachedRowSet crs, boolean updateRowset) throws SQLException {
    con.commit();
    if (updateRowset) {
      if (crs.getCommand() != null) {
        crs.execute(con);
      }
    }

//    if (reader.getCloseConnection() == true) {
//      con.close();
//    }
  }

  /**
   *
   */
  public void rollback() throws SQLException {
    con.rollback();
//    if (reader.getCloseConnection() == true) {
//      con.close();
//    }
  }

  /**
   *
   */
  public void rollback(Savepoint s) throws SQLException {
    con.rollback(s);
//    if (reader.getCloseConnection() == true) {
//      con.close();
//    }
  }

  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    // Default state initialization happens here
    ois.defaultReadObject();
    // Initialization of  Res Bundle happens here .
    try {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

  }
  static final long serialVersionUID = -8506030970299413976L;
}
