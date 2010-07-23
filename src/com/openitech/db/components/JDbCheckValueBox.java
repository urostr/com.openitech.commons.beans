/*
 * JDbCheckBox.java
 *
 * Created on April 2, 2006, 11:38 AM
 *
 * $Revision $
 */

package com.openitech.db.components;

import javax.swing.DefaultButtonModel;
import com.openitech.Settings;
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
  private String checkedValue="D";
  private String uncheckedValue=null;

  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  /** Creates a new instance of JDbCheckBox */
  public JDbCheckValueBox() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null);
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
      this.setToolTipText("Pomo\u010d : "+(tip?("izbrano"+(checkedValue!=null?" ["+checkedValue+"]":"")):("prazno"+(uncheckedValue!=null?" ["+uncheckedValue+"]":""))));
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
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
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
