/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openitech.db.model;

import java.sql.SQLException;

/**
 *
 * @author uros
 */
public interface Preprocessor<T,S> {
  public T preprocess(T object, S parameter) throws SQLException ;
}
