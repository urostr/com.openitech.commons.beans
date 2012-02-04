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

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author uros
 */
public class ParametersPreprocessor<T, S> implements Preprocessor<T, S>{

  protected final Set<Preprocessor<T, S>> preprocessors = Collections.synchronizedSet(new LinkedHashSet<Preprocessor<T, S>>());

  /**
   * Get the value of preprocessors
   *
   * @return the value of preprocessors
   */
  public Set<Preprocessor<T, S>> getPreprocessors() {
    return getPreprocessors(true);
  }

  protected void init() {
  }


  public Set<Preprocessor<T, S>> getPreprocessors(boolean init) {
    if (init) {
      init();
    }
    return preprocessors;
  }

  @Override
  public T preprocess(T object, S parameter) throws SQLException {
    T result = object;
    for (Preprocessor<T, S> preprocessor : getPreprocessors()) {
      result = preprocessor.preprocess(result, parameter);
    }

    return result;
  }
}
