package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.NotificationType;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.NotificationService;
import com.rookie.asset_management.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

  NotificationRepository notificationRepository;
  UserService userService;

  @Override
  @Async
  @Transactional
  public void createAssignmentNotification(User sender, User recipient, Assignment assignment) {
    try {
      Notification notification =
          Notification.builder()
              .type(NotificationType.ASSIGNMENT_CREATED)
              .sender(sender)
              .recipient(recipient)
              .assignment(assignment)
              .isRead(false)
              .build();

      notificationRepository.save(notification);
    } catch (Exception e) {
      log.error("Error creating assignment notification", e);
    }
  }

  @Override
  @Async
  @Transactional
  public void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest) {
    try {
      Notification notification =
          Notification.builder()
              .type(NotificationType.RETURN_REQUEST_COMPLETED)
              .sender(sender)
              .recipient(recipient)
              .returningRequest(returningRequest)
              .isRead(false)
              .build();

      notificationRepository.save(notification);
    } catch (Exception e) {
      log.error("Error creating returning request completed notification", e);
    }
  }

  @Override
  @Async
  @Transactional
  public void createReturningRequestNotification(
      User sender, ReturningRequest returningRequest) {
    try {
      // Get admin at location
      Integer locationId = sender.getLocation().getId();
      List<User> adminsAtLocation = userService.getAdminsByLocation(locationId);

      // Send notification to assignee if sender is an admin
      if (sender.getRole().getName().equals("ADMIN")) {
        Notification notification =
            Notification.builder()
                .type(NotificationType.RETURN_REQUEST_CREATED)
                .sender(sender)
                .recipient(returningRequest.getAssignment().getAssignedTo())
                .returningRequest(returningRequest)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
      }

      // Send notification to admins at location except sender
      for (User admin : adminsAtLocation) {
        if (!admin.getId().equals(sender.getId())) {
          Notification notification =
              Notification.builder()
                  .type(NotificationType.RETURN_REQUEST_CREATED)
                  .sender(sender)
                  .recipient(admin)
                  .returningRequest(returningRequest)
                  .isRead(false)
                  .build();
          notificationRepository.save(notification);
        }
      }
    } catch (Exception e) {
      log.error("Error creating returning request notification", e);
    }
  }
}
