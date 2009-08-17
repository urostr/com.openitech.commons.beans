/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.proxy;

import java.beans.*;

/**
 *
 * @author uros
 */
public class PreparedStatementProxyBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.proxy.PreparedStatementProxy.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    protected static final int PROPERTY_array = 0;
    protected static final int PROPERTY_asciiStream = 1;
    protected static final int PROPERTY_bigDecimal = 2;
    protected static final int PROPERTY_binaryStream = 3;
    protected static final int PROPERTY_blob = 4;
    protected static final int PROPERTY_boolean = 5;
    protected static final int PROPERTY_byte = 6;
    protected static final int PROPERTY_bytes = 7;
    protected static final int PROPERTY_characterStream = 8;
    protected static final int PROPERTY_clob = 9;
    protected static final int PROPERTY_closed = 10;
    protected static final int PROPERTY_connection = 11;
    protected static final int PROPERTY_cursorName = 12;
    protected static final int PROPERTY_date = 13;
    protected static final int PROPERTY_double = 14;
    protected static final int PROPERTY_escapeProcessing = 15;
    protected static final int PROPERTY_fetchDirection = 16;
    protected static final int PROPERTY_fetchSize = 17;
    protected static final int PROPERTY_float = 18;
    protected static final int PROPERTY_generatedKeys = 19;
    protected static final int PROPERTY_int = 20;
    protected static final int PROPERTY_long = 21;
    protected static final int PROPERTY_maxFieldSize = 22;
    protected static final int PROPERTY_maxRows = 23;
    protected static final int PROPERTY_metaData = 24;
    protected static final int PROPERTY_moreResults = 25;
    protected static final int PROPERTY_NCharacterStream = 26;
    protected static final int PROPERTY_NClob = 27;
    protected static final int PROPERTY_NString = 28;
    protected static final int PROPERTY_null = 29;
    protected static final int PROPERTY_object = 30;
    protected static final int PROPERTY_parameterMetaData = 31;
    protected static final int PROPERTY_poolable = 32;
    protected static final int PROPERTY_queryTimeout = 33;
    protected static final int PROPERTY_ref = 34;
    protected static final int PROPERTY_resultSet = 35;
    protected static final int PROPERTY_resultSetConcurrency = 36;
    protected static final int PROPERTY_resultSetHoldability = 37;
    protected static final int PROPERTY_resultSetType = 38;
    protected static final int PROPERTY_rowId = 39;
    protected static final int PROPERTY_short = 40;
    protected static final int PROPERTY_SQLXML = 41;
    protected static final int PROPERTY_string = 42;
    protected static final int PROPERTY_time = 43;
    protected static final int PROPERTY_timestamp = 44;
    protected static final int PROPERTY_updateCount = 45;
    protected static final int PROPERTY_URL = 46;
    protected static final int PROPERTY_warnings = 47;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[48];
    
        try {
            properties[PROPERTY_array] = new IndexedPropertyDescriptor ( "array", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setArray" ); // NOI18N
            properties[PROPERTY_asciiStream] = new IndexedPropertyDescriptor ( "asciiStream", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setAsciiStream" ); // NOI18N
            properties[PROPERTY_bigDecimal] = new IndexedPropertyDescriptor ( "bigDecimal", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setBigDecimal" ); // NOI18N
            properties[PROPERTY_binaryStream] = new IndexedPropertyDescriptor ( "binaryStream", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setBinaryStream" ); // NOI18N
            properties[PROPERTY_blob] = new IndexedPropertyDescriptor ( "blob", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setBlob" ); // NOI18N
            properties[PROPERTY_boolean] = new IndexedPropertyDescriptor ( "boolean", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setBoolean" ); // NOI18N
            properties[PROPERTY_byte] = new IndexedPropertyDescriptor ( "byte", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setByte" ); // NOI18N
            properties[PROPERTY_bytes] = new IndexedPropertyDescriptor ( "bytes", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setBytes" ); // NOI18N
            properties[PROPERTY_characterStream] = new IndexedPropertyDescriptor ( "characterStream", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setCharacterStream" ); // NOI18N
            properties[PROPERTY_clob] = new IndexedPropertyDescriptor ( "clob", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setClob" ); // NOI18N
            properties[PROPERTY_closed] = new PropertyDescriptor ( "closed", com.openitech.db.proxy.PreparedStatementProxy.class, "isClosed", null ); // NOI18N
            properties[PROPERTY_connection] = new PropertyDescriptor ( "connection", com.openitech.db.proxy.PreparedStatementProxy.class, "getConnection", null ); // NOI18N
            properties[PROPERTY_cursorName] = new PropertyDescriptor ( "cursorName", com.openitech.db.proxy.PreparedStatementProxy.class, null, "setCursorName" ); // NOI18N
            properties[PROPERTY_date] = new IndexedPropertyDescriptor ( "date", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setDate" ); // NOI18N
            properties[PROPERTY_double] = new IndexedPropertyDescriptor ( "double", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setDouble" ); // NOI18N
            properties[PROPERTY_escapeProcessing] = new PropertyDescriptor ( "escapeProcessing", com.openitech.db.proxy.PreparedStatementProxy.class, null, "setEscapeProcessing" ); // NOI18N
            properties[PROPERTY_fetchDirection] = new PropertyDescriptor ( "fetchDirection", com.openitech.db.proxy.PreparedStatementProxy.class, "getFetchDirection", "setFetchDirection" ); // NOI18N
            properties[PROPERTY_fetchSize] = new PropertyDescriptor ( "fetchSize", com.openitech.db.proxy.PreparedStatementProxy.class, "getFetchSize", "setFetchSize" ); // NOI18N
            properties[PROPERTY_float] = new IndexedPropertyDescriptor ( "float", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setFloat" ); // NOI18N
            properties[PROPERTY_generatedKeys] = new PropertyDescriptor ( "generatedKeys", com.openitech.db.proxy.PreparedStatementProxy.class, "getGeneratedKeys", null ); // NOI18N
            properties[PROPERTY_int] = new IndexedPropertyDescriptor ( "int", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setInt" ); // NOI18N
            properties[PROPERTY_long] = new IndexedPropertyDescriptor ( "long", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setLong" ); // NOI18N
            properties[PROPERTY_maxFieldSize] = new PropertyDescriptor ( "maxFieldSize", com.openitech.db.proxy.PreparedStatementProxy.class, "getMaxFieldSize", "setMaxFieldSize" ); // NOI18N
            properties[PROPERTY_maxRows] = new PropertyDescriptor ( "maxRows", com.openitech.db.proxy.PreparedStatementProxy.class, "getMaxRows", "setMaxRows" ); // NOI18N
            properties[PROPERTY_metaData] = new PropertyDescriptor ( "metaData", com.openitech.db.proxy.PreparedStatementProxy.class, "getMetaData", null ); // NOI18N
            properties[PROPERTY_moreResults] = new PropertyDescriptor ( "moreResults", com.openitech.db.proxy.PreparedStatementProxy.class, "getMoreResults", null ); // NOI18N
            properties[PROPERTY_NCharacterStream] = new IndexedPropertyDescriptor ( "NCharacterStream", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setNCharacterStream" ); // NOI18N
            properties[PROPERTY_NClob] = new IndexedPropertyDescriptor ( "NClob", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setNClob" ); // NOI18N
            properties[PROPERTY_NString] = new IndexedPropertyDescriptor ( "NString", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setNString" ); // NOI18N
            properties[PROPERTY_null] = new IndexedPropertyDescriptor ( "null", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setNull" ); // NOI18N
            properties[PROPERTY_object] = new IndexedPropertyDescriptor ( "object", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setObject" ); // NOI18N
            properties[PROPERTY_parameterMetaData] = new PropertyDescriptor ( "parameterMetaData", com.openitech.db.proxy.PreparedStatementProxy.class, "getParameterMetaData", null ); // NOI18N
            properties[PROPERTY_poolable] = new PropertyDescriptor ( "poolable", com.openitech.db.proxy.PreparedStatementProxy.class, "isPoolable", "setPoolable" ); // NOI18N
            properties[PROPERTY_queryTimeout] = new PropertyDescriptor ( "queryTimeout", com.openitech.db.proxy.PreparedStatementProxy.class, "getQueryTimeout", "setQueryTimeout" ); // NOI18N
            properties[PROPERTY_ref] = new IndexedPropertyDescriptor ( "ref", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setRef" ); // NOI18N
            properties[PROPERTY_resultSet] = new PropertyDescriptor ( "resultSet", com.openitech.db.proxy.PreparedStatementProxy.class, "getResultSet", null ); // NOI18N
            properties[PROPERTY_resultSetConcurrency] = new PropertyDescriptor ( "resultSetConcurrency", com.openitech.db.proxy.PreparedStatementProxy.class, "getResultSetConcurrency", null ); // NOI18N
            properties[PROPERTY_resultSetHoldability] = new PropertyDescriptor ( "resultSetHoldability", com.openitech.db.proxy.PreparedStatementProxy.class, "getResultSetHoldability", null ); // NOI18N
            properties[PROPERTY_resultSetType] = new PropertyDescriptor ( "resultSetType", com.openitech.db.proxy.PreparedStatementProxy.class, "getResultSetType", null ); // NOI18N
            properties[PROPERTY_rowId] = new IndexedPropertyDescriptor ( "rowId", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setRowId" ); // NOI18N
            properties[PROPERTY_short] = new IndexedPropertyDescriptor ( "short", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setShort" ); // NOI18N
            properties[PROPERTY_SQLXML] = new IndexedPropertyDescriptor ( "SQLXML", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setSQLXML" ); // NOI18N
            properties[PROPERTY_string] = new IndexedPropertyDescriptor ( "string", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setString" ); // NOI18N
            properties[PROPERTY_time] = new IndexedPropertyDescriptor ( "time", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setTime" ); // NOI18N
            properties[PROPERTY_timestamp] = new IndexedPropertyDescriptor ( "timestamp", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setTimestamp" ); // NOI18N
            properties[PROPERTY_updateCount] = new PropertyDescriptor ( "updateCount", com.openitech.db.proxy.PreparedStatementProxy.class, "getUpdateCount", null ); // NOI18N
            properties[PROPERTY_URL] = new IndexedPropertyDescriptor ( "URL", com.openitech.db.proxy.PreparedStatementProxy.class, null, null, null, "setURL" ); // NOI18N
            properties[PROPERTY_warnings] = new PropertyDescriptor ( "warnings", com.openitech.db.proxy.PreparedStatementProxy.class, "getWarnings", null ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[0];//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    protected static final int METHOD_addBatch0 = 0;
    protected static final int METHOD_addBatch1 = 1;
    protected static final int METHOD_cancel2 = 2;
    protected static final int METHOD_clearBatch3 = 3;
    protected static final int METHOD_clearParameters4 = 4;
    protected static final int METHOD_clearWarnings5 = 5;
    protected static final int METHOD_close6 = 6;
    protected static final int METHOD_execute7 = 7;
    protected static final int METHOD_execute8 = 8;
    protected static final int METHOD_execute9 = 9;
    protected static final int METHOD_execute10 = 10;
    protected static final int METHOD_execute11 = 11;
    protected static final int METHOD_executeBatch12 = 12;
    protected static final int METHOD_executeQuery13 = 13;
    protected static final int METHOD_executeQuery14 = 14;
    protected static final int METHOD_executeUpdate15 = 15;
    protected static final int METHOD_executeUpdate16 = 16;
    protected static final int METHOD_executeUpdate17 = 17;
    protected static final int METHOD_executeUpdate18 = 18;
    protected static final int METHOD_executeUpdate19 = 19;
    protected static final int METHOD_isWrapperFor20 = 20;
    protected static final int METHOD_setAsciiStream21 = 21;
    protected static final int METHOD_setAsciiStream22 = 22;
    protected static final int METHOD_setBinaryStream23 = 23;
    protected static final int METHOD_setBinaryStream24 = 24;
    protected static final int METHOD_setBlob25 = 25;
    protected static final int METHOD_setBlob26 = 26;
    protected static final int METHOD_setCharacterStream27 = 27;
    protected static final int METHOD_setCharacterStream28 = 28;
    protected static final int METHOD_setClob29 = 29;
    protected static final int METHOD_setClob30 = 30;
    protected static final int METHOD_setDate31 = 31;
    protected static final int METHOD_setNCharacterStream32 = 32;
    protected static final int METHOD_setNClob33 = 33;
    protected static final int METHOD_setNClob34 = 34;
    protected static final int METHOD_setNull35 = 35;
    protected static final int METHOD_setObject36 = 36;
    protected static final int METHOD_setObject37 = 37;
    protected static final int METHOD_setTime38 = 38;
    protected static final int METHOD_setTimestamp39 = 39;
    protected static final int METHOD_setUnicodeStream40 = 40;
    protected static final int METHOD_unwrap41 = 41;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[42];
    
        try {
            methods[METHOD_addBatch0] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("addBatch", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_addBatch0].setDisplayName ( "" );
            methods[METHOD_addBatch1] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("addBatch", new Class[] {})); // NOI18N
            methods[METHOD_addBatch1].setDisplayName ( "" );
            methods[METHOD_cancel2] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("cancel", new Class[] {})); // NOI18N
            methods[METHOD_cancel2].setDisplayName ( "" );
            methods[METHOD_clearBatch3] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("clearBatch", new Class[] {})); // NOI18N
            methods[METHOD_clearBatch3].setDisplayName ( "" );
            methods[METHOD_clearParameters4] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("clearParameters", new Class[] {})); // NOI18N
            methods[METHOD_clearParameters4].setDisplayName ( "" );
            methods[METHOD_clearWarnings5] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("clearWarnings", new Class[] {})); // NOI18N
            methods[METHOD_clearWarnings5].setDisplayName ( "" );
            methods[METHOD_close6] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("close", new Class[] {})); // NOI18N
            methods[METHOD_close6].setDisplayName ( "" );
            methods[METHOD_execute7] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_execute7].setDisplayName ( "" );
            methods[METHOD_execute8] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_execute8].setDisplayName ( "" );
            methods[METHOD_execute9] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, int[].class})); // NOI18N
            methods[METHOD_execute9].setDisplayName ( "" );
            methods[METHOD_execute10] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, java.lang.String[].class})); // NOI18N
            methods[METHOD_execute10].setDisplayName ( "" );
            methods[METHOD_execute11] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("execute", new Class[] {})); // NOI18N
            methods[METHOD_execute11].setDisplayName ( "" );
            methods[METHOD_executeBatch12] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeBatch", new Class[] {})); // NOI18N
            methods[METHOD_executeBatch12].setDisplayName ( "" );
            methods[METHOD_executeQuery13] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeQuery", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_executeQuery13].setDisplayName ( "" );
            methods[METHOD_executeQuery14] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("executeQuery", new Class[] {})); // NOI18N
            methods[METHOD_executeQuery14].setDisplayName ( "" );
            methods[METHOD_executeUpdate15] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_executeUpdate15].setDisplayName ( "" );
            methods[METHOD_executeUpdate16] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_executeUpdate16].setDisplayName ( "" );
            methods[METHOD_executeUpdate17] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, int[].class})); // NOI18N
            methods[METHOD_executeUpdate17].setDisplayName ( "" );
            methods[METHOD_executeUpdate18] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, java.lang.String[].class})); // NOI18N
            methods[METHOD_executeUpdate18].setDisplayName ( "" );
            methods[METHOD_executeUpdate19] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("executeUpdate", new Class[] {})); // NOI18N
            methods[METHOD_executeUpdate19].setDisplayName ( "" );
            methods[METHOD_isWrapperFor20] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("isWrapperFor", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_isWrapperFor20].setDisplayName ( "" );
            methods[METHOD_setAsciiStream21] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setAsciiStream", new Class[] {int.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_setAsciiStream21].setDisplayName ( "" );
            methods[METHOD_setAsciiStream22] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setAsciiStream", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_setAsciiStream22].setDisplayName ( "" );
            methods[METHOD_setBinaryStream23] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setBinaryStream", new Class[] {int.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_setBinaryStream23].setDisplayName ( "" );
            methods[METHOD_setBinaryStream24] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setBinaryStream", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_setBinaryStream24].setDisplayName ( "" );
            methods[METHOD_setBlob25] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setBlob", new Class[] {int.class, java.sql.Blob.class})); // NOI18N
            methods[METHOD_setBlob25].setDisplayName ( "" );
            methods[METHOD_setBlob26] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setBlob", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_setBlob26].setDisplayName ( "" );
            methods[METHOD_setCharacterStream27] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setCharacterStream", new Class[] {int.class, java.io.Reader.class, int.class})); // NOI18N
            methods[METHOD_setCharacterStream27].setDisplayName ( "" );
            methods[METHOD_setCharacterStream28] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setCharacterStream", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_setCharacterStream28].setDisplayName ( "" );
            methods[METHOD_setClob29] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setClob", new Class[] {int.class, java.sql.Clob.class})); // NOI18N
            methods[METHOD_setClob29].setDisplayName ( "" );
            methods[METHOD_setClob30] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setClob", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_setClob30].setDisplayName ( "" );
            methods[METHOD_setDate31] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setDate", new Class[] {int.class, java.sql.Date.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_setDate31].setDisplayName ( "" );
            methods[METHOD_setNCharacterStream32] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setNCharacterStream", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_setNCharacterStream32].setDisplayName ( "" );
            methods[METHOD_setNClob33] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setNClob", new Class[] {int.class, java.sql.NClob.class})); // NOI18N
            methods[METHOD_setNClob33].setDisplayName ( "" );
            methods[METHOD_setNClob34] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setNClob", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_setNClob34].setDisplayName ( "" );
            methods[METHOD_setNull35] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setNull", new Class[] {int.class, int.class, java.lang.String.class})); // NOI18N
            methods[METHOD_setNull35].setDisplayName ( "" );
            methods[METHOD_setObject36] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setObject", new Class[] {int.class, java.lang.Object.class, int.class})); // NOI18N
            methods[METHOD_setObject36].setDisplayName ( "" );
            methods[METHOD_setObject37] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setObject", new Class[] {int.class, java.lang.Object.class, int.class, int.class})); // NOI18N
            methods[METHOD_setObject37].setDisplayName ( "" );
            methods[METHOD_setTime38] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setTime", new Class[] {int.class, java.sql.Time.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_setTime38].setDisplayName ( "" );
            methods[METHOD_setTimestamp39] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setTimestamp", new Class[] {int.class, java.sql.Timestamp.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_setTimestamp39].setDisplayName ( "" );
            methods[METHOD_setUnicodeStream40] = new MethodDescriptor(com.openitech.db.proxy.PreparedStatementProxy.class.getMethod("setUnicodeStream", new Class[] {int.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_setUnicodeStream40].setDisplayName ( "" );
            methods[METHOD_unwrap41] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("unwrap", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_unwrap41].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx

    
//GEN-FIRST:Superclass

    // Here you can add code for customizing the Superclass BeanInfo.

//GEN-LAST:Superclass
	
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
	return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
	return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * 
     * @return  An array of EventSetDescriptors describing the kinds of 
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
	return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * 
     * @return  An array of MethodDescriptors describing the methods 
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
	return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are 
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean. 
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconMono32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
	default: return null;
        }
    }

}

