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
 * JDbDateTextField.java
 *
 * Created on Torek, 13 marec 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.db.model.DbDataSource;
import com.openitech.text.FormatFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class JDbIntegerTextField extends JDbFormattedTextField {

  private boolean autosize = true;
  private int length = 0;

  /**
   * Creates a new instance of JDbIntegerTextField
   */
  public JDbIntegerTextField() {
    super();
    setFormat(FormatFactory.getIntegerNumberFormat(0));
  }

  /**
   * Creates a new instance of JDbIntegerTextField
   */
  public JDbIntegerTextField(boolean autosize) {
    super();
    this.autosize = autosize;
    updateFormat();
  }

  /**
   * Creates a new instance of JDbIntegerTextField
   */
  public JDbIntegerTextField(int length, boolean autosize) {
    super();
    this.autosize = autosize;
    this.length = length;
    updateFormat();
  }

  public void setAutosize(boolean autosize) {
    this.autosize = autosize;
  }

  public boolean isAutosize() {
    return autosize;
  }

  protected void updateFormat() {
    setFormat(FormatFactory.getIntegerNumberFormat(length));
    if (isAutosize()) {
      setColumns(length + 1);
    }
  }

  public void setLength(int length) {
    this.length = length;
    updateFormat();
  }

  public int getLength() {
    return length;
  }
  public DbDataSource dataSourceForUpdates;

  /**
   * Get the value of dataSourceForUpdates
   *
   * @return the value of dataSourceForUpdates
   */
  public DbDataSource getDataSourceForUpdates() {
    return dataSourceForUpdates;
  }

  /**
   * Set the value of dataSourceForUpdates
   *
   * @param dataSourceForUpdates new value of dataSourceForUpdates
   */
  public void setDataSourceForUpdates(DbDataSource dataSourceForUpdates) {
    this.dataSourceForUpdates = dataSourceForUpdates;
    if (this.dataSourceForUpdates != null) {
      this.dataSourceForUpdates.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            dataSource_actionPerformed(e);
          } catch (ParseException ex) {
            Logger.getLogger(JDbIntegerTextField.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex);
          }
        }
      });
    }
  }
}
