package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.report.ReportDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.ReportService;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {
  CategoryRepository categoryRepository;

  @Override
  public List<ReportDtoResponse> getAllReports() {
    // Fetch all categories from the repository
    List<Category> categories = categoryRepository.findAll();

    // Create a report response for each category
    List<ReportDtoResponse> reportDtoResponses = new ArrayList<>();

    // Iterate through each category to calculate the report data
    for (Category category : categories) {
      String categoryName = category.getName();
      // Fetch all assets associated with the category
      List<Asset> assets = category.getAssets();
      // Initialize a list to hold the report responses
      int total = assets.size();
      // if total is 0, continue to the next category
      if (total == 0) {
        ReportDtoResponse reportDtoResponse = ReportDtoResponse.builder()
            .category(categoryName)
            .total(0)
            .assigned(0)
            .available(0)
            .notAvailable(0)
            .waiting(0)
            .recycled(0)
            .build();
        reportDtoResponses.add(reportDtoResponse);
        // Skip to the next category if there are no assets
        continue;
      }
      // Initialize counters for each asset status
      int totalAssigned = 0;
      int totalAvailable = 0;
      int totalNotAvailable = 0;
      int totalWaiting = 0;
      int totalRecycled = 0;
      // Iterate through assets to count their statuses
      for (Asset asset : assets) {
        switch (asset.getStatus()) {
          case ASSIGNED:
            totalAssigned++;
            break;
          case AVAILABLE:
            totalAvailable++;
            break;
          case NOT_AVAILABLE:
            totalNotAvailable++;
            break;
          case WAITING:
            totalWaiting++;
            break;
          case RECYCLED:
            totalRecycled++;
            break;
          default:
            break;
        }
      }
      // Create a ReportDtoResponse object with the calculated data
      ReportDtoResponse reportDtoResponse = ReportDtoResponse.builder()
          .category(categoryName)
          .total(total)
          .assigned(totalAssigned)
          .available(totalAvailable)
          .notAvailable(totalNotAvailable)
          .waiting(totalWaiting)
          .recycled(totalRecycled)
          .build();
      reportDtoResponses.add(reportDtoResponse);
    }

    // Return the list of report responses
    return reportDtoResponses;
  }
}
