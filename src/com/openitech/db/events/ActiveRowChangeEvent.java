/*
 * ActiveRowChangeEvent.java
 *
 * Created on April 2, 2006, 12:12 PM
 *
 * $Revision $
 */

package com.openitech.db.events;

import com.openitech.db.model.DbDataSource;

/**
 *
 * @author uros
 */
public class ActiveRowChangeEvent {
  public static final int ROW_CHANGED = 0;
  public static final int FIELD_CHANGED = 1;
  
  private int type;
  private int newRowNumber = -1;
  private int oldRowNumber = -1;
  private String columnName = null;
  private int columnIndex = -1;
  private DbDataSource source = null;
  private int hash = 17;
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public ActiveRowChangeEvent(DbDataSource source, int newRowNumber, int oldRowNumber) {
    type = ROW_CHANGED;
    this.source = source;
    this.newRowNumber = newRowNumber;
    this.oldRowNumber = oldRowNumber;
    this.hash = 37*source.hashCode();
  }
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public ActiveRowChangeEvent(DbDataSource source, String columnName, int columnIndex) {
    type = FIELD_CHANGED;
    this.source = source;
    this.columnName = columnName;
    this.columnIndex = columnIndex;
    this.hash = 17+37*source.hashCode();
  }
  
  public int getType() {
    return this.type;
  }
  
  public int getOldRowNumber() {
    return oldRowNumber;
  }
  
  public int getNewRowNumber() {
    return newRowNumber;
  }
  
  public String getColumnName() {
    return columnName;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }
  
  public DbDataSource getSource() {
    return source;
  }

  public int hashCode() {
    return this.hash;
  }
}
