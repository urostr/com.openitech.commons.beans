/*
 * @(#)WebRowSetImpl.java	1.11 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.openitech.db.model.web;

import com.openitech.db.model.DbClobImpl;
import com.openitech.db.model.sync.DbChachedRowSetImpl;
import com.openitech.db.model.sync.DbEventSyncProvider;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.sql.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.sql.RowSetMetaData;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.*;

/**
 * The standard implementation of the <code>WebRowSet</code> interface. See the interface
 * defintion for full behaviour and implementation requirements.
 *
 * @author Jonathan Bruce, Amit Handa
 */
public class DbWebRowSetImpl extends DbChachedRowSetImpl implements WebRowSet {

    /**
     * The <code>WebRowSetXmlReader</code> object that this
     * <code>WebRowSet</code> object will call when the method
     * <code>WebRowSet.readXml</code> is invoked.
     */
    private DbWebRowSetXmlReader xmlReader;
    /**
     * The <code>WebRowSetXmlWriter</code> object that this
     * <code>WebRowSet</code> object will call when the method
     * <code>WebRowSet.writeXml</code> is invoked.
     */
    private DbWebRowSetXmlWriter xmlWriter;

    /* This stores the cursor position prior to calling the writeXML.
     * This variable is used after the write to restore the position
     * to the point where the writeXml was called.
     */
    private int curPosBfrWrite;
    private SyncProvider provider;

    /**
     * Constructs a new <code>WebRowSet</code> object initialized with the
     * default values for a <code>CachedRowSet</code> object instance. This
     * provides the <code>RIOptimistic</code> provider to deliver
     * synchronization capabilities to relational datastores and a default
     * <code>WebRowSetXmlReader</code> object and a default
     * <code>WebRowSetXmlWriter</code> object to enable XML output
     * capabilities.
     *
     * @throws SQLException if an error occurs in configuring the default
     * synchronization providers for relational and XML providers.
     */
    public DbWebRowSetImpl() throws SQLException {
        super();

        SyncFactory.registerProvider(DbEventSyncProvider.PROVIDER);

        String providerName = DbEventSyncProvider.PROVIDER;


        // set the Reader, this maybe overridden latter
        provider = (SyncProvider) SyncFactory.getInstance(providerName);
        setSyncProvider(providerName);
        // %%%
        // Needs to use to SPI  XmlReader,XmlWriters
        //
        xmlReader = new DbWebRowSetXmlReader();
        xmlWriter = new DbWebRowSetXmlWriter();
    }

    /**
     * Constructs a new <code>WebRowSet</code> object initialized with the the
     * synchronization SPI provider properties as specified in the <code>Hashtable</code>. If
     * this hashtable is empty or is <code>null</code> the default constructor is invoked.
     *
     * @throws SQLException if an error occurs in configuring the specified
     * synchronization providers for the relational and XML providers; or
     * if the Hashtanle is null
     */
    public DbWebRowSetImpl(Hashtable env) throws SQLException {
        super();
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        if (env == null) {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.nullhash").toString());
        }

        String providerName =
                (String) env.get(javax.sql.rowset.spi.SyncFactory.ROWSET_SYNC_PROVIDER);

        SyncFactory.registerProvider(providerName);

        // set the Reader, this maybe overridden latter
        provider = (SyncProvider) SyncFactory.getInstance(providerName);
        setSyncProvider(provider);

        xmlReader = new DbWebRowSetXmlReader();
        xmlWriter = new DbWebRowSetXmlWriter();
    }

    /**
     * Populates this <code>WebRowSet</code> object with the
     * data in the given <code>ResultSet</code> object and writes itself
     * to the given <code>java.io.Writer</code> object in XML format.
     * This includes the rowset's data,  properties, and metadata.
     *
     * @throws SQLException if an error occurs writing out the rowset
     *          contents to XML
     */
    @Override
    public void writeXml(ResultSet rs, java.io.Writer writer)
            throws SQLException {
        // WebRowSetImpl wrs = new WebRowSetImpl();
        this.populate(rs);

        // Store the cursor position before writing
        curPosBfrWrite = this.getRow();

        this.writeXml(writer);
    }

    /**
     * Writes this <code>WebRowSet</code> object to the given
     * <code>java.io.Writer</code> object in XML format. This
     * includes the rowset's data,  properties, and metadata.
     *
     * @throws SQLException if an error occurs writing out the rowset
     * 		contents to XML
     */
    @Override
    public void writeXml(java.io.Writer writer) throws SQLException {
        // %%%
        // This will change to a XmlReader, which over-rides the default
        // Xml that is used when a WRS is instantiated.
        // WebRowSetXmlWriter xmlWriter = getXmlWriter();
        if (xmlWriter != null) {

            // Store the cursor position before writing
            curPosBfrWrite = this.getRow();

            xmlWriter.writeXML(this, writer);
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
        }
    }

    public void readXml(com.openitech.xml.wrs.WebRowSet webRowSet) throws SQLException {
        int columnCount = webRowSet.getColumnCount();
        RowSetMetaData md = new RowSetMetaDataImpl();
        md.setColumnCount(columnCount);
        int colIndex = 1;
        for (com.openitech.xml.wrs.WebRowSet.Column column : webRowSet.getColumn()) {
            md.setColumnName(colIndex, column.getName());
            md.setColumnType(colIndex, column.getType());

            colIndex++;
        }
        setMetaData(md);
        //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Number of rows = " + webRowSet.getRow().size());
        for (com.openitech.xml.wrs.WebRowSet.Row row : webRowSet.getRow()) {


            colIndex = 1;
            moveToInsertRow();
            for (Object object : row.getValue()) {

                if (object == null || (object instanceof String && ((String) object).equalsIgnoreCase("null"))) {
                    updateNull(colIndex);

                    colIndex++;
                    continue;
                }
                int type = getMetaData().getColumnType(colIndex);
                //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Tip=" + type + " object=" + object);
                switch (type) {
                    case java.sql.Types.BOOLEAN:
                        updateBoolean(colIndex, (Boolean) object);
                        break;
                    case java.sql.Types.BIT:

                    case java.sql.Types.INTEGER:
                        updateInt(colIndex, (Integer) object);
                        break;
                    case java.sql.Types.BIGINT:
                        updateLong(colIndex, ((BigInteger) object).longValue());
                        break;
                    case java.sql.Types.REAL:
                    case java.sql.Types.FLOAT:
                    case java.sql.Types.DOUBLE:
                        updateDouble(colIndex, (Double) object);
                        break;
                    case java.sql.Types.NUMERIC:
                    case java.sql.Types.DECIMAL:
                        break;
                    case java.sql.Types.BINARY:
                    case java.sql.Types.VARBINARY:
                    case java.sql.Types.LONGVARBINARY:
//        updateBytes(colIndex, getBinaryValue(s));
                        break;
                    case java.sql.Types.DATE:
                    case java.sql.Types.TIME:
                    case java.sql.Types.TIMESTAMP:
                        updateTimestamp(colIndex, new Timestamp((Long) object));
                        break;
                    case java.sql.Types.CHAR:
                    case java.sql.Types.VARCHAR:
                    case java.sql.Types.LONGVARCHAR:
                        updateString(colIndex, (String) object);
                        break;
                    case java.sql.Types.CLOB:
                        updateClob(colIndex, new DbClobImpl((String) object));
                        //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Updating clob with value: " + ((String) object));
                        break;

                    default:
                }
                colIndex++;
            }
            insertRow();
            moveToCurrentRow();
            next();

            setOriginalRow();
        }
        first();
    }

    /**
     * Reads this <code>WebRowSet</code> object in its XML format.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void readXml(java.io.Reader reader) throws SQLException {
        // %%%
        // This will change to a XmlReader, which over-rides the default
        // Xml that is used when a WRS is instantiated.
        //WebRowSetXmlReader xmlReader = getXmlReader();
        try {
            if (reader != null) {
                xmlReader.readXML(this, reader);

                // Position is before the first row
                // The cursor position is to be stored while serializng
                // and deserializing the WebRowSet Object.
                if (curPosBfrWrite == 0) {
                    this.beforeFirst();
                } // Return the position back to place prior to callin writeXml
                else {
                    this.absolute(curPosBfrWrite);
                }

            } else {
                throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
            }
        } catch (Exception e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, e.getMessage(), e);
            throw new SQLException(e.getMessage());
        }
    }

    // Stream based methods
    /**
     * Reads a stream based XML input to populate this <code>WebRowSet</code>
     * object.
     *
     * @throws SQLException if a data source access error occurs
     * @throws IOException if a IO exception occurs
     */
    @Override
    public void readXml(java.io.InputStream iStream) throws SQLException, IOException {
        if (iStream != null) {
            xmlReader.readXML(this, iStream);

            // Position is before the first row
            // The cursor position is to be stored while serializng
            // and deserializing the WebRowSet Object.
            if (curPosBfrWrite == 0) {
                this.beforeFirst();
            } // Return the position back to place prior to callin writeXml
            else {
                this.absolute(curPosBfrWrite);
            }

        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
        }
    }

    /**
     * Writes this <code>WebRowSet</code> object to the given <code> OutputStream</code>
     * object in XML format.
     * Creates an an output stream of the internal state and contents of a
     * <code>WebRowSet</code> for XML proceessing
     *
     * @throws SQLException if a datasource access error occurs
     * @throws IOException if an IO exception occurs
     */
    @Override
    public void writeXml(java.io.OutputStream oStream) throws SQLException, IOException {
        if (xmlWriter != null) {

            // Store the cursor position before writing
            curPosBfrWrite = this.getRow();

            xmlWriter.writeXML(this, oStream);
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
        }

    }

    /**
     * Populates this <code>WebRowSet</code> object with the
     * data in the given <code>ResultSet</code> object and writes itself
     * to the given <code>java.io.OutputStream</code> object in XML format.
     * This includes the rowset's data,  properties, and metadata.
     *
     * @throws SQLException if a datasource access error occurs
     * @throws IOException if an IO exception occurs
     */
    @Override
    public void writeXml(ResultSet rs, java.io.OutputStream oStream) throws SQLException, IOException {
        this.populate(rs);

        // Store the cursor position before writing
        curPosBfrWrite = this.getRow();

        this.writeXml(oStream);
    }

    /**
     * This method re populates the resBundle
     * during the deserialization process
     *
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Default state initialization happens here
        ois.defaultReadObject();
        // Initialization of transient Res Bundle happens here .
        try {
            resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }
    static final long serialVersionUID = -8771775154092422943L;
    private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name.replaceAll(":", "_").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll("\\-", "_");
    }
}
