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

import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author uros
 */
public class DbDataSourceParameterProxy extends DbDataSource.SubstSqlParameter {

  private SubstSqlParameter implementation;
  private PropertyChangeListener valueChangeListener = new PropertyChangeListener() {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
  };

  public DbDataSourceParameterProxy(String replace) {
    super(replace);
  }

  /**
   * Get the value of implementation
   *
   * @return the value of implementation
   */
  public SubstSqlParameter getImplementation() {
    return implementation;
  }

  /**
   * Set the value of implementation
   *
   * @param implementation new value of implementation
   */
  public void setImplementation(SubstSqlParameter implementation) {
    if (this.implementation != null) {
      this.implementation.removePropertyChangeListener(valueChangeListener);
    }
    this.implementation = implementation;
    if (this.implementation != null) {
      this.implementation.addPropertyChangeListener("query", valueChangeListener);
    }
  }

  @Override
  public Object getParameter(int index) {
    if (implementation == null) {
      return super.getParameter(index);
    } else {
      return implementation.getParameter(index);
    }
  }

  @Override
  public List<Object> getParameters() {
    if (implementation == null) {
      return super.getParameters();
    } else {
      return implementation.getParameters();
    }
  }

  @Override
  public String getValue() {
    if (implementation == null) {
      return super.getValue();
    } else {
      return implementation.getValue();
    }
  }

  @Override
  public void setValue(String value) {
    setValue(implementation, value);
  }

  private void setValue(SubstSqlParameter implementation, Object value) {
    final String svalue = value == null ? null : value.toString();
    if (implementation == null) {
      super.setValue(svalue);
    } else {
      implementation.setValue(svalue);
    }
  }
}
