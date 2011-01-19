/*
 * DataSourceConfig.java
 *
 * Created on Sobota, 29 april 2006, 9:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.text.CaseInsensitiveString;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import java.util.Collections;
import javax.swing.text.Document;

/**
 *
 * @author uros
 */
public class DataSourceConfig<T extends DbDataModel> {

  private final static java.util.Map<CaseInsensitiveString, Document> documents = Collections.synchronizedMap(new java.util.HashMap<CaseInsensitiveString, Document>());

  public DataSourceConfig(T dataModel) {
    this.dataModel = dataModel;
  }
  private T dataModel;

  /**
   * Get the value of dataModel
   *
   * @return the value of dataModel
   */
  public T getDataModel() {
    return dataModel;
  }

  /**
   * Set the value of dataModel
   *
   * @param dataModel new value of dataModel
   */
  public void setDataModel(T dataModel) {
    this.dataModel = dataModel;
  }

  public DbDataSource rppDataSource;

  /**
   * Get the value of rppDataSource
   *
   * @return the value of rppDataSource
   */
  public DbDataSource getRppDataSource() {
    return rppDataSource;
  }

  /**
   * Set the value of rppDataSource
   *
   * @param rppDataSource new value of rppDataSource
   */
  public void setRppDataSource(DbDataSource rppDataSource) {
    this.rppDataSource = rppDataSource;
  }

  public static Document get(String documentName) {
    return get(documentName, null);
  }

  public static Document get(String documentName, Document document) {
    CaseInsensitiveString ci = CaseInsensitiveString.valueOf(documentName);
    Document result = null;
    if (documents.containsKey(ci)) {
      result = documents.get(ci);
    } else {
      if (document==null) {
        document = new javax.swing.text.PlainDocument();
      }
      documents.put(ci, document);
      result = document;
    }

    return result;
  }
}
