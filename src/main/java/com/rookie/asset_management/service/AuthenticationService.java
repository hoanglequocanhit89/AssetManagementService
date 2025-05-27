package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service interface for handling user authentication and session management.
 * <p>
 * This interface provides methods for user login, password changes, and logout operations.
 * It integrates with Spring Security for authentication and manages JWT-based session tokens,
 * typically stored in HTTP-only cookies for secure client-server communication.
 * </p>
 *
 */
public interface AuthenticationService {

  /**
   * Authenticates a user and generates a JWT token for session management.
   * <p>
   * This method validates the provided credentials (username and password) against the
   * user data source. Upon successful authentication, it generates a JWT token and
   * attaches it to the response, typically as an HTTP-only cookie, to establish a
   * user session.
   * </p>
   *
   * @param loginRequestDTO the data transfer object containing the username and password
   * @param response the HTTP response used to attach the JWT token (e.g., as a cookie)
   * @return the generated JWT token as a string
   * @throws com.rookie.asset_management.exception.AppException if authentication fails
   *         (e.g., invalid credentials)
   */
  String login(LoginRequestDTO loginRequestDTO, HttpServletResponse response);

  /**
   * Changes the password for an authenticated user.
   * <p>
   * This method validates the provided password change request (e.g., old password and new
   * password) and updates the user's password in the data source. It may also refresh the
   * JWT token and update the response (e.g., by setting a new HTTP-only cookie) to maintain
   * the user session.
   * </p>
   *
   * @param changePasswordRequestDTO the data transfer object containing the old password,
   *                                 new password, and other required details
   * @param response the HTTP response used to update the JWT token (e.g., as a cookie)
   * @return the updated JWT token as a string, or an empty string if no new token is issued
   * @throws com.rookie.asset_management.exception.AppException if the password change fails
   *         (e.g., invalid old password or unauthorized access)
   */
  String changePassword(
      ChangePasswordRequestDTO changePasswordRequestDTO, HttpServletResponse response);

  /**
   * Terminates the user session by invalidating the JWT token.
   * <p>
   * This method clears the JWT token from the client, typically by removing or invalidating
   * the HTTP-only cookie containing the token, effectively logging out the user.
   * </p>
   *
   * @param response the HTTP response used to clear the JWT token (e.g., by removing the cookie)
   */
  void logout(HttpServletResponse response);
}
