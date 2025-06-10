package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.impl.handler.CategoryExcelExporter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CategoryExcelExporterTest {

  @Mock private CategoryReportDtoResponse mockReport;

  @Mock private ExcelStyleDecorator mockStyleDecorator;

  private CategoryExcelExporter categoryExcelExporter;

  @BeforeEach
  void setUp() {
    categoryExcelExporter = new CategoryExcelExporter(mockStyleDecorator);
  }

  @Test
  void exportsDataSuccessfullyWithValidInput() {
    when(mockReport.getCategory()).thenReturn("Electronics");
    when(mockReport.getTotal()).thenReturn(100);
    when(mockReport.getAssigned()).thenReturn(50);
    when(mockReport.getAvailable()).thenReturn(30);
    when(mockReport.getNotAvailable()).thenReturn(10);
    when(mockReport.getWaiting()).thenReturn(5);
    when(mockReport.getRecycled()).thenReturn(5);

    byte[] result = categoryExcelExporter.exportData(List.of(mockReport));

    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  @Test
  void exportsEmptyDataSuccessfully() {
    byte[] result = categoryExcelExporter.exportData(Collections.emptyList());

    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  @Test
  void throwsExceptionWhenExportFailsDueToIOException() {
    CategoryExcelExporter exporter = spy(categoryExcelExporter);
    doThrow(
            new AppException(
                HttpStatus.INTERNAL_SERVER_ERROR, "IOException: error when generating Excel file"))
        .when(exporter)
        .exportData(anyList());

    AppException exception =
        assertThrows(AppException.class, () -> exporter.exportData(List.of(mockReport)));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatusCode());
    assertEquals("IOException: error when generating Excel file", exception.getMessage());
  }

  @Test
  void supportsReturnsTrueForValidTypeAndFormat() {
    boolean result = categoryExcelExporter.supports("category", "excel");

    assertTrue(result);
  }

  @Test
  void supportsReturnsFalseForInvalidType() {
    boolean result = categoryExcelExporter.supports("user", "excel");

    assertFalse(result);
  }

  @Test
  void supportsReturnsFalseForInvalidFormat() {
    boolean result = categoryExcelExporter.supports("category", "csv");

    assertFalse(result);
  }
}
