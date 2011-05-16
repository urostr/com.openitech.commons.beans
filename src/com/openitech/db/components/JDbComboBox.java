/*
 * JDbComboBox.java
 *
 * Created on April 5, 2006, 10:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.swing.autocomplete.AutoCompleteDocument;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import com.openitech.util.Equals;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.event.ListDataEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author tomaz
 */
public class JDbComboBox extends JComboBox implements FieldObserver {

  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  private Document document = null;
  private final boolean decorate;

  /** Creates a new instance of JDbComboBox */
  public JDbComboBox() {
    this(false);
  }

  public JDbComboBox(boolean editable) {
    this(editable, true);
  }

  public JDbComboBox(boolean editable, boolean decorate) {
    super();
    this.decorate = decorate;
    setEditable(editable);
    init(decorate);
  }

  private void init(boolean decorate) {
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", null); //NOI18N
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_toolTipFieldValueChanged", null); //NOI18N
      actionWeakListener = new ActionWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addActionListener(actionWeakListener);
    if (decorate) {
      com.openitech.swing.autocomplete.AutoCompleteDecorator.decorate(this);

      document = ((JTextComponent) getEditor().getEditorComponent()).getDocument();
      if (document instanceof AutoCompleteDocument) {
        ((AutoCompleteDocument) document).setAutoComplete(false);
      }
    }
  }

  @Override
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
    if (document instanceof AutoCompleteDocument) {
      try {
        ((AutoCompleteDocument) document).setAutoComplete(true);
        return super.processKeyBinding(ks, e, condition, pressed);
      } finally {
        ((AutoCompleteDocument) document).setAutoComplete(false);
      }
    } else {
      return super.processKeyBinding(ks, e, condition, pressed);
    }
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
    actionWeakListener.setEnabled(false);
    try {
      if (getModel() instanceof DbComboBoxModel) {
        this.setSelectedItem(new DbComboBoxModel.DbComboBoxEntry<Object, Object>(dbFieldObserver.getValue(), null, null));
      } else {
        if (getModel().getSize() > 0) {
          final int valueAsInt = dbFieldObserver.getValueAsInt();
          if ((valueAsInt >= 0) && (valueAsInt < getModel().getSize())) {
            this.setSelectedIndex(valueAsInt);
          } else if (valueAsInt == Integer.MIN_VALUE) {
            this.setSelectedItem(null);
          }
        }
      }
      if (getEditor() != null) {
        getEditor().setItem(getSelectedItem());
      }
    } finally {
      actionWeakListener.setEnabled(true);
    }
    repaint(27);
  }

  @Override
  protected void fireActionEvent() {
    if (actionWeakListener.isEnabled()) {
      super.fireActionEvent();
    }
  }

  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    if (this.dbFieldObserverToolTip.getColumnName() != null) {
      int tip = dbFieldObserverToolTip.getValueAsInt();
      if (!dbFieldObserverToolTip.wasNull() && tip < this.getModel().getSize()) {
        this.setToolTipText(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("HELP") + this.getModel().getElementAt(tip));
      } else {
        this.setToolTipText(null);
      }
    } else {
      this.setToolTipText(null);
    }
  }

  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      if (getModel() instanceof DbComboBoxModel) {
        if (!((DbComboBoxModel) getModel()).isUpdatingEntries()) {
          if ((this.getSelectedItem() != null) && (this.getSelectedItem() instanceof DbComboBoxModel.DbComboBoxEntry)) {
            Object value = (((DbComboBoxModel.DbComboBoxEntry) this.getSelectedItem()).getKey());
            if (!Equals.equals(value, dbFieldObserver.getValue())) {
              if ((validator == null) || (validator != null && validator.isValid(value))) {
                dbFieldObserver.updateValue(value);
              }
            }
          }
        }
      } else {
        int value = this.getSelectedIndex();
        if (!Equals.equals(value, dbFieldObserver.getValueAsInt())) {
          if ((validator == null) || (validator != null && validator.isValid(value))) {
            dbFieldObserver.updateValue(value);
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
  @Override
  public void actionPerformed(ActionEvent e) {
    updateColumn();
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    boolean enabled = actionWeakListener.isEnabled();
    actionWeakListener.setEnabled(false);
    try {
      if (!(getSelectedItem() instanceof DbComboBoxModel.DbComboBoxEntry)) {
        //final Object oldValue = getSelectedItem();
        super.contentsChanged(e);
        //setSelectedItem(oldValue);
        //((JTextComponent) this.getEditor().getEditorComponent()).setText(oldValue.toString());
      } else {
        super.contentsChanged(e);
        dataSource_fieldValueChanged(null);
      }
    } finally {
      actionWeakListener.setEnabled(enabled);
    }
  }

  @Override
  public void setModel(ComboBoxModel aModel) {
    if (actionWeakListener != null) {
      actionWeakListener.setEnabled(false);
    }
    try {
      super.setModel(aModel);
      if (decorate && (aModel instanceof DbComboBoxModel)) {
        com.openitech.swing.autocomplete.AutoCompleteDecorator.decorate(this);
      }
    } finally {
      if (actionWeakListener != null) {
        actionWeakListener.setEnabled(true);
      }
    }
    if (dbFieldObserver != null) {
      dataSource_fieldValueChanged(null);
    }
  }
}
