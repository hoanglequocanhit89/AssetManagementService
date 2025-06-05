package com.rookie.asset_management.dto.asset;

import com.rookie.asset_management.dto.response.asset.ViewAssetListDtoResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetGroupedByStatusDto {
  private String status;
  private List<ViewAssetListDtoResponse> listAsset;
}
