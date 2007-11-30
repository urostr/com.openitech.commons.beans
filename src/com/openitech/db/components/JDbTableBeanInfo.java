/*
 * JDbTableBeanInfo.java
 *
 * Created on Petek, 23 marec 2007, 15:36
 */

package com.openitech.db.components;

import java.beans.*;

/**
 * @author uros
 */
public class JDbTableBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.components.JDbTable.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_dataSource = 0;
    private static final int PROPERTY_model = 1;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[2];
    
        try {
            properties[PROPERTY_dataSource] = new PropertyDescriptor ( "dataSource", com.openitech.db.components.JDbTable.class, "getDataSource", "setDataSource" ); // NOI18N
            properties[PROPERTY_dataSource].setPreferred ( true );
            properties[PROPERTY_model] = new PropertyDescriptor ( "model", com.openitech.db.components.JDbTable.class, "getModel", "setModel" ); // NOI18N
            properties[PROPERTY_model].setPreferred ( true );
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
    private static final int METHOD_action0 = 0;
    private static final int METHOD_add1 = 1;
    private static final int METHOD_addColumn2 = 2;
    private static final int METHOD_addColumnSelectionInterval3 = 3;
    private static final int METHOD_addNotify4 = 4;
    private static final int METHOD_addPropertyChangeListener5 = 5;
    private static final int METHOD_addRowSelectionInterval6 = 6;
    private static final int METHOD_applyComponentOrientation7 = 7;
    private static final int METHOD_areFocusTraversalKeysSet8 = 8;
    private static final int METHOD_bounds9 = 9;
    private static final int METHOD_changeSelection10 = 10;
    private static final int METHOD_checkImage11 = 11;
    private static final int METHOD_clearSelection12 = 12;
    private static final int METHOD_columnAdded13 = 13;
    private static final int METHOD_columnAtPoint14 = 14;
    private static final int METHOD_columnMarginChanged15 = 15;
    private static final int METHOD_columnMoved16 = 16;
    private static final int METHOD_columnRemoved17 = 17;
    private static final int METHOD_columnSelectionChanged18 = 18;
    private static final int METHOD_computeVisibleRect19 = 19;
    private static final int METHOD_contains20 = 20;
    private static final int METHOD_convertColumnIndexToModel21 = 21;
    private static final int METHOD_convertColumnIndexToView22 = 22;
    private static final int METHOD_countComponents23 = 23;
    private static final int METHOD_createDefaultColumnsFromModel24 = 24;
    private static final int METHOD_createImage25 = 25;
    private static final int METHOD_createScrollPaneForTable26 = 26;
    private static final int METHOD_createToolTip27 = 27;
    private static final int METHOD_createVolatileImage28 = 28;
    private static final int METHOD_deliverEvent29 = 29;
    private static final int METHOD_disable30 = 30;
    private static final int METHOD_dispatchEvent31 = 31;
    private static final int METHOD_doLayout32 = 32;
    private static final int METHOD_editCellAt33 = 33;
    private static final int METHOD_editingCanceled34 = 34;
    private static final int METHOD_editingStopped35 = 35;
    private static final int METHOD_enable36 = 36;
    private static final int METHOD_enableInputMethods37 = 37;
    private static final int METHOD_findComponentAt38 = 38;
    private static final int METHOD_firePropertyChange39 = 39;
    private static final int METHOD_getActionForKeyStroke40 = 40;
    private static final int METHOD_getBounds41 = 41;
    private static final int METHOD_getCellEditor42 = 42;
    private static final int METHOD_getCellRect43 = 43;
    private static final int METHOD_getCellRenderer44 = 44;
    private static final int METHOD_getClientProperty45 = 45;
    private static final int METHOD_getColumn46 = 46;
    private static final int METHOD_getComponentAt47 = 47;
    private static final int METHOD_getComponentZOrder48 = 48;
    private static final int METHOD_getConditionForKeyStroke49 = 49;
    private static final int METHOD_getDefaultEditor50 = 50;
    private static final int METHOD_getDefaultLocale51 = 51;
    private static final int METHOD_getDefaultRenderer52 = 52;
    private static final int METHOD_getFontMetrics53 = 53;
    private static final int METHOD_getInsets54 = 54;
    private static final int METHOD_getListeners55 = 55;
    private static final int METHOD_getLocation56 = 56;
    private static final int METHOD_getMousePosition57 = 57;
    private static final int METHOD_getPopupLocation58 = 58;
    private static final int METHOD_getPrintable59 = 59;
    private static final int METHOD_getPropertyChangeListeners60 = 60;
    private static final int METHOD_getScrollableBlockIncrement61 = 61;
    private static final int METHOD_getScrollableUnitIncrement62 = 62;
    private static final int METHOD_getSize63 = 63;
    private static final int METHOD_getToolTipLocation64 = 64;
    private static final int METHOD_getToolTipText65 = 65;
    private static final int METHOD_getValueAt66 = 66;
    private static final int METHOD_gotFocus67 = 67;
    private static final int METHOD_grabFocus68 = 68;
    private static final int METHOD_handleEvent69 = 69;
    private static final int METHOD_hasFocus70 = 70;
    private static final int METHOD_hide71 = 71;
    private static final int METHOD_imageUpdate72 = 72;
    private static final int METHOD_insets73 = 73;
    private static final int METHOD_inside74 = 74;
    private static final int METHOD_invalidate75 = 75;
    private static final int METHOD_isAncestorOf76 = 76;
    private static final int METHOD_isCellEditable77 = 77;
    private static final int METHOD_isCellSelected78 = 78;
    private static final int METHOD_isColumnSelected79 = 79;
    private static final int METHOD_isFocusCycleRoot80 = 80;
    private static final int METHOD_isLightweightComponent81 = 81;
    private static final int METHOD_isRowSelected82 = 82;
    private static final int METHOD_keyDown83 = 83;
    private static final int METHOD_keyUp84 = 84;
    private static final int METHOD_layout85 = 85;
    private static final int METHOD_list86 = 86;
    private static final int METHOD_locate87 = 87;
    private static final int METHOD_location88 = 88;
    private static final int METHOD_lostFocus89 = 89;
    private static final int METHOD_minimumSize90 = 90;
    private static final int METHOD_mouseDown91 = 91;
    private static final int METHOD_mouseDrag92 = 92;
    private static final int METHOD_mouseEnter93 = 93;
    private static final int METHOD_mouseExit94 = 94;
    private static final int METHOD_mouseMove95 = 95;
    private static final int METHOD_mouseUp96 = 96;
    private static final int METHOD_move97 = 97;
    private static final int METHOD_moveColumn98 = 98;
    private static final int METHOD_nextFocus99 = 99;
    private static final int METHOD_paint100 = 100;
    private static final int METHOD_paintAll101 = 101;
    private static final int METHOD_paintComponents102 = 102;
    private static final int METHOD_paintImmediately103 = 103;
    private static final int METHOD_postEvent104 = 104;
    private static final int METHOD_preferredSize105 = 105;
    private static final int METHOD_prepareEditor106 = 106;
    private static final int METHOD_prepareImage107 = 107;
    private static final int METHOD_prepareRenderer108 = 108;
    private static final int METHOD_print109 = 109;
    private static final int METHOD_printAll110 = 110;
    private static final int METHOD_printComponents111 = 111;
    private static final int METHOD_putClientProperty112 = 112;
    private static final int METHOD_registerKeyboardAction113 = 113;
    private static final int METHOD_remove114 = 114;
    private static final int METHOD_removeAll115 = 115;
    private static final int METHOD_removeColumn116 = 116;
    private static final int METHOD_removeColumnSelectionInterval117 = 117;
    private static final int METHOD_removeEditor118 = 118;
    private static final int METHOD_removeNotify119 = 119;
    private static final int METHOD_removePropertyChangeListener120 = 120;
    private static final int METHOD_removeRowSelectionInterval121 = 121;
    private static final int METHOD_repaint122 = 122;
    private static final int METHOD_requestDefaultFocus123 = 123;
    private static final int METHOD_requestFocus124 = 124;
    private static final int METHOD_requestFocusInWindow125 = 125;
    private static final int METHOD_resetKeyboardActions126 = 126;
    private static final int METHOD_reshape127 = 127;
    private static final int METHOD_resize128 = 128;
    private static final int METHOD_revalidate129 = 129;
    private static final int METHOD_rowAtPoint130 = 130;
    private static final int METHOD_scrollRectToVisible131 = 131;
    private static final int METHOD_selectAll132 = 132;
    private static final int METHOD_setBounds133 = 133;
    private static final int METHOD_setComponentZOrder134 = 134;
    private static final int METHOD_setDefaultEditor135 = 135;
    private static final int METHOD_setDefaultLocale136 = 136;
    private static final int METHOD_setDefaultRenderer137 = 137;
    private static final int METHOD_setRowHeight138 = 138;
    private static final int METHOD_setValueAt139 = 139;
    private static final int METHOD_show140 = 140;
    private static final int METHOD_size141 = 141;
    private static final int METHOD_sizeColumnsToFit142 = 142;
    private static final int METHOD_tableChanged143 = 143;
    private static final int METHOD_tableModel_activeRowChanged144 = 144;
    private static final int METHOD_toString145 = 145;
    private static final int METHOD_transferFocus146 = 146;
    private static final int METHOD_transferFocusBackward147 = 147;
    private static final int METHOD_transferFocusDownCycle148 = 148;
    private static final int METHOD_transferFocusUpCycle149 = 149;
    private static final int METHOD_unregisterKeyboardAction150 = 150;
    private static final int METHOD_update151 = 151;
    private static final int METHOD_updateUI152 = 152;
    private static final int METHOD_validate153 = 153;
    private static final int METHOD_valueChanged154 = 154;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[155];
    
        try {
            methods[METHOD_action0] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_action0].setDisplayName ( "" );
            methods[METHOD_add1] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("add", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_add1].setDisplayName ( "" );
            methods[METHOD_addColumn2] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("addColumn", new Class[] {javax.swing.table.TableColumn.class})); // NOI18N
            methods[METHOD_addColumn2].setDisplayName ( "" );
            methods[METHOD_addColumnSelectionInterval3] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("addColumnSelectionInterval", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_addColumnSelectionInterval3].setDisplayName ( "" );
            methods[METHOD_addNotify4] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("addNotify", new Class[] {})); // NOI18N
            methods[METHOD_addNotify4].setDisplayName ( "" );
            methods[METHOD_addPropertyChangeListener5] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_addPropertyChangeListener5].setDisplayName ( "" );
            methods[METHOD_addRowSelectionInterval6] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("addRowSelectionInterval", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_addRowSelectionInterval6].setDisplayName ( "" );
            methods[METHOD_applyComponentOrientation7] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class})); // NOI18N
            methods[METHOD_applyComponentOrientation7].setDisplayName ( "" );
            methods[METHOD_areFocusTraversalKeysSet8] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_areFocusTraversalKeysSet8].setDisplayName ( "" );
            methods[METHOD_bounds9] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("bounds", new Class[] {})); // NOI18N
            methods[METHOD_bounds9].setDisplayName ( "" );
            methods[METHOD_changeSelection10] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("changeSelection", new Class[] {Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_changeSelection10].setDisplayName ( "" );
            methods[METHOD_checkImage11] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_checkImage11].setDisplayName ( "" );
            methods[METHOD_clearSelection12] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("clearSelection", new Class[] {})); // NOI18N
            methods[METHOD_clearSelection12].setDisplayName ( "" );
            methods[METHOD_columnAdded13] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnAdded", new Class[] {javax.swing.event.TableColumnModelEvent.class})); // NOI18N
            methods[METHOD_columnAdded13].setDisplayName ( "" );
            methods[METHOD_columnAtPoint14] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnAtPoint", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_columnAtPoint14].setDisplayName ( "" );
            methods[METHOD_columnMarginChanged15] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnMarginChanged", new Class[] {javax.swing.event.ChangeEvent.class})); // NOI18N
            methods[METHOD_columnMarginChanged15].setDisplayName ( "" );
            methods[METHOD_columnMoved16] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnMoved", new Class[] {javax.swing.event.TableColumnModelEvent.class})); // NOI18N
            methods[METHOD_columnMoved16].setDisplayName ( "" );
            methods[METHOD_columnRemoved17] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnRemoved", new Class[] {javax.swing.event.TableColumnModelEvent.class})); // NOI18N
            methods[METHOD_columnRemoved17].setDisplayName ( "" );
            methods[METHOD_columnSelectionChanged18] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("columnSelectionChanged", new Class[] {javax.swing.event.ListSelectionEvent.class})); // NOI18N
            methods[METHOD_columnSelectionChanged18].setDisplayName ( "" );
            methods[METHOD_computeVisibleRect19] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_computeVisibleRect19].setDisplayName ( "" );
            methods[METHOD_contains20] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_contains20].setDisplayName ( "" );
            methods[METHOD_convertColumnIndexToModel21] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("convertColumnIndexToModel", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_convertColumnIndexToModel21].setDisplayName ( "" );
            methods[METHOD_convertColumnIndexToView22] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("convertColumnIndexToView", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_convertColumnIndexToView22].setDisplayName ( "" );
            methods[METHOD_countComponents23] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("countComponents", new Class[] {})); // NOI18N
            methods[METHOD_countComponents23].setDisplayName ( "" );
            methods[METHOD_createDefaultColumnsFromModel24] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("createDefaultColumnsFromModel", new Class[] {})); // NOI18N
            methods[METHOD_createDefaultColumnsFromModel24].setDisplayName ( "" );
            methods[METHOD_createImage25] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class})); // NOI18N
            methods[METHOD_createImage25].setDisplayName ( "" );
            methods[METHOD_createScrollPaneForTable26] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("createScrollPaneForTable", new Class[] {javax.swing.JTable.class})); // NOI18N
            methods[METHOD_createScrollPaneForTable26].setDisplayName ( "" );
            methods[METHOD_createToolTip27] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("createToolTip", new Class[] {})); // NOI18N
            methods[METHOD_createToolTip27].setDisplayName ( "" );
            methods[METHOD_createVolatileImage28] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_createVolatileImage28].setDisplayName ( "" );
            methods[METHOD_deliverEvent29] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_deliverEvent29].setDisplayName ( "" );
            methods[METHOD_disable30] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("disable", new Class[] {})); // NOI18N
            methods[METHOD_disable30].setDisplayName ( "" );
            methods[METHOD_dispatchEvent31] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class})); // NOI18N
            methods[METHOD_dispatchEvent31].setDisplayName ( "" );
            methods[METHOD_doLayout32] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("doLayout", new Class[] {})); // NOI18N
            methods[METHOD_doLayout32].setDisplayName ( "" );
            methods[METHOD_editCellAt33] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("editCellAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_editCellAt33].setDisplayName ( "" );
            methods[METHOD_editingCanceled34] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("editingCanceled", new Class[] {javax.swing.event.ChangeEvent.class})); // NOI18N
            methods[METHOD_editingCanceled34].setDisplayName ( "" );
            methods[METHOD_editingStopped35] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("editingStopped", new Class[] {javax.swing.event.ChangeEvent.class})); // NOI18N
            methods[METHOD_editingStopped35].setDisplayName ( "" );
            methods[METHOD_enable36] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("enable", new Class[] {})); // NOI18N
            methods[METHOD_enable36].setDisplayName ( "" );
            methods[METHOD_enableInputMethods37] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_enableInputMethods37].setDisplayName ( "" );
            methods[METHOD_findComponentAt38] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_findComponentAt38].setDisplayName ( "" );
            methods[METHOD_firePropertyChange39] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_firePropertyChange39].setDisplayName ( "" );
            methods[METHOD_getActionForKeyStroke40] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getActionForKeyStroke40].setDisplayName ( "" );
            methods[METHOD_getBounds41] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_getBounds41].setDisplayName ( "" );
            methods[METHOD_getCellEditor42] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getCellEditor", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getCellEditor42].setDisplayName ( "" );
            methods[METHOD_getCellRect43] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getCellRect", new Class[] {Integer.TYPE, Integer.TYPE, Boolean.TYPE})); // NOI18N
            methods[METHOD_getCellRect43].setDisplayName ( "" );
            methods[METHOD_getCellRenderer44] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getCellRenderer", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getCellRenderer44].setDisplayName ( "" );
            methods[METHOD_getClientProperty45] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getClientProperty45].setDisplayName ( "" );
            methods[METHOD_getColumn46] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getColumn", new Class[] {java.lang.Object.class})); // NOI18N
            methods[METHOD_getColumn46].setDisplayName ( "" );
            methods[METHOD_getComponentAt47] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getComponentAt47].setDisplayName ( "" );
            methods[METHOD_getComponentZOrder48] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getComponentZOrder", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_getComponentZOrder48].setDisplayName ( "" );
            methods[METHOD_getConditionForKeyStroke49] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_getConditionForKeyStroke49].setDisplayName ( "" );
            methods[METHOD_getDefaultEditor50] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getDefaultEditor", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getDefaultEditor50].setDisplayName ( "" );
            methods[METHOD_getDefaultLocale51] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getDefaultLocale", new Class[] {})); // NOI18N
            methods[METHOD_getDefaultLocale51].setDisplayName ( "" );
            methods[METHOD_getDefaultRenderer52] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getDefaultRenderer", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getDefaultRenderer52].setDisplayName ( "" );
            methods[METHOD_getFontMetrics53] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class})); // NOI18N
            methods[METHOD_getFontMetrics53].setDisplayName ( "" );
            methods[METHOD_getInsets54] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getInsets", new Class[] {java.awt.Insets.class})); // NOI18N
            methods[METHOD_getInsets54].setDisplayName ( "" );
            methods[METHOD_getListeners55] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getListeners", new Class[] {java.lang.Class.class})); // NOI18N
            methods[METHOD_getListeners55].setDisplayName ( "" );
            methods[METHOD_getLocation56] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getLocation", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_getLocation56].setDisplayName ( "" );
            methods[METHOD_getMousePosition57] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getMousePosition", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_getMousePosition57].setDisplayName ( "" );
            methods[METHOD_getPopupLocation58] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getPopupLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getPopupLocation58].setDisplayName ( "" );
            methods[METHOD_getPrintable59] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getPrintable", new Class[] {javax.swing.JTable.PrintMode.class, java.text.MessageFormat.class, java.text.MessageFormat.class})); // NOI18N
            methods[METHOD_getPrintable59].setDisplayName ( "" );
            methods[METHOD_getPropertyChangeListeners60] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getPropertyChangeListeners60].setDisplayName ( "" );
            methods[METHOD_getScrollableBlockIncrement61] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getScrollableBlockIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableBlockIncrement61].setDisplayName ( "" );
            methods[METHOD_getScrollableUnitIncrement62] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getScrollableUnitIncrement", new Class[] {java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getScrollableUnitIncrement62].setDisplayName ( "" );
            methods[METHOD_getSize63] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getSize", new Class[] {java.awt.Dimension.class})); // NOI18N
            methods[METHOD_getSize63].setDisplayName ( "" );
            methods[METHOD_getToolTipLocation64] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipLocation64].setDisplayName ( "" );
            methods[METHOD_getToolTipText65] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class})); // NOI18N
            methods[METHOD_getToolTipText65].setDisplayName ( "" );
            methods[METHOD_getValueAt66] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("getValueAt", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_getValueAt66].setDisplayName ( "" );
            methods[METHOD_gotFocus67] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_gotFocus67].setDisplayName ( "" );
            methods[METHOD_grabFocus68] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("grabFocus", new Class[] {})); // NOI18N
            methods[METHOD_grabFocus68].setDisplayName ( "" );
            methods[METHOD_handleEvent69] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("handleEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_handleEvent69].setDisplayName ( "" );
            methods[METHOD_hasFocus70] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("hasFocus", new Class[] {})); // NOI18N
            methods[METHOD_hasFocus70].setDisplayName ( "" );
            methods[METHOD_hide71] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("hide", new Class[] {})); // NOI18N
            methods[METHOD_hide71].setDisplayName ( "" );
            methods[METHOD_imageUpdate72] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_imageUpdate72].setDisplayName ( "" );
            methods[METHOD_insets73] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("insets", new Class[] {})); // NOI18N
            methods[METHOD_insets73].setDisplayName ( "" );
            methods[METHOD_inside74] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_inside74].setDisplayName ( "" );
            methods[METHOD_invalidate75] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("invalidate", new Class[] {})); // NOI18N
            methods[METHOD_invalidate75].setDisplayName ( "" );
            methods[METHOD_isAncestorOf76] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isAncestorOf76].setDisplayName ( "" );
            methods[METHOD_isCellEditable77] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isCellEditable", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_isCellEditable77].setDisplayName ( "" );
            methods[METHOD_isCellSelected78] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isCellSelected", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_isCellSelected78].setDisplayName ( "" );
            methods[METHOD_isColumnSelected79] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isColumnSelected", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_isColumnSelected79].setDisplayName ( "" );
            methods[METHOD_isFocusCycleRoot80] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class})); // NOI18N
            methods[METHOD_isFocusCycleRoot80].setDisplayName ( "" );
            methods[METHOD_isLightweightComponent81] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class})); // NOI18N
            methods[METHOD_isLightweightComponent81].setDisplayName ( "" );
            methods[METHOD_isRowSelected82] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("isRowSelected", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_isRowSelected82].setDisplayName ( "" );
            methods[METHOD_keyDown83] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyDown83].setDisplayName ( "" );
            methods[METHOD_keyUp84] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE})); // NOI18N
            methods[METHOD_keyUp84].setDisplayName ( "" );
            methods[METHOD_layout85] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("layout", new Class[] {})); // NOI18N
            methods[METHOD_layout85].setDisplayName ( "" );
            methods[METHOD_list86] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE})); // NOI18N
            methods[METHOD_list86].setDisplayName ( "" );
            methods[METHOD_locate87] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_locate87].setDisplayName ( "" );
            methods[METHOD_location88] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("location", new Class[] {})); // NOI18N
            methods[METHOD_location88].setDisplayName ( "" );
            methods[METHOD_lostFocus89] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_lostFocus89].setDisplayName ( "" );
            methods[METHOD_minimumSize90] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("minimumSize", new Class[] {})); // NOI18N
            methods[METHOD_minimumSize90].setDisplayName ( "" );
            methods[METHOD_mouseDown91] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDown91].setDisplayName ( "" );
            methods[METHOD_mouseDrag92] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseDrag92].setDisplayName ( "" );
            methods[METHOD_mouseEnter93] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseEnter93].setDisplayName ( "" );
            methods[METHOD_mouseExit94] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseExit94].setDisplayName ( "" );
            methods[METHOD_mouseMove95] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseMove95].setDisplayName ( "" );
            methods[METHOD_mouseUp96] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_mouseUp96].setDisplayName ( "" );
            methods[METHOD_move97] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_move97].setDisplayName ( "" );
            methods[METHOD_moveColumn98] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("moveColumn", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_moveColumn98].setDisplayName ( "" );
            methods[METHOD_nextFocus99] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("nextFocus", new Class[] {})); // NOI18N
            methods[METHOD_nextFocus99].setDisplayName ( "" );
            methods[METHOD_paint100] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("paint", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paint100].setDisplayName ( "" );
            methods[METHOD_paintAll101] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintAll101].setDisplayName ( "" );
            methods[METHOD_paintComponents102] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_paintComponents102].setDisplayName ( "" );
            methods[METHOD_paintImmediately103] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_paintImmediately103].setDisplayName ( "" );
            methods[METHOD_postEvent104] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("postEvent", new Class[] {java.awt.Event.class})); // NOI18N
            methods[METHOD_postEvent104].setDisplayName ( "" );
            methods[METHOD_preferredSize105] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("preferredSize", new Class[] {})); // NOI18N
            methods[METHOD_preferredSize105].setDisplayName ( "" );
            methods[METHOD_prepareEditor106] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("prepareEditor", new Class[] {javax.swing.table.TableCellEditor.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_prepareEditor106].setDisplayName ( "" );
            methods[METHOD_prepareImage107] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class})); // NOI18N
            methods[METHOD_prepareImage107].setDisplayName ( "" );
            methods[METHOD_prepareRenderer108] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("prepareRenderer", new Class[] {javax.swing.table.TableCellRenderer.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_prepareRenderer108].setDisplayName ( "" );
            methods[METHOD_print109] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("print", new Class[] {})); // NOI18N
            methods[METHOD_print109].setDisplayName ( "" );
            methods[METHOD_printAll110] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("printAll", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printAll110].setDisplayName ( "" );
            methods[METHOD_printComponents111] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_printComponents111].setDisplayName ( "" );
            methods[METHOD_putClientProperty112] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_putClientProperty112].setDisplayName ( "" );
            methods[METHOD_registerKeyboardAction113] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE})); // NOI18N
            methods[METHOD_registerKeyboardAction113].setDisplayName ( "" );
            methods[METHOD_remove114] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("remove", new Class[] {Integer.TYPE})); // NOI18N
            methods[METHOD_remove114].setDisplayName ( "" );
            methods[METHOD_removeAll115] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeAll", new Class[] {})); // NOI18N
            methods[METHOD_removeAll115].setDisplayName ( "" );
            methods[METHOD_removeColumn116] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeColumn", new Class[] {javax.swing.table.TableColumn.class})); // NOI18N
            methods[METHOD_removeColumn116].setDisplayName ( "" );
            methods[METHOD_removeColumnSelectionInterval117] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeColumnSelectionInterval", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_removeColumnSelectionInterval117].setDisplayName ( "" );
            methods[METHOD_removeEditor118] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeEditor", new Class[] {})); // NOI18N
            methods[METHOD_removeEditor118].setDisplayName ( "" );
            methods[METHOD_removeNotify119] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeNotify", new Class[] {})); // NOI18N
            methods[METHOD_removeNotify119].setDisplayName ( "" );
            methods[METHOD_removePropertyChangeListener120] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class})); // NOI18N
            methods[METHOD_removePropertyChangeListener120].setDisplayName ( "" );
            methods[METHOD_removeRowSelectionInterval121] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("removeRowSelectionInterval", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_removeRowSelectionInterval121].setDisplayName ( "" );
            methods[METHOD_repaint122] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_repaint122].setDisplayName ( "" );
            methods[METHOD_requestDefaultFocus123] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("requestDefaultFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestDefaultFocus123].setDisplayName ( "" );
            methods[METHOD_requestFocus124] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("requestFocus", new Class[] {})); // NOI18N
            methods[METHOD_requestFocus124].setDisplayName ( "" );
            methods[METHOD_requestFocusInWindow125] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("requestFocusInWindow", new Class[] {})); // NOI18N
            methods[METHOD_requestFocusInWindow125].setDisplayName ( "" );
            methods[METHOD_resetKeyboardActions126] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("resetKeyboardActions", new Class[] {})); // NOI18N
            methods[METHOD_resetKeyboardActions126].setDisplayName ( "" );
            methods[METHOD_reshape127] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_reshape127].setDisplayName ( "" );
            methods[METHOD_resize128] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_resize128].setDisplayName ( "" );
            methods[METHOD_revalidate129] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("revalidate", new Class[] {})); // NOI18N
            methods[METHOD_revalidate129].setDisplayName ( "" );
            methods[METHOD_rowAtPoint130] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("rowAtPoint", new Class[] {java.awt.Point.class})); // NOI18N
            methods[METHOD_rowAtPoint130].setDisplayName ( "" );
            methods[METHOD_scrollRectToVisible131] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class})); // NOI18N
            methods[METHOD_scrollRectToVisible131].setDisplayName ( "" );
            methods[METHOD_selectAll132] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("selectAll", new Class[] {})); // NOI18N
            methods[METHOD_selectAll132].setDisplayName ( "" );
            methods[METHOD_setBounds133] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setBounds133].setDisplayName ( "" );
            methods[METHOD_setComponentZOrder134] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setComponentZOrder", new Class[] {java.awt.Component.class, Integer.TYPE})); // NOI18N
            methods[METHOD_setComponentZOrder134].setDisplayName ( "" );
            methods[METHOD_setDefaultEditor135] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setDefaultEditor", new Class[] {java.lang.Class.class, javax.swing.table.TableCellEditor.class})); // NOI18N
            methods[METHOD_setDefaultEditor135].setDisplayName ( "" );
            methods[METHOD_setDefaultLocale136] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class})); // NOI18N
            methods[METHOD_setDefaultLocale136].setDisplayName ( "" );
            methods[METHOD_setDefaultRenderer137] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setDefaultRenderer", new Class[] {java.lang.Class.class, javax.swing.table.TableCellRenderer.class})); // NOI18N
            methods[METHOD_setDefaultRenderer137].setDisplayName ( "" );
            methods[METHOD_setRowHeight138] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setRowHeight", new Class[] {Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setRowHeight138].setDisplayName ( "" );
            methods[METHOD_setValueAt139] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("setValueAt", new Class[] {java.lang.Object.class, Integer.TYPE, Integer.TYPE})); // NOI18N
            methods[METHOD_setValueAt139].setDisplayName ( "" );
            methods[METHOD_show140] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("show", new Class[] {})); // NOI18N
            methods[METHOD_show140].setDisplayName ( "" );
            methods[METHOD_size141] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("size", new Class[] {})); // NOI18N
            methods[METHOD_size141].setDisplayName ( "" );
            methods[METHOD_sizeColumnsToFit142] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("sizeColumnsToFit", new Class[] {Boolean.TYPE})); // NOI18N
            methods[METHOD_sizeColumnsToFit142].setDisplayName ( "" );
            methods[METHOD_tableChanged143] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("tableChanged", new Class[] {javax.swing.event.TableModelEvent.class})); // NOI18N
            methods[METHOD_tableChanged143].setDisplayName ( "" );
            methods[METHOD_tableModel_activeRowChanged144] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("tableModel_activeRowChanged", new Class[] {com.openitech.db.events.ActiveRowChangeEvent.class})); // NOI18N
            methods[METHOD_tableModel_activeRowChanged144].setDisplayName ( "" );
            methods[METHOD_toString145] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString145].setDisplayName ( "" );
            methods[METHOD_transferFocus146] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("transferFocus", new Class[] {})); // NOI18N
            methods[METHOD_transferFocus146].setDisplayName ( "" );
            methods[METHOD_transferFocusBackward147] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("transferFocusBackward", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusBackward147].setDisplayName ( "" );
            methods[METHOD_transferFocusDownCycle148] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("transferFocusDownCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusDownCycle148].setDisplayName ( "" );
            methods[METHOD_transferFocusUpCycle149] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("transferFocusUpCycle", new Class[] {})); // NOI18N
            methods[METHOD_transferFocusUpCycle149].setDisplayName ( "" );
            methods[METHOD_unregisterKeyboardAction150] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class})); // NOI18N
            methods[METHOD_unregisterKeyboardAction150].setDisplayName ( "" );
            methods[METHOD_update151] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("update", new Class[] {java.awt.Graphics.class})); // NOI18N
            methods[METHOD_update151].setDisplayName ( "" );
            methods[METHOD_updateUI152] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("updateUI", new Class[] {})); // NOI18N
            methods[METHOD_updateUI152].setDisplayName ( "" );
            methods[METHOD_validate153] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("validate", new Class[] {})); // NOI18N
            methods[METHOD_validate153].setDisplayName ( "" );
            methods[METHOD_valueChanged154] = new MethodDescriptor ( com.openitech.db.components.JDbTable.class.getMethod("valueChanged", new Class[] {javax.swing.event.ListSelectionEvent.class})); // NOI18N
            methods[METHOD_valueChanged154].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods


    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


    public BeanInfo[] getAdditionalBeanInfo() {//GEN-FIRST:Superclass
        Class superclass = com.openitech.db.components.JDbTable.class.getSuperclass();
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

