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
