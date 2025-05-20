package com.rookie.asset_management.dto.response;

import com.rookie.asset_management.enums.AssetStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing asset information in the asset list view. Contains asset details such as
 * code, name, category, status, and location.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewAssetListDtoResponse {
  private String assetCode;
  private String assetName;
  private LocalDate installedDate;
  private String categoryName;
  private AssetStatus status;
  private String locationName;
}
