/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.beans.*;

/**
 *
 * @author uros
 */
public class DbDataSourceIndexBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.DbDataSourceIndex.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_dataSource = 0;
    private static final int PROPERTY_keys = 1;
    private static final int PROPERTY_uniqueKeys = 2;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[3];
    
        try {
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.model.DbDataSourceIndex.class, "getDataSource", null ); // NOI18N
            properties[PROPERTY_keys] = new PropertyDescriptor ( "keys", com.openitech.db.model.DbDataSourceIndex.class, "getKeys", "setKeys" ); // NOI18N
            properties[PROPERTY_uniqueKeys] = new PropertyDescriptor ( "uniqueKeys", com.openitech.db.model.DbDataSourceIndex.class, "getUniqueKeys", null ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_listDataListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
        try {
            eventSets[EVENT_listDataListener] = new EventSetDescriptor ( com.openitech.db.model.DbDataSourceIndex.class, "listDataListener", javax.swing.event.ListDataListener.class, new String[] {"intervalAdded", "intervalRemoved", "contentsChanged"}, "addListDataListener", "removeListDataListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_activeRowChanged0 = 0;
    private static final int METHOD_addKeys1 = 1;
    private static final int METHOD_addKeys2 = 2;
    private static final int METHOD_contentsChanged3 = 3;
    private static final int METHOD_equals4 = 4;
    private static final int METHOD_fieldValueChanged5 = 5;
    private static final int METHOD_findRow6 = 6;
    private static final int METHOD_findRow7 = 7;
    private static final int METHOD_findRows8 = 8;
    private static final int METHOD_findRows9 = 9;
    private static final int METHOD_getRowKey10 = 10;
    private static final int METHOD_getRowKeys11 = 11;
    private static final int METHOD_intervalAdded12 = 12;
    private static final int METHOD_intervalRemoved13 = 13;
    private static final int METHOD_setDataSource14 = 14;
    private static final int METHOD_setKeys15 = 15;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[16];
    
        try {
            methods[METHOD_activeRowChanged0] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_activeRowChanged0].setDisplayName ( "" );
            methods[METHOD_addKeys1] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("addKeys", new Class[] {java.util.List.class})); // NOI18N
            methods[METHOD_addKeys1].setDisplayName ( "" );
            methods[METHOD_addKeys2] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("addKeys", new Class[] {java.lang.String[].class})); // NOI18N
            methods[METHOD_addKeys2].setDisplayName ( "" );
            methods[METHOD_contentsChanged3] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("contentsChanged", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_contentsChanged3].setDisplayName ( "" );
            methods[METHOD_equals4] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("equals", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_equals4].setDisplayName ( "" );
            methods[METHOD_fieldValueChanged5] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_fieldValueChanged5].setDisplayName ( "" );
            methods[METHOD_findRow6] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("findRow", new Class[] {java.lang.Object[].class})); // NOI18N
            methods[METHOD_findRow6].setDisplayName ( "" );
            methods[METHOD_findRow7] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("findRow", new Class[] {com.openitech.db.model.DbDataSourceIndex.DbIndexKey.class})); // NOI18N
            methods[METHOD_findRow7].setDisplayName ( "" );
            methods[METHOD_findRows8] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("findRows", new Class[] {java.lang.Object[].class})); // NOI18N
            methods[METHOD_findRows8].setDisplayName ( "" );
            methods[METHOD_findRows9] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("findRows", new Class[] {com.openitech.db.model.DbDataSourceIndex.DbIndexKey.class})); // NOI18N
            methods[METHOD_findRows9].setDisplayName ( "" );
            methods[METHOD_getRowKey10] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("getRowKey", new Class[] {java.lang.Integer.class})); // NOI18N
            methods[METHOD_getRowKey10].setDisplayName ( "" );
            methods[METHOD_getRowKeys11] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("getRowKeys", new Class[] {java.util.Set.class})); // NOI18N
            methods[METHOD_getRowKeys11].setDisplayName ( "" );
            methods[METHOD_intervalAdded12] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("intervalAdded", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalAdded12].setDisplayName ( "" );
            methods[METHOD_intervalRemoved13] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("intervalRemoved", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalRemoved13].setDisplayName ( "" );
            methods[METHOD_setDataSource14] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("setDataSource", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_setDataSource14].setDisplayName ( "" );
            methods[METHOD_setKeys15] = new MethodDescriptor(com.openitech.db.model.DbDataSourceIndex.class.getMethod("setKeys", new Class[] {java.util.List.class})); // NOI18N
            methods[METHOD_setKeys15].setDisplayName ( "" );
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

