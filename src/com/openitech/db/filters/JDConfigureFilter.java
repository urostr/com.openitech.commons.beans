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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDConfigureFilter.java
 *
 * Created on 5.7.2011, 12:33:55
 */
package com.openitech.db.filters;

import com.Ostermiller.Syntax.HighlightedDocument;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.model.DbDataSource;
import com.openitech.events.concurrent.RefreshDataSource;
import com.openitech.util.ValueCheck;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author domenbasic
 */
public class JDConfigureFilter extends javax.swing.JDialog {

//  public static JDConfigureFilter instance;
//
//  public static JDConfigureFilter getInstance(){
//    if(instance == null){
//      instance = new JDConfigureFilter(null, false);
//    }
//    return instance;
//  }
  private ActiveRowChangeListener al;

  /** Creates new form JDConfigureFilter */
  public JDConfigureFilter(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();

    HighlightedDocument highlightedDocument = new HighlightedDocument();
    highlightedDocument.setHighlightStyle(HighlightedDocument.SQL_STYLE);
    jtfFilterValue.setDocument(highlightedDocument);

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();

    //Calculate the frame location
    int x = (screenSize.width - getWidth()) / 2;
    int y = (screenSize.height - getHeight()) / 2;

    //Set the new frame location
    setLocation(x, y);
  }
  protected Document document;

  /**
   * Set the value of document
   *
   * @param document new value of document
   */
  public void setDocument(Document document) {
    this.document = document;
  }
  protected DbDataSource dataSource;

  /**
   * Set the value of dataSource
   *
   * @param dataSource new value of dataSource
   */
  public void setDataSource(DbDataSource dataSource) throws SQLException {
    this.dataSource = dataSource;
    if (dataSource != null) {
      al = new ActiveRowChangeListener() {

        @Override
        public void activeRowChanged(ActiveRowChangeEvent event) {
          try {
            updateFilter();
          } catch (SQLException ex) {
            Logger.getLogger(JDConfigureFilter.class.getName()).log(Level.SEVERE, null, ex);
          }
        }

        @Override
        public void fieldValueChanged(ActiveRowChangeEvent event) {
        }
      };
      dataSource.addActiveRowChangeListener(al);

    }
  }

  private synchronized void updateFilter() throws SQLException {
    if (dataSource != null && dataSource.isDataLoaded()) {
      String[] columns = new String[dataSource.getColumnCount()];

      for (int c = 1; c <= columns.length; c++) {
        columns[c - 1] = dataSource.getColumnName(c);

      }
      jcbFields.setModel(new javax.swing.DefaultComboBoxModel(columns));
      dataSource.removeActiveRowChangeListener(al);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    jPanel1 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jcbFields = new com.openitech.db.components.JDbComboBox();
    jcbOperator = new com.openitech.db.components.JDbComboBox();
    jpCardPanel = new javax.swing.JPanel();
    jPanel6 = new javax.swing.JPanel();
    jtfSeekValue = new com.openitech.db.components.JDbTextField();
    jPanel7 = new javax.swing.JPanel();
    jtfBetweenSeekValue1 = new com.openitech.db.components.JDbTextField();
    jtfBetweenSeekValue2 = new com.openitech.db.components.JDbTextField();
    jbAND = new javax.swing.JButton();
    jbOR = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    jtfFilterValue = new javax.swing.JTextPane();
    jPanel2 = new javax.swing.JPanel();
    jbUporabiFilter = new javax.swing.JButton();
    jbZbrisiFilter = new javax.swing.JButton();
    jbPreklici = new javax.swing.JButton();

    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel3.setLayout(new java.awt.GridBagLayout());

    jPanel3.add(jcbFields, new java.awt.GridBagConstraints());

    jcbOperator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "!=", "<>", "<", ">", ">=", "<=", "LIKE", "NOT LIKE", "IN", "NOT IN", "BETWEEN", "NOT BETWEEN", "IS NULL", "IS NOT NULL" }));
    jcbOperator.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbOperatorActionPerformed(evt);
      }
    });
    jPanel3.add(jcbOperator, new java.awt.GridBagConstraints());

    jpCardPanel.setLayout(new java.awt.CardLayout());

    jPanel6.setLayout(new java.awt.GridBagLayout());

    jtfSeekValue.setColumns(10);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    jPanel6.add(jtfSeekValue, gridBagConstraints);

    jpCardPanel.add(jPanel6, "card1");

    jPanel7.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    jPanel7.add(jtfBetweenSeekValue1, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.5;
    jPanel7.add(jtfBetweenSeekValue2, gridBagConstraints);

    jpCardPanel.add(jPanel7, "card2");

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    jPanel3.add(jpCardPanel, gridBagConstraints);

    jbAND.setText("AND");
    jbAND.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbANDActionPerformed(evt);
      }
    });
    jPanel3.add(jbAND, new java.awt.GridBagConstraints());

    jbOR.setText("OR");
    jbOR.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbORActionPerformed(evt);
      }
    });
    jPanel3.add(jbOR, new java.awt.GridBagConstraints());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    jPanel1.add(jPanel3, gridBagConstraints);

    jPanel4.setLayout(new java.awt.GridBagLayout());

    jScrollPane2.setViewportView(jtfFilterValue);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel4.add(jScrollPane2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.3;
    jPanel1.add(jPanel4, gridBagConstraints);

    jPanel2.setLayout(new java.awt.GridBagLayout());

    jbUporabiFilter.setText("Uporabi filter");
    jbUporabiFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbUporabiFilterActionPerformed(evt);
      }
    });
    jPanel2.add(jbUporabiFilter, new java.awt.GridBagConstraints());

    jbZbrisiFilter.setText("Zbri�i filter");
    jbZbrisiFilter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbZbrisiFilterActionPerformed(evt);
      }
    });
    jPanel2.add(jbZbrisiFilter, new java.awt.GridBagConstraints());

    jbPreklici.setText("Preklil�i");
    jbPreklici.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbPrekliciActionPerformed(evt);
      }
    });
    jPanel2.add(jbPreklici, new java.awt.GridBagConstraints());

    jPanel1.add(jPanel2, new java.awt.GridBagConstraints());

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jbANDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbANDActionPerformed
    // TODO add your handling code here:
    updateFilterValue("AND");
  }//GEN-LAST:event_jbANDActionPerformed

  private void jbORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbORActionPerformed
    // TODO add your handling code here:
    updateFilterValue("OR");
  }//GEN-LAST:event_jbORActionPerformed

  private void jbUporabiFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbUporabiFilterActionPerformed
    try {
      // TODO add your handling code here:
      RefreshDataSource.suspend(dataSource);
      document.remove(0, document.getLength());
      document.insertString(0, jtfFilterValue.getText(), null);
      RefreshDataSource.resume(dataSource);
    } catch (BadLocationException ex) {
      Logger.getLogger(JDConfigureFilter.class.getName()).log(Level.SEVERE, null, ex);
    }
    setVisible(false);
  }//GEN-LAST:event_jbUporabiFilterActionPerformed

  private void jbZbrisiFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbZbrisiFilterActionPerformed
    // TODO add your handling code here:
    jtfFilterValue.setText(null);
    try {
      document.remove(0, document.getLength());
    } catch (BadLocationException ex) {
      Logger.getLogger(JDConfigureFilter.class.getName()).log(Level.SEVERE, null, ex);
    }
    setVisible(false);
  }//GEN-LAST:event_jbZbrisiFilterActionPerformed

  private void jbPrekliciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPrekliciActionPerformed
    // TODO add your handling code here:
    setVisible(false);
  }//GEN-LAST:event_jbPrekliciActionPerformed

  private void jcbOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbOperatorActionPerformed
    // TODO add your handling code here:
    String operator = (String) jcbOperator.getSelectedItem();
    if (operator.contains("IS")) {
      jtfSeekValue.setText(null);
      jtfSeekValue.setEnabled(false);
    } else {
      jtfSeekValue.setEnabled(true);
    }

    if (operator.contains("BETWEEN")) {
      ((CardLayout) jpCardPanel.getLayout()).show(jpCardPanel, "card2");
    } else {
      ((CardLayout) jpCardPanel.getLayout()).show(jpCardPanel, "card1");
    }
  }//GEN-LAST:event_jcbOperatorActionPerformed

  private void updateFilterValue(String operator) {
    // TODO add your handling code here:
    String fieldName = (String) jcbFields.getSelectedItem();
    String primerjalnik = (String) jcbOperator.getSelectedItem();
    String value = jtfSeekValue.getText();


    if (!primerjalnik.contains("IS") && !isNumeric(value)) {
      value = "CAST('" + value + "' AS VARCHAR(700))";
    }

    if (primerjalnik.contains("IN")) {
      value = "(" + value + ")";
    } else if (primerjalnik.contains("BETWEEN")) {
      String between1 = jtfBetweenSeekValue1.getText();
      String between2 = jtfBetweenSeekValue2.getText();
      if (!isNumeric(between1)) {
        between1 = "CAST('" + between1 + "' AS VARCHAR(700))";
      }
      if (!isNumeric(between2)) {
        between2 = "CAST('" + between2 + "' AS VARCHAR(700))";
      }
      value = between1 + " AND " + between2;
    }


    if (fieldName != null && operator != null && value != null) {
      String text = jtfFilterValue.getText();
      jtfFilterValue.setText(text + (text.length() > 0 ? ("\n" + operator + " ") : "") + fieldName + " " + primerjalnik + " " + value + " ");
    }

  }

  private boolean isNumeric(String string) {
    return ValueCheck.isNumeric(string);
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JButton jbAND;
  private javax.swing.JButton jbOR;
  private javax.swing.JButton jbPreklici;
  private javax.swing.JButton jbUporabiFilter;
  private javax.swing.JButton jbZbrisiFilter;
  private com.openitech.db.components.JDbComboBox jcbFields;
  private com.openitech.db.components.JDbComboBox jcbOperator;
  private javax.swing.JPanel jpCardPanel;
  private com.openitech.db.components.JDbTextField jtfBetweenSeekValue1;
  private com.openitech.db.components.JDbTextField jtfBetweenSeekValue2;
  private javax.swing.JTextPane jtfFilterValue;
  private com.openitech.db.components.JDbTextField jtfSeekValue;
  // End of variables declaration//GEN-END:variables
}
