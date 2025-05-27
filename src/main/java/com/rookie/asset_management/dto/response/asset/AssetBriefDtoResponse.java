package com.rookie.asset_management.dto.response.asset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssetBriefDtoResponse {
  int id;
  String assetCode;
  String assetName;
  String categoryName;
}
