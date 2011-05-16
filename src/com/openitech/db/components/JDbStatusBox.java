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
import javax.swing.JCheckBox;

/**
 *
 * @author uros
 */
public class JDbStatusBox extends JCheckBox implements FieldObserver {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  /** Creates a new instance of JDbStatusBox */
  public JDbStatusBox() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
  }
  
  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
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

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    super.setSelected(dbFieldObserver.isNotEmptyValue());
  }
  
  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    boolean tip  = dbFieldObserverToolTip.isNotEmptyValue();
    if (!dbFieldObserverToolTip.wasNull()) {
      this.setToolTipText(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("HELP")+(tip?"izbrano":"prazno"));
    } else
      this.setToolTipText(null);
  }
  
  public void setSelected(boolean b) {
    //ignore event
  }
  
}
