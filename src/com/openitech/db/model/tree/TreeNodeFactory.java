/*
 * TreeNodeFactory.java
 * 
 * Written by Joseph Bowbeer and released to the public domain,
 * as explained at http://creativecommons.org/licenses/publicdomain
 */

package com.openitech.db.model.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Remote model interface for dynamic node expansion. 
 *
 * @author  Joseph Bowbeer
 * @version 1.0
 */
public interface TreeNodeFactory<T extends DefaultMutableTreeNode> {
    /**
     * Creates and returns an array of child nodes for a newly-expanded parent
     * node. Called on worker thread. The <tt>userObject</tt> parameter is
     * the parent node's link to its counterpart in the remote model.
     * <p>
     * The {@link DefaultMutableTreeNode#allowsChildren allowsChildren} property
     * of each child is set false if the corresponding remote node is a leaf;
     * otherwise it is set true to indicate that the child can be expanded.
     * Each child is also assigned a <tt>userObject</tt> that links the child
     * to its counterpart in the remote model. 
     * 
     * @param userObject parent node's link to remote model
     * @return array of children
     * @throws Exception if failed to create children
     */
    T[] createChildren(T userObject) throws Exception;
}
