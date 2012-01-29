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
 * JDbCheckBox.java
 *
 * Created on April 2, 2006, 11:38 AM
 *
 * $Revision $
 */

package com.openitech.db.components;

import javax.swing.DefaultButtonModel;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;

/**
 *
 * @author uros
 */
public class JDbCheckValueBox extends JCheckBox implements ActionListener, FieldObserver {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private String checkedValue=java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("SELECTED_CHECK_VALUE");
  private String uncheckedValue=null;

  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  /** Creates a new instance of JDbCheckBox */
  public JDbCheckValueBox() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null); //NOI18N
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null); //NOI18N
      actionWeakListener = new ActionWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addActionListener(actionWeakListener);
  }

  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
  }

  public void setCheckedValue(String checkedValue) {
    this.checkedValue = checkedValue;
  }

  public String getCheckedValue() {
    return checkedValue;
  }

  public void setUncheckedValue(String uncheckedValue) {
    this.uncheckedValue = uncheckedValue;
  }

  public String getUncheckedValue() {
    return uncheckedValue;
  }

  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
  }

  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }

  public void setColumnName(String columnName) {
    dbFieldObserver.setColumnName(columnName);
  }

  public String getColumnName() {
    return dbFieldObserver.getColumnName();
  }

  public void setToolTipColumnName(String columnName) {
    dbFieldObserverToolTip.setColumnName(columnName);
  }

  public String getToolTipColumnName() {
    return dbFieldObserverToolTip.getColumnName();
  }

  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  public Validator getValidator() {
    return validator;
  }

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    this.actionWeakListener.setEnabled(false);
    try {
      setSelected(checkedValue.equalsIgnoreCase(dbFieldObserver.getValueAsText()));
    } finally {
      this.actionWeakListener.setEnabled(true);
    }
  }

  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    boolean tip  = dbFieldObserverToolTip.getValueAsText().equalsIgnoreCase(checkedValue);
    if (!dbFieldObserverToolTip.wasNull()) {
      this.setToolTipText(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("HELP")+(tip?(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("SELECTED")+(checkedValue!=null?" ["+checkedValue+"]":"")):(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("EMPTY")+(uncheckedValue!=null?" ["+uncheckedValue+"]":""))));
    } else
      this.setToolTipText(null);
  }

  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      if ((validator==null)||(validator!=null&&validator.isValid(this.isSelected()))) {
        if ((getModel() instanceof DefaultButtonModel)&&
           (((DefaultButtonModel) getModel()).getGroup()==null)) {
          dbFieldObserver.updateValue(this.isSelected()?checkedValue:uncheckedValue);
        } else {
          if (this.isSelected()) {
            dbFieldObserver.updateValue(checkedValue);
          }
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't update the value in the dataSource.", ex); //NOI18N
    } finally {
      activeRowChangeWeakListener.setEnabled(true);
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(ActionEvent e) {
    updateColumn();
  }

}
