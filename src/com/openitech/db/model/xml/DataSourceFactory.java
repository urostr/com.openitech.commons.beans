/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.xml;

import com.openitech.db.DataSourceObserver;
import com.openitech.db.FieldObserver;
import com.openitech.db.filters.ActiveFiltersReader;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.filters.DataSourceFiltersMap;
import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.concurrent.DataSourceEvent;
import com.openitech.db.model.sql.PendingSqlParameter;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.db.model.xml.config.SubQuery;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.db.model.xml.config.Workarea.DataModel.TableColumns.TableColumnDefinition;
import com.openitech.db.model.xml.config.Workarea.DataSource.Parameters;
import com.openitech.sql.Field;
import com.openitech.sql.FieldValueProxy;
import com.openitech.sql.util.SqlUtilities;
import groovy.lang.GroovyClassLoader;
import java.awt.Component;
import java.lang.reflect.Constructor;
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
 * @author uros
 */
public class DataSourceFactory {

  private final DbDataModel dbDataModel;

  public DataSourceFactory(DbDataModel dbDataModel) {
    this.dbDataModel = dbDataModel;
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
    configure(this, opis, new com.openitech.db.model.xml.DataSourceConfig(dbDataModel), clazz, resourceName);
  }

  public void configure(String opis, Clob resource) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(resource.getCharacterStream());

    configure(this, opis, new com.openitech.db.model.xml.DataSourceConfig(dbDataModel), workareaXML);
  }

  public static void configure(final DataSourceFactory waConfig, final String opis, com.openitech.db.model.xml.DataSourceConfig config, Class clazz, String resourceName) throws SQLException, JAXBException {
    Unmarshaller unmarshaller = JAXBContext.newInstance(com.openitech.db.model.xml.config.Workarea.class).createUnmarshaller();
    com.openitech.db.model.xml.config.Workarea workareaXML = (com.openitech.db.model.xml.config.Workarea) unmarshaller.unmarshal(clazz.getResourceAsStream(resourceName));

    configure(waConfig, opis, config, workareaXML);
  }

  public static void configure(final DataSourceFactory waConfig, final String opis, com.openitech.db.model.xml.DataSourceConfig config, com.openitech.db.model.xml.config.Workarea workareaXML) throws SQLException {
    waConfig.opis = opis;

    final DbDataSource dsWorkAreaEvents = new DbDataSource();

    dsWorkAreaEvents.lock();
    try {
      dsWorkAreaEvents.setQueuedDelay(0);

      Boolean canAddRows = workareaXML.getDataSource().isCanAddRows();
      Boolean canDeleteRows = workareaXML.getDataSource().isCanDeleteRows();      
      dsWorkAreaEvents.setCanAddRows(canAddRows == null ? false : canAddRows);
      dsWorkAreaEvents.setCanDeleteRows(canDeleteRows == null ? false : canDeleteRows);


      java.util.List parameters = new java.util.ArrayList();
      final DataSourceLimit dataSourceLimit = new DataSourceLimit("<%DATA_SOURCE_LIMIT%>");

      dataSourceLimit.setValue(" TOP 0 ");

      parameters.add(dataSourceLimit);

      dataSourceLimit.addDataSource(dsWorkAreaEvents);
//                  dsNaslovPendingSql.setImmediateSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Immediate.sql", "cp1250"));
//        dsNaslovPendingSql.setPendingSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Pending.sql", "cp1250"),
//                "Ulica", "HisnaStevilka", "PostnaStevilka", "Posta", "Naselje", "TipNaslova_Opis", "PPIzvori_Izvor");
//        dsNaslovPendingSql.setDeferredSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Deferred.sql", "cp1250"), "PPId");
//        dsPoslovniPartnerjiFilter.setOperator("AND");
      for (Parameters parameter : workareaXML.getDataSource().getParameters()) {
        if (parameter.getTemporaryTable() != null) {
          TemporaryTable tt = parameter.getTemporaryTable();
          TemporarySubselectSqlParameter ttParameter = new TemporarySubselectSqlParameter(tt.getReplace());

          ttParameter.setValue(tt.getTableName());
          ttParameter.setCheckTableSql(getReplacedSql(tt.getCheckTableSql()));
          ttParameter.setCreateTableSqls(tt.getCreateTableSqls().getQuery().toArray(new String[]{}));
          ttParameter.setEmptyTableSql(getReplacedSql(tt.getEmptyTableSql()));
          ttParameter.setFillTableSql(getReplacedSql(tt.getFillTableSql()));

          if (tt.isFillOnceOnly() != null) {
            ttParameter.setFillOnceOnly(tt.isFillOnceOnly());
          }

          parameters.add(ttParameter);
        } else if (parameter.getSubQuery() != null) {
          SubQuery subQuery = parameter.getSubQuery();
          PendingSqlParameter subQueryFilter = new PendingSqlParameter(subQuery.getReplace());
          subQueryFilter.setImmediateSQL(subQuery.getImmediateSQL());
          subQueryFilter.setPendingSQL(subQuery.getPendingSQL(), subQuery.getPendingColumns().getColumnNames().toArray(new String[subQuery.getPendingColumns().getColumnNames().size()]));
          subQueryFilter.setDeferredSQL(subQuery.getDeferredSQL(), subQuery.getParentKey().getColumnNames().toArray(new String[subQuery.getParentKey().getColumnNames().size()]));
          subQueryFilter.setSupportsMultipleKeys(subQuery.isSupportsMultipleKeys());
          if (subQuery.getMultipleKeysLimit() != null) {
            subQueryFilter.setMultipleKeysLimit(subQuery.getMultipleKeysLimit());
          }
//            subQueryFilter.setOperator(subQuery.getOperator());
          parameters.add(subQueryFilter);
        } else if (parameter.getDataSourceFilter() != null) {
          try {
            Object newInstance = null;
            DataSourceFilter dsf = parameter.getDataSourceFilter();
            if (dsf.getFactory().getGroovy() != null) {
              GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
              Class gcls = gcl.parseClass(dsf.getFactory().getGroovy(), "wa" + waConfig.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(String.class, com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsf.getReplace(), config);
            } else if (dsf.getFactory().getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(dsf.getFactory().getClassName());
              Constructor constructor = jcls.getConstructor(String.class, com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsf.getReplace(), config);
            }

            if (newInstance != null) {
              if (newInstance instanceof java.awt.Component) {
                waConfig.filterPanel = (java.awt.Component) newInstance;
              }
              if (newInstance instanceof DataSourceObserver) {
                ((DataSourceObserver) newInstance).setDataSource(dsWorkAreaEvents);
              }
              if (newInstance instanceof ActiveFiltersReader) {
                DataSourceFilters filter = ((ActiveFiltersReader) newInstance).getActiveFilter();
                if (dsf.getOperator() != null) {
                  filter.setOperator(dsf.getOperator());
                }
                parameters.add(filter);
              } else if (newInstance instanceof DataSourceFilters) {
                DataSourceFilters filter = (DataSourceFilters) newInstance;
                if (dsf.getOperator() != null) {
                  filter.setOperator(dsf.getOperator());
                }
                parameters.add(filter);
              }
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
          }
        } else if ((parameter.getDataSourceParametersFactory() != null) ||
                (parameter.getDataSourceFilterFactory() != null)) {
          try {
            Object newInstance = null;
            DataSourceParametersFactory dsf = (parameter.getDataSourceParametersFactory() != null) ? parameter.getDataSourceParametersFactory() : parameter.getDataSourceFilterFactory();
            if (dsf.getFactory().getGroovy() != null) {
              GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
              Class gcls = gcl.parseClass(dsf.getFactory().getGroovy(), "wa" + waConfig.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(DbDataSource.class, com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsWorkAreaEvents, config);
            } else if (dsf.getFactory().getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(dsf.getFactory().getClassName());
              Constructor constructor = jcls.getConstructor(DbDataSource.class, com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsWorkAreaEvents, config);
            }

            if (newInstance != null) {
              if (newInstance instanceof AbstractDataSourceParametersFactory) {
                AbstractDataSourceParametersFactory instance = (AbstractDataSourceParametersFactory) newInstance;
                waConfig.filterPanel = instance.getFilterPanel();
                waConfig.viewMenuItems.addAll(instance.getViewMenuItems());

                parameters.addAll(instance.getParameters());
              }
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }

      dsWorkAreaEvents.setParameters(parameters);

      if (workareaXML.getDataSource().getCOUNTSQL() != null) {
        dsWorkAreaEvents.setCountSql(workareaXML.getDataSource().getCOUNTSQL());
      }
      dsWorkAreaEvents.setSelectSql(workareaXML.getDataSource().getSQL());
      dsWorkAreaEvents.setName("DS:WORKAREA:" + waConfig.getOpis());

      List<String> eventColumns = workareaXML.getDataSource().getEventColumns();

      if (eventColumns.size() > 0) {
        dsWorkAreaEvents.setQueuedDelay(0);
        dataSourceLimit.reloadDataSources();

        for (String imePolja : eventColumns) {
          int tipPolja = dsWorkAreaEvents.getType(imePolja);

          DbFieldObserver fieldObserver = new DbFieldObserver();
          fieldObserver.setColumnName(imePolja);
          fieldObserver.setDataSource(dsWorkAreaEvents);
          waConfig.dataEntryValues.add(new FieldValueProxy(imePolja, tipPolja, fieldObserver));
        }



      }

      DataSourceEvent.suspend(dsWorkAreaEvents);
      DataSourceLimit.Limit.LALL.setSelected();
      dataSourceLimit.setValue(DataSourceLimit.Limit.LALL.getValue());

      Long delay = workareaXML.getDataSource().getQueuedDelay();
      dsWorkAreaEvents.setQueuedDelay(delay == null ? DbDataSource.DEFAULT_QUEUED_DELAY : delay.longValue());

      dataSourceLimit.reloadDataSources();

      waConfig.dataSource = dsWorkAreaEvents;
      com.openitech.db.model.DbTableModel tableModel = new com.openitech.db.model.DbTableModel();

      List<TableColumnDefinition> tableColumnDefinitions = workareaXML.getDataModel().getTableColumns().getTableColumnDefinition();
      List<String[]> tableColumns = new ArrayList<String[]>();
      for (TableColumnDefinition tableColumnDefinition : tableColumnDefinitions) {
        tableColumns.add(tableColumnDefinition.getTableColumnEntry().toArray(new String[tableColumnDefinition.getTableColumnEntry().size()]));
      }

      tableModel.setColumns(tableColumns.toArray(new String[tableColumns.size()][]));

      if (workareaXML.getDataModel().getSeparator() != null) {
        tableModel.setSeparator(workareaXML.getDataModel().getSeparator());
      }

      tableModel.setDataSource(dsWorkAreaEvents);
      waConfig.tableModel = tableModel;


      if (workareaXML.getInformation() != null) {
        for (com.openitech.db.model.xml.config.Workarea.Information.Panels panel : workareaXML.getInformation().getPanels()) {
          try {
            Object newInstance = null;
            if (panel.getGroovy() != null) {
              GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
              Class gcls = gcl.parseClass(panel.getGroovy(), "wa" + waConfig.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(config);
            } else if (panel.getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(panel.getClassName());
              Constructor constructor = jcls.getConstructor(com.openitech.db.model.xml.DataSourceConfig.class);
              newInstance = constructor.newInstance(config);
            }
            if (newInstance instanceof java.awt.Component) {
              waConfig.informationPanels.put(panel.getTitle(), (java.awt.Component) newInstance);
            } else {
              waConfig.linkedObjects.put(panel.getTitle(), newInstance);
            }
            if (newInstance instanceof DataSourceObserver) {
              ((DataSourceObserver) newInstance).setDataSource(dsWorkAreaEvents);
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    } finally {
      dsWorkAreaEvents.unlock();
    }
  }
  private DbDataSource dataSource;

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }
  private DbTableModel tableModel;

  /**
   * Get the value of tableModel
   *
   * @return the value of tableModel
   */
  public DbTableModel getTableModel() {
    return tableModel;
  }
  private List<FieldObserver> fieldObservers = new ArrayList<FieldObserver>();
  protected Set<Field> dataEntryValues = new java.util.HashSet<com.openitech.sql.Field>();
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
  private Component filterPanel;

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
  private Map<String, Component> informationPanels = new LinkedHashMap<String, Component>();

  /**
   * Get the value of informationPanels
   *
   * @return the value of informationPanels
   */
  public Map<String, Component> getInformationPanels() {
    return informationPanels;
  }
  private Map<String, Object> linkedObjects = new LinkedHashMap<String, Object>();

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

    return sql;
  }
}
