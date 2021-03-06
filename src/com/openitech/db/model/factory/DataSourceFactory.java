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

import com.openitech.db.components.dogodki.EventTask;
import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.DataSourceConfigObserver;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.xml.config.DataModel;
import com.openitech.db.model.xml.config.DataModel.TableColumns;
import com.openitech.db.model.xml.config.Factory;
import com.openitech.db.model.xml.config.Importer;
import com.openitech.db.model.xml.config.Importer.Destination;
import com.openitech.db.model.xml.config.Importer.Destination.Column;
import com.openitech.db.model.xml.config.Workarea.AssociatedTasks;
import com.openitech.db.model.xml.config.Workarea.DataSource;
import com.openitech.db.model.xml.config.Workarea.DataSource.CreationParameters;
import com.openitech.db.model.xml.config.Workarea.DataSource.CreationParameters.TopList;
import com.openitech.db.model.xml.config.Workarea.DataSource.Listeners;
import com.openitech.db.model.xml.config.Workarea.DataSource.Listeners.Listener;
import com.openitech.db.model.xml.config.Workarea.DataSource.ViewsParameters;

import com.openitech.db.model.xml.config.Workarea.EventImporters;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter.Options;
import com.openitech.db.model.xml.config.Workarea.EventImporters.EventImporter.Options.IdentityGroupBy;
import com.openitech.db.model.xml.config.Workarea.ExtendWorkarea;
import com.openitech.db.model.xml.config.Workarea.Importers;
import com.openitech.db.model.xml.config.Workarea.IncludeWorkarea;
import com.openitech.db.model.xml.config.Workarea.WorkSpaceInformation;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.db.model.sql.SQLMaterializedView;
import com.openitech.db.model.sql.SQLOrderByParameter;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.db.model.xml.config.DataModel.TableColumns.TableColumnDefinition;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.QueryParameter;
import com.openitech.db.model.xml.config.Sharing;
import com.openitech.db.model.xml.config.Workarea;
import com.openitech.db.model.xml.config.Workarea.AssociatedTasks.TaskPanes;
import com.openitech.db.model.xml.config.Workarea.AssociatedTasks.TaskPanes.DefaultTask;
import com.openitech.db.model.xml.config.Workarea.DataSource.ViewsParameters.Views;
import com.openitech.importer.JEventsImporter;
import com.openitech.importer.JImportEventsModel;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.Activity;
import com.openitech.value.events.ActivityEvent;
import com.openitech.value.fields.Field;
import com.openitech.value.fields.Field.FieldModel;
import com.openitech.value.fields.FieldValueProxy;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;
import javax.xml.bind.JAXBException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.jdesktop.swingx.JXTaskPane;

/**
 *
 * @author uros
 */
public class DataSourceFactory extends AbstractDataSourceFactory {

  public static final String DATA_SOURCE_LIMIT = "<%DATA_SOURCE_LIMIT%>";
  public static final String DB_ROW_SORTER = "<%DB_ROW_SORTER%>";
  private DataSourceLimit dataSourceLimit;
  private List<String> namedParameters = new ArrayList<String>();
  private Workarea root;
  private CreationParameters creationParameters;
  private Integer workSpaceId;
  private Integer workAreaId;
  private boolean editable = true;

  public DataSourceFactory(DbDataModel dbDataModel) {
    super(dbDataModel);
  }

  public void setWorkAreaId(Integer workAreaId) {
    this.workAreaId = workAreaId;
  }

  public void setWorkSpaceId(Integer workSpaceId) {
    this.workSpaceId = workSpaceId;
  }

  /**
   * Get the value of editable
   *
   * @return the value of editable
   */
  public boolean isEditable() {
    return editable;
  }

  @Override
  public void configure() throws SQLException, ClassNotFoundException {
    if (root == null) {
      root = dataSourceXML;
    }
    Workarea original = dataSourceXML;
    try {
      final ExtendWorkarea extendWorkarea = dataSourceXML.getExtendWorkarea();
      if (extendWorkarea != null) {
        try {
          List<Integer> workSpaceIDs = extendWorkarea.getWorkSpaceID();
          List<Integer> workareaIDs = extendWorkarea.getWorkareaID();
          for (Integer workAreaId : workareaIDs) {
            configure(opis, SqlUtilities.getInstance().getWorkArea(null, workAreaId), config);
          }
          for (Integer workSpaceID : workSpaceIDs) {
            configure(opis, SqlUtilities.getInstance().getWorkArea(workSpaceID, null), config);
          }
        } catch (JAXBException ex) {
          Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataSourceXML = original;
      }

      this.dataSource = createDataSource();

      if (dataSourceXML.isDisableDataEntry() != null) {
        this.editable = !dataSourceXML.isDisableDataEntry().booleanValue();
      }

      final DataSource dataSourceElement = dataSourceXML.getDataSource();

      if (dataSourceElement != null) {

        suspendDataSource();

        dataSource.lock();
        try {
          dataSource.setQueuedDelay(Integer.MAX_VALUE);
          this.dataSourceLimit = createDataSourceLimit();

          createViews();

          List<Object> parameters = new ArrayList<Object>();
          parameters.addAll(dataSource.getParameters());
          parameters.addAll(createDataSourceParameters());

          dataSource.setParameters(parameters, false);

          if (dataSourceElement.getCOUNTSQL() != null) {
            dataSource.setCountSql(getReplacedSql(dataSourceElement.getCOUNTSQL()));
          }

          if (dataSourceElement.getIdSifranta() != null) {
            String dataSourceSQL = SqlUtilities.getInstance().getDataSourceSQL(dataSourceElement.getIdSifranta(), dataSourceElement.getIdSifre());
            dataSource.setSelectSql(getReplacedSql(dataSourceSQL));
          } else if (dataSourceElement.getSQL() == null) {
            if (workAreaId != null) {
              String dataSourceSQL = SqlUtilities.getInstance().getDataSourceSQL(workAreaId);
              dataSource.setSelectSql(getReplacedSql(dataSourceSQL));
            }
          } else {
            dataSource.setSelectSql(getReplacedSql(dataSourceElement.getSQL()));
          }
          if (dataSourceElement.getQueryHints() != null) {
            dataSource.setCountSql(dataSource.getCountSql() + '\n' + dataSourceElement.getQueryHints());
            dataSource.setSelectSql(dataSource.getSelectSql() + '\n' + dataSourceElement.getQueryHints());
          }
          addListeners();

          createEventColumns();
          limitDataSource();
          setDataSourceQueuedDelay();

          storeCachedTemporaryTables();

          creationParameters = dataSourceElement.getCreationParameters();
        } catch (Throwable ex) {
          Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
          dataSource.unlock();
        }
      }

      dataSource.lock();
      try {
        this.tableModel = createTableModel();

        if (dataSourceElement == null && dataSourceXML.getIncludeWorkarea() == null) {
          StringBuilder sbSelect = new StringBuilder(100);
          int columnCount = tableModel.getColumnCount();
          for (int i = 0; i < columnCount; i++) {
            sbSelect.append(sbSelect.length() > 0 ? "," : "SELECT TOP 0 \n").append("(NULL) AS [").append(tableModel.getColumnName(i)).append("]");
          }
        }
        createInformationPanels();

        getTaskPanes().addAll(createTaskPanes(dataSourceXML));

        createImporters();

        createDataEntryPanel();

        configureOptions();

      } catch (Throwable ex) {
        Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        dataSource.unlock();
      }
      final IncludeWorkarea includeWorkarea = dataSourceXML.getIncludeWorkarea();
      if (includeWorkarea != null) {
        try {
          List<Integer> workSpaceIDs = includeWorkarea.getWorkSpaceID();
          List<Integer> workareaIDs = includeWorkarea.getWorkareaID();
          for (Integer workAreaId : workareaIDs) {
            configure(opis, SqlUtilities.getInstance().getWorkArea(null, workAreaId), config);
          }
          for (Integer workSpaceID : workSpaceIDs) {
            configure(opis, SqlUtilities.getInstance().getWorkArea(workSpaceID, null), config);
          }

        } catch (JAXBException ex) {
          Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } catch (SQLException ex) {
      throw ex;
    } finally {
      dataSourceXML = original;
      if (root.equals(dataSourceXML)) {
        resumeDataSource();
      }
    }
  }

  protected void storeCachedTemporaryTables() {
//    if (cachedTemporaryTables == null) {
//      cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
//    }
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      for (QueryParameter parameter : dataSourceElement.getParameters()) {
        if (parameter.getTemporaryTable() != null) {
          TemporaryTable tt = parameter.getTemporaryTable();
          if (tt.getMaterializedView() != null) {
            if (SqlUtilities.getInstance().getCachedTemporaryTable(tt.getMaterializedView().getValue()) == null) {
              SqlUtilities.getInstance().storeCachedTemporaryTable(tt);
            }
          }
        }
      }
    }
  }

  protected void createViews() {
    SqlUtilities sqlUtilities = SqlUtilities.getInstance();
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      ViewsParameters viewsParameters = dataSourceElement.getViewsParameters();
      if (viewsParameters != null) {
        List<Views> views = viewsParameters.getViews();
        if (views != null) {
          for (Views view : views) {
            sqlUtilities.createEventViews(view);
          }
        }
      }
    }
  }
  private AbstractDataSourceParametersFactory dataSourceParametersFactory;

  public AbstractDataSourceParametersFactory getDataSourceParametersFactory() {
    return dataSourceParametersFactory;
  }

  protected List createDataSourceParameters() {
    java.util.List parameters = new java.util.ArrayList();
    java.util.List<TemporarySubselectSqlParameter> temporaryTables = new ArrayList<TemporarySubselectSqlParameter>();
    if (dataSourceLimit != null) {
      parameters.add(dataSourceLimit);
    }
    if (additionalParameters != null) {
      parameters.addAll(additionalParameters);
    }
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      for (QueryParameter parameter : dataSourceElement.getParameters()) {
        Object queryParameter = createQueryParameter(parameter);
        if (parameter.getNamedParameter() != null) {
          namedParameters.add((String) queryParameter);
        } else if (parameter.getTemporaryTable() != null) {
          temporaryTables.add((TemporarySubselectSqlParameter) queryParameter);
          parameters.add(queryParameter);
        } else if (parameter.getTemporaryTableGroup() != null) {
          temporaryTables.addAll((java.util.List<TemporarySubselectSqlParameter>) queryParameter);
          parameters.addAll((java.util.List<TemporarySubselectSqlParameter>) queryParameter);
        } else if ((parameter.getDataSourceParametersFactory() != null) || (parameter.getDataSourceFilterFactory() != null)) {
          try {
            DataSourceParametersFactory dsf = (parameter.getDataSourceParametersFactory() != null) ? parameter.getDataSourceParametersFactory() : parameter.getDataSourceFilterFactory();
            if (dsf.getFactory() == null) {
              Factory defaultFactory = new Factory();
              defaultFactory.setClassName(DefaultFilterFactory.class.getName());
              dsf.setFactory(defaultFactory);
            }
            Object newInstance = ClassInstanceFactory.getInstance("wa" + this.getOpis() + "_" + System.currentTimeMillis(), dsf.getFactory(), DbDataSource.class, config.getClass()).newInstance(dataSource, config);
            if (newInstance != null) {
              if (newInstance instanceof AbstractDataSourceParametersFactory) {
                AbstractDataSourceParametersFactory instance = (AbstractDataSourceParametersFactory) newInstance;
                dataSourceParametersFactory = instance;
                instance.setDataSourceParametersFactory(dsf);
                instance.configure();
                this.filtersMap = instance.getFiltersMap();
                this.filterPanel = instance.getFilterPanel();
                this.viewMenuItems.addAll(instance.getViewMenuItems());
                this.workAreaFilters = instance.getWorkAreaFilters();
                this.autoInsertValues = instance.getAutoInsertValue();
                this.dataEntryValues = instance.getDataEntryValues();
                parameters.addAll(instance.getParameters());
              }
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, "Can't create " + config.getClass(), ex);
          }
        } else if (queryParameter != null) {
          parameters.add(queryParameter);
        }
      }
      SQLOrderByParameter orderByParameter = new SQLOrderByParameter(DB_ROW_SORTER, dataSource);

      if (dataSourceElement.getOrderBy() != null) {
        orderByParameter.setOrderBy(dataSourceElement.getOrderBy());
      }

      parameters.add(orderByParameter);

      List<SQLMaterializedView> mvParameters = new ArrayList<SQLMaterializedView>(temporaryTables.size());
      for (TemporarySubselectSqlParameter temporarySubselectSqlParameter : temporaryTables) {
        if (temporarySubselectSqlParameter.getSqlMaterializedView() != null) {
          mvParameters.add(temporarySubselectSqlParameter.getSqlMaterializedView());
        }
      }

      for (SQLMaterializedView sqlMaterializedView : mvParameters) {
        for (TemporarySubselectSqlParameter temporarySubselectSqlParameter : temporaryTables) {
          if (!sqlMaterializedView.equals(temporarySubselectSqlParameter.getSqlMaterializedView())) {
            temporarySubselectSqlParameter.addParameter(sqlMaterializedView);
          }
        }
      }

      return parameters;
    } else {
      return this.dataSource.getParameters();
    }
  }

  public List<String> getNamedParameters() {
    return namedParameters;
  }

  protected void limitDataSource() {
    if (dataSourceLimit != null) {
      dataSourceLimit.setValue(DataSourceLimit.Limit.DEFAULT_LIMIT);
    }
  }

  protected void resumeDataSource() {
    if (dataSource.getQueuedDelay() == Integer.MAX_VALUE) {
      dataSource.setQueuedDelay(DbDataSource.DEFAULT_QUEUED_DELAY);
    }
    if (dataSource != null) {
      DataSourceEvent.cancel(dataSource);
      DataSourceEvent.resume(dataSource);
      try {
        dataSource.filterChanged();
      } catch (SQLException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      }
      dataSource.reload();
    }
  }

  protected DbDataSource createDataSource() throws ClassNotFoundException {
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      CreationParameters creationParameters = dataSourceElement.getCreationParameters();

      String className = null;
      String provider = null;
      if (creationParameters != null) {
        className = creationParameters.getClassName();
        provider = creationParameters.getProviderClassName();
      } else {
        className = dataSourceElement.getClassName();
        provider = dataSourceElement.getProviderClassName();
      }
      final DbDataSource dataSource = this.dataSource != null ? this.dataSource : (className == null ? new DbDataSource() : new DbDataSource("", "", (Class<? extends DbDataSourceImpl>) Class.forName(className)));
      if (provider != null) {
        dataSource.setProvider(provider);
      }

      Boolean canAddRows = null;
      Boolean canDeleteRows = null;
      if (creationParameters != null) {
        canAddRows = creationParameters.isCanAddRows();
        canDeleteRows = creationParameters.isCanDeleteRows();
      } else {
        canAddRows = dataSourceElement.isCanAddRows();
        canDeleteRows = dataSourceElement.isCanDeleteRows();
      }
      if (creationParameters != null) {
        if (creationParameters.isDisableAutoSeek() != null) {
          dataSource.setAutoReload(!creationParameters.isDisableAutoSeek());
        }
      }

      setCanExportData(dataSourceXML.isCanExportData());

      dataSource.setCanAddRows(canAddRows == null ? (this.dataSource != null ? this.dataSource.isCanAddRows() : false) : canAddRows);
      dataSource.setCanDeleteRows(canDeleteRows == null ? (this.dataSource != null ? this.dataSource.isCanDeleteRows() : false) : canDeleteRows);

      dataSource.setCanExportData(isCanExportData());

      if (creationParameters != null) {
        Sharing sharing = creationParameters.getSharing();
        if (sharing != null) {
          switch (sharing) {
            case SHARING_GLOBAL:
              dataSource.setSharing(DbDataSource.SHARING_GLOBAL);
              break;
            case SHARING_LOCAL:
              dataSource.setSharing(DbDataSource.SHARING_LOCAL);
              break;
            case SHARING_OFF:
              dataSource.setSharing(DbDataSource.SHARING_OFF);
              break;
          }
        }
      }
      dataSource.setName("DS:FACTORY:" + this.getOpis());

      return dataSource;
    } else {
      return this.dataSource != null ? this.dataSource : new DbDataSource();
    }
  }

  protected void setDataSourceQueuedDelay() {
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      CreationParameters creationParameters = dataSourceElement.getCreationParameters();

      Long delay = null;
      if (creationParameters != null) {
        delay = creationParameters.getQueuedDelay();
      } else {
        delay = dataSourceElement.getQueuedDelay();
      }
      if (delay != null) {
        this.dataSource.setQueuedDelay(delay.longValue());
      } else {
        if (this.dataSource.getQueuedDelay() <= 0) {
          this.dataSource.setQueuedDelay(DbDataSource.DEFAULT_QUEUED_DELAY);
        }
      }
    }
  }

  protected DataSourceLimit createDataSourceLimit() {
    final DataSource dataSourceElement = dataSourceXML.getDataSource();
    if (dataSourceElement != null) {
      CreationParameters creationParameters = dataSourceElement.getCreationParameters();

      Boolean useLimitParameter = null;
      if (creationParameters != null) {
        useLimitParameter = creationParameters.isUseLimitParameter();
      }

      if (dataSourceElement.getSQL() != null && dataSourceElement.getSQL().indexOf(DATA_SOURCE_LIMIT) == -1) {
        useLimitParameter = Boolean.FALSE;
      }

      DataSourceLimit dataSourceLimit = null;
      if (useLimitParameter == null || useLimitParameter.booleanValue()) {
        dataSourceLimit = new DataSourceLimit(DATA_SOURCE_LIMIT);
        dataSourceLimit.setValue(" TOP 0 ");
        dataSourceLimit.addDataSource(dataSource);

        if (creationParameters != null) {
          if (dataSourceLimit != null) {
            TopList topList = creationParameters.getTopList();
            if (topList != null) {
              dataSourceLimit.setHideTop10(topList.isHideTop10());
              dataSourceLimit.setHideTop50(topList.isHideTop50());
              dataSourceLimit.setHideTop100(topList.isHideTop100());
              dataSourceLimit.setHideTop1000(topList.isHideTop1000());
              dataSourceLimit.setHideTopAll(topList.isHideTopAll());
            }
          }
        }

      }

      return dataSourceLimit;
    } else {
      return this.dataSourceLimit;
    }
  }

  protected void suspendDataSource() {
//    final DataSource dataSourceElement = dataSourceXML.getDataSource();
//    if (dataSourceElement != null) {
//      CreationParameters creationParameters = dataSourceElement.getCreationParameters();
//
//      Boolean suspend = null;
//      if (creationParameters != null) {
//        suspend = creationParameters.isSuspend();
//      }
//      if (suspend == null || suspend.booleanValue()) {
    if (dataSource != null) {
      DataSourceEvent.suspend(dataSource);
    }
//    }
  }

  protected void createInformationPanels() {
    if (dataSourceXML.getInformation() != null) {
      for (com.openitech.db.model.xml.config.Workarea.Information.Panels panel : dataSourceXML.getInformation().getPanels()) {
        try {
          Object newInstance = ClassInstanceFactory.getInstance("wa" + this.getOpis() + "_" + System.currentTimeMillis(), panel.getGroovy(), panel.getClassName(), config.getClass()).newInstance(config);

          if (newInstance instanceof java.awt.Component) {
            this.informationPanels.put(panel.getTitle(), (java.awt.Component) newInstance);
          } else {
            this.linkedObjects.put(panel.getTitle(), newInstance);
          }
          if (newInstance instanceof DataSourceObserver) {
            ((DataSourceObserver) newInstance).setDataSource(dataSource);
          }
        } catch (Exception ex) {
          Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    if (dataSourceXML.getWorkSpaceInformation() != null) {
      WorkSpaceInformation workSpaceInformation = dataSourceXML.getWorkSpaceInformation();
      for (WorkSpaceInformation.Panels panels : workSpaceInformation.getPanels()) {

        Integer panelWorkSpaceId = panels.getWorkSpaceId();
        if (panelWorkSpaceId == null) {
          panelWorkSpaceId = this.workSpaceId;
        }
        if (panelWorkSpaceId != null) {
          List<JPanel> components;
          if (this.workSpaceInformationPanels.containsKey(panelWorkSpaceId)) {
            components = workSpaceInformationPanels.get(panelWorkSpaceId);
          } else {
            components = new LinkedList<JPanel>();
            this.workSpaceInformationPanels.put(panelWorkSpaceId, components);
          }
          if (panels.getClassName() != null) {
            try {
              Object newInstance = ClassInstanceFactory.getInstance("wa" + this.getOpis() + "_" + System.currentTimeMillis(), null, panels.getClassName(), config.getClass()).newInstance(config);


              components.add(new com.openitech.db.components.dogodki.WorkSpaceInformation(panels, (java.awt.Component) newInstance));
              if (newInstance instanceof DataSourceObserver) {
                ((DataSourceObserver) newInstance).setDataSource(dataSource);
              }
            } catch (Exception ex) {
              Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
            }
          } else if (panels.getModel() != null) {
            components.add(new com.openitech.db.components.dogodki.WorkSpaceInformation(panels));

          }
        }
      }

    }
  }

  public void configureOptions() {
    Boolean disableTab = dataSourceXML.isDisableTab();
    this.disabledTab = disableTab != null && disableTab;
  }

  protected DbTableModel createTableModel() {
    com.openitech.db.model.DbTableModel tableModel = this.tableModel != null ? this.tableModel : new com.openitech.db.model.DbTableModel();
    final DataModel dataModel = dataSourceXML.getDataModel();
    List<String[]> tableColumns = new ArrayList<String[]>();
    for (String[] entries : tableModel.getColumns()) {
      tableColumns.add(entries);
    }
    boolean addDefaultColumns = false;
    if (dataModel != null) {
      final TableColumns tableColumnsElement = dataModel.getTableColumns();
      if (tableColumnsElement != null) {
        List<TableColumnDefinition> tableColumnDefinitions = tableColumnsElement.getTableColumnDefinition();
        if (tableColumnDefinitions.size() > 0) {
          for (TableColumnDefinition tableColumnDefinition : tableColumnDefinitions) {
            tableColumns.add(tableColumnDefinition.getTableColumnEntry().toArray(new String[tableColumnDefinition.getTableColumnEntry().size()]));
          }
          if (dataModel.getSeparator() != null) {
            tableModel.setSeparator(dataModel.getSeparator());
          }
        } else {
          addDefaultColumns = true;
        }
      } else {
        addDefaultColumns = true;
      }
    } else {
      addDefaultColumns = true;
    }
    if (addDefaultColumns && tableModel.getColumnCount() == 0 && dataSourceXML.getIncludeWorkarea() == null) {
      List<String> ignoredColumns = new ArrayList<String>();
      ignoredColumns.add("Id");
      ignoredColumns.add("EventId");
      ignoredColumns.add("IdSifranta");
      ignoredColumns.add("IdSifre");
      ignoredColumns.add("IdEventSource");
      ignoredColumns.add("VersionId");
      ignoredColumns.add("Version");
      ignoredColumns.add("Datum");
      ignoredColumns.add("DatumSpremembe");
      try {
        dataSource.setSafeMode(false);
        dataSource.setQueuedDelay(0);
        dataSource.filterChanged();
        dataSource.loadData();
        dataSource.setSafeMode(true);
        ResultSetMetaData metaData = dataSource.getMetaData();
        CachedRowSet generatedFields = null;
        if (workAreaId != null) {
          Activity activity = SqlUtilities.getInstance().getActivity(workAreaId);
          if (activity != null) {
            ActivityEvent activityEvent = SqlUtilities.getInstance().getActivityEvent((int) activity.getActivityId());
            if (activityEvent != null) {
              generatedFields = SqlUtilities.getInstance().getGeneratedFields(activityEvent.getIdSifranta(), activityEvent.getIdSifre());
            }
          }
        }

        if (generatedFields != null) {
          int columnCount = metaData.getColumnCount();

          for (int i = 1; i <= columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i);
            if (columnLabel != null && !ignoredColumns.contains(columnLabel)) {
              String header = columnLabel;
              boolean showInTable = true;
              generatedFields.beforeFirst();
              while (generatedFields.next()) {
                String fieldName = generatedFields.getString("ImePolja");
                if (fieldName != null && fieldName.equals(header)) {
                  header = generatedFields.getString("Opis");
                  showInTable = generatedFields.getBoolean("ShowInTable");
                }
              }
              if (showInTable) {
                tableColumns.add(new String[]{header, columnLabel});
              }

            }
          }
        }
      } catch (SQLException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
    tableModel.setColumns(tableColumns.toArray(new String[tableColumns.size()][]));

    tableModel.setDataSource(dataSource);
    return tableModel;

  }

  protected void createEventColumns() throws SQLException {
    List<String> eventColumns = dataSourceXML.getDataSource().getEventColumns();
    if (eventColumns.size() > 0) {
      for (String imePolja : eventColumns) {
        Field field = Field.getField(imePolja, 1, SqlUtilities.getInstance().getPreparedFields());
        if (field == null) {
          dataSource.setSafeMode(false);
          dataSource.setQueuedDelay(0);
          dataSource.filterChanged();
          dataSource.loadData();
          dataSource.setSafeMode(true);


          int tipPolja = dataSource.getType(imePolja);
          field = new Field(imePolja, tipPolja);
        }
        DbFieldObserver fieldObserver = new DbFieldObserver();
        fieldObserver.setColumnName(imePolja);
        fieldObserver.setDataSource(dataSource);
        final FieldValueProxy fieldValueProxy = new FieldValueProxy(field, fieldObserver);
        if (!this.dataEntryValues.contains(fieldValueProxy)) {
          this.dataEntryValues.add(fieldValueProxy);
        }
      }
    }
  }

  @Override
  protected Object createDataSourceFilter(DataSourceFilter dsf) throws NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, CompilationFailedException, IllegalArgumentException, SecurityException {
    Object newInstance = super.createDataSourceFilter(dsf);

    if (newInstance != null) {
      if (newInstance instanceof java.awt.Component) {
        this.filterPanel = (java.awt.Component) newInstance;
      }
    }
    return newInstance;
  }

  protected List<JXTaskPane> createTaskPanes(com.openitech.db.model.xml.config.Workarea dataSourceXML) throws ClassNotFoundException {
    List<JXTaskPane> result = new ArrayList<JXTaskPane>();
    try {

      final AssociatedTasks associatedTasks = dataSourceXML.getAssociatedTasks();
      if ((associatedTasks != null) && !associatedTasks.getTaskPanes().isEmpty()) {
        for (TaskPanes taskPane : associatedTasks.getTaskPanes()) {
          Object newInstance = null;
          if (taskPane.getFactory() != null) {
            newInstance = ClassInstanceFactory.getInstance("wa" + (taskPane.getTitle() == null ? "" : taskPane.getTitle()) + "_" + System.currentTimeMillis(), taskPane.getFactory().getGroovy(), taskPane.getFactory().getClassName(), config.getClass()).newInstance(config);
          } else if (taskPane.getTaskList() != null && !taskPane.getTaskList().getTasks().isEmpty()) {
            newInstance = new JXDataSourceTaskPane(config, dataSource);
            ((JXDataSourceTaskPane) newInstance).setTitle(taskPane.getTitle());
            ((JXDataSourceTaskPane) newInstance).addTasks(taskPane.getTaskList().getTasks());
          } else if (taskPane.getAutoInsert() != null && taskPane.getAutoInsert().isAutoInsert()) {
            newInstance = new EventTask(taskPane.getTitle(), taskPane.getAutoInsert().isDontMerge());
            ((EventTask) newInstance).setType(EventTask.TaskType.INSERT);
          } else if (taskPane.getBack() != null && taskPane.getBack().isBack()) {
            newInstance = new EventTask(taskPane.getTitle(), taskPane.getBack().isDontMerge());
            ((EventTask) newInstance).setType(EventTask.TaskType.BACK);
          } else if (taskPane.getOpenWorkArea() != null) {
            TaskPanes.OpenWorkArea openWorkArea = taskPane.getOpenWorkArea();
            newInstance = new EventTask(taskPane.getTitle(), openWorkArea.getWorkSpaceId(), openWorkArea.getWorkAreaId(), openWorkArea.isOpenDataEntry(), openWorkArea.isDontMerge());
            ((EventTask) newInstance).setType(EventTask.TaskType.OPEN_WORK_AREA);
          } else if (taskPane.getReportPrint() != null) {
            newInstance = new EventTask(taskPane.getTitle(), taskPane.getReportPrint().isDontMerge());
            ((EventTask) newInstance).setReportName(taskPane.getReportPrint().getName());
            ((EventTask) newInstance).setType(EventTask.TaskType.REPORT);
          } else if (taskPane.getDefaultTask() != null) {
            DefaultTask defaultTask = taskPane.getDefaultTask();
            //this.editable = !defaultTask.isHide();
            newInstance = (new EventTask(defaultTask.getTitle(), defaultTask.getTaskTitle(), defaultTask.isHide() == null ? false : defaultTask.isHide()));
            ((EventTask) newInstance).setType(EventTask.TaskType.DEFAULT);
          }
          if (newInstance != null) {
            if (newInstance instanceof DataSourceObserver) {
              ((DataSourceObserver) newInstance).setDataSource(dataSource);
            }

            if (newInstance instanceof JXTaskPane) {
              result.add((JXTaskPane) newInstance);
            }
          }
        }
      }
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
  }

  private void createImporters() {
    EventImporters eventImporters = dataSourceXML.getEventImporters();
    if (eventImporters != null) {
      List<EventImporter> eventImporterList = eventImporters.getEventImporter();
      if (eventImporterList != null) {
        for (EventImporter eventImporter : eventImporterList) {
          List<String> eventColumns = eventImporter.getEventColumns();
          Set<Field> eventColumnsList = new HashSet<Field>();
          Options options = eventImporter.getOptions();
          if (options != null) {
            for (IdentityGroupBy identityGroupBy : options.getIdentityGroupBy()) {
              eventColumns.add(identityGroupBy.getColumnName());
              eventColumns.add(identityGroupBy.getIdentityColumnName());
            }
          }
          for (String imePolja : eventColumns) {
            Field field = Field.getField(imePolja);
            if (field == null) {
              throw new IllegalArgumentException("Napa�no ime polja [" + imePolja + "]!");
            }
            DbFieldObserver fieldObserver = new DbFieldObserver();
            fieldObserver.setColumnName(imePolja);
            fieldObserver.setDataSource(dataSource);
            final FieldValueProxy fieldValueProxy = new FieldValueProxy(field, fieldObserver);
            if (!eventColumnsList.contains(fieldValueProxy)) {
              eventColumnsList.add(fieldValueProxy);
            }
          }
          imporEventsModels.add(new JImportEventsModel(eventImporter, dataSource, eventColumnsList));
        }
      }
    }
    ///////////
    Importers importers = dataSourceXML.getImporters();
    if (importers != null) {
      List<Importer> eventImporterList = importers.getImporter();
      if (eventImporterList != null) {
        for (Importer eventImporter : eventImporterList) {
          Destination destination = eventImporter.getDestination();
          if (destination != null) {
            List<Column> eventColumns = destination.getColumn();
            Set<Field> eventColumnsList = new HashSet<Field>();

            for (Column column : eventColumns) {
              String imePolja = column.getColumnName();
              boolean lookup = column.isLookup();
              String identityGroupBy = column.getIdentityGroupBy();

              Field field = Field.getField(imePolja);
              if (field == null) {
                throw new IllegalArgumentException("Napa�no ime polja [" + imePolja + "]!");
              }

              if (column.isEventColumn()) {
                DbFieldObserver fieldObserver = new DbFieldObserver();
                fieldObserver.setColumnName(imePolja);
                fieldObserver.setDataSource(dataSource);
                final FieldValueProxy fieldValueProxy = new FieldValueProxy(field, fieldObserver);
                field = fieldValueProxy;
                if (!eventColumnsList.contains(fieldValueProxy)) {
                  eventColumnsList.add(fieldValueProxy);
                }
              } else {
                if (!eventColumnsList.contains(field)) {
                  eventColumnsList.add(field);
                }
              }

              if (identityGroupBy != null) {
                eventColumnsList.add(new FieldValueProxy(identityGroupBy, java.sql.Types.INTEGER));
              }

              if (field != null) {
                Boolean showInTable = column.isShowInTable();
                if (showInTable != null) {
                  field.setShowInTable(showInTable);
                }
                FieldModel model = field.getModel();

                DataModel tableModel1 = column.getTableModel();
                if (tableModel1 != null) {
                  TableColumns tableColumns = tableModel1.getTableColumns();
                  if (tableColumns != null) {
                    FieldModel.TableColumns tcCopy = new FieldModel.TableColumns();
                    model.setTableColumns(tcCopy);
                    for (TableColumnDefinition tableColumnDefinition : tableColumns.getTableColumnDefinition()) {
                      FieldModel.TableColumns.TableColumnDefinition tcd = new FieldModel.TableColumns.TableColumnDefinition();
                      tcCopy.getTableColumnDefinition().add(tcd);
                      for (String string : tableColumnDefinition.getTableColumnEntry()) {
                        tcd.getTableColumnEntry().add(string);
                      }
                    }
                  }

                }
              }

              if (lookup && field != null) {
                for (Field.LookupType lookupType : Field.LookupType.values()) {
                  DbFieldObserver fo = new DbFieldObserver();
                  fo.setColumnName(lookupType.getColumnPrefix() + field.getName());
                  fo.setDataSource(dataSource);
                  final FieldValueProxy fvLookupProxy = new FieldValueProxy(lookupType.getField(field.getName()), fo);
                  fvLookupProxy.setLookup(true);
                  fvLookupProxy.setLookupType(lookupType);
                  eventColumnsList.add(fvLookupProxy);
                }
              }

            }

            eventImportersModels.add(new JEventsImporter(eventImporter, dataSource, eventColumnsList));
          }
        }
      }
    }
  }

  private void createDataEntryPanel() throws ClassNotFoundException {
    if (dataSourceXML.getDataEntryPanel() != null) {

      com.openitech.db.model.xml.config.Workarea.DataEntryPanel panel = dataSourceXML.getDataEntryPanel();

      try {
        Object newInstance = ClassInstanceFactory.getInstance("wa_dep_" + this.getOpis() + "_" + System.currentTimeMillis(), panel.getGroovy(), panel.getClassName(), config.getClass()).newInstance(config);
        if (newInstance instanceof javax.swing.JComponent) {
          this.dataEntryPanel = (javax.swing.JComponent) newInstance;
        }
        if (newInstance instanceof DataSourceObserver) {
          ((DataSourceObserver) newInstance).setDataSource(dataSource);
        }
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalArgumentException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
        Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void addListeners() {
    try {
      tableDoubleClick = dataSourceXML.getTableDoubleClick();
      final DataSource dataSourceElement = dataSourceXML.getDataSource();
      if (dataSourceElement != null) {
        Listeners listeners = dataSourceElement.getListeners();
        if (listeners != null) {
          for (Listener listener : listeners.getListener()) {
            Object value = null;
            if (listener.getActionListener() != null) {
              dataSource.addActionListener((ActionListener) (value = ClassInstanceFactory.getInstance(listener.getActionListener(), new Class[]{}).newInstance(new Object[]{})));
            } else if (listener.getActiveRowChangeListener() != null) {
              dataSource.addActiveRowChangeListener((ActiveRowChangeListener) (value = ClassInstanceFactory.getInstance(listener.getActiveRowChangeListener(), new Class[]{}).newInstance(new Object[]{})));
            } else if (listener.getListDataListener() != null) {
              dataSource.addListDataListener((ListDataListener) (value = ClassInstanceFactory.getInstance(listener.getListDataListener(), new Class[]{}).newInstance(new Object[]{})));
            }

            if (value instanceof DataSourceObserver) {
              ((DataSourceObserver) value).setDataSource(dataSource);
            }

            if (value instanceof DataSourceConfigObserver) {
              ((DataSourceConfigObserver) value).setConfig(config);
            }
          }
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  protected static class JXDataSourceTaskPane extends JXTaskPane implements DataSourceObserver {

    protected final DataSourceConfig config;
    protected final DbDataSource dataSource;

    public JXDataSourceTaskPane(DataSourceConfig config, DbDataSource dataSource) {
      this.config = config;
      this.dataSource = dataSource;
    }

    /**
     * Get the value of config
     *
     * @return the value of config
     */
    public DataSourceConfig getConfig() {
      return config;
    }

    /**
     * Get the value of dataSource
     *
     * @return the value of dataSource
     */
    @Override
    public DbDataSource getDataSource() {
      return dataSource;
    }

    @Override
    public void setDataSource(DbDataSource dataSource) {
      throw new UnsupportedOperationException("Not supported.");
    }

    private void addTasks(List<Factory> tasks) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
      for (Factory task : tasks) {
        Object newInstance = ClassInstanceFactory.getInstance("wa_task" + (this.getTitle() == null ? "" : this.getTitle()) + "_" + System.currentTimeMillis(), task.getGroovy(), task.getClassName(), config.getClass()).newInstance(config);
        if (newInstance != null) {
          if (newInstance instanceof DataSourceObserver) {
            ((DataSourceObserver) newInstance).setDataSource(dataSource);
          }

          if (newInstance instanceof javax.swing.Action) {
            this.add((javax.swing.Action) newInstance);
          } else if (newInstance instanceof java.awt.PopupMenu) {
            this.add((java.awt.PopupMenu) newInstance);
          } else if (newInstance instanceof java.awt.Component) {
            this.add((java.awt.Component) newInstance);
          }
        }
      }
    }
  }
}
