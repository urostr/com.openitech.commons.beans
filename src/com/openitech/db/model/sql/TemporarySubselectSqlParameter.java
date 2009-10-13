/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author uros
 */
public class TemporarySubselectSqlParameter extends SubstSqlParameter {

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

  public void executeQuery(Connection connection) throws SQLException {
    Statement statement = connection.createStatement();
    try {
      boolean fill = !isFillOnceOnly();
      long timer = System.currentTimeMillis();
      
      try {
        if (checkTableSql != null) {
          statement.executeQuery(checkTableSql);
        }

        if (fill) {
          statement.executeUpdate(emptyTableSql);
        }
      } catch (SQLException ex) {
        for (String sql : createTableSqls) {
          statement.addBatch(sql);
        }
        statement.executeBatch();
        fill = true;
      }

      if (fill) {
        if (DbDataSource.DUMP_SQL) {
          System.out.println("##############");
          System.out.println(fillTableSql);
        }

        SQLDataSource.executeUpdate(fillTableSql, getParameters());
        if (DbDataSource.DUMP_SQL) {
          System.out.println("temporary:fill:" + (System.currentTimeMillis() - timer) + "ms");
          System.out.println("##############");
        }
      }
    } finally {
      statement.close();
    }
  }
}
