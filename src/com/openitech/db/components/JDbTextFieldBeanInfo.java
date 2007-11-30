/*
 * JDbTextFieldBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 15:39
 */

package com.openitech.db.components;

import java.beans.*;

/**
 * @author uros
 */
public class JDbTextFieldBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbTextField.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_background = 0;
    private static final int PROPERTY_border = 1;
    private static final int PROPERTY_columnName = 2;
    private static final int PROPERTY_componentPopupMenu = 3;
    private static final int PROPERTY_dataSource = 4;
    private static final int PROPERTY_dbFieldObserver = 5;
    private static final int PROPERTY_document = 6;
    private static final int PROPERTY_foreground = 7;
    private static final int PROPERTY_toolTipColumnName = 8;
    private static final int PROPERTY_validator = 9;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[10];
    
        try {
            properties[PROPERTY_background] = new PropertyDescriptor ( "background", com.openitech.db.components.JDbTextField.class, "getBackground", "setBackground" ); // NOI18N
            properties[PROPERTY_border] = new PropertyDescriptor ( "border", com.openitech.db.components.JDbTextField.class, "getBorder", "setBorder" ); // NOI18N
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", com.openitech.db.components.JDbTextField.class, "getColumnName", "setColumnName" ); // NOI18N
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_componentPopupMenu] = new PropertyDescriptor ( "componentPopupMenu", com.openitech.db.components.JDbTextField.class, "getComponentPopupMenu", "setComponentPopupMenu" ); // NOI18N
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbTextField.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_dbFieldObserver] = new PropertyDescriptor ( "dbFieldObserver", com.openitech.db.components.JDbTextField.class, "getDbFieldObserver", null ); // NOI18N
            properties[PROPERTY_dbFieldObserver].setHidden ( true );
            properties[PROPERTY_document] = new PropertyDescriptor ( "document", com.openitech.db.components.JDbTextField.class, "getDocument", "setDocument" ); // NOI18N
            properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", com.openitech.db.components.JDbTextField.class, "getForeground", "setForeground" ); // NOI18N
            properties[PROPERTY_toolTipColumnName] = new PropertyDescriptor ( "toolTipColumnName", com.openitech.db.components.JDbTextField.class, "getToolTipColumnName", "setToolTipColumnName" ); // NOI18N
            properties[PROPERTY_validator] = new PropertyDescriptor ( "validator", com.openitech.db.components.JDbTextField.class, "getValidator", "setValidator" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_actionListener = 0;
    private static final int EVENT_ancestorListener = 1;
    private static final int EVENT_caretListener = 2;
    private static final int EVENT_componentListener = 3;
    private static final int EVENT_containerListener = 4;
    private static final int EVENT_focusListener = 5;
    private static final int EVENT_hierarchyBoundsListener = 6;
    private static final int EVENT_hierarchyListener = 7;
    private static final int EVENT_inputMethodListener = 8;
    private static final int EVENT_keyListener = 9;
    private static final int EVENT_mouseListener = 10;
    private static final int EVENT_mouseMotionListener = 11;
    private static final int EVENT_mouseWheelListener = 12;
    private static final int EVENT_propertyChangeListener = 13;
    private static final int EVENT_vetoableChangeListener = 14;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[15];
    
        try {
            eventSets[EVENT_actionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "actionListener", java.awt.event.ActionListener.class, new String[] {"actionPerformed"}, "addActionListener", "removeActionListener" ); // NOI18N
            eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorAdded", "ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener" ); // NOI18N
            eventSets[EVENT_caretListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "caretListener", javax.swing.event.CaretListener.class, new String[] {"caretUpdate"}, "addCaretListener", "removeCaretListener" ); // NOI18N
            eventSets[EVENT_componentListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentHidden", "componentMoved", "componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" ); // NOI18N
            eventSets[EVENT_containerListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" ); // NOI18N
            eventSets[EVENT_focusListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener" ); // NOI18N
            eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" ); // NOI18N
            eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" ); // NOI18N
            eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"caretPositionChanged", "inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" ); // NOI18N
            eventSets[EVENT_keyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyPressed", "keyReleased", "keyTyped"}, "addKeyListener", "removeKeyListener" ); // NOI18N
            eventSets[EVENT_mouseListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseClicked", "mouseEntered", "mouseExited", "mousePressed", "mouseReleased"}, "addMouseListener", "removeMouseListener" ); // NOI18N
            eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" ); // NOI18N
            eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbTextField.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_action0 = 0;
    private static final int METHOD_add1 = 1;
    private static final int METHOD_addKeymap2 = 2;
    private static final int METHOD_addNotify3 = 3;
    private static final int METHOD_addPropertyChangeListener4 = 4;
    private static final int METHOD_applyComponentOrientation5 = 5;
    private static final int METHOD_areFocusTraversalKeysSet6 = 6;
    private static final int METHOD_bounds7 = 7;
    private static final int METHOD_changedUpdate8 = 8;
    private static final int METHOD_checkImage9 = 9;
    private static final int METHOD_computeVisibleRect10 = 10;
    private static final int METHOD_contains11 = 11;
    private static final int METHOD_copy12 = 12;
    private static final int METHOD_countComponents13 = 13;
    private static final int METHOD_createImage14 = 14;
    private static final int METHOD_createToolTip15 = 15;
    private static final int METHOD_createVolatileImage16 = 16;
    private static final int METHOD_cut17 = 17;
    private static final int METHOD_dataSource_fieldValueChanged18 = 18;
    private static final int METHOD_dataSource_toolTipFieldValueChanged19 = 19;
    private static final int METHOD_deliverEvent20 = 20;
    private static final int METHOD_disable21 = 21;
    private static final int METHOD_dispatchEvent22 = 22;
    private static final int METHOD_doLayout23 = 23;
    private static final int METHOD_enable24 = 24;
    private static final int METHOD_enableInputMethods25 = 25;
    private static final int METHOD_findComponentAt26 = 26;
    private static final int METHOD_firePropertyChange27 = 27;
    private static final int METHOD_getActionForKeyStroke28 = 28;
    private static final int METHOD_getBounds29 = 29;
    private static final int METHOD_getClientProperty30 = 30;
    private static final int METHOD_getComponentAt31 = 31;
    private static final int METHOD_getComponentZOrder32 = 32;
    private static final int METHOD_getConditionForKeyStroke33 = 33;
    private static final int METHOD_getDefaultLocale34 = 34;
    private static final int METHOD_getFontMetrics35 = 35;
    private static final int METHOD_getInsets36 = 36;
    private static final int METHOD_getKeymap37 = 37;
    private static final int METHOD_getListeners38 = 38;
    private static final int METHOD_getLocation39 = 39;
    private static final int METHOD_getMousePosition40 = 40;
    private static final int METHOD_getPopupLocation41 = 41;
    private static final int METHOD_getPropertyChangeListeners42 = 42;
    private static final int METHOD_getScrollableBlockIncrement43 = 43;
    private static final int METHOD_getScrollableUnitIncrement44 = 44;
    private static final int METHOD_getSize45 = 45;
    private static final int METHOD_getText46 = 46;
    private static final int METHOD_getToolTipLocation47 = 47;
    private static final int METHOD_getToolTipText48 = 48;
    private static final int METHOD_gotFocus49 = 49;
    private static final int METHOD_grabFocus50 = 50;
    private static final int METHOD_handleEvent51 = 51;
    private static final int METHOD_hasFocus52 = 52;
    private static final int METHOD_hide53 = 53;
    private static final int METHOD_imageUpdate54 = 54;
    private static final int METHOD_insertUpdate55 = 55;
    private static final int METHOD_insets56 = 56;
    private static final int METHOD_inside57 = 57;
    private static final int METHOD_invalidate58 = 58;
    private static final int METHOD_isAncestorOf59 = 59;
    private static final int METHOD_isFocusCycleRoot60 = 60;
    private static final int METHOD_isLightweightComponent61 = 61;
    private static final int METHOD_keyDown62 = 62;
    private static final int METHOD_keyUp63 = 63;
    private static final int METHOD_layout64 = 64;
    private static final int METHOD_list65 = 65;
    private static final int METHOD_loadKeymap66 = 66;
    private static final int METHOD_locate67 = 67;
    private static final int METHOD_location68 = 68;
    private static final int METHOD_lostFocus69 = 69;
    private static final int METHOD_minimumSize70 = 70;
    private static final int METHOD_modelToView71 = 71;
    private static final int METHOD_mouseDown72 = 72;
    private static final int METHOD_mouseDrag73 = 73;
    private static final int METHOD_mouseEnter74 = 74;
    private static final int METHOD_mouseExit75 = 75;
    private static final int METHOD_mouseMove76 = 76;
    private static final int METHOD_mouseUp77 = 77;
    private static final int METHOD_move78 = 78;
    private static final int METHOD_moveCaretPosition79 = 79;
    private static final int METHOD_nextFocus80 = 80;
    private static final int METHOD_paint81 = 81;
    private static final int METHOD_paintAll82 = 82;
    private static final int METHOD_paintComponents83 = 83;
    private static final int METHOD_paintImmediately84 = 84;
    private static final int METHOD_paste85 = 85;
    private static final int METHOD_postActionEvent86 = 86;
    private static final int METHOD_postEvent87 = 87;
    private static final int METHOD_preferredSize88 = 88;
    private static final int METHOD_prepareImage89 = 89;
    private static final int METHOD_print90 = 90;
    private static final int METHOD_printAll91 = 91;
    private static final int METHOD_printComponents92 = 92;
    private static final int METHOD_putClientProperty93 = 93;
    private static final int METHOD_read94 = 94;
    private static final int METHOD_registerKeyboardAction95 = 95;
    private static final int METHOD_remove96 = 96;
    private static final int METHOD_removeAll97 = 97;
    private static final int METHOD_removeKeymap98 = 98;
    private static final int METHOD_removeNotify99 = 99;
    private static final int METHOD_removePropertyChangeListener100 = 100;
    private static final int METHOD_removeUpdate101 = 101;
    private static final int METHOD_repaint102 = 102;
    private static final int METHOD_replaceSelection103 = 103;
    private static final int METHOD_requestDefaultFocus104 = 104;
    private static final int METHOD_requestFocus105 = 105;
    private static final int METHOD_requestFocusInWindow106 = 106;
    private static final int METHOD_resetKeyboardActions107 = 107;
    private static final int METHOD_reshape108 = 108;
    private static final int METHOD_resize109 = 109;
    private static final int METHOD_revalidate110 = 110;
    private static final int METHOD_scrollRectToVisible111 = 111;
    private static final int METHOD_select112 = 112;
    private static final int METHOD_selectAll113 = 113;
    private static final int METHOD_setBounds114 = 114;
    private static final int METHOD_setComponentZOrder115 = 115;
    private static final int METHOD_setDefaultLocale116 = 116;
    private static final int METHOD_show117 = 117;
    private static final int METHOD_size118 = 118;
    private static final int METHOD_this_focusGained119 = 119;
    private static final int METHOD_toString120 = 120;
    private static final int METHOD_transferFocus121 = 121;
    private static final int METHOD_transferFocusBackward122 = 122;
    private static final int METHOD_transferFocusDownCycle123 = 123;
    private static final int METHOD_transferFocusUpCycle124 = 124;
    private static final int METHOD_unregisterKeyboardAction125 = 125;
    private static final int METHOD_update126 = 126;
    private static final int METHOD_updateUI127 = 127;
    private static final int METHOD_validate128 = 128;
    private static final int METHOD_viewToModel129 = 129;
    private static final int METHOD_write130 = 130;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[131];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_add1] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add1].setDisplayName ( "" );
            methods[METHOD_addKeymap2] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("addKeymap", new Class[] {java.lang.String.class, javax.swing.text.Keymap.class})); // NOI18N
            methods[METHOD_addKeymap2].setDisplayName ( "" );
            methods[METHOD_addNotify3] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify3].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener4] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener4].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation5] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation5].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet6] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet6].setDisplayName ( "" );
            methods[METHOD_bounds7] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds7].setDisplayName ( "" );
            methods[METHOD_changedUpdate8] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("changedUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_changedUpdate8].setDisplayName ( "" );
            methods[METHOD_checkImage9] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage9].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect10] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect10].setDisplayName ( "" );
            methods[METHOD_contains11] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains11].setDisplayName ( "" );
            methods[METHOD_copy12] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("copy", new Class[] {})); // NOI18N
            methods[METHOD_copy12].setDisplayName ( "" );
            methods[METHOD_countComponents13] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents13].setDisplayName ( "" );
            methods[METHOD_createImage14] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage14].setDisplayName ( "" );
            methods[METHOD_createToolTip15] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip15].setDisplayName ( "" );
            methods[METHOD_createVolatileImage16] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage16].setDisplayName ( "" );
            methods[METHOD_cut17] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("cut", new Class[] {})); // NOI18N
            methods[METHOD_cut17].setDisplayName ( "" );
            methods[METHOD_dataSource_fieldValueChanged18] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("dataSource_fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_fieldValueChanged18].setDisplayName ( "" );
            methods[METHOD_dataSource_toolTipFieldValueChanged19] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("dataSource_toolTipFieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_toolTipFieldValueChanged19].setDisplayName ( "" );
            methods[METHOD_deliverEvent20] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent20].setDisplayName ( "" );
            methods[METHOD_disable21] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable21].setDisplayName ( "" );
            methods[METHOD_dispatchEvent22] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent22].setDisplayName ( "" );
            methods[METHOD_doLayout23] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout23].setDisplayName ( "" );
            methods[METHOD_enable24] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable24].setDisplayName ( "" );
            methods[METHOD_enableInputMethods25] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods25].setDisplayName ( "" );
            methods[METHOD_findComponentAt26] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt26].setDisplayName ( "" );
            methods[METHOD_firePropertyChange27] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange27].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke28] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke28].setDisplayName ( "" );
            methods[METHOD_getBounds29] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds29].setDisplayName ( "" );
            methods[METHOD_getClientProperty30] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty30].setDisplayName ( "" );
            methods[METHOD_getComponentAt31] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt31].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder32] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder32].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke33] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke33].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale34] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale34].setDisplayName ( "" );
            methods[METHOD_getFontMetrics35] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics35].setDisplayName ( "" );
            methods[METHOD_getInsets36] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets36].setDisplayName ( "" );
            methods[METHOD_getKeymap37] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getKeymap37].setDisplayName ( "" );
            methods[METHOD_getListeners38] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners38].setDisplayName ( "" );
            methods[METHOD_getLocation39] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation39].setDisplayName ( "" );
            methods[METHOD_getMousePosition40] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition40].setDisplayName ( "" );
            methods[METHOD_getPopupLocation41] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation41].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners42] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners42].setDisplayName ( "" );
            methods[METHOD_getScrollableBlockIncrement43] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getScrollableBlockIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableBlockIncrement43].setDisplayName ( "" );
            methods[METHOD_getScrollableUnitIncrement44] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getScrollableUnitIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableUnitIncrement44].setDisplayName ( "" );
            methods[METHOD_getSize45] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize45].setDisplayName ( "" );
            methods[METHOD_getText46] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getText", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getText46].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation47] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation47].setDisplayName ( "" );
            methods[METHOD_getToolTipText48] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText48].setDisplayName ( "" );
            methods[METHOD_gotFocus49] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus49].setDisplayName ( "" );
            methods[METHOD_grabFocus50] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus50].setDisplayName ( "" );
            methods[METHOD_handleEvent51] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent51].setDisplayName ( "" );
            methods[METHOD_hasFocus52] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus52].setDisplayName ( "" );
            methods[METHOD_hide53] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide53].setDisplayName ( "" );
            methods[METHOD_imageUpdate54] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate54].setDisplayName ( "" );
            methods[METHOD_insertUpdate55] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("insertUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_insertUpdate55].setDisplayName ( "" );
            methods[METHOD_insets56] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets56].setDisplayName ( "" );
            methods[METHOD_inside57] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside57].setDisplayName ( "" );
            methods[METHOD_invalidate58] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate58].setDisplayName ( "" );
            methods[METHOD_isAncestorOf59] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf59].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot60] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot60].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent61] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent61].setDisplayName ( "" );
            methods[METHOD_keyDown62] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown62].setDisplayName ( "" );
            methods[METHOD_keyUp63] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp63].setDisplayName ( "" );
            methods[METHOD_layout64] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout64].setDisplayName ( "" );
            methods[METHOD_list65] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list65].setDisplayName ( "" );
            methods[METHOD_loadKeymap66] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("loadKeymap", new Class[] {javax.swing.text.Keymap.class, javax.swing.text.JTextComponent.KeyBinding[].class, javax.swing.Action[].class})); // NOI18N
            methods[METHOD_loadKeymap66].setDisplayName ( "" );
            methods[METHOD_locate67] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate67].setDisplayName ( "" );
            methods[METHOD_location68] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location68].setDisplayName ( "" );
            methods[METHOD_lostFocus69] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus69].setDisplayName ( "" );
            methods[METHOD_minimumSize70] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize70].setDisplayName ( "" );
            methods[METHOD_modelToView71] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("modelToView", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_modelToView71].setDisplayName ( "" );
            methods[METHOD_mouseDown72] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown72].setDisplayName ( "" );
            methods[METHOD_mouseDrag73] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag73].setDisplayName ( "" );
            methods[METHOD_mouseEnter74] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter74].setDisplayName ( "" );
            methods[METHOD_mouseExit75] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit75].setDisplayName ( "" );
            methods[METHOD_mouseMove76] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove76].setDisplayName ( "" );
            methods[METHOD_mouseUp77] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp77].setDisplayName ( "" );
            methods[METHOD_move78] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move78].setDisplayName ( "" );
            methods[METHOD_moveCaretPosition79] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("moveCaretPosition", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_moveCaretPosition79].setDisplayName ( "" );
            methods[METHOD_nextFocus80] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus80].setDisplayName ( "" );
            methods[METHOD_paint81] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint81].setDisplayName ( "" );
            methods[METHOD_paintAll82] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll82].setDisplayName ( "" );
            methods[METHOD_paintComponents83] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents83].setDisplayName ( "" );
            methods[METHOD_paintImmediately84] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately84].setDisplayName ( "" );
            methods[METHOD_paste85] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("paste", new Class[] {})); // NOI18N
            methods[METHOD_paste85].setDisplayName ( "" );
            methods[METHOD_postActionEvent86] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("postActionEvent", new Class[] {})); // NOI18N
            methods[METHOD_postActionEvent86].setDisplayName ( "" );
            methods[METHOD_postEvent87] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent87].setDisplayName ( "" );
            methods[METHOD_preferredSize88] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize88].setDisplayName ( "" );
            methods[METHOD_prepareImage89] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage89].setDisplayName ( "" );
            methods[METHOD_print90] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print90].setDisplayName ( "" );
            methods[METHOD_printAll91] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll91].setDisplayName ( "" );
            methods[METHOD_printComponents92] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents92].setDisplayName ( "" );
            methods[METHOD_putClientProperty93] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty93].setDisplayName ( "" );
            methods[METHOD_read94] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("read", new Class[] {java.io.Reader.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_read94].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction95] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction95].setDisplayName ( "" );
            methods[METHOD_remove96] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove96].setDisplayName ( "" );
            methods[METHOD_removeAll97] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll97].setDisplayName ( "" );
            methods[METHOD_removeKeymap98] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("removeKeymap", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_removeKeymap98].setDisplayName ( "" );
            methods[METHOD_removeNotify99] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify99].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener100] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener100].setDisplayName ( "" );
            methods[METHOD_removeUpdate101] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("removeUpdate", new Class[] {javax.swing.event.DocumentEvent.class})); // NOI18N
            methods[METHOD_removeUpdate101].setDisplayName ( "" );
            methods[METHOD_repaint102] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint102].setDisplayName ( "" );
            methods[METHOD_replaceSelection103] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("replaceSelection", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_replaceSelection103].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus104] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus104].setDisplayName ( "" );
            methods[METHOD_requestFocus105] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus105].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow106] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow106].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions107] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions107].setDisplayName ( "" );
            methods[METHOD_reshape108] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape108].setDisplayName ( "" );
            methods[METHOD_resize109] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize109].setDisplayName ( "" );
            methods[METHOD_revalidate110] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate110].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible111] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible111].setDisplayName ( "" );
            methods[METHOD_select112] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("select", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_select112].setDisplayName ( "" );
            methods[METHOD_selectAll113] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("selectAll", new Class[] {})); // NOI18N
            methods[METHOD_selectAll113].setDisplayName ( "" );
            methods[METHOD_setBounds114] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds114].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder115] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder115].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale116] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale116].setDisplayName ( "" );
            methods[METHOD_show117] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show117].setDisplayName ( "" );
            methods[METHOD_size118] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size118].setDisplayName ( "" );
            methods[METHOD_this_focusGained119] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("this_focusGained", new Class[] {java.awt.event.FocusEvent.class})); // NOI18N
            methods[METHOD_this_focusGained119].setDisplayName ( "" );
            methods[METHOD_toString120] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString120].setDisplayName ( "" );
            methods[METHOD_transferFocus121] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus121].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward122] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward122].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle123] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle123].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle124] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle124].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction125] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction125].setDisplayName ( "" );
            methods[METHOD_update126] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update126].setDisplayName ( "" );
            methods[METHOD_updateUI127] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI127].setDisplayName ( "" );
            methods[METHOD_validate128] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate128].setDisplayName ( "" );
            methods[METHOD_viewToModel129] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("viewToModel", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_viewToModel129].setDisplayName ( "" );
            methods[METHOD_write130] = new MethodDescriptor ( com.openitech.db.components.JDbTextField.class.getMethod("write", new Class[] {java.io.Writer.class})); // NOI18N
            methods[METHOD_write130].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbTextField.class.getSuperclass();
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

