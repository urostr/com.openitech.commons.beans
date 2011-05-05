/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.swing.framework.context;

import javax.swing.JTable;

/**
 *
 * @author uros
 */
public interface AssociatedTable<T extends JTable> {
  public T getAssociatedTable();
  public void setAssociatedTable(T table);
}
