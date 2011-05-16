/*
 * JDbControlButton.java
 *
 * Created on Nedelja, 16 julij 2006, 0:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.swing.JMnemonicButton;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.ref.events.ActionWeakListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 * @author uros
 */
public class JDbControlButton extends JMnemonicButton implements ActiveRowChangeListener, ActionListener {

  private transient DbDataSource dataSource;
  private transient ActiveRowChangeWeakListener activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this);
  private Operation operation = Operation.FIRST;
  private boolean useOperationIcon = true;
  private ActionListener controlActionListener;

  /** Creates a new instance of JDbControlButton */
  public JDbControlButton() {
  }

  public JDbControlButton(JDbControlButton.Operation operation) {
    this();
    setOperation(operation);
  }

  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource != null) {
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.removeActionListener(getControlActionListener());
    }
    this.dataSource = dataSource;
    if (this.dataSource != null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      this.dataSource.addActionListener(getControlActionListener());
      if (this.dataSource.canLock()) {
        if (this.dataSource.isDataLoaded() || this.dataSource.loadData()) {
          checkButton();
        }
      }
    }
  }

  private ActionListener getControlActionListener() {
    if (controlActionListener == null) {
      try {
        controlActionListener = new ActionWeakListener(this, "dbDataSource_actionPerformed"); //NOI18N
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(JDbControlButton.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return controlActionListener;
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  @Override
  public void activeRowChanged(ActiveRowChangeEvent event) {
    checkButton();
  }

  @Override
  public void fieldValueChanged(ActiveRowChangeEvent event) {
    checkButton();
  }

  public void dbDataSource_actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals(DbDataSource.UPDATING_STARTED)) {
      checkButton();
    }
  }

  private void checkButton() {
//    dataSource.lock();
//    try {
    setEnabled(operation.isEnabled(dataSource));
//    } finally {
//      dataSource.unlock();
//    }
  }

  public void setOperation(JDbControlButton.Operation operation) {
    if (this.operation != operation) {
      Icon oldValue = getIcon();
      this.operation = operation;
      if (useOperationIcon) {
        firePropertyChange(ICON_CHANGED_PROPERTY, oldValue, getIcon());
      }
    }
  }

  public JDbControlButton.Operation getOperation() {
    return operation;
  }

  @Override
  public Icon getIcon() {
    return isUseOperationIcon() ? operation.getIcon() : super.getIcon();
  }

  public void setUseOperationIcon(boolean useOperationIcon) {
    if (this.useOperationIcon != useOperationIcon) {
      Icon oldValue = getIcon();
      this.useOperationIcon = useOperationIcon;
      firePropertyChange(ICON_CHANGED_PROPERTY, oldValue, getIcon());
    }
  }

  public boolean isUseOperationIcon() {
    return useOperationIcon;
  }

  protected void fireActionPerformed(ActionEvent event) {
    operation.actionPerformed(getDataSource(), this);
    super.fireActionPerformed(event);
  }

  public static enum Operation {

    FIRST {

      Icon getIcon() {
        return ICON_FIRST;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.first();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error moving to the first record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return !dataSource.isFirst();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    PREV {

      Icon getIcon() {
        return ICON_PREV;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.previous();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error moving to the previous record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return !dataSource.isFirst();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    NEXT {

      Icon getIcon() {
        return ICON_NEXT;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.next();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error moving to the next record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return !dataSource.isLast();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    LAST {

      Icon getIcon() {
        return ICON_LAST;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.last();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error moving to the last record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return !dataSource.isLast();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    ADD {

      Icon getIcon() {
        return ICON_ADD;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.moveToInsertRow();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error adding the record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return dataSource.isCanAddRows() && !dataSource.rowUpdated();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    DELETE {

      Icon getIcon() {
        return ICON_DELETE;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null && (JOptionPane.showOptionDialog(parentComponent,
                  java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("SHOULD_DELETE_RECORD"),
                  java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("QUESTION_DELETING"),
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  new Object[]{java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("YES"), java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("NO")},
                  java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("NO")) == JOptionPane.YES_OPTION)) {
            dataSource.deleteRow();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error deleting the record.", ex); //NOI18N
          StringBuilder message = new StringBuilder();
          message.append(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("ERROR_WHILE_DELETING"));
          message.append(ex.getSQLState()).append(ex.getErrorCode()).append(" : ").append(ex.getMessage()); //NOI18N
          JOptionPane.showMessageDialog(parentComponent, message.toString(), java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("ERROR_NOTIFICATION"), JOptionPane.ERROR_MESSAGE);
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return dataSource.isCanDeleteRows() && !(dataSource.rowInserted() || dataSource.rowUpdated()) && (dataSource.getRowCount() > 0);
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    CONFIRM {

      Icon getIcon() {
        return ICON_CONFIRM;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.updateRow();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error storing the updated record.", ex); //NOI18N
          StringBuilder message = new StringBuilder();
          message.append(java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("ERROR_WHILE_STORING_CHANGES"));
          message.append(ex.getSQLState()).append(ex.getErrorCode()).append(" : ").append(ex.getMessage()); //NOI18N
          JOptionPane.showMessageDialog(parentComponent, message.toString(), java.util.ResourceBundle.getBundle("com/openitech/i18n/ResourceBundle").getString("ERROR_NOTIFICATION"), JOptionPane.ERROR_MESSAGE);
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return dataSource.rowUpdated();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    CANCEL {

      Icon getIcon() {
        return ICON_CANCEL;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        try {
          if (dataSource != null) {
            dataSource.cancelRowUpdates();
          }
        } catch (SQLException ex) {
          Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, "Error cancelling updates on the record.", ex); //NOI18N
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        try {
          return dataSource.rowUpdated();
        } catch (SQLException ex) {
          return false;
        }
      }
    },
    RELOAD {

      Icon getIcon() {
        return ICON_RELOAD;
      }

      void actionPerformed(DbDataSource dataSource, Component parentComponent) {
        if (dataSource != null) {
          dataSource.reload();
        }
      }

      boolean isEnabled(DbDataSource dataSource) {
        return dataSource != null;
      }
    };
    public final static Icon ICON_FIRST = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/2leftarrow.png")); //NOI18N
    public final static Icon ICON_PREV = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/1leftarrow.png")); //NOI18N
    public final static Icon ICON_NEXT = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/1rightarrow.png")); //NOI18N
    public final static Icon ICON_LAST = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/2rightarrow.png")); //NOI18N
    public final static Icon ICON_ADD = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/edit_add.png")); //NOI18N
    public final static Icon ICON_DELETE = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/edit_remove.png")); //NOI18N
    public final static Icon ICON_CONFIRM = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/button_ok.png")); //NOI18N
    public final static Icon ICON_CANCEL = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/button_cancel1.png")); //NOI18N
    public final static Icon ICON_RELOAD = new javax.swing.ImageIcon(Operation.class.getResource("/com/openitech/icons/reload_k.png")); //NOI18N

    abstract Icon getIcon();

    abstract void actionPerformed(DbDataSource dataSource, Component parentComponent);

    abstract boolean isEnabled(DbDataSource dataSource);
  }
}
