/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * SequentialTreeTableModelBeanInfo.java
 *
 * Created on Sobota, 12 januar 2008, 20:31
 */

package com.openitech.db.model.tree;

import java.beans.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author uros
 */
public class SequentialTreeTableModelBeanInfo extends SimpleBeanInfo {
  
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.tree.SequentialTreeTableModel.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    
    // Here you can add code for customizing the BeanDescriptor.
    
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
  
  
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_nodeColumns = 0;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[1];
    
        try {
            properties[PROPERTY_nodeColumns] = new PropertyDescriptor ( "nodeColumns", com.openitech.db.model.tree.SequentialTreeTableModel.class, "getNodeColumns", "setNodeColumns" ); // NOI18N
            properties[PROPERTY_nodeColumns].setPreferred ( true );
        }
        catch(IntrospectionException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, e.getMessage(), e);
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
    private static final int METHOD_getChild11 = 11;
    private static final int METHOD_getChildCount12 = 12;
    private static final int METHOD_getIndexOfChild13 = 13;
    private static final int METHOD_getListeners14 = 14;
    private static final int METHOD_getPathToRoot15 = 15;
    private static final int METHOD_getValueAt16 = 16;
    private static final int METHOD_intervalAdded17 = 17;
    private static final int METHOD_intervalRemoved18 = 18;
    private static final int METHOD_isCellEditable19 = 19;
    private static final int METHOD_isLeaf20 = 20;
    private static final int METHOD_putAllEditors21 = 21;
    private static final int METHOD_putAllFunctions22 = 22;
    private static final int METHOD_putAllRenderers23 = 23;
    private static final int METHOD_putEditor24 = 24;
    private static final int METHOD_putFunction25 = 25;
    private static final int METHOD_putRenderer26 = 26;
    private static final int METHOD_removeEditor27 = 27;
    private static final int METHOD_removeFunction28 = 28;
    private static final int METHOD_removeRenderer29 = 29;
    private static final int METHOD_setValueAt30 = 30;
    private static final int METHOD_valueForPathChanged31 = 31;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[32];
    
        try {
            methods[METHOD_activeRowChanged0] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_activeRowChanged0].setDisplayName ( "" );
            methods[METHOD_contentsChanged1] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("contentsChanged", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_contentsChanged1].setDisplayName ( "" );
            methods[METHOD_fieldValueChanged2] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_fieldValueChanged2].setDisplayName ( "" );
            methods[METHOD_findColumn3] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("findColumn", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_findColumn3].setDisplayName ( "" );
            methods[METHOD_fireTableCellUpdated4] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableCellUpdated", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableCellUpdated4].setDisplayName ( "" );
            methods[METHOD_fireTableChanged5] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableChanged", new Class[] {javax.swing.event.TableModelEvent.class})); // NOI18N
            methods[METHOD_fireTableChanged5].setDisplayName ( "" );
            methods[METHOD_fireTableDataChanged6] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableDataChanged", new Class[] {})); // NOI18N
            methods[METHOD_fireTableDataChanged6].setDisplayName ( "" );
            methods[METHOD_fireTableRowsDeleted7] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableRowsDeleted", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsDeleted7].setDisplayName ( "" );
            methods[METHOD_fireTableRowsInserted8] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableRowsInserted", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsInserted8].setDisplayName ( "" );
            methods[METHOD_fireTableRowsUpdated9] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableRowsUpdated", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_fireTableRowsUpdated9].setDisplayName ( "" );
            methods[METHOD_fireTableStructureChanged10] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("fireTableStructureChanged", new Class[] {})); // NOI18N
            methods[METHOD_fireTableStructureChanged10].setDisplayName ( "" );
            methods[METHOD_getChild11] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getChild", new Class[] {java.lang.Object.class, Integer.TYPE})); // NOI18N
            methods[METHOD_getChild11].setDisplayName ( "" );
            methods[METHOD_getChildCount12] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getChildCount", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getChildCount12].setDisplayName ( "" );
            methods[METHOD_getIndexOfChild13] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getIndexOfChild", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_getIndexOfChild13].setDisplayName ( "" );
            methods[METHOD_getListeners14] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners14].setDisplayName ( "" );
            methods[METHOD_getPathToRoot15] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getPathToRoot", new Class[] {org.jdesktop.swingx.treetable.TreeTableNode.class})); // NOI18N
            methods[METHOD_getPathToRoot15].setDisplayName ( "" );
            methods[METHOD_getValueAt16] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("getValueAt", new Class[] {java.lang.Object.class, Integer.TYPE})); // NOI18N
            methods[METHOD_getValueAt16].setDisplayName ( "" );
            methods[METHOD_intervalAdded17] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("intervalAdded", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalAdded17].setDisplayName ( "" );
            methods[METHOD_intervalRemoved18] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("intervalRemoved", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalRemoved18].setDisplayName ( "" );
            methods[METHOD_isCellEditable19] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("isCellEditable", new Class[] {java.lang.Object.class, Integer.TYPE})); // NOI18N
            methods[METHOD_isCellEditable19].setDisplayName ( "" );
            methods[METHOD_isLeaf20] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("isLeaf", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_isLeaf20].setDisplayName ( "" );
            methods[METHOD_putAllEditors21] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putAllEditors", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllEditors21].setDisplayName ( "" );
            methods[METHOD_putAllFunctions22] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putAllFunctions", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllFunctions22].setDisplayName ( "" );
            methods[METHOD_putAllRenderers23] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putAllRenderers", new Class[] {java.util.Map.class})); // NOI18N
            methods[METHOD_putAllRenderers23].setDisplayName ( "" );
            methods[METHOD_putEditor24] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putEditor", new Class[] {java.lang.String.class, java.lang.Class.class})); // NOI18N
            methods[METHOD_putEditor24].setDisplayName ( "" );
            methods[METHOD_putFunction25] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putFunction", new Class[] {java.lang.String.class, com.openitech.db.model.DbTableModel.ColumnDescriptor.ValueMethod.Method.class})); // NOI18N
            methods[METHOD_putFunction25].setDisplayName ( "" );
            methods[METHOD_putRenderer26] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("putRenderer", new Class[] {java.lang.String.class, java.lang.Class.class})); // NOI18N
            methods[METHOD_putRenderer26].setDisplayName ( "" );
            methods[METHOD_removeEditor27] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("removeEditor", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeEditor27].setDisplayName ( "" );
            methods[METHOD_removeFunction28] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("removeFunction", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeFunction28].setDisplayName ( "" );
            methods[METHOD_removeRenderer29] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("removeRenderer", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeRenderer29].setDisplayName ( "" );
            methods[METHOD_setValueAt30] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("setValueAt", new Class[] {java.lang.Object.class, java.lang.Object.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setValueAt30].setDisplayName ( "" );
            methods[METHOD_valueForPathChanged31] = new MethodDescriptor ( com.openitech.db.model.tree.SequentialTreeTableModel.class.getMethod("valueForPathChanged", new Class[] {javax.swing.tree.TreePath.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_valueForPathChanged31].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
    
    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods
  
  
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
  
  
    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.model.tree.SequentialTreeTableModel.class.getSuperclass();
        BeanInfo sbi = null;
        try {
            sbi = Introspector.getBeanInfo(superclass);//GEN-HEADEREND:Superclass
  
  // Here you can add code for customizing the Superclass BeanInfo.
  
            } catch(IntrospectionException ex) { }  return new BeanInfo[] { sbi }; }//GEN-LAST:Superclass
  
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

