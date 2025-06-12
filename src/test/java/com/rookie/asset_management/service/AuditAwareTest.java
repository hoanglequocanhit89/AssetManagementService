package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.rookie.asset_management.config.audit.AuditorAwareImpl;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserDetailModel;
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
    user.setDisabled(false);
    user.setUsername("testUser");

    UserDetailModel userDetailModel = new UserDetailModel(user);

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userDetailModel, null, Collections.emptyList());
    authenticationToken.setDetails("testUser");
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    Optional<User> auditor = auditorAware.getCurrentAuditor();

    User actualUser = auditor.orElse(null);

    Assertions.assertNotNull(actualUser);
    assertEquals(
        user.getUsername(), actualUser.getUsername(), "Expected auditor username to match");
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when no user is authenticated")
  void testGetCurrentAuditorNoUser() {
    SecurityContextHolder.clearContext();

    Optional<User> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when anonymous user is authenticated")
  void testGetCurrentAuditorAnonymousUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null));

    Optional<User> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }

  @Test
  @DisplayName("Test getCurrentAuditor returns empty when principal is not User type")
  void testGetCurrentAuditorNonUserPrincipal() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("someOtherPrincipal", null));

    Optional<User> auditor = auditorAware.getCurrentAuditor();

    assertNull(auditor.orElse(null));
  }
}
