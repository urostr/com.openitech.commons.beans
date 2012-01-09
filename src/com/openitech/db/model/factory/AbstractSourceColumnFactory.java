/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.importer.DataColumn;

/**
 *
 * @author domenbasic
 */
public abstract class AbstractSourceColumnFactory implements SourceColumnFactory {

  @Override
  public DataColumn getColumnValue(SourceColumnFactoryParameter parameter) {
    if (parameter.getRow() != null) {
      return getColumnValue(parameter.getColumn(), parameter.getRow().toArray(new DataColumn[parameter.getRow().size()]));
    } else {
      return getColumnValue(parameter.getColumn());
    }
  }
}
