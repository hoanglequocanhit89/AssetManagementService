package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.ViewAssetListDtoResponse;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.service.AssetService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AssetController handles HTTP requests related to asset management. It is designed for admin users
 * who need to manage and view assets within a specific location. All API endpoints follow the
 * "/api/v1/asset" base path.
 */
@RestController
@RequestMapping("/api/v1/asset")
public class AssetController {

  @Autowired private AssetService assetService;

  /**
   * API to get a list of assets filtered, searched, and sorted
   *
   * @param locationId mandatory location ID of the assets
   * @param keyword optional search the keyword
   * @param categoryId optional asset categoryId
   * @param states optional list of asset statuses to filter
   * @param page page number for pagination (default 0)
   * @param size page size for pagination (default 20)
   * @param sortBy sortBy field to sort by (default "assetCode")
   * @param sortDir sort direction: asc or desc (default asc)
   * @return paginated and filtered list of assets
   */
  @GetMapping("/getAsset")
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<ViewAssetListDtoResponse>>>
      getAssetsByFilterSearchAndSort(
          @RequestParam Integer locationId,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer categoryId,
          @RequestParam(required = false) List<AssetStatus> states,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size,
          @RequestParam(defaultValue = "assetCode") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {

    // Create Sort object base on SortBy and sortDir parameters
    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    // Create Pageable object with page number, size, and sorting info
    Pageable pageable = PageRequest.of(page, size, sort);

    // Call service method to fetch filtered, searched, sorted, and paginated asset list
    PagingDtoResponse<ViewAssetListDtoResponse> result =
        assetService.searchFilterAndSortAssets(locationId, keyword, categoryId, states, pageable);

    // Return the response wrapped in ApiDtoResponse with a success message
    return ResponseEntity.ok(
        ApiDtoResponse.<PagingDtoResponse<ViewAssetListDtoResponse>>builder()
            .message("Assets retrieved successfully.")
            .data(result)
            .build());
  }
}
