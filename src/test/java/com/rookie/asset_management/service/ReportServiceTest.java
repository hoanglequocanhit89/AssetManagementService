package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.impl.ReportServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ReportServiceTest {
  @Mock CategoryRepository categoryRepository;

  @InjectMocks ReportServiceImpl reportService;

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
    List<CategoryReportDtoResponse> reports = reportService.getAllReports();
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
    List<CategoryReportDtoResponse> reports = reportService.getAllReports();

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
    List<CategoryReportDtoResponse> reports = reportService.getAllReports();

    // Verify the results
    assertEquals(1, reports.size());
    CategoryReportDtoResponse report = reports.get(0);
    assertEquals("Test Category", report.getCategory());
    assertEquals(0, report.getTotal());
    assertEquals(0, report.getAssigned());
    assertEquals(0, report.getAvailable());
    assertEquals(0, report.getNotAvailable());
    assertEquals(0, report.getWaiting());
    assertEquals(0, report.getRecycled());
  }

  @Test
  @DisplayName("getAllReports with paging should return paginated reports")
  void getAllReports_WithPaging_ShouldReturnPaginatedReports() {
    // Create test categories with assets
    Category category1 = createCategoryWithAssets("Category 1", 5, 3, 2);
    Category category2 = createCategoryWithAssets("Category 2", 3, 2, 1);

    Page<Category> page = new PageImpl<>(List.of(category1, category2), PageRequest.of(0, 10), 2);

    when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    // Call method under test
    PagingDtoResponse<CategoryReportDtoResponse> response =
        reportService.getAllReports(0, 10, "total", "asc");

    // Verify results
    assertEquals(2, response.getContent().size());
    assertEquals(1, response.getPage()); // 0-based index converted to 1-based
    assertEquals(10, response.getSize());
    assertEquals(1, response.getTotalPages());
    assertEquals(2, response.getTotalElements());
    assertFalse(response.getEmpty());

    assertEquals("Category 1", response.getContent().stream().toList().get(0).getCategory());
    assertEquals(5, response.getContent().stream().toList().get(0).getTotal());
    assertEquals("Category 2", response.getContent().stream().toList().get(1).getCategory());
    assertEquals(3, response.getContent().stream().toList().get(1).getTotal());
  }

  @Test
  @DisplayName("getAllReports with paging should handle empty results")
  void getAllReports_WithPaging_ShouldHandleEmptyResults() {
    Page<Category> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

    when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    PagingDtoResponse<CategoryReportDtoResponse> response =
        reportService.getAllReports(0, 10, "total", "asc");

    assertEquals(0, response.getContent().size());
    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(0, response.getTotalPages());
    assertEquals(0, response.getTotalElements());
    assertTrue(response.getEmpty());
  }

  @Test
  @DisplayName("getAllReports with paging should sort by specified field descending")
  void getAllReports_WithPaging_ShouldSortBySpecifiedFieldDescending() {
    Category category1 = createCategoryWithAssets("Category 1", 5, 2, 3);
    Category category2 = createCategoryWithAssets("Category 2", 3, 1, 2);

    Page<Category> page = new PageImpl<>(List.of(category1, category2), PageRequest.of(0, 10), 2);

    when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    reportService.getAllReports(0, 10, "assigned", "desc");

    verify(categoryRepository).findAll(any(Specification.class), eq(PageRequest.of(0, 10)));
  }

  @Test
  @DisplayName("getAllReports with paging should handle null sort parameters")
  void getAllReports_WithPaging_ShouldHandleNullSortParameters() {
    Category category = createCategoryWithAssets("Test Category", 3, 1, 2);

    Page<Category> page = new PageImpl<>(List.of(category), PageRequest.of(0, 10), 1);

    when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    PagingDtoResponse<CategoryReportDtoResponse> response =
        reportService.getAllReports(0, 10, null, "asc");

    assertEquals(1, response.getContent().size());
    assertEquals("Test Category", response.getContent().stream().toList().get(0).getCategory());
  }

  // Helper method to create test data
  private Category createCategoryWithAssets(String name, int total, int available, int assigned) {
    Category category = new Category();
    category.setName(name);
    category.setId(1);
    category.setPrefix("TC");

    List<Asset> assets = new ArrayList<>();
    // Add available assets
    for (int i = 0; i < available; i++) {
      Asset asset = new Asset();
      asset.setId(i);
      asset.setStatus(AssetStatus.AVAILABLE);
      assets.add(asset);
    }

    // Add assigned assets
    for (int i = 0; i < assigned; i++) {
      Asset asset = new Asset();
      asset.setId(available + i);
      asset.setStatus(AssetStatus.ASSIGNED);
      assets.add(asset);
    }

    // Add other assets to reach the total count
    int remaining = total - available - assigned;
    for (int i = 0; i < remaining; i++) {
      Asset asset = new Asset();
      asset.setId(available + assigned + i);
      asset.setStatus(AssetStatus.WAITING);
      assets.add(asset);
    }

    category.setAssets(assets);
    return category;
  }
}
