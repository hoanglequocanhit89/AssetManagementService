package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.repository.RoleRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@Order(2)
public class RoleSeed extends Seeder implements CommandLineRunner {

  private final RoleRepository roleRepository;

  public RoleSeed(Environment environment, RoleRepository roleRepository) {
    super(environment);
    this.roleRepository = roleRepository;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (isNotEnableSeeding()) {
      // Skip seeding if not enabled
      log.info("Role seeding is disabled in the current environment.");
      return;
    }
    // Check if roles already exist
    if (roleRepository.count() != 0) {
      // If roles already exist, skip seeding
      log.info("Roles already exist, skipping seeding.");
      return;
    }

    // Create roles
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    Role staffRole = new Role();
    staffRole.setName("STAFF");

    // Save roles
    roleRepository.saveAll(List.of(adminRole, staffRole));
    log.info("Roles seeded successfully.");
  }
}
