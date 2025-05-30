package com.rookie.asset_management.config.seed;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public abstract class Seeder {
  private final Environment environment;

  protected Seeder(Environment environment) {
    this.environment = environment;
  }

  /**
   * Checks if the seed data is enabled based on the application properties.
   *
   * @return true if seed data is enabled, false otherwise.
   */
  protected boolean isNotEnableSeeding() {
    // Skip seeding for test environment
    return environment.acceptsProfiles(Profiles.of("test"));
  }
}
