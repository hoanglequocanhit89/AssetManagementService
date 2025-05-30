package com.rookie.asset_management.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FirstLoginChangePasswordRequestDTO {
  @NotBlank(message = "Password cannot be empty")
  @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,128}$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
  String newPassword;
}
