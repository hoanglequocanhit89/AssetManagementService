package com.rookie.asset_management.config.security;

import com.rookie.asset_management.service.LoginAttemptService;
import com.rookie.asset_management.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * implement a basic solution for preventing brute force authentication attempts using Spring
 * Security.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFailureListener {
  LoginAttemptService loginAttemptService;
  HttpServletRequest request;

  /**
   * Handles successful authentication events by logging the client's IP address and recording a
   * successful login attempt.
   *
   * @param event the authentication success event containing details about the successful
   *     authentication including the authenticated user and the authentication token.
   */
  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    String clientIP = SecurityUtils.getIP(request);
    String username = event.getAuthentication().getName();
    log.debug("Authentication succeed for IP: {} and username: {}", clientIP, username);
    //    loginAttemptService.loginSucceeded(clientIP);
    loginAttemptService.loginSucceeded(username);
  }

  /**
   * Handles authentication failure events by logging the client's IP address and recording a failed
   * login attempt.
   *
   * @param event the authentication failure event containing details about the failed
   *     authentication including the exception that caused the failure and the authentication
   *     token.
   */
  @EventListener
  public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
    String clientIP = SecurityUtils.getIP(request);
    String username = event.getAuthentication().getName();
    log.debug("Authentication failed for IP: {} and username: {}", clientIP, username);
    //    loginAttemptService.loginFailed(clientIP);
    loginAttemptService.loginFailed(username);
  }
}
