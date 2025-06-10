package com.rookie.asset_management.service.abstraction;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.ExcelStyleDecorator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

/**
 * Abstract class for exporting data to Excel format. This class provides methods to create rows,
 * sheets, and set data rows in an Excel workbook.
 *
 * @param <D> the type of data to be exported, typically a DTO or entity class
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractExcelExport<D> {

  /** Decorator for applying styles to Excel headers and data rows. */
  ExcelStyleDecorator decorator;

  /**
   * Sets the data rows in the provided sheet based on the list of reports.
   *
   * @param sheet the sheet where the data rows will be set
   * @param reports the list of reports to be included in the sheet
   */
  protected abstract void setData(Sheet sheet, List<D> reports);

  /**
   * Generates an Excel file with the specified sheet title, headers, and reports.
   *
   * @param sheetTitle the title of the sheet in the Excel file
   * @param headers the headers for the columns in the Excel sheet
   * @param reports the list of reports to be included in the Excel sheet
   * @param autoSizeColumns whether to automatically size the columns based on content (true) or not
   *     (false)
   * @param filterHeaders whether to enable filtering on the headers (true) or not (false)
   * @return a byte array representing the generated Excel file
   */
  protected byte[] generateExcelFile(
      String sheetTitle, String[] headers, List<D> reports, boolean autoSizeColumns, boolean filterHeaders) {
    try (Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      Sheet sheet = workbook.createSheet(sheetTitle);

      // Create header row
      Row headerRow = sheet.createRow(0); // Create the first row for headers

      for (int i = 0; i < headers.length; i++) {
        headerRow.createCell(i).setCellValue(headers[i]);
      }

      // Create header cells
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      setData(sheet, reports);

      if (decorator != null) {
        // Apply styles to the header row
        decorator.applyHeaderStyle(workbook, headerRow, headers);
      }

      if(filterHeaders) {
        // Enable filtering on the header row
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.length - 1));
      }

      if (autoSizeColumns) {
        sizeSheetColumns(sheet, headers.length);
      }

      workbook.write(outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new AppException(
          HttpStatus.INTERNAL_SERVER_ERROR, "IOException: error when generating Excel file");
    }
  }

  private void sizeSheetColumns(Sheet sheet, int numberOfColumns) {
    for (int i = 0; i < numberOfColumns; i++) {
      sheet.autoSizeColumn(i);
    }
  }
}
