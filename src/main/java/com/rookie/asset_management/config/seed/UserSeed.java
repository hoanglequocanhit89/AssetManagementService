package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserProfile;
import com.rookie.asset_management.enums.Gender;
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

    Location locationHN = locationRepository.findByName("HN");

    Location locationHCM = locationRepository.findByName("HCM");

    // Create admin user
    User adminUser1 =
        getUser(
            "admindnu1",
            "Admindn",
            "User 1",
            "01-01-2024",
            "01-01-1990",
            adminRole,
            locationDN,
            Gender.MALE);
    adminRole.setUsers(List.of(adminUser1));
    adminUser1.setStaffCode("ADMIN1");

    User adminUser2 =
        getUser(
            "adminhcmu2",
            "Adminhcm",
            "User 2",
            "02-01-2024",
            "02-01-1990",
            adminRole,
            locationHCM,
            Gender.MALE);
    adminRole.setUsers(List.of(adminUser2));
    adminUser2.setStaffCode("ADMIN2");

    User adminUser3 =
        getUser(
            "adminhnu3",
            "Adminhn",
            "User 3",
            "03-01-2024",
            "03-01-1990",
            adminRole,
            locationHN,
            Gender.FEMALE);
    adminRole.setUsers(List.of(adminUser3));
    adminUser3.setStaffCode("ADMIN3");

    // Create users
    User staffUser1 =
        getUser(
            "staffdnu1",
            "Staffdn",
            "User 1",
            "01-01-2025",
            "01-01-1995",
            staffRole,
            locationDN,
            Gender.MALE);
    staffRole.setUsers(List.of(staffUser1));
    staffUser1.setStaffCode("STAFF1");

    User staffUser2 =
        getUser(
            "staffdnu2",
            "Staffdn",
            "User 2",
            "02-01-2025",
            "02-01-1995",
            staffRole,
            locationDN,
            Gender.FEMALE);
    staffRole.setUsers(List.of(staffUser2));
    staffUser2.setStaffCode("STAFF2");

    User staffUser3 =
        getUser(
            "staffhcmu3",
            "Staffhcm",
            "User 3",
            "03-01-2025",
            "03-01-1995",
            staffRole,
            locationHCM,
            Gender.FEMALE);
    staffRole.setUsers(List.of(staffUser3));
    staffUser3.setStaffCode("STAFF3");

    User staffUser4 =
        getUser(
            "staffhcmu4",
            "Staffhcm",
            "User 4",
            "04-01-2025",
            "04-01-1995",
            staffRole,
            locationHCM,
            Gender.MALE);
    staffRole.setUsers(List.of(staffUser4));
    staffUser4.setStaffCode("STAFF4");

    User staffUser5 =
        getUser(
            "staffhnu5",
            "Staffhn",
            "User 5",
            "05-01-2025",
            "05-01-1995",
            staffRole,
            locationHN,
            Gender.MALE);
    staffRole.setUsers(List.of(staffUser5));
    staffUser5.setStaffCode("STAFF5");

    User staffUser6 =
        getUser(
            "staffhnu6",
            "Staffhn",
            "User 6",
            "06-01-2025",
            "06-01-1995",
            staffRole,
            locationHN,
            Gender.FEMALE);
    staffRole.setUsers(List.of(staffUser6));
    staffUser6.setStaffCode("STAFF6");

    locationDN.setUsers(List.of(adminUser1, staffUser1, staffUser2));
    locationHCM.setUsers(List.of(adminUser2, staffUser3, staffUser4));
    locationHN.setUsers(List.of(adminUser3, staffUser5, staffUser6));

    userRepository.saveAll(
        List.of(
            adminUser1,
            adminUser2,
            adminUser3,
            staffUser1,
            staffUser2,
            staffUser3,
            staffUser4,
            staffUser5,
            staffUser6));

    log.info("Users seeded successfully.");
  }

  private User getUser(
      String username,
      String firstName,
      String lastName,
      String joinedDate,
      String dob,
      Role staffRole,
      Location locationDN,
      Gender gender) {
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
    userProfile.setGender(gender);
    user.setUserProfile(userProfile);

    return user;
  }
}
