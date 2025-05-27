package com.rookie.asset_management.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom authentication entry point for handling unauthorized access attempts.
 * <p>
 * This class implements Spring Security's {@link AuthenticationEntryPoint} to define the behavior
 * when an unauthenticated user attempts to access a protected resource. It responds with an HTTP
 * 401 Unauthorized status and a descriptive error message, typically used in JWT-based
 * authentication flows where a valid token is missing or invalid.
 * </p>
 *
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  /**
   * Handles unauthorized access by sending an HTTP 401 Unauthorized response.
   * <p>
   * This method is invoked when an {@link AuthenticationException} occurs, indicating that the
   * request lacks valid authentication credentials (e.g., a missing or invalid JWT token). It
   * sets the HTTP response status to 401 and includes an error message to inform the client of
   * the unauthorized access attempt.
   * </p>
   *
   * @param request the HTTP request that resulted in an authentication failure
   * @param response the HTTP response to send the 401 Unauthorized status and error message
   * @param authException the exception that triggered the authentication failure
   * @throws IOException if an error occurs while writing to the response
   * @throws ServletException if a servlet-related error occurs during processing
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
  }
}
