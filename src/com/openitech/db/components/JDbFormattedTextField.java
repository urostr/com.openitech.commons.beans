/*
 * JDbFormattedTextField.java
 *
 * Created on April 2, 2006, 11:37 AM
 *
 * $Revision $
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.ref.events.ActionWeakListener;
import com.openitech.ref.events.DocumentWeakListener;
import com.openitech.ref.events.FocusWeakListener;
import com.openitech.ref.events.PropertyChangeWeakListener;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author uros
 */
public class JDbFormattedTextField extends JFormattedTextField  implements DocumentListener {
  private DbFieldObserver dbFieldObserver = new DbFieldObserver();
  private DbFieldObserver dbFieldObserverToolTip = new DbFieldObserver();
  private Validator validator = null;
  private FocusLostHandler focusLostHandler = new FocusLostHandler();
  private final Selector selector = new Selector(this);
  
  private transient ActionWeakListener actionWeakListener;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener;
  private transient ActiveRowChangeWeakListener tooltipRowChangeWeakListener;
  private transient DocumentWeakListener documentWeakListener;
  //private transient PropertyChangeWeakListener propertyChangeWeakListener;
  private transient FocusWeakListener focusWeakListener;
  
  /** Creates a new instance of JDbFormattedTextField */
  public JDbFormattedTextField() {
    try {
      actionWeakListener = new ActionWeakListener(this, "dataSource_actionPerformed");
      activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_fieldValueChanged",null);
      tooltipRowChangeWeakListener = new ActiveRowChangeWeakListener(this,"dataSource_toolTipFieldValueChanged",null);
      focusWeakListener = new FocusWeakListener(this,"this_focusGained", null);
      documentWeakListener = new DocumentWeakListener(this);
      //propertyChangeWeakListener = new PropertyChangeWeakListener(this);
    } catch (NoSuchMethodException ex) {
      throw (RuntimeException) new IllegalStateException().initCause(ex);
    }
    dbFieldObserver.addActiveRowChangeListener(activeRowChangeWeakListener);
    dbFieldObserverToolTip.addActiveRowChangeListener(tooltipRowChangeWeakListener);
    this.addFocusListener(focusWeakListener);
    this.getDocument().addDocumentListener(documentWeakListener);
    //this.addPropertyChangeListener("value", propertyChangeWeakListener);
  }
  
  public void this_focusGained(FocusEvent e) {
    EventQueue.invokeLater(selector);
  }
  
  public void setFormat(Format format) {
    JFormattedTextField.AbstractFormatterFactory af;
    if (format instanceof DateFormat) {
      af = new DefaultFormatterFactory(new DateFormatter((DateFormat) format));
    } else if (format instanceof NumberFormat) {
      af = new DefaultFormatterFactory(new NumberFormatter(
              (NumberFormat)format));
    } else if (format instanceof Format) {
      af = new DefaultFormatterFactory(new InternationalFormatter(
              (Format)format));
    } else
      af = new DefaultFormatterFactory(new DefaultFormatter());
    setFormatterFactory(af);
  }
  
  public void setFormat(String mask) {
    try {
      setFormatterFactory(new DefaultFormatterFactory(new MaskFormatter(mask)));
    } catch (ParseException ex) {
      throw (IllegalArgumentException) new IllegalArgumentException().initCause(ex);
    }
  }
  
  public DbFieldObserver getDbFieldObserver() {
    return dbFieldObserver;
  }
  
  public void setFormat(Number type) {
    AbstractFormatter displayFormatter = new NumberFormatter();
    ((NumberFormatter)displayFormatter).setValueClass(type.getClass());
    AbstractFormatter editFormatter = new NumberFormatter(
            new DecimalFormat("#.#"));
    ((NumberFormatter)editFormatter).setValueClass(type.getClass());
    
    setFormatterFactory(new DefaultFormatterFactory(displayFormatter,
            displayFormatter,editFormatter));
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
  
  /**
   * FOCUS_LOST behavior implementation
   */
  private class FocusLostHandler implements Runnable,Serializable {
      public void run() {
        if (validator!=null) {
          validator.isValid(getFormatter()==null?getText():getValue());
        }
      }
  }

  public void dataSource_actionPerformed(ActionEvent event) throws ParseException {
    if (event.getActionCommand().equals("update"))
      commitEdit();
  }
  
  private Object getFieldValue(boolean update) {
    Object value = dbFieldObserver.getValueAsText();
    boolean wasNull = dbFieldObserver.wasNull();
    JFormattedTextField.AbstractFormatter formatter = getFormatter();
    if (formatter!=null) {
      if (!wasNull) {
        if ((formatter instanceof NumberFormatter)) {
          if (((String) value).length()>0) {
            if (((NumberFormat) ((NumberFormatter) formatter).getFormat()).getMaximumFractionDigits()==0)
              value = dbFieldObserver.getValueAsInt();
            else
              value = dbFieldObserver.getValueAsDouble();
          } else
            wasNull = true;
        } else if ((formatter instanceof DateFormatter) && ((String) value).length()>0)  {
          if (((String) value).length()>0) {
            value = dbFieldObserver.getValueAsDate();
          } else
            wasNull = true;
        }
      }
      if (update) {
        try {
          if (wasNull) {
            this.setText("");
          } else
            setValue(value);
        } catch(Exception ex) {
          Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Can't display the '"+dbFieldObserver.getColumnName()+"' value. ["+ex.getMessage()+"]");
        }
      }
    }
    
    return value;
  }
  
  public void dataSource_fieldValueChanged(ActiveRowChangeEvent event) {
    documentWeakListener.setEnabled(false);
    //propertyChangeWeakListener.setEnabled(false);
    
    try {
      JFormattedTextField.AbstractFormatter formatter = getFormatter();
      
      if (formatter==null) {
        this.setText(dbFieldObserver.getValueAsText());
      } else {
        getFieldValue(true);
      }
    } finally {
      //propertyChangeWeakListener.setEnabled(true);
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
      if ((validator==null)||(validator!=null&&validator.isValid(getFormatter()==null?this.getText():this.getValue())))
        dbFieldObserver.updateValue(getFormatter()==null?this.getText():this.getValue());
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Can't update the value in the dataSource.", ex);
    } finally {
      activeRowChangeWeakListener.setEnabled(true);
    }
  }
  
  private void documentUpdated() {
    if (getFormatter()==null)
      updateColumn();
    else if ((dbFieldObserver!=null)&&!dbFieldObserver.isUpdatingFieldValue()) {
      dbFieldObserver.startUpdate();
    }
    /*else
      try {
        commitEdit();
      } catch (ParseException ex) {
        Logger.getLogger(Settings.LOGGER).log(Level.INFO, "Can't update the formatted value. ["+ex.getMessage()+"]");
      }//*/
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
    documentUpdated();
  }
  
  /**
   * Gives notification that there was an insert into the document.  The
   * range given by the DocumentEvent bounds the freshly inserted region.
   *
   *
   * @param e the document event
   */
  public void insertUpdate(DocumentEvent e) {
    documentUpdated();
  }
  
  /**
   * Gives notification that an attribute or set of attributes changed.
   *
   *
   * @param e the document event
   */
  public void changedUpdate(DocumentEvent e) {
    documentUpdated();
  }
  
  private boolean sameValues(Object oldValue, Object newValue) {
    if (oldValue==null)
      return newValue==null;
    else
      return oldValue.equals(newValue);
  }
  
  /*public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("value") &&
            !sameValues(evt.getOldValue(), evt.getNewValue()))
      updateColumn();
  }//*/
  
  /**
   * Sets the <code>AbstractFormatterFactory</code>.
   * <code>AbstractFormatterFactory</code> is
   * able to return an instance of <code>AbstractFormatter</code> that is
   * used to format a value for display, as well an enforcing an editing
   * policy.
   * <p>
   * If you have not explicitly set an <code>AbstractFormatterFactory</code>
   * by way of this method (or a constructor) an
   * <code>AbstractFormatterFactory</code> and consequently an
   * <code>AbstractFormatter</code> will be used based on the
   * <code>Class</code> of the value. <code>NumberFormatter</code> will
   * be used for <code>Number</code>s, <code>DateFormatter</code> will
   * be used for <code>Dates</code>, otherwise <code>DefaultFormatter</code>
   * will be used.
   * <p>
   * This is a JavaBeans bound property.
   *
   *
   * @param tf <code>AbstractFormatterFactory</code> used to lookup
   *          instances of <code>AbstractFormatter</code>
   * @beaninfo bound: true
   *   attribute: visualUpdate true
   * description: AbstractFormatterFactory, responsible for returning an
   *              AbstractFormatter that can format the current value.
   */
  public void setFormatterFactory(JFormattedTextField.AbstractFormatterFactory tf) {
    boolean wasEnabled = documentWeakListener.isEnabled();
    try {
      documentWeakListener.setEnabled(false);
      super.setFormatterFactory(tf);
      if (getDataSource()!=null&&getColumnName()!=null) {
        dataSource_fieldValueChanged(new ActiveRowChangeEvent(getDataSource(),getColumnName(), -1));
      }
    } finally {
      documentWeakListener.setEnabled(wasEnabled);
    }
  }
  
  /**
   * Forces the current value to be taken from the
   * <code>AbstractFormatter</code> and set as the current value.
   * This has no effect if there is no current
   * <code>AbstractFormatter</code> installed.
   *
   *
   * @throws ParseException if the <code>AbstractFormatter</code> is not able
   *         to format the current value
   */
  public void commitEdit() throws ParseException {
    try {
      super.commitEdit();
    } catch (ParseException ex) {
      if (getText().length()>0) {
        Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error updating the value. ["+ex.getMessage()+"]");
        StringBuffer message = new StringBuffer();
        message.append("Napaka pri vnosu podatkov!\n");
        message.append(getText().substring(0,ex.getErrorOffset())).append("[?").append(getText().substring(ex.getErrorOffset())).append("]\n\n");
        message.append(ex.getMessage());
        JOptionPane.showMessageDialog(this, message.toString(), "Napaka", JOptionPane.ERROR_MESSAGE);
      } else
        setValue(null);
    }
    if (!sameValues(getFieldValue(false),getValue()))
      updateColumn();
  }

  protected void processFocusEvent(FocusEvent e) {
    boolean wasEnabled = documentWeakListener.isEnabled();
    try {
      documentWeakListener.setEnabled(false);
      super.processFocusEvent(e);
    } finally {
      documentWeakListener.setEnabled(wasEnabled);
    }
    EventQueue.invokeLater(focusLostHandler);
  }
  
  public Object getPendingValue() {
    try {
      AbstractFormatter format = getFormatter();

      if (format != null) {
          return format.stringToValue(getText());
      }
    } catch (ParseException ex) {
      //ignore it;
    }
    return null;
  }
}
