package com.rookie.asset_management.dto.request.user;

import com.rookie.asset_management.enums.Gender;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
  LocalDate dob;
  Gender gender;
  LocalDate joinedDate;
  String role;
}
