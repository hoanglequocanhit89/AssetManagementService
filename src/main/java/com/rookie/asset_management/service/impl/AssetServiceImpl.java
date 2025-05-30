package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.AssetService;
import com.rookie.asset_management.service.JwtService;
import com.rookie.asset_management.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service implementation for asset management operations. Handles logic for creating a new asset.
 */
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
  private final AssetRepository assetRepository;

  private final CategoryRepository categoryRepository;

  private final UserRepository userRepository;

  private final JwtService jwtService;

  @Override
  public PagingDtoResponse<ViewAssetListDtoResponse> getAllAssets(
      Integer locationId,
      String keyword,
      String categoryName,
      List<AssetStatus> states,
      Pageable pageable) {

    // Initialize a SpecificationBuilder to build dynamic query conditions
    SpecificationBuilder<Asset> specBuilder = new SpecificationBuilder<>();

    // Mandatory filter: Filter by location ID
    specBuilder.add((root, query, cb) -> cb.equal(root.get("location").get("id"), locationId));

    // Mandatory filter: Only fetch assets that are not disabled
    specBuilder.add((root, query, cb) -> cb.isFalse(root.get("disabled")));

    // Optional keyword search: Match asset name or asset code (case-insensitive)
    if (keyword != null && !keyword.isBlank()) {
      specBuilder.add(
          (root, query, cb) ->
              cb.or(
                  cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                  cb.like(cb.lower(root.get("assetCode")), "%" + keyword.toLowerCase() + "%")));
    }

    // Optional filter: Filter by category ID
    if (categoryName != null && !categoryName.isBlank()) {
      specBuilder.add(
          (root, query, cb) ->
              cb.like(
                  cb.lower(root.get("category").get("name")),
                  "%" + categoryName.toLowerCase() + "%"));
    }

    // Optional filter: Filter by asset status (can be one or multiple statuses)
    if (states == null || states.isEmpty()) {
      specBuilder.add(
          (root, query, cb) ->
              root.get("status")
                  .in(
                      List.of(
                          AssetStatus.AVAILABLE, AssetStatus.NOT_AVAILABLE, AssetStatus.ASSIGNED)));
    } else {
      specBuilder.add((root, query, cb) -> root.get("status").in(states));
    }

    // Execute the query with built specifications and pagination/sorting
    Page<Asset> pageAssets = assetRepository.findAll(specBuilder.build(), pageable);

    // Map each Asset entity to a simplified DTO for response
    List<ViewAssetListDtoResponse> content =
        pageAssets.stream()
            .map(
                asset -> {
                  boolean hasAssignment = assetRepository.existsAssignmentByAssetId(asset.getId());
                  return ViewAssetListDtoResponse.builder()
                      .id(asset.getId())
                      .assetCode(asset.getAssetCode())
                      .name(asset.getName())
                      .installedDate(asset.getInstalledDate())
                      .categoryName(asset.getCategory().getName())
                      .status(asset.getStatus())
                      .locationName(asset.getLocation().getName())
                      .canDelete(!hasAssignment)
                      .build();
                })
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

  private String generateAssetCode(String prefix, long id) {
    return String.format("%s%06d", prefix, id);
  }

  @Override
  public CreateNewAssetDtoResponse createNewAsset(CreateNewAssetDtoRequest dto) {

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

    // Get admin user from token
    String currentUsername = jwtService.extractUsername();
    User admin =
        userRepository
            .findByUsername(currentUsername)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Get location from admin
    Location location = admin.getLocation();

    Optional<Asset> existingAssetOpt =
        assetRepository.findByNameAndLocation(dto.getName(), location);
    if (existingAssetOpt.isPresent()) {
      Asset existingAsset = existingAssetOpt.get();
      if (!existingAsset.getDisabled()) {
        throw new AppException(
            HttpStatus.CONFLICT,
            "Asset name already exists in this location and is active. Please choose a different name.");
      }
    }

    // Create and populate Asset entity
    Asset asset = new Asset();
    asset.setName(dto.getName());
    asset.setSpecification(dto.getSpecification());
    asset.setInstalledDate(dto.getInstalledDate());
    asset.setStatus(dto.getState());
    asset.setCategory(category);
    asset.setLocation(location);
    asset.setCreatedBy(admin);
    asset.setUpdatedBy(admin);

    // Set creation and update timestamps
    Date now = new Date();
    asset.setCreatedAt(now);
    asset.setUpdatedAt(now);

    // Set asset_code avoid NOT NULL
    asset.setAssetCode("PENDING");

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
        .createdAt(savedAsset.getCreatedAt())
        .build();
  }

  @Override
  public EditAssetDtoResponse editAsset(Integer assetId, EditAssetDtoRequest dto) {

    // Fetch asset by ID or throw if not found
    Asset asset =
        assetRepository
            .findById(assetId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Asset not found"));

    // Check if the asset has been assigned — cannot be edited if assigned
    if (asset.getStatus() == AssetStatus.ASSIGNED) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Cannot edit assigned asset");
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

    // Validate state
    if (dto.getState() == null
        || !(dto.getState() == AssetStatus.AVAILABLE
            || dto.getState() == AssetStatus.NOT_AVAILABLE
            || dto.getState() == AssetStatus.WAITING
            || dto.getState() == AssetStatus.RECYCLED)) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Invalid asset state");
    }

    // Get admin user from token
    String currentUsername = jwtService.extractUsername();
    User admin =
        userRepository
            .findByUsername(currentUsername)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    Location location = admin.getLocation();

    Optional<Asset> existingAssetOpt =
        assetRepository.findByNameAndLocationAndIdNot(dto.getName(), location, assetId);
    if (existingAssetOpt.isPresent()) {
      Asset existingAsset = existingAssetOpt.get();
      if (!existingAsset.getDisabled()) {
        throw new AppException(
            HttpStatus.CONFLICT,
            "Asset name already exists in this location and is active. Please choose a different name.");
      }
    }

    // Update asset
    asset.setName(dto.getName());
    asset.setSpecification(dto.getSpecification());
    asset.setInstalledDate(dto.getInstalledDate());
    asset.setStatus(dto.getState());

    asset.setUpdatedBy(admin);
    asset.setUpdatedAt(new Date());

    asset = assetRepository.save(asset);

    // Build response DTO and return response DTO
    return EditAssetDtoResponse.builder()
        .id(asset.getId())
        .assetCode(asset.getAssetCode())
        .name(asset.getName())
        .specification(asset.getSpecification())
        .installedDate(asset.getInstalledDate())
        .state(asset.getStatus())
        .categoryName(asset.getCategory().getName())
        .locationName(asset.getLocation().getName())
        .updatedAt(asset.getUpdatedAt())
        .build();
  }

  @Override
  public AssetDetailDtoResponse getAssetDetail(Integer assetId) {
    // Fetch asset by ID or throw if not found
    Asset asset =
        assetRepository
            .findById(assetId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Asset not found"));

    AssetDetailDtoResponse response = new AssetDetailDtoResponse();
    response.setId(asset.getId());
    response.setAssetCode(asset.getAssetCode());
    response.setName(asset.getName());
    response.setSpecification(asset.getSpecification());
    response.setInstalledDate(asset.getInstalledDate());
    response.setCategory(asset.getCategory().getName());
    response.setLocation(asset.getLocation().getName());
    response.setStatus(asset.getStatus());
    // map the history
    List<AssignmentDtoResponse> history =
        asset.getAssignments().stream()
            .map(
                assignment -> {
                  AssignmentDtoResponse historyItem = new AssignmentDtoResponse();
                  historyItem.setAssignedTo(assignment.getAssignedTo().getUsername());
                  historyItem.setAssignedBy(assignment.getAssignedBy().getUsername());
                  historyItem.setAssignedDate(assignment.getAssignedDate());
                  ReturningRequest returningRequest = assignment.getReturningRequest();
                  if (returningRequest != null && returningRequest.getReturnedDate() != null) {
                    historyItem.setReturnedDate(returningRequest.getReturnedDate());
                  }
                  return historyItem;
                })
            .toList();
    response.setAssignments(history);
    return response;
  }

  @Override
  public void deleteAsset(Integer assetId) {
    // Fetch asset by ID or throw if not found or deleted
    Asset asset =
        assetRepository
            .findByIdAndDisabledFalse(assetId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Asset not found"));

    // Check if the asset is in ASSIGNED state — cannot be deleted if assigned
    if (AssetStatus.ASSIGNED.equals(asset.getStatus())) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Cannot delete asset with Assigned status");
    }

    // Check if the asset has any historical assignments
    boolean hasAssignments = assetRepository.existsAssignmentByAssetId(assetId);
    if (hasAssignments) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          "Cannot delete the asset because it belongs to one or more historical assignments. "
              + "If the asset is not able to be used anymore, please update its state in Edit Asset page");
    }

    // Perform soft delete
    asset.setDisabled(true);
    assetRepository.save(asset);
  }

  @Override
  @Transactional
  public List<AssetBriefDtoResponse> getAllAvailableAssetBrief(
      String keyword, String sortBy, String sortDir) {
    // Get the user from JWT token
    String username = jwtService.extractUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "User Not Found"));

    // Initialize a SpecificationBuilder to build dynamic query conditions
    SpecificationBuilder<Asset> specBuilder = new SpecificationBuilder<>();

    // Mandatory filter: Filter by location ID
    specBuilder.add(
        (root, query, cb) -> cb.equal(root.get("location").get("id"), user.getLocation().getId()));

    // Mandatory filter: Only fetch assets that are not disabled
    specBuilder.add((root, query, cb) -> cb.isFalse(root.get("disabled")));

    // Optional keyword search: Match asset name or asset code (case-insensitive)
    if (keyword != null && !keyword.isBlank()) {
      specBuilder.add(
          (root, query, cb) ->
              cb.or(
                  cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                  cb.like(cb.lower(root.get("assetCode")), "%" + keyword.toLowerCase() + "%")));
    }

    // Filter by AVAILABLE status
    specBuilder.add((root, query, cb) -> cb.equal(root.get("status"), AssetStatus.AVAILABLE));

    // Filter to only get asset that doesn't have any assignment in WAITING status
    specBuilder.add(
        (root, query, cb) -> {
          var subquery = query.subquery(Long.class);
          var subRoot = subquery.from(Asset.class);
          subquery
              .select(cb.literal(1L))
              .where(
                  cb.equal(root, subRoot),
                  cb.equal(subRoot.join("assignments").get("status"), AssetStatus.WAITING),
                  cb.equal(subRoot.join("assignments").get("deleted"), false));
          return cb.not(cb.exists(subquery));
        });

    // Create sorting object
    Sort sort =
        "asc".equalsIgnoreCase(sortDir)
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    // Retrieve all available assets with sorting
    List<Asset> assets = assetRepository.findAll(specBuilder.build(), sort);

    // Map assets to AssetBriefDtoResponse
    return assets.stream()
        .map(
            asset ->
                AssetBriefDtoResponse.builder()
                    .id(asset.getId())
                    .assetCode(asset.getAssetCode())
                    .assetName(asset.getName())
                    .categoryName(asset.getCategory().getName())
                    .build())
        .toList();
  }
}
