/*
 * ActiveRowChangeEvent.java
 *
 * Created on April 2, 2006, 12:12 PM
 *
 * $Revision $
 */

package com.openitech.db.events;

import com.openitech.db.model.DbDataSource;
import java.util.Collections;
import java.util.EventObject;
import java.util.Map;

/**
 *
 * @author uros
 */
public class StoreUpdatesEvent extends EventObject {
  public static final int ROW_CHANGED = 0;
  public static final int FIELD_CHANGED = 1;
  private int row = -1;
  private DbDataSource source = null;
  private int hash = 17;
  
  /** Creates a new instance of ActiveRowChangeEvent */
  public StoreUpdatesEvent(DbDataSource source, int row, boolean insert, Map<String,Object> columnValues) {
    super(source);
    this.row = row;
    this.insert = insert;
    this.columnValues = Collections.unmodifiableMap(columnValues);
    this.hash = 37*source.hashCode();
  }
    
  public int getRow() {
    return row;
  }
  
  public DbDataSource getSource() {
    return (DbDataSource) source;
  }

  public int hashCode() {
    return this.hash;
  }

  /**
   * Holds value of property insert.
   */
  private boolean insert;

  /**
   * Getter for property insert.
   * @return Value of property insert.
   */
  public boolean isInsert() {
    return this.insert;
  }

  /**
   * Holds value of property columnValues.
   */
  private Map<String,Object> columnValues;

  /**
   * Getter for property columnValues.
   * @return Value of property columnValues.
   */
  public Map<String,Object> getColumnValues() {
    return this.columnValues;
  }
}
