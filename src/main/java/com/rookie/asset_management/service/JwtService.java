package com.rookie.asset_management.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;

/**
 * Service interface for managing JSON Web Tokens (JWTs) in the application.
 *
 * <p>This interface provides methods for generating, retrieving, validating, and removing JWTs, as
 * well as accessing the signing key used for token creation and verification. It is used to support
 * secure user authentication and session management, typically with JWTs stored in HTTP-only
 * cookies for client-server communication.
 */
public interface JwtService {

  /**
   * Generates a JWT for the specified user and attaches it to the HTTP response.
   *
   * <p>This method creates a JWT based on the provided username, signs it with the application's
   * secret key, and attaches it to the response, typically as an HTTP-only cookie, to establish a
   * user session.
   *
   * @param username the username for which the JWT is generated
   * @param response the HTTP response used to attach the JWT (e.g., as a cookie)
   * @throws com.rookie.asset_management.exception.AppException if token generation fails
   */
  void generateToken(String username, HttpServletResponse response);

  /**
   * Retrieves the JWT from the HTTP request's cookies.
   *
   * <p>This method extracts the JWT from the request's cookies, typically stored in an HTTP-only
   * cookie named 'jwt'. It returns the token as a string or null if no token is found.
   *
   * @param request the HTTP request containing the cookies
   * @return the JWT string if found, or null if no JWT cookie exists
   */
  String getJwtFromCookie(HttpServletRequest request);

  /**
   * Validates the provided JWT to ensure it is authentic and not expired.
   *
   * <p>This method verifies the JWT's signature, expiration, and other claims using the
   * application's secret key. If the token is invalid or expired, an exception is thrown.
   *
   * @param token the JWT string to validate
   * @throws com.rookie.asset_management.exception.AppException if the token is invalid or expired
   */
  void validateToken(String token);

  /**
   * Removes the JWT from the HTTP response's cookies.
   *
   * <p>This method clears the JWT cookie, typically named 'jwt', from the client by setting its
   * expiration to an immediate past date, effectively invalidating the user session.
   *
   * @param response the HTTP response used to remove the JWT cookie
   */
  void removeTokenFromCookie(HttpServletResponse response);

  /**
   * Retrieves the secret key used for signing and verifying JWTs.
   *
   * <p>This method returns the {@link SecretKey} used to sign and validate JWTs, typically derived
   * from a Base64-encoded secret configured in the application.
   *
   * @return the {@link SecretKey} for JWT signing and verification
   * @throws com.rookie.asset_management.exception.AppException if the secret key is invalid or not
   *     configured
   */
  SecretKey getSignInKey();

  /**
   * Extracts the username from the provided JWT.
   *
   * <p>This method parses the JWT and retrieves the username stored in the token's subject claim.
   * If the token is invalid or does not contain a subject, an exception is thrown.
   *
   * @return the username extracted from the JWT
   * @throws com.rookie.asset_management.exception.AppException if the token is invalid or the
   *     username cannot be extracted
   */
  String extractUsername();
}
