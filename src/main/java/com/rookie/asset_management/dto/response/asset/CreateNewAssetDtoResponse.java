package com.rookie.asset_management.dto.response.asset;

import com.rookie.asset_management.enums.AssetStatus;
import java.time.LocalDate;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateNewAssetDtoResponse {
  private Integer id;
  private String name;
  private String assetCode;
  private String specification;
  private LocalDate installedDate;
  private AssetStatus state;
  private String categoryName;
  private String locationName;
  private Date createdAt;
}
