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
import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import com.openitech.db.model.DbComboBoxModel;
import com.openitech.db.model.xml.config.GridBagConstraints;
import com.openitech.db.model.xml.config.SeekLayout;
import com.openitech.util.Equals;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.jdesktop.swingx.JXDatePicker;

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
  private final PropertyChangeListener queryChanged = new PropertyChangeListener() {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      updateFilterMenuItem();
    }
  };

  private void updateColumns() {
    Vector<DataSourceFilters.AbstractSeekType<? extends Object>> headers = new Vector<DataSourceFilters.AbstractSeekType<? extends Object>>();
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

    documents.clear();

    for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : filters.entrySet()) {
      java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

      for (int i = 0; i < seekTypeList.size(); i++) {
        final DataSourceFilters.AbstractSeekType<? extends Object> item = seekTypeList.get(i);
        final boolean addToPanel = item.getLayout() != null;
        final SeekLayout layout = item.getLayout();

        if (!item.isAutomatic()) {
          documents.put(item, item.getDocuments());
        } else {
          if (!addToPanel) {
            headers.add(item);
          }
          if ((parentFilterPanel != null) && (parentFilterPanel.documents.containsKey(item))) {
            documents.put(item, parentFilterPanel.documents.get(item));
            if (parentFilterPanel.sifranti.containsKey(item)) {
              sifranti.put(item, parentFilterPanel.sifranti.get(item));
            }
          } else if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            javax.swing.text.Document from = new com.openitech.db.components.JDbDateTextField().getDocument();
            javax.swing.text.Document to = new com.openitech.db.components.JDbDateTextField().getDocument();

            item.setDocuments(entry.getKey(), new javax.swing.text.Document[]{from, to});

            documents.put(item, new javax.swing.text.Document[]{from, to});
            if (addToPanel) {
              JPanel jpHoldingPanel = new javax.swing.JPanel();
              JLabel jlOd = new javax.swing.JLabel();
              jlOd.setText(item.toString() + " od");
              JLabel jlDo = new javax.swing.JLabel();
              jlDo.setText("do");
              JDbDateTextField jtfDateValueOd = new com.openitech.db.components.JDbDateTextField();
              jtfDateValueOd.setSearchField(true);
              JDbDateTextField jtfDateValueDo = new com.openitech.db.components.JDbDateTextField();
              jtfDateValueDo.setSearchField(true);

              jtfDateValueOd.setDocument(documents.get(item)[0]);
              jtfDateValueDo.setDocument(documents.get(item)[1]);

              JXDatePicker jXDatePicker = new org.jdesktop.swingx.JXDatePicker();
              JXDatePicker jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
              jXDatePicker.setEditor(jtfDateValueOd);
              jXDatePicker2.setEditor(jtfDateValueDo);

              jpHoldingPanel.setLayout(new java.awt.GridBagLayout());

              jpHoldingPanel.add(jlOd, new java.awt.GridBagConstraints());
              jpHoldingPanel.add(jXDatePicker, new java.awt.GridBagConstraints());

              jpHoldingPanel.add(jlDo, new java.awt.GridBagConstraints());
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
              jpHoldingPanel.add(jXDatePicker2, gridBagConstraints);

              if (layout.getGridBagConstraints() == null) {
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
              } else {
                gridBagConstraints = getCustomGridBagConstraints(layout);
              }
              customPanel.add(jpHoldingPanel, gridBagConstraints);


            }
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            javax.swing.text.Document document = new com.openitech.db.components.JDbTextField().getDocument();
            document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));

            documents.put(item, new javax.swing.text.Document[]{document});
            sifranti.put(item, ((DataSourceFilters.SifrantSeekType) item).getModel());

            if (addToPanel) {


              JPanel jpHoldingPanel = new javax.swing.JPanel();
              JLabel jlOpis = new javax.swing.JLabel();
              JDbTextField jtfSifra = new com.openitech.db.components.JDbTextField();
              final JDbComboBox jcbSifrant = new com.openitech.db.components.JDbComboBox();

              jpHoldingPanel.setLayout(new java.awt.GridBagLayout());

              jlOpis.setText(item.toString());
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
              jpHoldingPanel.add(jlOpis, gridBagConstraints);

              jtfSifra.setColumns(4);
              jtfSifra.setText("");
              jtfSifra.setDocument(documents.get(item)[0]);
              jpHoldingPanel.add(jtfSifra, new java.awt.GridBagConstraints());

              jcbSifrant.setModel(sifranti.get(item));
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.weightx = 1.0;
              jpHoldingPanel.add(jcbSifrant, gridBagConstraints);

              if (layout.getGridBagConstraints() == null) {
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
              } else {
                gridBagConstraints = getCustomGridBagConstraints(layout);
              }
              customPanel.add(jpHoldingPanel, gridBagConstraints);

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

            } else {
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
          } else if (item instanceof DataSourceFilters.IntegerSeekType) {
            javax.swing.text.Document document = new com.openitech.db.components.JDbTextField().getDocument();
            document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));
            documents.put(item, new javax.swing.text.Document[]{document});

            if (addToPanel) {
              final JPanel jpHoldingPanel = new javax.swing.JPanel();
              final JLabel jlOpis = new javax.swing.JLabel();
              final JComboBox jDbComboBox1 = new JComboBox();
              final JDbTextField jDbTextField1 = new com.openitech.db.components.JDbTextField();

              jpHoldingPanel.setLayout(new java.awt.GridBagLayout());

              jlOpis.setText(item.toString());
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
              jpHoldingPanel.add(jlOpis, gridBagConstraints);

              jcbNumberType.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"je enako", "je veèje ali enako od", "je manjše ali enako kot"}));
              jDbComboBox1.setFocusable(false);
              jDbComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                  if (!refreshing) {
                    filters.getFilterFor(item).setSeekType(item, jDbComboBox1.getSelectedIndex() + com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
                  }
                }
              });
              jpHoldingPanel.add(jDbComboBox1, new java.awt.GridBagConstraints());

              jDbTextField1.setText("");
              jDbTextField1.setSearchField(true);
              jDbTextField1.setDocument(documents.get(item)[0]);
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.weightx = 1.0;
              jpHoldingPanel.add(jDbTextField1, gridBagConstraints);

              if (layout.getGridBagConstraints() == null) {
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
              } else {
                gridBagConstraints = getCustomGridBagConstraints(layout);
              }
              customPanel.add(jpHoldingPanel, gridBagConstraints);

              if ((item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS) >= jcbNumberType.getItemCount()) {
                item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
              }
              jDbComboBox1.setSelectedIndex(item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);

            }
          } else {
            javax.swing.text.Document document = new com.openitech.db.components.JDbTextField().getDocument();
            document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));
            documents.put(item, new javax.swing.text.Document[]{document});

            if (addToPanel) {
              final JPanel jpHoldingPanel = new javax.swing.JPanel();
              final JLabel jlOpis = new javax.swing.JLabel();
              final JComboBox jDbComboBox1 = new JComboBox();
              final JDbTextField jDbTextField1 = new com.openitech.db.components.JDbTextField();

              jpHoldingPanel.setLayout(new java.awt.GridBagLayout());

              jlOpis.setText(item.toString());
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
              jpHoldingPanel.add(jlOpis, gridBagConstraints);

              jDbComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"je enako", "se zaène z", "se konèa z", "vsebuje"}));
              jDbComboBox1.setSelectedIndex(1);
              jDbComboBox1.setFocusable(false);
              jDbComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                  if (!refreshing) {

                    filters.getFilterFor(item).setSeekType(item, jDbComboBox1.getSelectedIndex());
                  }
                }
              });
              jpHoldingPanel.add(jDbComboBox1, new java.awt.GridBagConstraints());

              jDbTextField1.setText("");
              jDbTextField1.setColumns(20);
              jDbTextField1.setSearchField(true);
              jDbTextField1.setDocument(documents.get(item)[0]);
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.weightx = 1.0;
              jpHoldingPanel.add(jDbTextField1, gridBagConstraints);

              if (layout.getGridBagConstraints() == null) {
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
                gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
              } else {
                gridBagConstraints = getCustomGridBagConstraints(layout);
              }
              customPanel.add(jpHoldingPanel, gridBagConstraints);
            }
          }
        }
      }

      entry.getKey().addPropertyChangeListener("query", queryChanged);
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

  private java.awt.GridBagConstraints getCustomGridBagConstraints(SeekLayout layout) {
    GridBagConstraints customGridBagConstraints = layout.getGridBagConstraints();
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = customGridBagConstraints.getGridX();
    gridBagConstraints.gridy = customGridBagConstraints.getGridY();
    gridBagConstraints.gridwidth = customGridBagConstraints.getGridWidth();
    gridBagConstraints.gridwidth = customGridBagConstraints.getGridWidth();
    gridBagConstraints.fill = customGridBagConstraints.getFill();
    gridBagConstraints.anchor = customGridBagConstraints.getAnchor();
    gridBagConstraints.weightx = customGridBagConstraints.getWeightX();
    gridBagConstraints.weighty = customGridBagConstraints.getWeightY();
    try {
      if (customGridBagConstraints.getInsets() != null) {
        List<Object> insetsValue = customGridBagConstraints.getInsets().getInsetsValue();
        if (insetsValue != null) {
          gridBagConstraints.insets = new java.awt.Insets((Integer) insetsValue.get(0), (Integer) insetsValue.get(1), (Integer) insetsValue.get(2), (Integer) insetsValue.get(3));
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, ex);
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
        if (item.getLayout() == null) {
          if (item instanceof DataSourceFilters.BetweenDateSeekType) {
            jtfDateValueOd.setDocument(documents.get(item)[0]);
            jtfDateValueDo.setDocument(documents.get(item)[1]);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "DATEFIELD_CARD");
          } else if (item instanceof DataSourceFilters.SifrantSeekType) {
            jtfSifrant.setDocument(documents.get(item)[0]);
            jcbSifrant.setModel(sifranti.get(item));
            updateSifrant(documents.get(item)[0], jcbSifrant);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "SIFRANT_CARD");
          } else if (item.getSeekType() == com.openitech.db.filters.DataSourceFilters.SeekType.PREFORMATTED) {
            jtfPreformattedValue.setDocument(documents.get(item)[0]);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "PREFORMATTED_CARD");
          } else if (item instanceof DataSourceFilters.IntegerSeekType) {
            if ((item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS) >= jcbNumberType.getItemCount()) {
              item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
            }
            jcbNumberType.setSelectedIndex(item.getSeekType() - com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
            jtfNumberValue.setDocument(documents.get(item)[0]);
            ((CardLayout) jpFilterValues.getLayout()).show(jpFilterValues, "NUMBERFIELD_CARD");
          } else {
            if (item.getSeekType() >= jcbType.getItemCount()) {
              item.setSeekType(com.openitech.db.filters.DataSourceFilters.SeekType.UPPER_EQUALS);
            }
            jcbType.setSelectedIndex(item.getSeekType());
            jtfValue.setDocument(documents.get(item)[0]);
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

    customPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    add(customPanel, gridBagConstraints);

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
        .addGap(110, 110, 110)
        .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(47, Short.MAX_VALUE))
    );
    jpDateFieldLayout.setVerticalGroup(
      jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpDateFieldLayout.createSequentialGroup()
        .addGroup(jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2)
          .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(13, Short.MAX_VALUE))
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
        .addComponent(jtfValue, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
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
        .addGap(228, 228, 228)
        .addComponent(jcbSifrant, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
    );
    jpSifrantPanelLayout.setVerticalGroup(
      jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpSifrantPanelLayout.createSequentialGroup()
        .addGroup(jpSifrantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jcbSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jtfSifrant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jpFilterValues.add(jpSifrantPanel, "SIFRANT_CARD");

    jtfPreformattedValue.setColumns(20);
    jtfPreformattedValue.setSearchField(true);

    javax.swing.GroupLayout jpPreformattedFieldLayout = new javax.swing.GroupLayout(jpPreformattedField);
    jpPreformattedField.setLayout(jpPreformattedFieldLayout);
    jpPreformattedFieldLayout.setHorizontalGroup(
      jpPreformattedFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jtfPreformattedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
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
        .addComponent(jtfNumberValue, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
    );
    jpNumberFieldLayout.setVerticalGroup(
      jpNumberFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpNumberFieldLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addGroup(jpNumberFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
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
        DataSourceFilters.AbstractSeekType<? extends Object> item = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();

        filters.getFilterFor(item).setSeekType(item, jcbType.getSelectedIndex());
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
        DataSourceFilters.AbstractSeekType<? extends Object> item = (DataSourceFilters.AbstractSeekType<? extends Object>) jcbStolpec.getSelectedItem();

        filters.getFilterFor(item).setSeekType(item, jcbNumberType.getSelectedIndex() + com.openitech.db.filters.DataSourceFilters.SeekType.EQUALS);
      }
    }//GEN-LAST:event_jcbNumberTypeActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel customPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
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
}
