/*
 * DbDataSource.java
 *
 * Created on April 2, 2006, 11:59 AM
 *
 * $Revision: 1.8 $
 */
package com.openitech.db.model;

import com.openitech.db.model.DbDataSourceFactory.DbDataSourceImpl;
import com.openitech.importer.DataColumn;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
*/

/**
 *
 * @author uros
 */
public class ExcelDataSource extends FileDataSource {

  public ExcelDataSource(DbDataSource owner) {
    super(owner);
  }

  @Override
  public boolean loadData(boolean reload, int oldRow) {
    boolean result = false;

   /* if (isDataLoaded && !reload) {
      return false;
    }
    if (sourceFile != null) {
      try {
        Workbook workBook = org.apache.poi.ss.usermodel.WorkbookFactory.create(new FileInputStream(sourceFile));
        //        HSSFWorkbook workBook = new HSSFWorkbook(new FileInputStream(sourceFile));
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();

        boolean isFirstLineHeader = true;

        //count = sheet. - (isFirstLineHeader ? 1 : 0);
        int tempCount = 0;
        while (rows.hasNext()) {
          tempCount++;
          Row row = rows.next();
          // display row number in the console.
          System.out.println("Row No.: " + row.getRowNum());
          if (isFirstLineHeader && tempCount == 1) {
            populateHeaders(row);
            continue;
          }

          Map<String, DataColumn> values;
          if (rowValues.containsKey(row.getRowNum())) {
            values = rowValues.get(row.getRowNum());
          } else {
            values = new HashMap<String, DataColumn>();
            rowValues.put(row.getRowNum(), values);
          }

          // once get a row its time to iterate through cells.
          Iterator<Cell> cells = row.cellIterator();
          int j = 0;
          while (cells.hasNext()) {
            DataColumn dataColumn = new DataColumn();
            Cell cell = cells.next();
            System.out.println("Cell No.: " + cell.getColumnIndex());

            switch (cell.getCellType()) {
              case HSSFCell.CELL_TYPE_NUMERIC: {
                // cell type numeric.
                System.out.println("Numeric value: " + cell.getNumericCellValue());
                dataColumn.setValue(cell.getNumericCellValue(), Double.class);
                break;
              }
              case HSSFCell.CELL_TYPE_STRING:
                // cell type string.
                System.out.println("String value: " + cell.getStringCellValue());
                dataColumn.setValue(cell.getStringCellValue(), String.class);
                break;
              case HSSFCell.CELL_TYPE_BOOLEAN:
                // cell type string.
                System.out.println("String value: " + cell.getBooleanCellValue());
                dataColumn.setValue(cell.getBooleanCellValue(), Boolean.class);
                break;
              default:
                dataColumn.setValue(cell.getStringCellValue(), String.class);
                break;
            }

            values.put(getColumnName(cell.getColumnIndex()).toUpperCase(), dataColumn);

          }
        }
        count = tempCount;
        
        isDataLoaded = true;
        //se postavim na staro vrstico ali 1
        if (oldRow > 0) {
          absolute(oldRow);
        } else {
          first();
        }

        result = true;
      } catch (Exception ex) {
        Logger.getLogger(ExcelDataSource.class.getName()).log(Level.SEVERE, null, ex);
        result = false;
      }
    }
*/

    return result;
  }
/*
  private void populateHeaders(Row row) {
    Iterator<Cell> cells = row.cellIterator();
    while (cells.hasNext()) {
      Cell cell = cells.next();

      System.out.println("String value: " + cell.getStringCellValue());

      String header = cell.getStringCellValue();
      columnMapping.put(header, cell.getColumnIndex());
      columnMappingIndex.put(cell.getColumnIndex(), header);
    }
  }
*/
  @Override
  protected <T> T getStoredValue(int row, String columnName, T nullValue, Class<? extends T> type) throws SQLException {

    columnName = columnName.toUpperCase();
    Object result = nullValue;
    Integer r = new Integer(row);

    Map<String, DataColumn> values = rowValues.get(r);
    if (values != null) {
      DataColumn dataCoulmn = values.get(columnName);
      if (dataCoulmn == null) {
        result = nullValue;
        wasNull = true;
      } else {
        try {
          result = dataCoulmn.getValue(type);
          wasNull = dataCoulmn.wasNull();
        } catch (ParseException ex) {
          Logger.getLogger(ExcelDataSource.class.getName()).log(Level.SEVERE, null, ex);
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
