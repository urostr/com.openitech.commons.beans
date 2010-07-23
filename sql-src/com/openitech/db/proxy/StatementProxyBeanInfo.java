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
public class StatementProxyBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.proxy.StatementProxy.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    protected static final int PROPERTY_closed = 0;
    protected static final int PROPERTY_connection = 1;
    protected static final int PROPERTY_cursorName = 2;
    protected static final int PROPERTY_escapeProcessing = 3;
    protected static final int PROPERTY_fetchDirection = 4;
    protected static final int PROPERTY_fetchSize = 5;
    protected static final int PROPERTY_generatedKeys = 6;
    protected static final int PROPERTY_maxFieldSize = 7;
    protected static final int PROPERTY_maxRows = 8;
    protected static final int PROPERTY_moreResults = 9;
    protected static final int PROPERTY_poolable = 10;
    protected static final int PROPERTY_queryTimeout = 11;
    protected static final int PROPERTY_resultSet = 12;
    protected static final int PROPERTY_resultSetConcurrency = 13;
    protected static final int PROPERTY_resultSetHoldability = 14;
    protected static final int PROPERTY_resultSetType = 15;
    protected static final int PROPERTY_updateCount = 16;
    protected static final int PROPERTY_warnings = 17;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[18];
    
        try {
            properties[PROPERTY_closed] = new PropertyDescriptor ( "closed", com.openitech.db.proxy.StatementProxy.class, "isClosed", null ); // NOI18N
            properties[PROPERTY_connection] = new PropertyDescriptor ( "connection", com.openitech.db.proxy.StatementProxy.class, "getConnection", null ); // NOI18N
            properties[PROPERTY_cursorName] = new PropertyDescriptor ( "cursorName", com.openitech.db.proxy.StatementProxy.class, null, "setCursorName" ); // NOI18N
            properties[PROPERTY_escapeProcessing] = new PropertyDescriptor ( "escapeProcessing", com.openitech.db.proxy.StatementProxy.class, null, "setEscapeProcessing" ); // NOI18N
            properties[PROPERTY_fetchDirection] = new PropertyDescriptor ( "fetchDirection", com.openitech.db.proxy.StatementProxy.class, "getFetchDirection", "setFetchDirection" ); // NOI18N
            properties[PROPERTY_fetchSize] = new PropertyDescriptor ( "fetchSize", com.openitech.db.proxy.StatementProxy.class, "getFetchSize", "setFetchSize" ); // NOI18N
            properties[PROPERTY_generatedKeys] = new PropertyDescriptor ( "generatedKeys", com.openitech.db.proxy.StatementProxy.class, "getGeneratedKeys", null ); // NOI18N
            properties[PROPERTY_maxFieldSize] = new PropertyDescriptor ( "maxFieldSize", com.openitech.db.proxy.StatementProxy.class, "getMaxFieldSize", "setMaxFieldSize" ); // NOI18N
            properties[PROPERTY_maxRows] = new PropertyDescriptor ( "maxRows", com.openitech.db.proxy.StatementProxy.class, "getMaxRows", "setMaxRows" ); // NOI18N
            properties[PROPERTY_moreResults] = new PropertyDescriptor ( "moreResults", com.openitech.db.proxy.StatementProxy.class, "getMoreResults", null ); // NOI18N
            properties[PROPERTY_poolable] = new PropertyDescriptor ( "poolable", com.openitech.db.proxy.StatementProxy.class, "isPoolable", "setPoolable" ); // NOI18N
            properties[PROPERTY_queryTimeout] = new PropertyDescriptor ( "queryTimeout", com.openitech.db.proxy.StatementProxy.class, "getQueryTimeout", "setQueryTimeout" ); // NOI18N
            properties[PROPERTY_resultSet] = new PropertyDescriptor ( "resultSet", com.openitech.db.proxy.StatementProxy.class, "getResultSet", null ); // NOI18N
            properties[PROPERTY_resultSetConcurrency] = new PropertyDescriptor ( "resultSetConcurrency", com.openitech.db.proxy.StatementProxy.class, "getResultSetConcurrency", null ); // NOI18N
            properties[PROPERTY_resultSetHoldability] = new PropertyDescriptor ( "resultSetHoldability", com.openitech.db.proxy.StatementProxy.class, "getResultSetHoldability", null ); // NOI18N
            properties[PROPERTY_resultSetType] = new PropertyDescriptor ( "resultSetType", com.openitech.db.proxy.StatementProxy.class, "getResultSetType", null ); // NOI18N
            properties[PROPERTY_updateCount] = new PropertyDescriptor ( "updateCount", com.openitech.db.proxy.StatementProxy.class, "getUpdateCount", null ); // NOI18N
            properties[PROPERTY_warnings] = new PropertyDescriptor ( "warnings", com.openitech.db.proxy.StatementProxy.class, "getWarnings", null ); // NOI18N
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
    protected static final int METHOD_cancel1 = 1;
    protected static final int METHOD_clearBatch2 = 2;
    protected static final int METHOD_clearWarnings3 = 3;
    protected static final int METHOD_close4 = 4;
    protected static final int METHOD_execute5 = 5;
    protected static final int METHOD_execute6 = 6;
    protected static final int METHOD_execute7 = 7;
    protected static final int METHOD_execute8 = 8;
    protected static final int METHOD_executeBatch9 = 9;
    protected static final int METHOD_executeQuery10 = 10;
    protected static final int METHOD_executeUpdate11 = 11;
    protected static final int METHOD_executeUpdate12 = 12;
    protected static final int METHOD_executeUpdate13 = 13;
    protected static final int METHOD_executeUpdate14 = 14;
    protected static final int METHOD_isWrapperFor15 = 15;
    protected static final int METHOD_unwrap16 = 16;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[17];
    
        try {
            methods[METHOD_addBatch0] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("addBatch", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_addBatch0].setDisplayName ( "" );
            methods[METHOD_cancel1] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("cancel", new Class[] {})); // NOI18N
            methods[METHOD_cancel1].setDisplayName ( "" );
            methods[METHOD_clearBatch2] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("clearBatch", new Class[] {})); // NOI18N
            methods[METHOD_clearBatch2].setDisplayName ( "" );
            methods[METHOD_clearWarnings3] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("clearWarnings", new Class[] {})); // NOI18N
            methods[METHOD_clearWarnings3].setDisplayName ( "" );
            methods[METHOD_close4] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("close", new Class[] {})); // NOI18N
            methods[METHOD_close4].setDisplayName ( "" );
            methods[METHOD_execute5] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_execute5].setDisplayName ( "" );
            methods[METHOD_execute6] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_execute6].setDisplayName ( "" );
            methods[METHOD_execute7] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, int[].class})); // NOI18N
            methods[METHOD_execute7].setDisplayName ( "" );
            methods[METHOD_execute8] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("execute", new Class[] {java.lang.String.class, java.lang.String[].class})); // NOI18N
            methods[METHOD_execute8].setDisplayName ( "" );
            methods[METHOD_executeBatch9] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeBatch", new Class[] {})); // NOI18N
            methods[METHOD_executeBatch9].setDisplayName ( "" );
            methods[METHOD_executeQuery10] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeQuery", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_executeQuery10].setDisplayName ( "" );
            methods[METHOD_executeUpdate11] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_executeUpdate11].setDisplayName ( "" );
            methods[METHOD_executeUpdate12] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_executeUpdate12].setDisplayName ( "" );
            methods[METHOD_executeUpdate13] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, int[].class})); // NOI18N
            methods[METHOD_executeUpdate13].setDisplayName ( "" );
            methods[METHOD_executeUpdate14] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("executeUpdate", new Class[] {java.lang.String.class, java.lang.String[].class})); // NOI18N
            methods[METHOD_executeUpdate14].setDisplayName ( "" );
            methods[METHOD_isWrapperFor15] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("isWrapperFor", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_isWrapperFor15].setDisplayName ( "" );
            methods[METHOD_unwrap16] = new MethodDescriptor(com.openitech.db.proxy.StatementProxy.class.getMethod("unwrap", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_unwrap16].setDisplayName ( "" );
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

