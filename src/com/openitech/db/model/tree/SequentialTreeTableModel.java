/*
 * SequentialTreeTableModel.java
 *
 * Created on Sobota, 12 januar 2008, 12:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.model.tree;

import com.openitech.db.model.*;
import com.openitech.Settings;
import com.openitech.db.filters.Scheduler;
import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 *
 * @author uros
 */
public class SequentialTreeTableModel extends DbTableModel implements TreeTableModel {

  /**
   * Provides support for event dispatching.
   */
  protected TreeModelSupport modelSupport;
  private DbTreeModelNode root = new DbTreeModelNode();
  private List<DbDataSourceIndex> indices = new ArrayList<DbDataSourceIndex>();
  /**
   * Holds value of property nodeColumns.
   */
  private int nodeColumns = 2;

  /** Creates a new instance of SequentialTreeTableModel */
  public SequentialTreeTableModel() {
    this.modelSupport = new TreeModelSupport(this);
  }

  /**
   * Getter for property nodeColumns.
   * @return Value of property nodeColumns.
   */
  public int getNodeColumns() {
    return this.nodeColumns;
  }

  /**
   * Setter for property nodeColumns.
   * @param nodeColumns New value of property nodeColumns.
   */
  public void setNodeColumns(int nodeColumns) {
    this.nodeColumns = nodeColumns;
    changeDataStructure();
  }

  /**
   * Gets the path from the root to the specified node.
   *
   * @param aNode
   *            the node to query
   * @return an array of {@code TreeTableNode}s, where
   *         {@code arr[0].equals(getRoot())} and
   *         {@code arr[arr.length - 1].equals(aNode)}, or an empty array if
   *         the node is not found.
   * @throws NullPointerException
   *             if {@code aNode} is {@code null}
   */
  public TreeTableNode[] getPathToRoot(TreeTableNode aNode) {
    List<TreeTableNode> path = new ArrayList<TreeTableNode>();
    TreeTableNode node = aNode;

    while (node != root) {
      path.add(0, node);

      node = (TreeTableNode) node.getParent();
    }

    if (node == root) {
      path.add(0, node);
    }

    return path.toArray(new TreeTableNode[0]);
  }

  private boolean isValidTreeTableNode(Object node) {
    boolean result = false;

    if (node instanceof TreeTableNode) {
      TreeTableNode ttn = (TreeTableNode) node;

      while (!result && ttn != null) {
        result = ttn == root;

        ttn = ttn.getParent();
      }
    }

    return result;
  }


  private class ScheduleUpdateRoot extends Scheduler implements ListDataListener {
    private Runnable updateRoot = new Runnable() {

      @Override
      public void run() {
        EventQueue.invokeLater(new Runnable() {
          @Override
          public void run() {
            updateRoot();
          }
        });
      }
    };

    @Override
    public void intervalAdded(ListDataEvent e) {
      schedule(updateRoot);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
      schedule(updateRoot);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
      schedule(updateRoot);
    }
  }

  private ScheduleUpdateRoot scheduleUpdateRoot = new ScheduleUpdateRoot();
  //private ListDataWeakListener indexChange = new ListDataWeakListener(scheduleUpdateRoot);

  private void changeDataStructure() {
    try {
      for(DbDataSourceIndex index:indices) {
        index.removeListDataListener(scheduleUpdateRoot);
        index.setDataSource(null);
      }
      indices.clear();
      List<String> keys = new ArrayList<String>();
      for (int c = 0; c < Math.min(nodeColumns, columnDescriptors.length); c++) {
        keys.addAll(columnDescriptors[c].getColumnNames());
        DbDataSourceIndex index = new DbDataSourceIndex();
        index.setKeys(keys);
        index.setDataSource(getDataSource());
        index.addListDataListener(scheduleUpdateRoot);
        indices.add(index);
      }
      updateRoot();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't change the tree structure. [" + ex.getMessage() + "]");
    }
  }

  private void updateRoot() {
    if (indices.size() > 0) {
      root = new DbTreeModelNode();
      DbDataSourceIndex rootIndex = indices.get(0);
      for (DbDataSourceIndex.DbIndexKey key : rootIndex.getUniqueKeys()) {
        DbTreeModelNode node = new DbTreeModelNode();
        node.setKey(key);
        node.level = 0;
        updateTreeStructure(node, rootIndex, 1, new HashSet<Integer>());
        root.add(node);
      }
    }
    //System.out.println();
    //System.out.println();
    modelSupport.fireNewRoot();
  }

  public void reload() {
    changeDataStructure();
  }

  private void updateTreeStructure(DbTreeModelNode parent, DbDataSourceIndex parentIndex, int level, Set<Integer> excludeRows) {
    if (level < indices.size()) {
      Set<Integer> rows = parentIndex.findRows(parent.key);

      /*System.out.println();
      for (int i=0;i<level;i++)
      System.out.print("  ");
      System.out.print(parent.key.toString()+":");
      System.out.print(level);
      System.out.print(":");
      for (int row:rows) {
      System.out.print(row);
      System.out.print(" ");
      }
      System.out.println();//*/

      Set<Integer> excluded = new HashSet<Integer>();
      excluded.addAll(excludeRows);
      excluded.add(parent.key.getRow());
      DbDataSourceIndex childIndex = indices.get(level);

      /*for (int i=0;i<level;i++)
      System.out.print("  ");
      System.out.print(parent.key.toString()+":CHILD KEYS:  ");//*/
      for (DbDataSourceIndex.DbIndexKey key : childIndex.getRowKeys(rows)) {
        if (!excluded.contains(key.getRow())) {
          //System.out.print(key.toString()+"  ");

          DbTreeModelNode node = new DbTreeModelNode();
          node.setKey(key);
          node.level = level;
          updateTreeStructure(node, childIndex, level + 1, excluded);
          parent.add(node);
        }
      }
    }
  }

  public void setColumns(String[][] headers) {
    super.setColumns(headers);
    changeDataStructure();
  }

  public void setDataSource(DbDataSource dataSource) {
    super.setDataSource(dataSource);
    changeDataStructure();
  }

  public int getHierarchicalColumn() {
    return 0;
  }

  public Object getValueAt(Object node, int column) {
    Object result = null;
    if ((node instanceof DbTreeModelNode) && (indices.size() > 0)) {
      DbTreeModelNode treeNode = (DbTreeModelNode) node;
      Integer row = indices.get(treeNode.level).findRow(treeNode.key);
      if (row != null) {
        result = super.getValueAt(row.intValue() - 1, column + getNodeColumns());
      }
    }

    return result;
  }

  public boolean isCellEditable(Object node, int column) {
    return false;
  }

  public void setValueAt(Object value, Object node, int column) {
  }

  public Object getRoot() {
    return root;
  }

  /**
   * Returns the child of <code>parent</code> at index <code>index</code>
   * in the parent's
   * child array.  <code>parent</code> must be a node previously obtained
   * from this data source. This should not return <code>null</code>
   * if <code>index</code>
   * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
   * index < getChildCount(parent</code>)).
   *
   * @param   parent  a node in the tree, obtained from this data source
   * @return  the child of <code>parent</code> at index <code>index</code>
   */
  public Object getChild(Object parent, int index) {
    if (!isValidTreeTableNode(parent)) {
      throw new IllegalArgumentException(
              "parent must be a TreeTableNode managed by this model");
    }

    return ((TreeTableNode) parent).getChildAt(index);
  }

  /**
   * Returns the number of children of <code>parent</code>.
   * Returns 0 if the node
   * is a leaf or if it has no children.  <code>parent</code> must be a node
   * previously obtained from this data source.
   *
   * @param   parent  a node in the tree, obtained from this data source
   * @return  the number of children of the node <code>parent</code>
   */
  public int getChildCount(Object parent) {
    if (!isValidTreeTableNode(parent)) {
      throw new IllegalArgumentException(
              "parent must be a TreeTableNode managed by this model");
    }

    return ((TreeTableNode) parent).getChildCount();
  }

  /**
   * Returns <code>true</code> if <code>node</code> is a leaf.
   * It is possible for this method to return <code>false</code>
   * even if <code>node</code> has no children.
   * A directory in a filesystem, for example,
   * may contain no files; the node representing
   * the directory is not a leaf, but it also has no children.
   *
   * @param   node  a node in the tree, obtained from this data source
   * @return  true if <code>node</code> is a leaf
   */
  public boolean isLeaf(Object node) {
    if (!isValidTreeTableNode(node)) {
      throw new IllegalArgumentException(
              "node must be a TreeTableNode managed by this model");
    }

    return ((TreeTableNode) node).isLeaf();
  }

  /**
   * Messaged when the user has altered the value for the item identified
   * by <code>path</code> to <code>newValue</code>.
   * If <code>newValue</code> signifies a truly new value
   * the model should post a <code>treeNodesChanged</code> event.
   *
   * @param path path to the node that the user has altered
   * @param newValue the new value from the TreeCellEditor
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  /**
   * Returns the index of child in parent.  If either <code>parent</code>
   * or <code>child</code> is <code>null</code>, returns -1.
   * If either <code>parent</code> or <code>child</code> don't
   * belong to this tree model, returns -1.
   *
   * @param parent a note in the tree, obtained from this data source
   * @param child the node we are interested in
   * @return the index of the child in the parent, or -1 if either
   *    <code>child</code> or <code>parent</code> are <code>null</code>
   *    or don't belong to this tree model
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (!isValidTreeTableNode(parent)) {
      throw new IllegalArgumentException(
              "parent must be a TreeTableNode managed by this model");
    }

    if (!isValidTreeTableNode(parent)) {
      throw new IllegalArgumentException(
              "child must be a TreeTableNode managed by this model");
    }

    return ((TreeTableNode) parent).getIndex((TreeTableNode) child);
  }

//
//  Change Events
//
  /**
   * Adds a listener for the <code>TreeModelEvent</code>
   * posted after the tree changes.
   *
   * @param   l       the listener to add
   * @see     #removeTreeModelListener
   */
  public void addTreeModelListener(TreeModelListener l) {
    modelSupport.addTreeModelListener(l);
  }

  /**
   * Removes a listener previously added with
   * <code>addTreeModelListener</code>.
   *
   * @see     #addTreeModelListener
   * @param   l       the listener to remove
   */
  public void removeTreeModelListener(TreeModelListener l) {
    modelSupport.removeTreeModelListener(l);
  }

  /**
   * Returns an array of all the <code>TreeModelListener</code>s added
   * to this JXTreeTable with addTreeModelListener().
   *
   * @return all of the <code>TreeModelListener</code>s added or an empty
   *         array if no listeners have been added
   */
  public TreeModelListener[] getTreeModelListeners() {
    return modelSupport.getTreeModelListeners();
  }

  public int getColumnCount() {
    return Math.max(0, super.getColumnCount() - getNodeColumns());
  }

  public String getColumnName(int column) {
    return super.getColumnName(column + getNodeColumns());
  }

  public static class DbTreeModelNode extends AbstractMutableTreeTableNode implements MutableTreeTableNode {

    DbDataSourceIndex.DbIndexKey key;
    int level = 0;

    public void setKey(DbDataSourceIndex.DbIndexKey key) {
      this.key = key;
    }

    public DbDataSourceIndex.DbIndexKey getKey() {
      return key;
    }

    public int getLevel() {
      return level;
    }

    public Object getValueAt(int column) {
      return userObject.toString();
    }

    public int getColumnCount() {
      return 1;
    }

    public int hashCode() {
      return key == null ? super.hashCode() : key.hashCode();
    }

    public boolean equals(Object obj) {
      if ((obj == null) && !(obj instanceof DbTreeModelNode)) {
        return super.equals(obj);
      } else {
        return com.openitech.util.Equals.equals(this.key, ((DbTreeModelNode) obj).key);
      }
    }
  }
}
