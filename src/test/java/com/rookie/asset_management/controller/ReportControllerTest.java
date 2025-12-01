package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.service.ReportService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

  @Mock private ReportService reportService;

  @InjectMocks private ReportController reportController;

  @Nested
  @DisplayName("getAllReports (no pagination) Tests")
  class GetAllReportsNoPaginationTest {

    @Test
    @DisplayName("should return all reports successfully")
    void getAllReports_Success_ReturnsReports() {
      CategoryReportDtoResponse report =
          CategoryReportDtoResponse.builder()
              .category("Laptop")
              .total(10)
              .assigned(5)
              .available(3)
              .notAvailable(1)
              .waiting(1)
              .recycled(0)
              .build();

      when(reportService.getAllReports()).thenReturn(List.of(report));

      ResponseEntity<ApiDtoResponse<List<CategoryReportDtoResponse>>> response =
          reportController.getAllReports();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Reports retrieved successfully", response.getBody().getMessage());
      assertEquals(1, response.getBody().getData().size());
      assertEquals("Laptop", response.getBody().getData().get(0).getCategory());
      verify(reportService).getAllReports();
    }
  }

  @Nested
  @DisplayName("getAllReports (with pagination) Tests")
  class GetAllReportsWithPaginationTest {

    @Test
    @DisplayName("should return paginated reports successfully")
    void getAllReports_WithPagination_ReturnsPaginatedReports() {
      CategoryReportDtoResponse report =
          CategoryReportDtoResponse.builder()
              .category("Monitor")
              .total(20)
              .assigned(10)
              .available(8)
              .notAvailable(2)
              .waiting(0)
              .recycled(0)
              .build();

      PagingDtoResponse<CategoryReportDtoResponse> pagingResponse = new PagingDtoResponse<>();
      pagingResponse.setContent(List.of(report));

      when(reportService.getAllReports(0, 20, "category", "asc")).thenReturn(pagingResponse);

      ResponseEntity<ApiDtoResponse<PagingDtoResponse<CategoryReportDtoResponse>>> response =
          reportController.getAllReports(0, 20, "category", "asc");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Reports retrieved successfully", response.getBody().getMessage());
      assertNotNull(response.getBody().getData());
      verify(reportService).getAllReports(0, 20, "category", "asc");
    }
  }
}
