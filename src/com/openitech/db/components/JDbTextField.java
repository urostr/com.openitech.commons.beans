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
 * JDbTextField.java
 *
 * Created on April 2, 2006, 11:35 AM
 *
 * $Revision: 1.15 $
 */
package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.swing.autocomplete.AutoCompleteDecorator;
import com.openitech.swing.autocomplete.AutoCompleteDocument;
import com.openitech.swing.autocomplete.AutoCompleteTextComponent;
import com.openitech.swing.WindowsActions;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import com.openitech.ref.events.DocumentWeakListener;
import com.openitech.ref.events.FocusWeakListener;
import com.openitech.util.Equals;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Document;

/**
 *
 * @author uros
 */
public class JDbTextField extends JTextField implements DocumentListener, ListDataListener, AutoCompleteTextComponent, FieldObserver {

  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private final Selector selector = new Selector(this);
  private transient ActionWeakListener actionWeakListener;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  private transient FocusWeakListener focusWeakListener;
  private java.awt.Color c_default_bg;

  /** Creates a new instance of JDbTextField */
  public JDbTextField() {
    try {
      actionWeakListener = new ActionWeakListener(this, "dataSource_actionPerformed");
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_toolTipFieldValueChanged", null);
      focusWeakListener = new FocusWeakListener(this, "this_focusGained", "this_focusLost");
      documentWeakListener = new DocumentWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addFocusListener(focusWeakListener);
    this.getDocument().addDocumentListener(documentWeakListener);
    this.putClientProperty("Quaqua.Component.visualMargin", new java.awt.Insets(2, 2, 2, 2));

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

  public void this_focusLost(FocusEvent e) {
    boolean valid = isValid(this.getText());
    if (valid && c_default_bg != null) {
      super.setBackground(c_default_bg);
    } else if (!valid) {
      super.setBackground(java.awt.Color.yellow);
    }
    if (!valid) {
      validator.displayMessage();
      EventQueue.invokeLater(new Runnable() {

        public void run() {
          requestFocus();
        }
      });
    }
  }

  @Override
  public void setBackground(java.awt.Color bg) {
    c_default_bg = bg;
    super.setBackground(bg);
  }

  public void setDataSource(DbDataSource dataSource) {
    if (dataSource != null) {
      dataSource.removeActionListener(actionWeakListener);
    }
    dbFieldObserver.setDataSource(dataSource);
    dbFieldObserverToolTip.setDataSource(dataSource);
    if (dataSource != null) {
      dataSource.addActionListener(actionWeakListener);
    }
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

  public void dataSource_actionPerformed(ActionEvent event) {
    if (event.getActionCommand().equals(DbDataSource.UPDATE_ROW)) {

      boolean valid = isValid(this.getText());

      if (!valid) {
        throw new IllegalStateException("Polje vsebuje napa?no vrednost");
      }
    }
  }

  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    documentWeakListener.setEnabled(false);
    try {
      setText(dbFieldObserver.getValueAsText());
      boolean valid = isValid(this.getText());
      if (valid && c_default_bg != null) {
        super.setBackground(c_default_bg);
      } else if (!valid) {
        super.setBackground(java.awt.Color.yellow);
      }
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

  private boolean isValid(Object value) {
    DbDataSource dataSource = getDataSource();
    if (validator == null) {
      return true;
    } else if (dataSource != null) {
      try {
        return (dataSource.isDataLoaded() && (dataSource.rowUpdated() || dataSource.rowInserted())) ? validator.isValid(value) : true;
      } catch (SQLException ex) {
        Logger.getLogger(JDbTextField.class.getName()).log(Level.SEVERE, null, ex);
        return validator.isValid(value);
      }
    } else {
      return validator.isValid(value);
    }
  }

  private void updateColumn() {
    if (!Equals.equals(dbFieldObserver.getValueAsText(), this.getText())) {
      activeRowChangeWeakListener.setEnabled(false);
      try {
        boolean valid = isValid(this.getText());
        if (valid && c_default_bg != null) {
          super.setBackground(c_default_bg);
        } else if (!valid) {
          super.setBackground(java.awt.Color.yellow);
        }
        if (valid) {
          String text = this.getText();
          if (text != null && text.equals("")) {
            text = null;
          }
          dbFieldObserver.updateValue(text);
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
   * Holds value of property searchField.
   */
  private boolean searchField;

  /**
   * Getter for property searchField.
   * @return Value of property searchField.
   */
  public boolean isSearchField() {
    return this.searchField;
  }

  /**
   * Setter for property searchField.
   * @param searchField New value of property searchField.
   */
  public void setSearchField(boolean searchField) {
    this.searchField = searchField;
    this.putClientProperty("Quaqua.TextField.style", searchField ? "search" : "normal");
    this.putClientProperty("JTextField.variant", searchField ? "search" : null);
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
  /**
   * Holds value of property autoCompleteModel.
   */
  private ComboBoxModel autoCompleteModel;

  /**
   * Getter for property autoCompleteModel.
   * @return Value of property autoCompleteModel.
   */
  public ComboBoxModel getAutoCompleteModel() {
    return this.autoCompleteModel;
  }

  /**
   * Setter for property dataModel.
   *
   * @param dataModel New value of property dataModel.
   */
  public void setAutoCompleteModel(ComboBoxModel dataModel) {
    if (dataModel != null) {
      ComboBoxModel oldModel = dataModel;
      if (oldModel != null) {
        oldModel.removeListDataListener(this);
      }
      this.autoCompleteModel = dataModel;

      dataModel.addListDataListener(this);

      AutoCompleteDecorator.decorate(this, dataModel);
      if (getDocument() instanceof AutoCompleteDocument) {
        ((AutoCompleteDocument) getDocument()).setAutoComplete(false);
      }

      firePropertyChange("autoCompleteModel", oldModel, dataModel);
    }
  }

  @Override
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
    if ((autoCompleteModel != null) && (getDocument() instanceof AutoCompleteDocument)) {
      AutoCompleteDocument document = (AutoCompleteDocument) getDocument();

      try {
        document.setAutoComplete(true);
        return super.processKeyBinding(ks, e, condition, pressed);
      } finally {
        document.setAutoComplete(false);
      }
    } else {
      return super.processKeyBinding(ks, e, condition, pressed);
    }
  }

  /**
   * Adds a <code>PopupMenu</code> listener which will listen to notification
   * messages from the popup portion of the combo box.
   * <p>
   * For all standard look and feels shipped with Java 2, the popup list
   * portion of combo box is implemented as a <code>JPopupMenu</code>.
   * A custom look and feel may not implement it this way and will
   * therefore not receive the notification.
   *
   * @param l  the <code>PopupMenuListener</code> to add
   * @since 1.4
   */
  public void addPopupMenuListener(PopupMenuListener l) {
    listenerList.add(PopupMenuListener.class, l);
  }

  /**
   * Removes a <code>PopupMenuListener</code>.
   *
   * @param l  the <code>PopupMenuListener</code> to remove
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void removePopupMenuListener(PopupMenuListener l) {
    listenerList.remove(PopupMenuListener.class, l);
  }

  /**
   * Returns an array of all the <code>PopupMenuListener</code>s added
   * to this JComboBox with addPopupMenuListener().
   *
   * @return all of the <code>PopupMenuListener</code>s added or an empty
   *         array if no listeners have been added
   * @since 1.4
   */
  public PopupMenuListener[] getPopupMenuListeners() {
    return (PopupMenuListener[]) listenerList.getListeners(
            PopupMenuListener.class);
  }

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box will become visible.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuWillBecomeVisible() {
    Object[] listeners = listenerList.getListenerList();
    PopupMenuEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == PopupMenuListener.class) {
        if (e == null) {
          e = new PopupMenuEvent(this);
        }
        ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeVisible(e);
      }
    }
  }

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box has become invisible.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuWillBecomeInvisible() {
    Object[] listeners = listenerList.getListenerList();
    PopupMenuEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == PopupMenuListener.class) {
        if (e == null) {
          e = new PopupMenuEvent(this);
        }
        ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeInvisible(e);
      }
    }
  }

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box has been canceled.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuCanceled() {
    Object[] listeners = listenerList.getListenerList();
    PopupMenuEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == PopupMenuListener.class) {
        if (e == null) {
          e = new PopupMenuEvent(this);
        }
        ((PopupMenuListener) listeners[i + 1]).popupMenuCanceled(e);
      }
    }
  }
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the accessor methods instead.
   *
   * @see #getMaximumRowCount
   * @see #setMaximumRowCount
   */
  protected int maximumRowCount = 8;

  /**
   * Sets the maximum number of rows the <code>JComboBox</code> displays.
   * If the number of objects in the model is greater than count,
   * the combo box uses a scrollbar.
   *
   * @param count an integer specifying the maximum number of items to
   *              display in the list before using a scrollbar
   * @beaninfo
   *        bound: true
   *    preferred: true
   *  description: The maximum number of rows the popup should have
   */
  public void setMaximumRowCount(int count) {
    int oldCount = maximumRowCount;
    maximumRowCount = count;
    firePropertyChange("maximumRowCount", oldCount, maximumRowCount);
  }

  /**
   * Returns the maximum number of items the combo box can display
   * without a scrollbar
   *
   * @return an integer specifying the maximum number of items that are
   *         displayed in the list before using a scrollbar
   */
  public int getMaximumRowCount() {
    return maximumRowCount;
  }

  /**
   * Adds an <code>ItemListener</code>.
   * <p>
   * <code>aListener</code> will receive one or two <code>ItemEvent</code>s when
   * the selected item changes.
   *
   * @param aListener the <code>ItemListener</code> that is to be notified
   * @see #setSelectedItem
   */
  public void addItemListener(ItemListener aListener) {
    listenerList.add(ItemListener.class, aListener);
  }

  /** Removes an <code>ItemListener</code>.
   *
   * @param aListener  the <code>ItemListener</code> to remove
   */
  public void removeItemListener(ItemListener aListener) {
    listenerList.remove(ItemListener.class, aListener);
  }

  /**
   * Returns an array of all the <code>ItemListener</code>s added
   * to this JComboBox with addItemListener().
   *
   * @return all of the <code>ItemListener</code>s added or an empty
   *         array if no listeners have been added
   * @since 1.4
   */
  public ItemListener[] getItemListeners() {
    return (ItemListener[]) listenerList.getListeners(ItemListener.class);
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.
   * @param e  the event of interest
   *
   * @see EventListenerList
   */
  protected void fireItemStateChanged(ItemEvent e) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ItemListener.class) {
        // Lazily create the event:
        // if (changeEvent == null)
        // changeEvent = new ChangeEvent(this);
        ((ItemListener) listeners[i + 1]).itemStateChanged(e);
      }
    }
  }

  /**
   * Returns the first item in the list that matches the given item.
   * The result is not always defined if the <code>JComboBox</code>
   * allows selected items that are not in the list.
   * Returns -1 if there is no selected item or if the user specified
   * an item which is not in the list.

   * @return an integer specifying the currently selected list item,
   *			where 0 specifies
   *                	the first item in the list;
   *			or -1 if no item is selected or if
   *                	the currently selected item is not in the list
   */
  public int getSelectedIndex() {
    if (autoCompleteModel instanceof DbComboBoxModel) {
      return ((DbComboBoxModel) autoCompleteModel).getSelectedIndex();
    } else {
      Object sObject = autoCompleteModel.getSelectedItem();
      int i, c;
      Object obj;

      for (i = 0, c = autoCompleteModel.getSize();
              i < c; i++) {
        obj = autoCompleteModel.getElementAt(i);
        if (obj != null && obj.equals(sObject)) {
          return i;
        }
      }
      return -1;
    }
  }

  /**
   * Returns the current selected item.
   * <p>
   * If the combo box is editable, then this value may not have been added
   * to the combo box with <code>addItem</code>, <code>insertItemAt</code>
   * or the data constructors.
   *
   * @return the current selected Object
   * @see #setSelectedItem
   */
  public Object getSelectedItem() {
    return autoCompleteModel == null ? getText() : autoCompleteModel.getSelectedItem();
  }

  /**
   * Sets the selected item in the autocomplete display area to the object in
   * the argument.
   * If this constitutes a change in the selected item,
   * <code>ItemListener</code>s added to the combo box will be notified with
   * one or two <code>ItemEvent</code>s.
   * If there is a current selected item, an <code>ItemEvent</code> will be
   * fired and the state change will be <code>ItemEvent.DESELECTED</code>.
   * If <code>anObject</code> is in the list and is not currently selected
   * then an <code>ItemEvent</code> will be fired and the state change will
   * be <code>ItemEvent.SELECTED</code>.
   *
   * @param anObject  the list object to select; use <code>null</code> to
  clear the selection
   * @beaninfo
   *    preferred:   true
   *    description: Sets the selected item in the autocomplete popup.
   */
  public void setSelectedItem(Object anObject) {
    Object oldSelection = autoCompleteModel.getSelectedItem();
    Object objectToSelect = anObject;

    if (oldSelection == null || !oldSelection.equals(anObject)) {
      autoCompleteModel.setSelectedItem(objectToSelect);

      if (selectedItemReminder != autoCompleteModel.getSelectedItem()) {
        // in case a users implementation of ComboBoxModel
        // doesn't fire a ListDataEvent when the selection
        // changes.
        selectedItemChanged();
      }
    }
  }

  public Object[] getSelectedObjects() {
    Object selectedObject = getSelectedItem();
    if (selectedObject == null) {
      return new Object[0];
    } else {
      Object result[] = new Object[1];
      result[0] = selectedObject;
      return result;
    }
  }
  /**
   * This protected field is implementation specific. Do not access directly
   * or override.
   */
  protected Object selectedItemReminder = null;

  /**
   * This method is public as an implementation side effect.
   * do not call or override.
   */
  public void contentsChanged(ListDataEvent e) {
    setSelectedItem(getText());
  }

  /**
   * This method is public as an implementation side effect.
   * do not call or override.
   */
  public void intervalAdded(ListDataEvent e) {
    setSelectedItem(getText());
  }

  /**
   * This method is public as an implementation side effect.
   * do not call or override.
   */
  public void intervalRemoved(ListDataEvent e) {
    setSelectedItem(getText());
  }

  /**
   * This protected method is implementation specific. Do not access directly
   * or override.
   */
  protected void selectedItemChanged() {
    if (selectedItemReminder != null) {
      fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
              selectedItemReminder,
              ItemEvent.DESELECTED));
    }

    // set the new selected item.
    selectedItemReminder = autoCompleteModel.getSelectedItem();

    if (selectedItemReminder != null) {
      fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
              selectedItemReminder,
              ItemEvent.SELECTED));
    }
  }
}
