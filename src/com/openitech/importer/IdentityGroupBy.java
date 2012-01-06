/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.importer;

/**
 *
 * @author domenbasic
 */
public class IdentityGroupBy {

  private String columnName;
  private String groupByColumnName;

  public IdentityGroupBy(String columnName, String groupByColumnName) {
    this.columnName = columnName;
    this.groupByColumnName = groupByColumnName;
  }

  public String getColumnName() {
    return columnName;
  }

  public String getGroupByColumnName() {
    return groupByColumnName;
  }
}
