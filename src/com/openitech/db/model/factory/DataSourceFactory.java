/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.events.ActiveRowChangeListener;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.db.model.DbTableModel;
import com.openitech.db.model.xml.config.DataModel;
import com.openitech.db.model.xml.config.Factory;
import com.openitech.db.model.xml.config.Workarea.DataSource.CreationParameters;
import com.openitech.db.model.xml.config.Workarea.DataSource.Listeners;
import com.openitech.db.model.xml.config.Workarea.DataSource.Listeners.Listener;
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
import com.openitech.db.model.xml.config.Workarea.AssociatedTasks.TaskPanes;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.fields.FieldValueProxy;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListDataListener;
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

  public DataSourceFactory(DbDataModel dbDataModel) {
    super(dbDataModel);
  }

  @Override
  public void configure() throws SQLException, ClassNotFoundException {
    this.dataSource = createDataSource();

    dataSource.lock();
    try {
      dataSource.setQueuedDelay(0);
      this.dataSourceLimit = createDataSourceLimit();
      dataSource.setParameters(createDataSourceParameters());

      if (dataSourceXML.getDataSource().getCOUNTSQL() != null) {
        dataSource.setCountSql(getReplacedSql(dataSourceXML.getDataSource().getCOUNTSQL()));
      }
      dataSource.setSelectSql(getReplacedSql(dataSourceXML.getDataSource().getSQL()));

      if (dataSourceXML.getDataSource().getQueryHints() != null) {
        dataSource.setCountSql(dataSource.getCountSql() + '\n' + dataSourceXML.getDataSource().getQueryHints());
        dataSource.setSelectSql(dataSource.getSelectSql() + '\n' + dataSourceXML.getDataSource().getQueryHints());
      }
      addListeners();

      createEventColumns();
      suspendDataSource();
      limitDataSource();
      setDataSourceQueuedDelay();

      if (dataSourceLimit != null) {
        dataSourceLimit.reloadDataSources();
      }
      storeCachedTemporaryTables();

      this.tableModel = createTableModel();


      if (dataSourceXML.getInformation() != null) {
        createInformationPanels();
      }
      getTaskPanes().addAll(createTaskPanes(dataSourceXML));

      if (dataSourceXML.getDataEntryPanel() != null) {
        createDataEntryPanel();
      }
    } catch (Throwable ex) {
      Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      resumeDataSource();
      dataSource.unlock();
    }

  }

  protected void storeCachedTemporaryTables() {
    if (cachedTemporaryTables == null) {
      cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
    }
    for (QueryParameter parameter : dataSourceXML.getDataSource().getParameters()) {
      if (parameter.getTemporaryTable() != null) {
        TemporaryTable tt = parameter.getTemporaryTable();
        if (tt.getMaterializedView() != null) {
          if (!cachedTemporaryTables.containsKey(tt.getMaterializedView().getValue())) {
            SqlUtilities.getInstance().storeCachedTemporaryTable(tt);
          }
        }
      }
    }
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
    for (QueryParameter parameter : dataSourceXML.getDataSource().getParameters()) {
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
          Object newInstance = ClassInstanceFactory.getInstance("wa" + this.getOpis() + "_" + System.currentTimeMillis(), dsf.getFactory(), DbDataSource.class, config.getClass()).newInstance(dataSource, config);
          if (newInstance != null) {
            if (newInstance instanceof AbstractDataSourceParametersFactory) {
              AbstractDataSourceParametersFactory instance = (AbstractDataSourceParametersFactory) newInstance;
              instance.setDataSourceParametersFactory(dsf);
              instance.configure();
              this.filtersMap = instance.getFiltersMap();
              this.filterPanel = instance.getFilterPanel();
              this.viewMenuItems.addAll(instance.getViewMenuItems());
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

    if (dataSourceXML.getDataSource().getOrderBy()!=null) {
      orderByParameter.setOrderBy(dataSourceXML.getDataSource().getOrderBy());
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
  }

  public List<String> getNamedParameters() {
    return namedParameters;
  }

  protected void limitDataSource() {
    if (dataSourceLimit != null) {
      getLimit().setSelected();
      dataSourceLimit.setValue(getLimit().getValue());
    }
  }

  protected void resumeDataSource() {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();

    Boolean resume = null;
    if (creationParameters != null) {
      resume = creationParameters.isResumeAfterCreation();
    } else {
      resume = dataSourceXML.getDataSource().isResumeAfterCreation();
    }
    if (resume != null && resume && dataSource.isSuspended()) {
      DataSourceEvent.resume(dataSource);
    } else {
      DataSourceEvent.resume(dataSource);
    }
  }

  protected DbDataSource createDataSource() throws ClassNotFoundException {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();

    String className = null;
    String provider = null;
    if (creationParameters != null) {
      className = creationParameters.getClassName();
      provider = creationParameters.getProviderClassName();
    } else {
      className = dataSourceXML.getDataSource().getClassName();
      provider = dataSourceXML.getDataSource().getProviderClassName();
    }
    final DbDataSource dataSource = className == null ? new DbDataSource() : new DbDataSource("", "", (Class<? extends DbDataSourceImpl>) Class.forName(className));
    if (provider != null) {
      dataSource.setProvider(provider);
    }

    Boolean canAddRows = null;
    Boolean canDeleteRows = null;
    if (creationParameters != null) {
      canAddRows = creationParameters.isCanAddRows();
      canDeleteRows = creationParameters.isCanDeleteRows();
    } else {
      canAddRows = dataSourceXML.getDataSource().isCanAddRows();
      canDeleteRows = dataSourceXML.getDataSource().isCanDeleteRows();
    }
    setCanExportData(dataSourceXML.isCanExportData());

    dataSource.setCanAddRows(canAddRows == null ? false : canAddRows);
    dataSource.setCanDeleteRows(canDeleteRows == null ? false : canDeleteRows);
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
  }

  protected void setDataSourceQueuedDelay() {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();

    Long delay = null;
    if (creationParameters != null) {
      delay = creationParameters.getQueuedDelay();
    } else {
      delay = dataSourceXML.getDataSource().getQueuedDelay();
    }
    dataSource.setQueuedDelay(delay == null ? DbDataSource.DEFAULT_QUEUED_DELAY : delay.longValue());
  }

  protected DataSourceLimit createDataSourceLimit() {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();

    Boolean useLimitParameter = null;
    if (creationParameters != null) {
      useLimitParameter = creationParameters.isUseLimitParameter();
    }

    if (dataSourceXML.getDataSource().getSQL().indexOf(DATA_SOURCE_LIMIT) == -1) {
      useLimitParameter = Boolean.FALSE;
    }

    DataSourceLimit dataSourceLimit = null;
    if (useLimitParameter == null || useLimitParameter.booleanValue()) {
      dataSourceLimit = new DataSourceLimit(DATA_SOURCE_LIMIT);
      dataSourceLimit.setValue(" TOP 0 ");
      dataSourceLimit.addDataSource(dataSource);
    } //        dsNaslovPendingSql.setPendingSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Pending.sql", "cp1250"),
    //                "Ulica", "HisnaStevilka", "PostnaStevilka", "Posta", "Naselje", "TipNaslova_Opis", "PPIzvori_Izvor");
    return dataSourceLimit;
  }

  protected void suspendDataSource() {
    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();

    Boolean suspend = null;
    if (creationParameters != null) {
      suspend = creationParameters.isSuspend();
    }
    if (suspend == null || suspend.booleanValue()) {
      DataSourceEvent.suspend(dataSource);
    }
  }

  protected void createInformationPanels() {
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

  protected DbTableModel createTableModel() {
    com.openitech.db.model.DbTableModel tableModel = new com.openitech.db.model.DbTableModel();
    final DataModel dataModel = dataSourceXML.getDataModel();
    if(dataModel != null){
    List<TableColumnDefinition> tableColumnDefinitions = dataModel.getTableColumns().getTableColumnDefinition();
    List<String[]> tableColumns = new ArrayList<String[]>();
    for (TableColumnDefinition tableColumnDefinition : tableColumnDefinitions) {
      tableColumns.add(tableColumnDefinition.getTableColumnEntry().toArray(new String[tableColumnDefinition.getTableColumnEntry().size()]));
    }
    tableModel.setColumns(tableColumns.toArray(new String[tableColumns.size()][]));
    if (dataSourceXML.getDataModel().getSeparator() != null) {
      tableModel.setSeparator(dataSourceXML.getDataModel().getSeparator());
    }
    }
    tableModel.setDataSource(dataSource);
    return tableModel;
  }

  protected void createEventColumns() throws SQLException {
  List<String> eventColumns = dataSourceXML.getDataSource().getEventColumns();
    if (eventColumns.size() > 0) {
      dataSource.setSafeMode(false);
      dataSource.setQueuedDelay(0);
      dataSource.filterChanged();
      dataSource.loadData();
      dataSource.setSafeMode(true);

      for (String imePolja : eventColumns) {
        int tipPolja = dataSource.getType(imePolja);
        DbFieldObserver fieldObserver = new DbFieldObserver();
        fieldObserver.setColumnName(imePolja);
        fieldObserver.setDataSource(dataSource);
        this.dataEntryValues.add(new FieldValueProxy(imePolja, tipPolja, fieldObserver, dataSource.getObject(imePolja)));
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
      if ((dataSourceXML.getAssociatedTasks() != null) && !dataSourceXML.getAssociatedTasks().getTaskPanes().isEmpty()) {
        for (TaskPanes taskPane : dataSourceXML.getAssociatedTasks().getTaskPanes()) {
          Object newInstance = null;
          if (taskPane.getFactory() != null) {
            ClassInstanceFactory.getInstance("wa" + (taskPane.getTitle() == null ? "" : taskPane.getTitle()) + "_" + System.currentTimeMillis(), taskPane.getFactory().getGroovy(), taskPane.getFactory().getClassName(), config.getClass()).newInstance(config);
          } else if (taskPane.getTaskList() != null && !taskPane.getTaskList().getTasks().isEmpty()) {
            newInstance = new JXDataSourceTaskPane(config, dataSource);
            ((JXDataSourceTaskPane) newInstance).setTitle(taskPane.getTitle());
            ((JXDataSourceTaskPane) newInstance).addTasks(taskPane.getTaskList().getTasks());
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

  private void createDataEntryPanel() throws ClassNotFoundException {
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

  private void addListeners() {
    try {
      Listeners listeners = dataSourceXML.getDataSource().getListeners();
      if (listeners!=null) {
        for (Listener listener : listeners.getListener()) {
          Object value = null;
          if (listener.getActionListener() != null) {
            dataSource.addActionListener((ActionListener) (value = ClassInstanceFactory.getInstance(listener.getActionListener(), new Class[] {}).newInstance(new Object[] {})));
          } else if (listener.getActiveRowChangeListener() != null) {
            dataSource.addActiveRowChangeListener((ActiveRowChangeListener) (value = ClassInstanceFactory.getInstance(listener.getActiveRowChangeListener(), new Class[] {}).newInstance(new Object[] {})));
          } else if (listener.getListDataListener() != null) {
            dataSource.addListDataListener((ListDataListener) (value = ClassInstanceFactory.getInstance(listener.getListDataListener(), new Class[] {}).newInstance(new Object[] {})));
          }

          if (value instanceof DataSourceObserver) {
            ((DataSourceObserver) value).setDataSource(dataSource);
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
