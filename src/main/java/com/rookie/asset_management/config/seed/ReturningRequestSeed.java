package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.repository.ReturningRequestRepository;
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
@Order(8)
@RequiredArgsConstructor
public class ReturningRequestSeed implements CommandLineRunner {
  private final ReturningRequestRepository returningRequestRepository;
  private final UserRepository userRepository;
  private final AssignmentRepository assignmentRepository;

  @Override
  public void run(String... args) throws Exception {
    if (returningRequestRepository.count() != 0) {
      // If returning requests already exist, skip seeding
      log.info("Returning requests already exist, skipping seeding.");
      return;
    }

    List<User> users = userRepository.findAll();
    List<Assignment> assignments = assignmentRepository.findAll();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    ReturningRequest returningRequest1 = new ReturningRequest();
    returningRequest1.setStatus(ReturningRequestStatus.WAITING);
    returningRequest1.setReturnedDate(LocalDate.parse("04-02-2025", formatter));
    returningRequest1.setRequestedBy(users.get(2));
    returningRequest1.setAcceptedBy(null);
    returningRequest1.setAssignment(assignments.get(1));

    ReturningRequest returningRequest2 = new ReturningRequest();
    returningRequest2.setStatus(ReturningRequestStatus.COMPLETED);
    returningRequest2.setReturnedDate(LocalDate.parse("01-01-2025", formatter));
    returningRequest2.setRequestedBy(users.get(2));
    returningRequest2.setAcceptedBy(users.get(0));
    returningRequest2.setAssignment(assignments.get(2));

    returningRequestRepository.saveAll(List.of(returningRequest1, returningRequest2));
    log.info("Returning requests seeded successfully.");
  }
}
