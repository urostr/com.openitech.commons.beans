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
 * JDbComboBoxBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 14:56
 */

package com.openitech.db.components;

import java.beans.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author uros
 */
public class JDbComboBoxBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbComboBox.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

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
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", com.openitech.db.components.JDbComboBox.class, "getColumnName", "setColumnName" ); // NOI18N
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbComboBox.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_dbFieldObserver] = new PropertyDescriptor ( "dbFieldObserver", com.openitech.db.components.JDbComboBox.class, "getDbFieldObserver", null ); // NOI18N
            properties[PROPERTY_dbFieldObserver].setHidden ( true );
            properties[PROPERTY_toolTipColumnName] = new PropertyDescriptor ( "toolTipColumnName", com.openitech.db.components.JDbComboBox.class, "getToolTipColumnName", "setToolTipColumnName" ); // NOI18N
            properties[PROPERTY_validator] = new PropertyDescriptor ( "validator", com.openitech.db.components.JDbComboBox.class, "getValidator", "setValidator" ); // NOI18N
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
    private static final int METHOD_action0 = 0;
    private static final int METHOD_actionPerformed1 = 1;
    private static final int METHOD_add2 = 2;
    private static final int METHOD_addItem3 = 3;
    private static final int METHOD_addNotify4 = 4;
    private static final int METHOD_addPropertyChangeListener5 = 5;
    private static final int METHOD_applyComponentOrientation6 = 6;
    private static final int METHOD_areFocusTraversalKeysSet7 = 7;
    private static final int METHOD_bounds8 = 8;
    private static final int METHOD_checkImage9 = 9;
    private static final int METHOD_computeVisibleRect10 = 10;
    private static final int METHOD_configureEditor11 = 11;
    private static final int METHOD_contains12 = 12;
    private static final int METHOD_contentsChanged13 = 13;
    private static final int METHOD_countComponents14 = 14;
    private static final int METHOD_createImage15 = 15;
    private static final int METHOD_createToolTip16 = 16;
    private static final int METHOD_createVolatileImage17 = 17;
    private static final int METHOD_dataSource_fieldValueChanged18 = 18;
    private static final int METHOD_dataSource_toolTipFieldValueChanged19 = 19;
    private static final int METHOD_deliverEvent20 = 20;
    private static final int METHOD_disable21 = 21;
    private static final int METHOD_dispatchEvent22 = 22;
    private static final int METHOD_doLayout23 = 23;
    private static final int METHOD_enable24 = 24;
    private static final int METHOD_enableInputMethods25 = 25;
    private static final int METHOD_findComponentAt26 = 26;
    private static final int METHOD_firePopupMenuCanceled27 = 27;
    private static final int METHOD_firePopupMenuWillBecomeInvisible28 = 28;
    private static final int METHOD_firePopupMenuWillBecomeVisible29 = 29;
    private static final int METHOD_firePropertyChange30 = 30;
    private static final int METHOD_getActionForKeyStroke31 = 31;
    private static final int METHOD_getBounds32 = 32;
    private static final int METHOD_getClientProperty33 = 33;
    private static final int METHOD_getComponentAt34 = 34;
    private static final int METHOD_getComponentZOrder35 = 35;
    private static final int METHOD_getConditionForKeyStroke36 = 36;
    private static final int METHOD_getDefaultLocale37 = 37;
    private static final int METHOD_getFontMetrics38 = 38;
    private static final int METHOD_getInsets39 = 39;
    private static final int METHOD_getListeners40 = 40;
    private static final int METHOD_getLocation41 = 41;
    private static final int METHOD_getMousePosition42 = 42;
    private static final int METHOD_getPopupLocation43 = 43;
    private static final int METHOD_getPropertyChangeListeners44 = 44;
    private static final int METHOD_getSize45 = 45;
    private static final int METHOD_getToolTipLocation46 = 46;
    private static final int METHOD_getToolTipText47 = 47;
    private static final int METHOD_gotFocus48 = 48;
    private static final int METHOD_grabFocus49 = 49;
    private static final int METHOD_handleEvent50 = 50;
    private static final int METHOD_hasFocus51 = 51;
    private static final int METHOD_hide52 = 52;
    private static final int METHOD_hidePopup53 = 53;
    private static final int METHOD_imageUpdate54 = 54;
    private static final int METHOD_insertItemAt55 = 55;
    private static final int METHOD_insets56 = 56;
    private static final int METHOD_inside57 = 57;
    private static final int METHOD_intervalAdded58 = 58;
    private static final int METHOD_intervalRemoved59 = 59;
    private static final int METHOD_invalidate60 = 60;
    private static final int METHOD_isAncestorOf61 = 61;
    private static final int METHOD_isFocusCycleRoot62 = 62;
    private static final int METHOD_isLightweightComponent63 = 63;
    private static final int METHOD_keyDown64 = 64;
    private static final int METHOD_keyUp65 = 65;
    private static final int METHOD_layout66 = 66;
    private static final int METHOD_list67 = 67;
    private static final int METHOD_locate68 = 68;
    private static final int METHOD_location69 = 69;
    private static final int METHOD_lostFocus70 = 70;
    private static final int METHOD_minimumSize71 = 71;
    private static final int METHOD_mouseDown72 = 72;
    private static final int METHOD_mouseDrag73 = 73;
    private static final int METHOD_mouseEnter74 = 74;
    private static final int METHOD_mouseExit75 = 75;
    private static final int METHOD_mouseMove76 = 76;
    private static final int METHOD_mouseUp77 = 77;
    private static final int METHOD_move78 = 78;
    private static final int METHOD_nextFocus79 = 79;
    private static final int METHOD_paint80 = 80;
    private static final int METHOD_paintAll81 = 81;
    private static final int METHOD_paintComponents82 = 82;
    private static final int METHOD_paintImmediately83 = 83;
    private static final int METHOD_postEvent84 = 84;
    private static final int METHOD_preferredSize85 = 85;
    private static final int METHOD_prepareImage86 = 86;
    private static final int METHOD_print87 = 87;
    private static final int METHOD_printAll88 = 88;
    private static final int METHOD_printComponents89 = 89;
    private static final int METHOD_processKeyEvent90 = 90;
    private static final int METHOD_putClientProperty91 = 91;
    private static final int METHOD_registerKeyboardAction92 = 92;
    private static final int METHOD_remove93 = 93;
    private static final int METHOD_removeAll94 = 94;
    private static final int METHOD_removeAllItems95 = 95;
    private static final int METHOD_removeItem96 = 96;
    private static final int METHOD_removeItemAt97 = 97;
    private static final int METHOD_removeNotify98 = 98;
    private static final int METHOD_removePropertyChangeListener99 = 99;
    private static final int METHOD_repaint100 = 100;
    private static final int METHOD_requestDefaultFocus101 = 101;
    private static final int METHOD_requestFocus102 = 102;
    private static final int METHOD_requestFocusInWindow103 = 103;
    private static final int METHOD_resetKeyboardActions104 = 104;
    private static final int METHOD_reshape105 = 105;
    private static final int METHOD_resize106 = 106;
    private static final int METHOD_revalidate107 = 107;
    private static final int METHOD_scrollRectToVisible108 = 108;
    private static final int METHOD_selectWithKeyChar109 = 109;
    private static final int METHOD_setBounds110 = 110;
    private static final int METHOD_setComponentZOrder111 = 111;
    private static final int METHOD_setDefaultLocale112 = 112;
    private static final int METHOD_show113 = 113;
    private static final int METHOD_showPopup114 = 114;
    private static final int METHOD_size115 = 115;
    private static final int METHOD_toString116 = 116;
    private static final int METHOD_transferFocus117 = 117;
    private static final int METHOD_transferFocusBackward118 = 118;
    private static final int METHOD_transferFocusDownCycle119 = 119;
    private static final int METHOD_transferFocusUpCycle120 = 120;
    private static final int METHOD_unregisterKeyboardAction121 = 121;
    private static final int METHOD_update122 = 122;
    private static final int METHOD_updateUI123 = 123;
    private static final int METHOD_validate124 = 124;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[125];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_actionPerformed1] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("actionPerformed", new Class[] {java.awt.event.ActionEvent.class})); // NOI18N
            methods[METHOD_actionPerformed1].setDisplayName ( "" );
            methods[METHOD_add2] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add2].setDisplayName ( "" );
            methods[METHOD_addItem3] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("addItem", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_addItem3].setDisplayName ( "" );
            methods[METHOD_addNotify4] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify4].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener5] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener5].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation6] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation6].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet7] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet7].setDisplayName ( "" );
            methods[METHOD_bounds8] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds8].setDisplayName ( "" );
            methods[METHOD_checkImage9] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage9].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect10] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect10].setDisplayName ( "" );
            methods[METHOD_configureEditor11] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("configureEditor", new Class[] {javax.swing.ComboBoxEditor.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_configureEditor11].setDisplayName ( "" );
            methods[METHOD_contains12] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains12].setDisplayName ( "" );
            methods[METHOD_contentsChanged13] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("contentsChanged", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_contentsChanged13].setDisplayName ( "" );
            methods[METHOD_countComponents14] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents14].setDisplayName ( "" );
            methods[METHOD_createImage15] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage15].setDisplayName ( "" );
            methods[METHOD_createToolTip16] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip16].setDisplayName ( "" );
            methods[METHOD_createVolatileImage17] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage17].setDisplayName ( "" );
            methods[METHOD_dataSource_fieldValueChanged18] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("dataSource_fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_fieldValueChanged18].setDisplayName ( "" );
            methods[METHOD_dataSource_toolTipFieldValueChanged19] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("dataSource_toolTipFieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_toolTipFieldValueChanged19].setDisplayName ( "" );
            methods[METHOD_deliverEvent20] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent20].setDisplayName ( "" );
            methods[METHOD_disable21] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable21].setDisplayName ( "" );
            methods[METHOD_dispatchEvent22] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent22].setDisplayName ( "" );
            methods[METHOD_doLayout23] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout23].setDisplayName ( "" );
            methods[METHOD_enable24] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable24].setDisplayName ( "" );
            methods[METHOD_enableInputMethods25] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods25].setDisplayName ( "" );
            methods[METHOD_findComponentAt26] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt26].setDisplayName ( "" );
            methods[METHOD_firePopupMenuCanceled27] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("firePopupMenuCanceled", new Class[] {})); // NOI18N
            methods[METHOD_firePopupMenuCanceled27].setDisplayName ( "" );
            methods[METHOD_firePopupMenuWillBecomeInvisible28] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("firePopupMenuWillBecomeInvisible", new Class[] {})); // NOI18N
            methods[METHOD_firePopupMenuWillBecomeInvisible28].setDisplayName ( "" );
            methods[METHOD_firePopupMenuWillBecomeVisible29] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("firePopupMenuWillBecomeVisible", new Class[] {})); // NOI18N
            methods[METHOD_firePopupMenuWillBecomeVisible29].setDisplayName ( "" );
            methods[METHOD_firePropertyChange30] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange30].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke31] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke31].setDisplayName ( "" );
            methods[METHOD_getBounds32] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds32].setDisplayName ( "" );
            methods[METHOD_getClientProperty33] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty33].setDisplayName ( "" );
            methods[METHOD_getComponentAt34] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt34].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder35] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder35].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke36] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke36].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale37] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale37].setDisplayName ( "" );
            methods[METHOD_getFontMetrics38] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics38].setDisplayName ( "" );
            methods[METHOD_getInsets39] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets39].setDisplayName ( "" );
            methods[METHOD_getListeners40] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners40].setDisplayName ( "" );
            methods[METHOD_getLocation41] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation41].setDisplayName ( "" );
            methods[METHOD_getMousePosition42] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition42].setDisplayName ( "" );
            methods[METHOD_getPopupLocation43] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation43].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners44] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners44].setDisplayName ( "" );
            methods[METHOD_getSize45] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize45].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation46] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation46].setDisplayName ( "" );
            methods[METHOD_getToolTipText47] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText47].setDisplayName ( "" );
            methods[METHOD_gotFocus48] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus48].setDisplayName ( "" );
            methods[METHOD_grabFocus49] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus49].setDisplayName ( "" );
            methods[METHOD_handleEvent50] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent50].setDisplayName ( "" );
            methods[METHOD_hasFocus51] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus51].setDisplayName ( "" );
            methods[METHOD_hide52] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide52].setDisplayName ( "" );
            methods[METHOD_hidePopup53] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("hidePopup", new Class[] {})); // NOI18N
            methods[METHOD_hidePopup53].setDisplayName ( "" );
            methods[METHOD_imageUpdate54] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate54].setDisplayName ( "" );
            methods[METHOD_insertItemAt55] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("insertItemAt", new Class[] {java.lang.Object.class, Integer.TYPE})); // NOI18N
            methods[METHOD_insertItemAt55].setDisplayName ( "" );
            methods[METHOD_insets56] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets56].setDisplayName ( "" );
            methods[METHOD_inside57] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside57].setDisplayName ( "" );
            methods[METHOD_intervalAdded58] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("intervalAdded", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalAdded58].setDisplayName ( "" );
            methods[METHOD_intervalRemoved59] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("intervalRemoved", new Class[] {javax.swing.event.ListDataEvent.class})); // NOI18N
            methods[METHOD_intervalRemoved59].setDisplayName ( "" );
            methods[METHOD_invalidate60] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate60].setDisplayName ( "" );
            methods[METHOD_isAncestorOf61] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf61].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot62] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot62].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent63] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent63].setDisplayName ( "" );
            methods[METHOD_keyDown64] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown64].setDisplayName ( "" );
            methods[METHOD_keyUp65] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp65].setDisplayName ( "" );
            methods[METHOD_layout66] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout66].setDisplayName ( "" );
            methods[METHOD_list67] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list67].setDisplayName ( "" );
            methods[METHOD_locate68] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate68].setDisplayName ( "" );
            methods[METHOD_location69] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location69].setDisplayName ( "" );
            methods[METHOD_lostFocus70] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus70].setDisplayName ( "" );
            methods[METHOD_minimumSize71] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize71].setDisplayName ( "" );
            methods[METHOD_mouseDown72] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown72].setDisplayName ( "" );
            methods[METHOD_mouseDrag73] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag73].setDisplayName ( "" );
            methods[METHOD_mouseEnter74] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter74].setDisplayName ( "" );
            methods[METHOD_mouseExit75] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit75].setDisplayName ( "" );
            methods[METHOD_mouseMove76] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove76].setDisplayName ( "" );
            methods[METHOD_mouseUp77] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp77].setDisplayName ( "" );
            methods[METHOD_move78] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move78].setDisplayName ( "" );
            methods[METHOD_nextFocus79] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus79].setDisplayName ( "" );
            methods[METHOD_paint80] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint80].setDisplayName ( "" );
            methods[METHOD_paintAll81] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll81].setDisplayName ( "" );
            methods[METHOD_paintComponents82] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents82].setDisplayName ( "" );
            methods[METHOD_paintImmediately83] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately83].setDisplayName ( "" );
            methods[METHOD_postEvent84] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent84].setDisplayName ( "" );
            methods[METHOD_preferredSize85] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize85].setDisplayName ( "" );
            methods[METHOD_prepareImage86] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage86].setDisplayName ( "" );
            methods[METHOD_print87] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print87].setDisplayName ( "" );
            methods[METHOD_printAll88] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll88].setDisplayName ( "" );
            methods[METHOD_printComponents89] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents89].setDisplayName ( "" );
            methods[METHOD_processKeyEvent90] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("processKeyEvent", new Class[] {java.awt.event.KeyEvent.class})); // NOI18N
            methods[METHOD_processKeyEvent90].setDisplayName ( "" );
            methods[METHOD_putClientProperty91] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty91].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction92] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction92].setDisplayName ( "" );
            methods[METHOD_remove93] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove93].setDisplayName ( "" );
            methods[METHOD_removeAll94] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll94].setDisplayName ( "" );
            methods[METHOD_removeAllItems95] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removeAllItems", new Class[] {})); // NOI18N
            methods[METHOD_removeAllItems95].setDisplayName ( "" );
            methods[METHOD_removeItem96] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removeItem", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_removeItem96].setDisplayName ( "" );
            methods[METHOD_removeItemAt97] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removeItemAt", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_removeItemAt97].setDisplayName ( "" );
            methods[METHOD_removeNotify98] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify98].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener99] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener99].setDisplayName ( "" );
            methods[METHOD_repaint100] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint100].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus101] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus101].setDisplayName ( "" );
            methods[METHOD_requestFocus102] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus102].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow103] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow103].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions104] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions104].setDisplayName ( "" );
            methods[METHOD_reshape105] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape105].setDisplayName ( "" );
            methods[METHOD_resize106] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize106].setDisplayName ( "" );
            methods[METHOD_revalidate107] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate107].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible108] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible108].setDisplayName ( "" );
            methods[METHOD_selectWithKeyChar109] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("selectWithKeyChar", new Class[] {Character.TYPE})); // NOI18N
            methods[METHOD_selectWithKeyChar109].setDisplayName ( "" );
            methods[METHOD_setBounds110] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds110].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder111] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder111].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale112] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale112].setDisplayName ( "" );
            methods[METHOD_show113] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show113].setDisplayName ( "" );
            methods[METHOD_showPopup114] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("showPopup", new Class[] {})); // NOI18N
            methods[METHOD_showPopup114].setDisplayName ( "" );
            methods[METHOD_size115] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size115].setDisplayName ( "" );
            methods[METHOD_toString116] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString116].setDisplayName ( "" );
            methods[METHOD_transferFocus117] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus117].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward118] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward118].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle119] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle119].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle120] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle120].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction121] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction121].setDisplayName ( "" );
            methods[METHOD_update122] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update122].setDisplayName ( "" );
            methods[METHOD_updateUI123] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI123].setDisplayName ( "" );
            methods[METHOD_validate124] = new MethodDescriptor ( com.openitech.db.components.JDbComboBox.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate124].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbComboBox.class.getSuperclass();
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

