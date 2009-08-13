/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db;

import com.openitech.db.model.DbDataSource;

/**
 *
 * @author uros
 */
public interface DataSourceObserver {

  DbDataSource getDataSource();

  void setDataSource(DbDataSource dataSource);

}
