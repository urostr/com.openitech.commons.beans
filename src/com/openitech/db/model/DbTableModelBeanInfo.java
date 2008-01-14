/*
 * DbTableModelBeanInfo.java
 *
 * Created on Sobota, 12 januar 2008, 20:29
 */

package com.openitech.db.model;

import java.beans.*;

/**
 * @author uros
 */
public class DbTableModelBeanInfo extends SimpleBeanInfo {
  
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.DbTableModel.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    
    // Here you can add code for customizing the BeanDescriptor.
    
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
  
  
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_columns = 0;
    private static final int PROPERTY_dataSource = 1;
    private static final int PROPERTY_separator = 2;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[3];
    
        try {
            properties[PROPERTY_columns] = new PropertyDescriptor ( "columns", com.openitech.db.model.DbTableModel.class, "getColumns", "setColumns" ); // NOI18N
            properties[PROPERTY_columns].setPreferred ( true );
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.model.DbTableModel.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_separator] = new PropertyDescriptor ( "separator", com.openitech.db.model.DbTableModel.class, "getSeparator", "setSeparator" ); // NOI18N
            properties[PROPERTY_separator].setPreferred ( true );
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
    
    // Here you can add code for customizing the properties array.
    
        return properties;     }//GEN-LAST:Properties
  
    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_activeRowChangeListener = 0;
    private static final int EVENT_tableModelListener = 1;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[2];
    
        try {
            eventSets[EVENT_activeRowChangeListener] = new EventSetDescriptor ( com.openitech.db.model.DbTableModel.class, "activeRowChangeListener", com.openitech.db.events.ActiveRowChangeListener.class, new String[] {"activeRowChanged", "fieldValueChanged"}, "addActiveRowChangeListener", "removeActiveRowChangeListener" ); // NOI18N
            eventSets[EVENT_tableModelListener] = new EventSetDescriptor ( com.openitech.db.model.DbTableModel.class, "tableModelListener", javax.swing.event.TableModelListener.class, new String[] {"tableChanged"}, "addTableModelListener", "removeTableModelListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
    
    // Here you can add code for customizing the event sets array.
    
        return eventSets;     }//GEN-LAST:Events
  
    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_activeRowChanged0 = 0;
    private static final int METHOD_contentsChanged1 = 1;
    private static final int METHOD_fieldValueChanged2 = 2;
    private static final int METHOD_findColumn3 = 3;
    private static final int METHOD_fireTableCellUpdated4 = 4;
    private static final int METHOD_fireTableChanged5 = 5;
    private static final int METHOD_fireTableDataChanged6 = 6;
    private static final int METHOD_fireTableRowsDeleted7 = 7;
    private static final int METHOD_fireTableRowsInserted8 = 8;
    private static final int METHOD_fireTableRowsUpdated9 = 9;
    private static final int METHOD_fireTableStructureChanged10 = 10;
    private static final int METHOD_getListeners11 = 11;
    private static final int METHOD_getValueAt12 = 12;
    private static final int METHOD_intervalAdded13 = 13;
    private static final int METHOD_intervalRemoved14 = 14;
    private static final int METHOD_isCellEditable15 = 15;
    private static final int METHOD_putAllEditors16 = 16;
    private static final int METHOD_putAllFunctions17 = 17;
    private static final int METHOD_putAllRenderers18 = 18;
    private static final int METHOD_putEditor19 = 19;
    private static final int METHOD_putFunction20 = 20;
    private static final int METHOD_putRenderer21 = 21;
    private static final int METHOD_removeEditor22 = 22;
    private static final int METHOD_removeFunction23 = 23;
    private static final int METHOD_removeRenderer24 = 24;
    private static final int METHOD_setValueAt25 = 25;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[26];
    
        try {
            methods[METHOD_activeRowChanged0] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_activeRowChanged0].setDisplayName ( "" );
            methods[METHOD_contentsChanged1] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("contentsChanged", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_contentsChanged1].setDisplayName ( "" );
            methods[METHOD_fieldValueChanged2] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_fieldValueChanged2].setDisplayName ( "" );
            methods[METHOD_findColumn3] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("findColumn", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_findColumn3].setDisplayName ( "" );
            methods[METHOD_fireTableCellUpdated4] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableCellUpdated", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableCellUpdated4].setDisplayName ( "" );
            methods[METHOD_fireTableChanged5] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableChanged", new Class[] {javax.swing.event.TableModelEvent.class})); // NOI18N
            methods[METHOD_fireTableChanged5].setDisplayName ( "" );
            methods[METHOD_fireTableDataChanged6] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableDataChanged", new Class[] {})); // NOI18N
            methods[METHOD_fireTableDataChanged6].setDisplayName ( "" );
            methods[METHOD_fireTableRowsDeleted7] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableRowsDeleted", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsDeleted7].setDisplayName ( "" );
            methods[METHOD_fireTableRowsInserted8] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableRowsInserted", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsInserted8].setDisplayName ( "" );
            methods[METHOD_fireTableRowsUpdated9] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableRowsUpdated", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsUpdated9].setDisplayName ( "" );
            methods[METHOD_fireTableStructureChanged10] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("fireTableStructureChanged", new Class[] {})); // NOI18N
            methods[METHOD_fireTableStructureChanged10].setDisplayName ( "" );
            methods[METHOD_getListeners11] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners11].setDisplayName ( "" );
            methods[METHOD_getValueAt12] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("getValueAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getValueAt12].setDisplayName ( "" );
            methods[METHOD_intervalAdded13] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("intervalAdded", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalAdded13].setDisplayName ( "" );
            methods[METHOD_intervalRemoved14] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("intervalRemoved", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalRemoved14].setDisplayName ( "" );
            methods[METHOD_isCellEditable15] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("isCellEditable", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_isCellEditable15].setDisplayName ( "" );
            methods[METHOD_putAllEditors16] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putAllEditors", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllEditors16].setDisplayName ( "" );
            methods[METHOD_putAllFunctions17] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putAllFunctions", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllFunctions17].setDisplayName ( "" );
            methods[METHOD_putAllRenderers18] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putAllRenderers", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllRenderers18].setDisplayName ( "" );
            methods[METHOD_putEditor19] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putEditor", new Class[] {java.lang.String.class, java.lang.Class.class})); // NOI18N
            methods[METHOD_putEditor19].setDisplayName ( "" );
            methods[METHOD_putFunction20] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putFunction", new Class[] {java.lang.String.class, com.openitech.db.model.DbTableModel.ColumnDescriptor.ValueMethod.Method.class})); // NOI18N
            methods[METHOD_putFunction20].setDisplayName ( "" );
            methods[METHOD_putRenderer21] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("putRenderer", new Class[] {java.lang.String.class, java.lang.Class.class})); // NOI18N
            methods[METHOD_putRenderer21].setDisplayName ( "" );
            methods[METHOD_removeEditor22] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("removeEditor", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeEditor22].setDisplayName ( "" );
            methods[METHOD_removeFunction23] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("removeFunction", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeFunction23].setDisplayName ( "" );
            methods[METHOD_removeRenderer24] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("removeRenderer", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeRenderer24].setDisplayName ( "" );
            methods[METHOD_setValueAt25] = new MethodDescriptor ( com.openitech.db.model.DbTableModel.class.getMethod("setValueAt", new Class[] {java.lang.Object.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setValueAt25].setDisplayName ( "" );
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

