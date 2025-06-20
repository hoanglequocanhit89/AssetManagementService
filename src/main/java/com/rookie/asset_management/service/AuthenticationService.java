package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.authentication.ChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.FirstLoginChangePasswordRequestDTO;
import com.rookie.asset_management.dto.request.authentication.LoginRequestDTO;
import com.rookie.asset_management.dto.response.authentication.LoginResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service interface for handling user authentication and session management.
 *
 * <p>This interface provides methods for user login, password changes, and logout operations. It
 * integrates with Spring Security for authentication and manages JWT-based session tokens,
 * typically stored in HTTP-only cookies for secure client-server communication.
 */
public interface AuthenticationService {

  /**
   * Authenticates a user and generates a JWT token for session management.
   *
   * <p>This method validates the provided credentials (username and password) against the user data
   * source. Upon successful authentication, it generates a JWT token and attaches it to the
   * response, typically as an HTTP-only cookie, to establish a user session.
   *
   * @param loginRequestDTO the data transfer object containing the username and password
   * @param response the HTTP response used to attach the JWT token (e.g., as a cookie)
   * @return the generated JWT token as a string
   * @throws com.rookie.asset_management.exception.AppException if authentication fails (e.g.,
   *     invalid credentials)
   */
  LoginResponseDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response);

  /**
   * Changes the password for an authenticated user.
   *
   * <p>This method validates the provided password change request (e.g., old password and new
   * password) and updates the user's password in the data source. It may also refresh the JWT token
   * and update the response (e.g., by setting a new HTTP-only cookie) to maintain the user session.
   *
   * @param changePasswordRequestDTO the data transfer object containing the old password, new
   *     password, and other required details
   * @param response the HTTP response used to update the JWT token (e.g., as a cookie)
   * @return the updated JWT token as a string, or an empty string if no new token is issued
   * @throws com.rookie.asset_management.exception.AppException if the password change fails (e.g.,
   *     invalid old password or unauthorized access)
   */
  String changePassword(
      ChangePasswordRequestDTO changePasswordRequestDTO, HttpServletResponse response);

  /**
   * Terminates the user session by invalidating the JWT token.
   *
   * <p>This method clears the JWT token from the client, typically by removing or invalidating the
   * HTTP-only cookie containing the token, effectively logging out the user.
   *
   * @param response the HTTP response used to clear the JWT token (e.g., by removing the cookie)
   */
  void logout(HttpServletResponse response);

  /**
   * Changes the password for a user on their first login.
   *
   * <p>This method updates the user's password using the provided new password without requiring
   * the old password, intended for first-time login scenarios. It updates the user's password in
   * the data source, sets the first login flag to false, and may refresh the JWT token to maintain
   * the user session.
   *
   * @param firstLoginChangePasswordRequestDTO the data transfer object containing the new password
   * @param response the HTTP response used to update the JWT token (e.g., as a cookie)
   * @return a string message indicating the result of the operation (e.g., "Password changed
   *     successfully!")
   * @throws com.rookie.asset_management.exception.AppException if the password change fails (e.g.,
   *     new password matches the old one or unauthorized access)
   */
  String firstLoginChangePassword(
      FirstLoginChangePasswordRequestDTO firstLoginChangePasswordRequestDTO,
      HttpServletResponse response);
}
