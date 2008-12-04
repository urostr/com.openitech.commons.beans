/*
 * JPIzbiraNaslova.java
 *
 * Created on Ponedeljek, 25 avgust 2008, 14:07
 */
package com.openitech.db.components;

import com.openitech.db.ConnectionManager;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.filters.DataSourceFilters.IntegerSeekType;
import com.openitech.db.filters.DataSourceFilters.SeekType;
import com.openitech.db.filters.FilterDocumentCaretListener;
import com.openitech.db.filters.FilterDocumentListener;
import com.openitech.db.filters.Scheduler;
import com.openitech.db.model.DbComboBoxModel.DbComboBoxEntry;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.concurrent.DataSourceEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author  uros
 */
public class JPIzbiraNaslova extends javax.swing.JPanel {

  private static final int DEFAULT_DELAY = 380;
  DbDataModel dbDataModel = new DbDataModel();
  FilterDocumentCaretListener flDsPostneStevilePostnaStevilka;
  FilterDocumentCaretListener flDsPostePostnaStevilka;
  FilterDocumentCaretListener flPosta;
  DbDataSource dataSource;
  ActionListener alDataSource;

  /**
   * Creates new form JPIzbiraNaslova
   */
  public JPIzbiraNaslova() throws SQLException {
    initComponents();

    jtfUlice.addCaretListener(new FilterDocumentCaretListener(jtfUlice.getDocument(), dbDataModel.dsUliceFilter, dbDataModel.dsUliceFilter.I_TYPE_UL_IME, DEFAULT_DELAY));
    jtfUlice.getDocument().addDocumentListener(new FilterDocumentListener(dbDataModel.dsHisneStevilkeFilter, dbDataModel.dsHisneStevilkeFilter.I_TYPE_UL_IME, DEFAULT_DELAY));
    jtfUlice.getDocument().addDocumentListener(new FilterDocumentListener(dbDataModel.dsPosteFilter, dbDataModel.dsPosteFilter.I_TYPE_UL_IME, DEFAULT_DELAY));
    jtfUlice.getDocument().addDocumentListener(new FilterDocumentListener(dbDataModel.dsPostneStevilkeFilter, dbDataModel.dsPostneStevilkeFilter.I_TYPE_UL_IME, DEFAULT_DELAY));
    jtfUlice.getDocument().addDocumentListener(new FilterDocumentListener(dbDataModel.dsNaseljaFilter, dbDataModel.dsNaseljaFilter.I_TYPE_UL_IME, DEFAULT_DELAY));

    jtfHisnaStevilka.getDocument().addDocumentListener(new HisnaFilterDocumentListener(dbDataModel.dsPosteFilter, DEFAULT_DELAY));
    jtfHisnaStevilka.getDocument().addDocumentListener(new HisnaFilterDocumentListener(dbDataModel.dsPostneStevilkeFilter, DEFAULT_DELAY));
    jtfHisnaStevilka.getDocument().addDocumentListener(new HisnaFilterDocumentListener(dbDataModel.dsNaseljaFilter, DEFAULT_DELAY));

    jtfPostnaStevilka.getDocument().addDocumentListener(new FilterDocumentListener(dbDataModel.dsNaseljaFilter, dbDataModel.dsNaseljaFilter.I_TYPE_PT_ID, DEFAULT_DELAY));

    flDsPostneStevilePostnaStevilka = new FilterDocumentCaretListener(jtfPostnaStevilka.getDocument(), dbDataModel.dsPostneStevilkeFilter, dbDataModel.dsPostneStevilkeFilter.I_TYPE_PT_ID, DEFAULT_DELAY);
    //flDsPostePostnaStevilka = new FilterDocumentCaretListener(jtfPostnaStevilka.getDocument(), dbDataModel.dsPosteFilter, dbDataModel.dsPosteFilter.I_TYPE_PT_ID, DEFAULT_DELAY);
    flPosta = new FilterDocumentCaretListener(jtfPosta.getDocument(), dbDataModel.dsPosteFilter, dbDataModel.dsPosteFilter.I_TYPE_PT_IME, DEFAULT_DELAY);

    dbDataModel.dsUlice.setReloadsOnEventQueue(false);
    dbDataModel.dsNaselja.setReloadsOnEventQueue(false);
    dbDataModel.dsHisneStevilke.setReloadsOnEventQueue(false);
    dbDataModel.dsPoste.setReloadsOnEventQueue(false);
    dbDataModel.dsPostneStevilke.setReloadsOnEventQueue(false);
  }

  public DbDataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DbDataSource dataSource) {
    removeListeners();
    this.dataSource = dataSource;

    jtfUlice.setDataSource(dataSource);
    jtfHisnaStevilka.setDataSource(dataSource);
    jtfPosta.setDataSource(dataSource);
    jtfPostnaStevilka.setDataSource(dataSource);
    jtfNaselja.setDataSource(dataSource);
    foHisnaStevilka.setDataSource(dataSource);
    foHisnaStevilkaMID.setDataSource(dataSource);
    foUlica.setDataSource(dataSource);
    foUlicaMID.setDataSource(dataSource);
    foNaselje.setDataSource(dataSource);
    foNaseljeMID.setDataSource(dataSource);
    foPostnaStevilkaMID.setDataSource(dataSource);
    foPostnaStevilka.setDataSource(dataSource);
    foPosta.setDataSource(dataSource);
    addListeners();
  }

  private class RefreshMIDs extends DataSourceEvent {

    private RefreshMIDs() {
      super(new Event(dbDataModel.dsMIDs, Event.Type.REFRESH));
    }

    private RefreshMIDs(RefreshMIDs object) {
      super(object);
    }

    @Override
    public void run() {
      try {
        Thread.sleep(JPIzbiraNaslova.DEFAULT_DELAY);
      } catch (InterruptedException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).info("Thread interrupted [refreshMIDs]");
      }
      if (timestamps.get(event).longValue() <= timestamp.longValue()) {
        if (event.isOnEventQueue()) {
          try {
            EventQueue.invokeAndWait(new Runnable() {

              public void run() {
                lockAndGet();
              }
            });
          } catch (Exception ex) {
            Logger.getLogger(JPIzbiraNaslova.class.getName()).info("Thread interrupted [refreshMIDs]");
          }
        } else {
          lockAndGet();
        }
      } else {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).fine("Skipped loading [refreshMIDs...]");
      }
    }

    @Override
    public Object clone() {
      return new RefreshMIDs(this);
    }

    private void get() {
      if (isUpdating() && !(jtfHisnaStevilka.getText().trim().length() == 0)) {
        try {
          int hs_mid = dbDataModel.getHisnaStevilkaMID(jtfUlice.getText(), jtfHisnaStevilka.getText(), jtfPostnaStevilka.getText(), jtfNaselja.getText());
          foHisnaStevilkaMID.updateValue(hs_mid == -1 ? null : hs_mid);
          if (hs_mid == -1 && jtfHisnaStevilka.getText().length()>0 && !jtfHisnaStevilka.isFocusOwner()) {
            foHisnaStevilka.updateValue(jtfHisnaStevilka.getText());
          }
          int ul_mid = dbDataModel.getUlicaMID(jtfUlice.getText(), jtfPostnaStevilka.getText(), jtfNaselja.getText());
          foUlicaMID.updateValue(ul_mid == -1 ? null : ul_mid);
          if (ul_mid == -1 && jtfUlice.getText().length()>0 && !jtfUlice.isFocusOwner()) {
            foUlica.updateValue(jtfUlice.getText());
          }
          int pt_mid = dbDataModel.getPostnaStevilkaMID(jtfPostnaStevilka.getText(), jtfPosta.getText());
          foPostnaStevilkaMID.updateValue(pt_mid == -1 ? null : pt_mid);
          if (pt_mid == -1 && jtfPostnaStevilka.getText().length()>0 && !jtfPostnaStevilka.isFocusOwner() && !jtfPosta.isFocusOwner()) {
            foPostnaStevilka.updateValue(jtfPostnaStevilka.getText());
            foPosta.updateValue(jtfPosta.getText());
          }
          int na_mid = dbDataModel.getNaseljeMID(jtfPostnaStevilka.getText(), jtfNaselja.getText());
          foNaseljeMID.updateValue(na_mid == -1 ? null : na_mid);
          if (na_mid == -1 && jtfNaselja.getText().length()>0 && !jtfNaselja.isFocusOwner()) {
            foNaselje.updateValue(jtfNaselja.getText());
          }


        } catch (SQLException ex) {
          Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    private void lockAndGet() {
      if (getDataSource() != null) {
        if (getDataSource().lock(false)) {
          try {
            get();
          } finally {
            getDataSource().unlock();
          }
        } else {
          //resubmit();
        }
      }
    }
  }

  private void getMIDs() {
    if (isUpdating()) {
      DataSourceEvent.submit(new RefreshMIDs());
    }
  }

  private boolean isUpdating() {
    if (dataSource == null) {
      return false;
    } else {
      try {
        return dataSource.isDataLoaded() && (dataSource.rowInserted() || dataSource.rowUpdated());
      } catch (SQLException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
        return false;
      }
    }
  }

  private void removeListeners() {
    if ((alDataSource != null) && (dataSource != null)) {
      dataSource.removeActionListener(alDataSource);
    }
  }

  private void addListeners() {
    if (dataSource != null) {
      if (alDataSource == null) {
        alDataSource = new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            if (dataSource != null) {
              boolean updating = false;
              try {
                updating = dataSource.isDataLoaded() && (dataSource.rowInserted() || dataSource.rowUpdated());
              } catch (SQLException ex) {
                Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
              }

              dbDataModel.disableFilters(!updating);
            }
          }
        };
      }
      dbDataModel.disableFilters(!isUpdating());
      dataSource.addActionListener(alDataSource);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    dsUlice = dbDataModel.dsUlice;
    cmUlice = new com.openitech.db.model.DbComboBoxModel();
    dsHisneStevilke = dbDataModel.dsHisneStevilke;
    cmHisneStevilke = new com.openitech.db.model.DbComboBoxModel();
    dsPostneStevilke = dbDataModel.dsPostneStevilke;
    cmPostneStevilke = new com.openitech.db.model.DbComboBoxModel();
    dsPoste = dbDataModel.dsPoste;
    cmPoste = new com.openitech.db.model.DbComboBoxModel();
    dsNaselja = dbDataModel.dsNaselja;
    cmNaselja = new com.openitech.db.model.DbComboBoxModel();
    foHisnaStevilka = new com.openitech.db.model.DbFieldObserver();
    foHisnaStevilkaMID = new com.openitech.db.model.DbFieldObserver();
    foUlica = new com.openitech.db.model.DbFieldObserver();
    foUlicaMID = new com.openitech.db.model.DbFieldObserver();
    foNaselje = new com.openitech.db.model.DbFieldObserver();
    foNaseljeMID = new com.openitech.db.model.DbFieldObserver();
    foPostnaStevilkaMID = new com.openitech.db.model.DbFieldObserver();
    foPostnaStevilka = new com.openitech.db.model.DbFieldObserver();
    foPosta = new com.openitech.db.model.DbFieldObserver();
    jPanel1 = new javax.swing.JPanel();
    jtfUlice = new com.openitech.db.components.JDbTextField();
    jtfHisnaStevilka = new com.openitech.db.components.JDbTextField();
    jtfPostnaStevilka = new com.openitech.db.components.JDbTextField();
    jtfPosta = new com.openitech.db.components.JDbTextField();
    jtfNaselja = new com.openitech.db.components.JDbTextField();

    cmUlice.setDataSource(dsUlice);
    cmUlice.setKeyColumnName("ul_uime");
    cmUlice.setValueColumnNames(new String[] {"ul_uime"});

    cmHisneStevilke.setDataSource(dsHisneStevilke);
    cmHisneStevilke.setKeyColumnName("hs_hd");
    cmHisneStevilke.setSeparator(new String[]{""});
    cmHisneStevilke.setValueColumnNames(new String[] {"hs", "hd"});

    cmPostneStevilke.setDataSource(dsPostneStevilke);
    cmPostneStevilke.setExtendedValueColumnNames(new String[] {"pt_uime"});
    cmPostneStevilke.setKeyColumnName("pt_id");
    cmPostneStevilke.setValueColumnNames(new String[] {"pt_id"});

    cmPoste.setDataSource(dsPoste);
    cmPoste.setExtendedValueColumnNames(new String[] {"pt_id"});
    cmPoste.setKeyColumnName("pt_id");
    cmPoste.setValueColumnNames(new String[] {"pt_uime"});

    cmNaselja.setDataSource(dsNaselja);
    cmNaselja.setKeyColumnName("na_uime");
    cmNaselja.setValueColumnNames(new String[] {"na_uime"});
    cmNaselja.addListDataListener(new javax.swing.event.ListDataListener() {
      public void contentsChanged(javax.swing.event.ListDataEvent evt) {
        cmNaseljaContentsChanged(evt);
      }
      public void intervalAdded(javax.swing.event.ListDataEvent evt) {
      }
      public void intervalRemoved(javax.swing.event.ListDataEvent evt) {
      }
    });

    foHisnaStevilka.addActiveRowChangeListener(new com.openitech.db.events.ActiveRowChangeListener() {
      public void fieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
        foHisnaStevilkaFieldValueChanged(evt);
      }
      public void activeRowChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
      }
    });

    foUlica.addActiveRowChangeListener(new com.openitech.db.events.ActiveRowChangeListener() {
      public void fieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
        foUlicaFieldValueChanged(evt);
      }
      public void activeRowChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
      }
    });

    foNaselje.addActiveRowChangeListener(new com.openitech.db.events.ActiveRowChangeListener() {
      public void fieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
        foNaseljeFieldValueChanged(evt);
      }
      public void activeRowChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
      }
    });

    foPostnaStevilkaMID.addActiveRowChangeListener(new com.openitech.db.events.ActiveRowChangeListener() {
      public void fieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
        foPostnaStevilkaMIDFieldValueChanged(evt);
      }
      public void activeRowChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
      }
    });

    foPosta.addActiveRowChangeListener(new com.openitech.db.events.ActiveRowChangeListener() {
      public void fieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
        foPostaFieldValueChanged(evt);
      }
      public void activeRowChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {
      }
    });

    setLayout(new java.awt.GridBagLayout());

    jPanel1.setLayout(new java.awt.GridBagLayout());

    jtfUlice.setAutoCompleteModel(cmUlice);
    jtfUlice.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        jtfUliceFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        jtfUliceFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(jtfUlice, gridBagConstraints);

    jtfHisnaStevilka.setAutoCompleteModel(cmHisneStevilke);
    jtfHisnaStevilka.setColumns(3);
    jtfHisnaStevilka.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        jtfHisnaStevilkaFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        jtfHisnaStevilkaFocusLost(evt);
      }
    });
    jPanel1.add(jtfHisnaStevilka, new java.awt.GridBagConstraints());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    add(jPanel1, gridBagConstraints);

    jtfPostnaStevilka.setAutoCompleteModel(cmPostneStevilke);
    jtfPostnaStevilka.setColumns(6);
    jtfPostnaStevilka.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jtfPostnaStevilkaItemStateChanged(evt);
      }
    });
    jtfPostnaStevilka.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        jtfPostnaStevilkaFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        jtfPostnaStevilkaFocusLost(evt);
      }
    });
    add(jtfPostnaStevilka, new java.awt.GridBagConstraints());

    jtfPosta.setAutoCompleteModel(cmPoste);
    jtfPosta.setValidator(new ValidatorPosta());
    jtfPosta.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jtfPostaItemStateChanged(evt);
      }
    });
    jtfPosta.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        jtfPostaFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        jtfPostaFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    add(jtfPosta, gridBagConstraints);

    jtfNaselja.setAutoCompleteModel(cmNaselja);
    jtfNaselja.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        jtfNaseljaFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        jtfNaseljaFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    add(jtfNaselja, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

  private void cmNaseljaContentsChanged(javax.swing.event.ListDataEvent evt) {//GEN-FIRST:event_cmNaseljaContentsChanged
    if (isUpdating()) {
      if (!jtfNaselja.isFocusOwner()) {
        jtfNaselja.setText(cmNaselja.getSize() == 1 ? cmNaselja.getElementAt(0).toString() : "");
      }
    }
  }//GEN-LAST:event_cmNaseljaContentsChanged

  private void jtfNaseljaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfNaseljaFocusLost
    dbDataModel.dsNaselja.setReloadsOnEventQueue(false);
    dbDataModel.disableFilters(!isUpdating());
  }//GEN-LAST:event_jtfNaseljaFocusLost

  private void jtfNaseljaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfNaseljaFocusGained
    dbDataModel.dsNaselja.setReloadsOnEventQueue(true);
    dbDataModel.disableFilters(false);
  }//GEN-LAST:event_jtfNaseljaFocusGained

  private void jtfUliceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfUliceFocusLost
    dbDataModel.dsUlice.setReloadsOnEventQueue(false);
    dbDataModel.disableFilters(!isUpdating());
  }//GEN-LAST:event_jtfUliceFocusLost

  private void jtfUliceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfUliceFocusGained
    dbDataModel.dsUlice.setReloadsOnEventQueue(true);
    dbDataModel.disableFilters(false);
  }//GEN-LAST:event_jtfUliceFocusGained

  private void jtfHisnaStevilkaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfHisnaStevilkaFocusLost
    dbDataModel.dsHisneStevilke.setReloadsOnEventQueue(false);
    dbDataModel.disableFilters(!isUpdating());
  }//GEN-LAST:event_jtfHisnaStevilkaFocusLost

  private void jtfHisnaStevilkaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfHisnaStevilkaFocusGained
    dbDataModel.dsHisneStevilke.setReloadsOnEventQueue(true);
    dbDataModel.disableFilters(false);
  }//GEN-LAST:event_jtfHisnaStevilkaFocusGained

  private void jtfPostaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfPostaFocusLost
    dbDataModel.dsPoste.setReloadsOnEventQueue(false);
    dbDataModel.disableFilters(!isUpdating());
  }//GEN-LAST:event_jtfPostaFocusLost

  private void jtfPostaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfPostaFocusGained
    dbDataModel.dsPoste.setReloadsOnEventQueue(true);
    dbDataModel.disableFilters(false);
  }//GEN-LAST:event_jtfPostaFocusGained

  private void jtfPostnaStevilkaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfPostnaStevilkaFocusGained
    dbDataModel.dsPostneStevilke.setReloadsOnEventQueue(true);
    dbDataModel.disableFilters(false);
  }//GEN-LAST:event_jtfPostnaStevilkaFocusGained

  private void jtfPostnaStevilkaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfPostnaStevilkaFocusLost
    dbDataModel.dsPostneStevilke.setReloadsOnEventQueue(false);
    dbDataModel.disableFilters(!isUpdating());
  //dbDataModel.dsPosteFilter.setSeekValue(dbDataModel.dsPostneStevilkeFilter.I_TYPE_PT_ID, null);
//    if (cmPostneStevilke.getSelectedItem() != null) {
//      if (cmPostneStevilke.getSelectedItem().toString().toLowerCase().equals(jtfPostnaStevilka.getText().toLowerCase())) {
//        jtfPosta.setText(((DbComboBoxEntry<Object, String>) cmPostneStevilke.getSelectedItem()).getValue("pt_uime").toString());
//        cmPoste.setSelectedItem(jtfPosta.getText());
//      }
//    }
  }//GEN-LAST:event_jtfPostnaStevilkaFocusLost

  private void foHisnaStevilkaFieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {//GEN-FIRST:event_foHisnaStevilkaFieldValueChanged
    getMIDs();
  }//GEN-LAST:event_foHisnaStevilkaFieldValueChanged

private void foUlicaFieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {//GEN-FIRST:event_foUlicaFieldValueChanged
  if (isUpdating() && jtfUlice.isFocusOwner()) {
    jtfHisnaStevilka.setText("");
  }
  getMIDs();
}//GEN-LAST:event_foUlicaFieldValueChanged

private void foNaseljeFieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {//GEN-FIRST:event_foNaseljeFieldValueChanged
  getMIDs();
}//GEN-LAST:event_foNaseljeFieldValueChanged

private void foPostnaStevilkaMIDFieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {//GEN-FIRST:event_foPostnaStevilkaMIDFieldValueChanged
  getMIDs();
}//GEN-LAST:event_foPostnaStevilkaMIDFieldValueChanged

private void jtfPostnaStevilkaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtfPostnaStevilkaItemStateChanged
  if (isUpdating()) {
    if (jtfPostnaStevilka.isFocusOwner() && (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) && cmPostneStevilke.getSelectedItem() != null) {
      jtfPosta.setText(((DbComboBoxEntry<Object, String>) cmPostneStevilke.getSelectedItem()).getValue("pt_uime").toString());
    }
  }
}//GEN-LAST:event_jtfPostnaStevilkaItemStateChanged

private void jtfPostaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jtfPostaItemStateChanged
  if (isUpdating()) {
    if (jtfPosta.isFocusOwner() && (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) && cmPoste.getSelectedItem() != null) {
      jtfPostnaStevilka.setText(((DbComboBoxEntry<Object, String>) cmPoste.getSelectedItem()).getValue("pt_id").toString());
    }
  }
}//GEN-LAST:event_jtfPostaItemStateChanged

private void foPostaFieldValueChanged(com.openitech.db.events.ActiveRowChangeEvent evt) {//GEN-FIRST:event_foPostaFieldValueChanged
// TODO add your handling code here:
  getMIDs();
}//GEN-LAST:event_foPostaFieldValueChanged

  private class ValidatorPosta implements Validator {

    public boolean isValid(Object value) {
      if (isUpdating()) {
        if (cmPostneStevilke.getSelectedItem() == null || ((DbComboBoxEntry<Object, String>) cmPostneStevilke.getSelectedItem()).getValue("pt_uime").toString().equalsIgnoreCase(value.toString())) {
          return true;
        } else {
          return false;
        }
      } else {
        return true;
      }
    }

    public void displayMessage() {
      DbComboBoxEntry<Object, String> entry = (DbComboBoxEntry<Object, String>) cmPostneStevilke.getSelectedItem();
      JOptionPane.showMessageDialog(jtfPosta, "Pošta ni veljavna.\nPoštna številka "+entry.getKey().toString()+" pripada pošti "+entry.getValue("pt_uime").toString()+".", "Napaka", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static class HisnaFilterDocumentListener extends FilterDocumentListener {

    final static Pattern pattern = Pattern.compile("(^(\\d*)(\\s*)(.*)$)");

    public HisnaFilterDocumentListener(DbDataModel.DsFilterRPE filter) {
      super(filter, null, Scheduler.DELAY);
    }

    public HisnaFilterDocumentListener(DbDataModel.DsFilterRPE filter, long delay) {
      super(filter, null, delay);
    }

    protected void setSeekValue(final DocumentEvent e) {
      String text = getText(e);
      Integer hs = null;
      String hd = null;

      if ((text != null) && (text.length() > 0)) {
        text = text.trim();
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
          if (matcher.group(2) != null && matcher.group(2).length() > 0) {
            hs = Integer.parseInt(matcher.group(2));
          }
          if (matcher.group(4) != null && matcher.group(4).length() > 0) {
            hd = matcher.group(4);
          }
        }
      }

      schedule(new SeekValueUpdateRunnable<Integer>(filter, ((DbDataModel.DsFilterRPE) filter).I_TYPE_HS, hs));
      schedule(new SeekValueUpdateRunnable<String>(filter, ((DbDataModel.DsFilterRPE) filter).I_TYPE_HD, hd));
    }
  }
  /**
   * Holds value of property cnUlicaMID.
   */
  private String cnUlicaMID;

  /**
   * Getter for property cnUlicaMID.
   * @return Value of property cnUlicaMID.
   */
  public String getCnUlicaMID() {
    return this.cnUlicaMID;
  }

  /**
   * Setter for property cnUlicaMID.
   * @param cnUlicaMID New value of property cnUlicaMID.
   */
  public void setCnUlicaMID(String cnUlicaMID) {
    this.cnUlicaMID = cnUlicaMID;
    foUlicaMID.setColumnName(cnUlicaMID);
  }
  /**
   * Holds value of property cnUlica.
   */
  private String cnUlica;

  /**
   * Getter for property cnUlica.
   * @return Value of property cnUlica.
   */
  public String getCnUlica() {
    return this.cnUlica;
  }

  /**
   * Setter for property cnUlica.
   * @param cnUlica New value of property cnUlica.
   */
  public void setCnUlica(String cnUlica) {
    this.cnUlica = cnUlica;
    foUlica.setColumnName(cnUlica);
    jtfUlice.setColumnName(cnUlica);
  }
  /**
   * Holds value of property cnHisnaStevilkaMID.
   */
  private String cnHisnaStevilkaMID;

  /**
   * Getter for property cnHisnaStevilkaMID.
   * @return Value of property cnHisnaStevilkaMID.
   */
  public String getCnHisnaStevilkaMID() {
    return this.cnHisnaStevilkaMID;
  }

  /**
   * Setter for property cnHisnaStevilkaMID.
   * @param cnHisnaStevilkaMID New value of property cnHisnaStevilkaMID.
   */
  public void setCnHisnaStevilkaMID(String cnHisnaStevilkaMID) {
    this.cnHisnaStevilkaMID = cnHisnaStevilkaMID;
    foHisnaStevilkaMID.setColumnName(cnHisnaStevilkaMID);
  }
  /**
   * Holds value of property cnHisnaStevilka.
   */
  private String cnHisnaStevilka;

  /**
   * Getter for property cnHisnaStevilka.
   * @return Value of property cnHisnaStevilka.
   */
  public String getCnHisnaStevilka() {
    return this.cnHisnaStevilka;
  }

  /**
   * Setter for property cnHisnaStevilka.
   * @param cnHisnaStevilka New value of property cnHisnaStevilka.
   */
  public void setCnHisnaStevilka(String cnHisnaStevilka) {
    this.cnHisnaStevilka = cnHisnaStevilka;
    foHisnaStevilka.setColumnName(cnHisnaStevilka);
    jtfHisnaStevilka.setColumnName(cnHisnaStevilka);
  }
  //-------------------
  private String cnPostnaStevilkaMID;

  /**
   * Getter for property cnHisnaStevilkaMID.
   * @return Value of property cnHisnaStevilkaMID.
   */
  public String getCnPostnaStevilkaMID() {
    return this.cnPostnaStevilkaMID;
  }

  /**
   * Setter for property cnHisnaStevilkaMID.
   * @param cnHisnaStevilkaMID New value of property cnHisnaStevilkaMID.
   */
  public void setCnPostnaStevilkaMID(String cnPostnaStevilkaMID) {
    this.cnPostnaStevilkaMID = cnPostnaStevilkaMID;
    foPostnaStevilkaMID.setColumnName(cnPostnaStevilkaMID);
  }
  /**
   * Holds value of property cnHisnaStevilka.
   */
  private String cnPostnaStevilka;

  /**
   * Getter for property cnHisnaStevilka.
   * @return Value of property cnHisnaStevilka.
   */
  public String getCnPostnaStevilka() {
    return this.cnPostnaStevilka;
  }

  /**
   * Setter for property cnHisnaStevilka.
   * @param cnHisnaStevilka New value of property cnHisnaStevilka.
   */
  public void setCnPostnaStevilka(String cnPostnaStevilka) {
    this.cnPostnaStevilka = cnPostnaStevilka;
    foPostnaStevilka.setColumnName(cnPostnaStevilka);
    jtfPostnaStevilka.setColumnName(cnPostnaStevilka);
  }
  //----------
  /**
   * Holds value of property cnHisnaStevilka.
   */
  private String cnPosta;

  /**
   * Getter for property cnHisnaStevilka.
   * @return Value of property cnHisnaStevilka.
   */
  public String getCnPosta() {
    return this.cnPosta;
  }

  /**
   * Setter for property cnHisnaStevilka.
   * @param cnHisnaStevilka New value of property cnHisnaStevilka.
   */
  public void setCnPosta(String cnPosta) {
    this.cnPosta = cnPosta;
    foPosta.setColumnName(cnPosta);
    jtfPosta.setColumnName(cnPosta);
  }
  //----------
  /**
   * Getter for property cnPostnaStevilka.
   * @return Value of property cnPostnaStevilka.
   */
  /**
   * Holds value of property cnNaseljeMID.
   */
  private String cnNaseljeMID;

  /**
   * Getter for property cnNaseljeMID.
   * @return Value of property cnNaseljeMID.
   */
  public String getCnNaseljeMID() {
    return this.cnNaseljeMID;
  }

  /**
   * Setter for property cnNaseljeMID.
   * @param cnNaseljeMID New value of property cnNaseljeMID.
   */
  public void setCnNaseljeMID(String cnNaseljeMID) {
    this.cnNaseljeMID = cnNaseljeMID;
    foNaseljeMID.setColumnName(cnNaseljeMID);

  }
  /**
   * Holds value of property cnNaselje.
   */
  private String cnNaselje;

  /**
   * Getter for property cnNaselje.
   * @return Value of property cnNaselje.
   */
  public String getCnNaselje() {
    return this.cnNaselje;
  }

  /**
   * Setter for property cnNaselje.
   * @param cnNaselje New value of property cnNaselje.
   */
  public void setCnNaselje(String cnNaselje) {
    this.cnNaselje = cnNaselje;
    foNaselje.setColumnName(cnNaselje);
    jtfNaselja.setColumnName(cnNaselje);
  }

  public void clear() {
    try {
      jtfHisnaStevilka.setText("");
      jtfUlice.setText("");
      jtfPostnaStevilka.setText("");
      jtfPosta.setText("");
      jtfNaselja.setText("");
      foHisnaStevilkaMID.updateValue(null);
      foUlicaMID.updateValue(null);
      foPostnaStevilkaMID.updateValue(null);
      foNaseljeMID.updateValue(null);
    } catch (SQLException ex) {
      Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.openitech.db.model.DbComboBoxModel cmHisneStevilke;
  private com.openitech.db.model.DbComboBoxModel cmNaselja;
  private com.openitech.db.model.DbComboBoxModel cmPoste;
  private com.openitech.db.model.DbComboBoxModel cmPostneStevilke;
  private com.openitech.db.model.DbComboBoxModel cmUlice;
  private com.openitech.db.model.DbDataSource dsHisneStevilke;
  private com.openitech.db.model.DbDataSource dsNaselja;
  private com.openitech.db.model.DbDataSource dsPoste;
  private com.openitech.db.model.DbDataSource dsPostneStevilke;
  private com.openitech.db.model.DbDataSource dsUlice;
  private com.openitech.db.model.DbFieldObserver foHisnaStevilka;
  private com.openitech.db.model.DbFieldObserver foHisnaStevilkaMID;
  private com.openitech.db.model.DbFieldObserver foNaselje;
  private com.openitech.db.model.DbFieldObserver foNaseljeMID;
  private com.openitech.db.model.DbFieldObserver foPosta;
  private com.openitech.db.model.DbFieldObserver foPostnaStevilka;
  private com.openitech.db.model.DbFieldObserver foPostnaStevilkaMID;
  private com.openitech.db.model.DbFieldObserver foUlica;
  private com.openitech.db.model.DbFieldObserver foUlicaMID;
  private javax.swing.JPanel jPanel1;
  private com.openitech.db.components.JDbTextField jtfHisnaStevilka;
  private com.openitech.db.components.JDbTextField jtfNaselja;
  private com.openitech.db.components.JDbTextField jtfPosta;
  private com.openitech.db.components.JDbTextField jtfPostnaStevilka;
  private com.openitech.db.components.JDbTextField jtfUlice;
  // End of variables declaration//GEN-END:variables

  private static class DbDataModel {

    public final DbDataSource dsUlice = new DbDataSource();
    public final DsFilterRPE dsUliceFilter = new DsFilterRPE("<%filter_ulice%>");
    public final DbDataSource dsHisneStevilke = new DbDataSource();
    public final DsFilterRPE dsHisneStevilkeFilter = new DsFilterRPE("<%filter_hs%>");
    public final DbDataSource dsPoste = new DbDataSource();
    public final DsFilterRPE dsPosteFilter = new DsFilterRPE("<%filter_pt%>");
    public final DbDataSource dsPostneStevilke = new DbDataSource();
    public final DsFilterRPE dsPostneStevilkeFilter = new DsFilterRPE("<%filter_pt%>");
    public final DbDataSource dsNaselja = new DbDataSource();
    public final DsFilterRPE dsNaseljaFilter = new DsFilterRPE("<%filter_na%>");
    public final DbDataSource dsMIDs = new DbDataSource();
    private PreparedStatement findHS_MID_1 = null;
    private PreparedStatement findHS_MID_2 = null;
    private PreparedStatement findHS_MID_3 = null;
    private PreparedStatement findUL_MID = null;
    private PreparedStatement findPT_MID = null;
    private PreparedStatement findNA_MID = null;

    private static String getDialect() {
      String dialect = "";
      try {
        String url = ConnectionManager.getInstance().getProperty("db.jdbc.net").toLowerCase();
        if (url.startsWith("jdbc:jtds:sqlserver:")) {
          dialect = "mssql/";
        }
      } catch (NullPointerException ex) {
        //ignore
      }
      return dialect;
    }

    private DbDataModel() throws SQLException {
      String dialect = getDialect();
      java.sql.Connection connection = ConnectionManager.getInstance().getConnection();

      try {
        dsUliceFilter.setFilterRequired(true);
        dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_UL_IME);
        dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_NA_IME);
        dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_PT_IME, 2);
        dsUliceFilter.addRequired(dsUliceFilter.I_TYPE_PT_ID, 2);
        dsUliceFilter.setOperator("and");

        dsUlice.setCanAddRows(false);
        dsUlice.setCanDeleteRows(false);
        dsUlice.setReadOnly(true);
        dsUlice.setName("ulice");

        java.util.List parameters = new java.util.ArrayList();
        parameters.add(dsUliceFilter);
        parameters.add(dsUliceFilter);

        dsUlice.setParameters(parameters);
        dsUlice.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ulic_c.sql", "cp1250"));
        dsUlice.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ulic.sql", "cp1250"));
        dsUlice.setFetchSize(108);
//        dsUlice.setQueuedDelay(108);

        dsUliceFilter.addDataSource(dsUlice);

        dsHisneStevilkeFilter.setFilterRequired(true);
        dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_UL_IME);
        dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_NA_IME);
        dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_PT_IME, 2);
        dsHisneStevilkeFilter.addRequired(dsHisneStevilkeFilter.I_TYPE_PT_ID, 2);
        dsHisneStevilkeFilter.setOperator("and");

        dsHisneStevilke.setCanAddRows(false);
        dsHisneStevilke.setCanDeleteRows(false);
        dsHisneStevilke.setReadOnly(true);
        dsHisneStevilke.setName("hisne_stevilke");

        parameters = new java.util.ArrayList();
        parameters.add(dsHisneStevilkeFilter);
        parameters.add(dsHisneStevilkeFilter);

        dsHisneStevilke.setParameters(parameters);
        dsHisneStevilke.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_hs_c.sql", "cp1250"));
        dsHisneStevilke.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_hs.sql", "cp1250"));
        dsHisneStevilke.setFetchSize(108);
        //       dsHisneStevilke.setQueuedDelay(50);

        dsHisneStevilkeFilter.addDataSource(dsHisneStevilke);

        dsPosteFilter.setFilterRequired(true);
        dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_UL_IME);
        dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_NA_IME);
        dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_PT_IME);
        dsPosteFilter.addRequired(dsPosteFilter.I_TYPE_PT_ID);
        dsPosteFilter.setOperator("and");

        dsPoste.setCanAddRows(false);
        dsPoste.setCanDeleteRows(false);
        dsPoste.setReadOnly(true);
        dsPoste.setName("poste");

        parameters = new java.util.ArrayList();
        parameters.add(dsPosteFilter);
        parameters.add(dsPosteFilter);

        dsPoste.setParameters(parameters);
        dsPoste.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt_c.sql", "cp1250"));
        dsPoste.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt.sql", "cp1250") + "ORDER BY pt_ime");
        dsPoste.setFetchSize(108);
        //      dsPoste.setQueuedDelay(54);
        dsPosteFilter.addDataSource(dsPoste);

        dsPostneStevilkeFilter.setFilterRequired(true);
        dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_UL_IME);
        dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_NA_IME);
        dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_PT_IME);
        dsPostneStevilkeFilter.addRequired(dsPostneStevilkeFilter.I_TYPE_PT_ID);
        dsPostneStevilkeFilter.setOperator("and");

        dsPostneStevilke.setCanAddRows(false);
        dsPostneStevilke.setCanDeleteRows(false);
        dsPostneStevilke.setReadOnly(true);
        dsPostneStevilke.setName("postne_stevilke");

        parameters = new java.util.ArrayList();
        parameters.add(dsPostneStevilkeFilter);
        parameters.add(dsPostneStevilkeFilter);

        dsPostneStevilke.setParameters(parameters);
        dsPostneStevilke.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt_c.sql", "cp1250"));
        dsPostneStevilke.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_pt.sql", "cp1250") + "ORDER BY pt_id");
        dsPostneStevilke.setFetchSize(108);
        //      dsPostneStevilke.setQueuedDelay(54);

        dsPostneStevilkeFilter.addDataSource(dsPostneStevilke);

        dsNaseljaFilter.setFilterRequired(true);
        dsNaseljaFilter.addRequired(dsNaseljaFilter.I_TYPE_UL_IME);
        dsNaseljaFilter.setOperator("and");

        dsNaselja.setCanAddRows(false);
        dsNaselja.setCanDeleteRows(false);
        dsNaselja.setReadOnly(true);
        dsNaselja.setName("naselja");

        parameters = new java.util.ArrayList();
        parameters.add(dsNaseljaFilter);
        parameters.add(dsNaseljaFilter);

        dsNaselja.setParameters(parameters);
        dsNaselja.setCountSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ns_c.sql", "cp1250"));
        dsNaselja.setSelectSql(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "sifrant_ns.sql", "cp1250") + "ORDER BY na_ime");
        dsNaselja.setFetchSize(108);
//        dsNaselja.setQueuedDelay(54);

        dsNaseljaFilter.addDataSource(dsNaselja);

        findHS_MID_1 = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_1.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        findHS_MID_2 = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_2.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        findHS_MID_3 = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_hs_mid_3.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        findUL_MID = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_ul_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        findPT_MID = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_pt_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        findNA_MID = connection.prepareStatement(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + dialect + "find_na_mid.sql", "cp1250"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

      } catch (NullPointerException ex) {
        if (dialect.length() > 0) {
          throw (IllegalStateException) (new IllegalStateException("Napaka pri inicializaciji podatkovnega modela").initCause(ex));
        }
      }
    }

    public void disableFilters(boolean disable) {

      dsHisneStevilkeFilter.setDisabled(disable);
      dsUliceFilter.setDisabled(disable);
      dsNaseljaFilter.setDisabled(disable);
      dsPosteFilter.setDisabled(disable);
      dsPostneStevilkeFilter.setDisabled(disable);

    }

    public int getHisnaStevilkaMID(String ul_ime, String hs_hd, String pt_id, String na_ime) {
      int result = -1;

      ul_ime = ul_ime == null ? null : ul_ime.toUpperCase();
      na_ime = na_ime == null ? null : na_ime.toUpperCase();
      hs_hd = hs_hd == null ? null : hs_hd.replaceAll(" ", "");
      pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
      long timer;

      try {

        int param = 1;

        findHS_MID_1.clearParameters();

        findHS_MID_1.setString(param++, hs_hd);
        findHS_MID_1.setString(param++, ul_ime);

        timer = System.currentTimeMillis();
        ResultSet rsHS_MID = findHS_MID_1.executeQuery();
        System.out.println("izbiranaslova:findHS_MID_1: " + (System.currentTimeMillis() - timer) + "ms");

        if (rsHS_MID.next() && rsHS_MID.isLast()) {
          result = rsHS_MID.getInt(1);
        }
        rsHS_MID.close();

        if (result == -1) {
          param = 1;

          findHS_MID_2.clearParameters();

          findHS_MID_2.setString(param++, hs_hd);
          findHS_MID_2.setString(param++, ul_ime);
          findHS_MID_2.setObject(param++, pt_id, java.sql.Types.INTEGER);

          timer = System.currentTimeMillis();
          ResultSet rsHS_MID_2 = findHS_MID_2.executeQuery();
          System.out.println("izbiranaslova:findHS_MID_2: " + (System.currentTimeMillis() - timer) + "ms");

          if (rsHS_MID_2.next() && rsHS_MID_2.isLast()) {
            result = rsHS_MID_2.getInt(1);
          }
          rsHS_MID_2.close();
        }
        if (result == -1) {

          param = 1;

          findHS_MID_3.clearParameters();

          findHS_MID_3.setString(param++, hs_hd);
          findHS_MID_3.setString(param++, ul_ime);
          findHS_MID_3.setObject(param++, pt_id, java.sql.Types.INTEGER);
          findHS_MID_3.setString(param++, na_ime);


          timer = System.currentTimeMillis();
          ResultSet rsHS_MID_3 = findHS_MID_3.executeQuery();
          System.out.println("izbiranaslova:findHS_MID_3: " + (System.currentTimeMillis() - timer) + "ms");

          if (rsHS_MID_3.next() && rsHS_MID_3.isLast()) {
            result = rsHS_MID_3.getInt(1);
          }
          rsHS_MID_3.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
      }

      return result;
    }

    public int getUlicaMID(String ul_ime, String pt_id, String na_ime) {
      int result = -1;
      long timer;

      ul_ime = ul_ime == null ? null : ul_ime.toUpperCase();
      na_ime = na_ime == null ? null : na_ime.toUpperCase();
      pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;


      try {
        int param = 1;

        findUL_MID.clearParameters();


        findUL_MID.setString(param++, ul_ime);
        findUL_MID.setObject(param++, pt_id, java.sql.Types.INTEGER);
        findUL_MID.setString(param++, na_ime);

        timer = System.currentTimeMillis();
        ResultSet rsUL_MID = findUL_MID.executeQuery();
        System.out.println("izbiranaslova:findUL_MID: " + (System.currentTimeMillis() - timer) + "ms");



        if (rsUL_MID.next()) {
          result = rsUL_MID.getInt(1);
        }

      } catch (SQLException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
      }

      return result;
    }

    public int getPostnaStevilkaMID(String pt_id, String pt_ime) {
      int result = -1;
      long timer;

      pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
      pt_ime = pt_ime == null ? null : pt_ime.toUpperCase();


      try {
        int param = 1;

        findPT_MID.clearParameters();


        findPT_MID.setObject(param++, pt_id, java.sql.Types.INTEGER);
        findPT_MID.setString(param++, pt_ime);

        timer = System.currentTimeMillis();
        ResultSet rsPT_MID = findPT_MID.executeQuery();
        System.out.println("izbiranaslova:findPT_MID: " + (System.currentTimeMillis() - timer) + "ms");



        if (rsPT_MID.next() && rsPT_MID.isLast()) {
          result = rsPT_MID.getInt(1);
        }

      } catch (SQLException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
      }

      return result;
    }

    public int getNaseljeMID(String pt_id, String na_ime) {
      int result = -1;
      long timer;

      pt_id = pt_id == null || pt_id.length() == 0 ? null : pt_id;
      na_ime = na_ime == null ? null : na_ime.toUpperCase();

      try {
        int param = 1;

        findNA_MID.clearParameters();
        findNA_MID.setObject(param++, pt_id, java.sql.Types.INTEGER);
        findNA_MID.setString(param++, na_ime);

        timer = System.currentTimeMillis();
        ResultSet rsNA_MID = findNA_MID.executeQuery();
        System.out.println("izbiranaslova:findNA_MID: " + (System.currentTimeMillis() - timer) + "ms");



        if (rsNA_MID.next() && rsNA_MID.isLast()) {
          result = rsNA_MID.getInt(1);
        }

      } catch (SQLException ex) {
        Logger.getLogger(JPIzbiraNaslova.class.getName()).log(Level.SEVERE, null, ex);
      }

      return result;
    }

    private static class DsFilterRPE extends DataSourceFilters {

      public final SeekType I_TYPE_UL_IME = new SeekType("ul_ime", 8, 2);
      public final SeekType I_TYPE_NA_IME = new SeekType("na_ime", 8, 1);
      public final SeekType I_TYPE_PT_IME = new SeekType("pt_ime", 8, 1);
      public final SeekType I_TYPE_PT_ID = DbDataModel.getDialect().length() == 0 ? new SeekType("CAST(pt_id as varchar) like (?+'%')", 7, 1) : new SeekType(com.openitech.util.ReadInputStream.getResourceAsString(getClass(), "sql/" + DbDataModel.getDialect() + "fragment_filter_pt_id.sql", "cp1250"), 7, 1);
      public final IntegerSeekType I_TYPE_HS = new IntegerSeekType("hs", 4);
      public final SeekType I_TYPE_HD = new SeekType("hd", 4, 1);

      public DsFilterRPE(String replace) {
        super(replace);
      }
    }
  }
}
