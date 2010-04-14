/*
 * @(#)CachedRowSetReader.java	1.13 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.openitech.db.model.sync;

import com.openitech.db.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.rowSet.DbWebRowSetImpl;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.*;
import javax.naming.*;
import java.io.*;
import com.openitech.db.model.Types;

import com.openitech.db.model.sql.SQLDataSource;
import com.sun.rowset.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.*;

/**
 * The facility called by the <code>RIOptimisticProvider</code> object
 * internally to read data into it.  The calling <code>RowSet</code> object
 * must have implemented the <code>RowSetInternal</code> interface
 * and have the standard <code>CachedRowSetReader</code> object set as its
 * reader.
 * <P>
 * This implementation always reads all rows of the data source,
 * and it assumes that the <code>command</code> property for the caller
 * is set with a query that is appropriate for execution by a
 * <code>PreparedStatement</code> object.
 * <P>
 * Typically the <code>SyncFactory</code> manages the <code>RowSetReader</code> and
 * the <code>RowSetWriter</code> implementations using <code>SyncProvider</code> objects.
 * Standard JDBC RowSet implementations provide an object instance of this
 * reader by invoking the <code>SyncProvider.getRowSetReader()</code> method.
 *
 * @version 0.2
 * @author Jonathan Bruce
 * @see javax.sql.rowset.spi.SyncProvider
 * @see javax.sql.rowset.spi.SyncFactory
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class DbEventRowSetReader implements RowSetReader, Serializable {

  /**
   * The field that keeps track of whether the writer associated with
   * this <code>CachedRowSetReader</code> object's rowset has been called since
   * the rowset was populated.
   * <P>
   * When this <code>CachedRowSetReader</code> object reads data into
   * its rowset, it sets the field <code>writerCalls</code> to 0.
   * When the writer associated with the rowset is called to write
   * data back to the underlying data source, its <code>writeData</code>
   * method calls the method <code>CachedRowSetReader.reset</code>,
   * which increments <code>writerCalls</code> and returns <code>true</code>
   * if <code>writerCalls</code> is 1. Thus, <code>writerCalls</code> equals
   * 1 after the first call to <code>writeData</code> that occurs
   * after the rowset has had data read into it.
   *
   * @serial
   */
  private int writerCalls = 0;
  private boolean userCon = false;
  private int startPosition;
  private JdbcRowSetResourceBundle resBundle;
  // private Connection connection;

  public DbEventRowSetReader() {
    try {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Reads data from a data source and populates the given
   * <code>RowSet</code> object with that data.
   * This method is called by the rowset internally when
   * the application invokes the method <code>execute</code>
   * to read a new set of rows.
   * <P>
   * After clearing the rowset of its contents, if any, and setting
   * the number of writer calls to <code>0</code>, this reader calls
   * its <code>connect</code> method to make
   * a connection to the rowset's data source. Depending on which
   * of the rowset's properties have been set, the <code>connect</code>
   * method will use a <code>DataSource</code> object or the
   * <code>DriverManager</code> facility to make a connection to the
   * data source.
   * <P>
   * Once the connection to the data source is made, this reader
   * executes the query in the calling <code>CachedRowSet</code> object's
   * <code>command</code> property. Then it calls the rowset's
   * <code>populate</code> method, which reads data from the
   * <code>ResultSet</code> object produced by executing the rowset's
   * command. The rowset is then populated with this data.
   * <P>
   * This method's final act is to close the connection it made, thus
   * leaving the rowset disconnected from its data source.
   *
   * @param caller a <code>RowSet</code> object that has implemented
   *               the <code>RowSetInternal</code> interface and had
   *               this <code>CachedRowSetReader</code> object set as
   *               its reader
   * @throws SQLException if there is a database access error, there is a
   *         problem making the connection, or the command property has not
   *         been set
   */
  @Override
  public void readData(RowSetInternal caller) throws SQLException {
    Connection con = null;
    try {
      WebRowSet wrs = (WebRowSet) caller;

      // Get rid of the current contents of the rowset.

      /**
       * Checking added to verify whether page size has been set or not.
       * If set then do not close the object as certain parameters need
       * to be maintained.
       */
      if (wrs.getPageSize() == 0 && wrs.size() > 0) {
        // When page size is not set,
        // crs.size() will show the total no of rows.
        wrs.close();
      }

      writerCalls = 0;

      // Get a connection.  This reader assumes that the necessary
      // properties have been set on the caller to let it supply a
      // connection.
      userCon = false;

      if (caller.getConnection() != null) {
        con = caller.getConnection();
        userCon = true;
      } else {
        con = getTxConnection();
      }

      // Check our assumptions.
      if (con == null) {//|| crs.getCommand() == null) {
        throw new SQLException(resBundle.handleGetObject("crsreader.connecterr").toString());
      }

      try {
        con.setTransactionIsolation(wrs.getTransactionIsolation());
      } catch (Exception ex) {
      }
      // Use JDBC to read the data.
      PreparedStatement pstmt = con.prepareStatement("SELECT [ChangeLog].[dbo].[getEventXMLRowSet] (?,?,? )");
      // Pass any input parameters to JDBC.
      List<Object> param = new ArrayList<Object>();
      for (Object value : caller.getParams()) {
        if (value instanceof Object[]) {
          param.add(((Object[]) value)[0]);
        } else {
          param.add(value);
        }
      }


      //   setParameters(pstmt, param, 1, false);
      // decodeParams(caller.getParams(), pstmt);
      try {
        pstmt.setMaxRows(wrs.getMaxRows());
        pstmt.setMaxFieldSize(wrs.getMaxFieldSize());
        pstmt.setEscapeProcessing(wrs.getEscapeProcessing());
        pstmt.setQueryTimeout(wrs.getQueryTimeout());
      } catch (Exception ex) {
        /*
         * drivers may not support the above - esp. older
         * drivers being used by the bridge..
         */
        throw new SQLException(ex.getMessage());
      }

      //ResultSet rs = pstmt.executeQuery();
      ResultSet rs = SQLDataSource.executeQuery(pstmt, param);
      if (rs.next()) {

        String test = rs.getString(1);
        System.out.println(test);
        if (test.length() > 0) {
          wrs.readXml(rs.getCharacterStream(1));
        }

      } else {
        throw new SQLException("Ni podatkov");
      }
      rs.close();
      // Get the data.
      pstmt.close();

      // only close connections we created...
      if (getCloseConnection()) {
//        con.close();
      }
    } catch (SQLException ex) {
      // Throw an exception if reading fails for any reason.
      throw ex;
    } finally {
      try {
        // only close connections we created...
        if (con != null && getCloseConnection()) {
//          con.close();
//          con = null;
        }
      } catch (Exception e) {
        // will get exception if something already went wrong, but don't
        // override that exception with this one
      }
    }
  }

  private static int setParameters(PreparedStatement statement, List<?> parameters, int pos, boolean subset) throws SQLException {
    if (!subset) {
      statement.clearParameters();
    }

    ParameterMetaData metaData = null;
    int parameterCount = Integer.MAX_VALUE;
    try {
      metaData = statement.getParameterMetaData();
      parameterCount = metaData.getParameterCount();
    } catch (SQLException err) {
      err.printStackTrace();
    }
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
          statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
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
    if (parameterCount < Integer.MAX_VALUE) {
      while ((pos <= parameterCount) && !subset) {
        statement.setNull(pos, metaData == null ? java.sql.Types.VARCHAR : metaData.getParameterType(pos++));
      }
    }
    return pos;
  }

  /**
   * Checks to see if the writer associated with this reader needs
   * to reset its state.  The writer will need to initialize its state
   * if new contents have been read since the writer was last called.
   * This method is called by the writer that was registered with
   * this reader when components were being wired together.
   *
   * @return <code>true</code> if writer associated with this reader needs
   *         to reset the values of its fields; <code>false</code> otherwise
   * @throws SQLException if an access error occurs
   */
  public boolean reset() throws SQLException {
    writerCalls++;
    return writerCalls == 1;
  }

  public Connection getTxConnection() {
    //return (this.connection == null) ? ConnectionManager.getInstance().getTxConnection() : this.connection;
    return ConnectionManager.getInstance().getTxConnection();
  }

  /**
   * Establishes a connection with the data source for the given
   * <code>RowSet</code> object.  If the rowset's <code>dataSourceName</code>
   * property has been set, this method uses the JNDI API to retrieve the
   * <code>DataSource</code> object that it can use to make the connection.
   * If the url, username, and password properties have been set, this
   * method uses the <code>DriverManager.getConnection</code> method to
   * make the connection.
   * <P>
   * This method is used internally by the reader and writer associated with
   * the calling <code>RowSet</code> object; an application never calls it
   * directly.
   *
   * @param caller a <code>RowSet</code> object that has implemented
   *               the <code>RowSetInternal</code> interface and had
   *               this <code>CachedRowSetReader</code> object set as
   *               its reader
   * @return a <code>Connection</code> object that represents a connection
   *         to the caller's data source
   * @throws SQLException if an access error occurs
   */
  public Connection connect(RowSetInternal caller) throws SQLException {

    // Get a JDBC connection.
    if (caller.getConnection() != null) {
      // A connection was passed to execute(), so use it.
      // As we are using a connection the user gave us we
      // won't close it.
      userCon = true;
      return caller.getConnection();
    } else if (((RowSet) caller).getDataSourceName() != null) {
      // Connect using JNDI.
      try {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup(((RowSet) caller).getDataSourceName());

        // Check for username, password,
        // if it exists try getting a Connection handle through them
        // else try without these
        // else throw SQLException

        if (((RowSet) caller).getUsername() != null) {
          return ds.getConnection(((RowSet) caller).getUsername(),
                  ((RowSet) caller).getPassword());
        } else {
          return ds.getConnection();
        }
      } catch (javax.naming.NamingException ex) {
        SQLException sqlEx = new SQLException(resBundle.handleGetObject("crsreader.connect").toString());
        sqlEx.initCause(ex);
        throw sqlEx;
      }
    } else if (((RowSet) caller).getUrl() != null) {
      // Connect using the driver manager.
      return DriverManager.getConnection(((RowSet) caller).getUrl(),
              ((RowSet) caller).getUsername(),
              ((RowSet) caller).getPassword());
    } else {
      return null;
    }
  }

  /**
   * Sets the parameter placeholders
   * in the rowset's command (the given <code>PreparedStatement</code>
   * object) with the parameters in the given array.
   * This method, called internally by the method
   * <code>CachedRowSetReader.readData</code>, reads each parameter, and
   * based on its type, determines the correct
   * <code>PreparedStatement.setXXX</code> method to use for setting
   * that parameter.
   *
   * @param params an array of parameters to be used with the given
   *               <code>PreparedStatement</code> object
   * @param pstmt  the <code>PreparedStatement</code> object that is the
   *               command for the calling rowset and into which
   *               the given parameters are to be set
   * @throws SQLException if an access error occurs
   */
  private void decodeParams(Object[] params,
          PreparedStatement pstmt) throws SQLException {
    // There is a corresponding decodeParams in JdbcRowSetImpl
    // which does the same as this method. This is a design flaw.
    // Update the JdbcRowSetImpl.decodeParams when you update
    // this method.

    // Adding the same comments to JdbcRowSetImpl.decodeParams.

    int arraySize;
    Object[] param = null;

    for (int i = 0; i < params.length; i++) {
      if (params[i] instanceof Object[]) {
        param = (Object[]) params[i];

        if (param.length == 2) {
          if (param[0] == null) {
            pstmt.setNull(i + 1, ((Integer) param[1]).intValue());
            continue;
          }

          if (param[0] instanceof java.sql.Date ||
                  param[0] instanceof java.sql.Time ||
                  param[0] instanceof java.sql.Timestamp) {
            System.err.println(resBundle.handleGetObject("crsreader.datedetected").toString());
            if (param[1] instanceof java.util.Calendar) {
              System.err.println(resBundle.handleGetObject("crsreader.caldetected").toString());
              pstmt.setDate(i + 1, (java.sql.Date) param[0],
                      (java.util.Calendar) param[1]);
              continue;
            } else {
              throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());
            }
          }

          if (param[0] instanceof Reader) {
            pstmt.setCharacterStream(i + 1, (Reader) param[0],
                    ((Integer) param[1]).intValue());
            continue;
          }

          /*
           * What's left should be setObject(int, Object, scale)
           */
          if (param[1] instanceof Integer) {
            pstmt.setObject(i + 1, param[0], ((Integer) param[1]).intValue());
            continue;
          }

        } else if (param.length == 3) {

          if (param[0] == null) {
            pstmt.setNull(i + 1, ((Integer) param[1]).intValue(),
                    (String) param[2]);
            continue;
          }

          if (param[0] instanceof java.io.InputStream) {
            switch (((Integer) param[2]).intValue()) {
              case CachedRowSetImpl.UNICODE_STREAM_PARAM:
                pstmt.setUnicodeStream(i + 1,
                        (java.io.InputStream) param[0],
                        ((Integer) param[1]).intValue());
              case CachedRowSetImpl.BINARY_STREAM_PARAM:
                pstmt.setBinaryStream(i + 1,
                        (java.io.InputStream) param[0],
                        ((Integer) param[1]).intValue());
              case CachedRowSetImpl.ASCII_STREAM_PARAM:
                pstmt.setAsciiStream(i + 1,
                        (java.io.InputStream) param[0],
                        ((Integer) param[1]).intValue());
              default:
                throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());
            }
          }

          /*
           * no point at looking at the first element now;
           * what's left must be the setObject() cases.
           */
          if (param[1] instanceof Integer && param[2] instanceof Integer) {
            pstmt.setObject(i + 1, param[0], ((Integer) param[1]).intValue(),
                    ((Integer) param[2]).intValue());
            continue;
          }

          throw new SQLException(resBundle.handleGetObject("crsreader.paramtype").toString());

        } else {
          // common case - this catches all SQL92 types
          pstmt.setObject(i + 1, params[i]);
          continue;
        }
      } else {
        // Try to get all the params to be set here
        pstmt.setObject(i + 1, params[i]);

      }
    }
  }

  /**
   * Assists in determining whether the current connection was created by this
   * CachedRowSet to ensure incorrect connections are not prematurely terminated.
   *
   * @return a boolean giving the status of whether the connection has been closed.
   */
  protected boolean getCloseConnection() {
    if (userCon) {
      return false;
    }

    return true;
  }

  /**
   *  This sets the start position in the ResultSet from where to begin. This is
   * called by the Reader in the CachedRowSetImpl to set the position on the page
   * to begin populating from.
   * @param pos integer indicating the position in the <code>ResultSet</code> to begin
   *        populating from.
   */
  public void setStartPosition(int pos) {
    startPosition = pos;
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
  static final long serialVersionUID = 5049738185801363801L;
}
