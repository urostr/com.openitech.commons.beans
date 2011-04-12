/*
 * DbNavigatorDataSourceIndex.java
 *
 * Created on Sobota, 12 januar 2008, 12:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.sql.SQLException;
import java.util.Set;
import javax.swing.event.ListDataListener;

/**
 *
 * @author uros
 */
public interface DbNavigatorDataSourceIndex<T extends DbNavigatorDataSource> {
  
  public void setDataSource(T dataSource)  throws SQLException;
  public T getDataSource();
  
  public void setKeys(String... columns)  throws SQLException;
  
  public String[] getKeys();
  
  /**
   * Finds the indexed row
   * @param values 
   * @return 
   */
  public Set<Integer> findRows(Object... values);

  public void removeListDataListener(ListDataListener l);
  public void addListDataListener(ListDataListener l);

}
