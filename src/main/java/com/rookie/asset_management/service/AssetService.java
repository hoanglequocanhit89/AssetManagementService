package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.asset.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.request.asset.EditAssetDtoRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.asset.AssetDetailDtoResponse;
import com.rookie.asset_management.dto.response.asset.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.EditAssetDtoResponse;
import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import com.rookie.asset_management.enums.AssetStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * AssetService defines the business operations related to asset management. It provides
 * functionalities for creating, editing, and retrieving assets with filtering and sorting.
 */
public interface AssetService {

  /**
   * Searches, filters, and sorts assets based on keyword, category, and states. This method is
   * commonly used for listing assets in the admin view with pagination support.
   *
   * @param keyword keyword to search in asset name or code
   * @param categoryName the category name to filter assets (optional)
   * @param states a list of asset states to filter (e.g., AVAILABLE, NOT_AVAILABLE)
   * @param pageable pagination and sorting information
   * @return a paginated response of asset list items
   */
  PagingDtoResponse<ViewAssetListDtoResponse> getAllAssets(
      String keyword, String categoryName, List<AssetStatus> states, Pageable pageable);

  /**
   * Creates a new asset based on the given request DTO and assigns it to the user's location. The
   * asset code is auto-generated based on the category prefix and a sequence number.
   *
   * @param dto the DTO containing the new asset information
   * @return the response DTO containing the created asset details
   */
  CreateNewAssetDtoResponse createNewAsset(CreateNewAssetDtoRequest dto);

  /**
   * Edits an existing asset if it is not assigned. Allows updates to name, specification, installed
   * date, and state. The asset name must be unique within the user's location.
   *
   * @param assetId the ID of the asset to be edited
   * @param dto the DTO containing updated asset information
   * @return the response DTO containing the updated asset details
   */
  EditAssetDtoResponse editAsset(Integer assetId, EditAssetDtoRequest dto);

  /**
   * Get the detail of asset information
   *
   * @param assetId id key to find asset
   * @return the detail information of asset including history
   */
  AssetDetailDtoResponse getAssetDetail(Integer assetId);

  /**
   * Deletes an asset using soft delete (marks it as deleted). The asset can only be deleted if it
   * has no associated assignments and is not in the ASSIGNED state.
   *
   * @param assetId the ID of the asset to delete
   * @throws com.rookie.asset_management.exception.AppException if the asset cannot be deleted
   */
  void deleteAsset(Integer assetId);

  /**
   * Retrieves a list of brief asset information based on a search query, sorting criteria, and
   * direction. This is typically used for displaying available assets in a brief format.
   *
   * @param keyword the search query to filter assets by name or code
   * @param sortBy the field to sort by (e.g., name, code)
   * @param sortDir the direction of sorting (asc or desc)
   * @return a list of brief asset information
   */
  List<AssetBriefDtoResponse> getAllAvailableAssetBrief(
      String keyword, String sortBy, String sortDir);
}
