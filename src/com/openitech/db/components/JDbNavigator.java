/*
 * JDbNavigator.java
 *
 * Created on Sobota, 8 april 2006, 13:34
 */

package com.openitech.db.components;

import com.openitech.Settings;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.events.ActiveRowChangeWeakListener;
import com.openitech.db.model.DbDataSource;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.openitech.components.JMnemonicButton;

/**
 *
 * @author  uros
 */
public class JDbNavigator extends javax.swing.JPanel implements ActiveRowChangeListener {
  DbDataSource dataSource;

  private ActiveRowChangeWeakListener activeRowChangeWeakListener = new ActiveRowChangeWeakListener(this);
  private Component navigatorFor = null;
  private int fill = GridBagConstraints.NONE;
  private int condition = WHEN_IN_FOCUSED_WINDOW;
  private int mask = java.awt.Event.ALT_MASK;
  private GridLayout layout = new GridLayout(1,0);
  private EnumSet<JDbNavigator.Operation> buttons = EnumSet.allOf(JDbNavigator.Operation.class);
  

  /** Creates new form JDbNavigator */
  public JDbNavigator() {
    initComponents();
    layout = (GridLayout) jpInternal.getLayout();
  }
  
  public JDbNavigator(Operation... e) {
    this();
    setButtons(e);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    jpInternal = new javax.swing.JPanel();
    jbFirst = new com.openitech.components.JMnemonicButton();
    jbPrev = new com.openitech.components.JMnemonicButton();
    jbNext = new com.openitech.components.JMnemonicButton();
    jbLast = new com.openitech.components.JMnemonicButton();
    jbAdd = new com.openitech.components.JMnemonicButton();
    jbDelete = new com.openitech.components.JMnemonicButton();
    jbConfirm = new com.openitech.components.JMnemonicButton();
    jbCancel = new com.openitech.components.JMnemonicButton();
    jbReload = new com.openitech.components.JMnemonicButton();

    setLayout(new java.awt.GridBagLayout());

    setMinimumSize(new java.awt.Dimension(200, 32));
    jpInternal.setLayout(new java.awt.GridLayout(1, 0));

    jbFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/2leftarrow.png")));
    jbFirst.setMnemonic(java.awt.event.KeyEvent.VK_HOME);
    jbFirst.setEnabled(false);
    jbFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jbFirst.setMultiClickThreshhold(27L);
    jbFirst.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbFirstActionPerformed(evt);
      }
    });

    jpInternal.add(jbFirst);

    jbPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/1leftarrow.png")));
    jbPrev.setMnemonic(java.awt.event.KeyEvent.VK_LEFT);
    jbPrev.setEnabled(false);
    jbPrev.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jbPrev.setMultiClickThreshhold(27L);
    jbPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbPrevActionPerformed(evt);
      }
    });

    jpInternal.add(jbPrev);

    jbNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/1rightarrow.png")));
    jbNext.setMnemonic(java.awt.event.KeyEvent.VK_RIGHT);
    jbNext.setEnabled(false);
    jbNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jbNext.setMultiClickThreshhold(27L);
    jbNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbNextActionPerformed(evt);
      }
    });

    jpInternal.add(jbNext);

    jbLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/2rightarrow.png")));
    jbLast.setMnemonic(java.awt.event.KeyEvent.VK_END);
    jbLast.setEnabled(false);
    jbLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jbLast.setMultiClickThreshhold(27L);
    jbLast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbLastActionPerformed(evt);
      }
    });

    jpInternal.add(jbLast);

    jbAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/edit_add.png")));
    jbAdd.setMnemonic(java.awt.event.KeyEvent.VK_INSERT);
    jbAdd.setEnabled(false);
    jbAdd.setMultiClickThreshhold(27L);
    jbAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbAddActionPerformed(evt);
      }
    });

    jpInternal.add(jbAdd);

    jbDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/edit_remove.png")));
    jbDelete.setMnemonic(java.awt.event.KeyEvent.VK_DELETE);
    jbDelete.setEnabled(false);
    jbDelete.setMultiClickThreshhold(27L);
    jbDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbDeleteActionPerformed(evt);
      }
    });

    jpInternal.add(jbDelete);

    jbConfirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/button_ok.png")));
    jbConfirm.setMnemonic(java.awt.event.KeyEvent.VK_ENTER);
    jbConfirm.setEnabled(false);
    jbConfirm.setMultiClickThreshhold(27L);
    jbConfirm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbConfirmActionPerformed(evt);
      }
    });

    jpInternal.add(jbConfirm);

    jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/button_cancel1.png")));
    jbCancel.setMnemonic(java.awt.event.KeyEvent.VK_BACK_SPACE);
    jbCancel.setEnabled(false);
    jbCancel.setMultiClickThreshhold(27L);
    jbCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbCancelActionPerformed(evt);
      }
    });

    jpInternal.add(jbCancel);

    jbReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openitech/icons/reload_k.png")));
    jbReload.setMnemonic(java.awt.event.KeyEvent.VK_F5);
    jbReload.setEnabled(false);
    jbReload.setMultiClickThreshhold(27L);
    jbReload.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbReloadActionPerformed(evt);
      }
    });

    jpInternal.add(jbReload);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    add(jpInternal, gridBagConstraints);

  }// </editor-fold>//GEN-END:initComponents

  public void setFill(int fill) {
    this.fill = fill;
    invalidate();
  }

  public int getFill() {
    return fill;
  }
  
  public void setRows(int rows) {
    if (layout.getRows()!=rows) {
      layout.setRows(rows);
      this.revalidate();
    }
  }
  
  public int getRows() {
    return layout.getRows();
  }
  
  public void setColumns(int columns) {
    if (layout.getColumns()!=columns) {
      layout.setColumns(columns);
      this.revalidate();
    }
  }
  
  public int getColumns() {
    return layout.getColumns();
  }

  private void jbReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbReloadActionPerformed
    if (dataSource!=null)
      dataSource.reload();
    if (navigatorFor!=null)
        navigatorFor.requestFocus();
  }//GEN-LAST:event_jbReloadActionPerformed

  private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
    try {
      if (dataSource!=null)
        dataSource.cancelRowUpdates();
      if (navigatorFor!=null)
          navigatorFor.requestFocus();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error cancelling updates on the record.", ex);
    }
  }//GEN-LAST:event_jbCancelActionPerformed

  private void jbConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbConfirmActionPerformed
    try {
      if (dataSource!=null)
        dataSource.updateRow();
      if (navigatorFor!=null)
        navigatorFor.requestFocus();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error storing updated the record.", ex);
      StringBuffer message = new StringBuffer();
      message.append("Napaka pri potrjevanju vnosa!\n\n");
      message.append(ex.getSQLState()).append(ex.getErrorCode()).append(" : ").append(ex.getMessage());
      JOptionPane.showMessageDialog(this, message.toString(), "Napaka", JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_jbConfirmActionPerformed

  private void jbDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDeleteActionPerformed
    try {
      if (dataSource!=null && (JOptionPane.showOptionDialog(this,
              "Ali naj res zbri\u0161em zapis ?",
              "Brisanje",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              new Object[] {"Da","Ne"},
              "Ne")==JOptionPane.YES_OPTION))
        dataSource.deleteRow();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error deleting the record.", ex);
      StringBuffer message = new StringBuffer();
      message.append("Napaka pri brisanju zapisa!\n\n");
      message.append(ex.getSQLState()).append(ex.getErrorCode()).append(" : ").append(ex.getMessage());
      JOptionPane.showMessageDialog(this, message.toString(), "Napaka", JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_jbDeleteActionPerformed

  private void jbAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddActionPerformed
    try {
      if (dataSource!=null)
        dataSource.moveToInsertRow();
      if (navigatorFor!=null)
        navigatorFor.requestFocus();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error adding the record.", ex);
    }
  }//GEN-LAST:event_jbAddActionPerformed

  private void jbLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbLastActionPerformed
    try {
      if (dataSource!=null)
        dataSource.last();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error moving to the last record.", ex);
    }
  }//GEN-LAST:event_jbLastActionPerformed

  private void jbNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNextActionPerformed
    try {
      if (dataSource!=null)
        dataSource.next();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error moving to the next record.", ex);
    }
  }//GEN-LAST:event_jbNextActionPerformed

  private void jbPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPrevActionPerformed
    try {
      if (dataSource!=null)
        dataSource.previous();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error moving to the previous record.", ex);
    }

  }//GEN-LAST:event_jbPrevActionPerformed

  private void jbFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFirstActionPerformed
    try {
      if (dataSource!=null)
        dataSource.first();
    } catch (SQLException ex) {
      Logger.getLogger(Settings.LOGGER).log(Level.SEVERE, "Error moving to the first record.", ex);
    }
  }//GEN-LAST:event_jbFirstActionPerformed

  public void setDataSource(DbDataSource dataSource) {
    if (this.dataSource!=null)
      this.dataSource.removeActiveRowChangeListener(activeRowChangeWeakListener);
    this.dataSource = dataSource;
    if (this.dataSource!=null) {
      this.dataSource.addActiveRowChangeListener(activeRowChangeWeakListener);
      if (this.dataSource.isDataLoaded()||this.dataSource.loadData())
        checkButtons();
    }
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  private void checkButtons() {
    dataSource.lock();
    try {
      boolean first = this.dataSource.isFirst();
      boolean last = this.dataSource.isLast();
      boolean updating = this.dataSource.rowUpdated();
      boolean inserting = this.dataSource.rowInserted();
      boolean canAdd = this.dataSource.isCanAddRows();
      boolean canDelete = this.dataSource.isCanDeleteRows();

      jbFirst.setEnabled(!first);
      jbPrev.setEnabled(!first);
      jbNext.setEnabled(!last);
      jbLast.setEnabled(!last);
      jbAdd.setEnabled(canAdd&&!updating);
      jbDelete.setEnabled(canDelete&&!(inserting||updating)&&(this.dataSource.getRowCount()>0));
      jbConfirm.setEnabled(updating);
      jbCancel.setEnabled(updating);
      jbReload.setEnabled(this.dataSource!=null);
    } catch (SQLException ex) {
      jbFirst.setEnabled(false);
      jbPrev.setEnabled(false);
      jbNext.setEnabled(false);
      jbLast.setEnabled(false);
      jbAdd.setEnabled(false);
      jbDelete.setEnabled(false);
      jbConfirm.setEnabled(false);
      jbCancel.setEnabled(false);
      jbReload.setEnabled(false);
      Logger.getLogger(Settings.LOGGER).log(Level.WARNING, "Error setting navigator properties.", ex);
    } finally {
      dataSource.unlock();
    }
  }

  public void fieldValueChanged(ActiveRowChangeEvent event) {
    checkButtons();
  }

  public void activeRowChanged(ActiveRowChangeEvent event) {
    checkButtons();
  }

  public Component getNavigatorFor() {
    return navigatorFor;
  }

  public void setNavigatorFor(Component navigatorFor) {
    this.navigatorFor = navigatorFor;
  }

  public void setMask(int mask) {
    this.mask = mask;
    jbFirst.setMask(mask);
    jbPrev.setMask(mask);
    jbNext.setMask(mask);
    jbLast.setMask(mask);
    jbAdd.setMask(mask);
    jbDelete.setMask(mask);
    jbConfirm.setMask(mask);
    jbCancel.setMask(mask);
    jbReload.setMask(mask);
  }

  public int getMask() {
    return mask;
  }

  public void setCondition(int condition) {
    this.condition = condition;
    jbFirst.setCondition(condition);
    jbPrev.setCondition(condition);
    jbNext.setCondition(condition);
    jbLast.setCondition(condition);
    jbAdd.setCondition(condition);
    jbDelete.setCondition(condition);
    jbConfirm.setCondition(condition);
    jbCancel.setCondition(condition);
    jbReload.setCondition(condition);
  }

  public int getCondition() {
    return condition;
  }
  
  public void setButtons(Operation... e) {
    EnumSet<Operation> s = EnumSet.noneOf(Operation.class);
    jpInternal.removeAll();
    for (JDbNavigator.Operation visible:e) {
      visible.show(this);
      s.add(visible);
    }
    for (JDbNavigator.Operation hidden:EnumSet.complementOf(s)) {
      hidden.hide(this);
    }
    jpInternal.invalidate();
  }

  public EnumSet<JDbNavigator.Operation> getButtons() {
    return buttons;
  }

  public boolean delayed() {
    return false;
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.openitech.components.JMnemonicButton jbAdd;
  private com.openitech.components.JMnemonicButton jbCancel;
  private com.openitech.components.JMnemonicButton jbConfirm;
  private com.openitech.components.JMnemonicButton jbDelete;
  private com.openitech.components.JMnemonicButton jbFirst;
  private com.openitech.components.JMnemonicButton jbLast;
  private com.openitech.components.JMnemonicButton jbNext;
  private com.openitech.components.JMnemonicButton jbPrev;
  private com.openitech.components.JMnemonicButton jbReload;
  private javax.swing.JPanel jpInternal;
  // End of variables declaration//GEN-END:variables

  public static enum Operation {
    FIRST {   
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbFirst);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbFirst);
      }},
    PREV {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbPrev);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbPrev);
      }},
    NEXT {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbNext);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbNext);
      }},
    LAST {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbLast);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbLast);
      }},
    ADD {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbAdd);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbAdd);
      }},
    DELETE {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbDelete);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbDelete);
      }},
    CONFIRM {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbConfirm);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbConfirm);
      }},
    CANCEL {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbCancel);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbCancel);
      }},
    RELOAD {
      protected void show(JDbNavigator owner) {
        owner.jpInternal.add(owner.jbReload);
      }

      protected void hide(JDbNavigator owner) {
        owner.jpInternal.remove(owner.jbReload);
      }};
    protected abstract void show(JDbNavigator owner);
    protected abstract void hide(JDbNavigator owner);
  }
}