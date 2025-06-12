package com.rookie.asset_management.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Interface for applying styles to Excel headers and data rows. This interface allows for
 * customization of the appearance of headers and data cells in an Excel workbook using decorators
 * pattern.
 */
public interface ExcelStyleDecorator {
  /**
   * Applies a style to the header row of an Excel sheet.
   *
   * @param workbook the workbook to which the header style will be applied
   * @param headerRow the row containing the headers
   * @param headers the array of header names to be styled
   */
  void applyStyle(Workbook workbook, Row headerRow, String[] headers);
}
