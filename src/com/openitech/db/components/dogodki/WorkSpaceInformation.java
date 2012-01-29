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
package com.openitech.db.components.dogodki;

import com.openitech.db.model.xml.config.Workarea.WorkSpaceInformation.Panels;
import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author domenbasic
 */
public class WorkSpaceInformation extends JPanel{

 private final Panels panel;
 private final Component component;

  public WorkSpaceInformation(Panels panel, Component component) {
    this.panel = panel;
    this.component = component;
  }

 public WorkSpaceInformation(Component component) {
    this(null, component);
  }

  public WorkSpaceInformation(Panels panel) {
    this(panel, null);
  }

  public Component getComponent() {
    return component;
  }


  public Panels getPanel() {
    return panel;
  }


}
