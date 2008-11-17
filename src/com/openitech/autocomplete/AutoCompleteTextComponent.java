/*
 * AutoCompleteTextComponent.java
 *
 * Created on Ponedeljek, 1 september 2008, 9:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.autocomplete;

import java.awt.ItemSelectable;
import java.awt.event.ItemListener;
import javax.swing.ComboBoxModel;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author uros
 */
public interface AutoCompleteTextComponent extends ItemSelectable {

  /**
   * Getter for property autoCompleteModel.
   * @return Value of property autoCompleteModel.
   */
  public ComboBoxModel getAutoCompleteModel();

  /**
   * Setter for property autoCompleteModel.
   * @param autoCompleteModel New value of property autoCompleteModel.
   */
  public void setAutoCompleteModel(ComboBoxModel autoCompleteModel);

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
  public void addPopupMenuListener(PopupMenuListener l);

  /**
   * Removes a <code>PopupMenuListener</code>.
   *
   * @param l  the <code>PopupMenuListener</code> to remove
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void removePopupMenuListener(PopupMenuListener l);

  /**
   * Returns an array of all the <code>PopupMenuListener</code>s added
   * to this JComboBox with addPopupMenuListener().
   *
   * @return all of the <code>PopupMenuListener</code>s added or an empty
   *         array if no listeners have been added
   * @since 1.4
   */
  public PopupMenuListener[] getPopupMenuListeners();

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box will become visible.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuWillBecomeVisible();

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box has become invisible.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuWillBecomeInvisible();

  /**
   * Notifies <code>PopupMenuListener</code>s that the popup portion of the
   * combo box has been canceled.
   * <p>
   * This method is public but should not be called by anything other than
   * the UI delegate.
   * @see #addPopupMenuListener
   * @since 1.4
   */
  public void firePopupMenuCanceled();

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
  public void setMaximumRowCount(int count);

  /**
   * Returns the maximum number of items the combo box can display
   * without a scrollbar
   *
   * @return an integer specifying the maximum number of items that are
   *         displayed in the list before using a scrollbar
   */
  public int getMaximumRowCount();

  /**
   * Adds an <code>ItemListener</code>.
   * <p>
   * <code>aListener</code> will receive one or two <code>ItemEvent</code>s when
   * the selected item changes.
   *
   * @param aListener the <code>ItemListener</code> that is to be notified
   * @see #setSelectedItem
   */
  public void addItemListener(ItemListener aListener);

  /** Removes an <code>ItemListener</code>.
   *
   * @param aListener  the <code>ItemListener</code> to remove
   */
  public void removeItemListener(ItemListener aListener);

  /**
   * Returns an array of all the <code>ItemListener</code>s added
   * to this JComboBox with addItemListener().
   *
   * @return all of the <code>ItemListener</code>s added or an empty
   *         array if no listeners have been added
   * @since 1.4
   */
  public ItemListener[] getItemListeners();

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
  public Object getSelectedItem();

  /**
   * Returns the first item in the list that matches the given item.
   * The result is not always defined if the <code>JComboBox</code>
   * allows selected items that are not in the list.
   * Returns -1 if there is no selected item or if the user specified
   * an item which is not in the list.
   *
   * @return an integer specifying the currently selected list item,
   *			where 0 specifies
   *                	the first item in the list;
   *			or -1 if no item is selected or if
   *                	the currently selected item is not in the list
   */
  public int getSelectedIndex();

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
  public void setSelectedItem(Object anObject);
}
