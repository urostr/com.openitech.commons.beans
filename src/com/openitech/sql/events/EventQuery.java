/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.sql.events;

import com.openitech.db.model.DbDataSource.SqlParameter;
import com.openitech.sql.Field;
import java.util.List;
import java.util.Map;

/**
 *
 * @author uros
 */
public interface EventQuery {

  /**
   * Get the value of parameters
   *
   * @return the value of parameters
   */
  public List<Object> getParameters();

  /**
   * Get the value of query
   *
   * @return the value of query
   */
  public String getQuery();

  /**
   * Get the value of namedParameters
   *
   * @return the value of namedParameters
   */
  public Map<Field, SqlParameter<Object>> getNamedParameters();
}