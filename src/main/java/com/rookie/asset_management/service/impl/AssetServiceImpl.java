package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.ViewAssetListDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.service.AssetService;
import com.rookie.asset_management.util.SpecificationBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Service implementation for handling asset-related business logic. */
@Service
public class AssetServiceImpl implements AssetService {
  @Autowired private AssetRepository assetRepository;

  /**
   * Retrieves a paginated list of assets based on provided filters, search keyword, and sorting
   * options.
   *
   * @param locationId the ID of the location to filter assets (required)
   * @param keyword search term to match asset name or asset code (optional)
   * @param categoryId ID of the category to filter assets by (optional)
   * @param states list of asset statuses to filter by (optional)
   * @param pageable pagination and sorting information
   * @return paginated list of matching assets in simplified DTO format
   * @throws AppException if no assets are found
   */
  @Override
  public PagingDtoResponse<ViewAssetListDtoResponse> searchFilterAndSortAssets(
      Integer locationId,
      String keyword,
      Integer categoryId,
      List<AssetStatus> states,
      Pageable pageable) {

    // Initialize a SpecificationBuilder to build dynamic query conditions
    SpecificationBuilder<Asset> specBuilder = new SpecificationBuilder<>();

    // Mandatory filter: Filter by location ID
    specBuilder.add((root, query, cb) -> cb.equal(root.get("location").get("id"), locationId));

    // Optional keyword search: Match asset name or asset code (case-insensitive)
    if (keyword != null && !keyword.isBlank()) {
      specBuilder.add(
          (root, query, cb) ->
              cb.or(
                  cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                  cb.like(cb.lower(root.get("assetCode")), "%" + keyword.toLowerCase() + "%")));
    }

    // Optional filter: Filter by category ID
    if (categoryId != null) {
      specBuilder.add((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
    }

    // Optional filter: Filter by asset status (can be one or multiple statuses)
    if (states != null && !states.isEmpty()) {
      specBuilder.add((root, query, cb) -> root.get("status").in(states));
    }

    // Execute the query with built specifications and pagination/sorting
    Page<Asset> pageAssets = assetRepository.findAll(specBuilder.build(), pageable);

    // If no results found, throw a 404 exception
    if (pageAssets.isEmpty()) {
      throw new AppException(HttpStatus.NOT_FOUND, "No Assets found");
    }

    // Map each Asset entity to a simplified DTO for response
    List<ViewAssetListDtoResponse> content =
        pageAssets.stream()
            .map(
                asset ->
                    ViewAssetListDtoResponse.builder()
                        .assetCode(asset.getAssetCode())
                        .assetName(asset.getName())
                        .installedDate(asset.getInstalledDate())
                        .categoryName(asset.getCategory().getName())
                        .status(asset.getStatus())
                        .locationName(asset.getLocation().getName())
                        .build())
            .toList();

    // Return paginated response with metadata
    return new PagingDtoResponse<>(
        content,
        pageAssets.getTotalPages(),
        pageAssets.getTotalElements(),
        pageAssets.getSize(),
        pageAssets.getNumber(),
        pageAssets.isEmpty());
  }
}
