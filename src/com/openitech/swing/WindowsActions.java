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
package com.openitech.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 *
 * @author uros
 */
public class WindowsActions {

  private WindowsActions() {
  }

  public static void addActions(final JTextComponent component) {
    Action copyAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.copy();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine("Copy Pressed");
      }
    };
    Action pasteAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.paste();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine("Paste Pressed");
      }
    };
    Action cutAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        component.cut();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine("Cut Pressed");
      }
    };
    KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
    KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
    KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
    component.getInputMap().put(ctrlC, copyAction);
    component.getInputMap().put(ctrlV, pasteAction);
    component.getInputMap().put(ctrlX, cutAction);
  }
}
