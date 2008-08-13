/*
 * DbSifrantModelBeanInfo.java
 *
 * Created on Ponedeljek, 11 avgust 2008, 10:53
 */

package com.openitech.db.model;

import java.beans.*;

/**
 * @author uros
 */
public class DbSifrantModelBeanInfo extends SimpleBeanInfo {
  
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.DbSifrantModel.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    
    // Here you can add code for customizing the BeanDescriptor.
    
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
  
  
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_dataSource = 0;
    private static final int PROPERTY_elementAt = 1;
    private static final int PROPERTY_keyColumnName = 2;
    private static final int PROPERTY_listDataListeners = 3;
    private static final int PROPERTY_selectedIndex = 4;
    private static final int PROPERTY_selectedItem = 5;
    private static final int PROPERTY_separator = 6;
    private static final int PROPERTY_sifrantOpis = 7;
    private static final int PROPERTY_sifrantSkupina = 8;
    private static final int PROPERTY_size = 9;
    private static final int PROPERTY_textNotDefined = 10;
    private static final int PROPERTY_updatingEntries = 11;
    private static final int PROPERTY_valueColumnNames = 12;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[13];
    
        try {
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.model.DbSifrantModel.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setHidden ( true );
            properties[PROPERTY_elementAt] = new IndexedPropertyDescriptor ( "elementAt", com.openitech.db.model.DbSifrantModel.class, null, null, "getElementAt", null ); // NOI18N
            properties[PROPERTY_elementAt].setHidden ( true );
            properties[PROPERTY_keyColumnName] = new PropertyDescriptor ( "keyColumnName", com.openitech.db.model.DbSifrantModel.class, "getKeyColumnName", "setKeyColumnName" ); // NOI18N
            properties[PROPERTY_keyColumnName].setHidden ( true );
            properties[PROPERTY_listDataListeners] = new PropertyDescriptor ( "listDataListeners", com.openitech.db.model.DbSifrantModel.class, "getListDataListeners", null ); // NOI18N
            properties[PROPERTY_listDataListeners].setHidden ( true );
            properties[PROPERTY_selectedIndex] = new PropertyDescriptor ( "selectedIndex", com.openitech.db.model.DbSifrantModel.class, "getSelectedIndex", "setSelectedIndex" ); // NOI18N
            properties[PROPERTY_selectedIndex].setHidden ( true );
            properties[PROPERTY_selectedItem] = new PropertyDescriptor ( "selectedItem", com.openitech.db.model.DbSifrantModel.class, "getSelectedItem", "setSelectedItem" ); // NOI18N
            properties[PROPERTY_selectedItem].setHidden ( true );
            properties[PROPERTY_separator] = new PropertyDescriptor ( "separator", com.openitech.db.model.DbSifrantModel.class, "getSeparator", null ); // NOI18N
            properties[PROPERTY_separator].setHidden ( true );
            properties[PROPERTY_sifrantOpis] = new PropertyDescriptor ( "sifrantOpis", com.openitech.db.model.DbSifrantModel.class, "getSifrantOpis", "setSifrantOpis" ); // NOI18N
            properties[PROPERTY_sifrantOpis].setPreferred ( true );
            properties[PROPERTY_sifrantSkupina] = new PropertyDescriptor ( "sifrantSkupina", com.openitech.db.model.DbSifrantModel.class, "getSifrantSkupina", "setSifrantSkupina" ); // NOI18N
            properties[PROPERTY_sifrantSkupina].setPreferred ( true );
            properties[PROPERTY_size] = new PropertyDescriptor ( "size", com.openitech.db.model.DbSifrantModel.class, "getSize", null ); // NOI18N
            properties[PROPERTY_size].setHidden ( true );
            properties[PROPERTY_textNotDefined] = new PropertyDescriptor ( "textNotDefined", com.openitech.db.model.DbSifrantModel.class, "getTextNotDefined", "setTextNotDefined" ); // NOI18N
            properties[PROPERTY_textNotDefined].setPreferred ( true );
            properties[PROPERTY_updatingEntries] = new PropertyDescriptor ( "updatingEntries", com.openitech.db.model.DbSifrantModel.class, "isUpdatingEntries", null ); // NOI18N
            properties[PROPERTY_updatingEntries].setHidden ( true );
            properties[PROPERTY_valueColumnNames] = new PropertyDescriptor ( "valueColumnNames", com.openitech.db.model.DbSifrantModel.class, "getValueColumnNames", "setValueColumnNames" ); // NOI18N
            properties[PROPERTY_valueColumnNames].setHidden ( true );
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
            eventSets[EVENT_listDataListener] = new EventSetDescriptor ( com.openitech.db.model.DbSifrantModel.class, "listDataListener", javax.swing.event.ListDataListener.class, new String[] {"contentsChanged", "intervalAdded", "intervalRemoved"}, "addListDataListener", "removeListDataListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
    
    // Here you can add code for customizing the event sets array.
    
        return eventSets;     }//GEN-LAST:Events
  
    // Method identifiers//GEN-FIRST:Methods

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[0];//GEN-HEADEREND:Methods
    
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

