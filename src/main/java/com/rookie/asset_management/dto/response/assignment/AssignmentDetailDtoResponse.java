package com.rookie.asset_management.dto.response.assignment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDetailDtoResponse {
  private Integer id;
  private String assetCode;
  private String assetName;
  private String specification;
  private String assignedTo;
  private String assignedBy;
  private String assignedDate;
  private String status;
  private String note;
}
