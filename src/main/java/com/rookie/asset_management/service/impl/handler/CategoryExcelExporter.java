package com.rookie.asset_management.service.impl.handler;

import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.service.ExcelStyleDecorator;
import com.rookie.asset_management.service.ExportService;
import com.rookie.asset_management.service.abstraction.AbstractExcelExport;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

/**
 * Component for exporting category reports to Excel format. This class extends AbstractExcelExport
 * to provide specific implementation for exporting category report data. It implements
 * ExportService to define the export behavior. Uses {@link ExcelStyleDecorator} for styling the
 * Excel sheets. If no style is needed, the constructor can accept null.
 */
@Component
public class CategoryExcelExporter extends AbstractExcelExport<CategoryReportDtoResponse>
    implements ExportService<CategoryReportDtoResponse> {

  private static final String[] HEADERS = {
    "No.",
    "Category",
    "Total",
    "Assigned",
    "Available",
    "Not Available",
    "Waiting for recycling",
    "Recycled"
  };

  private static final String SHEET_NAME = "Category Report";

  public CategoryExcelExporter(ExcelStyleDecorator styleDecorator) {
    // if no need for decorator, pass null
    super(styleDecorator);
  }

  @Override
  protected void setData(Sheet sheet, List<CategoryReportDtoResponse> reports) {
    // Create data rows
    int rowIndex = 1; // Start from the second row (index 1) for data rows
    int serialNo = 1; // Serial number for the first column
    for (CategoryReportDtoResponse report : reports) {
      Row row = sheet.createRow(rowIndex++);
      row.createCell(0).setCellValue(serialNo++); // No. column
      row.createCell(1).setCellValue(report.getCategory());
      row.createCell(2).setCellValue(report.getTotal());
      row.createCell(3).setCellValue(report.getAssigned());
      row.createCell(4).setCellValue(report.getAvailable());
      row.createCell(5).setCellValue(report.getNotAvailable());
      row.createCell(6).setCellValue(report.getWaiting());
      row.createCell(7).setCellValue(report.getRecycled());
    }
  }

  @Override
  public boolean supports(String type, String format) {
    return "category".equalsIgnoreCase(type) && "excel".equalsIgnoreCase(format);
  }

  @Override
  public byte[] exportData(List<CategoryReportDtoResponse> data) {
    return generateExcelFile(SHEET_NAME, HEADERS, data, true, true);
  }
}
