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
 * JPDbDataSourceFilter.java
 *
 * Created on 13.1.2009, 9:31:02
 */
package com.openitech.db.filters;

import com.openitech.db.components.JDbComboBox;
import com.openitech.db.components.JDbDateTextField;
import com.openitech.db.components.JDbTextField;
import com.openitech.db.events.ActiveRowChangeEvent;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import com.openitech.db.filters.DataSourceFilters.CheckBoxSeekType;
import com.openitech.db.filters.DataSourceFilters.CheckBoxSeekType.CheckBoxValue;
import com.openitech.db.filters.DataSourceFilters.RezultatExistsSeekType;
import com.openitech.db.filters.DataSourceFilters.RezultatExistsSeekType.RezultatExistsValue;
import com.openitech.db.filters.DataSourceFilters.RezultatiKlicaSeekType;
import com.openitech.db.filters.DataSourceFilters.RezultatiKlicaSeekType.RezultatKlica;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.xml.config.GridBagConstraints;
import com.openitech.db.model.xml.config.SeekLayout;
import com.openitech.events.concurrent.RefreshDataSource;
import com.openitech.util.Equals;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author uros
 */
public class JPDbDataSourceFilter extends javax.swing.JPanel implements ActiveFiltersReader {

  private final DataSourceFiltersMap filters = new DataSourceFiltersMap();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document[]> documents = new HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document[]>();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, DbComboBoxModel> sifranti = new HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, DbComboBoxModel>();
  private final java.util.Set<DbDataSource> dataSources = new HashSet<DbDataSource>();
  private final java.util.List<ButtonGroup> buttonGroups = new java.util.ArrayList<ButtonGroup>();

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
  protected Map<String, Document> namedDocuments = new HashMap<String, Document>();

  /**
   * Get the value of namedDocuments
   *
   * @return the value of namedDocuments
   */
  public Map<String, Document> getNamedDocuments() {
    return namedDocuments;
  }

  /**
   * Set the value of namedDocuments
   *
   * @param namedDocuments new value of namedDocuments
   */
  public void setNamedDocuments(Map<String, Document> namedDocuments) {
    this.namedDocuments = namedDocuments == null ? new HashMap<String, Document>() : namedDocuments;
    updateColumns();
  }
  private JPDbDataSourceFilter parentFilterPanel;

  /**
   * Get the value of parent
   *
   * @return the value of parent
   */
  public JPDbDataSourceFilter getParentFilterPanel() {
    return parentFilterPanel;
  }

  /**
   * Set the value of parent
   *
   * @param parent new value of parent
   */
  public void setParentFilterPanel(JPDbDataSourceFilter parentFilterPanel) {
    if (!Equals.equals(this.parentFilterPanel, parentFilterPanel)) {
      this.parentFilterPanel = parentFilterPanel;
      updateColumns();
    }
  }

  public DataSourceFiltersMap getFilters() {
    return filters;
  }

  public Set<DbDataSource> getDataSources() {
    return Collections.unmodifiableSet(dataSources);
  }
  private JMenu filterMenuItem = new JMenu("Aktivni filtri");

  @Override
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
    JMenuItem clearMenuItem = new JMenuItem("Odstrani vse");
    clearMenuItem.setActionCommand("CLEAR");
    clearMenuItem.addActionListener(new ClearFilters());
    filterMenuItem.add(clearMenuItem);


    firePropertyChange("filter_menu", filterMenuItem, null);
  }

  @Override
  public DataSourceFilters getActiveFilter() {
    return null;
  }
  private boolean removedComboboxFilters = false;

  private void removeComboboxFilters() {
    remove(jcbStolpec);
    remove(jpFilterValues);
    removedComboboxFilters = true;
  }

  private void addComboboxFilters() {
    add(jcbStolpec, 1);
    add(jpFilterValues, 2);
    removedComboboxFilters = false;
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

  private class ClearFilters implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equalsIgnoreCase("CLEAR") && documents != null) {
        clearFilters();
      }
    }
  }
  private final PropertyChangeListener queryChanged = new PropertyChangeListener() {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      updateFilterMenuItem();
    }
  };

  public void clearFilters() {
    boolean autoReload = true;
    for (DbDataSource dbDataSource : dataSources) {
      autoReload = dbDataSource.isAutoReload();
      dbDataSource.setAutoReload(false);
    }
    for (javax.swing.text.Document documents1[] : documents.values()) {
      for (Document document : documents1) {
        try {
          document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
          Logger.getLogger(JPDbDataSourceFilter.class.getName()).log(Level.WARNING, null, ex);
        }
      }
    }
    for (DbDataSource dbDataSource : dataSources) {
      dbDataSource.setAutoReload(autoReload);
    }
    reload(true);
  }

  private void updateColumns() {
    Vector<DataSourceFilters.AbstractSeekType<? extends Object>> headers = new Vector<DataSourceFilters.AbstractSeekType<? extends Object>>();
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

    customPanel.removeAll();
    filtersInPanel = 0;
    documents.clear();
    dataSources.clear();

    for (ButtonGroup bg : buttonGroups) {
      for (Enumeration<AbstractButton> elements = bg.getElements(); elements.hasMoreElements();) {
        bg.remove(elements.nextElement());
      }
    }

    buttonGroups.clear();

    for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
      java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();
      final DataSourceFilters filter = entry.getKey();
      dataSources.addAll(filter.getDataSources());

      for (int i = 0; i < seekTypeList.size(); i++) {
        final DataSourceFilters.AbstractSeekType<? extends Object> item = seekTypeList.get(i) instanceof DataSourceFilters.SeekTypeWrapper
                ? ((DataSourceFilters.SeekTypeWrapper) seekTypeList.get(i)).getWrapperFor()
                : seekTypeList.get(i);
        final DataSourceFilters.AbstractSeekType<? extends Object> listenerItem = seekTypeList.get(i);
        final boolean addToPanel = item.getLayout() != null && item.getLayout().isDisplayInPanel();
        filtersInPanel += addToPanel ? 1 : 0;
        final SeekLayout layout = item.getLayout();

        if (!item.isAutomatic()) {
          documents.put(item, item.getDocuments());
        } else {
          List<javax.swing.text.Document> docs = new ArrayList<Document>();

          if (!addToPanel) {
            headers.add(item);
          }
          if (item.getLayout() != null && item.getLayout().getDocuments() != null) {
            for (String documentName : item.getLayout().getDocuments().getDocumentNames()) {
              if (namedDocuments.containsKey(documentName)) {
                docs.add(namedDocuments.get(documentName));
              } else {
                Document document = new com.openitech.db.components.JDbTextField().getDocument();
                docs.add(document);
                namedDocuments.put(documentName, document);
              }
            }
          }

          if (docs.isEmpty()) {
            docs.add(new com.openitech.db.components.JDbTextField().getDocument());
          }

          if ((parentFilterPanel != null) && (parentFilterPanel.documents.containsKey(item))) {
            documents.put(item, parentFilterPanel.documents.get(item));
            if (parentFilterPanel.sifranti.containsKey(item)) {
              sifranti.put(item, parentFilterPanel.sifranti.get(item));
            }
          } else if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            javax.swing.text.Document from = docs.get(0);
            javax.swing.text.Document to = docs.size() > 1 ? docs.get(1) : new com.openitech.db.components.JDbDateTextField().getDocument();

            item.setDocuments(filter, new javax.swing.text.Document[]{from, to});

            documents.put(item, new javax.swing.text.Document[]{from, to});
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            javax.swing.text.Document document = docs.get(0);
            document.addDocumentListener(new FilterDocumentListener(filter, listenerItem));

            documents.put(item, new javax.swing.text.Document[]{document});
            sifranti.put(item, ((DataSourceFilters.SifrantSeekType) item).getModel());

            if (!addToPanel) {
              document.addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                  updateSifrant(e.getDocument(), jcbSifrant);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                  updateSifrant(e.getDocument(), jcbSifrant);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                  updateSifrant(e.getDocument(), jcbSifrant);
                }
              });
            }
          } else {
            javax.swing.text.Document document = docs.get(0);
            document.addDocumentListener(new FilterDocumentListener(filter, listenerItem));
            documents.put(item, new javax.swing.text.Document[]{document});
          }

          if (!addToPanel && item.hasValue()) {
            try {
              if (item instanceof DataSourceFilters.BetweenDateSeekType) {
                DataSourceFilters.BetweenDateSeekType seek = (DataSourceFilters.BetweenDateSeekType) item;
                if (seek.getValue().get(0).after(new java.util.Date(0L))) {
                  com.openitech.text.Document.setText(documents.get(item)[0], jtfDateValueOd.getFormatter().valueToString(seek.getValue().get(0)));
                }
                if (seek.getValue().size() > 1
                        && seek.getValue().get(1).before(new java.util.Date())) {
                  com.openitech.text.Document.setText(documents.get(item)[1], jtfDateValueDo.getFormatter().valueToString(seek.getValue().get(1)));
                }
              } else {
                com.openitech.text.Document.setText(documents.get(item)[0], item.getValue().toString());
              }
            } catch (Exception ex) {
              Logger.getLogger(JPDbDataSourceFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }

        if (addToPanel) {
          final boolean group = (layout.getLayout() == null) || (layout.getLayout() != null && layout.getLayout().getGroup() != null);

          final JPanel jpHoldingPanel = group ? new javax.swing.JPanel() : customPanel;
          if (group) {
            jpHoldingPanel.setLayout(new java.awt.GridBagLayout());
          }

          if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            JLabel jlOd = new javax.swing.JLabel();
            jlOd.setText(item.toString() + " od");
            JLabel jlDo = new javax.swing.JLabel();
            jlDo.setText("do");
            JDbDateTextField jtfDateValueOd1 = new com.openitech.db.components.JDbDateTextField();
            jtfDateValueOd1.setSearchField(true);
            JDbDateTextField jtfDateValueDo1 = new com.openitech.db.components.JDbDateTextField();
            jtfDateValueDo1.setSearchField(true);

            jtfDateValueOd1.setDocument(documents.get(item)[0]);
            jtfDateValueDo1.setDocument(documents.get(item)[1]);

            JXDatePicker jXDatePicker = new org.jdesktop.swingx.JXDatePicker();
            JXDatePicker jXDatePicker3 = new org.jdesktop.swingx.JXDatePicker();
            jXDatePicker.setEditor(jtfDateValueOd1);
            jXDatePicker3.setEditor(jtfDateValueDo1);

            DataSourceFilters.BetweenDateSeekType seek = (DataSourceFilters.BetweenDateSeekType) item;

            if (seek.hasValue()) {
              if (seek.getValue().get(0).after(new java.util.Date(0L))) {
                jXDatePicker.setDate(seek.getValue().get(0));
              }
              if (seek.getValue().size() > 1
                      && seek.getValue().get(1).before(new java.util.Date())) {
                jXDatePicker3.setDate(seek.getValue().get(1));
              }
            }

            int index = 0;

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOd, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));
            jpHoldingPanel.add(jXDatePicker, group ? new java.awt.GridBagConstraints() : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));
            jpHoldingPanel.add(jlDo, group ? new java.awt.GridBagConstraints() : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));
            gridBagConstraints = new java.awt.GridBagConstraints();
            //gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            jpHoldingPanel.add(jXDatePicker3, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            if (group) {
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
              gridBagConstraints.weightx = 1.0;
              jpHoldingPanel.add(new JPanel(), gridBagConstraints);
            }
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            JLabel jlOpis = new javax.swing.JLabel();
            final JDbTextField jtfSifraOnPanel = new com.openitech.db.components.JDbTextField();
            final JDbComboBox jcbSifrantOnPanel = new com.openitech.db.components.JDbComboBox();

            int index = 0;

            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));

            gridBagConstraints = new java.awt.GridBagConstraints();
            jtfSifraOnPanel.setColumns(layout.getColumns() == null ? 4 : layout.getColumns());
            jtfSifraOnPanel.setDocument(documents.get(item)[0]);
            jtfSifraOnPanel.setSearchField(true);
            jpHoldingPanel.add(jtfSifraOnPanel, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            jcbSifrantOnPanel.setModel(sifranti.get(item));
            jcbSifrantOnPanel.addActionListener(new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {
                if (!updating) {
                  if ((jcbSifrantOnPanel.getSelectedItem() != null) && (jcbSifrantOnPanel.getSelectedItem() instanceof DbComboBoxModel.DbComboBoxEntry)) {
                    updating = true;
                    try {
                      Object value = (((DbComboBoxModel.DbComboBoxEntry) jcbSifrantOnPanel.getSelectedItem()).getKey());
                      if (value == null) {
                        jtfSifraOnPanel.setText("");
                      } else {
                        jtfSifraOnPanel.setText(value.toString());
                      }
                    } finally {
                      updating = false;
                    }
                  }
                }
              }
            });


            gridBagConstraints = new java.awt.GridBagConstraints();
            //gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            jpHoldingPanel.add(jcbSifrantOnPanel, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            documents.get(item)[0].addDocumentListener(new DocumentListener() {

              @Override
              public void insertUpdate(DocumentEvent e) {
                updateSifrant(e.getDocument(), jcbSifrantOnPanel);
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                updateSifrant(e.getDocument(), jcbSifrantOnPanel);
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                updateSifrant(e.getDocument(), jcbSifrantOnPanel);
              }
            });

            if (((DataSourceFilters.SifrantSeekType) item).hasValue()) {
              jtfSifraOnPanel.setText(((DataSourceFilters.SifrantSeekType) item).getValue());
            }
          } else if (item instanceof DataSourceFilters.IntegerSeekType) {
            final JLabel jlOpis = new javax.swing.JLabel();
            final JComboBox jDbComboBox1 = new JComboBox();
            final JDbTextField jDbTextField1 = new com.openitech.db.components.JDbTextField();

            int index = 0;

            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));
//            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), index++));

            if (item.getSeekType() != DataSourceFilters.SeekType.PREFORMATTED) {
              //jcbNumberType.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"je enako", "je veèje ali enako od", "je manjše ali enako kot"}));
              jDbComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"je enako", "je veèje ali enako od", "je manjše ali enako kot"}));
              jDbComboBox1.setFocusable(false);
              jDbComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                  if (!refreshing) {
                    filters.getFilterFor(listenerItem).setSeekType(listenerItem, jDbComboBox1.getSelectedIndex() + com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
                  }
                }
              });

              //ker se za integer equals zaène z 4, zato vedno odštejem EQUALS, da dobim index v moboboxu
              //èe pa je sluèajno index veèji kot je možnosti pa ponastavim na equals
              try {
                if ((item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS) >= jcbNumberType.getItemCount()) {
                  item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
                }
                jDbComboBox1.setSelectedIndex(item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
              } catch (Exception ex) {
              }

              jpHoldingPanel.add(jDbComboBox1, group ? new java.awt.GridBagConstraints() : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));
            }

            jDbTextField1.setSearchField(true);
            jDbTextField1.setColumns(layout.getColumns() == null ? 10 : layout.getColumns());
            jDbTextField1.setDocument(documents.get(item)[0]);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            jpHoldingPanel.add(jDbTextField1, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            if (((DataSourceFilters.IntegerSeekType) item).hasValue()) {
              jDbTextField1.setText(((DataSourceFilters.IntegerSeekType) item).getValue().toString());
            }
          } else if (item instanceof DataSourceFilters.BooleanSeekType) {
            final JCheckBox jCheckBox = new JCheckBox();

            int index = 0;
            final Document document = documents.get(item)[0];
            jCheckBox.setText(item.toString());
            jCheckBox.addItemListener(new ItemListener() {

              @Override
              public void itemStateChanged(ItemEvent e) {
                try {
                  if (e.getStateChange() == ItemEvent.SELECTED) {
                    com.openitech.text.Document.setText(document, Boolean.TRUE.toString());
                  } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    com.openitech.text.Document.setText(document, Boolean.FALSE.toString());
                  }
                } catch (BadLocationException ex) {
                  Logger.getLogger(JPDbDataSourceFilter.class.getName()).warning(ex.getMessage());
                }
              }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            jpHoldingPanel.add(jCheckBox, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));
          } else if (item instanceof DataSourceFilters.ValueSeekType) {

            int index = 0;
            final Document document = documents.get(item)[0];

            final JDbTextField jDbTextField1 = new com.openitech.db.components.JDbTextField();
            jDbTextField1.setColumns(layout.getColumns() == null ? 20 : layout.getColumns());
            jDbTextField1.setSearchField(true);
            jDbTextField1.setDocument(document);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
            jpHoldingPanel.add(jDbTextField1, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            JButton jbConfigureFilter = new JButton("Configure filter");
            final JDConfigureFilter jDConfigureFilter = new JDConfigureFilter(null, false);

            jDConfigureFilter.setDocument(document);
            try {
              jDConfigureFilter.setDataSource(filter.getFirstDataSource());
            } catch (SQLException ex) {
              Logger.getLogger(JPDbDataSourceFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
            jbConfigureFilter.addActionListener(new ActionListener() {

              @Override
              public void actionPerformed(ActionEvent e) {
                jDConfigureFilter.setVisible(true);

              }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            jpHoldingPanel.add(jbConfigureFilter, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));


          } else if (item instanceof DataSourceFilters.RezultatiKlicaSeekType) {

            final JLabel jlOpis = new javax.swing.JLabel();
            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));

            final DataSourceFilters.RezultatiKlicaSeekType rezultatiKlicaSeekType = (RezultatiKlicaSeekType) item;

            JPanel jpBoxes = new JPanel(new GridBagLayout());
            jpBoxes.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

            List<RezultatKlica> rezultati = rezultatiKlicaSeekType.getRezultati();
            for (final RezultatKlica rezultatKlica : rezultati) {
              final JCheckBox checkBox = new JCheckBox(rezultatKlica.getOpis());
              checkBox.setSelected(rezultatKlica.isChecked());
              checkBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                  rezultatKlica.setChecked(checkBox.isSelected());
                  rezultatiKlicaSeekType.reload();
                }
              });
              jpBoxes.add(checkBox, new java.awt.GridBagConstraints());
            }

            //ko je dataSource pripravljen, mu dodam default filtre
            for (DbDataSource dataSource : filter.getDataSources()) {
              dataSource.addActiveRowChangeListener(new RezultatKlicaActiveRowChangeListener(rezultatiKlicaSeekType));
            }

            JPanel jpChechBoxHolder = new JPanel(new GridBagLayout());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jpChechBoxHolder.add(jpBoxes, gridBagConstraints);


            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
            jpHoldingPanel.add(jpChechBoxHolder, gridBagConstraints);

          } else if (item instanceof DataSourceFilters.RezultatExistsSeekType) {

            final JLabel jlOpis = new javax.swing.JLabel();
            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));

            final DataSourceFilters.RezultatExistsSeekType existsSeekType = (RezultatExistsSeekType) item;

            JPanel jpBoxes = new JPanel(new GridBagLayout());
            jpBoxes.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

            List<RezultatExistsValue> rezultati = existsSeekType.getRezultati();
            for (final RezultatExistsValue rezultatKlica : rezultati) {
              final JCheckBox checkBox = new JCheckBox(rezultatKlica.getOpis());
              checkBox.setSelected(rezultatKlica.isChecked());
              checkBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                  rezultatKlica.setChecked(checkBox.isSelected());
                  existsSeekType.reload();
                }
              });
              jpBoxes.add(checkBox, new java.awt.GridBagConstraints());
            }

            //ko je dataSource pripravljen, mu dodam default filtre
            for (DbDataSource dataSource : filter.getDataSources()) {
              dataSource.addActiveRowChangeListener(new ExistsSeekTypeActiveRowChangeListener(existsSeekType));
            }

            JPanel jpChechBoxHolder = new JPanel(new GridBagLayout());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jpChechBoxHolder.add(jpBoxes, gridBagConstraints);


            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
            jpHoldingPanel.add(jpChechBoxHolder, gridBagConstraints);

          } else if (item instanceof DataSourceFilters.CheckBoxSeekType) {
            int index = 0;
            final JLabel jlOpis = new javax.swing.JLabel();
            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));

            final DataSourceFilters.CheckBoxSeekType existsSeekType = (CheckBoxSeekType) item;
            final ButtonGroup bg = new ButtonGroup();

            JPanel jpBoxes = new JPanel(new GridBagLayout());
            jpBoxes.setBorder(javax.swing.BorderFactory.createTitledBorder(""));


            List<JToggleButton> checkBoxes = new ArrayList<JToggleButton>();
            List<CheckBoxValue> rezultati = existsSeekType.getRezultati();
            for (final CheckBoxValue rezultatKlica : rezultati) {
              final JToggleButton checkBox = existsSeekType.isGrouped()?new JRadioButton(rezultatKlica.getOpis()):new JCheckBox(rezultatKlica.getOpis());

              checkBoxes.add(checkBox);


              if (existsSeekType.isGrouped()) {
                bg.add(checkBox);
              }

              checkBox.getModel().addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                  rezultatKlica.setChecked(checkBox.isSelected());
                }
              });

              checkBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                  existsSeekType.reload();
                }
              });
            }

            if (bg.getButtonCount() > 0) {
              buttonGroups.add(bg);
            }

            int cb = 0;
            for (final CheckBoxValue rezultatKlica : rezultati) {
              final JToggleButton checkBox = checkBoxes.get(cb++);

              checkBox.setSelected(rezultatKlica.isChecked());

              jpBoxes.add(checkBox, new java.awt.GridBagConstraints());
            }

            checkBoxes.clear();

            //ko je dataSource pripravljen, mu dodam default filtre
            for (DbDataSource dataSource : filter.getDataSources()) {
              dataSource.addActiveRowChangeListener(new CheckBoxSeekTypeActiveRowChangeListener(existsSeekType));
            }

            JPanel jpChechBoxHolder = new JPanel(new GridBagLayout());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jpChechBoxHolder.add(jpBoxes, gridBagConstraints);


            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            jpHoldingPanel.add(jpChechBoxHolder, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

          } else {
            final JLabel jlOpis = new javax.swing.JLabel();
            final JComboBox jDbComboBox1 = new JComboBox();
            final JDbTextField jDbTextField1 = new com.openitech.db.components.JDbTextField();

            int index = 0;

            jlOpis.setText(item.toString());
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
//            customPanel.add(jlOpis, gridBagConstraints);
            customPanel.add(jlOpis, group ? gridBagConstraints : getCustomLabelGridBagConstraints(layout, gridBagConstraints));

            jDbComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"je enako", "se zaène z", "se konèa z", "vsebuje"}));

            if (item.getSeekType() != DataSourceFilters.SeekType.PREFORMATTED) { //preformated
              if ((item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS) >= jDbComboBox1.getItemCount()) {
                item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
              }
              if (item.getSeekType() >= jDbComboBox1.getItemCount()) {
                jDbComboBox1.setSelectedIndex(item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
              } else {
                jDbComboBox1.setSelectedIndex(item.getSeekType());
              }

              jDbComboBox1.setFocusable(false);
              jDbComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                  if (!refreshing) {

                    filters.getFilterFor(listenerItem).setSeekType(listenerItem, jDbComboBox1.getSelectedIndex());
                  }
                }
              });
              jpHoldingPanel.add(jDbComboBox1, group ? new java.awt.GridBagConstraints() : getCustomGridBagConstraints(layout.getLayout(), false, index++, new java.awt.GridBagConstraints()));
            }

            jDbTextField1.setColumns(layout.getColumns() == null ? 20 : layout.getColumns());
            jDbTextField1.setSearchField(true);
            jDbTextField1.setDocument(documents.get(item)[0]);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            jpHoldingPanel.add(jDbTextField1, group ? gridBagConstraints : getCustomGridBagConstraints(layout.getLayout(), false, index++, gridBagConstraints));

            if (item.hasValue()) {
              jDbTextField1.setText(item.getValue().toString());
            }
          }

          if (group) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            customPanel.add(jpHoldingPanel, getCustomGridBagConstraints(layout, gridBagConstraints));
          }
        }
      }

      filter.addPropertyChangeListener("query", queryChanged);
    }

    if (headers.isEmpty()) {
      jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ni doloèen"}));
      removeComboboxFilters();
    } else {
      jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(headers));
      if (removedComboboxFilters) {
        addComboboxFilters();
      }
    }
    jcbStolpec.setPreferredSize(new JComboBox(jcbStolpec.getModel()).getPreferredSize());
    updateFilterPane();
  }

  public void reload(boolean queued) {
    for (DbDataSource dataSource : dataSources) {
      if (queued) {
        dataSource.reload(true);
      } else {
        RefreshDataSource refreshDataSource = new com.openitech.events.concurrent.RefreshDataSource(dataSource, true);
        refreshDataSource.setQueuedDelay(1);
        com.openitech.events.concurrent.DataSourceEvent.submit(refreshDataSource);
      }
    }
  }

  private java.awt.GridBagConstraints getCustomLabelGridBagConstraints(SeekLayout layout, java.awt.GridBagConstraints defaultConstraint) {
    java.awt.GridBagConstraints gridBagConstraints;
    if (layout.getLayout() == null) {
      gridBagConstraints = defaultConstraint;
    } else {
      gridBagConstraints = getCustomGridBagConstraints(layout.getLayout(), true, 0, defaultConstraint);
    }
    return gridBagConstraints;
  }

  private java.awt.GridBagConstraints getCustomGridBagConstraints(SeekLayout layout, java.awt.GridBagConstraints defaultConstraint) {
    java.awt.GridBagConstraints gridBagConstraints;
    if (layout.getLayout() == null) {
      gridBagConstraints = defaultConstraint;
    } else {
      gridBagConstraints = getCustomGridBagConstraints(layout.getLayout(), false, 0, defaultConstraint);
    }
    return gridBagConstraints;
  }

  private java.awt.GridBagConstraints getCustomGridBagConstraints(SeekLayout.Layout layout, boolean label, int index, java.awt.GridBagConstraints defaultConstraint) {
    java.awt.GridBagConstraints gridBagConstraints;
    if (layout != null && layout.getGroup() != null) {
      gridBagConstraints = getCustomGridBagConstraints(layout.getGroup(), defaultConstraint);
    } else if (layout != null && layout.getConstraints() != null) {
      final List<GridBagConstraints> constraints = new ArrayList<GridBagConstraints>(layout.getConstraints().getGridBagConstraints());
      int pos = Integer.MIN_VALUE;

      for (int i = 0; i < constraints.size(); i++) {
        if (constraints.get(i).isLabel()) {
          pos = i;
          break;
        }
      }

      if (label) {
        gridBagConstraints = pos > Integer.MIN_VALUE ? getCustomGridBagConstraints(constraints.get(pos), defaultConstraint) : defaultConstraint;
      } else {
        if (index >= pos) {
          index++;
        }
        if (index < constraints.size()) {
          gridBagConstraints = getCustomGridBagConstraints(constraints.get(index), defaultConstraint);
        } else {
          gridBagConstraints = defaultConstraint;
        }
      }
    } else {
      gridBagConstraints = defaultConstraint;
//      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
//      gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
//      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//      gridBagConstraints.weightx = 1.0;
    }
    return gridBagConstraints;
  }

  private java.awt.GridBagConstraints getCustomGridBagConstraints(final GridBagConstraints customGridBagConstraints, final java.awt.GridBagConstraints defaultConstraints) {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    if (customGridBagConstraints.getGridX() != null) {
      gridBagConstraints.gridx = customGridBagConstraints.getGridX();
    } else {
      gridBagConstraints.gridx = defaultConstraints.gridx;
    }
    if (customGridBagConstraints.getGridY() != null) {
      gridBagConstraints.gridy = customGridBagConstraints.getGridY();
    } else {
      gridBagConstraints.gridy = defaultConstraints.gridy;
    }
    if (customGridBagConstraints.getGridWidth() != null) {
      gridBagConstraints.gridwidth = customGridBagConstraints.getGridWidth();
    } else {
      gridBagConstraints.gridwidth = defaultConstraints.gridwidth;
    }
    if (customGridBagConstraints.getGridHeight() != null) {
      gridBagConstraints.gridheight = customGridBagConstraints.getGridHeight();
    } else {
      gridBagConstraints.gridheight = defaultConstraints.gridheight;
    }
    if (customGridBagConstraints.getFill() != null) {
      switch (customGridBagConstraints.getFill()) {
        case NONE:
          gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
          break;
        case BOTH:
          gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
          break;
        case HORIZONTAL:
          gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
          break;
        case VERTICAL:
          gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
          break;
      }
    } else {
      gridBagConstraints.fill = defaultConstraints.fill;
    }
    if (customGridBagConstraints.getAnchor() != null) {
      switch (customGridBagConstraints.getAnchor()) {
        case CENTER:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
          break;
        case NORTH:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
          break;
        case NORTHEAST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
          break;
        case EAST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
          break;
        case SOUTHEAST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
          break;
        case SOUTH:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
          break;
        case SOUTHWEST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
          break;
        case WEST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
          break;
        case NORTHWEST:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
          break;
        case PAGE_START:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
          break;
        case PAGE_END:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
          break;
        case LINE_START:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
          break;
        case LINE_END:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
          break;
        case FIRST_LINE_START:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
          break;
        case FIRST_LINE_END:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
          break;
        case LAST_LINE_START:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
          break;
        case LAST_LINE_END:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
          break;
        case BASELINE:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
          break;
        case BASELINE_LEADING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
          break;
        case BASELINE_TRAILING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
          break;
        case ABOVE_BASELINE:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE;
          break;
        case ABOVE_BASELINE_LEADING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
          break;
        case ABOVE_BASELINE_TRAILING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_TRAILING;
          break;
        case BELOW_BASELINE:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE;
          break;
        case BELOW_BASELINE_LEADING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE_LEADING;
          break;
        case BELOW_BASELINE_TRAILING:
          gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE_TRAILING;
          break;
      }
    } else {
      gridBagConstraints.anchor = defaultConstraints.anchor;
    }
    if (customGridBagConstraints.getWeightX() != null) {
      gridBagConstraints.weightx = customGridBagConstraints.getWeightX();
    } else {
      gridBagConstraints.weightx = defaultConstraints.weightx;
    }
    if (customGridBagConstraints.getWeightY() != null) {
      gridBagConstraints.weighty = customGridBagConstraints.getWeightY();
    } else {
      gridBagConstraints.weighty = defaultConstraints.weighty;
    }
    if (customGridBagConstraints.getIPadX() != null) {
      gridBagConstraints.ipadx = customGridBagConstraints.getIPadX();
    } else {
      gridBagConstraints.ipadx = defaultConstraints.ipadx;
    }
    if (customGridBagConstraints.getIPadY() != null) {
      gridBagConstraints.ipady = customGridBagConstraints.getIPadY();
    } else {
      gridBagConstraints.ipady = defaultConstraints.ipady;
    }

    if (customGridBagConstraints.getInsets() != null) {
      gridBagConstraints.insets = new java.awt.Insets(customGridBagConstraints.getInsets().getTop(), customGridBagConstraints.getInsets().getLeft(), customGridBagConstraints.getInsets().getBottom(), customGridBagConstraints.getInsets().getRight());
    } else {
      gridBagConstraints.insets = defaultConstraints.insets;
    }
    return gridBagConstraints;
  }
  private boolean refreshing = false;

  private void updateSifrant(javax.swing.text.Document doc, JDbComboBox jcbSifrant) {
    if ((!updating) && (jcbSifrant != null)) {
      updating = true;
      try {
        final String text = com.openitech.text.Document.getText(doc);
        if (text.isEmpty()) {
          if (jcbSifrant.getModel().getSize() > 0) {
            jcbSifrant.setSelectedIndex(0);
          }
        } else {
          jcbSifrant.setSelectedItem(new DbComboBoxModel.DbComboBoxEntry<Object, Object>(text, null, null));
          if (jcbSifrant.getEditor() != null) {
            jcbSifrant.getEditor().setItem(jcbSifrant.getSelectedItem());
          }
        }
        jcbSifrant.repaint(27);
      } catch (BadLocationException ex) {
        Logger.getLogger(JPDbDataSourceFilter.class.getName()).warning(ex.getMessage());
        jcbSifrant.setSelectedItem(null);
      } finally {
        updating = false;
      }
    }
  }

  private void updateFilterPane() {
    try {
      refreshing = true;
      if (jcbStolpec.getSelectedItem() instanceof DataSourceFilters.AbstractSeekType) {
        DataSourceFilters.AbstractSeekType<? extends Object> item = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();
        if (item.getLayout() == null || !item.getLayout().isDisplayInPanel()) {
          final Document document = documents.get(item)[0];
          if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            jtfDateValueOd.setDocument(document);
            jtfDateValueDo.setDocument(documents.get(item)[1]);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "DATEFIELD_CARD");
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            jtfSifrant.setDocument(document);
            jcbSifrant.setModel(sifranti.get(item));
            updateSifrant(document, jcbSifrant);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "SIFRANT_CARD");
          } else if (item.getSeekType() == com.openitech.db.filters.DataSourceFilters.SeekType.PREFORMATTED) {
            jtfPreformattedValue.setDocument(document);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "PREFORMATTED_CARD");
          } else if (item instanceof DataSourceFilters.IntegerSeekType) {
            if ((item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS) >= jcbNumberType.getItemCount()) {
              item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
            }
            jcbNumberType.setSelectedIndex(item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
            jtfNumberValue.setDocument(document);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "NUMBERFIELD_CARD");
          } else {
            if (item.getSeekType() >= jcbType.getItemCount()) {
              item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.UPPER_EQUALS);
            }
            jcbType.setSelectedIndex(item.getSeekType());
            jtfValue.setDocument(document);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "TEXTFIELD_CARD");
          }
          invalidate();
          repaint();
        }
      }
    } finally {
      refreshing = false;
    }
  }
  protected int filtersInPanel = 0;

  /**
   * Get the value of filtersInPanel
   *
   * @return the value of filtersInPanel
   */
  public int getFiltersInPanel() {
    return filtersInPanel;
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
    jPanel2 = new javax.swing.JPanel();
    customPanel = new javax.swing.JPanel();
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
    jpNumberField = new javax.swing.JPanel();
    jcbNumberType = new javax.swing.JComboBox();
    jtfNumberValue = new com.openitech.db.components.JDbTextField();
    jPanel1 = new javax.swing.JPanel();

    jtfDateValueOd.setSearchField(true);

    jtfDateValueDo.setSearchField(true);

    setLayout(new java.awt.GridBagLayout());

    jPanel2.setLayout(new java.awt.GridBagLayout());

    customPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jPanel2.add(customPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(jPanel2, gridBagConstraints);

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
        .addContainerGap(67, Short.MAX_VALUE))
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
        .addComponent(jcbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jtfValue, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
    );
    jpTextFieldLayout.setVerticalGroup(
      jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpTextFieldLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addGroup(jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jcbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jtfValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jpFilterValues.add(jpTextField, "TEXTFIELD_CARD");

    jtfSifrant.setColumns(4);
    jtfSifrant.setSearchField(true);

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
        .addComponent(jcbSifrant, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
    );
    jpSifrantPanelLayout.setVerticalGroup(
      jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jtfSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jcbSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jpFilterValues.add(jpSifrantPanel, "SIFRANT_CARD");

    jtfPreformattedValue.setColumns(20);
    jtfPreformattedValue.setSearchField(true);

    javax.swing.GroupLayout jpPreformattedFieldLayout = new javax.swing.GroupLayout(jpPreformattedField);
    jpPreformattedField.setLayout(jpPreformattedFieldLayout);
    jpPreformattedFieldLayout.setHorizontalGroup(
      jpPreformattedFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jtfPreformattedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
    );
    jpPreformattedFieldLayout.setVerticalGroup(
      jpPreformattedFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jtfPreformattedValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    jpFilterValues.add(jpPreformattedField, "PREFORMATTED_CARD");

    jcbNumberType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "je enako", "je veèje ali enako od", "je manjše ali enako kot" }));
    jcbNumberType.setFocusable(false);
    jcbNumberType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbNumberTypeActionPerformed(evt);
      }
    });

    jtfNumberValue.setColumns(20);
    jtfNumberValue.setSearchField(true);

    javax.swing.GroupLayout jpNumberFieldLayout = new javax.swing.GroupLayout(jpNumberField);
    jpNumberField.setLayout(jpNumberFieldLayout);
    jpNumberFieldLayout.setHorizontalGroup(
      jpNumberFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpNumberFieldLayout.createSequentialGroup()
        .addComponent(jcbNumberType, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jtfNumberValue, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
    );
    jpNumberFieldLayout.setVerticalGroup(
      jpNumberFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpNumberFieldLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addGroup(jpNumberFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jcbNumberType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jtfNumberValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jpFilterValues.add(jpNumberField, "NUMBERFIELD_CARD");

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
    add(jpFilterValues, gridBagConstraints);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(jPanel1, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

    private void jcbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTypeActionPerformed
      if (!refreshing) {
        DataSourceFilters.AbstractSeekType<? extends Object> seek = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();
        for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
          java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

          for (int i = 0; i < seekTypeList.size(); i++) {
            final DataSourceFilters.AbstractSeekType<? extends Object> item = seekTypeList.get(i) instanceof DataSourceFilters.SeekTypeWrapper
                    ? ((DataSourceFilters.SeekTypeWrapper) seekTypeList.get(i)).getWrapperFor()
                    : seekTypeList.get(i);

            if (item.equals(seek)) {
              filters.getFilterFor(seekTypeList.get(i)).setSeekType(seekTypeList.get(i), jcbType.getSelectedIndex());
              break;
            }
          }
        }

      }
      //pnDataModel.dsPonudbeFilter.setSeekType(pnDataModel.dsPonudbeFilter.I_TYPE_INSOFFERS_STRANKA_PRIIMEK, jcbType.getSelectedIndex());
}//GEN-LAST:event_jcbTypeActionPerformed

    private void jcbStolpecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbStolpecActionPerformed
      // TODO add your handling code here:
      updateFilterPane();
    }//GEN-LAST:event_jcbStolpecActionPerformed
  private boolean updating = false;

    private void jcbSifrantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSifrantActionPerformed
      if (!updating) {
        if ((jcbSifrant.getSelectedItem() != null) && (jcbSifrant.getSelectedItem() instanceof DbComboBoxModel.DbComboBoxEntry)) {
          updating = true;
          try {
            Object value = (((DbComboBoxModel.DbComboBoxEntry) jcbSifrant.getSelectedItem()).getKey());
            if (value == null) {
              jtfSifrant.setText("");
            } else {
              jtfSifrant.setText(value.toString());
            }
          } finally {
            updating = false;
          }

        }
      }

    }//GEN-LAST:event_jcbSifrantActionPerformed

    private void jcbNumberTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbNumberTypeActionPerformed
      if (!refreshing) {
        DataSourceFilters.AbstractSeekType<? extends Object> seek = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();
        for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
          java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

          for (int i = 0; i < seekTypeList.size(); i++) {
            final DataSourceFilters.AbstractSeekType<? extends Object> item = seekTypeList.get(i) instanceof DataSourceFilters.SeekTypeWrapper
                    ? ((DataSourceFilters.SeekTypeWrapper) seekTypeList.get(i)).getWrapperFor()
                    : seekTypeList.get(i);

            if (item.equals(seek)) {
              filters.getFilterFor(seekTypeList.get(i)).setSeekType(seekTypeList.get(i), jcbNumberType.getSelectedIndex() + com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
              break;
            }
          }
        }
      }
    }//GEN-LAST:event_jcbNumberTypeActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel customPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
  private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
  private javax.swing.JComboBox jcbNumberType;
  private com.openitech.db.components.JDbComboBox jcbSifrant;
  private javax.swing.JComboBox jcbStolpec;
  private javax.swing.JComboBox jcbType;
  private javax.swing.JPanel jpDateField;
  private javax.swing.JPanel jpFilterValues;
  private javax.swing.JPanel jpNumberField;
  private javax.swing.JPanel jpPreformattedField;
  private javax.swing.JPanel jpSifrantPanel;
  private javax.swing.JPanel jpTextField;
  private com.openitech.db.components.JDbDateTextField jtfDateValueDo;
  private com.openitech.db.components.JDbDateTextField jtfDateValueOd;
  private com.openitech.db.components.JDbTextField jtfNumberValue;
  private com.openitech.db.components.JDbTextField jtfPreformattedValue;
  private com.openitech.db.components.JDbTextField jtfSifrant;
  private com.openitech.db.components.JDbTextField jtfValue;
  private com.openitech.db.model.DbSifrantModel smSifrant;
  // End of variables declaration//GEN-END:variables

  private static class RezultatKlicaActiveRowChangeListener implements ActiveRowChangeListener {

    DataSourceFilters.RezultatiKlicaSeekType rezultatiKlicaSeekType;

    public RezultatKlicaActiveRowChangeListener(RezultatiKlicaSeekType rezultatiKlicaSeekType) {
      this.rezultatiKlicaSeekType = rezultatiKlicaSeekType;
    }

    @Override
    public void activeRowChanged(ActiveRowChangeEvent event) {
      event.getSource().removeActiveRowChangeListener(this);
      rezultatiKlicaSeekType.reload();
      rezultatiKlicaSeekType = null;
    }

    @Override
    public void fieldValueChanged(ActiveRowChangeEvent event) {
    }
  }

  private static class ExistsSeekTypeActiveRowChangeListener implements ActiveRowChangeListener {

    DataSourceFilters.RezultatExistsSeekType existsSeekTypeSeekType;

    public ExistsSeekTypeActiveRowChangeListener(RezultatExistsSeekType existsSeekType) {
      this.existsSeekTypeSeekType = existsSeekType;
    }

    @Override
    public void activeRowChanged(ActiveRowChangeEvent event) {
      event.getSource().removeActiveRowChangeListener(this);
      existsSeekTypeSeekType.reload();
      existsSeekTypeSeekType = null;
    }

    @Override
    public void fieldValueChanged(ActiveRowChangeEvent event) {
    }
  }

  private static class CheckBoxSeekTypeActiveRowChangeListener implements ActiveRowChangeListener {

    DataSourceFilters.CheckBoxSeekType existsSeekTypeSeekType;

    public CheckBoxSeekTypeActiveRowChangeListener(CheckBoxSeekType existsSeekType) {
      this.existsSeekTypeSeekType = existsSeekType;
    }

    @Override
    public void activeRowChanged(ActiveRowChangeEvent event) {
      event.getSource().removeActiveRowChangeListener(this);
      if (existsSeekTypeSeekType != null) {
        existsSeekTypeSeekType.reload();
        existsSeekTypeSeekType = null;
      }
    }

    @Override
    public void fieldValueChanged(ActiveRowChangeEvent event) {
    }
  }
}
