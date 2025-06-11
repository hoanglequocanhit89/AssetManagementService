package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.mapper.helper.AssetMappingHelper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {AssetMappingHelper.class, AssignmentMapper.class})
public interface AssetMapper extends PagingMapper<Asset, ViewAssetListDtoResponse> {

  /**
   * Convert an Asset entity to a ViewAssetListDtoResponse DTO.
   *
   * @param entity the entity to convert
   * @return a DTO representing the asset for viewing in a list
   */
  @Override
  @Mapping(target = "categoryName", expression = "java(toCategoryName(entity.getCategory()))")
  @Mapping(target = "locationName", expression = "java(toLocationName(entity.getLocation()))")
  @Mapping(source = "id", target = "canDelete", qualifiedByName = "mapCanNotDeleteAsset")
  ViewAssetListDtoResponse toDto(Asset entity);

  /**
   * Convert a CreateNewAssetDtoRequest DTO to an Asset entity.
   *
   * @param dto the DTO containing the new asset information
   * @return an Asset entity representing the new asset
   */
  @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapToCategoryById")
  @Mapping(target = "status", source = "state")
  Asset toEntity(CreateNewAssetDtoRequest dto);

  /**
   * Convert an Asset entity to a ViewAssetListDtoResponse DTO.
   *
   * @param asset the Asset entity to convert
   * @return a ViewAssetListDtoResponse DTO representing the asset
   */
  @Mapping(target = "categoryName", expression = "java(toCategoryName(asset.getCategory()))")
  @Mapping(target = "locationName", expression = "java(toLocationName(asset.getLocation()))")
  @Mapping(target = "state", source = "status")
  CreateNewAssetDtoResponse toCreationDto(Asset asset);

  /**
   * Convert an Asset entity to an EditAssetDtoResponse DTO.
   *
   * @param asset the Asset entity to convert
   * @return an EditAssetDtoResponse DTO representing the asset for editing
   */
  @Mapping(target = "categoryName", expression = "java(toCategoryName(asset.getCategory()))")
  @Mapping(target = "locationName", expression = "java(toLocationName(asset.getLocation()))")
  @Mapping(target = "state", source = "status")
  EditAssetDtoResponse toEditionDto(Asset asset);

  /**
   * Convert an Asset entity to an AssetDetailDtoResponse DTO.
   *
   * @param asset the Asset entity to convert
   * @return an AssetDetailDtoResponse DTO containing detailed information about the asset
   */
  @Mapping(
      target = "assignments",
      source = "assignments",
      qualifiedByName = "mapToAssignmentHistory")
  AssetDetailDtoResponse toDetailDto(Asset asset);

  /**
   * Convert an Asset entity to a ViewAssetListDtoResponse DTO.
   *
   * @param assets the Asset entity to convert
   * @return a ViewAssetListDtoResponse DTO representing the asset for listing
   */
  List<AssetBriefDtoResponse> toAssetBriefDtoResponses(List<Asset> assets);

  /**
   * Convert an Asset entity to an AssetBriefDtoResponse DTO.
   *
   * @param asset the Asset entity to convert
   * @return an AssetBriefDtoResponse DTO representing a brief view of the asset
   */
  @Mapping(target = "categoryName", expression = "java(toCategoryName(asset.getCategory()))")
  @Mapping(target = "assetName", expression = "java(toAssetName(asset))")
  AssetBriefDtoResponse toAssetBriefDto(Asset asset);

  /**
   * Convert a Category entity to its name.
   *
   * @param category the Category entity to convert
   * @return the name of the category, or null if the category is null
   */
  default String toCategoryName(Category category) {
    return category != null ? category.getName() : null;
  }

  /**
   * Convert a Location entity to its name.
   *
   * @param location the Location entity to convert
   * @return the name of the location, or null if the location is null
   */
  default String toLocationName(Location location) {
    return location != null ? location.getName() : null;
  }

  /**
   * Convert an Asset entity to its name.
   *
   * @param asset the Asset entity to convert
   * @return the name of the asset, or null if the asset is null
   */
  default String toAssetName(Asset asset) {
    return asset != null ? asset.getName() : null;
  }

  @Mapping(target = "categoryName", expression = "java(toCategoryName(asset.getCategory()))")
  @Mapping(target = "locationName", expression = "java(toLocationName(asset.getLocation()))")
  @Mapping(source = "id", target = "canDelete", qualifiedByName = "mapCanNotDeleteAsset")
  ViewAssetListDtoResponse toAssetListDto(Asset asset);
}
