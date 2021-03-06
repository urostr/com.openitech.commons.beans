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
 * JPLimitGroup.java
 *
 * Created on 26.9.2011, 16:44:08
 */
package com.openitech.db.filters;

import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author domenbasic
 */
public class JPLimitGroup extends javax.swing.JPanel {

  /** Creates new form JPLimitGroup */
  public JPLimitGroup() {
    initComponents();
  }
  private List<DataSourceLimit> parameters = new ArrayList<DataSourceLimit>();

  public void addParameter(final DataSourceLimit parameter) {
    if (!parameters.contains(parameter)) {
      this.parameters.add(parameter);
      parameter.addPropertyChangeListener("value", new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          parameter.reloadDataSources();
        }
      });

      if (parameter.isHideTop10()) {
        remove(jrbLimit10);
      }
      if (parameter.isHideTop50()) {
        remove(jrbLimit50);
      }
      if (parameter.isHideTop100()) {
        remove(jrbLimit100);
      }
      if (parameter.isHideTop1000()) {
        remove(jrbLimit1000);
      }

      if (parameter.isHideTopAll()) {
        remove(jrbLimitAll);
      }
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

    buttonGroup1 = new javax.swing.ButtonGroup();
    jrbLimit10 = new javax.swing.JRadioButton();
    jrbLimit50 = new javax.swing.JRadioButton();
    jrbLimit100 = new javax.swing.JRadioButton();
    jrbLimit1000 = new javax.swing.JRadioButton();
    jrbLimitAll = new javax.swing.JRadioButton();

    setLayout(new java.awt.GridBagLayout());

    buttonGroup1.add(jrbLimit10);
    jrbLimit10.setSelected(true);
    jrbLimit10.setText("10");
    jrbLimit10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jrbLimit10ActionPerformed(evt);
      }
    });
    add(jrbLimit10, new java.awt.GridBagConstraints());

    buttonGroup1.add(jrbLimit50);
    jrbLimit50.setText("50");
    jrbLimit50.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jrbLimit50ActionPerformed(evt);
      }
    });
    add(jrbLimit50, new java.awt.GridBagConstraints());

    buttonGroup1.add(jrbLimit100);
    jrbLimit100.setText("100");
    jrbLimit100.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jrbLimit100ActionPerformed(evt);
      }
    });
    add(jrbLimit100, new java.awt.GridBagConstraints());

    buttonGroup1.add(jrbLimit1000);
    jrbLimit1000.setText("1000");
    jrbLimit1000.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jrbLimit1000ActionPerformed(evt);
      }
    });
    add(jrbLimit1000, new java.awt.GridBagConstraints());

    buttonGroup1.add(jrbLimitAll);
    jrbLimitAll.setText("Vsi");
    jrbLimitAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jrbLimitAllActionPerformed(evt);
      }
    });
    add(jrbLimitAll, new java.awt.GridBagConstraints());
  }// </editor-fold>//GEN-END:initComponents

    private void jrbLimit10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLimit10ActionPerformed
      // TODO add your handling code here:
      for (SubstSqlParameter substSqlParameter : parameters) {
        substSqlParameter.setValue(" TOP 10 ");
      }


    }//GEN-LAST:event_jrbLimit10ActionPerformed

    private void jrbLimit50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLimit50ActionPerformed
      // TODO add your handling code here:
      for (SubstSqlParameter substSqlParameter : parameters) {
        substSqlParameter.setValue(" TOP 50 ");
      }

    }//GEN-LAST:event_jrbLimit50ActionPerformed

    private void jrbLimit100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLimit100ActionPerformed
      // TODO add your handling code here:
      for (SubstSqlParameter substSqlParameter : parameters) {
        substSqlParameter.setValue(" TOP 100 ");
      }

    }//GEN-LAST:event_jrbLimit100ActionPerformed

    private void jrbLimit1000ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLimit1000ActionPerformed
      // TODO add your handling code here:
      for (SubstSqlParameter substSqlParameter : parameters) {
        substSqlParameter.setValue(" TOP 1000 ");
      }
    }//GEN-LAST:event_jrbLimit1000ActionPerformed

    private void jrbLimitAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbLimitAllActionPerformed
      // TODO add your handling code here:
      for (SubstSqlParameter substSqlParameter : parameters) {
        substSqlParameter.setValue("  ");
      }
    }//GEN-LAST:event_jrbLimitAllActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JRadioButton jrbLimit10;
  private javax.swing.JRadioButton jrbLimit100;
  private javax.swing.JRadioButton jrbLimit1000;
  private javax.swing.JRadioButton jrbLimit50;
  private javax.swing.JRadioButton jrbLimitAll;
  // End of variables declaration//GEN-END:variables
}
