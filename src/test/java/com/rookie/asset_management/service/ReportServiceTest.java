package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.response.report.ReportDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ReportServiceTest {
  @Mock
  CategoryRepository categoryRepository;

  @InjectMocks
  ReportServiceImpl reportService;

  // Add test methods here to test the ReportServiceImpl methods
  @Test
  @DisplayName("getAllReports should return a valid list of reports")
  void getAllReports_ShouldReturnValidList() {
    Category category = new Category();
    category.setName("Test Category");
    category.setId(1);
    category.setPrefix("TC");

    Asset asset1 = new Asset();
    asset1.setId(1);
    asset1.setStatus(AssetStatus.AVAILABLE);

    Asset asset2 = new Asset();
    asset2.setId(2);
    asset2.setStatus(AssetStatus.NOT_AVAILABLE);

    Asset asset3 = new Asset();
    asset3.setId(3);
    asset3.setStatus(AssetStatus.ASSIGNED);

    Asset asset4 = new Asset();
    asset4.setId(4);
    asset4.setStatus(AssetStatus.WAITING);

    Asset asset5 = new Asset();
    asset5.setId(5);
    asset5.setStatus(AssetStatus.RECYCLED);

    category.setAssets(List.of(asset1, asset2, asset3, asset4, asset5));

    // Mock the repository to return a list containing the test category
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    // Call the method under test
    List<ReportDtoResponse> reports = reportService.getAllReports();
    // Verify the results
    assertEquals(1, reports.size());
    assertEquals("Test Category", reports.getFirst().getCategory());
    assertEquals(5, reports.getFirst().getTotal());
    assertEquals(1, reports.getFirst().getAssigned());
    assertEquals(1, reports.getFirst().getAvailable());
    assertEquals(1, reports.getFirst().getNotAvailable());
    assertEquals(1, reports.getFirst().getWaiting());
    assertEquals(1, reports.getFirst().getRecycled());
  }

  @Test
  @DisplayName("getAllReports should handle empty categories gracefully")
  void getAllReports_ShouldHandleEmptyCategories() {
    // Mock the repository to return an empty list
    when(categoryRepository.findAll()).thenReturn(List.of());

    // Call the method under test
    List<ReportDtoResponse> reports = reportService.getAllReports();

    assertEquals(0, reports.size());

    // verify that the repository was called
    verify(categoryRepository).findAll();
  }

  @Test
  @DisplayName("getAllReports should return reports with correct totals")
  void getAllReports_ShouldReturnReportsWithCorrectTotals() {
    Category category = new Category();
    category.setName("Test Category");
    category.setId(1);
    category.setPrefix("TC");
    category.setAssets(List.of()); // No assets in this category

    // Mock the repository to return a list containing the test category
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    // Call the method under test
    List<ReportDtoResponse> reports = reportService.getAllReports();

    // Verify the results
    assertEquals(1, reports.size());
    ReportDtoResponse report = reports.get(0);
    assertEquals("Test Category", report.getCategory());
    assertEquals(0, report.getTotal());
    assertEquals(0, report.getAssigned());
    assertEquals(0, report.getAvailable());
    assertEquals(0, report.getNotAvailable());
    assertEquals(0, report.getWaiting());
    assertEquals(0, report.getRecycled());
  }
}
