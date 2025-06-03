package com.rookie.asset_management.dto.response.returning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturningRequestDetailDtoResponse {
  private Integer id;
  private String assetCode;
  private String assetName;
  private String requestedBy;
  private String assignedDate;
  private String status;
}
