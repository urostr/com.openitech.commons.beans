/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

/**
 *
 * @author domenbasic
 */
public interface ColumnNameReader {

  public abstract String getColumnName(String columnName);

  public abstract String getColumnName(int columnIndex);
}
