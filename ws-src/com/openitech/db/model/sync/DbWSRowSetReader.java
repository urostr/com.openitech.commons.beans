/*
 * @(#)CachedRowSetReader.java	1.13 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.openitech.db.model.sync;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.web.DbWebRowSetImpl;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.io.*;

import com.openitech.xml.wrs.WSParameters;
import com.sun.rowset.*;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.*;
import javax.ws.rs.core.MediaType;

/**
 * The facility called by the <code>DbEventRowSetProvider</code> object
 * internally to read data into it.  The calling <code>RowSet</code> object
 * must have implemented the <code>RowSetInternal</code> interface
 * and have the standard <code>CachedRowSetReader</code> object set as its
 * reader.
 * <P>
 * This implementation always reads all rows of the data source,
 * and it assumes that the database has stored procedure <code>getEventXMLRowSet</code>.
 * <P>
 * Typically the <code>SyncFactory</code> manages the <code>RowSetReader</code> and
 * the <code>RowSetWriter</code> implementations using <code>SyncProvider</code> objects.
 * Standard JDBC RowSet implementations provide an object instance of this
 * reader by invoking the <code>SyncProvider.getRowSetReader()</code> method.
 *
 * This implementation does not use <code>command</code> property, but uses pre-created stored procedure
 * which returns standard webRowSet xml for <code>WebRowSet</code> which populates itself with that data.
 *
 * <P>
 * Store procedure requires tree parameters, passed by webRowSet:
 *     <code>EventID</code>
 *     <code>IDSifranta</code>
 *     <code>IDSifre</code>
 * 
 * @version 1.0
 * @author Domen Ba�i�
 * @see javax.sql.rowset.spi.SyncProvider
 * @see javax.sql.rowset.spi.SyncFactory
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class DbWSRowSetReader implements RowSetReader, Serializable {

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
    private int startPosition;
//    private SQLCache sqlCache = new SQLCache();
    private JdbcRowSetResourceBundle resBundle;
    private com.sun.jersey.api.client.WebResource webResource;
    private com.sun.jersey.api.client.Client client;
//    private static final String RESOURCE_URI = "http://localhost:8081/DogodkiWA/resources/secondary";

    // private Connection connection;
    public DbWSRowSetReader() {
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
        if (client == null) {
            client = new com.sun.jersey.api.client.Client();
            webResource = client.resource(ConnectionManager.getInstance().getProperty(ConnectionManager.DB_SECONDARY_WS));
        }
        try {
            WebRowSet wrs = (WebRowSet) caller;


            writerCalls = 0;

            WSParameters wsParameters = null;
            for (Object value : caller.getParams()) {
                wsParameters = ((WSParameters) value);
            }
            com.openitech.xml.wrs.WebRowSet rowSet = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(com.openitech.xml.wrs.WebRowSet.class, wsParameters);

            if (wrs instanceof DbWebRowSetImpl) {
                ((DbWebRowSetImpl) wrs).readXml(rowSet);
            }


        } catch (SQLException ex) {
            // Throw an exception if reading fails for any reason.
            throw ex;
        }
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
     * Assists in determining whether the current connection was created by this
     * CachedRowSet to ensure incorrect connections are not prematurely terminated.
     *
     * @return a boolean giving the status of whether the connection has been closed.
     */
    protected boolean getCloseConnection() {
        return false;
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
    static final long serialVersionUID = 5049738185801363801L;
}
