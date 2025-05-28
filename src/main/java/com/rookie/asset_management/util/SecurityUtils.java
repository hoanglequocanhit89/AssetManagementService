package com.rookie.asset_management.util;

import com.rookie.asset_management.entity.UserDetailModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SecurityUtils {

  public static boolean isFirstLogin() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetailModel userDetailModel) {
      return userDetailModel.getIsFirstLogin();
    }
    return false;
  }
}
