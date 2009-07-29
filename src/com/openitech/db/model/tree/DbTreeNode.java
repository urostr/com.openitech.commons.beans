package com.openitech.db.model.tree;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class DbTreeNode extends DefaultMutableTreeNode {

  protected DbTreeNodeType type;
  protected java.util.List<Object> key;

  public DbTreeNode(DbTreeNodeType type, List<Object> key) {
    super(type==null?"NULL":type.getName(), true);
    this.type = type;
    this.key = key == null ? new java.util.ArrayList<Object>(): key;
  }

  /**
   * Get the value of key
   *
   * @return the value of key
   */
  public java.util.List<Object> getKey() {
    return key;
  }

  /**
   * Get the value of type
   *
   * @return the value of type
   */
  public DbTreeNodeType getType() {
    return type;
  }
}
