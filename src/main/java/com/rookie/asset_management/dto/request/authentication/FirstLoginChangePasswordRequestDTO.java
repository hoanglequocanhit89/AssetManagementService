package com.rookie.asset_management.dto.request.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FirstLoginChangePasswordRequestDTO {
  String newPassword;
}
