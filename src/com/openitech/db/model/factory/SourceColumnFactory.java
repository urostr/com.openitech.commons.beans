/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.importer.DataColumn;

/**
 *
 * @author uros
 */
public interface SourceColumnFactory {
  public DataColumn getColumnValue(DataColumn... columns);  
}
