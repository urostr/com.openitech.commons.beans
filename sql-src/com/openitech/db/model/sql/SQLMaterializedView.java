/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model.sql;

import com.openitech.db.model.DbDataSource.SubstSqlParameter;
import com.openitech.db.model.Types;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class SQLMaterializedView  extends SubstSqlParameter {

  public SQLMaterializedView(String replace) {
    super(replace);
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


  public boolean isViewValid(Connection connection) {
    try {
      ResultSet executeQuery = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY).executeQuery(isViewValidSQL);
      if (executeQuery.next()) {
        return executeQuery.getBoolean(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(SQLMaterializedView.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }
}
