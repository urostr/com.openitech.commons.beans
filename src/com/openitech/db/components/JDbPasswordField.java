/*
 * JDbPasswordField.java
 *
 * Created on Nedelja, 16 julij 2006, 13:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.crypto.Encoder;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.DocumentWeakListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author uros
 */
public class JDbPasswordField extends JPasswordField implements DocumentListener, FieldObserver {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  
  private boolean encrypted = true;
  private boolean savePassword = true;
  
  
  /** Creates a new instance of JDbTextField */
  public JDbPasswordField() {
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

  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  public Validator getValidator() {
    return validator;
  }

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    if (isSavePassword()) {
      documentWeakListener.setEnabled(false);
      try {
        if (isEncrypted())
          setText(new String(Encoder.decrypt(dbFieldObserver.getValueAsByteArray())));
        else
          setText(dbFieldObserver.getValueAsText());
      } finally {
        documentWeakListener.setEnabled(true);
      }
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
    if (isSavePassword()) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if ((validator==null)||(validator!=null&&validator.isValid(new String(this.getPassword())))) {
          if (isEncrypted())
            dbFieldObserver.updateValue(Encoder.encrypt( (new String(this.getPassword())).getBytes()));
          else
            dbFieldObserver.updateValue(new String(this.getPassword()));
        }
      } catch (SQLException ex) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
      } finally {
        activeRowChangeWeakListener.setEnabled(true);
      }
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
  
  public void setEncrypted(boolean encrypted) {
    this.encrypted = encrypted;
  }
  
  public boolean isEncrypted() {
    return encrypted;
  }
  
  public void setSavePassword(boolean savePassword) {
    this.savePassword = savePassword;
  }
  
  public boolean isSavePassword() {
    return savePassword;
  }
}
