package com.rookie.asset_management.service.impl.handler;

import com.rookie.asset_management.service.ExcelStyleDecorator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ExcelStyleDecorator} that applies basic styles to the header and
 * data rows in an Excel workbook.
 */
@Component
public class DefaultExcelStyleDecorator implements ExcelStyleDecorator {

  @Override
  public void applyHeaderStyle(Workbook workbook, Row headerRow, String[] headers) {
    CellStyle headerStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 12);
    headerStyle.setFont(font);

    for (int i = 0; i < headers.length; i++) {
      Cell cell = headerRow.getCell(i);
      cell.setCellValue(headers[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  @Override
  public void applyDataStyle(Workbook workbook, Row dataRow, int columnIndex, Object value) {
    // Optional: Add logic to apply cell style for data if needed
    // For now, we just set the value without specific styling
    Cell cell = dataRow.createCell(columnIndex);
    switch (value) {
      case String string -> cell.setCellValue(string);
      case Number number -> cell.setCellValue(number.doubleValue());
      case Boolean b -> cell.setCellValue(b);
      default -> cell.setCellValue(value.toString());
    }
  }
}
