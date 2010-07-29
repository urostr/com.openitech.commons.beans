/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.filters.ActiveFiltersReader;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.filters.DataSourceLimit;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.db.model.DbFieldObserver;
import com.openitech.db.model.xml.config.Workarea.DataSource.CreationParameters;
import com.openitech.events.concurrent.DataSourceEvent;
import com.openitech.db.model.sql.PendingSqlParameter;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.DataSourceParametersFactory;
import com.openitech.db.model.xml.config.SubQuery;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.db.model.xml.config.Workarea.DataModel.TableColumns.TableColumnDefinition;
import com.openitech.db.model.xml.config.Workarea.DataSource.Parameters;
import com.openitech.value.fields.FieldValueProxy;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class DataSourceFactory extends AbstractDataSourceFactory {

  public DataSourceFactory(DbDataModel dbDataModel) {
    super(dbDataModel);
  }

  @Override
  public void configure(final AbstractDataSourceFactory factory, final String opis, com.openitech.db.model.factory.DataSourceConfig config, com.openitech.db.model.xml.config.Workarea dataSourceXML) throws SQLException, ClassNotFoundException {
    factory.opis = opis;

    CreationParameters creationParameters = dataSourceXML.getDataSource().getCreationParameters();
    String className = null;
    String provider = null;
    if (creationParameters != null) {
      className = creationParameters.getClassName();
      provider = creationParameters.getProviderClassName();
    }
    final DbDataSource dataSource = className == null ? new DbDataSource() : new DbDataSource("", "", (Class<? extends DbDataSourceImpl>) Class.forName(className));
    if (provider != null) {
      dataSource.setProvider(provider);
    }

    dataSource.lock();
    try {
      dataSource.setQueuedDelay(0);

      Boolean canAddRows = null;
      Boolean canDeleteRows = null;
      if (creationParameters != null) {
        canAddRows = creationParameters.isCanAddRows();
        canDeleteRows = creationParameters.isCanDeleteRows();
      }
      dataSource.setCanAddRows(canAddRows == null ? false : canAddRows);
      dataSource.setCanDeleteRows(canDeleteRows == null ? false : canDeleteRows);


      java.util.List parameters = new java.util.ArrayList();
      Boolean useLimitParameter = null;
      if (creationParameters != null) {
        useLimitParameter = creationParameters.isUseLimitParameter();
      }
      DataSourceLimit dataSourceLimit = null;
      if (useLimitParameter == null || useLimitParameter.booleanValue()) {
        dataSourceLimit = new DataSourceLimit("<%DATA_SOURCE_LIMIT%>");

        dataSourceLimit.setValue(" TOP 0 ");

        parameters.add(dataSourceLimit);

        dataSourceLimit.addDataSource(dataSource);
      }
//                  dsNaslovPendingSql.setImmediateSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Immediate.sql", "cp1250"));
//        dsNaslovPendingSql.setPendingSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Pending.sql", "cp1250"),
//                "Ulica", "HisnaStevilka", "PostnaStevilka", "Posta", "Naselje", "TipNaslova_Opis", "PPIzvori_Izvor");
//        dsNaslovPendingSql.setDeferredSQL(ReadInputStream.getResourceAsString(getClass(), "sql/PP.Deferred.sql", "cp1250"), "PPId");
//        dsPoslovniPartnerjiFilter.setOperator("AND");
      for (Parameters parameter : dataSourceXML.getDataSource().getParameters()) {
        if (parameter.getTemporaryTable() != null) {
          TemporaryTable tt = parameter.getTemporaryTable();
          TemporarySubselectSqlParameter ttParameter = new TemporarySubselectSqlParameter(tt.getReplace());

          ttParameter.setValue(tt.getTableName());
          ttParameter.setCheckTableSql(getReplacedSql(tt.getCheckTableSql()));
          ttParameter.setCreateTableSqls(getReplacedSqls(tt.getCreateTableSqls().getQuery().toArray(new String[]{})));
          ttParameter.setEmptyTableSql(getReplacedSql(tt.getEmptyTableSql()));
          ttParameter.setFillTableSql(getReplacedSql(tt.getFillTableSql()));
          if (tt.getCleanTableSqls() != null) {
            ttParameter.setCleanTableSqls(getReplacedSqls(tt.getCleanTableSqls().getQuery().toArray(new String[]{})));
          }

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
              Class gcls = gcl.parseClass(dsf.getFactory().getGroovy(), "wa" + factory.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(String.class, com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsf.getReplace(), config);
            } else if (dsf.getFactory().getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(dsf.getFactory().getClassName());
              Constructor constructor = jcls.getConstructor(String.class, com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(dsf.getReplace(), config);
            }

            if (newInstance != null) {
              if (newInstance instanceof java.awt.Component) {
                factory.filterPanel = (java.awt.Component) newInstance;
              }
              if (newInstance instanceof DataSourceObserver) {
                ((DataSourceObserver) newInstance).setDataSource(dataSource);
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
        } else if ((parameter.getDataSourceParametersFactory() != null)
                || (parameter.getDataSourceFilterFactory() != null)) {
          try {
            Object newInstance = null;
            DataSourceParametersFactory dsf = (parameter.getDataSourceParametersFactory() != null) ? parameter.getDataSourceParametersFactory() : parameter.getDataSourceFilterFactory();
            if (dsf.getFactory().getGroovy() != null) {
              GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
              Class gcls = gcl.parseClass(dsf.getFactory().getGroovy(), "wa" + factory.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(DbDataSource.class, com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(dataSource, config);
            } else if (dsf.getFactory().getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(dsf.getFactory().getClassName());
              Constructor constructor = jcls.getConstructor(DbDataSource.class, com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(dataSource, config);
            }

            if (newInstance != null) {
              if (newInstance instanceof AbstractDataSourceParametersFactory) {
                AbstractDataSourceParametersFactory instance = (AbstractDataSourceParametersFactory) newInstance;
                factory.filterPanel = instance.getFilterPanel();
                factory.viewMenuItems.addAll(instance.getViewMenuItems());

                parameters.addAll(instance.getParameters());
              }
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }

      dataSource.setParameters(parameters);

      if (dataSourceXML.getDataSource().getCOUNTSQL() != null) {
        dataSource.setCountSql(getReplacedSql(dataSourceXML.getDataSource().getCOUNTSQL()));
      }
      dataSource.setSelectSql(getReplacedSql(dataSourceXML.getDataSource().getSQL()));
      dataSource.setName("DS:FACTORY:" + factory.getOpis());

      List<String> eventColumns = dataSourceXML.getDataSource().getEventColumns();

      if (eventColumns.size() > 0) {
        dataSource.setQueuedDelay(0);
        if (dataSourceLimit != null) {
          dataSourceLimit.reloadDataSources();
        }
        for (String imePolja : eventColumns) {
          int tipPolja = dataSource.getType(imePolja);

          DbFieldObserver fieldObserver = new DbFieldObserver();
          fieldObserver.setColumnName(imePolja);
          fieldObserver.setDataSource(dataSource);
          factory.dataEntryValues.add(new FieldValueProxy(imePolja, tipPolja, fieldObserver));
        }



      }
      Boolean suspend = null;
      if (creationParameters != null) {
        suspend = creationParameters.isSuspend();
      }
      if (suspend == null || suspend.booleanValue()) {
        DataSourceEvent.suspend(dataSource);
      }

      if (dataSourceLimit != null) {
        DataSourceLimit.Limit.LALL.setSelected();
        dataSourceLimit.setValue(DataSourceLimit.Limit.LALL.getValue());
      }

      Long delay = null;
      if (creationParameters != null) {
        delay = creationParameters.getQueuedDelay();
      }
      dataSource.setQueuedDelay(delay == null ? DbDataSource.DEFAULT_QUEUED_DELAY : delay.longValue());

      if (dataSourceLimit != null) {
        dataSourceLimit.reloadDataSources();
      }

      factory.dataSource = dataSource;
      com.openitech.db.model.DbTableModel tableModel = new com.openitech.db.model.DbTableModel();

      List<TableColumnDefinition> tableColumnDefinitions = dataSourceXML.getDataModel().getTableColumns().getTableColumnDefinition();
      List<String[]> tableColumns = new ArrayList<String[]>();
      for (TableColumnDefinition tableColumnDefinition : tableColumnDefinitions) {
        tableColumns.add(tableColumnDefinition.getTableColumnEntry().toArray(new String[tableColumnDefinition.getTableColumnEntry().size()]));
      }

      tableModel.setColumns(tableColumns.toArray(new String[tableColumns.size()][]));

      if (dataSourceXML.getDataModel().getSeparator() != null) {
        tableModel.setSeparator(dataSourceXML.getDataModel().getSeparator());
      }

      tableModel.setDataSource(dataSource);
      factory.tableModel = tableModel;


      if (dataSourceXML.getInformation() != null) {
        for (com.openitech.db.model.xml.config.Workarea.Information.Panels panel : dataSourceXML.getInformation().getPanels()) {
          try {
            Object newInstance = null;
            if (panel.getGroovy() != null) {
              GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
              Class gcls = gcl.parseClass(panel.getGroovy(), "wa" + factory.getOpis() + "_" + System.currentTimeMillis());
              Constructor constructor = gcls.getConstructor(com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(config);
            } else if (panel.getClassName() != null) {
              @SuppressWarnings("static-access")
              Class jcls = DataSourceFactory.class.forName(panel.getClassName());
              Constructor constructor = jcls.getConstructor(com.openitech.db.model.factory.DataSourceConfig.class);
              newInstance = constructor.newInstance(config);
            }
            if (newInstance instanceof java.awt.Component) {
              factory.informationPanels.put(panel.getTitle(), (java.awt.Component) newInstance);
            } else {
              factory.linkedObjects.put(panel.getTitle(), newInstance);
            }
            if (newInstance instanceof DataSourceObserver) {
              ((DataSourceObserver) newInstance).setDataSource(dataSource);
            }
          } catch (Exception ex) {
            Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    } finally {
      Boolean resume = null;
      if (creationParameters != null) {
        resume = creationParameters.isResumeAfterCreation();
      }
      if (resume != null && resume && dataSource.isSuspended()) {
        DataSourceEvent.resume(dataSource);
      }
      dataSource.unlock();
    }

  }
}
