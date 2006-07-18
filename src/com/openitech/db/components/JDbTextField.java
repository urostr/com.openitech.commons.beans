/*
 * JDbTextField.java
 *
 * Created on April 2, 2006, 11:35 AM
 *
 * $Revision: 1.1 $
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.DocumentWeakListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author uros
 */
public class JDbTextField extends JTextField implements DocumentListener {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  
 
  /** Creates a new instance of JDbTextField */
  public JDbTextField() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null);
      documentWeakListener = new DocumentWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.getDocument().addDocumentListener(documentWeakListener);
  }
  
  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
  }

  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
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
    documentWeakListener.setEnabled(false);
    try {
      setText(dbFieldObserver.getValueAsText());
    } finally {
      documentWeakListener.setEnabled(true);
    }
  }
  
  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    String tip  = dbFieldObserverToolTip.getValueAsText();
    if (!dbFieldObserverToolTip.wasNull()&&tip.length()>0) {
      this.setToolTipText("Pomo\u010d : "+tip);
    } else
      this.setToolTipText(null);
  }
  
  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      dbFieldObserver.updateValue(this.getText());
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
    } finally {
      activeRowChangeWeakListener.setEnabled(true);
    }
  }
  
  /**
   * Gives notification that a portion of the document has been
   * removed.  The range is given in terms of what the view last
   * saw (that is, before updating sticky positions).
   *
   *
   * @param e the document event
   */
  public void removeUpdate(DocumentEvent e) {
    updateColumn();
  }
  
  /**
   * Gives notification that there was an insert into the document.  The
   * range given by the DocumentEvent bounds the freshly inserted region.
   *
   *
   * @param e the document event
   */
  public void insertUpdate(DocumentEvent e) {
    updateColumn();
  }
  
  /**
   * Gives notification that an attribute or set of attributes changed.
   *
   *
   * @param e the document event
   */
  public void changedUpdate(DocumentEvent e) {
    updateColumn();
  }
}
