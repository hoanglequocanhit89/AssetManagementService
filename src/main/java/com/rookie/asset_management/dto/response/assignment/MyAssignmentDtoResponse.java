package com.rookie.asset_management.dto.response.assignment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyAssignmentDtoResponse {
  private Integer id;
  private String assetCode;
  private String assetName;
  private String category;
  private String assignedDate;
  private String status;
}
