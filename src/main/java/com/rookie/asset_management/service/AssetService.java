package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.CreateNewAssetDtoRequest;
import com.rookie.asset_management.dto.response.CreateNewAssetDtoResponse;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.ViewAssetListDtoResponse;
import com.rookie.asset_management.enums.AssetStatus;

import java.util.List;

import org.springframework.data.domain.Pageable;

/**
 * AssetService defines the business operations related to asset management.
 */
public interface AssetService {

    PagingDtoResponse<ViewAssetListDtoResponse> searchFilterAndSortAssets(
            Integer locationId,
            String keyword,
            Integer categoryId,
            List<AssetStatus> states,
            Pageable pageable);

    CreateNewAssetDtoResponse createNewAsset(CreateNewAssetDtoRequest dto, String username);
}
