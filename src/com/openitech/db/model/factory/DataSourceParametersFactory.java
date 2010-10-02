/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.factory;

import com.openitech.db.filters.ActiveFiltersReader;
import com.openitech.db.filters.DataSourceFilters;
import com.openitech.db.model.DataSourceObserver;
import com.openitech.db.model.DbDataModel;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.sql.PendingSqlParameter;
import com.openitech.db.model.sql.SQLMaterializedView;
import com.openitech.db.model.sql.TemporarySubselectSqlParameter;
import com.openitech.db.model.xml.config.DataSourceFilter;
import com.openitech.db.model.xml.config.QueryParameter;
import com.openitech.db.model.xml.config.SubQuery;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.io.ReadInputStream;
import com.openitech.sql.util.SqlUtilities;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author uros
 */
public abstract class DataSourceParametersFactory<T extends DataSourceConfig> {

  protected static java.util.Map<String, TemporaryTable> cachedTemporaryTables;
  protected T config;
  protected DbDataSource dataSource = new DbDataSource();

  public DataSourceParametersFactory() {
  }

  protected Object createQueryParameter(QueryParameter parameter) {
    Object result = null;
    if (parameter.getTemporaryTable() != null) {
      final TemporarySubselectSqlParameter temporaryTable = createTemporaryTable(parameter.getTemporaryTable());
      result = temporaryTable;
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
    tt = getCachedTemporaryTable(tt);
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
      ttParameter.setSqlMaterializedView(mv);
    }
    ttParameter.setDisabled(tt.isDisabled());
    return ttParameter;
  }

  public TemporaryTable getCachedTemporaryTable(TemporaryTable tt) {
    if (cachedTemporaryTables == null) {
      cachedTemporaryTables = SqlUtilities.getInstance().getCachedTemporaryTables();
    }
    final boolean cached = (tt.getMaterializedView() != null) && cachedTemporaryTables.containsKey(tt.getMaterializedView().getValue());
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
    Object newInstance = null;
    if (dsf.getFactory().getGroovy() != null) {
      GroovyClassLoader gcl = new GroovyClassLoader(DataSourceFactory.class.getClassLoader());
      Class gcls = gcl.parseClass(dsf.getFactory().getGroovy(), "wa" + (dsf.getName() == null ? "" : dsf.getName()) + "_" + System.currentTimeMillis());
      Constructor constructor = gcls.getConstructor(String.class, config.getClass());
      newInstance = constructor.newInstance(dsf.getReplace(), config);
    } else if (dsf.getFactory().getClassName() != null) {
      @SuppressWarnings(value = "static-access")
      Class jcls = DataSourceFactory.class.forName(dsf.getFactory().getClassName());
      Constructor constructor = jcls.getConstructor(String.class, config.getClass());
      newInstance = constructor.newInstance(dsf.getReplace(), config);
    }
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
