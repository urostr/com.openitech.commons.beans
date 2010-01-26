/*
 * DataSourceConfig.java
 *
 * Created on Sobota, 29 april 2006, 9:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.model.xml;

import com.openitech.CaseInsensitiveString;
import com.openitech.db.model.DbDataModel;
import java.util.Collections;
import javax.swing.text.Document;

/**
 *
 * @author uros
 */
public class DataSourceConfig {
  private final static java.util.Map<CaseInsensitiveString, Document> documents = Collections.synchronizedMap(new java.util.HashMap<CaseInsensitiveString, Document>());

  public DataSourceConfig(DbDataModel dataModel) {
    this.dataModel = dataModel;
  }


  private DbDataModel dataModel;

  /**
   * Get the value of dataModel
   *
   * @return the value of dataModel
   */
  public DbDataModel getDataModel() {
    return dataModel;
  }

  /**
   * Set the value of dataModel
   *
   * @param dataModel new value of dataModel
   */
  public void setDataModel(DbDataModel dataModel) {
    this.dataModel = dataModel;
  }
  
  public static Document get(String documentName) {
    return get(documentName, new javax.swing.text.PlainDocument());
  }

  public static Document get(String documentName, Document document) {
    CaseInsensitiveString ci = CaseInsensitiveString.valueOf(documentName);
    Document result = null;
    if (documents.containsKey(ci)) {
      result = documents.get(ci);
    } else {
      documents.put(ci, document);
      result = document;
    }

    return result;
  }

}
