/*
 * JDbTextPane.java
 *
 * Created on May 14, 2009, 8:38 AM
 *
 */
package com.openitech.db.components;

/**
 *
 * @author domenbasic
 */
/*
 * @(#)JTextPane.java	1.95 06/08/08
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import com.openitech.Settings;
import com.openitech.db.FieldObserver;
import com.openitech.components.style.JTextPaneStyleFormatter;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.DocumentWeakListener;
import com.openitech.ref.events.FocusWeakListener;
import com.openitech.util.Equals;
import java.awt.*;


import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

/**
 * A text component that can be marked up with attributes that are
 * represented graphically.
 * You can find how-to information and examples of using text panes in
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/text.html">Using Text Components</a>,
 * a section in <em>The Java Tutorial.</em>
 *
 * <p>
 * This component models paragraphs
 * that are composed of runs of character level attributes.  Each
 * paragraph may have a logical style attached to it which contains
 * the default attributes to use if not overridden by attributes set
 * on the paragraph or character run.  Components and images may
 * be embedded in the flow of text.
 * <p>
 * <dl>
 * <dt><b><font size=+1>Newlines</font></b>
 * <dd>
 * For a discussion on how newlines are handled, see
 * <a href="text/DefaultEditorKit.html">DefaultEditorKit</a>.
 * </dl>
 *
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
 *   attribute: isContainer true
 * description: A text component that can be marked up with attributes that are graphically represented.
 *
 * @author  Timothy Prinzing
 * @version 1.95 08/08/06
 * @see javax.swing.text.StyledEditorKit
 */
public class JDbTextPane extends JTextPane implements DocumentListener, FieldObserver {

  static final Map<String, String> defaultEditorKitMap = new HashMap<String, String>(0);
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private final Selector selector = new Selector(this);
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  private transient FocusWeakListener focusWeakListener;

  /**
   * Creates a new <code>JDbTextPane</code>.  A new instance of
   * <code>StyledEditorKit</code> is
   * created and set, and the document model set to <code>null</code>.
   */
  public JDbTextPane() {
    super();
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

    addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (getDocumentStyleFormatter() != null) {
          getDocumentStyleFormatter().applyStyle(JDbTextPane.this);
        }
      }
    });


    Action copyAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        copy();
        System.out.println("Copy Pressed");
      }
    };
    Action pasteAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        paste();
        System.out.println("Paste Pressed");
      }
    };
    Action cutAction = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        cut();
        System.out.println("Cut Pressed");
      }
    };
    KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
    KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
    KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
    getInputMap().put(ctrlC, copyAction);
    getInputMap().put(ctrlV, pasteAction);
    getInputMap().put(ctrlX, cutAction);

  }

  /**
   * Creates a new <code>JDbTextPane</code>, with a specified document model.
   * A new instance of <code>javax.swing.text.StyledEditorKit</code>
   *  is created and set.
   *
   * @param doc the document model
   */
  public JDbTextPane(StyledDocument doc) {
    this();
    setStyledDocument(doc);
  }

  public void this_focusGained(FocusEvent e) {
    EventQueue.invokeLater(selector);
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

  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
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
    documentWeakListener.setEnabled(false);
    try {
      setText(dbFieldObserver.getValueAsText());
    } finally {
      documentWeakListener.setEnabled(true);
    }
  }

  @Override
  public void setText(String t) {
    super.setText(t);
    if (getDocumentStyleFormatter() != null) {
      getDocumentStyleFormatter().applyStyle(this);
    }
  }

  public void dataSource_toolTipFieldValueChanged(ActiveRowChangeEvent event) {
    String tip = dbFieldObserverToolTip.getValueAsText();
    if (!dbFieldObserverToolTip.wasNull() && tip.length() > 0) {
      this.setToolTipText("Pomo\u010d : " + tip);
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
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
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
  @Override
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
  @Override
  public void insertUpdate(DocumentEvent e) {
    updateColumn();
  }

  /**
   * Gives notification that an attribute or set of attributes changed.
   *
   *
   * @param e the document event
   */
  @Override
  public void changedUpdate(DocumentEvent e) {
    updateColumn();
  }

  /**
   * Associates the editor with a text document.  This
   * must be a <code>StyledDocument</code>.
   *
   * @param doc  the document to display/edit
   * @exception IllegalArgumentException  if <code>doc</code> can't
   *   be narrowed to a <code>StyledDocument</code> which is the
   *   required type of model for this text component
   */
  @Override
  public void setDocument(Document doc) {
    if (getDocument() != null && documentWeakListener != null) {
      getDocument().removeDocumentListener(documentWeakListener);
    }
    if (doc instanceof StyledDocument) {
      super.setDocument(doc);
    } else {
      throw new IllegalArgumentException("Model must be StyledDocument");
    }
    if (getDocument() != null && documentWeakListener != null) {
      getDocument().addDocumentListener(documentWeakListener);
    }
  }
  private JTextPaneStyleFormatter documentStyleFormatter;

  /**
   * Get the value of documentStyleFormatter
   *
   * @return the value of documentStyleFormatter
   */
  public JTextPaneStyleFormatter getDocumentStyleFormatter() {
    return documentStyleFormatter;
  }

  /**
   * Set the value of documentStyleFormatter
   *
   * @param documentStyleFormatter new value of documentStyleFormatter
   */
  public void setDocumentStyleFormatter(JTextPaneStyleFormatter documentStyleFormatter) {
    this.documentStyleFormatter = documentStyleFormatter;
  }
}


