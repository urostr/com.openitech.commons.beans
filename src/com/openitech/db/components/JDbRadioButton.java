/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.components;

/**
 *
 * @author domenbasic
 */

/*
 * @(#)JRadioButton.java	1.78 06/08/08
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import com.openitech.Settings;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.components.Validator;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.*;
import javax.accessibility.*;

import javax.swing.*;

/**
 * An implementation of a radio button -- an item that can be selected or
 * deselected, and which displays its state to the user.
 * Used with a {@link ButtonGroup} object to create a group of buttons
 * in which only one button at a time can be selected. (Create a ButtonGroup
 * object and use its <code>add</code> method to include the JRadioButton objects
 * in the group.)
 * <blockquote>
 * <strong>Note:</strong>
 * The ButtonGroup object is a logical grouping -- not a physical grouping.
 * Tocreate a button panel, you should still create a {@link JPanel} or similar
 * container-object and add a {@link javax.swing.border.Border} to it to set it off from surrounding
 * components.
 * </blockquote>
 * <p>
 * Buttons can be configured, and to some degree controlled, by
 * <code><a href="Action.html">Action</a></code>s.  Using an
 * <code>Action</code> with a button has many benefits beyond directly
 * configuring a button.  Refer to <a href="Action.html#buttonActions">
 * Swing Components Supporting <code>Action</code></a> for more
 * details, and you can find more information in <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/action.html">How
 * to Use Actions</a>, a section in <em>The Java Tutorial</em>.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/button.html">How to Use Buttons, Check Boxes, and Radio Buttons</a>
 * in <em>The Java Tutorial</em>
 * for further documentation.
 * <p>
 * <strong>Warning:</strong> Swing is not thread safe. For more
 * information see <a
 * href="package-summary.html#threading">Swing's Threading
 * Policy</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A component which can display it's state as selected or deselected.
 *
 * @see ButtonGroup
 * @see JCheckBox
 * @version 1.78 08/08/06
 * @author Jeff Dinkins
 */
public class JDbRadioButton extends JToggleButton implements ActionListener, FieldObserver, Accessible {

  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient ActionWeakListener actionWeakListener;
  private String checkedValue = "D";
  private String uncheckedValue = null;
  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "RadioButtonUI";

  /**
   * Creates an initially unselected radio button
   * with no set text.
   */
  public JDbRadioButton() {
    this(null, null, false);
  }

  /**
   * Creates an initially unselected radio button
   * with the specified image but no text.
   *
   * @param icon  the image that the button should display
   */
  public JDbRadioButton(Icon icon) {
    this(null, icon, false);
  }

  /**
   * Creates a radiobutton where properties are taken from the
   * Action supplied.
   *
   * @since 1.3
   */
  public JDbRadioButton(Action a) {
    this();
    setAction(a);
  }

  /**
   * Creates a radio button with the specified image
   * and selection state, but no text.
   *
   * @param icon  the image that the button should display
   * @param selected  if true, the button is initially selected;
   *                  otherwise, the button is initially unselected
   */
  public JDbRadioButton(Icon icon, boolean selected) {
    this(null, icon, selected);
  }

  /**
   * Creates an unselected radio button with the specified text.
   *
   * @param text  the string displayed on the radio button
   */
  public JDbRadioButton(String text) {
    this(text, null, false);
  }

  /**
   * Creates a radio button with the specified text
   * and selection state.
   *
   * @param text  the string displayed on the radio button
   * @param selected  if true, the button is initially selected;
   *                  otherwise, the button is initially unselected
   */
  public JDbRadioButton(String text, boolean selected) {
    this(text, null, selected);
  }

  /**
   * Creates a radio button that has the specified text and image,
   * and that is initially unselected.
   *
   * @param text  the string displayed on the radio button
   * @param icon  the image that the button should display
   */
  public JDbRadioButton(String text, Icon icon) {
    this(text, icon, false);
  }

  /**
   * Creates a radio button that has the specified text, image,
   * and selection state.
   *
   * @param text  the string displayed on the radio button
   * @param icon  the image that the button should display
   */
  public JDbRadioButton(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    setBorderPainted(false);
    setHorizontalAlignment(LEADING);
    try {
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_fieldValueChanged", null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this, "dataSource_toolTipFieldValueChanged", null);
      actionWeakListener = new ActionWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addActionListener(actionWeakListener);
  }

  /**
   * Resets the UI property to a value from the current look and feel.
   *
   * @see JComponent#updateUI
   */
  public void updateUI() {
    setUI((ButtonUI) UIManager.getUI(this));
  }

  /**
   * Returns the name of the L&F class
   * that renders this component.
   *
   * @return String "RadioButtonUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   * @beaninfo
   *        expert: true
   *   description: A string that specifies the name of the L&F class.
   */
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   * The icon for radio buttons comes from the look and feel,
   * not the Action.
   */
  void setIconFromAction(Action a) {
  }

  /**
   * Returns a string representation of this JRadioButton. This method
   * is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not
   * be <code>null</code>.
   *
   * @return  a string representation of this JRadioButton.
   */
  protected String paramString() {
    return super.paramString();
  }


/////////////////
// Accessibility support
////////////////
  /**
   * Gets the AccessibleContext associated with this JRadioButton.
   * For JRadioButtons, the AccessibleContext takes the form of an
   * AccessibleJRadioButton.
   * A new AccessibleJRadioButton instance is created if necessary.
   *
   * @return an AccessibleJRadioButton that serves as the
   *         AccessibleContext of this JRadioButton
   * @beaninfo
   *       expert: true
   *  description: The AccessibleContext associated with this Button
   */
  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJRadioButton();
    }
    return accessibleContext;
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
    boolean tip = dbFieldObserverToolTip.getValueAsText().equalsIgnoreCase(checkedValue);
    if (!dbFieldObserverToolTip.wasNull()) {
      this.setToolTipText("Pomo\u010d : " + (tip ? ("izbrano" + (checkedValue != null ? " [" + checkedValue + "]" : "")) : ("prazno" + (uncheckedValue != null ? " [" + uncheckedValue + "]" : ""))));
    } else {
      this.setToolTipText(null);
    }
  }

  private void updateColumn() {
    activeRowChangeWeakListener.setEnabled(false);
    try {
      if ((validator == null) || (validator != null && validator.isValid(this.isSelected()))) {
        if ((getModel() instanceof DefaultButtonModel) &&
                (((DefaultButtonModel) getModel()).getGroup() == null)) {
          dbFieldObserver.updateValue(this.isSelected() ? checkedValue : uncheckedValue);
        } else {
          if (this.isSelected()) {
            dbFieldObserver.updateValue(checkedValue);
          }
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
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

  /**
   * This class implements accessibility support for the
   * <code>JRadioButton</code> class.  It provides an implementation of the
   * Java Accessibility API appropriate to radio button
   * user-interface elements.
   * <p>
   * <strong>Warning:</strong>
   * Serialized objects of this class will not be compatible with
   * future Swing releases. The current serialization support is
   * appropriate for short term storage or RMI between applications running
   * the same version of Swing.  As of 1.4, support for long term storage
   * of all JavaBeans<sup><font size="-2">TM</font></sup>
   * has been added to the <code>java.beans</code> package.
   * Please see {@link java.beans.XMLEncoder}.
   */
  protected class AccessibleJRadioButton extends AccessibleJToggleButton {

    /**
     * Get the role of this object.
     *
     * @return an instance of AccessibleRole describing the role of the object
     * @see AccessibleRole
     */
    public AccessibleRole getAccessibleRole() {
      return AccessibleRole.RADIO_BUTTON;
    }
  } // inner class AccessibleJRadioButton
}

