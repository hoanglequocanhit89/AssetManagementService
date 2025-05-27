package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.rookie.asset_management.config.bean.AuditorAwareImpl;
import com.rookie.asset_management.entity.User;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuditAwareTest {

  @Mock private AuditorAwareImpl auditorAware;

  @BeforeEach
  void setUp() {
    auditorAware = new AuditorAwareImpl();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext(); // Clear the security context after each test
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns current user")
  void testGetCurrentAuditor() {
    User user = new User();
    user.setId(1);
    user.setUsername("testUser");

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    authenticationToken.setDetails("testUser");
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    Optional<UserDetails> auditor = auditorAware.getCurrentAuditor();

    User actualUser = (User) auditor.orElse(null);

    Assertions.assertNotNull(actualUser);
    assertEquals(
        user.getUsername(), actualUser.getUsername(), "Expected auditor username to match");
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when no user is authenticated")
  void testGetCurrentAuditorNoUser() {
    SecurityContextHolder.clearContext();

    Optional<UserDetails> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when anonymous user is authenticated")
  void testGetCurrentAuditorAnonymousUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null));

    Optional<UserDetails> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when principal is not User type")
  void testGetCurrentAuditorNonUserPrincipal() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("someOtherPrincipal", null));

    Optional<UserDetails> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }
}
