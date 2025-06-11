package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.AssetMapper;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.service.AssetService;
import com.rookie.asset_management.service.abstraction.PagingServiceImpl;
import com.rookie.asset_management.service.specification.AssetSpecification;
import com.rookie.asset_management.util.SecurityUtils;
import com.rookie.asset_management.util.SpecificationBuilder;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service implementation for asset management operations. Handles logic for creating a new asset.
 */
@Service
public class AssetServiceImpl extends PagingServiceImpl<ViewAssetListDtoResponse, Asset, Integer>
    implements AssetService {
  private final AssetRepository assetRepository;

  private final AssetMapper assetMapper;

  @Autowired
  public AssetServiceImpl(AssetRepository assetRepository, AssetMapper assetMapper) {
    super(assetMapper, assetRepository);
    this.assetRepository = assetRepository;
    this.assetMapper = assetMapper;
  }

  @Override
  public PagingDtoResponse<ViewAssetListDtoResponse> getAllAssets(
      String keyword, String categoryName, List<AssetStatus> states, Pageable pageable) {

    User admin = SecurityUtils.getCurrentUser();

    // Initialize a SpecificationBuilder to build dynamic query conditions
    Specification<Asset> specBuilder =
        new SpecificationBuilder<Asset>()
            .add(AssetSpecification.hasLocationId(admin.getLocation().getId()))
            .add(AssetSpecification.excludeDisabled())
            .addIfNotNull(keyword, AssetSpecification.hasNameOrCodeLike(keyword))
            .addIfNotNull(categoryName, AssetSpecification.hasCategoryName(categoryName))
            .addIfNotNull(states, AssetSpecification.hasStateIn(states))
            .build();

    return getMany(specBuilder, pageable);
  }

  @Override
  public CreateNewAssetDtoResponse createNewAsset(CreateNewAssetDtoRequest dto) {
    // Get admin user from token
    User admin = SecurityUtils.getCurrentUser();

    // Get location from admin
    Location location = admin.getLocation();

    List<Asset> assets = assetRepository.findByNameAndLocation(dto.getName(), location);
    for (Asset asset : assets) {
      if (Boolean.FALSE.equals(asset.getDisabled())) {
        throw new AppException(
            HttpStatus.CONFLICT,
            "Asset name already exists in this location and is active. Please choose a different name.");
      }
    }

    // Create and populate Asset entity
    Asset asset = assetMapper.toEntity(dto);
    asset.setLocation(location);

    // Set asset_code avoid NOT NULL
    asset.setAssetCode("PENDING");
    Asset savedAsset = assetRepository.save(asset);
    return assetMapper.toCreationDto(savedAsset);
  }

  @Override
  public EditAssetDtoResponse editAsset(Integer assetId, EditAssetDtoRequest dto) {

    // Fetch asset by ID or throw if not found
    Asset asset =
        assetRepository
            .findById(assetId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Asset not found"));

    // New check: asset was disabled by another user
    Location location = getLocation(asset);

    List<Asset> existingAssets =
        assetRepository.findByNameAndLocationAndIdNot(dto.getName(), location, assetId);
    if (!existingAssets.isEmpty()) {
      boolean hasActive =
          existingAssets.stream().anyMatch(assets -> Boolean.FALSE.equals(assets.getDisabled()));
      if (hasActive) {
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

    asset = assetRepository.save(asset);

    // Build response DTO and return response DTO
    return assetMapper.toEditionDto(asset);
  }

  @Override
  public AssetDetailDtoResponse getAssetDetail(Integer assetId) {
    // Fetch asset by ID or throw if not found
    Asset asset =
        assetRepository
            .findById(assetId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Asset not found"));

    return assetMapper.toDetailDto(asset);
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
    User user = SecurityUtils.getCurrentUser();

    // Initialize a SpecificationBuilder to build dynamic query conditions
    Specification<Asset> specBuilder =
        new SpecificationBuilder<Asset>()
            .add(AssetSpecification.hasLocationId(user.getLocation().getId()))
            .add(AssetSpecification.excludeDisabled())
            .addIfNotNull(keyword, AssetSpecification.hasNameOrCodeLike(keyword))
            .add(AssetSpecification.hasStateIn(List.of(AssetStatus.AVAILABLE)))
            .add(AssetSpecification.excludeAssignmentStatus(AssetStatus.WAITING))
            .build();

    // Create sorting object
    Sort sort =
        "asc".equalsIgnoreCase(sortDir)
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    // Retrieve all available assets with sorting
    List<Asset> assets = assetRepository.findAll(specBuilder, sort);

    // Map assets to AssetBriefDtoResponse
    return assetMapper.toAssetBriefDtoResponses(assets);
  }

  private static Location getLocation(Asset asset) {
    if (Boolean.TRUE.equals(asset.getDisabled())) {
      throw new AppException(
          HttpStatus.CONFLICT,
          "Update failed: The asset was modified by another user. Please refresh and try again.");
    }

    // Check if the asset has been assigned — cannot be edited if assigned
    if (asset.getStatus() == AssetStatus.ASSIGNED) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Cannot edit assigned asset");
    }

    // Get admin user from token
    User admin = SecurityUtils.getCurrentUser();

    return admin.getLocation();
  }
}
