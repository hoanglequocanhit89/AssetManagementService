package com.rookie.asset_management.dto.response.return_request;

import java.time.LocalDate;
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
public class ReturningRequestDtoResponse {
  Integer id;
  String assetCode;
  String assetName;
  String requestedBy;
  String acceptedBy;
  LocalDate assignedDate;
  LocalDate returnedDate;
  String status;
}
