/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.importer.DataColumn;
import com.openitech.value.fields.FieldValue;

/**
 *
 * @author uros
 */
public interface SourceColumnFactory {
  public FieldValue getFieldValue(DataColumn... columns);  
}
