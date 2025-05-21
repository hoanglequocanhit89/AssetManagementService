package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.ViewAssetListDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
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
@RequestMapping("api/v1/asset")
public class AssetController {

  @Autowired private AssetService assetService;

  /**
   * API to get a list of assets filtered, searched, and sorted
   *
   * @param locationId mandatory location ID of the assets
   * @param keyword optional search the keyword
   * @param categoryName optional asset categoryName
   * @param states optional list of asset statuses to filter
   * @param page page number for pagination (default 0)
   * @param size page size for pagination (default 20)
   * @param sortBy sortBy field to sort by (default "assetCode")
   * @param sortDir sort direction: asc or desc (default asc)
   * @return paginated and filtered list of assets
   */
  @GetMapping
  public ResponseEntity<ApiDtoResponse<PagingDtoResponse<ViewAssetListDtoResponse>>>
      getAssetsByFilterSearchAndSort(
          @RequestParam Integer locationId,
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
        assetService.searchFilterAndSortAssets(locationId, keyword, categoryName, states, pageable);

    // Return the response wrapped in ApiDtoResponse with a success message
    return ResponseEntity.ok(
        ApiDtoResponse.<PagingDtoResponse<ViewAssetListDtoResponse>>builder()
            .message("Assets retrieved successfully.")
            .data(result)
            .build());
  }

  /**
   * This endpoint accepts a JSON payload representing asset details and the username of the user
   * who is performing the creation. It delegates the creation logic to the AssetService and returns
   * the newly created asset in the response.
   *
   * @param dto the asset data sent from the client
   * @param username the username of the currently authenticated user
   * @return ResponseEntity containing the created asset and HTTP 201 status
   */
  @PostMapping
  public ResponseEntity<CreateNewAssetDtoResponse> createAsset(
      @RequestBody CreateNewAssetDtoRequest dto, @RequestParam("username") String username) {
    // Call the service layer to handle asset creation logic
    CreateNewAssetDtoResponse createdAsset = assetService.createNewAsset(dto, username);

    // Return HTTP 201 Created with the asset details in response body
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset);
  }

  /**
   * Handles HTTP PUT request to update an existing asset by its ID. Only unassigned assets can be
   * edited. The editable fields include: - name - specification - installed date - state
   *
   * <p>The asset name must be unique within the same location. The category cannot be changed.
   *
   * @param assetId the ID of the asset to be updated
   * @param dto the DTO containing updated asset information
   * @param username the username of the admin performing the update
   * @return ResponseEntity containing the updated asset information
   */
  @PutMapping("/{assetId}")
  public ResponseEntity<EditAssetDtoResponse> editAsset(
      @PathVariable Integer assetId,
      @RequestBody @Valid EditAssetDtoRequest dto,
      @RequestParam("username") String username) {

    // Call the service layer to perform the update
    EditAssetDtoResponse updatedAsset = assetService.editAsset(assetId, dto, username);

    // Call the service layer to perform the update
    return ResponseEntity.ok(updatedAsset);
  }

  /**
   * Deletes an asset by its ID using soft delete. The asset can only be deleted if it has no
   * associated assignments and is not in the ASSIGNED state.
   *
   * @param assetId the ID of the asset to delete
   * @return ResponseEntity containing a success message wrapped in ApiDtoResponse
   * @throws AppException if the asset cannot be deleted (not found, assigned, or has assignments)
   */
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
}
