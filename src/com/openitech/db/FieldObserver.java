/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db;

import com.openitech.db.model.*;

/**
 *
 * @author uros
 */
public interface FieldObserver {

  String getColumnName();

  DbDataSource getDataSource();

  void setColumnName(String columnName);

  void setDataSource(DbDataSource dataSource);

}
