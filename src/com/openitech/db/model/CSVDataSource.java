/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.8 $
 */
package com.openitech.db.model;

import au.com.bytecode.opencsv.CSVReader;
import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.importer.DataColumn;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uros
 */
public class CSVDataSource extends FileDataSource {

  public CSVDataSource(DbDataSource owner) {
    super(owner);
  }

  protected char separator = ';';

  /**
   * Get the value of separator
   *
   * @return the value of separator
   */
  public char getSeparator() {
    return separator;
  }

  /**
   * Set the value of separator
   *
   * @param separator new value of separator
   */
  public void setSeparator(char separator) {
    this.separator = separator;
  }

  @Override
  public boolean loadData(boolean reload, int oldRow) {
    boolean result = false;

    if (isDataLoaded) {
      return true;
    }
    if (sourceFile != null) {
      try {
        CSVReader reader = new CSVReader(new FileReader(sourceFile), separator);
        List<String[]> readAll = reader.readAll();

        boolean isFirstLineHeader = true;

        count = readAll.size() - (isFirstLineHeader ? 1 : 0);

        rowValues.clear();
        
        for (int i = 0; i < readAll.size(); i++) {
          String[] columns = readAll.get(i);

          if (isFirstLineHeader && i == 0) {
            populateHeaders(columns);
            continue;
          }
          Map<String, DataColumn> values;
          if (rowValues.containsKey(i)) {
            values = rowValues.get(i);
          } else {
            values = new HashMap<String, DataColumn>();
            rowValues.put(i, values);
          }

          for (int j = 0; j < columns.length; j++) {
            String value = columns[j];
            values.put(getColumnName(j + 1).toUpperCase(), new DataColumn(value));
          }
        }
        isDataLoaded = true;
        
        //se postavim na staro vrstico ali 1
        if (oldRow > 0) {
          absolute(oldRow);
        } else {
          first();
        }

        result = true;
      } catch (Exception ex) {
        Logger.getLogger(CSVDataSource.class.getName()).log(Level.SEVERE, null, ex);
        result = false;
      }
    }


    return result;
  }

  private void populateHeaders(String[] headers) {
    for (int i = 0; i < headers.length; i++) {
      String header = headers[i];
      columnMapping.put(header, i + 1);
      columnMappingIndex.put(i + 1, header);
    }
    columnCount = headers.length;
  }

  @Override
  protected <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException {

    columnName = columnName.toUpperCase();
    if(columnReader != null){
      String sourceColumnName = columnReader.getColumnName(columnName, columnMapping, columnMappingIndex);
      Class sourceType = columnReader.getColumnType(columnName);
      if(sourceColumnName != null){
        columnName = sourceColumnName.toUpperCase();
      }
      
      if (sourceType != null) {
        type = sourceType;
      }
    }
    Object result = nullValue;
    Integer r = new Integer(row);

    Map<String, DataColumn> values = rowValues.get(r);
    if (values != null) {
      DataColumn csvValue = values.get(columnName);
      if (csvValue == null) {
        result = nullValue;
        wasNull = true;
      } else {
        try {
          result = csvValue.getValue(type);
          wasNull = csvValue.wasNull();
        } catch (ParseException ex) {
          Logger.getLogger(CSVDataSource.class.getName()).log(Level.SEVERE, null, ex);
          result = nullValue;
          wasNull = true;
        }
      }

      if (result instanceof java.util.Date) {
        java.util.Date value = ((java.util.Date) result);
        if (value != null) {
          if (Time.class.isAssignableFrom(type)) {
            result = new java.sql.Time(value.getTime());
          } else if (Timestamp.class.isAssignableFrom(type)) {
            result = new java.sql.Timestamp(value.getTime());
          } else if (java.sql.Date.class.isAssignableFrom(type)) {
            result = new java.sql.Date(value.getTime());
          } else if (!Object.class.isAssignableFrom(type)) {
            result = new java.sql.Date(value.getTime());
          }
        }
      }
    }


    return result == null ? nullValue : (T) result;

  }

  @Override
  public DbDataSourceImpl copy(DbDataSource owner) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void close() throws SQLException {
    super.close();

  }

  @Override
  public int getType(String columnName) throws SQLException {
    //TODO
    return java.sql.Types.VARCHAR;
    /*
    if (getMetaData() != null) {
    return getMetaData().getColumnType(columnMapping.checkedGet(columnName));
    } else {
    throw new SQLException("Ni pripravljenih podatkov.");
    }
     *
     */
  }

  @Override
  public void destroy() {
  }
}
