/*
 * DbDataSourceBeanInfo.java
 *
 * Created on Petek, 23 november 2007, 15:08
 */

package com.openitech.db.model;

import java.beans.*;

/**
 * @author uros
 */
public class DbDataSourceBeanInfo extends SimpleBeanInfo {
  
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.DbDataSource.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    
    // Here you can add code for customizing the BeanDescriptor.
    
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
  
  
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_afterLast = 0;
    private static final int PROPERTY_array = 1;
    private static final int PROPERTY_asciiStream = 2;
    private static final int PROPERTY_beforeFirst = 3;
    private static final int PROPERTY_bigDecimal = 4;
    private static final int PROPERTY_binaryStream = 5;
    private static final int PROPERTY_blob = 6;
    private static final int PROPERTY_boolean = 7;
    private static final int PROPERTY_byte = 8;
    private static final int PROPERTY_bytes = 9;
    private static final int PROPERTY_canAddRows = 10;
    private static final int PROPERTY_canDeleteRows = 11;
    private static final int PROPERTY_characterStream = 12;
    private static final int PROPERTY_clob = 13;
    private static final int PROPERTY_columnCount = 14;
    private static final int PROPERTY_columnName = 15;
    private static final int PROPERTY_concurrency = 16;
    private static final int PROPERTY_connection = 17;
    private static final int PROPERTY_countSql = 18;
    private static final int PROPERTY_cursorName = 19;
    private static final int PROPERTY_dataLoaded = 20;
    private static final int PROPERTY_date = 21;
    private static final int PROPERTY_defaultValues = 22;
    private static final int PROPERTY_double = 23;
    private static final int PROPERTY_fetchDirection = 24;
    private static final int PROPERTY_fetchSize = 25;
    private static final int PROPERTY_first = 26;
    private static final int PROPERTY_float = 27;
    private static final int PROPERTY_int = 28;
    private static final int PROPERTY_last = 29;
    private static final int PROPERTY_long = 30;
    private static final int PROPERTY_metaData = 31;
    private static final int PROPERTY_name = 32;
    private static final int PROPERTY_object = 33;
    private static final int PROPERTY_parameters = 34;
    private static final int PROPERTY_propertyChangeListeners = 35;
    private static final int PROPERTY_queuedDelay = 36;
    private static final int PROPERTY_readOnly = 37;
    private static final int PROPERTY_ref = 38;
    private static final int PROPERTY_resultSet = 39;
    private static final int PROPERTY_row = 40;
    private static final int PROPERTY_rowCount = 41;
    private static final int PROPERTY_selectSql = 42;
    private static final int PROPERTY_short = 43;
    private static final int PROPERTY_singleTableSelect = 44;
    private static final int PROPERTY_statement = 45;
    private static final int PROPERTY_storedUpdates = 46;
    private static final int PROPERTY_string = 47;
    private static final int PROPERTY_time = 48;
    private static final int PROPERTY_timestamp = 49;
    private static final int PROPERTY_type = 50;
    private static final int PROPERTY_unicodeStream = 51;
    private static final int PROPERTY_uniqueID = 52;
    private static final int PROPERTY_updateRowFireOnly = 53;
    private static final int PROPERTY_updateTableName = 54;
    private static final int PROPERTY_URL = 55;
    private static final int PROPERTY_warnings = 56;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[57];
    
        try {
            properties[PROPERTY_afterLast] = new PropertyDescriptor ( "afterLast", com.openitech.db.model.DbDataSource.class, "isAfterLast", null ); // NOI18N
            properties[PROPERTY_array] = new IndexedPropertyDescriptor ( "array", com.openitech.db.model.DbDataSource.class, null, null, "getArray", null ); // NOI18N
            properties[PROPERTY_asciiStream] = new IndexedPropertyDescriptor ( "asciiStream", com.openitech.db.model.DbDataSource.class, null, null, "getAsciiStream", null ); // NOI18N
            properties[PROPERTY_beforeFirst] = new PropertyDescriptor ( "beforeFirst", com.openitech.db.model.DbDataSource.class, "isBeforeFirst", null ); // NOI18N
            properties[PROPERTY_bigDecimal] = new IndexedPropertyDescriptor ( "bigDecimal", com.openitech.db.model.DbDataSource.class, null, null, "getBigDecimal", null ); // NOI18N
            properties[PROPERTY_binaryStream] = new IndexedPropertyDescriptor ( "binaryStream", com.openitech.db.model.DbDataSource.class, null, null, "getBinaryStream", null ); // NOI18N
            properties[PROPERTY_blob] = new IndexedPropertyDescriptor ( "blob", com.openitech.db.model.DbDataSource.class, null, null, "getBlob", null ); // NOI18N
            properties[PROPERTY_boolean] = new IndexedPropertyDescriptor ( "boolean", com.openitech.db.model.DbDataSource.class, null, null, "getBoolean", null ); // NOI18N
            properties[PROPERTY_byte] = new IndexedPropertyDescriptor ( "byte", com.openitech.db.model.DbDataSource.class, null, null, "getByte", null ); // NOI18N
            properties[PROPERTY_bytes] = new IndexedPropertyDescriptor ( "bytes", com.openitech.db.model.DbDataSource.class, null, null, "getBytes", null ); // NOI18N
            properties[PROPERTY_canAddRows] = new PropertyDescriptor ( "canAddRows", com.openitech.db.model.DbDataSource.class, "isCanAddRows", "setCanAddRows" ); // NOI18N
            properties[PROPERTY_canDeleteRows] = new PropertyDescriptor ( "canDeleteRows", com.openitech.db.model.DbDataSource.class, "isCanDeleteRows", "setCanDeleteRows" ); // NOI18N
            properties[PROPERTY_characterStream] = new IndexedPropertyDescriptor ( "characterStream", com.openitech.db.model.DbDataSource.class, null, null, "getCharacterStream", null ); // NOI18N
            properties[PROPERTY_clob] = new IndexedPropertyDescriptor ( "clob", com.openitech.db.model.DbDataSource.class, null, null, "getClob", null ); // NOI18N
            properties[PROPERTY_columnCount] = new PropertyDescriptor ( "columnCount", com.openitech.db.model.DbDataSource.class, "getColumnCount", null ); // NOI18N
            properties[PROPERTY_columnName] = new IndexedPropertyDescriptor ( "columnName", com.openitech.db.model.DbDataSource.class, null, null, "getColumnName", null ); // NOI18N
            properties[PROPERTY_concurrency] = new PropertyDescriptor ( "concurrency", com.openitech.db.model.DbDataSource.class, "getConcurrency", null ); // NOI18N
            properties[PROPERTY_connection] = new PropertyDescriptor ( "connection", com.openitech.db.model.DbDataSource.class, "getConnection", "setConnection" ); // NOI18N
            properties[PROPERTY_countSql] = new PropertyDescriptor ( "countSql", com.openitech.db.model.DbDataSource.class, "getCountSql", "setCountSql" ); // NOI18N
            properties[PROPERTY_cursorName] = new PropertyDescriptor ( "cursorName", com.openitech.db.model.DbDataSource.class, "getCursorName", null ); // NOI18N
            properties[PROPERTY_dataLoaded] = new PropertyDescriptor ( "dataLoaded", com.openitech.db.model.DbDataSource.class, "isDataLoaded", null ); // NOI18N
            properties[PROPERTY_date] = new IndexedPropertyDescriptor ( "date", com.openitech.db.model.DbDataSource.class, null, null, "getDate", null ); // NOI18N
            properties[PROPERTY_defaultValues] = new PropertyDescriptor ( "defaultValues", com.openitech.db.model.DbDataSource.class, "getDefaultValues", null ); // NOI18N
            properties[PROPERTY_double] = new IndexedPropertyDescriptor ( "double", com.openitech.db.model.DbDataSource.class, null, null, "getDouble", null ); // NOI18N
            properties[PROPERTY_fetchDirection] = new PropertyDescriptor ( "fetchDirection", com.openitech.db.model.DbDataSource.class, "getFetchDirection", "setFetchDirection" ); // NOI18N
            properties[PROPERTY_fetchSize] = new PropertyDescriptor ( "fetchSize", com.openitech.db.model.DbDataSource.class, "getFetchSize", "setFetchSize" ); // NOI18N
            properties[PROPERTY_first] = new PropertyDescriptor ( "first", com.openitech.db.model.DbDataSource.class, "isFirst", null ); // NOI18N
            properties[PROPERTY_float] = new IndexedPropertyDescriptor ( "float", com.openitech.db.model.DbDataSource.class, null, null, "getFloat", null ); // NOI18N
            properties[PROPERTY_int] = new IndexedPropertyDescriptor ( "int", com.openitech.db.model.DbDataSource.class, null, null, "getInt", null ); // NOI18N
            properties[PROPERTY_last] = new PropertyDescriptor ( "last", com.openitech.db.model.DbDataSource.class, "isLast", null ); // NOI18N
            properties[PROPERTY_long] = new IndexedPropertyDescriptor ( "long", com.openitech.db.model.DbDataSource.class, null, null, "getLong", null ); // NOI18N
            properties[PROPERTY_metaData] = new PropertyDescriptor ( "metaData", com.openitech.db.model.DbDataSource.class, "getMetaData", null ); // NOI18N
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", com.openitech.db.model.DbDataSource.class, "getName", null ); // NOI18N
            properties[PROPERTY_object] = new IndexedPropertyDescriptor ( "object", com.openitech.db.model.DbDataSource.class, null, null, "getObject", null ); // NOI18N
            properties[PROPERTY_parameters] = new PropertyDescriptor ( "parameters", com.openitech.db.model.DbDataSource.class, "getParameters", null ); // NOI18N
            properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", com.openitech.db.model.DbDataSource.class, "getPropertyChangeListeners", null ); // NOI18N
            properties[PROPERTY_queuedDelay] = new PropertyDescriptor ( "queuedDelay", com.openitech.db.model.DbDataSource.class, "getQueuedDelay", "setQueuedDelay" ); // NOI18N
            properties[PROPERTY_readOnly] = new PropertyDescriptor ( "readOnly", com.openitech.db.model.DbDataSource.class, "isReadOnly", "setReadOnly" ); // NOI18N
            properties[PROPERTY_ref] = new IndexedPropertyDescriptor ( "ref", com.openitech.db.model.DbDataSource.class, null, null, "getRef", null ); // NOI18N
            properties[PROPERTY_resultSet] = new PropertyDescriptor ( "resultSet", com.openitech.db.model.DbDataSource.class, "getResultSet", null ); // NOI18N
            properties[PROPERTY_row] = new PropertyDescriptor ( "row", com.openitech.db.model.DbDataSource.class, "getRow", null ); // NOI18N
            properties[PROPERTY_rowCount] = new PropertyDescriptor ( "rowCount", com.openitech.db.model.DbDataSource.class, "getRowCount", null ); // NOI18N
            properties[PROPERTY_selectSql] = new PropertyDescriptor ( "selectSql", com.openitech.db.model.DbDataSource.class, "getSelectSql", "setSelectSql" ); // NOI18N
            properties[PROPERTY_short] = new IndexedPropertyDescriptor ( "short", com.openitech.db.model.DbDataSource.class, null, null, "getShort", null ); // NOI18N
            properties[PROPERTY_singleTableSelect] = new PropertyDescriptor ( "singleTableSelect", com.openitech.db.model.DbDataSource.class, "isSingleTableSelect", "setSingleTableSelect" ); // NOI18N
            properties[PROPERTY_statement] = new PropertyDescriptor ( "statement", com.openitech.db.model.DbDataSource.class, "getStatement", null ); // NOI18N
            properties[PROPERTY_storedUpdates] = new PropertyDescriptor ( "storedUpdates", com.openitech.db.model.DbDataSource.class, "getStoredUpdates", null ); // NOI18N
            properties[PROPERTY_string] = new IndexedPropertyDescriptor ( "string", com.openitech.db.model.DbDataSource.class, null, null, "getString", null ); // NOI18N
            properties[PROPERTY_time] = new IndexedPropertyDescriptor ( "time", com.openitech.db.model.DbDataSource.class, null, null, "getTime", null ); // NOI18N
            properties[PROPERTY_timestamp] = new IndexedPropertyDescriptor ( "timestamp", com.openitech.db.model.DbDataSource.class, null, null, "getTimestamp", null ); // NOI18N
            properties[PROPERTY_type] = new PropertyDescriptor ( "type", com.openitech.db.model.DbDataSource.class, "getType", null ); // NOI18N
            properties[PROPERTY_unicodeStream] = new IndexedPropertyDescriptor ( "unicodeStream", com.openitech.db.model.DbDataSource.class, null, null, "getUnicodeStream", null ); // NOI18N
            properties[PROPERTY_uniqueID] = new PropertyDescriptor ( "uniqueID", com.openitech.db.model.DbDataSource.class, "getUniqueID", "setUniqueID" ); // NOI18N
            properties[PROPERTY_updateRowFireOnly] = new PropertyDescriptor ( "updateRowFireOnly", com.openitech.db.model.DbDataSource.class, "isUpdateRowFireOnly", "setUpdateRowFireOnly" ); // NOI18N
            properties[PROPERTY_updateTableName] = new PropertyDescriptor ( "updateTableName", com.openitech.db.model.DbDataSource.class, "getUpdateTableName", "setUpdateTableName" ); // NOI18N
            properties[PROPERTY_URL] = new IndexedPropertyDescriptor ( "URL", com.openitech.db.model.DbDataSource.class, null, null, "getURL", null ); // NOI18N
            properties[PROPERTY_warnings] = new PropertyDescriptor ( "warnings", com.openitech.db.model.DbDataSource.class, "getWarnings", null ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
    
    // Here you can add code for customizing the properties array.
    
        return properties;     }//GEN-LAST:Properties
  
    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_activeRowChangeListener = 0;
    private static final int EVENT_listDataListener = 1;
    private static final int EVENT_propertyChangeListener = 2;
    private static final int EVENT_storeUpdatesListener = 3;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[4];
    
        try {
            eventSets[EVENT_activeRowChangeListener] = new EventSetDescriptor ( com.openitech.db.model.DbDataSource.class, "activeRowChangeListener", com.openitech.db.events.ActiveRowChangeListener.class, new String[] {"activeRowChanged", "fieldValueChanged"}, "addActiveRowChangeListener", "removeActiveRowChangeListener" ); // NOI18N
            eventSets[EVENT_listDataListener] = new EventSetDescriptor ( com.openitech.db.model.DbDataSource.class, "listDataListener", javax.swing.event.ListDataListener.class, new String[] {"contentsChanged", "intervalAdded", "intervalRemoved"}, "addListDataListener", "removeListDataListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( com.openitech.db.model.DbDataSource.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_storeUpdatesListener] = new EventSetDescriptor ( com.openitech.db.model.DbDataSource.class, "storeUpdatesListener", com.openitech.db.events.StoreUpdatesListener.class, new String[] {"storeUpdates"}, "addStoreUpdatesListener", "removeStoreUpdatesListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
    
    // Here you can add code for customizing the event sets array.
    
        return eventSets;     }//GEN-LAST:Events
  
    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_absolute0 = 0;
    private static final int METHOD_addActioneListener1 = 1;
    private static final int METHOD_addDefaultValues2 = 2;
    private static final int METHOD_addPropertyChangeListener3 = 3;
    private static final int METHOD_afterLast4 = 4;
    private static final int METHOD_beforeFirst5 = 5;
    private static final int METHOD_cancelRowUpdates6 = 6;
    private static final int METHOD_canUpdateRow7 = 7;
    private static final int METHOD_clearWarnings8 = 8;
    private static final int METHOD_close9 = 9;
    private static final int METHOD_deleteRow10 = 10;
    private static final int METHOD_filterChanged11 = 11;
    private static final int METHOD_findColumn12 = 12;
    private static final int METHOD_firePropertyChange13 = 13;
    private static final int METHOD_first14 = 14;
    private static final int METHOD_getArray15 = 15;
    private static final int METHOD_getAsciiStream16 = 16;
    private static final int METHOD_getBigDecimal17 = 17;
    private static final int METHOD_getBinaryStream18 = 18;
    private static final int METHOD_getBlob19 = 19;
    private static final int METHOD_getBoolean20 = 20;
    private static final int METHOD_getByte21 = 21;
    private static final int METHOD_getBytes22 = 22;
    private static final int METHOD_getCharacterStream23 = 23;
    private static final int METHOD_getClob24 = 24;
    private static final int METHOD_getDate25 = 25;
    private static final int METHOD_getDouble26 = 26;
    private static final int METHOD_getFloat27 = 27;
    private static final int METHOD_getInt28 = 28;
    private static final int METHOD_getLong29 = 29;
    private static final int METHOD_getObject30 = 30;
    private static final int METHOD_getPropertyChangeListeners31 = 31;
    private static final int METHOD_getRef32 = 32;
    private static final int METHOD_getShort33 = 33;
    private static final int METHOD_getString34 = 34;
    private static final int METHOD_getTime35 = 35;
    private static final int METHOD_getTimestamp36 = 36;
    private static final int METHOD_getType37 = 37;
    private static final int METHOD_getUnicodeStream38 = 38;
    private static final int METHOD_getURL39 = 39;
    private static final int METHOD_getValueAt40 = 40;
    private static final int METHOD_insertRow41 = 41;
    private static final int METHOD_isColumnReadOnly42 = 42;
    private static final int METHOD_last43 = 43;
    private static final int METHOD_loadData44 = 44;
    private static final int METHOD_lock45 = 45;
    private static final int METHOD_moveToCurrentRow46 = 46;
    private static final int METHOD_moveToInsertRow47 = 47;
    private static final int METHOD_next48 = 48;
    private static final int METHOD_previous49 = 49;
    private static final int METHOD_refreshRow50 = 50;
    private static final int METHOD_relative51 = 51;
    private static final int METHOD_reload52 = 52;
    private static final int METHOD_removeActionListener53 = 53;
    private static final int METHOD_removePropertyChangeListener54 = 54;
    private static final int METHOD_rowDeleted55 = 55;
    private static final int METHOD_rowInserted56 = 56;
    private static final int METHOD_rowUpdated57 = 57;
    private static final int METHOD_setDefaultValues58 = 58;
    private static final int METHOD_setParameters59 = 59;
    private static final int METHOD_startUpdate60 = 60;
    private static final int METHOD_unlock61 = 61;
    private static final int METHOD_updateArray62 = 62;
    private static final int METHOD_updateAsciiStream63 = 63;
    private static final int METHOD_updateBigDecimal64 = 64;
    private static final int METHOD_updateBinaryStream65 = 65;
    private static final int METHOD_updateBlob66 = 66;
    private static final int METHOD_updateBoolean67 = 67;
    private static final int METHOD_updateByte68 = 68;
    private static final int METHOD_updateBytes69 = 69;
    private static final int METHOD_updateCharacterStream70 = 70;
    private static final int METHOD_updateClob71 = 71;
    private static final int METHOD_updateDate72 = 72;
    private static final int METHOD_updateDouble73 = 73;
    private static final int METHOD_updateFloat74 = 74;
    private static final int METHOD_updateInt75 = 75;
    private static final int METHOD_updateLong76 = 76;
    private static final int METHOD_updateNull77 = 77;
    private static final int METHOD_updateObject78 = 78;
    private static final int METHOD_updateRef79 = 79;
    private static final int METHOD_updateRow80 = 80;
    private static final int METHOD_updateShort81 = 81;
    private static final int METHOD_updateString82 = 82;
    private static final int METHOD_updateTime83 = 83;
    private static final int METHOD_updateTimestamp84 = 84;
    private static final int METHOD_wasNull85 = 85;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[86];
    
        try {
            methods[METHOD_absolute0] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("absolute", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_absolute0].setDisplayName ( "" );
            methods[METHOD_addActioneListener1] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("addActioneListener", new Class[] {java.awt.event.ActionListener.class})); // NOI18N
            methods[METHOD_addActioneListener1].setDisplayName ( "" );
            methods[METHOD_addDefaultValues2] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("addDefaultValues", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_addDefaultValues2].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener3] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener3].setDisplayName ( "" );
            methods[METHOD_afterLast4] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("afterLast", new Class[] {})); // NOI18N
            methods[METHOD_afterLast4].setDisplayName ( "" );
            methods[METHOD_beforeFirst5] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("beforeFirst", new Class[] {})); // NOI18N
            methods[METHOD_beforeFirst5].setDisplayName ( "" );
            methods[METHOD_cancelRowUpdates6] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("cancelRowUpdates", new Class[] {})); // NOI18N
            methods[METHOD_cancelRowUpdates6].setDisplayName ( "" );
            methods[METHOD_canUpdateRow7] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("canUpdateRow", new Class[] {})); // NOI18N
            methods[METHOD_canUpdateRow7].setDisplayName ( "" );
            methods[METHOD_clearWarnings8] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("clearWarnings", new Class[] {})); // NOI18N
            methods[METHOD_clearWarnings8].setDisplayName ( "" );
            methods[METHOD_close9] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("close", new Class[] {})); // NOI18N
            methods[METHOD_close9].setDisplayName ( "" );
            methods[METHOD_deleteRow10] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("deleteRow", new Class[] {})); // NOI18N
            methods[METHOD_deleteRow10].setDisplayName ( "" );
            methods[METHOD_filterChanged11] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("filterChanged", new Class[] {})); // NOI18N
            methods[METHOD_filterChanged11].setDisplayName ( "" );
            methods[METHOD_findColumn12] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("findColumn", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_findColumn12].setDisplayName ( "" );
            methods[METHOD_firePropertyChange13] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Byte.TYPE, Byte.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange13].setDisplayName ( "" );
            methods[METHOD_first14] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("first", new Class[] {})); // NOI18N
            methods[METHOD_first14].setDisplayName ( "" );
            methods[METHOD_getArray15] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getArray", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getArray15].setDisplayName ( "" );
            methods[METHOD_getAsciiStream16] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getAsciiStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getAsciiStream16].setDisplayName ( "" );
            methods[METHOD_getBigDecimal17] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getBigDecimal", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBigDecimal17].setDisplayName ( "" );
            methods[METHOD_getBinaryStream18] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getBinaryStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBinaryStream18].setDisplayName ( "" );
            methods[METHOD_getBlob19] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getBlob", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBlob19].setDisplayName ( "" );
            methods[METHOD_getBoolean20] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getBoolean", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBoolean20].setDisplayName ( "" );
            methods[METHOD_getByte21] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getByte", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getByte21].setDisplayName ( "" );
            methods[METHOD_getBytes22] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getBytes", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getBytes22].setDisplayName ( "" );
            methods[METHOD_getCharacterStream23] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getCharacterStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getCharacterStream23].setDisplayName ( "" );
            methods[METHOD_getClob24] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getClob", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getClob24].setDisplayName ( "" );
            methods[METHOD_getDate25] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getDate", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getDate25].setDisplayName ( "" );
            methods[METHOD_getDouble26] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getDouble", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getDouble26].setDisplayName ( "" );
            methods[METHOD_getFloat27] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getFloat", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getFloat27].setDisplayName ( "" );
            methods[METHOD_getInt28] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getInt", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getInt28].setDisplayName ( "" );
            methods[METHOD_getLong29] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getLong", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getLong29].setDisplayName ( "" );
            methods[METHOD_getObject30] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getObject", new Class[] {java.lang.String.class, java.util.Map.class})); // NOI18N
            methods[METHOD_getObject30].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners31] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners31].setDisplayName ( "" );
            methods[METHOD_getRef32] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getRef", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getRef32].setDisplayName ( "" );
            methods[METHOD_getShort33] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getShort", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getShort33].setDisplayName ( "" );
            methods[METHOD_getString34] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getString", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getString34].setDisplayName ( "" );
            methods[METHOD_getTime35] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getTime", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTime35].setDisplayName ( "" );
            methods[METHOD_getTimestamp36] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getTimestamp", new Class[] {java.lang.String.class, java.util.Calendar.class})); // NOI18N
            methods[METHOD_getTimestamp36].setDisplayName ( "" );
            methods[METHOD_getType37] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getType", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getType37].setDisplayName ( "" );
            methods[METHOD_getUnicodeStream38] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getUnicodeStream", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getUnicodeStream38].setDisplayName ( "" );
            methods[METHOD_getURL39] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getURL", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getURL39].setDisplayName ( "" );
            methods[METHOD_getValueAt40] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("getValueAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getValueAt40].setDisplayName ( "" );
            methods[METHOD_insertRow41] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("insertRow", new Class[] {})); // NOI18N
            methods[METHOD_insertRow41].setDisplayName ( "" );
            methods[METHOD_isColumnReadOnly42] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("isColumnReadOnly", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_isColumnReadOnly42].setDisplayName ( "" );
            methods[METHOD_last43] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("last", new Class[] {})); // NOI18N
            methods[METHOD_last43].setDisplayName ( "" );
            methods[METHOD_loadData44] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("loadData", new Class[] {})); // NOI18N
            methods[METHOD_loadData44].setDisplayName ( "" );
            methods[METHOD_lock45] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("lock", new Class[] {})); // NOI18N
            methods[METHOD_lock45].setDisplayName ( "" );
            methods[METHOD_moveToCurrentRow46] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("moveToCurrentRow", new Class[] {})); // NOI18N
            methods[METHOD_moveToCurrentRow46].setDisplayName ( "" );
            methods[METHOD_moveToInsertRow47] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("moveToInsertRow", new Class[] {})); // NOI18N
            methods[METHOD_moveToInsertRow47].setDisplayName ( "" );
            methods[METHOD_next48] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("next", new Class[] {})); // NOI18N
            methods[METHOD_next48].setDisplayName ( "" );
            methods[METHOD_previous49] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("previous", new Class[] {})); // NOI18N
            methods[METHOD_previous49].setDisplayName ( "" );
            methods[METHOD_refreshRow50] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("refreshRow", new Class[] {})); // NOI18N
            methods[METHOD_refreshRow50].setDisplayName ( "" );
            methods[METHOD_relative51] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("relative", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_relative51].setDisplayName ( "" );
            methods[METHOD_reload52] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("reload", new Class[] {})); // NOI18N
            methods[METHOD_reload52].setDisplayName ( "" );
            methods[METHOD_removeActionListener53] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("removeActionListener", new Class[] {java.awt.event.ActionListener.class})); // NOI18N
            methods[METHOD_removeActionListener53].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener54] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener54].setDisplayName ( "" );
            methods[METHOD_rowDeleted55] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("rowDeleted", new Class[] {})); // NOI18N
            methods[METHOD_rowDeleted55].setDisplayName ( "" );
            methods[METHOD_rowInserted56] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("rowInserted", new Class[] {})); // NOI18N
            methods[METHOD_rowInserted56].setDisplayName ( "" );
            methods[METHOD_rowUpdated57] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("rowUpdated", new Class[] {})); // NOI18N
            methods[METHOD_rowUpdated57].setDisplayName ( "" );
            methods[METHOD_setDefaultValues58] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("setDefaultValues", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_setDefaultValues58].setDisplayName ( "" );
            methods[METHOD_setParameters59] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("setParameters", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_setParameters59].setDisplayName ( "" );
            methods[METHOD_startUpdate60] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("startUpdate", new Class[] {})); // NOI18N
            methods[METHOD_startUpdate60].setDisplayName ( "" );
            methods[METHOD_unlock61] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("unlock", new Class[] {})); // NOI18N
            methods[METHOD_unlock61].setDisplayName ( "" );
            methods[METHOD_updateArray62] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateArray", new Class[] {java.lang.String.class, java.sql.Array.class})); // NOI18N
            methods[METHOD_updateArray62].setDisplayName ( "" );
            methods[METHOD_updateAsciiStream63] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateAsciiStream", new Class[] {java.lang.String.class, java.io.InputStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_updateAsciiStream63].setDisplayName ( "" );
            methods[METHOD_updateBigDecimal64] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateBigDecimal", new Class[] {java.lang.String.class, java.math.BigDecimal.class})); // NOI18N
            methods[METHOD_updateBigDecimal64].setDisplayName ( "" );
            methods[METHOD_updateBinaryStream65] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateBinaryStream", new Class[] {java.lang.String.class, java.io.InputStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_updateBinaryStream65].setDisplayName ( "" );
            methods[METHOD_updateBlob66] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateBlob", new Class[] {java.lang.String.class, java.sql.Blob.class})); // NOI18N
            methods[METHOD_updateBlob66].setDisplayName ( "" );
            methods[METHOD_updateBoolean67] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateBoolean", new Class[] {java.lang.String.class, Boolean.TYPE})); // NOI18N
            methods[METHOD_updateBoolean67].setDisplayName ( "" );
            methods[METHOD_updateByte68] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateByte", new Class[] {java.lang.String.class, Byte.TYPE})); // NOI18N
            methods[METHOD_updateByte68].setDisplayName ( "" );
            methods[METHOD_updateBytes69] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateBytes", new Class[] {java.lang.String.class, byte[].class})); // NOI18N
            methods[METHOD_updateBytes69].setDisplayName ( "" );
            methods[METHOD_updateCharacterStream70] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateCharacterStream", new Class[] {java.lang.String.class, java.io.Reader.class, Integer.TYPE})); // NOI18N
            methods[METHOD_updateCharacterStream70].setDisplayName ( "" );
            methods[METHOD_updateClob71] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateClob", new Class[] {java.lang.String.class, java.sql.Clob.class})); // NOI18N
            methods[METHOD_updateClob71].setDisplayName ( "" );
            methods[METHOD_updateDate72] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateDate", new Class[] {java.lang.String.class, java.sql.Date.class})); // NOI18N
            methods[METHOD_updateDate72].setDisplayName ( "" );
            methods[METHOD_updateDouble73] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateDouble", new Class[] {Integer.TYPE, Double.TYPE})); // NOI18N
            methods[METHOD_updateDouble73].setDisplayName ( "" );
            methods[METHOD_updateFloat74] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateFloat", new Class[] {java.lang.String.class, Float.TYPE})); // NOI18N
            methods[METHOD_updateFloat74].setDisplayName ( "" );
            methods[METHOD_updateInt75] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateInt", new Class[] {java.lang.String.class, Integer.TYPE})); // NOI18N
            methods[METHOD_updateInt75].setDisplayName ( "" );
            methods[METHOD_updateLong76] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateLong", new Class[] {java.lang.String.class, Long.TYPE})); // NOI18N
            methods[METHOD_updateLong76].setDisplayName ( "" );
            methods[METHOD_updateNull77] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateNull", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_updateNull77].setDisplayName ( "" );
            methods[METHOD_updateObject78] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateObject", new Class[] {java.lang.String.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_updateObject78].setDisplayName ( "" );
            methods[METHOD_updateRef79] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateRef", new Class[] {java.lang.String.class, java.sql.Ref.class})); // NOI18N
            methods[METHOD_updateRef79].setDisplayName ( "" );
            methods[METHOD_updateRow80] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateRow", new Class[] {})); // NOI18N
            methods[METHOD_updateRow80].setDisplayName ( "" );
            methods[METHOD_updateShort81] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateShort", new Class[] {Integer.TYPE, Short.TYPE})); // NOI18N
            methods[METHOD_updateShort81].setDisplayName ( "" );
            methods[METHOD_updateString82] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateString", new Class[] {Integer.TYPE, java.lang.String.class})); // NOI18N
            methods[METHOD_updateString82].setDisplayName ( "" );
            methods[METHOD_updateTime83] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateTime", new Class[] {Integer.TYPE, java.sql.Time.class})); // NOI18N
            methods[METHOD_updateTime83].setDisplayName ( "" );
            methods[METHOD_updateTimestamp84] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("updateTimestamp", new Class[] {java.lang.String.class, java.sql.Timestamp.class})); // NOI18N
            methods[METHOD_updateTimestamp84].setDisplayName ( "" );
            methods[METHOD_wasNull85] = new MethodDescriptor ( com.openitech.db.model.DbDataSource.class.getMethod("wasNull", new Class[] {})); // NOI18N
            methods[METHOD_wasNull85].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
    
    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods
  
  
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
}

