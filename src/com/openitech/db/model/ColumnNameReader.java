/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.db.model;

import java.util.Map;

/**
 *
 * @author domenbasic
 */
public interface ColumnNameReader {

  public abstract String getColumnName(String columnName, Map<String, Integer> columnMapping, Map<Integer, String> columnMappingIndex);
  public abstract <T> Class<? extends T> getColumnType(String columnName);
}
