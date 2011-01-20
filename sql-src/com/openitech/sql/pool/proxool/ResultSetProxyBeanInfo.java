/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.sql.pool.proxool;

import java.beans.*;

/**
 *
 * @author uros
 */
public class ResultSetProxyBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.sql.pool.proxool.ResultSetProxy.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    protected static final int PROPERTY_afterLast = 0;
    protected static final int PROPERTY_array = 1;
    protected static final int PROPERTY_asciiStream = 2;
    protected static final int PROPERTY_beforeFirst = 3;
    protected static final int PROPERTY_bigDecimal = 4;
    protected static final int PROPERTY_binaryStream = 5;
    protected static final int PROPERTY_blob = 6;
    protected static final int PROPERTY_boolean = 7;
    protected static final int PROPERTY_byte = 8;
    protected static final int PROPERTY_bytes = 9;
    protected static final int PROPERTY_characterStream = 10;
    protected static final int PROPERTY_clob = 11;
    protected static final int PROPERTY_closed = 12;
    protected static final int PROPERTY_concurrency = 13;
    protected static final int PROPERTY_cursorName = 14;
    protected static final int PROPERTY_date = 15;
    protected static final int PROPERTY_double = 16;
    protected static final int PROPERTY_fetchDirection = 17;
    protected static final int PROPERTY_fetchSize = 18;
    protected static final int PROPERTY_first = 19;
    protected static final int PROPERTY_float = 20;
    protected static final int PROPERTY_holdability = 21;
    protected static final int PROPERTY_int = 22;
    protected static final int PROPERTY_last = 23;
    protected static final int PROPERTY_long = 24;
    protected static final int PROPERTY_metaData = 25;
    protected static final int PROPERTY_NCharacterStream = 26;
    protected static final int PROPERTY_NClob = 27;
    protected static final int PROPERTY_NString = 28;
    protected static final int PROPERTY_object = 29;
    protected static final int PROPERTY_ref = 30;
    protected static final int PROPERTY_row = 31;
    protected static final int PROPERTY_rowId = 32;
    protected static final int PROPERTY_short = 33;
    protected static final int PROPERTY_SQLXML = 34;
    protected static final int PROPERTY_statement = 35;
    protected static final int PROPERTY_string = 36;
    protected static final int PROPERTY_time = 37;
    protected static final int PROPERTY_timestamp = 38;
    protected static final int PROPERTY_type = 39;
    protected static final int PROPERTY_unicodeStream = 40;
    protected static final int PROPERTY_URL = 41;
    protected static final int PROPERTY_warnings = 42;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[43];
    
        try {
            properties[PROPERTY_afterLast] = new PropertyDescriptor ( "afterLast", com.openitech.sql.pool.proxool.ResultSetProxy.class, "isAfterLast", null ); // NOI18N
            properties[PROPERTY_array] = new IndexedPropertyDescriptor ( "array", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getArray", null ); // NOI18N
            properties[PROPERTY_asciiStream] = new IndexedPropertyDescriptor ( "asciiStream", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getAsciiStream", null ); // NOI18N
            properties[PROPERTY_beforeFirst] = new PropertyDescriptor ( "beforeFirst", com.openitech.sql.pool.proxool.ResultSetProxy.class, "isBeforeFirst", null ); // NOI18N
            properties[PROPERTY_bigDecimal] = new IndexedPropertyDescriptor ( "bigDecimal", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getBigDecimal", null ); // NOI18N
            properties[PROPERTY_binaryStream] = new IndexedPropertyDescriptor ( "binaryStream", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getBinaryStream", null ); // NOI18N
            properties[PROPERTY_blob] = new IndexedPropertyDescriptor ( "blob", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getBlob", null ); // NOI18N
            properties[PROPERTY_boolean] = new IndexedPropertyDescriptor ( "boolean", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getBoolean", null ); // NOI18N
            properties[PROPERTY_byte] = new IndexedPropertyDescriptor ( "byte", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getByte", null ); // NOI18N
            properties[PROPERTY_bytes] = new IndexedPropertyDescriptor ( "bytes", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getBytes", null ); // NOI18N
            properties[PROPERTY_characterStream] = new IndexedPropertyDescriptor ( "characterStream", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getCharacterStream", null ); // NOI18N
            properties[PROPERTY_clob] = new IndexedPropertyDescriptor ( "clob", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getClob", null ); // NOI18N
            properties[PROPERTY_closed] = new PropertyDescriptor ( "closed", com.openitech.sql.pool.proxool.ResultSetProxy.class, "isClosed", null ); // NOI18N
            properties[PROPERTY_concurrency] = new PropertyDescriptor ( "concurrency", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getConcurrency", null ); // NOI18N
            properties[PROPERTY_cursorName] = new PropertyDescriptor ( "cursorName", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getCursorName", null ); // NOI18N
            properties[PROPERTY_date] = new IndexedPropertyDescriptor ( "date", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getDate", null ); // NOI18N
            properties[PROPERTY_double] = new IndexedPropertyDescriptor ( "double", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getDouble", null ); // NOI18N
            properties[PROPERTY_fetchDirection] = new PropertyDescriptor ( "fetchDirection", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getFetchDirection", "setFetchDirection" ); // NOI18N
            properties[PROPERTY_fetchSize] = new PropertyDescriptor ( "fetchSize", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getFetchSize", "setFetchSize" ); // NOI18N
            properties[PROPERTY_first] = new PropertyDescriptor ( "first", com.openitech.sql.pool.proxool.ResultSetProxy.class, "isFirst", null ); // NOI18N
            properties[PROPERTY_float] = new IndexedPropertyDescriptor ( "float", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getFloat", null ); // NOI18N
            properties[PROPERTY_holdability] = new PropertyDescriptor ( "holdability", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getHoldability", null ); // NOI18N
            properties[PROPERTY_int] = new IndexedPropertyDescriptor ( "int", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getInt", null ); // NOI18N
            properties[PROPERTY_last] = new PropertyDescriptor ( "last", com.openitech.sql.pool.proxool.ResultSetProxy.class, "isLast", null ); // NOI18N
            properties[PROPERTY_long] = new IndexedPropertyDescriptor ( "long", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getLong", null ); // NOI18N
            properties[PROPERTY_metaData] = new PropertyDescriptor ( "metaData", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getMetaData", null ); // NOI18N
            properties[PROPERTY_NCharacterStream] = new IndexedPropertyDescriptor ( "NCharacterStream", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getNCharacterStream", null ); // NOI18N
            properties[PROPERTY_NClob] = new IndexedPropertyDescriptor ( "NClob", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getNClob", null ); // NOI18N
            properties[PROPERTY_NString] = new IndexedPropertyDescriptor ( "NString", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getNString", null ); // NOI18N
            properties[PROPERTY_object] = new IndexedPropertyDescriptor ( "object", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getObject", null ); // NOI18N
            properties[PROPERTY_ref] = new IndexedPropertyDescriptor ( "ref", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getRef", null ); // NOI18N
            properties[PROPERTY_row] = new PropertyDescriptor ( "row", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getRow", null ); // NOI18N
            properties[PROPERTY_rowId] = new IndexedPropertyDescriptor ( "rowId", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getRowId", null ); // NOI18N
            properties[PROPERTY_short] = new IndexedPropertyDescriptor ( "short", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getShort", null ); // NOI18N
            properties[PROPERTY_SQLXML] = new IndexedPropertyDescriptor ( "SQLXML", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getSQLXML", null ); // NOI18N
            properties[PROPERTY_statement] = new PropertyDescriptor ( "statement", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getStatement", null ); // NOI18N
            properties[PROPERTY_string] = new IndexedPropertyDescriptor ( "string", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getString", null ); // NOI18N
            properties[PROPERTY_time] = new IndexedPropertyDescriptor ( "time", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getTime", null ); // NOI18N
            properties[PROPERTY_timestamp] = new IndexedPropertyDescriptor ( "timestamp", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getTimestamp", null ); // NOI18N
            properties[PROPERTY_type] = new PropertyDescriptor ( "type", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getType", null ); // NOI18N
            properties[PROPERTY_unicodeStream] = new IndexedPropertyDescriptor ( "unicodeStream", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getUnicodeStream", null ); // NOI18N
            properties[PROPERTY_URL] = new IndexedPropertyDescriptor ( "URL", com.openitech.sql.pool.proxool.ResultSetProxy.class, null, null, "getURL", null ); // NOI18N
            properties[PROPERTY_warnings] = new PropertyDescriptor ( "warnings", com.openitech.sql.pool.proxool.ResultSetProxy.class, "getWarnings", null ); // NOI18N
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
    protected static final int METHOD_absolute0 = 0;
    protected static final int METHOD_afterLast1 = 1;
    protected static final int METHOD_beforeFirst2 = 2;
    protected static final int METHOD_cancelRowUpdates3 = 3;
    protected static final int METHOD_clearWarnings4 = 4;
    protected static final int METHOD_close5 = 5;
    protected static final int METHOD_deleteRow6 = 6;
    protected static final int METHOD_findColumn7 = 7;
    protected static final int METHOD_first8 = 8;
    protected static final int METHOD_getArray9 = 9;
    protected static final int METHOD_getAsciiStream10 = 10;
    protected static final int METHOD_getBigDecimal11 = 11;
    protected static final int METHOD_getBigDecimal12 = 12;
    protected static final int METHOD_getBigDecimal13 = 13;
    protected static final int METHOD_getBinaryStream14 = 14;
    protected static final int METHOD_getBlob15 = 15;
    protected static final int METHOD_getBoolean16 = 16;
    protected static final int METHOD_getByte17 = 17;
    protected static final int METHOD_getBytes18 = 18;
    protected static final int METHOD_getCharacterStream19 = 19;
    protected static final int METHOD_getClob20 = 20;
    protected static final int METHOD_getDate21 = 21;
    protected static final int METHOD_getDate22 = 22;
    protected static final int METHOD_getDate23 = 23;
    protected static final int METHOD_getDouble24 = 24;
    protected static final int METHOD_getFloat25 = 25;
    protected static final int METHOD_getInt26 = 26;
    protected static final int METHOD_getLong27 = 27;
    protected static final int METHOD_getNCharacterStream28 = 28;
    protected static final int METHOD_getNClob29 = 29;
    protected static final int METHOD_getNString30 = 30;
    protected static final int METHOD_getObject31 = 31;
    protected static final int METHOD_getObject32 = 32;
    protected static final int METHOD_getObject33 = 33;
    protected static final int METHOD_getRef34 = 34;
    protected static final int METHOD_getRowId35 = 35;
    protected static final int METHOD_getShort36 = 36;
    protected static final int METHOD_getSQLXML37 = 37;
    protected static final int METHOD_getString38 = 38;
    protected static final int METHOD_getTime39 = 39;
    protected static final int METHOD_getTime40 = 40;
    protected static final int METHOD_getTime41 = 41;
    protected static final int METHOD_getTimestamp42 = 42;
    protected static final int METHOD_getTimestamp43 = 43;
    protected static final int METHOD_getTimestamp44 = 44;
    protected static final int METHOD_getUnicodeStream45 = 45;
    protected static final int METHOD_getURL46 = 46;
    protected static final int METHOD_insertRow47 = 47;
    protected static final int METHOD_isWrapperFor48 = 48;
    protected static final int METHOD_last49 = 49;
    protected static final int METHOD_moveToCurrentRow50 = 50;
    protected static final int METHOD_moveToInsertRow51 = 51;
    protected static final int METHOD_next52 = 52;
    protected static final int METHOD_previous53 = 53;
    protected static final int METHOD_refreshRow54 = 54;
    protected static final int METHOD_relative55 = 55;
    protected static final int METHOD_rowDeleted56 = 56;
    protected static final int METHOD_rowInserted57 = 57;
    protected static final int METHOD_rowUpdated58 = 58;
    protected static final int METHOD_unwrap59 = 59;
    protected static final int METHOD_updateArray60 = 60;
    protected static final int METHOD_updateArray61 = 61;
    protected static final int METHOD_updateAsciiStream62 = 62;
    protected static final int METHOD_updateAsciiStream63 = 63;
    protected static final int METHOD_updateAsciiStream64 = 64;
    protected static final int METHOD_updateAsciiStream65 = 65;
    protected static final int METHOD_updateAsciiStream66 = 66;
    protected static final int METHOD_updateAsciiStream67 = 67;
    protected static final int METHOD_updateBigDecimal68 = 68;
    protected static final int METHOD_updateBigDecimal69 = 69;
    protected static final int METHOD_updateBinaryStream70 = 70;
    protected static final int METHOD_updateBinaryStream71 = 71;
    protected static final int METHOD_updateBinaryStream72 = 72;
    protected static final int METHOD_updateBinaryStream73 = 73;
    protected static final int METHOD_updateBinaryStream74 = 74;
    protected static final int METHOD_updateBinaryStream75 = 75;
    protected static final int METHOD_updateBlob76 = 76;
    protected static final int METHOD_updateBlob77 = 77;
    protected static final int METHOD_updateBlob78 = 78;
    protected static final int METHOD_updateBlob79 = 79;
    protected static final int METHOD_updateBlob80 = 80;
    protected static final int METHOD_updateBlob81 = 81;
    protected static final int METHOD_updateBoolean82 = 82;
    protected static final int METHOD_updateBoolean83 = 83;
    protected static final int METHOD_updateByte84 = 84;
    protected static final int METHOD_updateByte85 = 85;
    protected static final int METHOD_updateBytes86 = 86;
    protected static final int METHOD_updateBytes87 = 87;
    protected static final int METHOD_updateCharacterStream88 = 88;
    protected static final int METHOD_updateCharacterStream89 = 89;
    protected static final int METHOD_updateCharacterStream90 = 90;
    protected static final int METHOD_updateCharacterStream91 = 91;
    protected static final int METHOD_updateCharacterStream92 = 92;
    protected static final int METHOD_updateCharacterStream93 = 93;
    protected static final int METHOD_updateClob94 = 94;
    protected static final int METHOD_updateClob95 = 95;
    protected static final int METHOD_updateClob96 = 96;
    protected static final int METHOD_updateClob97 = 97;
    protected static final int METHOD_updateClob98 = 98;
    protected static final int METHOD_updateClob99 = 99;
    protected static final int METHOD_updateDate100 = 100;
    protected static final int METHOD_updateDate101 = 101;
    protected static final int METHOD_updateDouble102 = 102;
    protected static final int METHOD_updateDouble103 = 103;
    protected static final int METHOD_updateFloat104 = 104;
    protected static final int METHOD_updateFloat105 = 105;
    protected static final int METHOD_updateInt106 = 106;
    protected static final int METHOD_updateInt107 = 107;
    protected static final int METHOD_updateLong108 = 108;
    protected static final int METHOD_updateLong109 = 109;
    protected static final int METHOD_updateNCharacterStream110 = 110;
    protected static final int METHOD_updateNCharacterStream111 = 111;
    protected static final int METHOD_updateNCharacterStream112 = 112;
    protected static final int METHOD_updateNCharacterStream113 = 113;
    protected static final int METHOD_updateNClob114 = 114;
    protected static final int METHOD_updateNClob115 = 115;
    protected static final int METHOD_updateNClob116 = 116;
    protected static final int METHOD_updateNClob117 = 117;
    protected static final int METHOD_updateNClob118 = 118;
    protected static final int METHOD_updateNClob119 = 119;
    protected static final int METHOD_updateNString120 = 120;
    protected static final int METHOD_updateNString121 = 121;
    protected static final int METHOD_updateNull122 = 122;
    protected static final int METHOD_updateNull123 = 123;
    protected static final int METHOD_updateObject124 = 124;
    protected static final int METHOD_updateObject125 = 125;
    protected static final int METHOD_updateObject126 = 126;
    protected static final int METHOD_updateObject127 = 127;
    protected static final int METHOD_updateRef128 = 128;
    protected static final int METHOD_updateRef129 = 129;
    protected static final int METHOD_updateRow130 = 130;
    protected static final int METHOD_updateRowId131 = 131;
    protected static final int METHOD_updateRowId132 = 132;
    protected static final int METHOD_updateShort133 = 133;
    protected static final int METHOD_updateShort134 = 134;
    protected static final int METHOD_updateSQLXML135 = 135;
    protected static final int METHOD_updateSQLXML136 = 136;
    protected static final int METHOD_updateString137 = 137;
    protected static final int METHOD_updateString138 = 138;
    protected static final int METHOD_updateTime139 = 139;
    protected static final int METHOD_updateTime140 = 140;
    protected static final int METHOD_updateTimestamp141 = 141;
    protected static final int METHOD_updateTimestamp142 = 142;
    protected static final int METHOD_wasNull143 = 143;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[144];
    
        try {
            methods[METHOD_absolute0] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("absolute", new Class[] {int.class})); // NOI18N
            methods[METHOD_absolute0].setDisplayName ( "" );
            methods[METHOD_afterLast1] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("afterLast", new Class[] {})); // NOI18N
            methods[METHOD_afterLast1].setDisplayName ( "" );
            methods[METHOD_beforeFirst2] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("beforeFirst", new Class[] {})); // NOI18N
            methods[METHOD_beforeFirst2].setDisplayName ( "" );
            methods[METHOD_cancelRowUpdates3] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("cancelRowUpdates", new Class[] {})); // NOI18N
            methods[METHOD_cancelRowUpdates3].setDisplayName ( "" );
            methods[METHOD_clearWarnings4] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("clearWarnings", new Class[] {})); // NOI18N
            methods[METHOD_clearWarnings4].setDisplayName ( "" );
            methods[METHOD_close5] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("close", new Class[] {})); // NOI18N
            methods[METHOD_close5].setDisplayName ( "" );
            methods[METHOD_deleteRow6] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("deleteRow", new Class[] {})); // NOI18N
            methods[METHOD_deleteRow6].setDisplayName ( "" );
            methods[METHOD_findColumn7] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("findColumn", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_findColumn7].setDisplayName ( "" );
            methods[METHOD_first8] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("first", new Class[] {})); // NOI18N
            methods[METHOD_first8].setDisplayName ( "" );
            methods[METHOD_getArray9] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getArray", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getArray9].setDisplayName ( "" );
            methods[METHOD_getAsciiStream10] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getAsciiStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getAsciiStream10].setDisplayName ( "" );
            methods[METHOD_getBigDecimal11] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBigDecimal", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_getBigDecimal11].setDisplayName ( "" );
            methods[METHOD_getBigDecimal12] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBigDecimal", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_getBigDecimal12].setDisplayName ( "" );
            methods[METHOD_getBigDecimal13] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBigDecimal", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBigDecimal13].setDisplayName ( "" );
            methods[METHOD_getBinaryStream14] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBinaryStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBinaryStream14].setDisplayName ( "" );
            methods[METHOD_getBlob15] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBlob", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBlob15].setDisplayName ( "" );
            methods[METHOD_getBoolean16] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBoolean", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBoolean16].setDisplayName ( "" );
            methods[METHOD_getByte17] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getByte", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getByte17].setDisplayName ( "" );
            methods[METHOD_getBytes18] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getBytes", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBytes18].setDisplayName ( "" );
            methods[METHOD_getCharacterStream19] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getCharacterStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getCharacterStream19].setDisplayName ( "" );
            methods[METHOD_getClob20] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getClob", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getClob20].setDisplayName ( "" );
            methods[METHOD_getDate21] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getDate", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getDate21].setDisplayName ( "" );
            methods[METHOD_getDate22] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getDate", new Class[] {int.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getDate22].setDisplayName ( "" );
            methods[METHOD_getDate23] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getDate", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getDate23].setDisplayName ( "" );
            methods[METHOD_getDouble24] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getDouble", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getDouble24].setDisplayName ( "" );
            methods[METHOD_getFloat25] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getFloat", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getFloat25].setDisplayName ( "" );
            methods[METHOD_getInt26] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getInt", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getInt26].setDisplayName ( "" );
            methods[METHOD_getLong27] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getLong", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getLong27].setDisplayName ( "" );
            methods[METHOD_getNCharacterStream28] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getNCharacterStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getNCharacterStream28].setDisplayName ( "" );
            methods[METHOD_getNClob29] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getNClob", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getNClob29].setDisplayName ( "" );
            methods[METHOD_getNString30] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getNString", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getNString30].setDisplayName ( "" );
            methods[METHOD_getObject31] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getObject", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getObject31].setDisplayName ( "" );
            methods[METHOD_getObject32] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getObject", new Class[] {int.class, java.util.Map.class})); // NOI18N
            methods[METHOD_getObject32].setDisplayName ( "" );
            methods[METHOD_getObject33] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getObject", new Class[] {java.lang.String.class, java.util.Map.class})); // NOI18N
            methods[METHOD_getObject33].setDisplayName ( "" );
            methods[METHOD_getRef34] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getRef", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getRef34].setDisplayName ( "" );
            methods[METHOD_getRowId35] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getRowId", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getRowId35].setDisplayName ( "" );
            methods[METHOD_getShort36] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getShort", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getShort36].setDisplayName ( "" );
            methods[METHOD_getSQLXML37] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getSQLXML", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getSQLXML37].setDisplayName ( "" );
            methods[METHOD_getString38] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getString", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getString38].setDisplayName ( "" );
            methods[METHOD_getTime39] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTime", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getTime39].setDisplayName ( "" );
            methods[METHOD_getTime40] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTime", new Class[] {int.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTime40].setDisplayName ( "" );
            methods[METHOD_getTime41] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTime", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTime41].setDisplayName ( "" );
            methods[METHOD_getTimestamp42] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTimestamp", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getTimestamp42].setDisplayName ( "" );
            methods[METHOD_getTimestamp43] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTimestamp", new Class[] {int.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTimestamp43].setDisplayName ( "" );
            methods[METHOD_getTimestamp44] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getTimestamp", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTimestamp44].setDisplayName ( "" );
            methods[METHOD_getUnicodeStream45] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getUnicodeStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getUnicodeStream45].setDisplayName ( "" );
            methods[METHOD_getURL46] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("getURL", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getURL46].setDisplayName ( "" );
            methods[METHOD_insertRow47] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("insertRow", new Class[] {})); // NOI18N
            methods[METHOD_insertRow47].setDisplayName ( "" );
            methods[METHOD_isWrapperFor48] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("isWrapperFor", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_isWrapperFor48].setDisplayName ( "" );
            methods[METHOD_last49] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("last", new Class[] {})); // NOI18N
            methods[METHOD_last49].setDisplayName ( "" );
            methods[METHOD_moveToCurrentRow50] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("moveToCurrentRow", new Class[] {})); // NOI18N
            methods[METHOD_moveToCurrentRow50].setDisplayName ( "" );
            methods[METHOD_moveToInsertRow51] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("moveToInsertRow", new Class[] {})); // NOI18N
            methods[METHOD_moveToInsertRow51].setDisplayName ( "" );
            methods[METHOD_next52] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("next", new Class[] {})); // NOI18N
            methods[METHOD_next52].setDisplayName ( "" );
            methods[METHOD_previous53] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("previous", new Class[] {})); // NOI18N
            methods[METHOD_previous53].setDisplayName ( "" );
            methods[METHOD_refreshRow54] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("refreshRow", new Class[] {})); // NOI18N
            methods[METHOD_refreshRow54].setDisplayName ( "" );
            methods[METHOD_relative55] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("relative", new Class[] {int.class})); // NOI18N
            methods[METHOD_relative55].setDisplayName ( "" );
            methods[METHOD_rowDeleted56] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("rowDeleted", new Class[] {})); // NOI18N
            methods[METHOD_rowDeleted56].setDisplayName ( "" );
            methods[METHOD_rowInserted57] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("rowInserted", new Class[] {})); // NOI18N
            methods[METHOD_rowInserted57].setDisplayName ( "" );
            methods[METHOD_rowUpdated58] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("rowUpdated", new Class[] {})); // NOI18N
            methods[METHOD_rowUpdated58].setDisplayName ( "" );
            methods[METHOD_unwrap59] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("unwrap", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_unwrap59].setDisplayName ( "" );
            methods[METHOD_updateArray60] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateArray", new Class[] {int.class, java.sql.Array.class})); // NOI18N
            methods[METHOD_updateArray60].setDisplayName ( "" );
            methods[METHOD_updateArray61] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateArray", new Class[] {java.lang.String.class, java.sql.Array.class})); // NOI18N
            methods[METHOD_updateArray61].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream62] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {int.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_updateAsciiStream62].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream63] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {java.lang.String.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_updateAsciiStream63].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream64] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateAsciiStream64].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream65] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {java.lang.String.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateAsciiStream65].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream66] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {int.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateAsciiStream66].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream67] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateAsciiStream", new Class[] {java.lang.String.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateAsciiStream67].setDisplayName ( "" );
            methods[METHOD_updateBigDecimal68] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBigDecimal", new Class[] {int.class, java.math.BigDecimal.class})); // NOI18N
            methods[METHOD_updateBigDecimal68].setDisplayName ( "" );
            methods[METHOD_updateBigDecimal69] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBigDecimal", new Class[] {java.lang.String.class, java.math.BigDecimal.class})); // NOI18N
            methods[METHOD_updateBigDecimal69].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream70] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {int.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_updateBinaryStream70].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream71] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {java.lang.String.class, java.io.InputStream.class, int.class})); // NOI18N
            methods[METHOD_updateBinaryStream71].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream72] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateBinaryStream72].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream73] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {java.lang.String.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateBinaryStream73].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream74] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {int.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateBinaryStream74].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream75] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBinaryStream", new Class[] {java.lang.String.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateBinaryStream75].setDisplayName ( "" );
            methods[METHOD_updateBlob76] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {int.class, java.sql.Blob.class})); // NOI18N
            methods[METHOD_updateBlob76].setDisplayName ( "" );
            methods[METHOD_updateBlob77] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {java.lang.String.class, java.sql.Blob.class})); // NOI18N
            methods[METHOD_updateBlob77].setDisplayName ( "" );
            methods[METHOD_updateBlob78] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {int.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateBlob78].setDisplayName ( "" );
            methods[METHOD_updateBlob79] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {java.lang.String.class, java.io.InputStream.class, long.class})); // NOI18N
            methods[METHOD_updateBlob79].setDisplayName ( "" );
            methods[METHOD_updateBlob80] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {int.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateBlob80].setDisplayName ( "" );
            methods[METHOD_updateBlob81] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBlob", new Class[] {java.lang.String.class, java.io.InputStream.class})); // NOI18N
            methods[METHOD_updateBlob81].setDisplayName ( "" );
            methods[METHOD_updateBoolean82] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBoolean", new Class[] {int.class, boolean.class})); // NOI18N
            methods[METHOD_updateBoolean82].setDisplayName ( "" );
            methods[METHOD_updateBoolean83] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBoolean", new Class[] {java.lang.String.class, boolean.class})); // NOI18N
            methods[METHOD_updateBoolean83].setDisplayName ( "" );
            methods[METHOD_updateByte84] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateByte", new Class[] {int.class, byte.class})); // NOI18N
            methods[METHOD_updateByte84].setDisplayName ( "" );
            methods[METHOD_updateByte85] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateByte", new Class[] {java.lang.String.class, byte.class})); // NOI18N
            methods[METHOD_updateByte85].setDisplayName ( "" );
            methods[METHOD_updateBytes86] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBytes", new Class[] {int.class, byte[].class})); // NOI18N
            methods[METHOD_updateBytes86].setDisplayName ( "" );
            methods[METHOD_updateBytes87] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateBytes", new Class[] {java.lang.String.class, byte[].class})); // NOI18N
            methods[METHOD_updateBytes87].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream88] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {int.class, java.io.Reader.class, int.class})); // NOI18N
            methods[METHOD_updateCharacterStream88].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream89] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class, int.class})); // NOI18N
            methods[METHOD_updateCharacterStream89].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream90] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateCharacterStream90].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream91] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateCharacterStream91].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream92] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {int.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateCharacterStream92].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream93] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateCharacterStream93].setDisplayName ( "" );
            methods[METHOD_updateClob94] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {int.class, java.sql.Clob.class})); // NOI18N
            methods[METHOD_updateClob94].setDisplayName ( "" );
            methods[METHOD_updateClob95] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {java.lang.String.class, java.sql.Clob.class})); // NOI18N
            methods[METHOD_updateClob95].setDisplayName ( "" );
            methods[METHOD_updateClob96] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateClob96].setDisplayName ( "" );
            methods[METHOD_updateClob97] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {java.lang.String.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateClob97].setDisplayName ( "" );
            methods[METHOD_updateClob98] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {int.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateClob98].setDisplayName ( "" );
            methods[METHOD_updateClob99] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateClob", new Class[] {java.lang.String.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateClob99].setDisplayName ( "" );
            methods[METHOD_updateDate100] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateDate", new Class[] {int.class, java.sql.Date.class})); // NOI18N
            methods[METHOD_updateDate100].setDisplayName ( "" );
            methods[METHOD_updateDate101] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateDate", new Class[] {java.lang.String.class, java.sql.Date.class})); // NOI18N
            methods[METHOD_updateDate101].setDisplayName ( "" );
            methods[METHOD_updateDouble102] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateDouble", new Class[] {int.class, double.class})); // NOI18N
            methods[METHOD_updateDouble102].setDisplayName ( "" );
            methods[METHOD_updateDouble103] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateDouble", new Class[] {java.lang.String.class, double.class})); // NOI18N
            methods[METHOD_updateDouble103].setDisplayName ( "" );
            methods[METHOD_updateFloat104] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateFloat", new Class[] {int.class, float.class})); // NOI18N
            methods[METHOD_updateFloat104].setDisplayName ( "" );
            methods[METHOD_updateFloat105] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateFloat", new Class[] {java.lang.String.class, float.class})); // NOI18N
            methods[METHOD_updateFloat105].setDisplayName ( "" );
            methods[METHOD_updateInt106] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateInt", new Class[] {int.class, int.class})); // NOI18N
            methods[METHOD_updateInt106].setDisplayName ( "" );
            methods[METHOD_updateInt107] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateInt", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_updateInt107].setDisplayName ( "" );
            methods[METHOD_updateLong108] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateLong", new Class[] {int.class, long.class})); // NOI18N
            methods[METHOD_updateLong108].setDisplayName ( "" );
            methods[METHOD_updateLong109] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateLong", new Class[] {java.lang.String.class, long.class})); // NOI18N
            methods[METHOD_updateLong109].setDisplayName ( "" );
            methods[METHOD_updateNCharacterStream110] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNCharacterStream", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateNCharacterStream110].setDisplayName ( "" );
            methods[METHOD_updateNCharacterStream111] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateNCharacterStream111].setDisplayName ( "" );
            methods[METHOD_updateNCharacterStream112] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNCharacterStream", new Class[] {int.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateNCharacterStream112].setDisplayName ( "" );
            methods[METHOD_updateNCharacterStream113] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateNCharacterStream113].setDisplayName ( "" );
            methods[METHOD_updateNClob114] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {int.class, java.sql.NClob.class})); // NOI18N
            methods[METHOD_updateNClob114].setDisplayName ( "" );
            methods[METHOD_updateNClob115] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {java.lang.String.class, java.sql.NClob.class})); // NOI18N
            methods[METHOD_updateNClob115].setDisplayName ( "" );
            methods[METHOD_updateNClob116] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {int.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateNClob116].setDisplayName ( "" );
            methods[METHOD_updateNClob117] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {java.lang.String.class, java.io.Reader.class, long.class})); // NOI18N
            methods[METHOD_updateNClob117].setDisplayName ( "" );
            methods[METHOD_updateNClob118] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {int.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateNClob118].setDisplayName ( "" );
            methods[METHOD_updateNClob119] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNClob", new Class[] {java.lang.String.class, java.io.Reader.class})); // NOI18N
            methods[METHOD_updateNClob119].setDisplayName ( "" );
            methods[METHOD_updateNString120] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNString", new Class[] {int.class, java.lang.String.class})); // NOI18N
            methods[METHOD_updateNString120].setDisplayName ( "" );
            methods[METHOD_updateNString121] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNString", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_updateNString121].setDisplayName ( "" );
            methods[METHOD_updateNull122] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNull", new Class[] {int.class})); // NOI18N
            methods[METHOD_updateNull122].setDisplayName ( "" );
            methods[METHOD_updateNull123] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateNull", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_updateNull123].setDisplayName ( "" );
            methods[METHOD_updateObject124] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateObject", new Class[] {int.class, java.lang.Object.class, int.class})); // NOI18N
            methods[METHOD_updateObject124].setDisplayName ( "" );
            methods[METHOD_updateObject125] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateObject", new Class[] {int.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_updateObject125].setDisplayName ( "" );
            methods[METHOD_updateObject126] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateObject", new Class[] {java.lang.String.class, java.lang.Object.class, int.class})); // NOI18N
            methods[METHOD_updateObject126].setDisplayName ( "" );
            methods[METHOD_updateObject127] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateObject", new Class[] {java.lang.String.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_updateObject127].setDisplayName ( "" );
            methods[METHOD_updateRef128] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateRef", new Class[] {int.class, java.sql.Ref.class})); // NOI18N
            methods[METHOD_updateRef128].setDisplayName ( "" );
            methods[METHOD_updateRef129] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateRef", new Class[] {java.lang.String.class, java.sql.Ref.class})); // NOI18N
            methods[METHOD_updateRef129].setDisplayName ( "" );
            methods[METHOD_updateRow130] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateRow", new Class[] {})); // NOI18N
            methods[METHOD_updateRow130].setDisplayName ( "" );
            methods[METHOD_updateRowId131] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateRowId", new Class[] {int.class, java.sql.RowId.class})); // NOI18N
            methods[METHOD_updateRowId131].setDisplayName ( "" );
            methods[METHOD_updateRowId132] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateRowId", new Class[] {java.lang.String.class, java.sql.RowId.class})); // NOI18N
            methods[METHOD_updateRowId132].setDisplayName ( "" );
            methods[METHOD_updateShort133] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateShort", new Class[] {int.class, short.class})); // NOI18N
            methods[METHOD_updateShort133].setDisplayName ( "" );
            methods[METHOD_updateShort134] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateShort", new Class[] {java.lang.String.class, short.class})); // NOI18N
            methods[METHOD_updateShort134].setDisplayName ( "" );
            methods[METHOD_updateSQLXML135] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateSQLXML", new Class[] {int.class, java.sql.SQLXML.class})); // NOI18N
            methods[METHOD_updateSQLXML135].setDisplayName ( "" );
            methods[METHOD_updateSQLXML136] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateSQLXML", new Class[] {java.lang.String.class, java.sql.SQLXML.class})); // NOI18N
            methods[METHOD_updateSQLXML136].setDisplayName ( "" );
            methods[METHOD_updateString137] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateString", new Class[] {int.class, java.lang.String.class})); // NOI18N
            methods[METHOD_updateString137].setDisplayName ( "" );
            methods[METHOD_updateString138] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateString", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_updateString138].setDisplayName ( "" );
            methods[METHOD_updateTime139] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateTime", new Class[] {int.class, java.sql.Time.class})); // NOI18N
            methods[METHOD_updateTime139].setDisplayName ( "" );
            methods[METHOD_updateTime140] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateTime", new Class[] {java.lang.String.class, java.sql.Time.class})); // NOI18N
            methods[METHOD_updateTime140].setDisplayName ( "" );
            methods[METHOD_updateTimestamp141] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateTimestamp", new Class[] {int.class, java.sql.Timestamp.class})); // NOI18N
            methods[METHOD_updateTimestamp141].setDisplayName ( "" );
            methods[METHOD_updateTimestamp142] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("updateTimestamp", new Class[] {java.lang.String.class, java.sql.Timestamp.class})); // NOI18N
            methods[METHOD_updateTimestamp142].setDisplayName ( "" );
            methods[METHOD_wasNull143] = new MethodDescriptor(com.openitech.sql.pool.proxool.ResultSetProxy.class.getMethod("wasNull", new Class[] {})); // NOI18N
            methods[METHOD_wasNull143].setDisplayName ( "" );
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

