/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
