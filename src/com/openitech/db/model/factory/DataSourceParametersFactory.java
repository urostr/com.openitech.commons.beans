/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.filters.ActiveFiltersReader;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.PendingSqlParameter;
import com.openitech.db.model.sql.SQLMaterializedView;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.MaterializedView.CacheEvents.Event;
import com.openitech.db.model.xml.config.QueryParameter;
import com.openitech.db.model.xml.config.SubQuery;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.db.model.xml.config.TemporaryTableGroup;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import com.openitech.value.events.EventType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author uros
 */
public abstract class DataSourceParametersFactory<T extends DataSourceConfig> {

  protected boolean override = Boolean.parseBoolean(ConnectionManager.getInstance().getProperty(ConnectionManager.DB_OVERRIDE_CACHED_VIEWS, "false"));
  protected static java.util.Map<String, TemporaryTable> cachedTemporaryTables;
  protected T config;
  protected DbDataSource dataSource = new DbDataSource();

  public DataSourceParametersFactory() {
  }

  protected Object createQueryParameter(QueryParameter parameter) {
    Object result = null;
    if (parameter.getNamedParameter() != null) {
      result = parameter.getNamedParameter();
    } else if (parameter.getTemporaryTable() != null) {
      final TemporarySubselectSqlParameter temporaryTable = createTemporaryTable(parameter.getTemporaryTable());
      result = temporaryTable;
    } if (parameter.getTemporaryTableGroup() != null) {
      final java.util.List<TemporarySubselectSqlParameter> temporaryTables = createTemporaryTableGroup(parameter.getTemporaryTableGroup());
      result = temporaryTables;
    } else if (parameter.getSubQuery() != null) {
      result = createSubQuery(parameter.getSubQuery());
    } else if (parameter.getSqlParameter() != null) {
      final DbDataSource.SqlParameter<Object> sqlParameter = new DbDataSource.SqlParameter<Object>();
      sqlParameter.setValue(parameter.getSqlParameter().getValue());
      sqlParameter.setType(parameter.getSqlParameter().getType());
      result = sqlParameter;
    } else if (parameter.getDataSourceFilter() != null) {
      try {
        DataSourceFilter dsf = parameter.getDataSourceFilter();
        Object newInstance = createDataSourceFilter(dsf);
        if (newInstance != null) {
          DataSourceFilters filter = null;
          if (newInstance instanceof ActiveFiltersReader) {
            ((ActiveFiltersReader) newInstance).getActiveFilter();
          } else if (newInstance instanceof DataSourceFilters) {
            filter = (DataSourceFilters) newInstance;
          }
          if (filter != null) {
            if (dsf.getOperator() != null) {
              filter.setOperator(dsf.getOperator());
            }
          }
          result = filter;
        }
      } catch (Exception ex) {
        Logger.getLogger(DbDataModel.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return result;
  }

  protected PendingSqlParameter createSubQuery(SubQuery subQuery) {
    PendingSqlParameter subQueryFilter = new PendingSqlParameter(subQuery.getReplace());
    subQueryFilter.setImmediateSQL(subQuery.getImmediateSQL());
    subQueryFilter.setPendingSQL(subQuery.getPendingSQL(), subQuery.getPendingColumns().getColumnNames().toArray(new String[subQuery.getPendingColumns().getColumnNames().size()]));
    subQueryFilter.setDeferredSQL(subQuery.getDeferredSQL(), subQuery.getParentKey().getColumnNames().toArray(new String[subQuery.getParentKey().getColumnNames().size()]));
    subQueryFilter.setSupportsMultipleKeys(subQuery.isSupportsMultipleKeys());
    if (subQuery.getMultipleKeysLimit() != null) {
      subQueryFilter.setMultipleKeysLimit(subQuery.getMultipleKeysLimit());
    }
    return subQueryFilter;
  }

  protected TemporarySubselectSqlParameter createTemporaryTable(TemporaryTable tt) {
    String tt_replace = tt.getReplace();
    tt = getCachedTemporaryTable(tt);
    TemporarySubselectSqlParameter ttParameter = createTemporaryTableParameter(tt);
    ttParameter.setReplace(tt_replace);
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
    if (tt.getIsTableDataValidSql() != null) {
      ttParameter.setIsTableDataValidSql(getReplacedSql(tt.getIsTableDataValidSql()));
    }
    if (tt.getCatalog()!=null) {
      ttParameter.setCatalog(tt.getCatalog());
    }
    ttParameter.setHasParameters(tt.isUseParameters());

    if (tt.getParameter() != null) {
      for (QueryParameter parameter : tt.getParameter().getParameters()) {
        final Object queryParameter = createQueryParameter(parameter);
        if (queryParameter != null) {
          ttParameter.addParameter(queryParameter);
        }
      }
    }
    if (tt.getMaterializedView() != null) {
      String replace = tt.getMaterializedView().getReplace();
      if (replace == null) {
        replace = tt.getTableName();
      }
      SQLMaterializedView mv = new SQLMaterializedView(replace);
      mv.setValue(tt.getMaterializedView().getValue());
      mv.setIsViewValidSQL(tt.getMaterializedView().getIsViewValidSql());
      mv.setSetViewVersionSql(tt.getMaterializedView().getSetViewVersionSql());
      mv.setHasParameters(tt.getMaterializedView().isUseParameters());

      if (tt.getMaterializedView().getCacheEvents()!=null &&
          tt.getMaterializedView().getCacheEvents().getEvent().size()>0) {
        mv.setCacheEvent(true);
        for (Event event : tt.getMaterializedView().getCacheEvents().getEvent()) {
          mv.getCacheEventTypes().add(new EventType(event.getSifrant(), event.getSifra(), event.isCacheOnUpdate()));
        }
      }
      
      ttParameter.setSqlMaterializedView(mv);
    }
    ttParameter.setDisabled(tt.isDisabled());
    return ttParameter;
  }

  protected TemporarySubselectSqlParameter createTemporaryTableParameter(TemporaryTable tt) {
    return new TemporarySubselectSqlParameter(tt.getReplace());
  }

  protected List<TemporarySubselectSqlParameter> createTemporaryTableGroup(TemporaryTableGroup temporaryTableGroup) {
    List<TemporarySubselectSqlParameter> result = new java.util.ArrayList<TemporarySubselectSqlParameter>(temporaryTableGroup.getTemporaryTable().size());

    TemporarySubselectSqlParameter.TemporaryTableGroup group = null;

    for (TemporaryTable temporaryTable : temporaryTableGroup.getTemporaryTable()) {
      TemporarySubselectSqlParameter tt = createTemporaryTable(temporaryTable);
      if (group==null) {
        group = tt.getGroup();
      } else {
        group.add(tt);
      }

      result.add(tt);
    }

    return result;
  }

  public TemporaryTable getCachedTemporaryTable(TemporaryTable tt) {
    if (cachedTemporaryTables == null) {
      cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
    }
    final boolean cached = !override && (tt.getMaterializedView() != null) && cachedTemporaryTables.containsKey(tt.getMaterializedView().getValue());
    if (cached) {
      tt = cachedTemporaryTables.get(tt.getMaterializedView().getValue());
      System.out.println("CACHED:TT:" + tt.getMaterializedView().getValue());
    } else if (tt.getMaterializedView() != null) {
      SqlUtilities.getInstance().storeCachedTemporaryTable(tt);
      cachedTemporaryTables.put(tt.getMaterializedView().getValue(), tt);
    }
    return tt;
  }

  protected Object createDataSourceFilter(DataSourceFilter dsf) throws NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, CompilationFailedException, IllegalArgumentException, SecurityException {
    Object newInstance = ClassInstanceFactory.getInstance("wa" + (dsf.getName() == null ? "" : dsf.getName()) + "_" + System.currentTimeMillis(), dsf.getFactory(), String.class, config.getClass()).newInstance(dsf.getReplace(), config);
    if (newInstance != null) {
      if (newInstance instanceof DataSourceObserver) {
        ((DataSourceObserver) newInstance).setDataSource(dataSource);
      }
    }
    return newInstance;
  }

  /**
   * Get the value of dataSource
   *
   * @return the value of dataSource
   */
  public DbDataSource getDataSource() {
    return dataSource;
  }

  protected String getReplacedSql(String sql) {
    return ReadInputStream.getReplacedSql(sql);
  }

  protected String[] getReplacedSqls(String[] sqls) {
    return ReadInputStream.getReplacedSqls(sqls);
  }
}
