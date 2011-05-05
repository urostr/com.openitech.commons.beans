/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.FieldObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbTableModel;
import com.openitech.swing.framework.context.AssociatedTasks;
import com.openitech.value.fields.Field;
import java.awt.Component;
import java.io.StringReader;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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

  /**
   * Get the value of dbDataModel
   *
   * @return the value of dbDataModel
   */
  public DbDataModel getDbDataModel() {
    return dbDataModel;
  }

  public void configure(String opis, Class clazz, String resourceName) throws SQLException, JAXBException {
    configure(this, opis, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel), clazz, resourceName);
  }

  public void configure(String opis, Clob resource) throws SQLException, JAXBException {
    configure(opis, resource, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel));
  }

  public void configure(String opis, String xml) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(new StringReader(xml));
    configure(opis, workareaXML, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel));
  }

  public void configure(String opis, Clob resource, com.openitech.db.model.factory.DataSourceConfig config) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(resource.getCharacterStream());
    configure(opis, workareaXML, config);
  }

  public void configure(String opis, com.openitech.db.model.xml.config.Workarea workareaXML, com.openitech.db.model.factory.DataSourceConfig config) throws SQLException {
    try {
      configure(this, opis, config, workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  protected void configure(final AbstractDataSourceFactory waConfig, final String opis, com.openitech.db.model.factory.DataSourceConfig config, Class clazz, String resourceName) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(clazz.getResourceAsStream(resourceName));
    try {
      configure(waConfig, opis, config, workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
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
  protected DataSourceLimit.Limit limit = DataSourceLimit.Limit.LALL;

  /**
   * Get the value of limit
   *
   * @return the value of limit
   */
  public DataSourceLimit.Limit getLimit() {
    return limit;
  }

  /**
   * Set the value of limit
   *
   * @param limit new value of limit
   */
  public void setLimit(DataSourceLimit.Limit limit) {
    this.limit = limit;
  }

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
