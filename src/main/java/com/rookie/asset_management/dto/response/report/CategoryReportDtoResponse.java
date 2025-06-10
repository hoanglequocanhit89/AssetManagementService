package com.rookie.asset_management.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CategoryReportDtoResponse {
  String category;
  Integer total;
  Integer assigned;
  Integer available;
  Integer notAvailable;
  Integer waiting;
  Integer recycled;
}
