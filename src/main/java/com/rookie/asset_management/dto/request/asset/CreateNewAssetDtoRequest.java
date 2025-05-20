package com.rookie.asset_management.dto.request.asset;

import com.rookie.asset_management.enums.AssetStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewAssetDtoRequest {
  private String username;
  private String name;
  private Integer categoryId;
  private String specification;
  private LocalDate installedDate;
  private AssetStatus state;
}
