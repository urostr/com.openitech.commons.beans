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
