/*
 * JDbControlButtonBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 14:57
 */

package com.openitech.db.components;

import java.beans.*;

/**
 * @author uros
 */
public class JDbControlButtonBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbControlButton.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_dataSource = 0;
    private static final int PROPERTY_icon = 1;
    private static final int PROPERTY_operation = 2;
    private static final int PROPERTY_useOperationIcon = 3;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[4];
    
        try {
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbControlButton.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_icon] = new PropertyDescriptor ( "icon", com.openitech.db.components.JDbControlButton.class, "getIcon", "setIcon" ); // NOI18N
            properties[PROPERTY_operation] = new PropertyDescriptor ( "operation", com.openitech.db.components.JDbControlButton.class, "getOperation", "setOperation" ); // NOI18N
            properties[PROPERTY_operation].setPreferred ( true );
            properties[PROPERTY_operation].setPropertyEditorClass ( com.openitech.editors.OperationsPropertyEditor.class );
            properties[PROPERTY_useOperationIcon] = new PropertyDescriptor ( "useOperationIcon", com.openitech.db.components.JDbControlButton.class, "isUseOperationIcon", "setUseOperationIcon" ); // NOI18N
            properties[PROPERTY_useOperationIcon].setPreferred ( true );
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
    private static final int METHOD_activeRowChanged0 = 0;
    private static final int METHOD_fieldValueChanged1 = 1;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[2];
    
        try {
            methods[METHOD_activeRowChanged0] = new MethodDescriptor(com.openitech.db.components.JDbControlButton.class.getMethod("activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_activeRowChanged0].setDisplayName ( "" );
            methods[METHOD_fieldValueChanged1] = new MethodDescriptor(com.openitech.db.components.JDbControlButton.class.getMethod("fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_fieldValueChanged1].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbControlButton.class.getSuperclass();
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

