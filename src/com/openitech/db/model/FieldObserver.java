/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

import com.openitech.db.model.DataSourceObserver;

/**
 *
 * @author uros
 */
public interface FieldObserver extends DataSourceObserver {

  String getColumnName();

  void setColumnName(String columnName);

}
