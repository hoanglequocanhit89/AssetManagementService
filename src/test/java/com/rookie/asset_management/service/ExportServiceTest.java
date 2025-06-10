package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.impl.ExportServiceStrategy;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

  @Mock private ExportService<?> mockExportService;

  private ExportServiceStrategy exportServiceStrategy;

  @BeforeEach
  void setUp() {
    exportServiceStrategy = new ExportServiceStrategy(Collections.singletonList(mockExportService));
  }

  @Test
  void exportsEmptyDataSuccessfullyWhenServiceSupportsTypeAndFormat() {
    when(mockExportService.supports("category", "excel")).thenReturn(true);
    when(mockExportService.exportData(anyList())).thenReturn(new byte[] {});

    byte[] result = exportServiceStrategy.export("category", "excel", Collections.emptyList());

    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void throwsExceptionWhenServiceSupportsTypeButNotFormat() {
    when(mockExportService.supports("category", "csv")).thenReturn(false);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> exportServiceStrategy.export("category", "csv", List.of("data")));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("No export service found", exception.getMessage());
  }

  @Test
  void throwsExceptionWhenServiceSupportsFormatButNotType() {
    when(mockExportService.supports("user", "excel")).thenReturn(false);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> exportServiceStrategy.export("user", "excel", List.of("data")));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatusCode());
    assertEquals("No export service found", exception.getMessage());
  }
}
