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
 * JActionPanelBeanInfo.java
 *
 * Created on Ponedeljek, 4 februar 2008, 10:20
 */

package com.openitech.swing.framework;

import java.beans.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author uros
 */
public class JActionPanelBeanInfo extends SimpleBeanInfo {
  
    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.swing.framework.JActionPanel.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    
    // Here you can add code for customizing the BeanDescriptor.
    
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
  
  
    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_childUpdatesPanels = 0;
    private static final int PROPERTY_filterPanel = 1;
    private static final int PROPERTY_filterPanelContainer = 2;
    private static final int PROPERTY_informationPaneContainer = 3;
    private static final int PROPERTY_taskPane = 4;
    private static final int PROPERTY_taskPaneContainer = 5;
    private static final int PROPERTY_taskPanes = 6;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[7];
    
        try {
            properties[PROPERTY_childUpdatesPanels] = new PropertyDescriptor ( "childUpdatesPanels", com.openitech.swing.framework.JActionPanel.class, "isChildUpdatesPanels", "setChildUpdatesPanels" ); // NOI18N
            properties[PROPERTY_filterPanel] = new PropertyDescriptor ( "filterPanel", com.openitech.swing.framework.JActionPanel.class, "getFilterPanel", "setFilterPanel" ); // NOI18N
            properties[PROPERTY_filterPanel].setPreferred ( true );
            properties[PROPERTY_filterPanelContainer] = new PropertyDescriptor ( "filterPanelContainer", com.openitech.swing.framework.JActionPanel.class, "getFilterPanelContainer", "setFilterPanelContainer" ); // NOI18N
            properties[PROPERTY_filterPanelContainer].setPreferred ( true );
            properties[PROPERTY_informationPaneContainer] = new PropertyDescriptor ( "informationPaneContainer", com.openitech.swing.framework.JActionPanel.class, "getInformationPaneContainer", "setInformationPaneContainer" ); // NOI18N
            properties[PROPERTY_informationPaneContainer].setPreferred ( true );
            properties[PROPERTY_taskPane] = new PropertyDescriptor ( "taskPane", com.openitech.swing.framework.JActionPanel.class, "getTaskPane", "setTaskPane" ); // NOI18N
            properties[PROPERTY_taskPane].setPreferred ( true );
            properties[PROPERTY_taskPaneContainer] = new PropertyDescriptor ( "taskPaneContainer", com.openitech.swing.framework.JActionPanel.class, "getTaskPaneContainer", "setTaskPaneContainer" ); // NOI18N
            properties[PROPERTY_taskPaneContainer].setPreferred ( true );
            properties[PROPERTY_taskPanes] = new PropertyDescriptor ( "taskPanes", com.openitech.swing.framework.JActionPanel.class, "getTaskPanes", null ); // NOI18N
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
    private static final int METHOD_add0 = 0;
    private static final int METHOD_remove1 = 1;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[2];
    
        try {
            methods[METHOD_add0] = new MethodDescriptor(com.openitech.swing.framework.JActionPanel.class.getMethod("add", new Class[] {org.jdesktop.swingx.JXTaskPane.class})); // NOI18N
            methods[METHOD_add0].setDisplayName ( "" );
            methods[METHOD_remove1] = new MethodDescriptor(com.openitech.swing.framework.JActionPanel.class.getMethod("remove", new Class[] {org.jdesktop.swingx.JXTaskPane.class})); // NOI18N
            methods[METHOD_remove1].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
    
    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods
  
  
    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx
  
  
    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.swing.framework.JActionPanel.class.getSuperclass();
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

