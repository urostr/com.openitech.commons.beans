/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import com.openitech.db.model.factory.DataSourceConfig;

/**
 *
 * @author domenbasic
 */
public interface DataSourceConfigObserver<T extends DataSourceConfig> {

  /**
   * Get the value of config
   *
   * @return the value of config
   */
  public T getConfig();

  /**
   * Set the value of config
   *
   * @param config new value of config
   */
  public void setConfig(T config);
}
