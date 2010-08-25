/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import com.openitech.sql.util.SqlUtilities;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 * @author uros
 */
public class TemporarySubselectSqlParameter extends SubstSqlParameter {

  private static final Semaphore lock = new Semaphore(1);

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
  private String emptyTableSql;

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
    this.emptyTableSql = emptyTableSql;
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

  public void executeQuery(Connection connection, List<Object> parameters) throws SQLException {
    Statement statement = connection.createStatement();
    try {
      boolean fill = !isFillOnceOnly();
      long timer = System.currentTimeMillis();

      String DB_USER = ConnectionManager.getInstance().getProperty(ConnectionManager.DB_USER, "");
      final List<Object> qparams = new ArrayList<Object>(parameters.size());
      qparams.addAll(parameters);
      qparams.addAll(getParameters());
      if (sqlMaterializedView != null) {
        qparams.add(sqlMaterializedView);
      }

      boolean transaction = false;
      boolean commit = false;
      try {
        try {
          if (checkTableSql != null) {
            statement.executeQuery(SQLDataSource.substParameters(checkTableSql, qparams));
          }
        } catch (SQLException ex) {
          if (sqlMaterializedView != null) {
            if (!SqlUtilities.getInstance().isTransaction()) {
              transaction = true;
              SqlUtilities.getInstance().beginTransaction();
            }
          }
          lock.acquireUninterruptibly();
          try {
            for (String sql : createTableSqls) {
              statement.addBatch(SQLDataSource.substParameters(sql.replaceAll("<%TS%>", "_" + DB_USER + Long.toString(System.currentTimeMillis())), qparams));
            }
            statement.executeBatch();
            fill = true;
          } finally {
            lock.release();
          }
        }

        if ((!fill) && (sqlMaterializedView != null)) {
          fill = !sqlMaterializedView.isViewValid(connection);
        }

        if (fill && !disabled) {
          if (sqlMaterializedView != null) {
            if (!SqlUtilities.getInstance().isTransaction()) {
              transaction = true;
              SqlUtilities.getInstance().beginTransaction();
              lock.acquireUninterruptibly();
            }
          }


          if (emptyTableSql.length() > 0) {
            try {
              final String sql = SQLDataSource.substParameters(emptyTableSql, qparams);
              if (DbDataSource.DUMP_SQL) {
                System.out.println("##############");
                System.out.println(sql);
              }
              statement.executeUpdate(sql);
            } finally {
              if (DbDataSource.DUMP_SQL) {
                System.out.println("##############");
              }
            }
          }

          SQLDataSource.executeUpdate(fillTableSql, qparams);
          if (cleanTableSqls != null) {
            for (String sql : cleanTableSqls) {
              SQLDataSource.executeUpdate(sql, qparams);
            }
          }
          if ((sqlMaterializedView != null) && (sqlMaterializedView.setViewVersionSql != null)) {
            SQLDataSource.execute(sqlMaterializedView.setViewVersionSql, qparams);
          }
          if (DbDataSource.DUMP_SQL) {
            System.out.println("temporary:fill:" + (System.currentTimeMillis() - timer) + "ms");
            System.out.println("##############");
          }

        }
        commit = true;
      } finally {
        if (transaction) {
          SqlUtilities.getInstance().endTransaction(commit);
          lock.release();
        }
      }
    } finally {
      statement.close();
    }
  }

  public void setParameters(List<Object> queryParameters) {
    parameters.clear();
    parameters.addAll(queryParameters);
  }
}
