/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

import com.openitech.components.JWProgressMonitor;
import com.openitech.db.model.DbTableModel;
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
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

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
    xls_header_cell_style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
    xls_header_cell_style.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());
    //xls_header_cell_style.setBorderBottom(HSSFCellStyle.BORDER_DOUBLE);

    HSSFDataFormat xls_data_format = xls_workbook.createDataFormat();

    HSSFCellStyle xls_date_cell_style = xls_workbook.createCellStyle();
    xls_date_cell_style.setDataFormat(xls_data_format.getFormat("d.m.yyyy"));

    HSSFCellStyle xls_double_cell_style = xls_workbook.createCellStyle();
    xls_double_cell_style.setDataFormat(xls_data_format.getFormat("#,##0.00"));

    while (columns.hasMoreElements()) {
      TableColumn column = columns.nextElement();

      HSSFCell xls_cell = xls_row.createCell(cell++);
      xls_cell.setCellValue(new HSSFRichTextString(column.getHeaderValue().toString()));
      xls_cell.setCellStyle(xls_header_cell_style);
    }

    TableModel tableModel = source.getModel();

    short row = 1;

    JWProgressMonitor progress = new JWProgressMonitor((java.awt.Frame) null);

    progress.setTitle("Izvoz podatkov v Excel");
    progress.setMax(tableModel.getRowCount());

    progress.setVisible(true);

    try {
      while (row <= tableModel.getRowCount()) {
        xls_row = xls_sheet.createRow(row);
        cell = 0;

        HSSFCell xls_cell = xls_row.createCell(cell++);
        //xls_cell.setCellValue(new HSSFRichTextString(row + "/" + tableModel.getRowCount()));

        while (cell <= columnModel.getColumnCount()) {
          Object value = tableModel.getValueAt(row - 1, cell - 1);
          if (value != null) {
            if (value instanceof DbTableModel.ColumnDescriptor.ValueMethod) {
              DbTableModel.ColumnDescriptor.ValueMethod vm = (DbTableModel.ColumnDescriptor.ValueMethod) value;

              if (vm.getColumnNames().size() == 1) {
                java.util.List<Object> values = vm.getValues();

                Object vm_value = values.get(0);

                if (vm_value != null) {
                  xls_cell = xls_row.createCell(cell);
                  if (vm_value instanceof java.util.Date) {
                    xls_cell.setCellValue((java.util.Date) vm_value);
                    xls_cell.setCellStyle(xls_date_cell_style);
                  } else if (vm_value instanceof java.lang.Number) {
                    xls_cell.setCellValue(((java.lang.Number) vm_value).doubleValue());
                    if ((vm_value instanceof java.math.BigDecimal) ||
                            (vm_value instanceof java.lang.Double) ||
                            (vm_value instanceof java.lang.Float)) {
                      xls_cell.setCellStyle(xls_double_cell_style);
                    }
                  } else if (vm_value instanceof java.lang.Boolean) {
                    xls_cell.setCellValue(((java.lang.Boolean) vm_value).booleanValue());
                  } else {
                    xls_cell.setCellValue(new HSSFRichTextString(value.toString()));
                  }
                }
              } else {
                xls_cell = xls_row.createCell(cell);
                xls_cell.setCellValue(new HSSFRichTextString(value.toString()));
              }
            } else {
              xls_cell = xls_row.createCell(cell);
              xls_cell.setCellValue(new HSSFRichTextString(value.toString()));
            }
          }
          cell++;
        }

        row++;
        progress.next();
      }

      for (cell=0; cell<=columnModel.getColumnCount(); cell++)
        xls_sheet.autoSizeColumn(cell);

      xls_sheet.createFreezePane(1, 1);
    } finally {
      progress.setVisible(false);
    }


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