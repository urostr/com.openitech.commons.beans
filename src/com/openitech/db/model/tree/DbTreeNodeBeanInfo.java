/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model.tree;

import java.beans.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class DbTreeNodeBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( com.openitech.db.model.tree.DbTreeNode.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_allowsChildren = 0;
    private static final int PROPERTY_childAt = 1;
    private static final int PROPERTY_childCount = 2;
    private static final int PROPERTY_depth = 3;
    private static final int PROPERTY_firstChild = 4;
    private static final int PROPERTY_firstLeaf = 5;
    private static final int PROPERTY_key = 6;
    private static final int PROPERTY_lastChild = 7;
    private static final int PROPERTY_lastLeaf = 8;
    private static final int PROPERTY_leaf = 9;
    private static final int PROPERTY_leafCount = 10;
    private static final int PROPERTY_level = 11;
    private static final int PROPERTY_nextLeaf = 12;
    private static final int PROPERTY_nextNode = 13;
    private static final int PROPERTY_nextSibling = 14;
    private static final int PROPERTY_parent = 15;
    private static final int PROPERTY_path = 16;
    private static final int PROPERTY_previousLeaf = 17;
    private static final int PROPERTY_previousNode = 18;
    private static final int PROPERTY_previousSibling = 19;
    private static final int PROPERTY_root = 20;
    private static final int PROPERTY_siblingCount = 21;
    private static final int PROPERTY_type = 22;
    private static final int PROPERTY_userObject = 23;
    private static final int PROPERTY_userObjectPath = 24;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[25];
    
        try {
            properties[PROPERTY_allowsChildren] = new PropertyDescriptor ( "allowsChildren", com.openitech.db.model.tree.DbTreeNode.class, "getAllowsChildren", "setAllowsChildren" ); // NOI18N
            properties[PROPERTY_childAt] = new IndexedPropertyDescriptor ( "childAt", com.openitech.db.model.tree.DbTreeNode.class, null, null, "getChildAt", null ); // NOI18N
            properties[PROPERTY_childCount] = new PropertyDescriptor ( "childCount", com.openitech.db.model.tree.DbTreeNode.class, "getChildCount", null ); // NOI18N
            properties[PROPERTY_depth] = new PropertyDescriptor ( "depth", com.openitech.db.model.tree.DbTreeNode.class, "getDepth", null ); // NOI18N
            properties[PROPERTY_firstChild] = new PropertyDescriptor ( "firstChild", com.openitech.db.model.tree.DbTreeNode.class, "getFirstChild", null ); // NOI18N
            properties[PROPERTY_firstLeaf] = new PropertyDescriptor ( "firstLeaf", com.openitech.db.model.tree.DbTreeNode.class, "getFirstLeaf", null ); // NOI18N
            properties[PROPERTY_key] = new PropertyDescriptor ( "key", com.openitech.db.model.tree.DbTreeNode.class, "getKey", null ); // NOI18N
            properties[PROPERTY_lastChild] = new PropertyDescriptor ( "lastChild", com.openitech.db.model.tree.DbTreeNode.class, "getLastChild", null ); // NOI18N
            properties[PROPERTY_lastLeaf] = new PropertyDescriptor ( "lastLeaf", com.openitech.db.model.tree.DbTreeNode.class, "getLastLeaf", null ); // NOI18N
            properties[PROPERTY_leaf] = new PropertyDescriptor ( "leaf", com.openitech.db.model.tree.DbTreeNode.class, "isLeaf", null ); // NOI18N
            properties[PROPERTY_leafCount] = new PropertyDescriptor ( "leafCount", com.openitech.db.model.tree.DbTreeNode.class, "getLeafCount", null ); // NOI18N
            properties[PROPERTY_level] = new PropertyDescriptor ( "level", com.openitech.db.model.tree.DbTreeNode.class, "getLevel", null ); // NOI18N
            properties[PROPERTY_nextLeaf] = new PropertyDescriptor ( "nextLeaf", com.openitech.db.model.tree.DbTreeNode.class, "getNextLeaf", null ); // NOI18N
            properties[PROPERTY_nextNode] = new PropertyDescriptor ( "nextNode", com.openitech.db.model.tree.DbTreeNode.class, "getNextNode", null ); // NOI18N
            properties[PROPERTY_nextSibling] = new PropertyDescriptor ( "nextSibling", com.openitech.db.model.tree.DbTreeNode.class, "getNextSibling", null ); // NOI18N
            properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", com.openitech.db.model.tree.DbTreeNode.class, "getParent", null ); // NOI18N
            properties[PROPERTY_path] = new PropertyDescriptor ( "path", com.openitech.db.model.tree.DbTreeNode.class, "getPath", null ); // NOI18N
            properties[PROPERTY_previousLeaf] = new PropertyDescriptor ( "previousLeaf", com.openitech.db.model.tree.DbTreeNode.class, "getPreviousLeaf", null ); // NOI18N
            properties[PROPERTY_previousNode] = new PropertyDescriptor ( "previousNode", com.openitech.db.model.tree.DbTreeNode.class, "getPreviousNode", null ); // NOI18N
            properties[PROPERTY_previousSibling] = new PropertyDescriptor ( "previousSibling", com.openitech.db.model.tree.DbTreeNode.class, "getPreviousSibling", null ); // NOI18N
            properties[PROPERTY_root] = new PropertyDescriptor ( "root", com.openitech.db.model.tree.DbTreeNode.class, "isRoot", null ); // NOI18N
            properties[PROPERTY_siblingCount] = new PropertyDescriptor ( "siblingCount", com.openitech.db.model.tree.DbTreeNode.class, "getSiblingCount", null ); // NOI18N
            properties[PROPERTY_type] = new PropertyDescriptor ( "type", com.openitech.db.model.tree.DbTreeNode.class, "getType", null ); // NOI18N
            properties[PROPERTY_type].setPreferred ( true );
            properties[PROPERTY_userObject] = new PropertyDescriptor ( "userObject", com.openitech.db.model.tree.DbTreeNode.class, "getUserObject", "setUserObject" ); // NOI18N
            properties[PROPERTY_userObjectPath] = new PropertyDescriptor ( "userObjectPath", com.openitech.db.model.tree.DbTreeNode.class, "getUserObjectPath", null ); // NOI18N
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
    private static final int METHOD_add0 = 0;
    private static final int METHOD_breadthFirstEnumeration1 = 1;
    private static final int METHOD_children2 = 2;
    private static final int METHOD_clone3 = 3;
    private static final int METHOD_depthFirstEnumeration4 = 4;
    private static final int METHOD_getChildAfter5 = 5;
    private static final int METHOD_getChildBefore6 = 6;
    private static final int METHOD_getIndex7 = 7;
    private static final int METHOD_getRoot8 = 8;
    private static final int METHOD_getSharedAncestor9 = 9;
    private static final int METHOD_insert10 = 10;
    private static final int METHOD_isNodeAncestor11 = 11;
    private static final int METHOD_isNodeChild12 = 12;
    private static final int METHOD_isNodeDescendant13 = 13;
    private static final int METHOD_isNodeRelated14 = 14;
    private static final int METHOD_isNodeSibling15 = 15;
    private static final int METHOD_pathFromAncestorEnumeration16 = 16;
    private static final int METHOD_postorderEnumeration17 = 17;
    private static final int METHOD_preorderEnumeration18 = 18;
    private static final int METHOD_remove19 = 19;
    private static final int METHOD_remove20 = 20;
    private static final int METHOD_removeAllChildren21 = 21;
    private static final int METHOD_removeFromParent22 = 22;
    private static final int METHOD_setParent23 = 23;
    private static final int METHOD_toString24 = 24;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[25];
    
        try {
            methods[METHOD_add0] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("add", new Class[] {javax.swing.tree.MutableTreeNode.class})); // NOI18N
            methods[METHOD_add0].setDisplayName ( "" );
            methods[METHOD_breadthFirstEnumeration1] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("breadthFirstEnumeration", new Class[] {})); // NOI18N
            methods[METHOD_breadthFirstEnumeration1].setDisplayName ( "" );
            methods[METHOD_children2] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("children", new Class[] {})); // NOI18N
            methods[METHOD_children2].setDisplayName ( "" );
            methods[METHOD_clone3] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("clone", new Class[] {})); // NOI18N
            methods[METHOD_clone3].setDisplayName ( "" );
            methods[METHOD_depthFirstEnumeration4] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("depthFirstEnumeration", new Class[] {})); // NOI18N
            methods[METHOD_depthFirstEnumeration4].setDisplayName ( "" );
            methods[METHOD_getChildAfter5] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("getChildAfter", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_getChildAfter5].setDisplayName ( "" );
            methods[METHOD_getChildBefore6] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("getChildBefore", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_getChildBefore6].setDisplayName ( "" );
            methods[METHOD_getIndex7] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("getIndex", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_getIndex7].setDisplayName ( "" );
            methods[METHOD_getRoot8] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("getRoot", new Class[] {})); // NOI18N
            methods[METHOD_getRoot8].setDisplayName ( "" );
            methods[METHOD_getSharedAncestor9] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("getSharedAncestor", new Class[] {javax.swing.tree.DefaultMutableTreeNode.class})); // NOI18N
            methods[METHOD_getSharedAncestor9].setDisplayName ( "" );
            methods[METHOD_insert10] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("insert", new Class[] {javax.swing.tree.MutableTreeNode.class, int.class})); // NOI18N
            methods[METHOD_insert10].setDisplayName ( "" );
            methods[METHOD_isNodeAncestor11] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("isNodeAncestor", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_isNodeAncestor11].setDisplayName ( "" );
            methods[METHOD_isNodeChild12] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("isNodeChild", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_isNodeChild12].setDisplayName ( "" );
            methods[METHOD_isNodeDescendant13] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("isNodeDescendant", new Class[] {javax.swing.tree.DefaultMutableTreeNode.class})); // NOI18N
            methods[METHOD_isNodeDescendant13].setDisplayName ( "" );
            methods[METHOD_isNodeRelated14] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("isNodeRelated", new Class[] {javax.swing.tree.DefaultMutableTreeNode.class})); // NOI18N
            methods[METHOD_isNodeRelated14].setDisplayName ( "" );
            methods[METHOD_isNodeSibling15] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("isNodeSibling", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_isNodeSibling15].setDisplayName ( "" );
            methods[METHOD_pathFromAncestorEnumeration16] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("pathFromAncestorEnumeration", new Class[] {javax.swing.tree.TreeNode.class})); // NOI18N
            methods[METHOD_pathFromAncestorEnumeration16].setDisplayName ( "" );
            methods[METHOD_postorderEnumeration17] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("postorderEnumeration", new Class[] {})); // NOI18N
            methods[METHOD_postorderEnumeration17].setDisplayName ( "" );
            methods[METHOD_preorderEnumeration18] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("preorderEnumeration", new Class[] {})); // NOI18N
            methods[METHOD_preorderEnumeration18].setDisplayName ( "" );
            methods[METHOD_remove19] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("remove", new Class[] {int.class})); // NOI18N
            methods[METHOD_remove19].setDisplayName ( "" );
            methods[METHOD_remove20] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("remove", new Class[] {javax.swing.tree.MutableTreeNode.class})); // NOI18N
            methods[METHOD_remove20].setDisplayName ( "" );
            methods[METHOD_removeAllChildren21] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("removeAllChildren", new Class[] {})); // NOI18N
            methods[METHOD_removeAllChildren21].setDisplayName ( "" );
            methods[METHOD_removeFromParent22] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("removeFromParent", new Class[] {})); // NOI18N
            methods[METHOD_removeFromParent22].setDisplayName ( "" );
            methods[METHOD_setParent23] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("setParent", new Class[] {javax.swing.tree.MutableTreeNode.class})); // NOI18N
            methods[METHOD_setParent23].setDisplayName ( "" );
            methods[METHOD_toString24] = new MethodDescriptor(javax.swing.tree.DefaultMutableTreeNode.class.getMethod("toString", new Class[] {})); // NOI18N
            methods[METHOD_toString24].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

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

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconMono32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
	default: return null;
        }
    }

}

