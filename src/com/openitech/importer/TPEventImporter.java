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
 * TPUpravljanjeSifrantovVnos.java
 *
 * Created on Sreda, 18 februar 2008, 9:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.importer;

import com.openitech.db.components.JDbTable;
import com.openitech.swing.framework.context.MergeableActions;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.action.BoundAction;

/**
 *
 * @author DomenBasic
 */
public class TPEventImporter extends JXTaskPane implements MergeableActions, ActionListener {

  List<Action> actionList = new ArrayList<Action>();
  private BoundAction baPregledSifrantov = new BoundAction();
  private  Window windowImporter;
  private final java.awt.Frame parent;
  private final boolean modal;
  private final JDbTable associatedTable;

  public TPEventImporter(java.awt.Frame parent, boolean modal, JImporter jImportEventsModel, JDbTable associatedTable) {
    this.parent = parent;
    this.modal = modal;
    this.associatedTable = associatedTable;

    String title = jImportEventsModel.getTitle();
    if(title == null || title.equals("")){
      title = "Importer";
    }
    baPregledSifrantov.setEnabled(true);
    baPregledSifrantov.setLongDescription(title);

    baPregledSifrantov.setName(title);
    baPregledSifrantov.setActionCommand(title);

    baPregledSifrantov.addActionListener(this);
    actionList.add(baPregledSifrantov);

    add(baPregledSifrantov);

  }

  @Override
  public List<Action> getMergeableActions() {
    return actionList;
  }

  public void setImporter(Window importer) {
    this.windowImporter = importer;
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    if (windowImporter != null) {
      if (!windowImporter.isVisible()) {
        windowImporter.setVisible(true);
      }
      windowImporter.toFront();
    }
  }
}
