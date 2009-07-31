/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.db.model.tree.DbTreeNode;
import com.openitech.db.model.tree.DbTreeNodeFactory;
import com.openitech.db.model.tree.DbTreeNodeType;
import com.openitech.db.model.tree.TreeNodeFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author uros
 */
public class JDbTree extends javax.swing.JTree {

  /** Creates children for expanded nodes. */
  private transient TreeNodeFactory<DbTreeNode> factory = new DbTreeNodeFactory();
  /** The active worker. */
  private transient DbTreeNodeFactory.FactoryWorker<DefaultMutableTreeNode[]> worker;

  public JDbTree() {
    this(new DbTreeNodeType());
  }

  public JDbTree(DbTreeNodeType type) {
    super(new DbTreeNode(type, null));
    final DefaultTreeModel model = (DefaultTreeModel) super.getModel();
    model.setAsksAllowsChildren(true);
    addTreeExpansionListener(new TreeExpansionListener() {

      @Override
      public void treeExpanded(TreeExpansionEvent event) {
        jTreeTreeExpanded(event);
      }

      @Override
      public void treeCollapsed(TreeExpansionEvent event) {
        jTreeTreeCollapsed(event);
      }
    });
    addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent evt) {
        jTreeValueChanged(evt);
      }
    });
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() > 1) && (getSelectionPath() != null)) {
          stopWorker();

          final DefaultTreeModel model = (DefaultTreeModel) JDbTree.this.getModel();
          final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();

          node.removeAllChildren();
          
          model.setAsksAllowsChildren(false);
          model.nodeStructureChanged(node);
          model.setAsksAllowsChildren(true);

          startWorker(factory, node);
        }
      }
    });
  }

  /**
   * Called when a node is expanded. Stops the active worker, if any, and
   * starts a new worker to create children for the expanded node.
   */
  private void jTreeTreeExpanded(TreeExpansionEvent evt) {
    stopWorker();
    DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
    if (factory != null) {
      startWorker(factory, node);
    }
  }

  /**
   * Called when a node is collapsed. Stops the active worker, if any, and
   * removes all the children.
   */
  private void jTreeTreeCollapsed(TreeExpansionEvent evt) {
    stopWorker();
    DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
    node.removeAllChildren();
    DefaultTreeModel model = (DefaultTreeModel) this.getModel();

    /*
     * To avoid having JTree re-expand the root node, we disable
     * ask-allows-children when we notify JTree about the new node
     * structure.
     */

    model.setAsksAllowsChildren(false);
    model.nodeStructureChanged(node);
    model.setAsksAllowsChildren(true);
  }

  /** Updates the status line when a node is selected. */
  private void jTreeValueChanged(TreeSelectionEvent evt) {
    Object node = evt.getPath().getLastPathComponent();
    Logger.getLogger(JDbTree.class.getName()).finer(evt.isAddedPath() ? "Selected " + node : "");
  }

  /**
   * Given a node factory and an expanded node, starts a SwingWorker to create
   * children for the expanded node and insert them into the tree.
   */
  protected void startWorker(final TreeNodeFactory fac,
          final DefaultMutableTreeNode node) {

    worker = new DbTreeNodeFactory.FactoryWorker<DefaultMutableTreeNode[]>() {

      protected DefaultMutableTreeNode[] construct() throws Exception {
        /* Create children for the expanded node. */
        return fac.createChildren(node);
      }

      protected void finished() {
        /*
         * Set the worker to null and stop the animation, but only if we
         * are the active worker.
         */
        if (worker == this) {
          worker = null;
        }
        try {
          /*
           * Get the children created by the factory and insert them
           * into the local tree model.
           */
          DefaultMutableTreeNode[] children;
          children = get();
          for (int i = 0; i < children.length; i++) {
            node.insert(children[i], i);
          }
          DefaultTreeModel model =
                  (DefaultTreeModel) JDbTree.this.getModel();
          model.nodeStructureChanged(node);
        } catch (CancellationException ex) {
          Logger.getLogger(JDbTree.class.getName()).log(Level.SEVERE, "Failed expanding " + node + ": cancelled", ex);
        } catch (ExecutionException ex) {
          /* Handle exceptions thrown by the factory method. */
          Throwable err = ex.getCause();
          Logger.getLogger(JDbTree.class.getName()).log(Level.SEVERE, "Failed expanding " + node + ": " + err, ex);
        } catch (InterruptedException ex) {
          // event-dispatch thread won't be interrupted
          throw new IllegalStateException(ex + "");
        }
      }
    };

    /* Start worker, update status line, and start animation. */
    worker.start();
  }

  /** Stops the active worker, if any. */
  protected void stopWorker() {
    if (worker != null) {
      worker.cancel(true);
      // worker set to null in finished
    }
  }
}
