/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db;

/**
 *
 * @author uros
 */
public interface FieldObserver extends DataSourceObserver {

  String getColumnName();

  void setColumnName(String columnName);

}
