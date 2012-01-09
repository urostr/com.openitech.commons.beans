/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.db.model.factory.ClassInstanceFactory;
import com.openitech.db.model.factory.SourceColumnFactory;
import com.openitech.db.model.xml.config.Importer.Destination.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author domenbasic
 */
public class SourceColumn {

  private String columnName;
  private Integer columnIndex;
  private String factoryClassName;
  private SourceColumnFactory factory;

  public SourceColumn(Column.SourceColumn sourceColumn) {
    this.columnName = sourceColumn.getName();
    this.columnIndex = sourceColumn.getIndex();
    if (sourceColumn.getFactory() != null && sourceColumn.getFactory().getClassName() != null) {
      this.factoryClassName = sourceColumn.getFactory().getClassName();
      try {
        this.factory = (SourceColumnFactory) ClassInstanceFactory.getInstance(factoryClassName).newInstance();
      } catch (Exception ex) {
        Logger.getLogger(SourceColumn.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  }

  public Integer getColumnIndex() {
    return columnIndex;
  }

  public String getColumnName() {
    return columnName;
  }

  public SourceColumnFactory getFactory() {
    return factory;
  }
}
