package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.repository.UserRepository;
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
@Order(7)
@RequiredArgsConstructor
public class AssignmentSeed implements CommandLineRunner {

  private final AssignmentRepository assignmentRepository;
  private final UserRepository userRepository;
  private final AssetRepository assetRepository;

  @Override
  public void run(String... args) throws Exception {
    // Check if assignments already exist
    if (assignmentRepository.count() != 0) {
      // If assignments already exist, skip seeding
      log.info("Assignments already exist, skipping seeding.");
      return;
    }

    // Create users
    List<User> users = userRepository.findAll();
    List<Asset> assets = assetRepository.findAll();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Create and save assignments here
    Assignment assignment1 = new Assignment();
    assignment1.setAssignedBy(users.get(0));
    assignment1.setAssignedTo(users.get(1));
    assignment1.setAsset(assets.get(0));
    assignment1.setAssignedDate(LocalDate.parse("01-02-2023", formatter));
    assignment1.setStatus(AssignmentStatus.WAITING);

    Assignment assignment2 = new Assignment();
    assignment2.setAssignedBy(users.get(1));
    assignment2.setAssignedTo(users.get(2));
    assignment2.setAsset(assets.get(1));
    assignment2.setAssignedDate(LocalDate.parse("04-05-2022", formatter));
    assignment2.setStatus(AssignmentStatus.ACCEPTED);

    Assignment assignment3 = new Assignment();
    assignment3.setAssignedBy(users.get(0));
    assignment3.setAssignedTo(users.get(2));
    assignment3.setAsset(assets.get(2));
    assignment3.setAssignedDate(LocalDate.parse("06-07-2021", formatter));
    assignment3.setStatus(AssignmentStatus.ACCEPTED);

    // Save assignments
    assignmentRepository.saveAll(List.of(assignment1, assignment2, assignment3));

    log.info("Assignments seeded successfully.");
  }
}
