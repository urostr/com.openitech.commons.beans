/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.FieldObserver;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbTableModel;
import com.openitech.value.fields.Field;
import com.openitech.sql.util.SqlUtilities;
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
import javax.swing.JMenu;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author domenbasic
 */
public abstract class AbstractDataSourceFactory {

  protected final DbDataModel dbDataModel;

  public AbstractDataSourceFactory(DbDataModel dbDataModel) {
    this.dbDataModel = dbDataModel;
  }

  public AbstractDataSourceFactory() {
    this(null);
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
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(resource.getCharacterStream());
    try {
      configure(this, opis, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel), workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void configure(String opis, String xml) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(new StringReader(xml));
    try {
      configure(this, opis, new com.openitech.db.model.factory.DataSourceConfig(dbDataModel), workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void configure(final AbstractDataSourceFactory waConfig, final String opis, com.openitech.db.model.factory.DataSourceConfig config, Class clazz, String resourceName) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(clazz.getResourceAsStream(resourceName));
    try {
      configure(waConfig, opis, config, workareaXML);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public abstract void configure(final AbstractDataSourceFactory factory, final String opis, com.openitech.db.model.factory.DataSourceConfig config, com.openitech.db.model.xml.config.Workarea dataSourceXML) throws SQLException, ClassNotFoundException;

//   
  protected DbDataSource dataSource;

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }
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
  protected List<JMenu> viewMenuItems = new ArrayList<JMenu>();

  /**
   * Get the value of viewMenuItems
   *
   * @return the value of viewMenuItems
   */
  public List<JMenu> getViewMenuItems() {
    return viewMenuItems;
  }

  public static String getReplacedSql(String sql) {
    sql = sql.replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
    sql = sql.replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
    sql = sql.replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
    sql = sql.replaceAll("<%TS%>", Long.toString(System.currentTimeMillis()));

    return sql;
  }

  public static String[] getReplacedSqls(String[] sqls) {
    for (int i = 0; i < sqls.length; i++) {
      sqls[i] = sqls[i].replaceAll("<%ChangeLog%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.CHANGE_LOG_DB, SqlUtilities.CHANGE_LOG_DB));
      sqls[i] = sqls[i].replaceAll("<%RPP%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPP_DB, SqlUtilities.RPP_DB));
      sqls[i] = sqls[i].replaceAll("<%RPE%>", SqlUtilities.DATABASES.getProperty(SqlUtilities.RPE_DB, SqlUtilities.RPE_DB));
      sqls[i] = sqls[i].replaceAll("<%TS%>", Long.toString(System.currentTimeMillis()));
    }

    return sqls;
  }
}
