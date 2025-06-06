package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.AssetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AssetController handles HTTP requests related to asset management. It is designed for admin users
 * who need to manage and view assets within a specific location. All API endpoints follow the
 * "/api/v1/asset" base path.
 */
@RestController
@RequestMapping("api/v1/assets")
public class AssetController {

  @Autowired private AssetService assetService;

  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<ViewAssetListDtoResponse>>>
      getAssetsByFilterSearchAndSort(
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String categoryName,
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
        assetService.getAllAssets(keyword, categoryName, states, pageable);

    // Return the response wrapped in ApiDtoResponse with a success message
    return ResponseEntity.ok(
        ApiDtoResponse.<PagingDtoResponse<ViewAssetListDtoResponse>>builder()
            .message("Assets retrieved successfully.")
            .data(result)
            .build());
  }

  @PostMapping
  public ResponseEntity<ApiDtoResponse<CreateNewAssetDtoResponse>> createAsset(
      @RequestBody CreateNewAssetDtoRequest dto) {
    // Call the service layer to handle asset creation logic
    CreateNewAssetDtoResponse createdAsset = assetService.createNewAsset(dto);

    // Return HTTP 201 Created with the asset details in response body
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiDtoResponse.<CreateNewAssetDtoResponse>builder()
                .message("Asset created successfully.")
                .data(createdAsset)
                .build());
  }

  @PutMapping("/{assetId}")
  public ResponseEntity<ApiDtoResponse<EditAssetDtoResponse>> editAsset(
      @PathVariable Integer assetId, @RequestBody @Valid EditAssetDtoRequest dto) {

    // Call the service layer to perform the update
    EditAssetDtoResponse updatedAsset = assetService.editAsset(assetId, dto);

    // Call the service layer to perform the update
    return ResponseEntity.ok()
        .body(
            ApiDtoResponse.<EditAssetDtoResponse>builder()
                .message("Asset updated successfully.")
                .data(updatedAsset)
                .build());
  }

  @DeleteMapping("/{assetId}")
  public ResponseEntity<ApiDtoResponse<String>> deleteAsset(@PathVariable Integer assetId) {
    try {
      // Call the service layer to perform the soft delete
      assetService.deleteAsset(assetId);

      // Return success response with HTTP 200 OK status
      return ResponseEntity.ok(
          ApiDtoResponse.<String>builder()
              .message("Asset deleted successfully.")
              .data(null)
              .build());
    } catch (AppException e) {
      // Handle specific error cases based on HTTP status
      if (e.getHttpStatusCode() == HttpStatus.BAD_REQUEST) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiDtoResponse.<String>builder().message(e.getMessage()).data(null).build());
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiDtoResponse.<String>builder().message("Asset not found.").data(null).build());
    }
  }

  @GetMapping("/{assetId}")
  public ResponseEntity<ApiDtoResponse<AssetDetailDtoResponse>> getAssetDetails(
      @PathVariable Integer assetId) {
    // Call the service layer to fetch asset details
    AssetDetailDtoResponse assetDetails = assetService.getAssetDetail(assetId);

    // Return the response wrapped in ApiDtoResponse with a success message
    return ResponseEntity.ok(
        ApiDtoResponse.<AssetDetailDtoResponse>builder()
            .message("Asset details retrieved successfully.")
            .data(assetDetails)
            .build());
  }

  @GetMapping("/all/brief")
  public ResponseEntity<ApiDtoResponse<List<AssetBriefDtoResponse>>> getAllAssetBrief(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(defaultValue = "assetCode") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    List<AssetBriefDtoResponse> assetBriefs =
        assetService.getAllAvailableAssetBrief(query, sortBy, sortDir);
    return ResponseEntity.ok(
        ApiDtoResponse.<List<AssetBriefDtoResponse>>builder()
            .message("Assets retrieved successfully.")
            .data(assetBriefs)
            .build());
  }
}
