package com.rookie.asset_management.dto.asset;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryGroupedAssetDto {
  private String name;
  private List<AssetGroupedByStatusDto> data;
}
