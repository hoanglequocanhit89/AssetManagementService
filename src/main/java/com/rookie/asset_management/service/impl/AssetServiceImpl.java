package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.response.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.ViewAssetListDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.AssetService;
import com.rookie.asset_management.util.SpecificationBuilder;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service implementation for asset management operations. Handles logic for creating a new asset.
 */
@Service
public class AssetServiceImpl implements AssetService {
  @Autowired private AssetRepository assetRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private UserRepository userRepository;

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

  /**
   * Generates an asset code using the category prefix and asset ID. The format is PREFIX + 6-digit
   * zero-padded ID (e.g., "LA000123").
   *
   * @param prefix the prefix defined by the category
   * @param id the ID of the asset
   * @return formatted asset code
   */
  private String generateAssetCode(String prefix, long id) {
    return String.format("%s%06d", prefix, id);
  }

  /**
   * Creates a new asset based on the provided DTO and current username. Performs all necessary
   * validations and mapping before saving to the database.
   *
   * @param dto the request data for creating a new asset
   * @param username the username of the user performing the creation
   * @return response DTO containing created asset details
   */
  @Override
  public CreateNewAssetDtoResponse createNewAsset(CreateNewAssetDtoRequest dto, String username) {

    // Validate asset state
    if (dto.getState() != AssetStatus.AVAILABLE && dto.getState() != AssetStatus.NOT_AVAILABLE) {
      throw new IllegalArgumentException("Asset state must be AVAILABLE or NOT_AVAILABLE");
    }

    // Validate name
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Asset name is required");
    }

    // Validate specification
    if (dto.getSpecification() == null || dto.getSpecification().trim().isEmpty()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Specification is required");
    }

    // Validate installed date
    if (dto.getInstalledDate() == null) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Installed date is required");
    }

    // Installed date must be today or a future date
    if (dto.getInstalledDate().isBefore(LocalDate.now())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Installed date must be today or a future date");
    }

    // Validate category ID
    if (dto.getCategoryId() == null) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Category ID is required");
    }

    // Redundant state check (already validated above, but added for extra safety)
    if (dto.getState() != AssetStatus.AVAILABLE && dto.getState() != AssetStatus.NOT_AVAILABLE) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Asset state must be AVAILABLE or NOT_AVAILABLE");
    }

    // Retrieve category from repository
    Category category =
        categoryRepository
            .findById(dto.getCategoryId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Category not found"));

    // Retrieve user by username
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

    // Get the user's associated location
    Location location = user.getLocation();

    // Check if asset name already exists in this location (must be unique per location)
    if (assetRepository.existsByNameAndLocation(dto.getName(), location)) {
      throw new AppException(
          HttpStatus.CONFLICT,
          "Asset name already exists in this location. Please choose a different name.");
    }

    // Create and populate Asset entity
    Asset asset = new Asset();
    asset.setName(dto.getName());
    asset.setSpecification(dto.getSpecification());
    asset.setInstalledDate(dto.getInstalledDate());
    asset.setStatus(dto.getState());
    asset.setCategory(category);
    asset.setLocation(location);
    asset.setCreatedBy(user);
    asset.setUpdatedBy(user);

    // Set creation and update timestamps
    Date now = new Date();
    asset.setCreatedAt(now);
    asset.setUpdatedAt(now);

    // Save first time to get id
    Asset savedAsset = assetRepository.save(asset);

    // check prefix and id
    String prefix = category.getPrefix();
    if (prefix == null || prefix.trim().isEmpty()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Category prefix is missing");
    }

    Integer generatedId = savedAsset.getId();
    if (generatedId == null) {
      throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate asset ID");
    }

    // Create assetCode và save
    String assetCode = generateAssetCode(prefix, generatedId);
    savedAsset.setAssetCode(assetCode);
    savedAsset = assetRepository.save(savedAsset);

    // Build and return response DTO
    return CreateNewAssetDtoResponse.builder()
        .id(savedAsset.getId())
        .assetCode(savedAsset.getAssetCode())
        .name(savedAsset.getName())
        .specification(savedAsset.getSpecification())
        .installedDate(savedAsset.getInstalledDate())
        .state(savedAsset.getStatus())
        .categoryName(savedAsset.getCategory().getName())
        .locationName(savedAsset.getLocation().getName())
        .createdByUsername(savedAsset.getCreatedBy().getUsername())
        .createdAt(savedAsset.getCreatedAt())
        .build();
  }
}
