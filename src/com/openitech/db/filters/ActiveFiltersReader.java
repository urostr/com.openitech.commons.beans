/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.filters;

import javax.swing.JMenu;

/**
 *
 * @author uros
 */
public interface ActiveFiltersReader {

  public DataSourceFilters getActiveFilter();
  public JMenu getFilterMenuItem();
}
