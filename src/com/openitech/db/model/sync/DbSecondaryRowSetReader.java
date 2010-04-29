/*
 * @(#)CachedRowSetReader.java	1.13 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.openitech.db.model.sync;

import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.db.model.sql.SQLCache;
import java.sql.*;
import java.util.logging.Level;
import javax.sql.*;
import javax.naming.*;
import java.io.*;

import com.openitech.db.model.sql.SQLDataSource;
import com.openitech.sql.Field;
import com.openitech.sql.events.EventQuery;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.sql.util.mssql.SqlUtilitesImpl;
import com.sun.rowset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
public class DbSecondaryRowSetReader implements RowSetReader, Serializable {

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
  private SQLCache sqlCache = new SQLCache();
  private JdbcRowSetResourceBundle resBundle;
  // private Connection connection;

  public DbSecondaryRowSetReader() {
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
      con = connect(caller);

      // Check our assumptions.
      if (con == null) {//|| crs.getCommand() == null) {
        throw new SQLException(resBundle.handleGetObject("crsreader.connecterr").toString());
      }

      try {
        con.setTransactionIsolation(wrs.getTransactionIsolation());
      } catch (Exception ex) {
      }

      //create SP najprej


      EventQuery eq = null;
      for (Object value : caller.getParams()) {
        eq = ((EventQuery) value);

      }

      //  dropProcedure(con);
      String procedureName = createSecondarySP(eq, con, wrs.toString());



      //poklièi SP
      String parametri = "";
      int steviloParametrov = 0;
      List<SqlParameter> param = new ArrayList<SqlParameter>();
      for (Map.Entry entry : eq.getNamedParameters().entrySet()) {
        param.add((SqlParameter) entry.getValue());
        parametri += " ?,";
        steviloParametrov++;
      }
      if (steviloParametrov > 0) {
        parametri = parametri.substring(0, parametri.length() - 1);//pobrišem zadnjo vejco
      }
      String exePrecedure = " [dbo].[" + procedureName + "] ( " + parametri + " ) ";
      Logger.getAnonymousLogger().info(exePrecedure);
      PreparedStatement pstmt = sqlCache.getSharedCall(con, "{ call " + exePrecedure + " }");

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

        String xml = rs.getString(1);
//        System.out.println(test);

        Logger.getAnonymousLogger().warning(xml);
        if (xml.length() > 0) {
          wrs.release();
          wrs.clearParameters();
          wrs.readXml(new StringReader(xml));
        }

      } else {
        throw new SQLException("Ni podatkov");
      }
      rs.close();
      // Get the data.
      //pstmt.close();
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

  private String createSecondarySP(EventQuery eq, Connection conn, String imeProcedure) {
    String parametri = "";
    int steviloParametrov = 0;
    List<String> param = new ArrayList<String>();
    param.add(Integer.toString(eq.getSifrant()));
    if (eq.getSifra() != null) {
      param.add(eq.getSifra());
    }
    for (Map.Entry entry : eq.getNamedParameters().entrySet()) {
      Field field = (Field) entry.getKey();
      parametri += "@" + field.getName() + "  " + typeToString(field.getType()) + " ,";
      param.add("@" + field.getName());
      steviloParametrov++;
    }
    if (steviloParametrov > 0) {
      parametri = parametri.substring(0, parametri.length() - 1);//pobrišem zadnjo vejco
    }

    String sql = eq.getQuery();
    sql = SQLDataSource.substParameters(sql, eq.getParameters());//mi pusti vprašaje
    sql = substParameters(sql, param);


    String preparedSQL =
            //            "USE [ChangeLog]  " +
            //            "GO ; " +
            //            /****** Object:  UserDefinedFunction [dbo].[getSecondaryXMLRowSet]    Script Date: 04/22/2010 09:33:23 ******/
            //            "SET ANSI_NULLS ON  " +
            //            "GO  " +
            //            "SET QUOTED_IDENTIFIER ON " +
            //            "GO  " +
            " CREATE PROCEDURE [dbo].[" + imeProcedure + "] (  " +
            parametri +
            //            "	@idSifranta [int], " +
            //            "	@idPrivolitveneIzjave [varchar] (100), " +
            //            "	@idPP [int] " +
            ")  " +
            "AS  " +
            "BEGIN " +
            "DECLARE @xmlWRS [xml]; " +
            "	WITH XMLNAMESPACES ('http://java.sun.com/xml/ns/jdbc' as ns0) " +
            "	SELECT @xmlWRS=(SELECT (null) " +
            "	FOR XML RAW('ns0:webRowSet'), ELEMENTS); " +
            "	SET @xmlWRS.modify(' " +
            "		declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "						insert  " +
            "							<ns0:properties> " +
            "								<ns0:command>null</ns0:command> " +
            "								<ns0:concurrency>1008</ns0:concurrency> " +
            "								<ns0:datasource>null</ns0:datasource> " +
            "								<ns0:escape-processing>true</ns0:escape-processing> " +
            "								<ns0:fetch-direction>1000</ns0:fetch-direction> " +
            "								<ns0:fetch-size>0</ns0:fetch-size> " +
            "								<ns0:isolation-level>2</ns0:isolation-level> " +
            "								<ns0:key-columns> " +
            "								</ns0:key-columns> " +
            "								<ns0:map> " +
            "								</ns0:map> " +
            "								<ns0:max-field-size>0</ns0:max-field-size> " +
            "								<ns0:max-rows>0</ns0:max-rows> " +
            "								<ns0:query-timeout>0</ns0:query-timeout> " +
            "								<ns0:read-only>true</ns0:read-only> " +
            "								<ns0:rowset-type>ResultSet.TYPE_SCROLL_INSENSITIVE</ns0:rowset-type> " +
            "               <ns0:show-deleted>false</ns0:show-deleted> " +
            "   						<ns0:table-name>null</ns0:table-name> " +
            "								<ns0:url>null</ns0:url> " +
            "								<ns0:sync-provider> " +
            "									<ns0:sync-provider-name>com.openitech.db.model.sync.DbSecondarySyncProvider</ns0:sync-provider-name> " +
            "									<ns0:sync-provider-vendor>Domen</ns0:sync-provider-vendor> " +
            "									<ns0:sync-provider-version>1.0</ns0:sync-provider-version> " +
            "									<ns0:sync-provider-grade>2</ns0:sync-provider-grade> " +
            "									<ns0:data-source-lock>1</ns0:data-source-lock> " +
            "								</ns0:sync-provider> " +
            "							</ns0:properties> " +
            "						as first " +
            "						into   (/ns0:webRowSet)[1] " +
            "					'); " +
            "	SET @xmlWRS.modify(' " +
            "		declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "						insert  " +
            "				<ns0:metadata/> " +
            "						as last  " +
            "						into   (/ns0:webRowSet)[1] " +
            "					'); " +
            "	SET @xmlWRS.modify(' " +
            "		declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "						insert  " +
            "							<ns0:column-count>6</ns0:column-count> " +
            "						as last  " +
            "						into   (/ns0:webRowSet/ns0:metadata)[1] " +
            "					'); " +
            "	SET @xmlWRS.modify(' " +
            "		declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "						insert  " +
            "				<ns0:data/> " +
            "						as last  " +
            "						into   (/ns0:webRowSet)[1] " +
            "					'); " +
            "					SET @xmlWRS.modify(' " +
            "		declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "						insert  " +
            "				<ns0:currentRow/> " +
            "						as last  " +
            "						into   (/ns0:webRowSet/ns0:data)[1] " +
            "					'); " +
            "	DECLARE	 " +
            "			@ID [int], " +
            "			@EventID [int], " +
            "			@IDSifranta [int], " +
            "			@IDSifre [varchar](100), " +
            "			@IDEventSource [int], " +
            "			@datum [varchar](108) " +
            "	DECLARE eventValues_cursor  CURSOR FAST_FORWARD FOR " +
            sql +
            "	OPEN eventValues_cursor " +
            "	FETCH NEXT FROM eventValues_cursor " +
            "	INTO " +
            "			@ID , " +
            "			@EventID , " +
            "			@IDSifranta, " +
            "			@IDSifre , " +
            "			@IDEventSource , " +
            "			@datum  " +
            /*ID*/
            "SET @xmlWRS.modify(' " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\"; " +
            "				insert  " +
            "					<ns0:column-definition> " +
            "						<ns0:column-index>1</ns0:column-index> " +
            "						<ns0:auto-increment>false</ns0:auto-increment> " +
            "						<ns0:case-sensitive>false</ns0:case-sensitive> " +
            "						<ns0:currency>false</ns0:currency> " +
            "						<ns0:nullable>0</ns0:nullable> " +
            "						<ns0:signed>true</ns0:signed> " +
            "						<ns0:searchable>true</ns0:searchable> " +
            "						<ns0:column-display-size>11</ns0:column-display-size> " +
            "						<ns0:column-label>ID</ns0:column-label> " +
            "						<ns0:column-name>ID</ns0:column-name>  " +
            "						<ns0:schema-name></ns0:schema-name>  " +
            "						<ns0:column-precision>10</ns0:column-precision>  " +
            "						<ns0:column-scale>0</ns0:column-scale>  " +
            "						<ns0:table-name></ns0:table-name>  " +
            "						<ns0:catalog-name></ns0:catalog-name>  " +
            "						<ns0:column-type>4</ns0:column-type>  " +
            "						<ns0:column-type-name>int</ns0:column-type-name>  " +
            "					</ns0:column-definition>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "			');  " +
            "			SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "				<ns0:columnValue>{ sql:variable(\"@id\") }</ns0:columnValue>			  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            /*EventID*/
            "	SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "					<ns0:column-definition>  " +
            "						<ns0:column-index>2</ns0:column-index>  " +
            "						<ns0:auto-increment>false</ns0:auto-increment>  " +
            "						<ns0:case-sensitive>false</ns0:case-sensitive>  " +
            "						<ns0:currency>false</ns0:currency>  " +
            "						<ns0:nullable>0</ns0:nullable>  " +
            "						<ns0:signed>true</ns0:signed>  " +
            "						<ns0:searchable>true</ns0:searchable>  " +
            "						<ns0:column-display-size>11</ns0:column-display-size>  " +
            "						<ns0:column-label>EventID</ns0:column-label>  " +
            "						<ns0:column-name>EventID</ns0:column-name>  " +
            "						<ns0:schema-name></ns0:schema-name>  " +
            "						<ns0:column-precision>10</ns0:column-precision>  " +
            "						<ns0:column-scale>0</ns0:column-scale>  " +
            "						<ns0:table-name></ns0:table-name>  " +
            "						<ns0:catalog-name></ns0:catalog-name>  " +
            "						<ns0:column-type>4</ns0:column-type>  " +
            "						<ns0:column-type-name>int</ns0:column-type-name>  " +
            "					</ns0:column-definition>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "			');  " +
            "			SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "				<ns0:columnValue>{ sql:variable(\"@eventid\") }</ns0:columnValue>			  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            /*IDSifranta*/
            "	SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "					<ns0:column-definition>  " +
            "						<ns0:column-index>3</ns0:column-index>  " +
            "						<ns0:auto-increment>false</ns0:auto-increment>  " +
            "						<ns0:case-sensitive>false</ns0:case-sensitive>  " +
            "						<ns0:currency>false</ns0:currency>  " +
            "						<ns0:nullable>0</ns0:nullable>  " +
            "						<ns0:signed>true</ns0:signed>  " +
            "						<ns0:searchable>true</ns0:searchable>  " +
            "						<ns0:column-display-size>11</ns0:column-display-size>  " +
            "						<ns0:column-label>IDSifranta</ns0:column-label>  " +
            "						<ns0:column-name>IDSifranta</ns0:column-name>  " +
            "						<ns0:schema-name></ns0:schema-name>  " +
            "						<ns0:column-precision>10</ns0:column-precision>  " +
            "						<ns0:column-scale>0</ns0:column-scale>  " +
            "						<ns0:table-name></ns0:table-name>  " +
            "						<ns0:catalog-name></ns0:catalog-name>  " +
            "						<ns0:column-type>4</ns0:column-type>  " +
            "						<ns0:column-type-name>int</ns0:column-type-name>  " +
            "					</ns0:column-definition>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "			');  " +
            "			SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDSifranta\") }</ns0:columnValue>			  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            "			/*IDSifre*/	  " +
            "	SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "					<ns0:column-definition>  " +
            "							<ns0:column-index>4</ns0:column-index>  " +
            "							<ns0:auto-increment>false</ns0:auto-increment>  " +
            "							<ns0:case-sensitive>false</ns0:case-sensitive>  " +
            "							<ns0:currency>false</ns0:currency>  " +
            "							<ns0:nullable>1</ns0:nullable>  " +
            "							<ns0:signed>false</ns0:signed>  " +
            "							<ns0:searchable>true</ns0:searchable>  " +
            "							<ns0:column-display-size>30</ns0:column-display-size>  " +
            "							<ns0:column-label>IDSifre</ns0:column-label>  " +
            "							<ns0:column-name>IDSifre</ns0:column-name>  " +
            "							<ns0:schema-name></ns0:schema-name>  " +
            "							<ns0:column-precision>30</ns0:column-precision>  " +
            "							<ns0:column-scale>0</ns0:column-scale>  " +
            "							<ns0:table-name></ns0:table-name>  " +
            "							<ns0:catalog-name></ns0:catalog-name>  " +
            "							<ns0:column-type>12</ns0:column-type>  " +
            "							<ns0:column-type-name>varchar</ns0:column-type-name>  " +
            "						</ns0:column-definition>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "			');  " +
            "			SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDSifre\") }</ns0:columnValue>			  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            /*IDEventSource*/
            "	SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "					<ns0:column-definition>  " +
            "						<ns0:column-index>5</ns0:column-index>  " +
            "						<ns0:auto-increment>false</ns0:auto-increment>  " +
            "						<ns0:case-sensitive>false</ns0:case-sensitive>  " +
            "						<ns0:currency>false</ns0:currency>  " +
            "						<ns0:nullable>0</ns0:nullable>  " +
            "						<ns0:signed>true</ns0:signed>  " +
            "						<ns0:searchable>true</ns0:searchable>  " +
            "						<ns0:column-display-size>11</ns0:column-display-size>  " +
            "						<ns0:column-label>IDEventSource</ns0:column-label>  " +
            "						<ns0:column-name>IDEventSource</ns0:column-name>  " +
            "						<ns0:schema-name></ns0:schema-name>  " +
            "						<ns0:column-precision>10</ns0:column-precision>  " +
            "						<ns0:column-scale>0</ns0:column-scale>  " +
            "						<ns0:table-name></ns0:table-name>  " +
            "						<ns0:catalog-name></ns0:catalog-name>  " +
            "						<ns0:column-type>4</ns0:column-type>  " +
            "						<ns0:column-type-name>int</ns0:column-type-name>  " +
            "					</ns0:column-definition>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "			');  " +
            "			SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDEventSource\") }</ns0:columnValue>			  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            "	SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "					<ns0:column-definition>  " +
            "						<ns0:column-index>6</ns0:column-index>  " +
            "						<ns0:auto-increment>false</ns0:auto-increment>  " +
            "						<ns0:case-sensitive>false</ns0:case-sensitive>  " +
            "						<ns0:currency>false</ns0:currency>  " +
            "						<ns0:nullable>1</ns0:nullable>  " +
            "						<ns0:signed>false</ns0:signed>  " +
            "						<ns0:searchable>true</ns0:searchable>  " +
            "						<ns0:column-display-size>23</ns0:column-display-size>  " +
            "						<ns0:column-label>Datum</ns0:column-label>  " +
            "						<ns0:column-name>Datum</ns0:column-name>  " +
            "						<ns0:schema-name>dbo</ns0:schema-name>  " +
            "						<ns0:column-precision>23</ns0:column-precision>  " +
            "						<ns0:column-scale>3</ns0:column-scale>  " +
            "						<ns0:table-name>Events</ns0:table-name>  " +
            "						<ns0:catalog-name>ChangeLog</ns0:catalog-name>  " +
            "						<ns0:column-type>93</ns0:column-type>  " +
            "						<ns0:column-type-name>datetime</ns0:column-type-name>  " +
            "					</ns0:column-definition>  " +
            "				as last   " +
            "				into   (/ns0:webRowSet/ns0:metadata)[1]  " +
            "				');  " +
            "			SET @xmlWRS.modify('  " +
            "				declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "				insert   " +
            "				<ns0:columnValue>{ sql:variable(\"@datum\") }</ns0:columnValue>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data/ns0:currentRow)[1]  " +
            "			');  " +
            "	FETCH NEXT FROM eventValues_cursor  " +
            "	INTO  " +
            "			@ID ,  " +
            "			@EventID ,  " +
            "			@IDSifranta, " +
            "			@IDSifre ,  " +
            "			@IDEventSource ,  " +
            "			@datum   " +
            "    WHILE @@FETCH_STATUS = 0  " +
            "	BEGIN  " +
            "	SET @xmlWRS.modify('  " +
            "			declare namespace  ns0=\"http://java.sun.com/xml/ns/jdbc\";  " +
            "			insert 			  " +
            "			<ns0:currentRow>  " +
            "				<ns0:columnValue>{ sql:variable(\"@ID\") }</ns0:columnValue>			  " +
            "				<ns0:columnValue>{ sql:variable(\"@EventID\") }</ns0:columnValue>  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDSifranta\") }</ns0:columnValue>			  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDSifre\") }</ns0:columnValue>			  " +
            "				<ns0:columnValue>{ sql:variable(\"@IDEventSource\") }</ns0:columnValue>			  " +
            "				<ns0:columnValue>{ sql:variable(\"@datum\") }</ns0:columnValue>  " +
            "			</ns0:currentRow>  " +
            "			as last   " +
            "			into   (/ns0:webRowSet/ns0:data)[1]  " +
            "			');  " +
            "	FETCH NEXT FROM eventValues_cursor  " +
            "	INTO  " +
            "			@ID ,  " +
            "			@EventID ,  " +
            "			@IDSifranta, " +
            "			@IDSifre ,  " +
            "			@IDEventSource ,  " +
            "			@datum   " +
            "	END  " +
            "	CLOSE eventValues_cursor  " +
            "	DEALLOCATE eventValues_cursor	  " +
            "	/*SELECT CAST(@xmlWRS AS [xml](collectionXMLEvents));  */ " +
            " SELECT CAST(@xmlWRS AS [xml]);  " +
            "END";

    //Logger.getAnonymousLogger().log(Level.INFO, preparedSQL);
    String result = null;
    int par;
    try {
//
      String findSQL = "SELECT Id, ImeProcedure, Procedura FROM ChangeLog.dbo.StoredProcedures WHERE ImeProcedure = ?";
      PreparedStatement findProcedure = sqlCache.getSharedStatement(conn, findSQL);
     
      par = 1;
      findProcedure.setString(par++, imeProcedure);

      ResultSet rsFindProcedure = findProcedure.executeQuery();
      if (rsFindProcedure.next()) {
        result = rsFindProcedure.getString("ImeProcedure");
      } else {

        boolean commit = false;

        try {
          SqlUtilities.getInstance().beginTransaction();
          Statement statement = conn.createStatement();
          statement.executeUpdate(preparedSQL);
          result = imeProcedure;

          par = 1;
          String insertSQL = "INSERT INTO ChangeLog.dbo.StoredProcedures ([ImeProcedure], Procedura) VALUES ( ?, ?)";
          PreparedStatement insertProcedure = sqlCache.getSharedStatement(conn, insertSQL);

          insertProcedure.setString(par++, imeProcedure);
          insertProcedure.setString(par++, preparedSQL);
          insertProcedure.executeUpdate();

          commit = true;
        } catch (SQLException ex) {
          Logger.getLogger(DbSecondaryRowSetReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
          SqlUtilities.getInstance().endTransaction(commit);
        }
      }

    } catch (SQLException ex) {
      Logger.getLogger(DbSecondaryRowSetReader.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

  private String typeToString(int type) {
    switch (type) {
      case java.sql.Types.VARCHAR:
        return "varchar (MAX)";
      case java.sql.Types.INTEGER:
        return "int";
      case java.sql.Types.DATE:
      case java.sql.Types.TIMESTAMP:
        return "date";
      case java.sql.Types.BIT:
        return "bit";
    }
    return null;
  }

  private void dropProcedure(Connection conn) {
    String preparedSQL =
            //" --IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getSecondaryXMLRowSet]') AND type in (N'P', N'PC')) " +
            " DROP PROCEDURE [dbo].[getSecondaryXMLRowSet]";

    Logger.getAnonymousLogger().log(Level.INFO, preparedSQL);
    try {
      PreparedStatement psDropProcedure = sqlCache.getSharedStatement(conn, preparedSQL);
      psDropProcedure.execute();
    } catch (SQLException ex) {
      Logger.getLogger(DbSecondaryRowSetReader.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private String substParameters(String sql, List<String> param) {
    for (String imeParametra : param) {
      sql = sql.replaceFirst("\\?", imeParametra);
    }

    Logger.getAnonymousLogger().log(Level.INFO, sql);
    return sql;
  }
}
