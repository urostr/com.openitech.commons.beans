/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.sql.Connection;
import java.util.List;

/**
 *
 * @author uros
 */
public class DbDataSourceParametersPreprocessor extends ParametersPreprocessor<List<?>, Connection> {
  private static DbDataSourceParametersPreprocessor instance = null;

  private DbDataSourceParametersPreprocessor() {
  }


  public static DbDataSourceParametersPreprocessor getInstance() {
    if (instance==null) {
      instance = new DbDataSourceParametersPreprocessor();
    }

    return instance;
  }

  private boolean initialized = false;

  @Override
  public void init() {
    if (!initialized) {
      try {
        Class.forName("com.openitech.i18n.TranslationPreprocessor");
      } catch (ClassNotFoundException ex) {
        //ignore it;
      }
      try {
        Class.forName("com.openitech.db.model.sql.TemporaryParameterPreprocessor");
      } catch (ClassNotFoundException ex) {
        //ignore it;
      }
      initialized = true;
    }
  }
}
