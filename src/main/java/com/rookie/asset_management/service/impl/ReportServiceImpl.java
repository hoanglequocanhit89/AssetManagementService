package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.mapper.PagingMapper;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.ReportService;
import com.rookie.asset_management.service.abstraction.PagingServiceImpl;
import com.rookie.asset_management.service.specification.ReportSpecification;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl
    extends PagingServiceImpl<CategoryReportDtoResponse, Category, Integer>
    implements ReportService {
  CategoryRepository categoryRepository;

  @Autowired
  public ReportServiceImpl(CategoryRepository categoryRepository) {
    // Initialize the PagingMapper with a no-op implementation
    // using anonymous inner class
    super(
        new PagingMapper<>() {
          @Override
          public CategoryReportDtoResponse toDto(Category entity) {
            return null;
          }

          @Override
          public Category toEntity(CategoryReportDtoResponse dto) {
            return null;
          }
        },
        categoryRepository);
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<CategoryReportDtoResponse> getAllReports() {
    // Retrieve all categories from the repository
    List<Category> categories = categoryRepository.findAll();
    return categories.stream()
        .map(this::getReport)
        .toList(); // Convert each category to a CategoryReportDtoResponse
  }

  @Override
  public PagingDtoResponse<CategoryReportDtoResponse> getAllReports(
      int page, int size, String sortBy, String sortDir) {
    Specification<Category> spec = ReportSpecification.getSortedByAssetsCount(sortBy, sortDir);
    Pageable pageable = PageRequest.of(page, size);
    return getMany(spec, pageable, this::getReport);
  }

  // mapping from Category to CategoryReportDtoResponse
  private CategoryReportDtoResponse getReport(Category category) {
    String categoryName = category.getName();
    List<Asset> assets = category.getAssets();

    // If there are no assets, return a report with zero counts
    if (assets == null || assets.isEmpty()) {
      return CategoryReportDtoResponse.builder()
          .category(categoryName)
          .total(0)
          .assigned(0)
          .available(0)
          .notAvailable(0)
          .waiting(0)
          .recycled(0)
          .build();
    }

    // Use a map for counting
    Map<AssetStatus, Integer> statusCounts = new EnumMap<>(AssetStatus.class);

    // Initialize all statuses with zero
    for (AssetStatus status : AssetStatus.values()) {
      statusCounts.put(status, 0);
    }

    // Count in a single pass
    for (Asset asset : assets) {
      statusCounts.merge(asset.getStatus(), 1, Integer::sum);
    }

    return CategoryReportDtoResponse.builder()
        .category(categoryName)
        .total(assets.size())
        .assigned(statusCounts.get(AssetStatus.ASSIGNED))
        .available(statusCounts.get(AssetStatus.AVAILABLE))
        .notAvailable(statusCounts.get(AssetStatus.NOT_AVAILABLE))
        .waiting(statusCounts.get(AssetStatus.WAITING))
        .recycled(statusCounts.get(AssetStatus.RECYCLED))
        .build();
  }
}
