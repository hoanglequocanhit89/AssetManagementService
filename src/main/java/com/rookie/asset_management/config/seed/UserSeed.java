package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserProfile;
import com.rookie.asset_management.repository.LocationRepository;
import com.rookie.asset_management.repository.RoleRepository;
import com.rookie.asset_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(3) // after LocationSeed and RoleSeed
public class UserSeed implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final LocationRepository locationRepository;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    // Check if users already exist
    if (userRepository.count() != 0) {
      // If users already exist, skip seeding
      log.info("Users already exist, skipping seeding.");
      return;
    }

    // get role admin and staff
    Role adminRole = roleRepository.findByName("ADMIN");
    if (adminRole == null) {
      log.error("Admin role not found, cannot seed users.");
      return;
    }

    Role staffRole = roleRepository.findByName("STAFF");
    if (staffRole == null) {
      log.error("Staff role not found, cannot seed users.");
      return;
    }

    // get location DN
    Location locationDN = locationRepository.findByName("DN");
    if (locationDN == null) {
      log.error("Location DN not found, cannot seed users.");
      return;
    }

    // Create admin user
    User adminUser =
        getUser("adminu1", "Admin", "User 1", "01-01-2024", "01-01-1990", adminRole, locationDN);
    adminRole.setUsers(List.of(adminUser));
    adminUser.setStaffCode("ADMIN1");

    // Create users
    User staffUser =
        getUser("staffu1", "Staff", "User 1", "01-01-2025", "01-01-1995", staffRole, locationDN);
    staffRole.setUsers(List.of(staffUser));
    staffUser.setStaffCode("STAFF1");

    locationDN.setUsers(List.of(adminUser, staffUser));

    userRepository.saveAll(List.of(adminUser, staffUser));

    log.info("Users seeded successfully.");
  }

  private User getUser(
      String username,
      String firstName,
      String lastName,
      String joinedDate,
      String dob,
      Role staffRole,
      Location locationDN) {
    // Date format: dd-MM-yyyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    // create user
    User user = new User();
    user.setUsername(username);
    user.setRole(staffRole);
    user.setLocation(locationDN);
    user.setDisabled(false);
    user.setFirstLogin(false);
    user.setJoinedDate(LocalDate.parse(joinedDate, formatter));
    // set user profile
    UserProfile userProfile = new UserProfile();
    userProfile.setFirstName(firstName);
    userProfile.setLastName(lastName);
    LocalDate userDob = LocalDate.parse(dob, formatter);
    userProfile.setDob(userDob);
    userProfile.setUser(user);
    user.setUserProfile(userProfile);

    return user;
  }
}
