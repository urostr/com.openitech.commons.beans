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
            eventSets[EVENT_actionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "actionListener", java.awt.event.ActionListener.class, new String[] {"actionPerformed"}, "addActionListener", "removeActionListener" ); // NOI18N
            eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorAdded", "ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener" ); // NOI18N
            eventSets[EVENT_changeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "changeListener", javax.swing.event.ChangeListener.class, new String[] {"stateChanged"}, "addChangeListener", "removeChangeListener" ); // NOI18N
            eventSets[EVENT_componentListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentHidden", "componentMoved", "componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" ); // NOI18N
            eventSets[EVENT_containerListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" ); // NOI18N
            eventSets[EVENT_focusListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener" ); // NOI18N
            eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" ); // NOI18N
            eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" ); // NOI18N
            eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"caretPositionChanged", "inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" ); // NOI18N
            eventSets[EVENT_itemListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "itemListener", java.awt.event.ItemListener.class, new String[] {"itemStateChanged"}, "addItemListener", "removeItemListener" ); // NOI18N
            eventSets[EVENT_keyListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyPressed", "keyReleased", "keyTyped"}, "addKeyListener", "removeKeyListener" ); // NOI18N
            eventSets[EVENT_mouseListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseClicked", "mouseEntered", "mouseExited", "mousePressed", "mouseReleased"}, "addMouseListener", "removeMouseListener" ); // NOI18N
            eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" ); // NOI18N
            eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" ); // NOI18N
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
            eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( com.openitech.db.components.JDbControlButton.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_action0 = 0;
    private static final int METHOD_actionPerformed1 = 1;
    private static final int METHOD_activeRowChanged2 = 2;
    private static final int METHOD_add3 = 3;
    private static final int METHOD_addMnemonic4 = 4;
    private static final int METHOD_addNotify5 = 5;
    private static final int METHOD_addPropertyChangeListener6 = 6;
    private static final int METHOD_applyComponentOrientation7 = 7;
    private static final int METHOD_areFocusTraversalKeysSet8 = 8;
    private static final int METHOD_bounds9 = 9;
    private static final int METHOD_checkImage10 = 10;
    private static final int METHOD_computeVisibleRect11 = 11;
    private static final int METHOD_contains12 = 12;
    private static final int METHOD_countComponents13 = 13;
    private static final int METHOD_createImage14 = 14;
    private static final int METHOD_createToolTip15 = 15;
    private static final int METHOD_createVolatileImage16 = 16;
    private static final int METHOD_deliverEvent17 = 17;
    private static final int METHOD_disable18 = 18;
    private static final int METHOD_dispatchEvent19 = 19;
    private static final int METHOD_doClick20 = 20;
    private static final int METHOD_doLayout21 = 21;
    private static final int METHOD_enable22 = 22;
    private static final int METHOD_enableInputMethods23 = 23;
    private static final int METHOD_fieldValueChanged24 = 24;
    private static final int METHOD_findComponentAt25 = 25;
    private static final int METHOD_firePropertyChange26 = 26;
    private static final int METHOD_getActionForKeyStroke27 = 27;
    private static final int METHOD_getBounds28 = 28;
    private static final int METHOD_getClientProperty29 = 29;
    private static final int METHOD_getComponentAt30 = 30;
    private static final int METHOD_getComponentZOrder31 = 31;
    private static final int METHOD_getConditionForKeyStroke32 = 32;
    private static final int METHOD_getDefaultLocale33 = 33;
    private static final int METHOD_getFontMetrics34 = 34;
    private static final int METHOD_getInsets35 = 35;
    private static final int METHOD_getListeners36 = 36;
    private static final int METHOD_getLocation37 = 37;
    private static final int METHOD_getMnemonic38 = 38;
    private static final int METHOD_getMousePosition39 = 39;
    private static final int METHOD_getPopupLocation40 = 40;
    private static final int METHOD_getPropertyChangeListeners41 = 41;
    private static final int METHOD_getSize42 = 42;
    private static final int METHOD_getToolTipLocation43 = 43;
    private static final int METHOD_getToolTipText44 = 44;
    private static final int METHOD_gotFocus45 = 45;
    private static final int METHOD_grabFocus46 = 46;
    private static final int METHOD_handleEvent47 = 47;
    private static final int METHOD_hasFocus48 = 48;
    private static final int METHOD_hide49 = 49;
    private static final int METHOD_imageUpdate50 = 50;
    private static final int METHOD_insets51 = 51;
    private static final int METHOD_inside52 = 52;
    private static final int METHOD_invalidate53 = 53;
    private static final int METHOD_isAncestorOf54 = 54;
    private static final int METHOD_isFocusCycleRoot55 = 55;
    private static final int METHOD_isLightweightComponent56 = 56;
    private static final int METHOD_keyDown57 = 57;
    private static final int METHOD_keyUp58 = 58;
    private static final int METHOD_layout59 = 59;
    private static final int METHOD_list60 = 60;
    private static final int METHOD_locate61 = 61;
    private static final int METHOD_location62 = 62;
    private static final int METHOD_lostFocus63 = 63;
    private static final int METHOD_minimumSize64 = 64;
    private static final int METHOD_mouseDown65 = 65;
    private static final int METHOD_mouseDrag66 = 66;
    private static final int METHOD_mouseEnter67 = 67;
    private static final int METHOD_mouseExit68 = 68;
    private static final int METHOD_mouseMove69 = 69;
    private static final int METHOD_mouseUp70 = 70;
    private static final int METHOD_move71 = 71;
    private static final int METHOD_nextFocus72 = 72;
    private static final int METHOD_paint73 = 73;
    private static final int METHOD_paintAll74 = 74;
    private static final int METHOD_paintComponents75 = 75;
    private static final int METHOD_paintImmediately76 = 76;
    private static final int METHOD_postEvent77 = 77;
    private static final int METHOD_preferredSize78 = 78;
    private static final int METHOD_prepareImage79 = 79;
    private static final int METHOD_print80 = 80;
    private static final int METHOD_printAll81 = 81;
    private static final int METHOD_printComponents82 = 82;
    private static final int METHOD_putClientProperty83 = 83;
    private static final int METHOD_registerKeyboardAction84 = 84;
    private static final int METHOD_remove85 = 85;
    private static final int METHOD_removeAll86 = 86;
    private static final int METHOD_removeNotify87 = 87;
    private static final int METHOD_removePropertyChangeListener88 = 88;
    private static final int METHOD_repaint89 = 89;
    private static final int METHOD_requestDefaultFocus90 = 90;
    private static final int METHOD_requestFocus91 = 91;
    private static final int METHOD_requestFocusInWindow92 = 92;
    private static final int METHOD_resetKeyboardActions93 = 93;
    private static final int METHOD_reshape94 = 94;
    private static final int METHOD_resize95 = 95;
    private static final int METHOD_revalidate96 = 96;
    private static final int METHOD_scrollRectToVisible97 = 97;
    private static final int METHOD_setBounds98 = 98;
    private static final int METHOD_setComponentZOrder99 = 99;
    private static final int METHOD_setDefaultLocale100 = 100;
    private static final int METHOD_setMnemonic101 = 101;
    private static final int METHOD_show102 = 102;
    private static final int METHOD_size103 = 103;
    private static final int METHOD_toString104 = 104;
    private static final int METHOD_transferFocus105 = 105;
    private static final int METHOD_transferFocusBackward106 = 106;
    private static final int METHOD_transferFocusDownCycle107 = 107;
    private static final int METHOD_transferFocusUpCycle108 = 108;
    private static final int METHOD_unregisterKeyboardAction109 = 109;
    private static final int METHOD_update110 = 110;
    private static final int METHOD_updateUI111 = 111;
    private static final int METHOD_validate112 = 112;

    // Method array
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[113];

        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_actionPerformed1] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("actionPerformed", new Class[] {java.awt.event.ActionEvent.class})); // NOI18N
            methods[METHOD_actionPerformed1].setDisplayName ( "" );
            methods[METHOD_activeRowChanged2] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_activeRowChanged2].setDisplayName ( "" );
            methods[METHOD_add3] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add3].setDisplayName ( "" );
            methods[METHOD_addMnemonic4] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("addMnemonic", new Class[] {Character.TYPE})); // NOI18N
            methods[METHOD_addMnemonic4].setDisplayName ( "" );
            methods[METHOD_addNotify5] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify5].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener6] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener6].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation7] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation7].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet8] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet8].setDisplayName ( "" );
            methods[METHOD_bounds9] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds9].setDisplayName ( "" );
            methods[METHOD_checkImage10] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage10].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect11] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect11].setDisplayName ( "" );
            methods[METHOD_contains12] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains12].setDisplayName ( "" );
            methods[METHOD_countComponents13] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents13].setDisplayName ( "" );
            methods[METHOD_createImage14] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage14].setDisplayName ( "" );
            methods[METHOD_createToolTip15] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip15].setDisplayName ( "" );
            methods[METHOD_createVolatileImage16] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage16].setDisplayName ( "" );
            methods[METHOD_deliverEvent17] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent17].setDisplayName ( "" );
            methods[METHOD_disable18] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable18].setDisplayName ( "" );
            methods[METHOD_dispatchEvent19] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent19].setDisplayName ( "" );
            methods[METHOD_doClick20] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("doClick", new Class[] {})); // NOI18N
            methods[METHOD_doClick20].setDisplayName ( "" );
            methods[METHOD_doLayout21] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout21].setDisplayName ( "" );
            methods[METHOD_enable22] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable22].setDisplayName ( "" );
            methods[METHOD_enableInputMethods23] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods23].setDisplayName ( "" );
            methods[METHOD_fieldValueChanged24] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("fieldValueChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_fieldValueChanged24].setDisplayName ( "" );
            methods[METHOD_findComponentAt25] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt25].setDisplayName ( "" );
            methods[METHOD_firePropertyChange26] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange26].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke27] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke27].setDisplayName ( "" );
            methods[METHOD_getBounds28] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds28].setDisplayName ( "" );
            methods[METHOD_getClientProperty29] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty29].setDisplayName ( "" );
            methods[METHOD_getComponentAt30] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt30].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder31] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder31].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke32] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke32].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale33] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale33].setDisplayName ( "" );
            methods[METHOD_getFontMetrics34] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics34].setDisplayName ( "" );
            methods[METHOD_getInsets35] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets35].setDisplayName ( "" );
            methods[METHOD_getListeners36] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners36].setDisplayName ( "" );
            methods[METHOD_getLocation37] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation37].setDisplayName ( "" );
            methods[METHOD_getMnemonic38] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getMnemonic", new Class[] {})); // NOI18N
            methods[METHOD_getMnemonic38].setDisplayName ( "" );
            methods[METHOD_getMousePosition39] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition39].setDisplayName ( "" );
            methods[METHOD_getPopupLocation40] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation40].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners41] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners41].setDisplayName ( "" );
            methods[METHOD_getSize42] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize42].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation43] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation43].setDisplayName ( "" );
            methods[METHOD_getToolTipText44] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText44].setDisplayName ( "" );
            methods[METHOD_gotFocus45] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus45].setDisplayName ( "" );
            methods[METHOD_grabFocus46] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus46].setDisplayName ( "" );
            methods[METHOD_handleEvent47] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent47].setDisplayName ( "" );
            methods[METHOD_hasFocus48] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus48].setDisplayName ( "" );
            methods[METHOD_hide49] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide49].setDisplayName ( "" );
            methods[METHOD_imageUpdate50] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate50].setDisplayName ( "" );
            methods[METHOD_insets51] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets51].setDisplayName ( "" );
            methods[METHOD_inside52] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside52].setDisplayName ( "" );
            methods[METHOD_invalidate53] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate53].setDisplayName ( "" );
            methods[METHOD_isAncestorOf54] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf54].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot55] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot55].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent56] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent56].setDisplayName ( "" );
            methods[METHOD_keyDown57] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown57].setDisplayName ( "" );
            methods[METHOD_keyUp58] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp58].setDisplayName ( "" );
            methods[METHOD_layout59] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout59].setDisplayName ( "" );
            methods[METHOD_list60] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list60].setDisplayName ( "" );
            methods[METHOD_locate61] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate61].setDisplayName ( "" );
            methods[METHOD_location62] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location62].setDisplayName ( "" );
            methods[METHOD_lostFocus63] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus63].setDisplayName ( "" );
            methods[METHOD_minimumSize64] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize64].setDisplayName ( "" );
            methods[METHOD_mouseDown65] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown65].setDisplayName ( "" );
            methods[METHOD_mouseDrag66] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag66].setDisplayName ( "" );
            methods[METHOD_mouseEnter67] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter67].setDisplayName ( "" );
            methods[METHOD_mouseExit68] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit68].setDisplayName ( "" );
            methods[METHOD_mouseMove69] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove69].setDisplayName ( "" );
            methods[METHOD_mouseUp70] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp70].setDisplayName ( "" );
            methods[METHOD_move71] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move71].setDisplayName ( "" );
            methods[METHOD_nextFocus72] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus72].setDisplayName ( "" );
            methods[METHOD_paint73] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint73].setDisplayName ( "" );
            methods[METHOD_paintAll74] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll74].setDisplayName ( "" );
            methods[METHOD_paintComponents75] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents75].setDisplayName ( "" );
            methods[METHOD_paintImmediately76] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately76].setDisplayName ( "" );
            methods[METHOD_postEvent77] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent77].setDisplayName ( "" );
            methods[METHOD_preferredSize78] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize78].setDisplayName ( "" );
            methods[METHOD_prepareImage79] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage79].setDisplayName ( "" );
            methods[METHOD_print80] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("print", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_print80].setDisplayName ( "" );
            methods[METHOD_printAll81] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll81].setDisplayName ( "" );
            methods[METHOD_printComponents82] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents82].setDisplayName ( "" );
            methods[METHOD_putClientProperty83] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty83].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction84] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction84].setDisplayName ( "" );
            methods[METHOD_remove85] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove85].setDisplayName ( "" );
            methods[METHOD_removeAll86] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll86].setDisplayName ( "" );
            methods[METHOD_removeNotify87] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify87].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener88] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener88].setDisplayName ( "" );
            methods[METHOD_repaint89] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint89].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus90] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus90].setDisplayName ( "" );
            methods[METHOD_requestFocus91] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus91].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow92] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow92].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions93] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions93].setDisplayName ( "" );
            methods[METHOD_reshape94] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape94].setDisplayName ( "" );
            methods[METHOD_resize95] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize95].setDisplayName ( "" );
            methods[METHOD_revalidate96] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate96].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible97] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible97].setDisplayName ( "" );
            methods[METHOD_setBounds98] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds98].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder99] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder99].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale100] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale100].setDisplayName ( "" );
            methods[METHOD_setMnemonic101] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("setMnemonic", new Class[] {Character.TYPE})); // NOI18N
            methods[METHOD_setMnemonic101].setDisplayName ( "" );
            methods[METHOD_show102] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show102].setDisplayName ( "" );
            methods[METHOD_size103] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size103].setDisplayName ( "" );
            methods[METHOD_toString104] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString104].setDisplayName ( "" );
            methods[METHOD_transferFocus105] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus105].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward106] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward106].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle107] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle107].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle108] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle108].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction109] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction109].setDisplayName ( "" );
            methods[METHOD_update110] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update110].setDisplayName ( "" );
            methods[METHOD_updateUI111] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI111].setDisplayName ( "" );
            methods[METHOD_validate112] = new MethodDescriptor ( com.openitech.db.components.JDbControlButton.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate112].setDisplayName ( "" );
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

