/*
 * AutoCompleteTextComponentPopup.java
 *
 * Created on Ponedeljek, 1 september 2008, 9:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.autocomplete;

/*
 * @(#)BasicComboPopup.java	1.78 04/03/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import static com.openitech.autocomplete.ObjectToStringConverter.DEFAULT_IMPLEMENTATION;
import com.openitech.db.model.DbComboBoxModel;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;

/**
 * This is a basic implementation of the <code>ComboPopup</code> interface.
 * 
 * This class represents the ui for the popup portion of the combo box.
 * <p>
 * All event handling is handled by listener classes created with the 
 * <code>createxxxListener()</code> methods and internal classes.
 * You can change the behavior of this class by overriding the
 * <code>createxxxListener()</code> methods and supplying your own
 * event listeners or subclassing from the ones supplied in this class.
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
 * @version 1.78 03/05/04
 * @author Tom Santos
 * @author Mark Davidson
 */
public class AutoCompleteTextComponentPopup extends JPopupMenu implements ComboPopup {

  // An empty ListMode, this is used when the UI changes to allow
  // the JList to be gc'ed.
  private static class EmptyListModelClass implements ListModel,
          Serializable {

    public int getSize() {
      return 0;
    }

    public Object getElementAt(int index) {
      return null;
    }

    public void addListDataListener(ListDataListener l) {
    }

    public void removeListDataListener(ListDataListener l) {
    }
  };
  static final ListModel EmptyListModel = new EmptyListModelClass();
  private static Border LIST_BORDER = new LineBorder(Color.BLACK, 1);
  protected JTextComponent textComponent;
  protected ComboBoxModel comboBoxModel;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the accessor methods instead.
   *
   * @see #getList
   * @see #createList
   */
  protected JList list;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead
   *
   * @see #createScroller
   */
  protected JScrollPane scroller;
  /**
   * As of Java 2 platform v1.4 this previously undocumented field is no
   * longer used.
   */
  protected boolean valueIsAdjusting = false;

  // Listeners that are required by the ComboPopup interface
  /**
   * Implementation of all the listener classes.
   */
  private Handler handler;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the accessor or create methods instead.
   *
   * @see #getMouseMotionListener
   * @see #createMouseMotionListener
   */
  protected MouseMotionListener mouseMotionListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the accessor or create methods instead.
   *
   * @see #getMouseListener
   * @see #createMouseListener
   */
  protected MouseListener mouseListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the accessor or create methods instead.
   *
   * @see #getKeyListener
   * @see #createKeyListener
   */
  protected KeyListener keyListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead.
   *
   * @see #createListSelectionListener
   */
  protected ListSelectionListener listSelectionListener;

  // Listeners that are attached to the list
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead.
   *
   * @see #createListMouseListener
   */
  protected MouseListener listMouseListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead
   *
   * @see #createListMouseMotionListener
   */
  protected MouseMotionListener listMouseMotionListener;

  // Added to the combo box for bound properties
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead
   *
   * @see #createPropertyChangeListener
   */
  protected PropertyChangeListener propertyChangeListener;

  // Added to the combo box model
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead
   *
   * @see #createListDataListener
   */
  protected ListDataListener listDataListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override. Use the create method instead
   *
   * @see #createItemListener
   */
  protected ItemListener itemListener;
  /**
   * This protected field is implementation specific. Do not access directly
   * or override.
   */
  protected Timer autoscrollTimer;
  protected boolean hasEntered = false;
  protected boolean isAutoScrolling = false;
  protected int scrollDirection = SCROLL_UP;
  protected static final int SCROLL_UP = 0;
  protected static final int SCROLL_DOWN = 1;


  //========================================
  // begin ComboPopup method implementations
  //
  protected String getSelectedText() {
    return DEFAULT_IMPLEMENTATION.getPreferredStringForItem(list.getSelectedValue());
  }

  /**
   * Returns the first item in the list that matches the given item.
   * The result is not always defined if the <code>JTextComponent</code>
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
    if (comboBoxModel instanceof DbComboBoxModel) {
      return ((DbComboBoxModel) comboBoxModel).getSelectedIndex();
    } else {
      Object sObject = comboBoxModel.getSelectedItem();
      int i, c;
      Object obj;

      for (i = 0      ,
        c = comboBoxModel.getSize();
         i < c;i++ ) {
            obj = comboBoxModel.getElementAt(i);
            if ( obj != null && obj.equals(sObject)) {
          return i;
        }
      }
      return -1;
    }
  }

  /**
   * Implementation of ComboPopup.show().
   */
  public void show() {
    setListSelection(getSelectedIndex());

    Point location = getPopupLocation();
    show(textComponent, location.x, location.y);
  }

  /**
   * Implementation of ComboPopup.hide().
   */
  public void hide() {
    MenuSelectionManager manager = MenuSelectionManager.defaultManager();
    MenuElement[] selection = manager.getSelectedPath();
    for (int i = 0; i < selection.length; i++) {
      if (selection[i] == this) {
        manager.clearSelectedPath();
        break;
      }
    }
    if (selection.length > 0) {
      textComponent.repaint();
    }
  }

  private void refresh() {
    if (isVisible()) {
      setVisible(false);
      Point location = getPopupLocation();
      show(textComponent, location.x, location.y);
    }
  }

  /**
   * Implementation of ComboPopup.getList().
   */
  public JList getList() {
    return list;
  }

  /**
   * Implementation of ComboPopup.getMouseListener().
   *
   * @return a <code>MouseListener</code> or null
   * @see ComboPopup#getMouseListener
   */
  public MouseListener getMouseListener() {
    if (mouseListener == null) {
      mouseListener = createMouseListener();
    }
    return mouseListener;
  }

  /**
   * Implementation of ComboPopup.getMouseMotionListener().
   *
   * @return a <code>MouseMotionListener</code> or null
   * @see ComboPopup#getMouseMotionListener
   */
  public MouseMotionListener getMouseMotionListener() {
    if (mouseMotionListener == null) {
      mouseMotionListener = createMouseMotionListener();
    }
    return mouseMotionListener;
  }

  /**
   * Implementation of ComboPopup.getKeyListener().
   *
   * @return a <code>KeyListener</code> or null
   * @see ComboPopup#getKeyListener
   */
  public KeyListener getKeyListener() {
    if (keyListener == null) {
      keyListener = createKeyListener();
    }
    return keyListener;
  }

  /**
   * Called when the UI is uninstalling.  Since this popup isn't in the component
   * tree, it won't get it's uninstallUI() called.  It removes the listeners that
   * were added in addComboBoxListeners().
   */
  public void uninstallingUI() {
    if (propertyChangeListener != null) {
      textComponent.removePropertyChangeListener(propertyChangeListener);
    }
    if (textComponent instanceof AutoCompleteTextComponent) {
      if (itemListener != null) {
        ((AutoCompleteTextComponent) textComponent).removeItemListener(itemListener);
      }
    }
    uninstallComboBoxModelListeners(comboBoxModel);
    uninstallKeyboardActions();
    uninstallListListeners();
    // We do this, otherwise the listener the ui installs on
    // the model (the combobox model in this case) will keep a
    // reference to the list, causing the list (and us) to never get gced.
    list.setModel(EmptyListModel);
  }

  //
  // end ComboPopup method implementations
  //======================================
  /**
   * Removes the listeners from the combo box model
   *
   * @param model The combo box model to install listeners
   * @see #installComboBoxModelListeners
   */
  protected void uninstallComboBoxModelListeners(ComboBoxModel model) {
    if (model != null && listDataListener != null) {
      model.removeListDataListener(listDataListener);
    }
  }

  protected void uninstallKeyboardActions() {
    // XXX - shouldn't call this method
//        comboBoxModel.unregisterKeyboardAction( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ) );
    }

  //===================================================================
  // begin Initialization routines
  //
  public AutoCompleteTextComponentPopup(JTextComponent component, ComboBoxModel model) {
    super();
    setName("AutoCompleteTextComponentPopup.popup");
    textComponent = component;
    comboBoxModel = model;

    setLightWeightPopupEnabled((new JComboBox()).isLightWeightPopupEnabled());

    // UI construction of the popup.
    list = createList();
    list.setName("AutoCompleteTextComponent.list");
    configureList();
    scroller = createScroller();
    scroller.setName("AutoCompleteTextComponent.scrollPane");
    configureScroller();
    configurePopup();

    installComboBoxListeners();
    installKeyboardActions();
  }

  // Overriden PopupMenuListener notification methods to inform combo box
  // PopupMenuListeners.
  protected void firePopupMenuWillBecomeVisible() {
    super.firePopupMenuWillBecomeVisible();
    if (textComponent instanceof AutoCompleteTextComponent) {
      ((AutoCompleteTextComponent) textComponent).firePopupMenuWillBecomeVisible();
    }
  }

  protected void firePopupMenuWillBecomeInvisible() {
    super.firePopupMenuWillBecomeInvisible();
    if (textComponent instanceof AutoCompleteTextComponent) {
      ((AutoCompleteTextComponent) textComponent).firePopupMenuWillBecomeInvisible();
    }
  }

  protected void firePopupMenuCanceled() {
    super.firePopupMenuCanceled();
    if (textComponent instanceof AutoCompleteTextComponent) {
      ((AutoCompleteTextComponent) textComponent).firePopupMenuCanceled();
    }
  }

  /**
   * Creates a listener
   * that will watch for mouse-press and release events on the combo box.
   *
   * <strong>Warning:</strong>
   * When overriding this method, make sure to maintain the existing
   * behavior.
   *
   * @return a <code>MouseListener</code> which will be added to
   * the combo box or null
   */
  protected MouseListener createMouseListener() {
    return getHandler();
  }

  /**
   * Creates the mouse motion listener which will be added to the combo
   * box.
   *
   * <strong>Warning:</strong>
   * When overriding this method, make sure to maintain the existing
   * behavior.
   *
   * @return a <code>MouseMotionListener</code> which will be added to
   *         the combo box or null
   */
  protected MouseMotionListener createMouseMotionListener() {
    return getHandler();
  }

  /**
   * Creates the key listener that will be added to the combo box. If
   * this method returns null then it will not be added to the combo box.
   *
   * @return a <code>KeyListener</code> or null
   */
  protected KeyListener createKeyListener() {
    return null;
  }

  /**
   * Creates a list selection listener that watches for selection changes in
   * the popup's list.  If this method returns null then it will not
   * be added to the popup list.
   *
   * @return an instance of a <code>ListSelectionListener</code> or null
   */
  protected ListSelectionListener createListSelectionListener() {
    return null;
  }

  /**
   * Creates a list data listener which will be added to the
   * <code>ComboBoxModel</code>. If this method returns null then
   * it will not be added to the combo box model.
   *
   * @return an instance of a <code>ListDataListener</code> or null
   */
  protected ListDataListener createListDataListener() {
    return getHandler();
  }

  /**
   * Creates a mouse listener that watches for mouse events in
   * the popup's list. If this method returns null then it will
   * not be added to the combo box.
   *
   * @return an instance of a <code>MouseListener</code> or null
   */
  protected MouseListener createListMouseListener() {
    return getHandler();
  }

  /**
   * Creates a mouse motion listener that watches for mouse motion
   * events in the popup's list. If this method returns null then it will
   * not be added to the combo box.
   *
   * @return an instance of a <code>MouseMotionListener</code> or null
   */
  protected MouseMotionListener createListMouseMotionListener() {
    return getHandler();
  }

  /**
   * Creates a <code>PropertyChangeListener</code> which will be added to
   * the combo box. If this method returns null then it will not
   * be added to the combo box.
   *
   * @return an instance of a <code>PropertyChangeListener</code> or null
   */
  protected PropertyChangeListener createPropertyChangeListener() {
    return getHandler();
  }

  /**
   * Creates an <code>ItemListener</code> which will be added to the
   * combo box. If this method returns null then it will not
   * be added to the combo box.
   * <p>
   * Subclasses may override this method to return instances of their own
   * ItemEvent handlers.
   *
   * @return an instance of an <code>ItemListener</code> or null
   */
  protected ItemListener createItemListener() {
    return getHandler();
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  /**
   * Creates the JList used in the popup to display
   * the items in the combo box model. This method is called when the UI class
   * is created.
   *
   * @return a <code>JList</code> used to display the combo box items
   */
  protected JList createList() {
    return new JList(comboBoxModel) {

      public void processMouseEvent(MouseEvent e) {
        if (e.isControlDown()) {
          // Fix for 4234053. Filter out the Control Key from the list.
          // ie., don't allow CTRL key deselection.
          e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(),
                  e.getModifiers() ^ InputEvent.CTRL_MASK,
                  e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger());
        }
        super.processMouseEvent(e);
      }
    };
  }

  /**
   * Configures the list which is used to hold the combo box items in the
   * popup. This method is called when the UI class
   * is created.
   *
   * @see #createList
   */
  protected void configureList() {
    list.setFont(textComponent.getFont());
    list.setForeground(textComponent.getForeground());
    list.setBackground(textComponent.getBackground());
    list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
    list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
    list.setBorder(null);
    list.setCellRenderer((new JComboBox()).getRenderer());
    list.setFocusable(false);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setListSelection(getSelectedIndex());
    installListListeners();
  }

  /**
   * Adds the listeners to the list control.
   */
  protected void installListListeners() {
    if ((listMouseListener = createListMouseListener()) != null) {
      list.addMouseListener(listMouseListener);
    }
    if ((listMouseMotionListener = createListMouseMotionListener()) != null) {
      list.addMouseMotionListener(listMouseMotionListener);
    }
    if ((listSelectionListener = createListSelectionListener()) != null) {
      list.addListSelectionListener(listSelectionListener);
    }
  }

  void uninstallListListeners() {
    if (listMouseListener != null) {
      list.removeMouseListener(listMouseListener);
      listMouseListener = null;
    }
    if (listMouseMotionListener != null) {
      list.removeMouseMotionListener(listMouseMotionListener);
      listMouseMotionListener = null;
    }
    if (listSelectionListener != null) {
      list.removeListSelectionListener(listSelectionListener);
      listSelectionListener = null;
    }
    handler = null;
  }

  /**
   * Creates the scroll pane which houses the scrollable list.
   */
  protected JScrollPane createScroller() {
    JScrollPane sp = new JScrollPane(list,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sp.setHorizontalScrollBar(null);
    return sp;
  }

  /**
   * Configures the scrollable portion which holds the list within
   * the combo box popup. This method is called when the UI class
   * is created.
   */
  protected void configureScroller() {
    scroller.setFocusable(false);
    scroller.getVerticalScrollBar().setFocusable(false);
    scroller.setBorder(null);
  }

  /**
   * Configures the popup portion of the combo box. This method is called
   * when the UI class is created.
   */
  protected void configurePopup() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorderPainted(true);
    setBorder(LIST_BORDER);
    setOpaque(false);
    add(scroller);
    setDoubleBuffered(true);
    setFocusable(false);
  }

  /**
   * This method adds the necessary listeners to the JTextComponent.
   */
  protected void installComboBoxListeners() {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      textComponent.addPropertyChangeListener(propertyChangeListener);
    }
    if (textComponent instanceof AutoCompleteTextComponent) {
      if ((itemListener = createItemListener()) != null) {
        ((AutoCompleteTextComponent) textComponent).addItemListener(itemListener);
      }
    }
    installComboBoxModelListeners(comboBoxModel);
  }

  /**
   * Installs the listeners on the combo box model. Any listeners installed
   * on the combo box model should be removed in
   * <code>uninstallComboBoxModelListeners</code>.
   *
   * @param model The combo box model to install listeners
   * @see #uninstallComboBoxModelListeners
   */
  protected void installComboBoxModelListeners(ComboBoxModel model) {
    if (model != null && (listDataListener = createListDataListener()) != null) {
      model.addListDataListener(listDataListener);
    }
  }

  protected void installKeyboardActions() {
    /* XXX - shouldn't call this method. take it out for testing.
    ActionListener action = new ActionListener() {
    public void actionPerformed(ActionEvent e){
    }
    };

    comboBoxModel.registerKeyboardAction( action,
    KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ),
    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ); */
  }

  //
  // end Initialization routines
  //=================================================================
  //===================================================================
  // begin Event Listenters
  //
  /**
   * A listener to be registered upon the combo box
   * (<em>not</em> its popup menu)
   * to handle mouse events
   * that affect the state of the popup menu.
   * The main purpose of this listener is to make the popup menu
   * appear and disappear.
   * This listener also helps
   * with click-and-drag scenarios by setting the selection if the mouse was
   * released over the list during a drag.
   *
   * <p>
   * <strong>Warning:</strong>
   * We recommend that you <em>not</em>
   * create subclasses of this class.
   * If you absolutely must create a subclass,
   * be sure to invoke the superclass
   * version of each method.
   *
   * @see BasicComboPopup#createMouseListener
   */
  protected class InvocationMouseHandler extends MouseAdapter {

    /**
     * Responds to mouse-pressed events on the combo box.
     *
     * @param e the mouse-press event to be handled
     */
    public void mousePressed(MouseEvent e) {
      getHandler().mousePressed(e);
    }

    /**
     * Responds to the user terminating
     * a click or drag that began on the combo box.
     *
     * @param e the mouse-release event to be handled
     */
    public void mouseReleased(MouseEvent e) {
      getHandler().mouseReleased(e);
    }
  }

  /**
   * This listener watches for dragging and updates the current selection in the
   * list if it is dragging over the list.
   */
  protected class InvocationMouseMotionHandler extends MouseMotionAdapter {

    public void mouseDragged(MouseEvent e) {
      getHandler().mouseDragged(e);
    }
  }

  /**
   * As of Java 2 platform v 1.4, this class is now obsolete and is only included for
   * backwards API compatibility. Do not instantiate or subclass.
   * <p>
   * All the functionality of this class has been included in
   * BasicComboBoxUI ActionMap/InputMap methods.
   */
  public class InvocationKeyHandler extends KeyAdapter {

    public void keyReleased(KeyEvent keyEvent) {
      int keyCode = keyEvent.getKeyCode();
      // don't popup if the combobox isn't visible anyway
      //confirm selection and close popup
      if (isVisible() && (keyCode == keyEvent.VK_ENTER)) {
        textComponent.setText(getSelectedText());
        hide();
      }
    }
  }

  /**
   * As of Java 2 platform v 1.4, this class is now obsolete, doesn't do anything, and
   * is only included for backwards API compatibility. Do not call or
   * override.
   */
  protected class ListSelectionHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
    }
  }

  /**
   * As of 1.4, this class is now obsolete, doesn't do anything, and
   * is only included for backwards API compatibility. Do not call or
   * override.
   * <p>
   * The functionality has been migrated into <code>ItemHandler</code>.
   *
   * @see #createItemListener
   */
  public class ListDataHandler implements ListDataListener {

    public void contentsChanged(ListDataEvent e) {
    }

    public void intervalAdded(ListDataEvent e) {
    }

    public void intervalRemoved(ListDataEvent e) {
    }
  }

  /**
   * This listener hides the popup when the mouse is released in the list.
   */
  protected class ListMouseHandler extends MouseAdapter {

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent anEvent) {
      getHandler().mouseReleased(anEvent);
    }
  }

  /**
   * This listener changes the selected item as you move the mouse over the list.
   * The selection change is not committed to the model, this is for user feedback only.
   */
  protected class ListMouseMotionHandler extends MouseMotionAdapter {

    public void mouseMoved(MouseEvent anEvent) {
      getHandler().mouseMoved(anEvent);
    }
  }

  /**
   * This listener watches for changes to the selection in the
   * combo box.
   */
  protected class ItemHandler implements ItemListener {

    public void itemStateChanged(ItemEvent e) {
      getHandler().itemStateChanged(e);
    }
  }

  /**
   * This listener watches for bound properties that have changed in the
   * combo box.
   * <p>
   * Subclasses which wish to listen to combo box property changes should
   * call the superclass methods to ensure that the combo popup correctly
   * handles property changes.
   *
   * @see #createPropertyChangeListener
   */
  protected class PropertyChangeHandler implements PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent e) {
      getHandler().propertyChange(e);
    }
  }

  private class AutoScrollActionHandler implements ActionListener {

    private int direction;

    AutoScrollActionHandler(int direction) {
      this.direction = direction;
    }

    public void actionPerformed(ActionEvent e) {
      if (direction == SCROLL_UP) {
        autoScrollUp();
      } else {
        autoScrollDown();
      }
    }
  }

  private class Handler implements ItemListener, MouseListener,
          MouseMotionListener, PropertyChangeListener, ListDataListener,
          Serializable {
    //
    // MouseListener
    // NOTE: this is added to both the JList and JTextComponent
    //

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
      if (e.getSource() == list) {
        return;
      }
      if (!SwingUtilities.isLeftMouseButton(e) || !textComponent.isEnabled()) {
        return;
      }

      if (textComponent.isEditable()) {
        Component comp = textComponent;
        if ((!(comp instanceof JComponent)) || ((JComponent) comp).isRequestFocusEnabled()) {
          comp.requestFocus();
        }
      } else if (textComponent.isRequestFocusEnabled()) {
        textComponent.requestFocus();
      }
      togglePopup();
    }

    public void mouseReleased(MouseEvent e) {
      if (e.getSource() == list) {
        // JList mouse listener
        comboBoxModel.setSelectedItem(list.getSelectedValue());
        textComponent.setText(list.getSelectedValue().toString());
        hide();
        return;
      }
      // JTextComponent mouse listener
      Component source = (Component) e.getSource();
      Dimension size = source.getSize();
      Rectangle bounds = new Rectangle(0, 0, size.width - 1, size.height - 1);
      if (!bounds.contains(e.getPoint())) {
        MouseEvent newEvent = convertMouseEvent(e);
        Point location = newEvent.getPoint();
        Rectangle r = new Rectangle();
        list.computeVisibleRect(r);
        if (r.contains(location)) {
          comboBoxModel.setSelectedItem(list.getSelectedValue());
        }
        hide();
      }
      hasEntered = false;
      stopAutoScrolling();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    //
    // MouseMotionListener:
    // NOTE: this is added to both the List and ComboBox
    //
    public void mouseMoved(MouseEvent anEvent) {
      if (anEvent.getSource() == list) {
        Point location = anEvent.getPoint();
        Rectangle r = new Rectangle();
        list.computeVisibleRect(r);
        if (r.contains(location)) {
          updateListBoxSelectionForEvent(anEvent, false);
        }
      }
    }

    public void mouseDragged(MouseEvent e) {
      if (e.getSource() == list) {
        return;
      }
      if (isVisible()) {
        MouseEvent newEvent = convertMouseEvent(e);
        Rectangle r = new Rectangle();
        list.computeVisibleRect(r);

        if (newEvent.getPoint().y >= r.y && newEvent.getPoint().y <= r.y + r.height - 1) {
          hasEntered = true;
          if (isAutoScrolling) {
            stopAutoScrolling();
          }
          Point location = newEvent.getPoint();
          if (r.contains(location)) {
            updateListBoxSelectionForEvent(newEvent, false);
          }
        } else {
          if (hasEntered) {
            int directionToScroll = newEvent.getPoint().y < r.y ? SCROLL_UP : SCROLL_DOWN;
            if (isAutoScrolling && scrollDirection != directionToScroll) {
              stopAutoScrolling();
              startAutoScrolling(directionToScroll);
            } else if (!isAutoScrolling) {
              startAutoScrolling(directionToScroll);
            }
          } else {
            if (e.getPoint().y < 0) {
              hasEntered = true;
              startAutoScrolling(SCROLL_UP);
            }
          }
        }
      }
    }

    //
    // PropertyChangeListener
    //
    public void propertyChange(PropertyChangeEvent e) {
      JTextComponent comboBox = (JTextComponent) e.getSource();
      String propertyName = e.getPropertyName();

      if (propertyName == "autoCompleteModel") {
        ComboBoxModel oldModel = (ComboBoxModel) e.getOldValue();
        ComboBoxModel newModel = (ComboBoxModel) e.getNewValue();
        uninstallComboBoxModelListeners(oldModel);
        installComboBoxModelListeners(newModel);

        list.setModel(newModel);

        if (isVisible()) {
          hide();
        }
      } else if (propertyName == "componentOrientation") {
        // Pass along the new component orientation
        // to the list and the scroller

        ComponentOrientation o = (ComponentOrientation) e.getNewValue();

        JList list = getList();
        if (list != null && list.getComponentOrientation() != o) {
          list.setComponentOrientation(o);
        }

        if (scroller != null && scroller.getComponentOrientation() != o) {
          scroller.setComponentOrientation(o);
        }

        if (o != getComponentOrientation()) {
          setComponentOrientation(o);
        }
      }
    }

    //
    // ItemListener
    //
    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (e instanceof AutoCompleteTextComponent) {
          AutoCompleteTextComponent textComponent = (AutoCompleteTextComponent) e.getSource();
          setListSelection(textComponent.getSelectedIndex());
        }
      }
    }

    public void intervalAdded(ListDataEvent e) {
      refresh();
    }

    public void intervalRemoved(ListDataEvent e) {
      refresh();
    }

    public void contentsChanged(ListDataEvent e) {
      refresh();
    }
  }

  //
  // end Event Listeners
  //=================================================================
  /**
   * Overridden to unconditionally return false.
   */
  public boolean isFocusTraversable() {
    return false;
  }

  //===================================================================
  // begin Autoscroll methods
  //
  /**
   * This protected method is implementation specific and should be private.
   * do not call or override.
   */
  protected void startAutoScrolling(int direction) {
    // XXX - should be a private method within InvocationMouseMotionHandler
    // if possible.
    if (isAutoScrolling) {
      autoscrollTimer.stop();
    }

    isAutoScrolling = true;

    if (direction == SCROLL_UP) {
      scrollDirection = SCROLL_UP;
      Point convertedPoint = SwingUtilities.convertPoint(scroller, new Point(1, 1), list);
      int top = list.locationToIndex(convertedPoint);
      list.setSelectedIndex(top);

      autoscrollTimer = new Timer(100, new AutoScrollActionHandler(
              SCROLL_UP));
    } else if (direction == SCROLL_DOWN) {
      scrollDirection = SCROLL_DOWN;
      Dimension size = scroller.getSize();
      Point convertedPoint = SwingUtilities.convertPoint(scroller,
              new Point(1, (size.height - 1) - 2),
              list);
      int bottom = list.locationToIndex(convertedPoint);
      list.setSelectedIndex(bottom);

      autoscrollTimer = new Timer(100, new AutoScrollActionHandler(
              SCROLL_DOWN));
    }
    autoscrollTimer.start();
  }

  /**
   * This protected method is implementation specific and should be private.
   * do not call or override.
   */
  protected void stopAutoScrolling() {
    isAutoScrolling = false;

    if (autoscrollTimer != null) {
      autoscrollTimer.stop();
      autoscrollTimer = null;
    }
  }

  /**
   * This protected method is implementation specific and should be private.
   * do not call or override.
   */
  protected void autoScrollUp() {
    int index = list.getSelectedIndex();
    if (index > 0) {
      list.setSelectedIndex(index - 1);
      list.ensureIndexIsVisible(index - 1);
    }
  }

  /**
   * This protected method is implementation specific and should be private.
   * do not call or override.
   */
  protected void autoScrollDown() {
    int index = list.getSelectedIndex();
    int lastItem = list.getModel().getSize() - 1;
    if (index < lastItem) {
      list.setSelectedIndex(index + 1);
      list.ensureIndexIsVisible(index + 1);
    }
  }

  //
  // end Autoscroll methods
  //=================================================================
  //===================================================================
  // begin Utility methods
  //
  /**
   * Gets the AccessibleContext associated with this BasicComboPopup.
   * The AccessibleContext will have its parent set to the ComboBox.
   *
   * @return an AccessibleContext for the BasicComboPopup
   * @since 1.5
   */
  public AccessibleContext getAccessibleContext() {
    AccessibleContext context = super.getAccessibleContext();
    context.setAccessibleParent(textComponent);
    return context;
  }

  /**
   * This is is a utility method that helps event handlers figure out where to
   * send the focus when the popup is brought up.  The standard implementation
   * delegates the focus to the editor (if the combo box is editable) or to
   * the JTextComponent if it is not editable.
   */
  protected void delegateFocus(MouseEvent e) {
    if (textComponent.isEditable()) {
      Component comp = textComponent;
      if ((!(comp instanceof JComponent)) || ((JComponent) comp).isRequestFocusEnabled()) {
        comp.requestFocus();
      }
    } else if (textComponent.isRequestFocusEnabled()) {
      textComponent.requestFocus();
    }
  }

  /**
   * Makes the popup visible if it is hidden and makes it hidden if it is
   * visible.
   */
  protected void togglePopup() {
    if (isVisible()) {
      hide();
    } else {
      show();
    }
  }

  /**
   * Returns the first selected index; returns -1 if there is no
   * selected item.
   *
   * @return the value of <code>getMinSelectionIndex</code>
   * @see #getMinSelectionIndex
   * @see #addListSelectionListener
   */
  protected int getListSelection() {
    return list.getSelectedIndex();
  }

  /**
   * Sets the list selection index to the selectedIndex. This
   * method is used to synchronize the list selection with the
   * combo box selection.
   *
   * @param selectedIndex the index to set the list
   */
  protected void setListSelection(int selectedIndex) {
    if (selectedIndex == -1) {
      list.clearSelection();
    } else {
      list.setSelectedIndex(selectedIndex);
      list.ensureIndexIsVisible(selectedIndex);
    }
  }

  protected MouseEvent convertMouseEvent(MouseEvent e) {
    Point convertedPoint = SwingUtilities.convertPoint((Component) e.getSource(),
            e.getPoint(), list);
    MouseEvent newEvent = new MouseEvent((Component) e.getSource(),
            e.getID(),
            e.getWhen(),
            e.getModifiers(),
            convertedPoint.x,
            convertedPoint.y,
            e.getClickCount(),
            e.isPopupTrigger());
    return newEvent;
  }

  /**
   * Retrieves the height of the popup based on the current
   * ListCellRenderer and the maximum row count.
   */
  protected int getPopupHeightForRowCount(int maxRowCount) {
    // Set the cached value of the minimum row count
    int minRowCount = Math.min(maxRowCount, comboBoxModel.getSize());
    int height = 0;
    ListCellRenderer renderer = list.getCellRenderer();
    Object value = null;

    for (int i = 0; i < minRowCount; ++i) {
      value = list.getModel().getElementAt(i);
      Component c = renderer.getListCellRendererComponent(list, value, i, false, false);
      height += c.getPreferredSize().height;
    }

    return height == 0 ? 100 : height;
  }

  /**
   * Calculate the placement and size of the popup portion of the combo box based
   * on the combo box location and the enclosing screen bounds. If
   * no transformations are required, then the returned rectangle will
   * have the same values as the parameters.
   *
   * @param px starting x location
   * @param py starting y location
   * @param pw starting width
   * @param ph starting height
   * @return a rectangle which represents the placement and size of the popup
   */
  protected Rectangle computePopupBounds1(int px, int py, int pw, int ph) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Rectangle screenBounds;

    // Calculate the desktop dimensions relative to the combo box.
    GraphicsConfiguration gc = textComponent.getGraphicsConfiguration();
    Point p = new Point();
    SwingUtilities.convertPointFromScreen(p, textComponent);
    if (gc != null) {
      Insets screenInsets = toolkit.getScreenInsets(gc);
      screenBounds = gc.getBounds();
      screenBounds.width -= (screenInsets.left + screenInsets.right);
      screenBounds.height -= (screenInsets.top + screenInsets.bottom);
      screenBounds.x += (p.x + screenInsets.left);
      screenBounds.y += (p.y + screenInsets.top);
    } else {
      screenBounds = new Rectangle(p, toolkit.getScreenSize());
    }

    Rectangle rect = new Rectangle(px, py, pw, ph);
    if (py + ph > screenBounds.y + screenBounds.height && ph < screenBounds.height) {
      rect.y = -rect.height;
    }
    return rect;
  }

  /**
   * Calculate the placement and size of the popup portion of the combo box based
   * on the combo box location and the enclosing screen bounds. If
   * no transformations are required, then the returned rectangle will
   * have the same values as the parameters.
   *
   * @param px starting x location
   * @param py starting y location
   * @param pw starting width
   * @param ph starting height
   * @return a rectangle which represents the placement and size of the popup
   */
  protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Rectangle screenBounds;
    int listWidth = getList().getPreferredSize().width;
    Insets margin = getUIMargin();
    boolean isTableCellEditor = isTableCellEditor();
    boolean hasScrollBars = hasScrollBars();
    boolean isEditable = isEditable();
    boolean isSmall = isSmall();

    if (isTableCellEditor) {
      if (hasScrollBars) {
        pw = Math.max(pw, listWidth + 16);
      } else {
        pw = Math.max(pw, listWidth);
      }
    } else {
      if (hasScrollBars) {
        px += margin.left;
        pw = Math.max(pw - margin.left - margin.right, listWidth + 16);
      } else {
        if (isEditable) {
          px += margin.left;
          pw = Math.max(pw - margin.left, listWidth);
        } else {
          px += margin.left;
          pw = Math.max(pw - margin.left, listWidth);
        }
      }
    }
    // Calculate the desktop dimensions relative to the combo box.
    GraphicsConfiguration gc = textComponent.getGraphicsConfiguration();
    Point p = new Point();
    SwingUtilities.convertPointFromScreen(p, textComponent);
    if (gc != null) {
      // Get the screen insets.
      // This method will work with JDK 1.4 only. Since we want to stay
      // compatible with JDk 1.3, we use the Reflection API to access it.
      //Insets screenInsets = toolkit.getScreenInsets(gc);
      Insets screenInsets;
      try {
        screenInsets = (Insets) Toolkit.class.getMethod("getScreenInsets", new Class[]{GraphicsConfiguration.class}).invoke(toolkit, new Object[]{gc});
      } catch (Exception e) {
        //e.printStackTrace();
        screenInsets = new Insets(22, 0, 0, 0);
      }
      // Note: We must create a new rectangle here, because method
      // getBounds does not return a copy of a rectangle on J2SE 1.3.
      screenBounds = new Rectangle(gc.getBounds());
      screenBounds.width -= (screenInsets.left + screenInsets.right);
      screenBounds.height -= (screenInsets.top + screenInsets.bottom);
      screenBounds.x += screenInsets.left;
      screenBounds.y += screenInsets.top;
    } else {
      screenBounds = new Rectangle(p, toolkit.getScreenSize());
    }

    if (isDropDown()) {
      if (!isTableCellEditor) {
        if (isEditable) {
          py -= margin.bottom + 2;
        } else {
          py -= margin.bottom;
        }
      }
    } else {
      int yOffset;
      if (isTableCellEditor) {
        yOffset = 7;
      } else {
        yOffset = 3 - margin.top;
      }
      int selectedIndex = getSelectedIndex();
      if (selectedIndex <= 0) {
        py = -yOffset;
      } else {
        py = -yOffset - list.getCellBounds(0, selectedIndex - 1).height;

      }
    }

    // Compute the rectangle for the popup menu
    Rectangle rect = new Rectangle(
            px,
            Math.max(py, p.y + screenBounds.y),
            Math.min(screenBounds.width, pw),
            Math.min(screenBounds.height - 40, ph));

    // Add the preferred scroll bar width, if the popup does not fit
    // on the available rectangle.
    if (rect.height < ph) {
      rect.width += 16;
    }

    return rect;
  }

  protected Insets getUIMargin() {
    Insets margin = (Insets) textComponent.getClientProperty("Quaqua.Component.visualMargin");
    if (margin == null) {
      margin = UIManager.getInsets("Component.visualMargin");
    }
    return (Insets) margin.clone();
  }

  protected boolean isSmall() {
    return textComponent.getFont().getSize() <= 11;
  }

  private boolean isTableCellEditor() {
    return false;
  }

  private boolean isDropDown() {
    return textComponent.isEditable() || hasScrollBars();
  }

  private boolean hasScrollBars() {
    return comboBoxModel.getSize() > getMaximumRowCount();
  }

  private boolean isEditable() {
    return textComponent.isEditable();
  }

  private int getMaximumRowCount() {
    return (textComponent instanceof AutoCompleteTextComponent) ? (((AutoCompleteTextComponent) textComponent).getMaximumRowCount()) : 8;
  }

  /**
   * Calculates the upper left location of the Popup.
   */
  private Point getPopupLocation() {
    Dimension popupSize = textComponent.getSize();
    Insets insets = getInsets();

    // reduce the width of the scrollpane by the insets so that the popup
    // is the same width as the combo box.
    popupSize.setSize(popupSize.width - (insets.right + insets.left),
            getPopupHeightForRowCount(getMaximumRowCount()));
    Rectangle popupBounds = computePopupBounds(0, textComponent.getBounds().height,
            popupSize.width, popupSize.height);
    Dimension scrollSize = popupBounds.getSize();
    Point popupLocation = popupBounds.getLocation();

    scroller.setMaximumSize(scrollSize);
    scroller.setPreferredSize(scrollSize);
    scroller.setMinimumSize(scrollSize);

    //list.revalidate();
    list.invalidate();
    list.revalidate();

    return popupLocation;
  }

  /**
   * A utility method used by the event listeners.  Given a mouse event, it changes
   * the list selection to the list item below the mouse.
   */
  protected void updateListBoxSelectionForEvent(MouseEvent anEvent, boolean shouldScroll) {
    // XXX - only seems to be called from this class. shouldScroll flag is
    // never true
    Point location = anEvent.getPoint();
    if (list == null) {
      return;
    }
    int index = list.locationToIndex(location);
    if (index == -1) {
      if (location.y < 0) {
        index = 0;
      } else {
        index = comboBoxModel.getSize() - 1;
      }
    }
    if (list.getSelectedIndex() != index) {
      list.setSelectedIndex(index);
      if (shouldScroll) {
        list.ensureIndexIsVisible(index);
      }
    }
  }

  //
  // end Utility methods
  //=================================================================
}



