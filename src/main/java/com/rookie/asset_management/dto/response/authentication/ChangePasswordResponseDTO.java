package com.rookie.asset_management.dto.response.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChangePasswordResponseDTO {
  String oldPassword;
  String newPassword;
}
