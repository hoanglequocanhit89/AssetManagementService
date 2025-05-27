package com.rookie.asset_management.config.bean;

import com.rookie.asset_management.entity.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * This class implements the AuditorAware interface to provide the current auditor for auditing
 * purposes. It retrieves the current authenticated user from the security context.
 */
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<User> {

  /**
   * This method retrieves the current auditor (user) from the security context.
   *
   * @return an Optional containing the current user if authenticated, otherwise an empty Optional
   */
  @Override
  public Optional<User> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof User user) {
      return Optional.of(user);
    }

    return Optional.empty();
  }
}
