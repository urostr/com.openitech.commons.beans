/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import com.openitech.db.model.xml.config.MaterializedView;
import com.openitech.db.model.xml.config.MaterializedView.CacheEvents.Event;
import com.openitech.db.model.xml.config.QueryParameter;
import com.openitech.db.model.xml.config.TemporaryTable;
import com.openitech.io.LogWriter;
import com.openitech.sql.SQLWorker;
import com.openitech.sql.util.TransactionManager;
import com.openitech.util.Equals;
import com.openitech.value.events.EventType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class TemporarySubselectSqlParameter extends SubstSqlParameter {

  private static final Map<String, ReentrantLock> locks = Collections.synchronizedMap(new HashMap<String, ReentrantLock>());
  protected final LogWriter logWriter = new LogWriter(Logger.getLogger(TemporarySubselectSqlParameter.class.getName()), Level.INFO);
  protected final SQLWorker sqlWorker = new SQLWorker(logWriter);
  private Semaphore semaphore = new Semaphore(1);

  public TemporarySubselectSqlParameter(String replace) {
    super(replace);
  }

  @Override
  public int getType() {
    return Types.SUBST_ALL;
  }
  private String[] createTableSqls;

  /**
   * Get the value of createTableSqls
   *
   * @return the value of createTableSqls
   */
  public String[] getCreateTableSqls() {
    return createTableSqls;
  }

  /**
   * Set the value of createTableSqls
   *
   * @param createTableSqls new value of createTableSqls
   */
  public void setCreateTableSqls(String... createTableSqls) {
    this.createTableSqls = createTableSqls;
  }
  private String fillTableSql;

  /**
   * Get the value of fillTableSql
   *
   * @return the value of fillTableSql
   */
  public String getFillTableSql() {
    return fillTableSql;
  }

  /**
   * Set the value of fillTableSql
   *
   * @param fillTableSql new value of fillTableSql
   */
  public void setFillTableSql(String fillTableSql) {
    this.fillTableSql = fillTableSql;
  }
  private String[] cleanTableSqls;

  /**
   * Get the value of cleanTableSqls
   *
   * @return the value of cleanTableSqls
   */
  public String[] getCleanTableSqls() {
    return cleanTableSqls;
  }

  /**
   * Set the value of cleanTableSqls
   *
   * @param cleanTableSqls new value of cleanTableSqls
   */
  public void setCleanTableSqls(String... cleanTableSqls) {
    this.cleanTableSqls = cleanTableSqls;
  }
  private boolean fillOnceOnly = false;

  /**
   * Get the value of fillOnceOnly
   *
   * @return the value of fillOnceOnly
   */
  public boolean isFillOnceOnly() {
    return fillOnceOnly;
  }

  /**
   * Set the value of fillOnceOnly
   *
   * @param fillOnceOnly new value of fillOnceOnly
   */
  public void setFillOnceOnly(boolean fillOnceOnly) {
    this.fillOnceOnly = fillOnceOnly;
  }
  private boolean disabled = false;

  /**
   * Get the value of disabled
   *
   * @return the value of disabled
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Set the value of disabled
   *
   * @param disabled new value of disabled
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
  private String emptyTableSql = "";

  /**
   * Get the value of emptyTableSql
   *
   * @return the value of emptyTableSql
   */
  public String getEmptyTableSql() {
    return emptyTableSql;
  }

  /**
   * Set the value of emptyTableSql
   *
   * @param emptyTableSql new value of emptyTableSql
   */
  public void setEmptyTableSql(String emptyTableSql) {
    this.emptyTableSql = emptyTableSql == null ? "" : emptyTableSql;
  }
  private String catalog;

  /**
   * Get the value of catalog
   *
   * @return the value of catalog
   */
  public String getCatalog() {
    return catalog;
  }

  /**
   * Set the value of catalog
   *
   * @param catalog new value of catalog
   */
  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }
  private String checkTableSql;

  /**
   * Get the value of checkTableSql
   *
   * @return the value of checkTableSql
   */
  public String getCheckTableSql() {
    return checkTableSql;
  }

  /**
   * Set the value of checkTableSql
   *
   * @param checkTableSql new value of checkTableSql
   */
  public void setCheckTableSql(String checkTableSql) {
    this.checkTableSql = checkTableSql;
  }
  protected String isTableDataValidSql;

  /**
   * Get the value of isTableDataValidSql
   *
   * @return the value of isTableDataValidSql
   */
  public String getIsTableDataValidSql() {
    return isTableDataValidSql;
  }

  /**
   * Set the value of isTableDataValidSql
   *
   * @param isTableDataValidSql new value of isTableDataValidSql
   */
  public void setIsTableDataValidSql(String isTableDataValidSql) {
    this.isTableDataValidSql = isTableDataValidSql;
  }
  protected SQLMaterializedView sqlMaterializedView;

  /**
   * Get the value of sqlMaterializedView
   *
   * @return the value of sqlMaterializedView
   */
  public SQLMaterializedView getSqlMaterializedView() {
    return sqlMaterializedView;
  }

  /**
   * Set the value of sqlMaterializedView
   *
   * @param sqlMaterializedView new value of sqlMaterializedView
   */
  public void setSqlMaterializedView(SQLMaterializedView sqlMaterializedView) {
    this.sqlMaterializedView = sqlMaterializedView;
  }

  @Override
  public String getValue() {
    return sqlMaterializedView == null ? super.getValue() : sqlMaterializedView.getValue();
  }
  protected boolean useParameters = false;

  /**
   * Get the value of useParameters
   *
   * @return the value of useParameters
   */
  public boolean isUseParameters() {
    return useParameters;
  }

  /**
   * Set the value of useParameters
   *
   * @param useParameters new value of useParameters
   */
  public void setHasParameters(boolean useParameters) {
    this.useParameters = useParameters;
  }

  public SubstSqlParameter getSubstSqlParameter() {
    SubstSqlParameter substSqlParameter = new DbDataSource.SubstSqlParameter(this.getReplace());
    substSqlParameter.setValue(this.getValue());
    substSqlParameter.setType(com.openitech.db.model.Types.SUBST_ALL);

    return substSqlParameter;
  }
  protected Connection connection = null;
  protected String qFillTable;
  protected PreparedStatement psFillTable = null;
  protected String qEmptyTable;
  protected List<PreparedStatement> psEmptyTable = new ArrayList<PreparedStatement>();
  protected List<String> qCleanTable = new ArrayList<String>();
  protected List<PreparedStatement> psCleanTable = new ArrayList<PreparedStatement>();
  protected String qIsTableDataValidSql = null;
  protected List<PreparedStatement> psIsTableDataValidSql = new ArrayList<PreparedStatement>();

  public boolean isTableDataValidSql(Connection connection, java.util.List<Object> parameters) {
    if (isTableDataValidSql == null) {
      return true;
    } else {
      try {
        long timer = System.currentTimeMillis();
        String query = sqlWorker.substParameters(isTableDataValidSql, parameters);
        if (!Equals.equals(this.connection, connection)
                || !Equals.equals(this.qIsTableDataValidSql, query)) {
          String[] sqls = query.split(";");
          for (PreparedStatement preparedStatement : this.psIsTableDataValidSql) {
            preparedStatement.close();
          }
          this.psIsTableDataValidSql.clear();
          for (String sql : sqls) {
            this.psIsTableDataValidSql.add(connection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT));
          }
          this.qIsTableDataValidSql = query;
        }

        boolean result = true;

        if (DbDataSource.DUMP_SQL) {
          logWriter.println("##############");
          logWriter.println(this.qIsTableDataValidSql);
        }
        for (PreparedStatement preparedStatement : psIsTableDataValidSql) {
          ResultSet executeQuery = sqlWorker.executeQuery(preparedStatement, parameters);
          try {
            if (executeQuery.next()) {
              result = result && executeQuery.getBoolean(1);
            }
          } finally {
            executeQuery.close();
          }
          if (!result) {
            break;
          }
        }
        if (DbDataSource.DUMP_SQL) {
          logWriter.format("tt:isvalid:{0}..[{1}]...{2}ms", new Object[]{getValue(), result, System.currentTimeMillis() - timer});
          logWriter.println("##############");
        }
        return result;
      } catch (SQLException ex) {
        Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
    }
  }

  public void executeQuery(Connection connection, List<Object> parameters) throws SQLException, InterruptedException {
    final SQLMaterializedView mv = getSqlMaterializedView();

    if (mv == null || (mv != null
            && !mv.isIndexedView())) {

      boolean fill = !isFillOnceOnly();
      long timer = System.currentTimeMillis();

      String DB_USER = ConnectionManager.getInstance().getProperty(ConnectionManager.DB_USER, "");
      List<Object> qparams = new ArrayList<Object>(parameters.size());
      qparams.addAll(getParameters());
      qparams.addAll(parameters);
      qparams.add(getSubstSqlParameter());
      if (mv != null) {
        qparams.add(mv);
      }

      String table = mv == null ? getValue() : mv.getValue();

      ReentrantLock lock = null;
      if (locks.containsKey(table)) {
        lock = locks.get(table);
      } else {
        lock = new ReentrantLock();
        locks.put(table, lock);
      }

      if (!(lock.tryLock() || lock.tryLock(1, TimeUnit.SECONDS))) {
        throw new SQLException("Can't lock " + table);
      }

      try {
        synchronized (connection) {
          qparams = sqlWorker.preprocessParameters(qparams, connection);
//          boolean preprocessed = true;

          Statement statement = connection.createStatement();
          semaphore.acquire();
          try {
            TransactionManager tm = TransactionManager.getInstance(connection);

            boolean transaction = false;
            boolean commit = false;
//            boolean preprocessed = false;

            try {
              try {
                if (checkTableSql != null) {
                  statement.executeQuery(sqlWorker.substParameters(checkTableSql, qparams));
                }
              } catch (SQLException ex) {
                if (sqlMaterializedView != null) {
                  if (!tm.isTransaction()) {
                    tm.beginTransaction();
                    transaction = true;
                  }
                }

//                qparams = sqlWorker.preprocessParameters(qparams, connection);
//                preprocessed = true;

                String context = connection.getCatalog();

                if (getCatalog() != null) {
                  logWriter.println("SET:CATALOG:" + catalog);
                  connection.setCatalog(catalog);
                }

                Statement createStatement = connection.createStatement();
                try {
                  for (String sql : createTableSqls) {
                    String createSQL = sqlWorker.substParameters(sql.replaceAll("<%TS%>", "_" + DB_USER + Long.toString(System.currentTimeMillis())), qparams);
                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println(createSQL + ";");
                      logWriter.println("-- -- -- --");
                    }
                    try {
                      createStatement.execute(createSQL);
                    } catch (SQLException ex1) {
                      Logger.getAnonymousLogger().log(Level.SEVERE, null, ex1);
                      throw new SQLException(ex1);
                    }
                  }
                } finally {
                  createStatement.close();

                  if (getCatalog() != null) {
                    logWriter.println("SET:CATALOG:" + context);
                    connection.setCatalog(context);
                  }
                }


                fill = true;

                if (mv != null
                        && !mv.getCacheEventTypes().isEmpty()
                        && mv.getCacheEventTypes().get(0).getIndexedAsView() != null) {
                  mv.setIndexedView(true);
                }
              }


              if (isUseParameters()) {
//                if (!preprocessed) {
//                  qparams = sqlWorker.preprocessParameters(qparams, connection);
//                  preprocessed = true;
//                }
                fill = fill || !isTableDataValidSql(connection, qparams);
              } else {
                fill = fill || !isTableDataValidSql(connection, new ArrayList<Object>());
              }


              if ((!fill) && (mv != null)) {
                if (mv.isUseParameters()) {
//                  if (!preprocessed) {
//                    qparams = sqlWorker.preprocessParameters(qparams, connection);
//                    preprocessed = true;
//                  }
                  fill = fill || !mv.isViewValid(connection, qparams);
                } else {
                  fill = fill || !mv.isViewValid(connection, new ArrayList<Object>());
                }
              }

              if (fill && !disabled) {
//                if (!preprocessed) {
//                  qparams = sqlWorker.preprocessParameters(qparams, connection);
//                  preprocessed = true;
//                }

                if (mv != null) {
                  if (!(transaction || tm.isTransaction())) {
                    tm.beginTransaction();
                    transaction = true;
                  }
                }

                try {
                  if (emptyTableSql.length() > 0) {
                    String query = sqlWorker.substParameters(emptyTableSql, qparams);
                    if (!Equals.equals(this.connection, connection)
                            || !Equals.equals(this.qEmptyTable, query)) {
                      String[] sqls = query.split(";");
                      for (PreparedStatement preparedStatement : this.psEmptyTable) {
                        preparedStatement.close();
                      }
                      this.psEmptyTable.clear();
                      for (String sql : sqls) {
                        this.psEmptyTable.add(connection.prepareStatement(sql,
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY,
                                ResultSet.HOLD_CURSORS_OVER_COMMIT));
                      }
                      this.qEmptyTable = query;
                    }

                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println("############## empty");
                      logWriter.println(this.qEmptyTable);
                    }
                    int rowsDeleted = 0;
                    for (PreparedStatement preparedStatement : psEmptyTable) {
                      rowsDeleted += sqlWorker.executeUpdate(preparedStatement, qparams);
                    }
                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println("Rows deleted:" + rowsDeleted);
                    }
                  }
                  if (fillTableSql != null) {
                    String query = sqlWorker.substParameters(fillTableSql, qparams);
                    if (!Equals.equals(this.connection, connection)
                            || !Equals.equals(this.qFillTable, query)) {
                      if (this.psFillTable != null) {
                        this.psFillTable.close();
                      }
                      this.psFillTable = connection.prepareStatement(query,
                              ResultSet.TYPE_SCROLL_INSENSITIVE,
                              ResultSet.CONCUR_READ_ONLY,
                              ResultSet.HOLD_CURSORS_OVER_COMMIT);
                      this.qFillTable = query;
                    }

                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println("############## fill");
                      logWriter.println(this.qFillTable);
                    }
                    try {
                      logWriter.println("Rows added:" + sqlWorker.executeUpdate(psFillTable, qparams));
                    } catch (SQLException ex1) {
                      Logger.getAnonymousLogger().log(Level.SEVERE, null, ex1);
                      throw new SQLException(ex1);
                    }
                  }
                  if (cleanTableSqls != null) {
                    List<String> queries = new ArrayList<String>(cleanTableSqls.length);
                    for (String sql : cleanTableSqls) {
                      queries.add(sqlWorker.substParameters(sql, qparams));
                    }

                    if (!Equals.equals(this.connection, connection)
                            || !Equals.equals(this.qCleanTable, queries)) {
                      for (PreparedStatement preparedStatement : this.psCleanTable) {
                        preparedStatement.close();
                      }
                      this.psCleanTable.clear();
                      for (String sql : queries) {
                        this.psCleanTable.add(connection.prepareStatement(sql,
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY,
                                ResultSet.HOLD_CURSORS_OVER_COMMIT));
                      }
                      this.qCleanTable = queries;
                    }

                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println("############## cleanup/update");
                      for (String string : queries) {
                        logWriter.println(string);
                      }
                    }
                    int rowsAffected = 0;
                    for (PreparedStatement preparedStatement : psCleanTable) {
                      rowsAffected += sqlWorker.executeUpdate(preparedStatement, qparams);
                    }
                    if (DbDataSource.DUMP_SQL) {
                      logWriter.println("Rows cleaned/updated:" + rowsAffected);
                    }
                  }
                  if ((mv != null) && (mv.setViewVersionSql != null)) {
                    PreparedStatement ps = connection.prepareStatement(sqlWorker.substParameters(sqlMaterializedView.setViewVersionSql, qparams));
                    try {
                      sqlWorker.execute(ps, qparams);
                    } finally {
                      ps.close();
                    }
                  }
                  if (DbDataSource.DUMP_SQL) {
                    logWriter.println("temporary:fill:" + getValue() + "..." + (System.currentTimeMillis() - timer) + "ms");
                    logWriter.println("##############");
                  }


                  commit = true;
                } catch (SQLException ex) {
                  logWriter.println("ERROR:temporary:fill:" + getValue());
                  logWriter.flush(logWriter.getLogger(), Level.SEVERE, ex);
                  throw new SQLException(ex);
                }
              }
            } finally {
              if (transaction) {
                tm.endTransaction(commit);
              }
            }
          } finally {
            semaphore.release();
            statement.close();
          }
          this.connection = connection;
        }
      } finally {
        lock.unlock();
        logWriter.flush();
      }
    }
  }

  public TemporaryTable getTemporaryTable() {
    TemporaryTable tt = new TemporaryTable();

    tt.setFillOnceOnly(isFillOnceOnly());
    tt.setTableName(getValue());
    tt.setReplace(getReplace());
    tt.setUseParameters(isUseParameters());
    tt.setCheckTableSql(getCheckTableSql());
    if (getCleanTableSqls() != null) {
      tt.setCleanTableSqls(new TemporaryTable.CleanTableSqls());
      tt.getCleanTableSqls().getQuery().addAll(Arrays.asList(getCleanTableSqls()));
    }
    if (getCreateTableSqls() != null) {
      tt.setCreateTableSqls(new TemporaryTable.CreateTableSqls());
      tt.getCreateTableSqls().getQuery().addAll(Arrays.asList(getCreateTableSqls()));
    }
    if (getIsTableDataValidSql() != null) {
      tt.setIsTableDataValidSql(getIsTableDataValidSql());
    }
    if (getCatalog() != null) {
      tt.setCatalog(getCatalog());
    }
    tt.setEmptyTableSql(getEmptyTableSql());
    tt.setFillTableSql(getFillTableSql());

    if (getParameters().size() > 0) {
      tt.setParameter(new TemporaryTable.Parameter());
      for (Object parameter : getParameters()) {
        if (parameter instanceof TemporarySubselectSqlParameter) {
          QueryParameter queryParameter = new QueryParameter();

          queryParameter.setTemporaryTable(((TemporarySubselectSqlParameter) parameter).getTemporaryTable());

          tt.getParameter().getParameters().add(queryParameter);
        } else if (parameter instanceof PendingSqlParameter) {
          QueryParameter queryParameter = new QueryParameter();

          queryParameter.setSubQuery(((PendingSqlParameter) parameter).getSubQuery());

          tt.getParameter().getParameters().add(queryParameter);
        } else if (parameter instanceof DbDataSource.SqlParameter<?>) {
          QueryParameter queryParameter = new QueryParameter();

          queryParameter.setSqlParameter(((DbDataSource.SqlParameter) parameter).getSqlParameter());

          tt.getParameter().getParameters().add(queryParameter);
        }
      }
    }

    if (getSqlMaterializedView() != null) {
      tt.setMaterializedView(new MaterializedView());
      tt.getMaterializedView().setUseParameters(getSqlMaterializedView().isUseParameters());
      tt.getMaterializedView().setValue(getSqlMaterializedView().getValue());
      tt.getMaterializedView().setIsViewValidSql(getSqlMaterializedView().getIsViewValidSQL());
      tt.getMaterializedView().setSetViewVersionSql(getSqlMaterializedView().getSetViewVersionSql());

      if (getSqlMaterializedView().getCacheEventTypes().size() > 0) {
        tt.getMaterializedView().setCacheEvents(new MaterializedView.CacheEvents());
        List<Event> events = tt.getMaterializedView().getCacheEvents().getEvent();
        for (EventType eventType : getSqlMaterializedView().getCacheEventTypes()) {
          Event event = new Event();
          event.setSifra(eventType.getSifra());
          event.setSifrant(eventType.getSifrant());
          event.setCacheOnUpdate(eventType.isCacheOnUpdate());
          events.add(event);
        }
      }
    }

    return tt;
  }

  public void setParameters(List<Object> queryParameters) {
    parameters.clear();
    parameters.addAll(queryParameters);
  }

  public TemporaryTableGroup getGroup() {
    return TemporaryTableGroup.getGroup(this);
  }

  public static class TemporaryTableGroup implements Iterable<TemporarySubselectSqlParameter> {

    private static final Map<TemporarySubselectSqlParameter, TemporaryTableGroup> groups = new HashMap<TemporarySubselectSqlParameter, TemporaryTableGroup>();
    private final ReentrantLock lock = new ReentrantLock();
    final Set<TemporarySubselectSqlParameter> ttp = new LinkedHashSet<TemporarySubselectSqlParameter>();

    private TemporaryTableGroup() {
    }

    public static TemporaryTableGroup getGroup(TemporarySubselectSqlParameter parameter) {
      TemporaryTableGroup result;
      if (groups.containsKey(parameter)) {
        result = groups.get(parameter);
      } else {
        result = new TemporaryTableGroup();
        groups.put(parameter, result);
      }

      result.ttp.add(parameter);

      return result;
    }

    public int size() {
      return ttp.size();
    }

    public boolean add(TemporarySubselectSqlParameter e) {
      if (!groups.containsKey(e)) {
        groups.put(e, this);
        return ttp.add(e);
      } else {
        return groups.containsKey(e);
      }
    }

    public boolean remove(TemporarySubselectSqlParameter e) {
      groups.remove(e);
      return ttp.remove(e);
    }

    @Override
    public Iterator<TemporarySubselectSqlParameter> iterator() {
      return ttp.iterator();
    }

    public void executeQuery(Connection connection, List<Object> queryParameters) throws SQLException, InterruptedException {
      if (size() > 1) {
        if (!(lock.tryLock() || lock.tryLock(1, TimeUnit.SECONDS))) {
          throw new SQLException("Can't lock temporary parameters group");
        }
        try {
          synchronized (connection) {
            TransactionManager tm = TransactionManager.getInstance(connection);

            boolean commit = false;
            tm.beginTransaction();
            try {
              execute(connection, queryParameters);
              commit = true;
            } catch (SQLException ex) {
              Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
              throw ex;
            } finally {
              tm.endTransaction(commit);
            }
          }
        } finally {
          lock.unlock();
        }
      } else {
        execute(connection, queryParameters);
      }
    }

    private void execute(Connection connection, List<Object> queryParameters) throws SQLException, InterruptedException {
      for (TemporarySubselectSqlParameter temporarySubselectSqlParameter : ttp) {
        temporarySubselectSqlParameter.executeQuery(connection, queryParameters);
      }
    }
  }
}
