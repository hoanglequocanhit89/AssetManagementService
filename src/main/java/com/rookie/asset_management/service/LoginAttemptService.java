package com.rookie.asset_management.service;

/**
 * Service interface for handling login attempts. This interface can be implemented to track and
 * manage login attempts,
 */
public interface LoginAttemptService {
  /**
   * Records a failed login attempt for a given key. This method is typically used to track failed
   * login attempts
   *
   * @param key the key (usually an IP address or username) associated with the failed login
   *     attempt.
   */
  void loginFailed(String key);

  /**
   * Checks if attempt cache (usually an IP address or username) is blocked due to too many failed
   * login attempts.
   *
   * @param key the key (usually an IP address or username) to check for blocking status.
   * @return true if it is blocked, false otherwise.
   */
  boolean isBlocked(String key);

  /**
   * Records a successful login attempt for a given key. This method is typically used to indicate
   * that a login attempt was successful and to reset any tracking for that key.
   *
   * @param key the key (usually an IP address or username) associated with the successful login
   *     attempt.
   */
  void loginSucceeded(String key);
}
