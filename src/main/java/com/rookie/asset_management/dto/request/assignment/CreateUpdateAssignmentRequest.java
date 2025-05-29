package com.rookie.asset_management.dto.request.assignment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateAssignmentRequest {

  @NotNull(message = "Asset Id is required")
  Integer assetId;

  @NotNull(message = "User Id is required")
  Integer userId;

  @NotNull(message = "Assigned date is required")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  @FutureOrPresent(message = "Assigned date must be today or in the future")
  LocalDate assignedDate;

  @Size(max = 255, message = "Note must be less than or equal to 255 characters")
  String note;
}
