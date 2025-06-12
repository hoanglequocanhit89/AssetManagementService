package com.rookie.asset_management.service.impl.handler;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.NotificationType;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.NotificationCreator;
import com.rookie.asset_management.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationCreatorImpl implements NotificationCreator {

  NotificationRepository notificationRepository;
  UserService userService;

  private void createAndSaveNotification(
      NotificationType type,
      User sender,
      User recipient,
      Assignment assignment,
      ReturningRequest returningRequest) {
    if (sender == null || recipient == null) {
      log.warn("Invalid notification parameters: sender={}, recipient={}", sender, recipient);
      return;
    }

    try {
      Notification.NotificationBuilder builder =
          Notification.builder().type(type).sender(sender).recipient(recipient).isRead(false);

      if (assignment != null) {
        builder.assignment(assignment);
      }
      if (returningRequest != null) {
        builder.returningRequest(returningRequest);
      }

      notificationRepository.save(builder.build());
    } catch (RuntimeException e) {
      log.error(
          "Failed to create notification of type {} for sender {} and recipient {}",
          type,
          sender.getId(),
          recipient.getId(),
          e);
    }
  }

  @Override
  @Async
  @Transactional
  public void createAssignmentNotification(User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_CREATED, sender, recipient, assignment, null);
  }

  @Override
  @Async
  @Transactional
  public void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest) {
    createAndSaveNotification(
        NotificationType.RETURN_REQUEST_COMPLETED, sender, recipient, null, returningRequest);
  }

  @Override
  @Async
  @Transactional
  public void createReturningRequestNotification(User sender, ReturningRequest returningRequest) {
    if (sender == null || returningRequest == null || sender.getLocation() == null) {
      log.warn(
          "Invalid parameters for return request notification: sender={}, returningRequest={}",
          sender,
          returningRequest);
      return;
    }
    try {
      // Notify assignee if sender is admin
      if ("ADMIN".equals(sender.getRole().getName())) {
        createAndSaveNotification(
            NotificationType.RETURN_REQUEST_CREATED,
            sender,
            returningRequest.getAssignment().getAssignedTo(),
            null,
            returningRequest);
      }

      // Notify other admins at the same location
      userService.getAdminsByLocation(sender.getLocation().getId()).stream()
          .filter(admin -> !Objects.equals(admin.getId(), sender.getId()))
          .forEach(
              admin ->
                  createAndSaveNotification(
                      NotificationType.RETURN_REQUEST_CREATED,
                      sender,
                      admin,
                      null,
                      returningRequest));
    } catch (RuntimeException e) {
      log.error("Failed to create return request notification for sender {}", sender.getId(), e);
    }
  }

  @Override
  @Async
  @Transactional
  public void createReturningRequestRejectedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.RETURN_REQUEST_REJECTED, sender, recipient, assignment, null);
  }

  @Override
  @Async
  @Transactional
  public void createAssignmentAcceptedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_ACCEPTED, sender, recipient, assignment, null);
  }

  @Override
  @Async
  @Transactional
  public void createAssignmentRejectedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_REJECTED, sender, recipient, assignment, null);
  }
}
