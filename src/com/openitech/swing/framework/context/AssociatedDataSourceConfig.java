/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.swing.framework.context;

import com.openitech.db.model.factory.DataSourceConfig;

/**
 *
 * @author uros
 */
public interface AssociatedDataSourceConfig {
  public DataSourceConfig getAssociatedDataSourceConfig();
  public void setAssociatedDataSourceConfig(DataSourceConfig config);
}
