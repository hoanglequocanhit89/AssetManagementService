package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;

import com.rookie.asset_management.service.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

  private LoginAttemptService loginAttemptService;

  @BeforeEach
  void setUp() {
    loginAttemptService = new LoginAttemptServiceImpl();
  }

  @Test
  void givenNewIP_whenCheckIfBlocked_thenReturnFalse() {
    // Given a new IP
    String ip = "192.168.1.1";

    // When checking if blocked
    boolean isBlocked = loginAttemptService.isBlocked(ip);

    // Then it should not be blocked
    assertFalse(isBlocked);
  }

  @Test
  void givenIP_whenMaxFailedAttempts_thenShouldBeBlocked() {
    // Given an IP with max failed attempts
    String ip = "192.168.1.2";

    // When login fails MAX_ATTEMPT times
    for (int i = 0; i < LoginAttemptServiceImpl.MAX_ATTEMPT; i++) {
      loginAttemptService.loginFailed(ip);
    }

    // Then it should be blocked
    assertTrue(loginAttemptService.isBlocked(ip));
  }

  @Test
  void givenBlockedIP_whenLoginSucceeded_thenShouldBeUnblocked() {
    // Given a blocked IP
    String ip = "192.168.1.3";
    for (int i = 0; i < LoginAttemptServiceImpl.MAX_ATTEMPT; i++) {
      loginAttemptService.loginFailed(ip);
    }
    assertTrue(loginAttemptService.isBlocked(ip));

    // When login succeeds
    loginAttemptService.loginSucceeded(ip);

    // Then it should be unblocked
    assertFalse(loginAttemptService.isBlocked(ip));
  }

  @Test
  void givenIP_whenFewerThanMaxFailedAttempts_thenShouldNotBeBlocked() {
    // Given an IP with fewer than MAX_ATTEMPT failures
    String ip = "192.168.1.4";

    // When login fails fewer than MAX_ATTEMPT times
    for (int i = 0; i < LoginAttemptServiceImpl.MAX_ATTEMPT - 1; i++) {
      loginAttemptService.loginFailed(ip);
    }

    // Then it should not be blocked
    assertFalse(loginAttemptService.isBlocked(ip));
  }
}
