package com.rookie.asset_management.dto.response.asset;

import com.rookie.asset_management.enums.AssetStatus;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAssetDtoResponse {
  private Integer id;
  private String assetCode;
  private String name;
  private String specification;
  private LocalDate installedDate;
  private AssetStatus state;
  private String categoryName;
  private String locationName;
  private Date updatedAt;
}
