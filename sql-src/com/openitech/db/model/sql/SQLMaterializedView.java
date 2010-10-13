/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.connection.ConnectionManager;
import com.openitech.db.model.DbDataSource;
import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import com.openitech.util.Equals;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SQLMaterializedView extends SubstSqlParameter {

  public SQLMaterializedView(String tableName) {
    super();

    final ConnectionManager cm = ConnectionManager.getInstance();
    final String dl = cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_LEFT, "");
    final String dr = cm.getProperty(com.openitech.db.connection.DbConnection.DB_DELIMITER_RIGHT, "");

    if (dl.length() > 0 && dr.length() > 0) {
      this.replace = "(\\" + dl + tableName + "\\" + dr + '|' + tableName + ')';
    } else {
      this.replace = tableName;
    }
  }

  @Override
  public int getType() {
    return Types.SUBST_ALL;
  }
  protected String name;

  /**
   * Get the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name
   *
   * @param name new value of name
   */
  public void setName(String name) {
    this.name = name;
  }
  protected String isViewValidSQL;

  /**
   * Get the value of isViewValidSQL
   *
   * @return the value of isViewValidSQL
   */
  public String getIsViewValidSQL() {
    return isViewValidSQL;
  }

  /**
   * Set the value of isViewValidSQL
   *
   * @param isViewValidSQL new value of isViewValidSQL
   */
  public void setIsViewValidSQL(String isViewValidSQL) {
    this.isViewValidSQL = isViewValidSQL;
  }
  protected String setViewVersionSql;

  /**
   * Get the value of setViewVersionSql
   *
   * @return the value of setViewVersionSql
   */
  public String getSetViewVersionSql() {
    return setViewVersionSql;
  }

  /**
   * Set the value of setViewVersionSql
   *
   * @param setViewVersionSql new value of setViewVersionSql
   */
  public void setSetViewVersionSql(String setViewVersionSql) {
    this.setViewVersionSql = setViewVersionSql;
  }
  
  private Connection connection = null;
  private String qIsViewValid = null;
  private PreparedStatement isViewValid = null;
  private final ReentrantLock lock = new ReentrantLock();

  public boolean isViewValid(Connection connection, java.util.List<Object> parameters) {
    lock.lock();
    try {
      long timer = System.currentTimeMillis();
      if (!Equals.equals(this.connection, connection)) {
        this.qIsViewValid = SQLDataSource.substParameters(isViewValidSQL, parameters);
        isViewValid = connection.prepareStatement(this.qIsViewValid,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
        this.connection = connection;
      }

      if (DbDataSource.DUMP_SQL) {
        System.out.println("##############");
        System.out.println(this.qIsViewValid);
      }
      ResultSet executeQuery = SQLDataSource.executeQuery(isViewValid, parameters);
      if (DbDataSource.DUMP_SQL) {
        System.out.println("materialized:isvalid:" + getValue() + "..." + (System.currentTimeMillis() - timer) + "ms");
        System.out.println("##############");
      }
      if (executeQuery.next()) {
        return executeQuery.getBoolean(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      lock.unlock();
    }

    return false;
  }
}
