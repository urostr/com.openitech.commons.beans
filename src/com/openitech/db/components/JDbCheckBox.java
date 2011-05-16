/*
 * JDbCheckBox.java
 *
 * Created on April 2, 2006, 11:38 AM
 *
 * $Revision $
 */

package com.openitech.db.components;

import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;

/**
 *
 * @author uros
 */
public class JDbCheckBox extends JCheckBox implements ActionListener, FieldObserver {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  /** Creates a new instance of JDbCheckBox */
  public JDbCheckBox() {
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
  
  @Override
  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
  }
  
  @Override
  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }
  
  @Override
  public void setColumnName(String columnName) {
    dbFieldObserver.setColumnName(columnName);
  }
  
  @Override
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
      setSelected(dbFieldObserver.getValueAsBoolean());
    } finally {
      this.actionWeakListener.setEnabled(true);
    }
  }
  
  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    boolean tip  = dbFieldObserverToolTip.getValueAsBoolean();
    if (!dbFieldObserverToolTip.wasNull()) {
      this.setToolTipText(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("HELP")+(tip?java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("SELECTED"):java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("EMPTY")));
    } else
      this.setToolTipText(null);
  }
  
  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      if ((validator==null)||(validator!=null&&validator.isValid(this.isSelected())))
        dbFieldObserver.updateValue(this.isSelected());
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
