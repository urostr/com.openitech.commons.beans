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
 * DefaultFilterPanel.java
 *
 * Created on 14.10.2010, 12:09:13
 */
package com.openitech.db.filters;

import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.model.DbDataSource;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

/**
 *
 * @author domenbasic
 */
public class DefaultFilterPanel extends javax.swing.JPanel implements Cloneable {

  protected DataSourceFiltersMap filtersMap;
  private boolean addSearchButton = false;
  private SpinnerNumberModel spinnerModel;

  /**
   * Creates new form JPKadrovskaEvidencaFilterPanel
   */
  public DefaultFilterPanel(Map<String, Document> namedDocuments,
          DataSourceFiltersMap filtersMap) {
    this(namedDocuments, (JPDbDataSourceFilter) null, filtersMap);
  }

  public DefaultFilterPanel(Map<String, Document> documents, DefaultFilterPanel defaultFilterPanel, DataSourceFiltersMap filtersMap) {
    this(documents, defaultFilterPanel.getJPDbDataSourceFilter(), filtersMap);
  }

  /**
   * Creates new form JPKadrovskaEvidencaFilterPanel
   */
  public DefaultFilterPanel(Map<String, Document> namedDocuments, com.openitech.db.filters.JPDbDataSourceFilter parentFilterPanel,
          DataSourceFiltersMap filtersMap) {
    this.filtersMap = filtersMap;
    dbDataSourceFilter = new com.openitech.db.filters.JPDbDataSourceFilter();


    dbDataSourceFilter.setNamedDocuments(namedDocuments);
    dbDataSourceFilter.setParentFilterPanel(parentFilterPanel);
    dbDataSourceFilter.getFilters().putAll(filtersMap);

    for (DataSourceFilters dataSourceFilters : dbDataSourceFilter.getFilters().keySet()) {
      addSearchButton = addSearchButton || dataSourceFilters.isUseSearchButton();
    }
    if (addSearchButton) {
      toggleAutomaticSearch(!addSearchButton);
    }

    initComponents();
    invalidate();

    if (dbDataSourceFilter.getDataSources().isEmpty()) {
      hideTopPanel();
      invalidate();
    } else {
      for (DbDataSource dataSource : dbDataSourceFilter.getDataSources()) {
        jcbAutomaticSearch.setSelected(dataSource.isAutoReload());
        toggleAutomaticSearch(dataSource.isAutoReload());
      }
    }
    for (DbDataSource dbDataSource : dbDataSourceFilter.getDataSources()) {
      dbDataSource.addActiveRowChangeListener(new ActiveRowChangeListener() {

        @Override
        public void activeRowChanged(ActiveRowChangeEvent event) {
          event.getSource().removeActiveRowChangeListener(this);
          setLimitParameter();
        }

        @Override
        public void fieldValueChanged(ActiveRowChangeEvent event) {
        }
      });
    }

    if (dbDataSourceFilter.getFilters().isEmpty()) {
      removeAllItems();
    }
  }

  private void setLimitParameter() {
    boolean removeLimitPanel = true;
    for (DbDataSource dbDataSource : dbDataSourceFilter.getDataSources()) {
      for (Object param : dbDataSource.getParameters()) {
        if (param instanceof DataSourceLimit) {
          jPLimitGroup1.addParameter((DataSourceLimit) param);
          removeLimitPanel = false;
        }
      }
    }
    if (removeLimitPanel) {
      hideTopPanel();
      invalidate();
    }
  }

  public JPDbDataSourceFilter getJPDbDataSourceFilter() {
    return dbDataSourceFilter;
  }

  @Override
  public void setEnabled(boolean enabled) {
    setEnabled(this, enabled);
  }

  private void setEnabled(Container container, boolean enabled) {
    if (this.equals(container)) {
      super.setEnabled(enabled);
    } else {
      container.setEnabled(enabled);
    }
    for (Component component : container.getComponents()) {
      component.setEnabled(enabled);
      if (component instanceof Container) {
        setEnabled((Container) component, enabled);
      }
    }
  }

  @Override
  public Object clone() {
    return new DefaultFilterPanel(dbDataSourceFilter.getNamedDocuments(), dbDataSourceFilter, filtersMap);
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

    dbDataSourceFilter = dbDataSourceFilter==null?new com.openitech.db.filters.JPDbDataSourceFilter():dbDataSourceFilter;
    jpOptions = new javax.swing.JPanel();
    jpLimitParameter = new javax.swing.JPanel();
    jlLimit = new javax.swing.JLabel();
    jPLimitGroup1 = new com.openitech.db.filters.JPLimitGroup();
    jPanel3 = new javax.swing.JPanel();
    jpAvtomaticnoIskanje = new javax.swing.JPanel();
    jcbAutomaticSearch = new javax.swing.JCheckBox();
    jsDataSourceDelay = new javax.swing.JSpinner();
    jbIsci = new javax.swing.JButton();
    jbRefresh = new javax.swing.JButton();
    jbPocisti = new javax.swing.JButton();

    setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
    setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(dbDataSourceFilter, gridBagConstraints);

    jpOptions.setLayout(new java.awt.GridBagLayout());

    jpLimitParameter.setLayout(new java.awt.GridBagLayout());

    jlLimit.setText("Prika�i najve�:");
    jpLimitParameter.add(jlLimit, new java.awt.GridBagConstraints());
    jpLimitParameter.add(jPLimitGroup1, new java.awt.GridBagConstraints());

    jpOptions.add(jpLimitParameter, new java.awt.GridBagConstraints());

    jPanel3.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    jpOptions.add(jPanel3, gridBagConstraints);

    jpAvtomaticnoIskanje.setLayout(new java.awt.GridBagLayout());

    jcbAutomaticSearch.setSelected(!addSearchButton);
    jcbAutomaticSearch.setText("Avtomati�no iskanje");
    jcbAutomaticSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbAutomaticSearchActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jpAvtomaticnoIskanje.add(jcbAutomaticSearch, gridBagConstraints);

    jsDataSourceDelay.setModel(this.getSpinnerModel());
    jsDataSourceDelay.setToolTipText("Zamik pri avtomati�nem iskanju");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jpAvtomaticnoIskanje.add(jsDataSourceDelay, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jpOptions.add(jpAvtomaticnoIskanje, gridBagConstraints);

    jbIsci.setText("I��i");
    jbIsci.setEnabled(addSearchButton);
    jbIsci.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbIsciActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    jpOptions.add(jbIsci, gridBagConstraints);

    jbRefresh.setText("Osve�i");
    jbRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbRefreshActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    jpOptions.add(jbRefresh, gridBagConstraints);

    jbPocisti.setText("Po�isti");
    jbPocisti.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jbPocistiActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    jpOptions.add(jbPocisti, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    add(jpOptions, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

  private void jbIsciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbIsciActionPerformed
    // TODO add your handling code here:
    dbDataSourceFilter.reload(false);
  }//GEN-LAST:event_jbIsciActionPerformed

  /**
   * Get the value of spinnerModel
   *
   * @return the value of spinnerModel
   */
  public SpinnerNumberModel getSpinnerModel() {
    if (spinnerModel == null) {
      int delay = 270;

      for (DbDataSource dbDataSource : dbDataSourceFilter.getDataSources()) {
        delay = Math.min(delay, (int) dbDataSource.getQueuedDelay());
      }

      spinnerModel = new SpinnerNumberModel(delay, Math.min(100, delay), 5000, 100);
      spinnerModel.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          long queuedDelay = ((Number) ((SpinnerNumberModel) e.getSource()).getValue()).longValue();

          for (DbDataSource dbDataSource : dbDataSourceFilter.getDataSources()) {
            dbDataSource.setQueuedDelay(queuedDelay);
          }
        }
      });
    }
    return spinnerModel;
  }

  private void jcbAutomaticSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAutomaticSearchActionPerformed
    toggleAutomaticSearch(jcbAutomaticSearch.isSelected());
  }

  private void toggleAutomaticSearch(boolean autoReload) {
    for (DbDataSource dbDataSource : dbDataSourceFilter.getDataSources()) {
      dbDataSource.setAutoReload(autoReload);
    }
    if (jsDataSourceDelay != null) {
      jsDataSourceDelay.setEnabled(autoReload);
    }
    if (jbIsci != null) {
      jbIsci.setEnabled(!autoReload);
    }
  }//GEN-LAST:event_jcbAutomaticSearchActionPerformed

  private void jbPocistiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPocistiActionPerformed
    // TODO add your handling code here:
    dbDataSourceFilter.clearFilters();
    fireActionPerformed("CLEAR_FILTERS");

  }//GEN-LAST:event_jbPocistiActionPerformed

  private void jbRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRefreshActionPerformed
    // TODO add your handling code here:
    dbDataSourceFilter.reload(true);
    fireActionPerformed("RELOAD");
  }//GEN-LAST:event_jbRefreshActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.openitech.db.filters.JPDbDataSourceFilter dbDataSourceFilter;
  private com.openitech.db.filters.JPLimitGroup jPLimitGroup1;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JButton jbIsci;
  private javax.swing.JButton jbPocisti;
  private javax.swing.JButton jbRefresh;
  private javax.swing.JCheckBox jcbAutomaticSearch;
  private javax.swing.JLabel jlLimit;
  private javax.swing.JPanel jpAvtomaticnoIskanje;
  private javax.swing.JPanel jpLimitParameter;
  private javax.swing.JPanel jpOptions;
  private javax.swing.JSpinner jsDataSourceDelay;
  // End of variables declaration//GEN-END:variables
  private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

  public void addActionListener(ActionListener l) {
    this.actionListeners.add(l);
  }

  protected void fireActionPerformed(String command) {
    for (ActionListener actionListener : actionListeners) {
      actionListener.actionPerformed(new ActionEvent(this, 1, command));
    }
  }

  public void hideAutoSeekPanel() {
    jpOptions.remove(jpAvtomaticnoIskanje);
  }

  public void hideClearButton() {
    jpOptions.remove(jbPocisti);
  }

  public void hideSeekButton() {
    jpOptions.remove(jbIsci);
  }

  public void hidePanel() {
    remove(jpOptions);
  }

  public void hideReloadButton() {
    jpOptions.remove(jbRefresh);
  }

  public void hideTopPanel() {
    jpOptions.remove(jpLimitParameter);
  }

  public void removeAllItems() {
    setBorder(null);
    remove(jpOptions);
  }
}
