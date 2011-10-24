/*
 * JDbTextArea.java
 *
 * Created on April 2, 2006, 11:38 AM
 *
 * $Revision $
 */
package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.swing.WindowsActions;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.DocumentWeakListener;
import com.openitech.ref.events.FocusWeakListener;
import com.openitech.util.Equals;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 *
 * @author uros
 */
public class JDbTextArea extends JTextArea implements DocumentListener, FieldObserver {

  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private final Selector selector = new Selector(this);
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  private transient FocusWeakListener focusWeakListener;

  /** Creates a new instance of JDbTextArea */
  public JDbTextArea() {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_toolTipFieldValueChanged", null);
      focusWeakListener = new FocusWeakListener(this, "this_focusGained", null);
      documentWeakListener = new DocumentWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addFocusListener(focusWeakListener);
    this.getDocument().addDocumentListener(documentWeakListener);

    WindowsActions.addActions(this);
  }

  public void this_focusGained(FocusEvent e) {
    if (!ignoreSelectAll) {
      EventQueue.invokeLater(selector);
    }
  }
  private boolean ignoreSelectAll = false;

  /**
   * Get the value of ignoreSelectAll
   *
   * @return the value of ignoreSelectAll
   */
  public boolean isIgnoreSelectAll() {
    return ignoreSelectAll;
  }

  /**
   * Set the value of ignoreSelectAll
   *
   * @param ignoreSelectAll new value of ignoreSelectAll
   */
  public void setIgnoreSelectAll(boolean ignoreSelectAll) {
    this.ignoreSelectAll = ignoreSelectAll;
  }

  public void setDataSource(DbDataSource dataSource) {
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
  }

  public DbDataSource getDataSource() {
    return dbFieldObserver.getDataSource();
  }

  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
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
    documentWeakListener.setEnabled(false);
    try {
      setText(dbFieldObserver.getValueAsText());
    } finally {
      documentWeakListener.setEnabled(true);
    }
  }

  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    String tip = dbFieldObserverToolTip.getValueAsText();
    if (!dbFieldObserverToolTip.wasNull() && tip.length() > 0) {
      this.setToolTipText(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("HELP") + tip);
    } else {
      this.setToolTipText(null);
    }
  }

  private void updateColumn() {
    if (!Equals.equals(dbFieldObserver.getValueAsText(), this.getText())) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        if ((validator == null) || (validator != null && validator.isValid(this.getText()))) {
          dbFieldObserver.updateValue(this.getText());
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

  /**
   * Associates the editor with a text document.
   * The currently registered factory is used to build a view for
   * the document, which gets displayed by the editor after revalidation.
   * A PropertyChange event ("document") is propagated to each listener.
   * 
   * 
   * @param doc  the document to display/edit
   * @see #getDocument
   * @beaninfo description: the text document model
   *        bound: true
   *       expert: true
   */
  public void setDocument(Document doc) {
    if (getDocument() != null && documentWeakListener != null) {
      getDocument().removeDocumentListener(documentWeakListener);
    }
    super.setDocument(doc);
    if (getDocument() != null && documentWeakListener != null) {
      getDocument().addDocumentListener(documentWeakListener);
    }
  }
}
