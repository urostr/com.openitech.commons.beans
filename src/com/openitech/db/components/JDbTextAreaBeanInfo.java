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
 * JDbTextAreaBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 15:38
 */

package com.openitech.db.components;

import java.beans.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author uros
 */
public class JDbTextAreaBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbTextArea.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_columnName = 0;
    private static final int PROPERTY_dataSource = 1;
    private static final int PROPERTY_dbFieldObserver = 2;
    private static final int PROPERTY_toolTipColumnName = 3;
    private static final int PROPERTY_validator = 4;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[5];
    
        try {
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", com.openitech.db.components.JDbTextArea.class, "getColumnName", "setColumnName" ); // NOI18N
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbTextArea.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_dbFieldObserver] = new PropertyDescriptor ( "dbFieldObserver", com.openitech.db.components.JDbTextArea.class, "getDbFieldObserver", null ); // NOI18N
            properties[PROPERTY_dbFieldObserver].setHidden ( true );
            properties[PROPERTY_toolTipColumnName] = new PropertyDescriptor ( "toolTipColumnName", com.openitech.db.components.JDbTextArea.class, "getToolTipColumnName", "setToolTipColumnName" ); // NOI18N
            properties[PROPERTY_validator] = new PropertyDescriptor ( "validator", com.openitech.db.components.JDbTextArea.class, "getValidator", "setValidator" ); // NOI18N
        }
        catch(IntrospectionException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, e.getMessage(), e);
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_ancestorListener = 0;
    private static final int EVENT_caretListener = 1;
    private static final int EVENT_componentListener = 2;
    private static final int EVENT_containerListener = 3;
    private static final int EVENT_focusListener = 4;
    private static final int EVENT_hierarchyBoundsListener = 5;
    private static final int EVENT_hierarchyListener = 6;
    private static final int EVENT_inputMethodListener = 7;
    private static final int EVENT_keyListener = 8;
    private static final int EVENT_mouseListener = 9;
    private static final int EVENT_mouseMotionListener = 10;
    private static final int EVENT_mouseWheelListener = 11;
    private static final int EVENT_propertyChangeListener = 12;
    private static final int EVENT_vetoableChangeListener = 13;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[14];
    
        try {
            eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorAdded", "ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener" ); // NOI18N
            eventSets[EVENT_caretListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "caretListener", javax.swing.event.CaretListener.class, new String[] {"caretUpdate"}, "addCaretListener", "removeCaretListener" ); // NOI18N
            eventSets[EVENT_componentListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentHidden", "componentMoved", "componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" ); // NOI18N
            eventSets[EVENT_containerListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" ); // NOI18N
            eventSets[EVENT_focusListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener" ); // NOI18N
            eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" ); // NOI18N
            eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" ); // NOI18N
            eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"caretPositionChanged", "inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" ); // NOI18N
            eventSets[EVENT_keyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyPressed", "keyReleased", "keyTyped"}, "addKeyListener", "removeKeyListener" ); // NOI18N
            eventSets[EVENT_mouseListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseClicked", "mouseEntered", "mouseExited", "mousePressed", "mouseReleased"}, "addMouseListener", "removeMouseListener" ); // NOI18N
            eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" ); // NOI18N
            eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextArea.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.WARNING, e.getMessage(), e);
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_action0 = 0;
    private static final int METHOD_add1 = 1;
    private static final int METHOD_addKeymap2 = 2;
    private static final int METHOD_addNotify3 = 3;
    private static final int METHOD_addPropertyChangeListener4 = 4;
    private static final int METHOD_append5 = 5;
    private static final int METHOD_applyComponentOrientation6 = 6;
    private static final int METHOD_areFocusTraversalKeysSet7 = 7;
    private static final int METHOD_bounds8 = 8;
    private static final int METHOD_changedUpdate9 = 9;
    private static final int METHOD_checkImage10 = 10;
    private static final int METHOD_computeVisibleRect11 = 11;
    private static final int METHOD_contains12 = 12;
    private static final int METHOD_copy13 = 13;
    private static final int METHOD_countComponents14 = 14;
    private static final int METHOD_createImage15 = 15;
    private static final int METHOD_createToolTip16 = 16;
    private static final int METHOD_createVolatileImage17 = 17;
    private static final int METHOD_cut18 = 18;
    private static final int METHOD_dataSource_fieldValueChanged19 = 19;
    private static final int METHOD_dataSource_toolTipFieldValueChanged20 = 20;
    private static final int METHOD_deliverEvent21 = 21;
    private static final int METHOD_disable22 = 22;
    private static final int METHOD_dispatchEvent23 = 23;
    private static final int METHOD_doLayout24 = 24;
    private static final int METHOD_enable25 = 25;
    private static final int METHOD_enableInputMethods26 = 26;
    private static final int METHOD_findComponentAt27 = 27;
    private static final int METHOD_firePropertyChange28 = 28;
    private static final int METHOD_getActionForKeyStroke29 = 29;
    private static final int METHOD_getBounds30 = 30;
    private static final int METHOD_getClientProperty31 = 31;
    private static final int METHOD_getComponentAt32 = 32;
    private static final int METHOD_getComponentZOrder33 = 33;
    private static final int METHOD_getConditionForKeyStroke34 = 34;
    private static final int METHOD_getDefaultLocale35 = 35;
    private static final int METHOD_getFontMetrics36 = 36;
    private static final int METHOD_getInsets37 = 37;
    private static final int METHOD_getKeymap38 = 38;
    private static final int METHOD_getListeners39 = 39;
    private static final int METHOD_getLocation40 = 40;
    private static final int METHOD_getMousePosition41 = 41;
    private static final int METHOD_getPopupLocation42 = 42;
    private static final int METHOD_getPropertyChangeListeners43 = 43;
    private static final int METHOD_getScrollableBlockIncrement44 = 44;
    private static final int METHOD_getScrollableUnitIncrement45 = 45;
    private static final int METHOD_getSize46 = 46;
    private static final int METHOD_getText47 = 47;
    private static final int METHOD_getToolTipLocation48 = 48;
    private static final int METHOD_getToolTipText49 = 49;
    private static final int METHOD_gotFocus50 = 50;
    private static final int METHOD_grabFocus51 = 51;
    private static final int METHOD_handleEvent52 = 52;
    private static final int METHOD_hasFocus53 = 53;
    private static final int METHOD_hide54 = 54;
    private static final int METHOD_imageUpdate55 = 55;
    private static final int METHOD_insert56 = 56;
    private static final int METHOD_insertUpdate57 = 57;
    private static final int METHOD_insets58 = 58;
    private static final int METHOD_inside59 = 59;
    private static final int METHOD_invalidate60 = 60;
    private static final int METHOD_isAncestorOf61 = 61;
    private static final int METHOD_isFocusCycleRoot62 = 62;
    private static final int METHOD_isLightweightComponent63 = 63;
    private static final int METHOD_keyDown64 = 64;
    private static final int METHOD_keyUp65 = 65;
    private static final int METHOD_layout66 = 66;
    private static final int METHOD_list67 = 67;
    private static final int METHOD_loadKeymap68 = 68;
    private static final int METHOD_locate69 = 69;
    private static final int METHOD_location70 = 70;
    private static final int METHOD_lostFocus71 = 71;
    private static final int METHOD_minimumSize72 = 72;
    private static final int METHOD_modelToView73 = 73;
    private static final int METHOD_mouseDown74 = 74;
    private static final int METHOD_mouseDrag75 = 75;
    private static final int METHOD_mouseEnter76 = 76;
    private static final int METHOD_mouseExit77 = 77;
    private static final int METHOD_mouseMove78 = 78;
    private static final int METHOD_mouseUp79 = 79;
    private static final int METHOD_move80 = 80;
    private static final int METHOD_moveCaretPosition81 = 81;
    private static final int METHOD_nextFocus82 = 82;
    private static final int METHOD_paint83 = 83;
    private static final int METHOD_paintAll84 = 84;
    private static final int METHOD_paintComponents85 = 85;
    private static final int METHOD_paintImmediately86 = 86;
    private static final int METHOD_paste87 = 87;
    private static final int METHOD_postEvent88 = 88;
    private static final int METHOD_preferredSize89 = 89;
    private static final int METHOD_prepareImage90 = 90;
    private static final int METHOD_print91 = 91;
    private static final int METHOD_printAll92 = 92;
    private static final int METHOD_printComponents93 = 93;
    private static final int METHOD_putClientProperty94 = 94;
    private static final int METHOD_read95 = 95;
    private static final int METHOD_registerKeyboardAction96 = 96;
    private static final int METHOD_remove97 = 97;
    private static final int METHOD_removeAll98 = 98;
    private static final int METHOD_removeKeymap99 = 99;
    private static final int METHOD_removeNotify100 = 100;
    private static final int METHOD_removePropertyChangeListener101 = 101;
    private static final int METHOD_removeUpdate102 = 102;
    private static final int METHOD_repaint103 = 103;
    private static final int METHOD_replaceRange104 = 104;
    private static final int METHOD_replaceSelection105 = 105;
    private static final int METHOD_requestDefaultFocus106 = 106;
    private static final int METHOD_requestFocus107 = 107;
    private static final int METHOD_requestFocusInWindow108 = 108;
    private static final int METHOD_resetKeyboardActions109 = 109;
    private static final int METHOD_reshape110 = 110;
    private static final int METHOD_resize111 = 111;
    private static final int METHOD_revalidate112 = 112;
    private static final int METHOD_scrollRectToVisible113 = 113;
    private static final int METHOD_select114 = 114;
    private static final int METHOD_selectAll115 = 115;
    private static final int METHOD_setBounds116 = 116;
    private static final int METHOD_setComponentZOrder117 = 117;
    private static final int METHOD_setDefaultLocale118 = 118;
    private static final int METHOD_show119 = 119;
    private static final int METHOD_size120 = 120;
    private static final int METHOD_this_focusGained121 = 121;
    private static final int METHOD_toString122 = 122;
    private static final int METHOD_transferFocus123 = 123;
    private static final int METHOD_transferFocusBackward124 = 124;
    private static final int METHOD_transferFocusDownCycle125 = 125;
    private static final int METHOD_transferFocusUpCycle126 = 126;
    private static final int METHOD_unregisterKeyboardAction127 = 127;
    private static final int METHOD_update128 = 128;
    private static final int METHOD_updateUI129 = 129;
    private static final int METHOD_validate130 = 130;
    private static final int METHOD_viewToModel131 = 131;
    private static final int METHOD_write132 = 132;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[133];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_add1] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add1].setDisplayName ( "" );
            methods[METHOD_addKeymap2] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("addKeymap", new Class[] {java.lang.String.class, javax.swing.text.Keymap.class})); // NOI18N
            methods[METHOD_addKeymap2].setDisplayName ( "" );
            methods[METHOD_addNotify3] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify3].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener4] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener4].setDisplayName ( "" );
            methods[METHOD_append5] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("append", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_append5].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation6] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation6].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet7] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet7].setDisplayName ( "" );
            methods[METHOD_bounds8] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds8].setDisplayName ( "" );
            methods[METHOD_changedUpdate9] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("changedUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_changedUpdate9].setDisplayName ( "" );
            methods[METHOD_checkImage10] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage10].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect11] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect11].setDisplayName ( "" );
            methods[METHOD_contains12] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains12].setDisplayName ( "" );
            methods[METHOD_copy13] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("copy", new Class[] {})); // NOI18N
            methods[METHOD_copy13].setDisplayName ( "" );
            methods[METHOD_countComponents14] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents14].setDisplayName ( "" );
            methods[METHOD_createImage15] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage15].setDisplayName ( "" );
            methods[METHOD_createToolTip16] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip16].setDisplayName ( "" );
            methods[METHOD_createVolatileImage17] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage17].setDisplayName ( "" );
            methods[METHOD_cut18] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("cut", new Class[] {})); // NOI18N
            methods[METHOD_cut18].setDisplayName ( "" );
            methods[METHOD_dataSource_fieldValueChanged19] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("dataSource_fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_fieldValueChanged19].setDisplayName ( "" );
            methods[METHOD_dataSource_toolTipFieldValueChanged20] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("dataSource_toolTipFieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_toolTipFieldValueChanged20].setDisplayName ( "" );
            methods[METHOD_deliverEvent21] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent21].setDisplayName ( "" );
            methods[METHOD_disable22] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable22].setDisplayName ( "" );
            methods[METHOD_dispatchEvent23] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent23].setDisplayName ( "" );
            methods[METHOD_doLayout24] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout24].setDisplayName ( "" );
            methods[METHOD_enable25] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable25].setDisplayName ( "" );
            methods[METHOD_enableInputMethods26] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods26].setDisplayName ( "" );
            methods[METHOD_findComponentAt27] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt27].setDisplayName ( "" );
            methods[METHOD_firePropertyChange28] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange28].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke29] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke29].setDisplayName ( "" );
            methods[METHOD_getBounds30] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds30].setDisplayName ( "" );
            methods[METHOD_getClientProperty31] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty31].setDisplayName ( "" );
            methods[METHOD_getComponentAt32] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt32].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder33] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder33].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke34] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke34].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale35] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale35].setDisplayName ( "" );
            methods[METHOD_getFontMetrics36] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics36].setDisplayName ( "" );
            methods[METHOD_getInsets37] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets37].setDisplayName ( "" );
            methods[METHOD_getKeymap38] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getKeymap38].setDisplayName ( "" );
            methods[METHOD_getListeners39] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners39].setDisplayName ( "" );
            methods[METHOD_getLocation40] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation40].setDisplayName ( "" );
            methods[METHOD_getMousePosition41] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition41].setDisplayName ( "" );
            methods[METHOD_getPopupLocation42] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation42].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners43] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners43].setDisplayName ( "" );
            methods[METHOD_getScrollableBlockIncrement44] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getScrollableBlockIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableBlockIncrement44].setDisplayName ( "" );
            methods[METHOD_getScrollableUnitIncrement45] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getScrollableUnitIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableUnitIncrement45].setDisplayName ( "" );
            methods[METHOD_getSize46] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize46].setDisplayName ( "" );
            methods[METHOD_getText47] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getText", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getText47].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation48] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation48].setDisplayName ( "" );
            methods[METHOD_getToolTipText49] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText49].setDisplayName ( "" );
            methods[METHOD_gotFocus50] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus50].setDisplayName ( "" );
            methods[METHOD_grabFocus51] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus51].setDisplayName ( "" );
            methods[METHOD_handleEvent52] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent52].setDisplayName ( "" );
            methods[METHOD_hasFocus53] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus53].setDisplayName ( "" );
            methods[METHOD_hide54] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide54].setDisplayName ( "" );
            methods[METHOD_imageUpdate55] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate55].setDisplayName ( "" );
            methods[METHOD_insert56] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("insert", new Class[] {java.lang.String.class, Integer.TYPE})); // NOI18N
            methods[METHOD_insert56].setDisplayName ( "" );
            methods[METHOD_insertUpdate57] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("insertUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_insertUpdate57].setDisplayName ( "" );
            methods[METHOD_insets58] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets58].setDisplayName ( "" );
            methods[METHOD_inside59] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside59].setDisplayName ( "" );
            methods[METHOD_invalidate60] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate60].setDisplayName ( "" );
            methods[METHOD_isAncestorOf61] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf61].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot62] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot62].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent63] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent63].setDisplayName ( "" );
            methods[METHOD_keyDown64] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown64].setDisplayName ( "" );
            methods[METHOD_keyUp65] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp65].setDisplayName ( "" );
            methods[METHOD_layout66] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout66].setDisplayName ( "" );
            methods[METHOD_list67] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list67].setDisplayName ( "" );
            methods[METHOD_loadKeymap68] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("loadKeymap", new Class[] {javax.swing.text.Keymap.class, javax.swing.text.JTextComponent.KeyBinding[].class, javax.swing.Action[].class})); // NOI18N
            methods[METHOD_loadKeymap68].setDisplayName ( "" );
            methods[METHOD_locate69] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate69].setDisplayName ( "" );
            methods[METHOD_location70] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location70].setDisplayName ( "" );
            methods[METHOD_lostFocus71] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus71].setDisplayName ( "" );
            methods[METHOD_minimumSize72] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize72].setDisplayName ( "" );
            methods[METHOD_modelToView73] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("modelToView", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_modelToView73].setDisplayName ( "" );
            methods[METHOD_mouseDown74] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown74].setDisplayName ( "" );
            methods[METHOD_mouseDrag75] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag75].setDisplayName ( "" );
            methods[METHOD_mouseEnter76] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter76].setDisplayName ( "" );
            methods[METHOD_mouseExit77] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit77].setDisplayName ( "" );
            methods[METHOD_mouseMove78] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove78].setDisplayName ( "" );
            methods[METHOD_mouseUp79] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp79].setDisplayName ( "" );
            methods[METHOD_move80] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move80].setDisplayName ( "" );
            methods[METHOD_moveCaretPosition81] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("moveCaretPosition", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_moveCaretPosition81].setDisplayName ( "" );
            methods[METHOD_nextFocus82] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus82].setDisplayName ( "" );
            methods[METHOD_paint83] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint83].setDisplayName ( "" );
            methods[METHOD_paintAll84] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll84].setDisplayName ( "" );
            methods[METHOD_paintComponents85] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents85].setDisplayName ( "" );
            methods[METHOD_paintImmediately86] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately86].setDisplayName ( "" );
            methods[METHOD_paste87] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("paste", new Class[] {})); // NOI18N
            methods[METHOD_paste87].setDisplayName ( "" );
            methods[METHOD_postEvent88] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent88].setDisplayName ( "" );
            methods[METHOD_preferredSize89] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize89].setDisplayName ( "" );
            methods[METHOD_prepareImage90] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage90].setDisplayName ( "" );
            methods[METHOD_print91] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print91].setDisplayName ( "" );
            methods[METHOD_printAll92] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll92].setDisplayName ( "" );
            methods[METHOD_printComponents93] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents93].setDisplayName ( "" );
            methods[METHOD_putClientProperty94] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty94].setDisplayName ( "" );
            methods[METHOD_read95] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("read", new Class[] {java.io.Reader.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_read95].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction96] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction96].setDisplayName ( "" );
            methods[METHOD_remove97] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove97].setDisplayName ( "" );
            methods[METHOD_removeAll98] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll98].setDisplayName ( "" );
            methods[METHOD_removeKeymap99] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("removeKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeKeymap99].setDisplayName ( "" );
            methods[METHOD_removeNotify100] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify100].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener101] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener101].setDisplayName ( "" );
            methods[METHOD_removeUpdate102] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("removeUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_removeUpdate102].setDisplayName ( "" );
            methods[METHOD_repaint103] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint103].setDisplayName ( "" );
            methods[METHOD_replaceRange104] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("replaceRange", new Class[] {java.lang.String.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_replaceRange104].setDisplayName ( "" );
            methods[METHOD_replaceSelection105] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("replaceSelection", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_replaceSelection105].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus106] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus106].setDisplayName ( "" );
            methods[METHOD_requestFocus107] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus107].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow108] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow108].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions109] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions109].setDisplayName ( "" );
            methods[METHOD_reshape110] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape110].setDisplayName ( "" );
            methods[METHOD_resize111] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize111].setDisplayName ( "" );
            methods[METHOD_revalidate112] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate112].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible113] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible113].setDisplayName ( "" );
            methods[METHOD_select114] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("select", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_select114].setDisplayName ( "" );
            methods[METHOD_selectAll115] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("selectAll", new Class[] {})); // NOI18N
            methods[METHOD_selectAll115].setDisplayName ( "" );
            methods[METHOD_setBounds116] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds116].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder117] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder117].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale118] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale118].setDisplayName ( "" );
            methods[METHOD_show119] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show119].setDisplayName ( "" );
            methods[METHOD_size120] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size120].setDisplayName ( "" );
            methods[METHOD_this_focusGained121] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("this_focusGained", new Class[] {java.awt.event.FocusEvent.class})); // NOI18N
            methods[METHOD_this_focusGained121].setDisplayName ( "" );
            methods[METHOD_toString122] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString122].setDisplayName ( "" );
            methods[METHOD_transferFocus123] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus123].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward124] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward124].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle125] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle125].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle126] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle126].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction127] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction127].setDisplayName ( "" );
            methods[METHOD_update128] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update128].setDisplayName ( "" );
            methods[METHOD_updateUI129] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI129].setDisplayName ( "" );
            methods[METHOD_validate130] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate130].setDisplayName ( "" );
            methods[METHOD_viewToModel131] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("viewToModel", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_viewToModel131].setDisplayName ( "" );
            methods[METHOD_write132] = new MethodDescriptor ( com.openitech.db.components.JDbTextArea.class.getMethod("write", new Class[] {java.io.Writer.class})); // NOI18N
            methods[METHOD_write132].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbTextArea.class.getSuperclass();
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

