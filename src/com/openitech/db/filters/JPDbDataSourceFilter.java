/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPDbDataSourceFilter.java
 *
 * Created on 13.1.2009, 9:31:02
 */
package com.openitech.db.filters;

import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.formats.FormatFactory;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;

/**
 *
 * @author uros
 */
public class JPDbDataSourceFilter extends javax.swing.JPanel implements ActiveFiltersReader {

  private final DataSourceFiltersMap filters = new DataSourceFiltersMap();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document[]> documents = new HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document[]>();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, DbComboBoxModel> sifranti = new HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, DbComboBoxModel>();

  /** Creates new form JPDbDataSourceFilter */
  public JPDbDataSourceFilter() {
    initComponents();

    filters.addPropertyChangeListener("model", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        updateColumns();
      }
    });

    filters.addPropertyChangeListener("clear", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
          entry.getKey().removePropertyChangeListener(queryChanged);
        }
        ;
      }
    });

    filters.addPropertyChangeListener("removed", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getOldValue() instanceof DataSourceFilters) {
          ((DataSourceFilters) evt.getOldValue()).removePropertyChangeListener(queryChanged);
        }
      }
    });
  }

  public DataSourceFiltersMap getFilters() {
    return filters;
  }
  private JMenu filterMenuItem = new JMenu("Aktivni filtri");

  public JMenu getFilterMenuItem() {
    updateFilterMenuItem();
    return filterMenuItem;
  }

  private void updateFilterMenuItem() {
    filterMenuItem.removeAll();
    for (Map.Entry<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document[]> entry : documents.entrySet()) {
      if ((entry.getValue()[0].getLength() > 0) || ((entry.getValue().length == 2) && (entry.getValue()[1].getLength() > 0))) {
        DataSourceFilters.AbstractSeekType<? extends Object> seekType = entry.getKey();
//        if (seekType.isAutomatic()) {
        JCheckBoxMenuItem miCheckbox = new JCheckBoxMenuItem(seekType.getDescription(), true);
        miCheckbox.setActionCommand("CLEAR");
        miCheckbox.addActionListener(new ToggleFilter(seekType));
        filterMenuItem.add(miCheckbox);
//        } else {
//          JMenuItem miFilter = new JMenuItem(seekType.getDescription());
//          filterMenuItem.add(miFilter);
//        }

      }
    }
    firePropertyChange("filter_menu", filterMenuItem, null);
  }

  @Override
  public DataSourceFilters getActiveFilter() {
    return null;
  }

  private class ToggleFilter implements ActionListener {

    DataSourceFilters.AbstractSeekType<? extends Object> seekType;

    public ToggleFilter(DataSourceFilters.AbstractSeekType<? extends Object> seekType) {
      this.seekType = seekType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equalsIgnoreCase("CLEAR") && documents.containsKey(seekType)) {
        for (javax.swing.text.Document document : documents.get(seekType)) {
          try {
            document.remove(0, document.getLength());
          } catch (BadLocationException ex) {
            Logger.getLogger(JPDbDataSourceFilter.class.getName()).log(Level.WARNING, null, ex);
          }
        }
      }
    }
  }
  private final PropertyChangeListener queryChanged = new PropertyChangeListener() {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      updateFilterMenuItem();
    }
  };

  private void updateColumns() {
    Vector<DataSourceFilters.AbstractSeekType<? extends Object>> headers = new Vector<DataSourceFilters.AbstractSeekType<? extends Object>>();

    documents.clear();
    for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
      java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

      for (int i = 0; i < seekTypeList.size(); i++) {
        DataSourceFilters.AbstractSeekType<? extends Object> item = seekTypeList.get(i);


        if (!item.isAutomatic()) {
          documents.put(item, item.getDocuments());
        } else {
          headers.add(item);

          if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            javax.swing.text.Document from = new com.openitech.db.components.JDbDateTextField().getDocument();
            javax.swing.text.Document to = new com.openitech.db.components.JDbDateTextField().getDocument();

            from.addDocumentListener(new BetweenDateDocumentListener(entry.getKey(), (DataSourceFilters.BetweenDateSeekType) item, from, to));
            to.addDocumentListener(new BetweenDateDocumentListener(entry.getKey(), (DataSourceFilters.BetweenDateSeekType) item, from, to));

            documents.put(item, new javax.swing.text.Document[]{from, to});
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            javax.swing.text.Document document = new com.openitech.db.components.JDbTextField().getDocument();
            document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));
            documents.put(item, new javax.swing.text.Document[]{document});
            sifranti.put(item, ((DataSourceFilters.SifrantSeekType) item).getModel());
          } else {
            javax.swing.text.Document document = new com.openitech.db.components.JDbTextField().getDocument();
            document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));
            documents.put(item, new javax.swing.text.Document[]{document});
          }
        }
      }

      entry.getKey().addPropertyChangeListener("query", queryChanged);
    }
    if (headers.size() == 0) {
      jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ni doloèen"}));
    } else {
      jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(headers));
    }
    jcbStolpec.setPreferredSize(new JComboBox(jcbStolpec.getModel()).getPreferredSize());
    updateFilterPane();
  }
  private boolean refreshing = false;

  private void updateFilterPane() {
    try {
      refreshing = true;
      DataSourceFilters.AbstractSeekType<? extends Object> item = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();

      if (item instanceof DataSourceFilters.BetweenDateSeekType) {
        jtfDateValueOd.setDocument(documents.get(item)[0]);
        jtfDateValueDo.setDocument(documents.get(item)[1]);
        ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "DATEFIELD_CARD");
      } else if (item instanceof DataSourceFilters.SifrantSeekType) {
        jtfSifrant.setDocument(documents.get(item)[0]);
        jcbSifrant.setModel(sifranti.get(item));
        ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "SIFRANT_CARD");
      } else if (item.getSeekType()==com.openitech.db.filters.DataSourceFilters.SeekType.PREFORMATTED) {
        jtfPreformattedValue.setDocument(documents.get(item)[0]);
        ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "PREFORMATTED_CARD");
      } else {
        if (item.getSeekType()>=jcbType.getItemCount()) {
          item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.UPPER_EQUALS);
        }
        jcbType.setSelectedIndex(item.getSeekType());
        jtfValue.setDocument(documents.get(item)[0]);
        ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "TEXTFIELD_CARD");
      }
      invalidate();
      repaint();
    } finally {
      refreshing = false;
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

    jtfDateValueOd = new com.openitech.db.components.JDbDateTextField();
    jtfDateValueDo = new com.openitech.db.components.JDbDateTextField();
    try {
      smSifrant = new com.openitech.db.model.DbSifrantModel();
    } catch (java.sql.SQLException e1) {
      e1.printStackTrace();
    }
    jcbStolpec = new javax.swing.JComboBox();
    jpFilterValues = new javax.swing.JPanel();
    jpDateField = new javax.swing.JPanel();
    jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
    jpTextField = new javax.swing.JPanel();
    jcbType = new javax.swing.JComboBox();
    jtfValue = new com.openitech.db.components.JDbTextField();
    jpSifrantPanel = new javax.swing.JPanel();
    jtfSifrant = new com.openitech.db.components.JDbTextField();
    jcbSifrant = new com.openitech.db.components.JDbComboBox();
    jpPreformattedField = new javax.swing.JPanel();
    jtfPreformattedValue = new com.openitech.db.components.JDbTextField();

    jtfDateValueOd.setSearchField(true);

    jtfDateValueDo.setSearchField(true);

    setLayout(new java.awt.GridBagLayout());

    jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ni doloèen" }));
    jcbStolpec.setPreferredSize(jcbType.getPreferredSize());
    jcbStolpec.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbStolpecActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
    add(jcbStolpec, gridBagConstraints);

    jpFilterValues.setLayout(new java.awt.CardLayout());

    jXDatePicker1.setEditor(jtfDateValueOd);

    jLabel1.setText("od");

    jLabel2.setText("do");

    jXDatePicker2.setEditor(jtfDateValueDo);

    javax.swing.GroupLayout jpDateFieldLayout = new javax.swing.GroupLayout(jpDateField);
    jpDateField.setLayout(jpDateFieldLayout);
    jpDateFieldLayout.setHorizontalGroup(
      jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpDateFieldLayout.createSequentialGroup()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(80, Short.MAX_VALUE))
    );
    jpDateFieldLayout.setVerticalGroup(
      jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jLabel1)
        .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jLabel2)
        .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jpFilterValues.add(jpDateField, "DATEFIELD_CARD");

    jcbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "je enako", "se zaène z", "se konèa z", "vsebuje" }));
    jcbType.setSelectedIndex(1);
    jcbType.setFocusable(false);
    jcbType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbTypeActionPerformed(evt);
      }
    });

    jtfValue.setColumns(20);
    jtfValue.setSearchField(true);

    javax.swing.GroupLayout jpTextFieldLayout = new javax.swing.GroupLayout(jpTextField);
    jpTextField.setLayout(jpTextFieldLayout);
    jpTextFieldLayout.setHorizontalGroup(
      jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpTextFieldLayout.createSequentialGroup()
        .addComponent(jcbType, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jtfValue, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
    );
    jpTextFieldLayout.setVerticalGroup(
      jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpTextFieldLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addGroup(jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(jcbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jtfValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jpFilterValues.add(jpTextField, "TEXTFIELD_CARD");

    jtfSifrant.setColumns(4);

    jcbSifrant.setModel(smSifrant);
    jcbSifrant.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbSifrantActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jpSifrantPanelLayout = new javax.swing.GroupLayout(jpSifrantPanel);
    jpSifrantPanel.setLayout(jpSifrantPanelLayout);
    jpSifrantPanelLayout.setHorizontalGroup(
      jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSifrantPanelLayout.createSequentialGroup()
        .addComponent(jtfSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jcbSifrant, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
    );
    jpSifrantPanelLayout.setVerticalGroup(
      jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jcbSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jtfSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jpFilterValues.add(jpSifrantPanel, "SIFRANT_CARD");

    jtfPreformattedValue.setColumns(20);
    jtfPreformattedValue.setSearchField(true);

    javax.swing.GroupLayout jpPreformattedFieldLayout = new javax.swing.GroupLayout(jpPreformattedField);
    jpPreformattedField.setLayout(jpPreformattedFieldLayout);
    jpPreformattedFieldLayout.setHorizontalGroup(
      jpPreformattedFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jtfPreformattedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
    );
    jpPreformattedFieldLayout.setVerticalGroup(
      jpPreformattedFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jtfPreformattedValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    jpFilterValues.add(jpPreformattedField, "PREFORMATTED_CARD");

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
    add(jpFilterValues, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

    private void jcbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTypeActionPerformed
      // TODO add your handling code here:
      if (!refreshing) {
        DataSourceFilters.AbstractSeekType<? extends Object> item = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();

        filters.getFilterFor(item).setSeekType(item, jcbType.getSelectedIndex());
      }
    //pnDataModel.dsPonudbeFilter.setSeekType(pnDataModel.dsPonudbeFilter.I_TYPE_INSOFFERS_STRANKA_PRIIMEK, jcbType.getSelectedIndex());
}//GEN-LAST:event_jcbTypeActionPerformed

    private void jcbStolpecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbStolpecActionPerformed
      // TODO add your handling code here:
      updateFilterPane();
    }//GEN-LAST:event_jcbStolpecActionPerformed

    private void jcbSifrantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSifrantActionPerformed
      if ((jcbSifrant.getSelectedItem() != null) && (jcbSifrant.getSelectedItem() instanceof DbComboBoxModel.DbComboBoxEntry)) {
        Object value = (((DbComboBoxModel.DbComboBoxEntry) jcbSifrant.getSelectedItem()).getKey());
        if (value == null) {
          jtfSifrant.setText("");
        } else {
          jtfSifrant.setText(value.toString());
        }
      }

    }//GEN-LAST:event_jcbSifrantActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
  private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
  private com.openitech.db.components.JDbComboBox jcbSifrant;
  private javax.swing.JComboBox jcbStolpec;
  private javax.swing.JComboBox jcbType;
  private javax.swing.JPanel jpDateField;
  private javax.swing.JPanel jpFilterValues;
  private javax.swing.JPanel jpPreformattedField;
  private javax.swing.JPanel jpSifrantPanel;
  private javax.swing.JPanel jpTextField;
  private com.openitech.db.components.JDbDateTextField jtfDateValueDo;
  private com.openitech.db.components.JDbDateTextField jtfDateValueOd;
  private com.openitech.db.components.JDbTextField jtfPreformattedValue;
  private com.openitech.db.components.JDbTextField jtfSifrant;
  private com.openitech.db.components.JDbTextField jtfValue;
  private com.openitech.db.model.DbSifrantModel smSifrant;
  // End of variables declaration//GEN-END:variables

  private static class BetweenDateDocumentListener extends FilterDocumentListener {

    javax.swing.text.Document from;
    javax.swing.text.Document to;

    public BetweenDateDocumentListener(DataSourceFilters filter, DataSourceFilters.BetweenDateSeekType seek_type, javax.swing.text.Document from, javax.swing.text.Document to) {
      super(filter, seek_type);
      this.from = from;
      this.to = to;
    }

    @Override
    protected void setSeekValue(DocumentEvent e) {
      java.util.Date from_date;
      try {
        from_date = FormatFactory.DATE_FORMAT.parse(getText(from));
      } catch (ParseException ex) {
        //from_date = Calendar.getInstance().getTime();
        from_date = null;
      }
      java.util.Date to_date;
      try {
        to_date = FormatFactory.DATE_FORMAT.parse(getText(to));
      } catch (ParseException ex) {
        if (from_date == null) {
          to_date = null;
        } else {
          to_date = Calendar.getInstance().getTime();
        }
      }

      if (to_date != null) {
        java.util.Calendar calendar = Calendar.getInstance();
        calendar.setTime(to_date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        to_date = calendar.getTime();
      }

      if (from_date == null && to_date != null) {
        from_date = new java.util.Date(0);
      }

      java.util.List<java.util.Date> value = new ArrayList<java.util.Date>(2);
      value.add(from_date);
      value.add(to_date);

      schedule(new SeekValueUpdateRunnable<Object>(filter, seek_type, value));
    }
  }
}
