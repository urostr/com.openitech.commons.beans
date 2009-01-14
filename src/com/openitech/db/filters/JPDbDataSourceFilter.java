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

import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.filters.DataSourceFilters.AbstractSeekType;
import com.openitech.db.filters.DataSourceFilters.SeekType;
import com.openitech.db.filters.FilterDocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.text.BadLocationException;

/**
 *
 * @author uros
 */
public class JPDbDataSourceFilter extends javax.swing.JPanel {

  private final FiltersMap filters = new FiltersMap();
  private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document> documents = new HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document>();

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

  public FiltersMap getFilters() {
    return filters;
  }
  private JMenu filterMenuItem = new JMenu("Aktivni filtri");

  public JMenu getFilterMenuItem() {
    updateFilterMenuItem();
    return filterMenuItem;
  }

  private void updateFilterMenuItem() {
    filterMenuItem.removeAll();
    for (Map.Entry<DataSourceFilters.AbstractSeekType<? extends Object>, javax.swing.text.Document> entry : documents.entrySet()) {
      if (entry.getValue().getLength() > 0) {
        DataSourceFilters.AbstractSeekType<? extends Object> seekType = entry.getKey();
        JCheckBoxMenuItem miCheckbox = new JCheckBoxMenuItem(seekType.getDescription(), true);
        miCheckbox.setActionCommand("CLEAR");
        miCheckbox.addActionListener(new ToggleFilter(seekType));

        filterMenuItem.add(miCheckbox);
      }
    }
    firePropertyChange("filter_menu", filterMenuItem, null);
  }

  private class ToggleFilter implements ActionListener {

    DataSourceFilters.AbstractSeekType<? extends Object> seekType;

    public ToggleFilter(DataSourceFilters.AbstractSeekType<? extends Object> seekType) {
      this.seekType = seekType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equalsIgnoreCase("CLEAR") && documents.containsKey(seekType)) {
        javax.swing.text.Document document = documents.get(seekType);
        try {
          document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
          Logger.getLogger(JPDbDataSourceFilter.class.getName()).log(Level.WARNING, null, ex);
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

        headers.add(item);
        javax.swing.text.Document document;
        if (item instanceof DataSourceFilters.DateSeekType) {
          document = new com.openitech.db.components.JDbDateTextField().getDocument();
        } else {
          document = new com.openitech.db.components.JDbTextField().getDocument();
        }
        document.addDocumentListener(new FilterDocumentListener(entry.getKey(), item));
        documents.put(item, document);
      }

      entry.getKey().addPropertyChangeListener("query", queryChanged);
    }
    if (headers.size()==0) {
      jcbStolpec.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ni doloèen" }));
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

      if (item instanceof DataSourceFilters.DateSeekType) {
        jtfDateValue.setDocument(documents.get(item));
        lpFilterValues.setPosition(jpDateField, 0);
      } else {
        jcbType.setSelectedIndex(item.getSeekType());
        jtfValue.setDocument(documents.get(item));
        lpFilterValues.setPosition(jpTextField, 0);
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

    jtfDateValue = new com.openitech.db.components.JDbDateTextField();
    jpTextField = new javax.swing.JPanel();
    jcbType = new javax.swing.JComboBox();
    jtfValue = new com.openitech.db.components.JDbTextField();
    jpDateField = new javax.swing.JPanel();
    jcbDateType = new javax.swing.JComboBox();
    jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
    jcbStolpec = new javax.swing.JComboBox();
    lpFilterValues = new javax.swing.JLayeredPane();

    jtfDateValue.setSearchField(true);

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
        .addComponent(jtfValue, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
    );
    jpTextFieldLayout.setVerticalGroup(
      jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpTextFieldLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addGroup(jpTextFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(jcbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jtfValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    jcbDateType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ni doloèen", "je enak", "je veèji ali enak", "je manjši ali enak" }));
    jcbDateType.setFocusable(false);
    jcbDateType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jcbDateTypeActionPerformed(evt);
      }
    });

    jXDatePicker1.setEditor(jtfDateValue);

    javax.swing.GroupLayout jpDateFieldLayout = new javax.swing.GroupLayout(jpDateField);
    jpDateField.setLayout(jpDateFieldLayout);
    jpDateFieldLayout.setHorizontalGroup(
      jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpDateFieldLayout.createSequentialGroup()
        .addComponent(jcbDateType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(20, Short.MAX_VALUE))
    );
    jpDateFieldLayout.setVerticalGroup(
      jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jpDateFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(jcbDateType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

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

    lpFilterValues.setLayout(new java.awt.BorderLayout());

    lpFilterValues.add(jpDateField, java.awt.BorderLayout.CENTER);
    lpFilterValues.add(jpTextField, java.awt.BorderLayout.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
    add(lpFilterValues, gridBagConstraints);
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

    private void jcbDateTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbDateTypeActionPerformed
      // TODO add your handling code here:
}//GEN-LAST:event_jcbDateTypeActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
  private javax.swing.JComboBox jcbDateType;
  private javax.swing.JComboBox jcbStolpec;
  private javax.swing.JComboBox jcbType;
  private javax.swing.JPanel jpDateField;
  private javax.swing.JPanel jpTextField;
  private com.openitech.db.components.JDbDateTextField jtfDateValue;
  private com.openitech.db.components.JDbTextField jtfValue;
  private javax.swing.JLayeredPane lpFilterValues;
  // End of variables declaration//GEN-END:variables

  public static class FiltersMap implements java.util.Map<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private final java.util.Map<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> seekTypes = new java.util.LinkedHashMap<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>>();
    private final java.util.Map<DataSourceFilters.AbstractSeekType<? extends Object>, DataSourceFilters> filters = new java.util.HashMap<DataSourceFilters.AbstractSeekType<? extends Object>, DataSourceFilters>();

    public int size() {
      return seekTypes.size();
    }

    private void updateFilters() {
      filters.clear();
      for (java.util.Map.Entry<DataSourceFilters, java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>>> entry : seekTypes.entrySet()) {
        java.util.List<DataSourceFilters.AbstractSeekType<? extends Object>> seekTypeList = entry.getValue();

        for (int i = 0; i < seekTypeList.size(); i++) {
          filters.put(seekTypeList.get(i), entry.getKey());
        }
      }

    }

    public DataSourceFilters getFilterFor(DataSourceFilters.AbstractSeekType<? extends Object> seekType) {
      return filters.get(seekType);
    }

    @Override
    public boolean isEmpty() {
      return seekTypes.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return seekTypes.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return seekTypes.containsValue(value);
    }

    @Override
    public List<AbstractSeekType<? extends Object>> get(Object key) {
      return seekTypes.get(key);
    }

    public void put(DataSourceFilters key, DataSourceFilters.AbstractSeekType<? extends Object> value) {
      List<AbstractSeekType<? extends Object>> list;
      if (seekTypes.containsKey(key)) {
        list = seekTypes.get(key);
      } else {
        list = new ArrayList<AbstractSeekType<? extends Object>>();
      }
      if (!list.contains(value)) {
        list.add(value);
      }
      put(key,list);
    }

    @Override
    public List<AbstractSeekType<? extends Object>> put(DataSourceFilters key, List<AbstractSeekType<? extends Object>> value) {
      List<AbstractSeekType<? extends Object>> result = seekTypes.put(key, value);
      updateFilters();
      firePropertyChange("model", false, true);
      return result;
    }

    @Override
    public List<AbstractSeekType<? extends Object>> remove(Object key) {
      List<AbstractSeekType<? extends Object>> result = null;
      if (seekTypes.containsKey(key)) {
        result = seekTypes.remove(key);
        updateFilters();
        firePropertyChange("removed", key, null);
        firePropertyChange("model", result, null);
      }
      return result;
    }

    @Override
    public void putAll(Map<? extends DataSourceFilters, ? extends List<AbstractSeekType<? extends Object>>> m) {
      seekTypes.putAll(m);
      updateFilters();
      firePropertyChange("model", false, true);
    }

    @Override
    public void clear() {
      firePropertyChange("clear", false, true);
      seekTypes.clear();
      updateFilters();
      firePropertyChange("model", false, true);
    }

    @Override
    public Set<DataSourceFilters> keySet() {
      return seekTypes.keySet();
    }

    @Override
    public Collection<List<AbstractSeekType<? extends Object>>> values() {
      return seekTypes.values();
    }

    @Override
    public Set<Entry<DataSourceFilters, List<AbstractSeekType<? extends Object>>>> entrySet() {
      return seekTypes.entrySet();
    }

    /**
     * Adds a PropertyChangeListener to the listener list.
     * If <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param    listener  the property change listener to be added
     *
     * @see #removePropertyChangeListener
     * @see #getPropertyChangeListeners
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
      if (listener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener from the listener list. This method
     * should be used to remove PropertyChangeListeners that were registered
     * for all bound properties of this class.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
      if (listener == null || changeSupport == null) {
        return;
      }
      changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Returns an array of all the property change listeners
     * registered on this component.
     *
     * @return all of this component's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered
     *
     * @see      #addPropertyChangeListener
     * @see      #removePropertyChangeListener
     * @see      #getPropertyChangeListeners(java.lang.String)
     * @see      java.beans.PropertyChangeSupport#getPropertyChangeListeners
     * @since    1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners();
    }

    /**
     * Adds a PropertyChangeListener to the listener list for a specific
     * property. If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName one of the property names listed above
     * @param listener the property change listener to be added
     *
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
      if (listener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener
     * list for a specific property. This method should be used to remove
     * <code>PropertyChangeListener</code>s
     * that were registered for a specific bound property.
     * <p>
     * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName a valid property name
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
      if (listener == null || changeSupport == null) {
        return;
      }
      changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @return all of the <code>PropertyChangeListener</code>s associated with
     *         the named property; if no such listeners have been added or
     *         if <code>propertyName</code> is <code>null</code>, an empty
     *         array is returned
     *
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners
     * @since 1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName) {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners(propertyName);
    }

    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null ||
              (oldValue != null && newValue != null && oldValue.equals(newValue))) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Support for reporting bound property changes for boolean properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     * @since 1.4
     */
    protected void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Support for reporting bound property changes for integer properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     * @since 1.4
     */
    protected void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
      PropertyChangeSupport changeSupport = this.changeSupport;
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a byte)
     * @param newValue the new value of the property (as a byte)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, Byte.valueOf(oldValue), Byte.valueOf(newValue));
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a char)
     * @param newValue the new value of the property (as a char)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, new Character(oldValue), new Character(newValue));
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a short)
     * @param newValue the old value of the property (as a short)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, Short.valueOf(oldValue), Short.valueOf(newValue));
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a long)
     * @param newValue the new value of the property (as a long)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, Long.valueOf(oldValue), Long.valueOf(newValue));
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a float)
     * @param newValue the new value of the property (as a float)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, Float.valueOf(oldValue), Float.valueOf(newValue));
    }

    /**
     * Reports a bound property change.
     *
     * @param propertyName the programmatic name of the property
     *          that was changed
     * @param oldValue the old value of the property (as a double)
     * @param newValue the new value of the property (as a double)
     * @see #firePropertyChange(java.lang.String, java.lang.Object,
     *          java.lang.Object)
     * @since 1.5
     */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
      if (changeSupport == null || oldValue == newValue) {
        return;
      }
      firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
    }
  }
}
