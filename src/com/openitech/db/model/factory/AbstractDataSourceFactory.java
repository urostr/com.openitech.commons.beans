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
package com.openitech.db.model.factory;

import com.openitech.db.model.FieldObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.filters.JWorkAreaFilter;
import com.openitech.db.model.AutoInsertValue;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.xml.config.Workarea.TableDoubleClick;
import com.openitech.importer.JEventsImporter;
import com.openitech.importer.JImportEventsModel;
import com.openitech.swing.framework.context.AssociatedTasks;
import com.openitech.value.fields.Field;
import java.awt.Component;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author domenbasic
 */
public abstract class AbstractDataSourceFactory extends com.openitech.db.model.factory.DataSourceParametersFactory<DataSourceConfig> implements AssociatedTasks {

  protected final DbDataModel dbDataModel;
  protected com.openitech.db.model.xml.config.Workarea dataSourceXML;

  public AbstractDataSourceFactory(DbDataModel dbDataModel) {
    this.dbDataModel = dbDataModel;
  }

  public AbstractDataSourceFactory() {
    this(null);
  }
  protected List<Object> additionalParameters;

  /**
   * Get the value of additionalParameters
   *
   * @return the value of additionalParameters
   */
  public List<Object> getAdditionalParameters() {
    if (additionalParameters == null) {
      additionalParameters = new java.util.ArrayList<Object>();
    }
    return additionalParameters;
  }

  public void configure(String opis, Class clazz, String resourceName) throws SQLException, JAXBException {
    configure(this, opis, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel), clazz, resourceName);
  }

  public void configure(String opis, Clob resource) throws SQLException, JAXBException {
    configure(opis, resource, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel));
  }

  public void configure(String opis, String xml) throws SQLException, JAXBException {
    configure(opis, unmarshal(xml, null, null, null), new com.openitech.db.model.factory.DataSourceConfig(dbDataModel));
  }

  public void configure(String opis, String xml, com.openitech.db.model.factory.DataSourceConfig config) throws SQLException, JAXBException {
    configure(opis, unmarshal(xml, null, null, null), config);
  }

  public void configure(String opis, Clob resource, com.openitech.db.model.factory.DataSourceConfig config) throws SQLException, JAXBException {
    configure(opis, unmarshal(null, resource, null, null), config);
  }

  public void configure(String opis, com.openitech.db.model.xml.config.Workarea workareaXML, com.openitech.db.model.factory.DataSourceConfig config) throws SQLException {
    try {
      configure(this, opis, config, workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  protected void configure(final AbstractDataSourceFactory waConfig, final String opis, com.openitech.db.model.factory.DataSourceConfig config, Class clazz, String resourceName) throws SQLException, JAXBException {
    try {
      configure(waConfig, opis, config, unmarshal(null, null, clazz, resourceName));
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private com.openitech.db.model.xml.config.Workarea unmarshal(String xml, Clob resource, Class clazz, String resourceName) throws SQLException, JAXBException {
    com.openitech.db.model.xml.config.Workarea result = null;
    if (xml != null) {
      result = (com.openitech.db.model.xml.config.Workarea) JaxbUnmarshaller.getInstance().unmarshall(com.openitech.db.model.xml.config.Workarea.class, xml);
    } else if (resource != null) {
      result = (com.openitech.db.model.xml.config.Workarea) JaxbUnmarshaller.getInstance().unmarshall(com.openitech.db.model.xml.config.Workarea.class, resource);
    } else if (clazz != null && resourceName != null) {
      result = (com.openitech.db.model.xml.config.Workarea) JaxbUnmarshaller.getInstance().unmarshall(com.openitech.db.model.xml.config.Workarea.class, clazz.getResourceAsStream(resourceName));
    }
    return result;
  }

  protected void configure(final AbstractDataSourceFactory factory, final String opis, com.openitech.db.model.factory.DataSourceConfig config, com.openitech.db.model.xml.config.Workarea dataSourceXML) throws SQLException, ClassNotFoundException {
    factory.opis = opis;
    factory.config = config;
    factory.dataSourceXML = dataSourceXML;

    factory.configure();
  }

  ;

  public abstract void configure() throws SQLException, ClassNotFoundException;
//   
  protected DbTableModel tableModel;

  /**
   * Get the value of tableModel
   *
   * @return the value of tableModel
   */
  public DbTableModel getTableModel() {
    return tableModel;
  }
  protected List<FieldObserver> fieldObservers = new ArrayList<FieldObserver>();
  protected Set<Field> dataEntryValues = new java.util.HashSet<com.openitech.value.fields.Field>();
  protected String opis;

  /**
   * Get the value of opis
   *
   * @return the value of opis
   */
  public String getOpis() {
    return opis;
  }

  /**
   * Get the value of dataEntryValues
   *
   * @return the value of dataEntryValues
   */
  public Set<Field> getDataEntryValues() {
    return dataEntryValues;
  }
  protected List<JImportEventsModel> imporEventsModels = new ArrayList<JImportEventsModel>();

  /**
   *
   * @deprecated Use getEventImportersModels instead
   **/
  @Deprecated
  public List<JImportEventsModel> getImporEventsModels() {
    return imporEventsModels;
  }
  protected List<JEventsImporter> eventImportersModels = new ArrayList<JEventsImporter>();

  public List<JEventsImporter> getEventImportersModels() {
    return eventImportersModels;
  }
  protected boolean taskList;

  /**
   * Get the value of taskList
   *
   * @return the value of taskList
   */
  public boolean isTaskList() {
    return taskList;
  }
  protected Component filterPanel;

  /**
   * Get the value of filterPanel
   *
   * @return the value of filterPanel
   */
  public Component getFilterPanel() {
    return filterPanel;
  }

  /**
   * Set the value of filterPanel
   *
   * @param filterPanel new value of filterPanel
   */
  public void setFilterPanel(Component filterPanel) {
    this.filterPanel = filterPanel;
  }
  protected Map<String, Component> informationPanels = new LinkedHashMap<String, Component>();

  /**
   * Get the value of informationPanels
   *
   * @return the value of informationPanels
   */
  public Map<String, Component> getInformationPanels() {
    return informationPanels;
  }
  protected Map<Integer, List<JPanel>> workSpaceInformationPanels = new LinkedHashMap<Integer, List<JPanel>>();

  /**
   * Get the value of informationPanels
   *
   * @return the value of informationPanels
   */
  public Map<Integer, List<JPanel>> getWorkSpaceInformationPanels() {
    return workSpaceInformationPanels;
  }
  protected Map<String, Object> linkedObjects = new LinkedHashMap<String, Object>();

  /**
   * Get the value of informationPanels
   *
   * @return the value of informationPanels
   */
  public Map<String, Object> getLinkedObjects() {
    return linkedObjects;
  }
  protected DataSourceFiltersMap filtersMap;

  public DataSourceFiltersMap getFiltersMap() {
    return filtersMap;
  }
  protected List<JWorkAreaFilter> workAreaFilters = new ArrayList<JWorkAreaFilter>();

  public List<JWorkAreaFilter> getWorkAreaFilters() {
    return workAreaFilters;
  }
  protected List<AutoInsertValue> autoInsertValues = new ArrayList<AutoInsertValue>();

  public List<AutoInsertValue> getAutoInsertValue() {
    return autoInsertValues;
  }
  protected TableDoubleClick tableDoubleClick = null;

  public TableDoubleClick getTableDoubleClick() {
    return tableDoubleClick;
  }
  protected boolean disabledTab = false;

  public boolean isDisabledTab() {
    return disabledTab;
  }
  protected boolean canExportData = true;

  /**
   * Get the value of canExportData
   *
   * @return the value of canExportData
   */
  public boolean isCanExportData() {
    return canExportData;
  }

  /**
   * Set the value of canExportData
   *
   * @param canExportData new value of canExportData
   */
  public void setCanExportData(boolean canExportData) {
    this.canExportData = canExportData;
  }
  protected List<JMenu> viewMenuItems = new ArrayList<JMenu>();

  /**
   * Get the value of viewMenuItems
   *
   * @return the value of viewMenuItems
   */
  public List<JMenu> getViewMenuItems() {
    if (canExportData) {
      return viewMenuItems;
    } else {
      return new ArrayList<JMenu>();
    }
  }
  private java.util.List<JXTaskPane> taskPanes = new java.util.ArrayList<JXTaskPane>();

  @Override
  public List<JXTaskPane> getTaskPanes() {
    return taskPanes;
  }

  @Override
  public boolean add(JXTaskPane taskPane) {
    return taskPanes.add(taskPane);
  }

  @Override
  public boolean remove(JXTaskPane taskPane) {
    return taskPanes.remove(taskPane);
  }
  protected JComponent dataEntryPanel;

  /**
   * Get the value of dataEntryPanel
   *
   * @return the value of dataEntryPanel
   */
  public JComponent getDataEntryPanel() {
    return dataEntryPanel;
  }

  /**
   * Set the value of dataEntryPanel
   *
   * @param dataEntryPanel new value of dataEntryPanel
   */
  public void setDataEntryPanel(JComponent dataEntryPanel) {
    this.dataEntryPanel = dataEntryPanel;
  }
  protected boolean readOnly;

  /**
   * Get the value of readOnly
   *
   * @return the value of readOnly
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * Set the value of readOnly
   *
   * @param readOnly new value of readOnly
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
}
