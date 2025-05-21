package com.rookie.asset_management.dto.response.assignment;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDtoResponse {
  private String assignedTo;
  private String assignedBy;
  private LocalDate assignedDate;
  private LocalDate returnedDate;
}
