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
 * AutoCompleteComboBoxModelAdaptor.java
 *
 * Created on Petek, 29 avgust 2008, 13:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.swing.autocomplete;

import javax.swing.ComboBoxModel;
import javax.swing.text.JTextComponent;

/**
 *
 * @author uros
 */
public class AutoCompleteComboBoxModelAdaptor extends AbstractAutoCompleteAdaptor {
  
    /** */
  /** Creates a new instance of AutoCompleteComboBoxModelAdaptor */
  public AutoCompleteComboBoxModelAdaptor() {
  }
  
  public AutoCompleteComboBoxModelAdaptor(JTextComponent textComponent, ComboBoxModel model) {
    this.textComponent = textComponent;
    this.model = model;
  }

  public Object getSelectedItem() {
    return this.model.getSelectedItem();
  }

  public void setSelectedItem(Object item) {
    if (this.textComponent instanceof AutoCompleteTextComponent) {
      ((AutoCompleteTextComponent) this.textComponent).setSelectedItem(item);
    } else {
      this.model.setSelectedItem(item);
    }
  }

  public int getItemCount() {
    return this.model.getSize();
  }

  public Object getItem(int index) {
    return this.model.getElementAt(index);
  }

  /**
   * Holds value of the text component that is used for automatic completion.
   */
  private JTextComponent textComponent;

  /**
   * Getter for property textComponent.
   * @return Value of property textComponent.
   */
  public JTextComponent getTextComponent() {
    return this.textComponent;
  }

  /**
   * Setter for property textComponent.
   * @param textComponent New value of property textComponent.
   */
  public void setTextComponent(JTextComponent textComponent) {
    this.textComponent = textComponent;
  }

  /**
   * Holds value of property model.
   */
  private ComboBoxModel model;

  /**
   * Getter for property model.
   * @return Value of property model.
   */
  public ComboBoxModel getModel() {
    return this.model;
  }

  /**
   * Setter for property model.
   * @param model New value of property model.
   */
  public void setModel(ComboBoxModel model) {
    this.model = model;
  }

}
