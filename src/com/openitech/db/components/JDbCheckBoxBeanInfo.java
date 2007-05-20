/*
 * JDbCheckBoxBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 11:54
 */

package com.openitech.db.components;

import java.beans.*;

/**
 * @author uros
 */
public class JDbCheckBoxBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbCheckBox.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_columnName = 0;
    private static final int PROPERTY_dataSource = 1;
    private static final int PROPERTY_dbFieldObserver = 2;
    private static final int PROPERTY_selected = 3;
    private static final int PROPERTY_toolTipColumnName = 4;
    private static final int PROPERTY_validator = 5;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[6];
    
        try {
            properties[PROPERTY_columnName] = new PropertyDescriptor ( "columnName", com.openitech.db.components.JDbCheckBox.class, "getColumnName", "setColumnName" ); // NOI18N
            properties[PROPERTY_columnName].setPreferred ( true );
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbCheckBox.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_dbFieldObserver] = new PropertyDescriptor ( "dbFieldObserver", com.openitech.db.components.JDbCheckBox.class, "getDbFieldObserver", null ); // NOI18N
            properties[PROPERTY_dbFieldObserver].setHidden ( true );
            properties[PROPERTY_selected] = new PropertyDescriptor ( "selected", com.openitech.db.components.JDbCheckBox.class, "isSelected", "setSelected" ); // NOI18N
            properties[PROPERTY_selected].setHidden ( true );
            properties[PROPERTY_toolTipColumnName] = new PropertyDescriptor ( "toolTipColumnName", com.openitech.db.components.JDbCheckBox.class, "getToolTipColumnName", "setToolTipColumnName" ); // NOI18N
            properties[PROPERTY_validator] = new PropertyDescriptor ( "validator", com.openitech.db.components.JDbCheckBox.class, "getValidator", "setValidator" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_actionListener = 0;
    private static final int EVENT_ancestorListener = 1;
    private static final int EVENT_changeListener = 2;
    private static final int EVENT_componentListener = 3;
    private static final int EVENT_containerListener = 4;
    private static final int EVENT_focusListener = 5;
    private static final int EVENT_hierarchyBoundsListener = 6;
    private static final int EVENT_hierarchyListener = 7;
    private static final int EVENT_inputMethodListener = 8;
    private static final int EVENT_itemListener = 9;
    private static final int EVENT_keyListener = 10;
    private static final int EVENT_mouseListener = 11;
    private static final int EVENT_mouseMotionListener = 12;
    private static final int EVENT_mouseWheelListener = 13;
    private static final int EVENT_propertyChangeListener = 14;
    private static final int EVENT_vetoableChangeListener = 15;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[16];
    
        try {
            eventSets[EVENT_actionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "actionListener", java.awt.event.ActionListener.class, new String[] {"actionPerformed"}, "addActionListener", "removeActionListener" ); // NOI18N
            eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorAdded", "ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener" ); // NOI18N
            eventSets[EVENT_changeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "changeListener", javax.swing.event.ChangeListener.class, new String[] {"stateChanged"}, "addChangeListener", "removeChangeListener" ); // NOI18N
            eventSets[EVENT_componentListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentHidden", "componentMoved", "componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" ); // NOI18N
            eventSets[EVENT_containerListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" ); // NOI18N
            eventSets[EVENT_focusListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener" ); // NOI18N
            eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" ); // NOI18N
            eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" ); // NOI18N
            eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"caretPositionChanged", "inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" ); // NOI18N
            eventSets[EVENT_itemListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "itemListener", java.awt.event.ItemListener.class, new String[] {"itemStateChanged"}, "addItemListener", "removeItemListener" ); // NOI18N
            eventSets[EVENT_keyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyPressed", "keyReleased", "keyTyped"}, "addKeyListener", "removeKeyListener" ); // NOI18N
            eventSets[EVENT_mouseListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseClicked", "mouseEntered", "mouseExited", "mousePressed", "mouseReleased"}, "addMouseListener", "removeMouseListener" ); // NOI18N
            eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" ); // NOI18N
            eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbCheckBox.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_action0 = 0;
    private static final int METHOD_actionPerformed1 = 1;
    private static final int METHOD_add2 = 2;
    private static final int METHOD_addNotify3 = 3;
    private static final int METHOD_addPropertyChangeListener4 = 4;
    private static final int METHOD_applyComponentOrientation5 = 5;
    private static final int METHOD_areFocusTraversalKeysSet6 = 6;
    private static final int METHOD_bounds7 = 7;
    private static final int METHOD_checkImage8 = 8;
    private static final int METHOD_computeVisibleRect9 = 9;
    private static final int METHOD_contains10 = 10;
    private static final int METHOD_countComponents11 = 11;
    private static final int METHOD_createImage12 = 12;
    private static final int METHOD_createToolTip13 = 13;
    private static final int METHOD_createVolatileImage14 = 14;
    private static final int METHOD_dataSource_fieldValueChanged15 = 15;
    private static final int METHOD_dataSource_toolTipFieldValueChanged16 = 16;
    private static final int METHOD_deliverEvent17 = 17;
    private static final int METHOD_disable18 = 18;
    private static final int METHOD_dispatchEvent19 = 19;
    private static final int METHOD_doClick20 = 20;
    private static final int METHOD_doLayout21 = 21;
    private static final int METHOD_enable22 = 22;
    private static final int METHOD_enableInputMethods23 = 23;
    private static final int METHOD_findComponentAt24 = 24;
    private static final int METHOD_firePropertyChange25 = 25;
    private static final int METHOD_getActionForKeyStroke26 = 26;
    private static final int METHOD_getBounds27 = 27;
    private static final int METHOD_getClientProperty28 = 28;
    private static final int METHOD_getComponentAt29 = 29;
    private static final int METHOD_getComponentZOrder30 = 30;
    private static final int METHOD_getConditionForKeyStroke31 = 31;
    private static final int METHOD_getDefaultLocale32 = 32;
    private static final int METHOD_getFontMetrics33 = 33;
    private static final int METHOD_getInsets34 = 34;
    private static final int METHOD_getListeners35 = 35;
    private static final int METHOD_getLocation36 = 36;
    private static final int METHOD_getMnemonic37 = 37;
    private static final int METHOD_getMousePosition38 = 38;
    private static final int METHOD_getPopupLocation39 = 39;
    private static final int METHOD_getPropertyChangeListeners40 = 40;
    private static final int METHOD_getSize41 = 41;
    private static final int METHOD_getToolTipLocation42 = 42;
    private static final int METHOD_getToolTipText43 = 43;
    private static final int METHOD_gotFocus44 = 44;
    private static final int METHOD_grabFocus45 = 45;
    private static final int METHOD_handleEvent46 = 46;
    private static final int METHOD_hasFocus47 = 47;
    private static final int METHOD_hide48 = 48;
    private static final int METHOD_imageUpdate49 = 49;
    private static final int METHOD_insets50 = 50;
    private static final int METHOD_inside51 = 51;
    private static final int METHOD_invalidate52 = 52;
    private static final int METHOD_isAncestorOf53 = 53;
    private static final int METHOD_isFocusCycleRoot54 = 54;
    private static final int METHOD_isLightweightComponent55 = 55;
    private static final int METHOD_keyDown56 = 56;
    private static final int METHOD_keyUp57 = 57;
    private static final int METHOD_layout58 = 58;
    private static final int METHOD_list59 = 59;
    private static final int METHOD_locate60 = 60;
    private static final int METHOD_location61 = 61;
    private static final int METHOD_lostFocus62 = 62;
    private static final int METHOD_minimumSize63 = 63;
    private static final int METHOD_mouseDown64 = 64;
    private static final int METHOD_mouseDrag65 = 65;
    private static final int METHOD_mouseEnter66 = 66;
    private static final int METHOD_mouseExit67 = 67;
    private static final int METHOD_mouseMove68 = 68;
    private static final int METHOD_mouseUp69 = 69;
    private static final int METHOD_move70 = 70;
    private static final int METHOD_nextFocus71 = 71;
    private static final int METHOD_paint72 = 72;
    private static final int METHOD_paintAll73 = 73;
    private static final int METHOD_paintComponents74 = 74;
    private static final int METHOD_paintImmediately75 = 75;
    private static final int METHOD_postEvent76 = 76;
    private static final int METHOD_preferredSize77 = 77;
    private static final int METHOD_prepareImage78 = 78;
    private static final int METHOD_print79 = 79;
    private static final int METHOD_printAll80 = 80;
    private static final int METHOD_printComponents81 = 81;
    private static final int METHOD_putClientProperty82 = 82;
    private static final int METHOD_registerKeyboardAction83 = 83;
    private static final int METHOD_remove84 = 84;
    private static final int METHOD_removeAll85 = 85;
    private static final int METHOD_removeNotify86 = 86;
    private static final int METHOD_removePropertyChangeListener87 = 87;
    private static final int METHOD_repaint88 = 88;
    private static final int METHOD_requestDefaultFocus89 = 89;
    private static final int METHOD_requestFocus90 = 90;
    private static final int METHOD_requestFocusInWindow91 = 91;
    private static final int METHOD_resetKeyboardActions92 = 92;
    private static final int METHOD_reshape93 = 93;
    private static final int METHOD_resize94 = 94;
    private static final int METHOD_revalidate95 = 95;
    private static final int METHOD_scrollRectToVisible96 = 96;
    private static final int METHOD_setBounds97 = 97;
    private static final int METHOD_setComponentZOrder98 = 98;
    private static final int METHOD_setDefaultLocale99 = 99;
    private static final int METHOD_setMnemonic100 = 100;
    private static final int METHOD_show101 = 101;
    private static final int METHOD_size102 = 102;
    private static final int METHOD_toString103 = 103;
    private static final int METHOD_transferFocus104 = 104;
    private static final int METHOD_transferFocusBackward105 = 105;
    private static final int METHOD_transferFocusDownCycle106 = 106;
    private static final int METHOD_transferFocusUpCycle107 = 107;
    private static final int METHOD_unregisterKeyboardAction108 = 108;
    private static final int METHOD_update109 = 109;
    private static final int METHOD_updateUI110 = 110;
    private static final int METHOD_validate111 = 111;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[112];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_actionPerformed1] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("actionPerformed", new Class[] {java.awt.event.ActionEvent.class})); // NOI18N
            methods[METHOD_actionPerformed1].setDisplayName ( "" );
            methods[METHOD_add2] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add2].setDisplayName ( "" );
            methods[METHOD_addNotify3] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify3].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener4] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener4].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation5] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation5].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet6] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet6].setDisplayName ( "" );
            methods[METHOD_bounds7] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds7].setDisplayName ( "" );
            methods[METHOD_checkImage8] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage8].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect9] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect9].setDisplayName ( "" );
            methods[METHOD_contains10] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains10].setDisplayName ( "" );
            methods[METHOD_countComponents11] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents11].setDisplayName ( "" );
            methods[METHOD_createImage12] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage12].setDisplayName ( "" );
            methods[METHOD_createToolTip13] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip13].setDisplayName ( "" );
            methods[METHOD_createVolatileImage14] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage14].setDisplayName ( "" );
            methods[METHOD_dataSource_fieldValueChanged15] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("dataSource_fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_fieldValueChanged15].setDisplayName ( "" );
            methods[METHOD_dataSource_toolTipFieldValueChanged16] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("dataSource_toolTipFieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_dataSource_toolTipFieldValueChanged16].setDisplayName ( "" );
            methods[METHOD_deliverEvent17] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent17].setDisplayName ( "" );
            methods[METHOD_disable18] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable18].setDisplayName ( "" );
            methods[METHOD_dispatchEvent19] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent19].setDisplayName ( "" );
            methods[METHOD_doClick20] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("doClick", new Class[] {})); // NOI18N
            methods[METHOD_doClick20].setDisplayName ( "" );
            methods[METHOD_doLayout21] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout21].setDisplayName ( "" );
            methods[METHOD_enable22] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable22].setDisplayName ( "" );
            methods[METHOD_enableInputMethods23] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods23].setDisplayName ( "" );
            methods[METHOD_findComponentAt24] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt24].setDisplayName ( "" );
            methods[METHOD_firePropertyChange25] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange25].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke26] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke26].setDisplayName ( "" );
            methods[METHOD_getBounds27] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds27].setDisplayName ( "" );
            methods[METHOD_getClientProperty28] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty28].setDisplayName ( "" );
            methods[METHOD_getComponentAt29] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt29].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder30] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder30].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke31] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke31].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale32] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale32].setDisplayName ( "" );
            methods[METHOD_getFontMetrics33] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics33].setDisplayName ( "" );
            methods[METHOD_getInsets34] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets34].setDisplayName ( "" );
            methods[METHOD_getListeners35] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners35].setDisplayName ( "" );
            methods[METHOD_getLocation36] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation36].setDisplayName ( "" );
            methods[METHOD_getMnemonic37] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getMnemonic", new Class[] {})); // NOI18N
            methods[METHOD_getMnemonic37].setDisplayName ( "" );
            methods[METHOD_getMousePosition38] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition38].setDisplayName ( "" );
            methods[METHOD_getPopupLocation39] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation39].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners40] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners40].setDisplayName ( "" );
            methods[METHOD_getSize41] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize41].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation42] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation42].setDisplayName ( "" );
            methods[METHOD_getToolTipText43] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText43].setDisplayName ( "" );
            methods[METHOD_gotFocus44] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus44].setDisplayName ( "" );
            methods[METHOD_grabFocus45] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus45].setDisplayName ( "" );
            methods[METHOD_handleEvent46] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent46].setDisplayName ( "" );
            methods[METHOD_hasFocus47] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus47].setDisplayName ( "" );
            methods[METHOD_hide48] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide48].setDisplayName ( "" );
            methods[METHOD_imageUpdate49] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate49].setDisplayName ( "" );
            methods[METHOD_insets50] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets50].setDisplayName ( "" );
            methods[METHOD_inside51] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside51].setDisplayName ( "" );
            methods[METHOD_invalidate52] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate52].setDisplayName ( "" );
            methods[METHOD_isAncestorOf53] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf53].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot54] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot54].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent55] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent55].setDisplayName ( "" );
            methods[METHOD_keyDown56] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown56].setDisplayName ( "" );
            methods[METHOD_keyUp57] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp57].setDisplayName ( "" );
            methods[METHOD_layout58] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout58].setDisplayName ( "" );
            methods[METHOD_list59] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list59].setDisplayName ( "" );
            methods[METHOD_locate60] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate60].setDisplayName ( "" );
            methods[METHOD_location61] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location61].setDisplayName ( "" );
            methods[METHOD_lostFocus62] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus62].setDisplayName ( "" );
            methods[METHOD_minimumSize63] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize63].setDisplayName ( "" );
            methods[METHOD_mouseDown64] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown64].setDisplayName ( "" );
            methods[METHOD_mouseDrag65] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag65].setDisplayName ( "" );
            methods[METHOD_mouseEnter66] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter66].setDisplayName ( "" );
            methods[METHOD_mouseExit67] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit67].setDisplayName ( "" );
            methods[METHOD_mouseMove68] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove68].setDisplayName ( "" );
            methods[METHOD_mouseUp69] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp69].setDisplayName ( "" );
            methods[METHOD_move70] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move70].setDisplayName ( "" );
            methods[METHOD_nextFocus71] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus71].setDisplayName ( "" );
            methods[METHOD_paint72] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint72].setDisplayName ( "" );
            methods[METHOD_paintAll73] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll73].setDisplayName ( "" );
            methods[METHOD_paintComponents74] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents74].setDisplayName ( "" );
            methods[METHOD_paintImmediately75] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately75].setDisplayName ( "" );
            methods[METHOD_postEvent76] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent76].setDisplayName ( "" );
            methods[METHOD_preferredSize77] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize77].setDisplayName ( "" );
            methods[METHOD_prepareImage78] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage78].setDisplayName ( "" );
            methods[METHOD_print79] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print79].setDisplayName ( "" );
            methods[METHOD_printAll80] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll80].setDisplayName ( "" );
            methods[METHOD_printComponents81] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents81].setDisplayName ( "" );
            methods[METHOD_putClientProperty82] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty82].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction83] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction83].setDisplayName ( "" );
            methods[METHOD_remove84] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove84].setDisplayName ( "" );
            methods[METHOD_removeAll85] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll85].setDisplayName ( "" );
            methods[METHOD_removeNotify86] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify86].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener87] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener87].setDisplayName ( "" );
            methods[METHOD_repaint88] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint88].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus89] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus89].setDisplayName ( "" );
            methods[METHOD_requestFocus90] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus90].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow91] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow91].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions92] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions92].setDisplayName ( "" );
            methods[METHOD_reshape93] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape93].setDisplayName ( "" );
            methods[METHOD_resize94] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize94].setDisplayName ( "" );
            methods[METHOD_revalidate95] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate95].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible96] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible96].setDisplayName ( "" );
            methods[METHOD_setBounds97] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds97].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder98] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder98].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale99] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale99].setDisplayName ( "" );
            methods[METHOD_setMnemonic100] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("setMnemonic", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_setMnemonic100].setDisplayName ( "" );
            methods[METHOD_show101] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show101].setDisplayName ( "" );
            methods[METHOD_size102] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size102].setDisplayName ( "" );
            methods[METHOD_toString103] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString103].setDisplayName ( "" );
            methods[METHOD_transferFocus104] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus104].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward105] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward105].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle106] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle106].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle107] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle107].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction108] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction108].setDisplayName ( "" );
            methods[METHOD_update109] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update109].setDisplayName ( "" );
            methods[METHOD_updateUI110] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI110].setDisplayName ( "" );
            methods[METHOD_validate111] = new MethodDescriptor ( com.openitech.db.components.JDbCheckBox.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate111].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbCheckBox.class.getSuperclass();
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

