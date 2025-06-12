package com.rookie.asset_management.util;

import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserDetailModel;
import com.rookie.asset_management.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SecurityUtils {

  /**
   * Checks if the current user is authenticated. This method retrieves the authentication object
   * from the security context and checks if it is not null and the user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise
   */
  public static boolean isFirstLogin() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetailModel userDetailModel) {
      return userDetailModel.getIsFirstLogin();
    }
    return false;
  }

  /**
   * Retrieves the client's IP address from the request headers or remote address. This method
   * checks for the "X-Forwarded-For" and "X-Real-IP" headers to get the original IP address if the
   * request is behind a proxy or load balancer. If those headers are not present, it falls back to
   * the remote address of the request.
   *
   * @param request the HttpServletRequest object containing the request information
   * @return the client's IP address as a String
   */
  public static String getIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      log.debug("xForwardedFor: {}", xForwardedFor);
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIP = request.getHeader("X-Real-IP");
    if (xRealIP != null && !xRealIP.isEmpty()) {
      log.debug("xRealIP: {}", xRealIP);
      return xRealIP;
    }

    log.debug("Remote Address: {}", request.getRemoteAddr());
    return request.getRemoteAddr();
  }

  /**
   * Retrieves the current authenticated user from the security context. This method checks the
   * principal object in the security context and casts it to UserDetailModel. If the principal is
   * not an instance of UserDetailModel, it throws an AppException with an UNAUTHORIZED status.
   *
   * @return the current authenticated User object
   */
  public static User getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetailModel userDetailModel) {
      return userDetailModel.getUser();
    }
    throw new AppException(HttpStatus.UNAUTHORIZED, "Unauthorized access. Please log in.");
  }
}
