package com.rookie.asset_management.dto.response.asset;

import com.rookie.asset_management.enums.AssetStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDtoResponse {
  private Integer id;
  private String name;
  private String assetCode;
  private String category;
  private AssetStatus status;
}
