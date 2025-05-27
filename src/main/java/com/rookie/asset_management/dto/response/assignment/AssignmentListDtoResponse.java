package com.rookie.asset_management.dto.response.assignment;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentListDtoResponse {
  private Integer id;
  private String assetCode;
  private String assetName;
  private String assignedTo;
  private String assignedBy;
  private LocalDate assignedDate;
  private String status;
}
