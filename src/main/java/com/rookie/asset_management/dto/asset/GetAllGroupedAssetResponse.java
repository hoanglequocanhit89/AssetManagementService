package com.rookie.asset_management.dto.asset;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetAllGroupedAssetResponse {
  private String message;
  private Map<String, List<CategoryGroupedAssetDto>> data;
}
