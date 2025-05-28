package com.rookie.asset_management.dto.response.assignment;

import com.rookie.asset_management.enums.AssignmentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentStatusResponse {

  int id;

  AssignmentStatus status;
}
