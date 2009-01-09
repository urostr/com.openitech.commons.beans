/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author uros
 */
public class HSSFWrapper {

  private static String excel = System.getProperty("excel.bin", "");

  private HSSFWrapper() {
  }

  public static final HSSFWorkbook getWorkbook(JTable source) {
    HSSFWorkbook xls_workbook = new HSSFWorkbook();
    HSSFSheet xls_sheet = xls_workbook.createSheet("Pregled podatkov");


    TableColumnModel columnModel = source.getColumnModel();
    Enumeration<TableColumn> columns = columnModel.getColumns();

    HSSFRow xls_row = xls_sheet.createRow(0);
    short cell = 1;
    HSSFCellStyle xls_header_cell_style = xls_workbook.createCellStyle();
    HSSFFont xls_header_font = xls_workbook.createFont();

    xls_header_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

    xls_header_cell_style.setFont(xls_header_font);
    xls_header_cell_style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    xls_header_cell_style.setBorderBottom(HSSFCellStyle.BORDER_DOUBLE);

    while (columns.hasMoreElements()) {
      TableColumn column = columns.nextElement();

      HSSFCell xls_cell = xls_row.createCell(cell++);
      xls_cell.setCellValue(new HSSFRichTextString(column.getHeaderValue().toString()));
      xls_cell.setCellStyle(xls_header_cell_style);
    }

    TableModel tableModel = source.getModel();

    short row = 1;

    while (row <= tableModel.getRowCount()) {
      xls_row = xls_sheet.createRow(row);
      cell = 0;

      HSSFCell xls_cell = xls_row.createCell(cell++);
      xls_cell.setCellValue(new HSSFRichTextString(row + "/" + tableModel.getRowCount()));

      while (cell<=columnModel.getColumnCount()) {
        Object value = tableModel.getValueAt(row - 1, cell - 1);
        if (value!=null) {
           xls_cell = xls_row.createCell(cell++);
           xls_cell.setCellValue(new HSSFRichTextString(value.toString()));
        } else {
          cell++;
        }
      }

      row++;
    }

    xls_sheet.createFreezePane(1, 1);


    return xls_workbook;
  }

  public static final void openWorkbook(JTable table) {
    try {
      openWorkbook(getWorkbook(table));
    } catch (IllegalAccessException ex) {
      Logger.getLogger(HSSFWrapper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalArgumentException ex) {
      Logger.getLogger(HSSFWrapper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(HSSFWrapper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(HSSFWrapper.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static final void openWorkbook(HSSFWorkbook workbook) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException, IOException {
    String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Calendar.getInstance().getTimeInMillis() + "_export.xls";

    java.io.File file = new java.io.File(filename);
    if (!System.getProperty("os.name").equals("Linux")) {
      file.deleteOnExit();
    }

    java.io.FileOutputStream out = new java.io.FileOutputStream(file);
    workbook.write(out);
    out.close();



    if (file.exists()) {
      if (Desktop.isFileOpenSupported() && (excel.length() == 0)) {
        Desktop.open(file);
      } else {
        Runtime.getRuntime().exec(new String[]{excel, file.getAbsolutePath()});
      }
    }
  }
}
