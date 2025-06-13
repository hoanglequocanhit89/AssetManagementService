package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.enums.NotificationType;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.NotificationMapper;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.NotificationService;
import com.rookie.asset_management.service.UserService;
import com.rookie.asset_management.util.SecurityUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

  NotificationRepository notificationRepository;
  NotificationMapper notificationMapper;
  UserService userService;

  @Override
  public List<NotificationDtoResponse> getAllNotifications() {
    User currentUser = SecurityUtils.getCurrentUser();

    List<Notification> notifications = notificationRepository.findAllByRecipient(currentUser);
    return notifications.stream().map(notificationMapper::toDto).toList();
  }

  @Override
  public Integer getUnreadNotificationsCount() {
    User currentUser = SecurityUtils.getCurrentUser();

    List<Notification> notifications =
        notificationRepository.findAllByRecipientAndIsRead(currentUser, false);
    return notifications.size();
  }

  @Override
  public void markNotificationAsRead(Integer notificationId) {
    User currentUser = SecurityUtils.getCurrentUser();
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Notification not found"));

    if (notification.isRead()) {
      throw new AppException(HttpStatus.CONFLICT, "Notification is already marked as read");
    }

    if (notification.getRecipient().getUsername().equals(currentUser.getUsername())) {
      notification.setRead(true);
      notificationRepository.save(notification);
    } else {
      throw new AppException(
          HttpStatus.FORBIDDEN, "You do not have permission to mark this notification as read");
    }
  }

  @Override
  public void markAllNotificationsAsRead() {
    User currentUser = SecurityUtils.getCurrentUser();

    List<Notification> notifications =
        notificationRepository.findAllByRecipientAndIsRead(currentUser, false);
    for (Notification notification : notifications) {
      notification.setRead(true);
    }
    notificationRepository.saveAll(notifications);
  }

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

  @Async
  @Transactional
  @Override
  public void createAssignmentNotification(User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_CREATED, sender, recipient, assignment, null);
  }

  @Async
  @Transactional
  @Override
  public void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest) {

    if (sender == null || recipient == null || returningRequest == null) {
      log.warn(
          "Invalid parameters for completed return request notification: sender={}, recipient={}, returningRequest={}",
          sender,
          recipient,
          returningRequest);
      return;
    }

    // Đảm bảo assignment được load để tránh lazy loading exception sau này
    Assignment assignment = returningRequest.getAssignment();
    if (assignment == null) {
      log.warn(
          "ReturningRequest {} has no associated assignment. Notification not sent.",
          returningRequest.getId());
      return;
    }

    try {
      createAndSaveNotification(
          NotificationType.RETURN_REQUEST_COMPLETED,
          sender,
          recipient,
          assignment,
          returningRequest);
    } catch (RuntimeException e) {
      log.error("Failed to create completed return request notification", e);
    }
  }

  @Transactional
  @Override
  public void createReturningRequestNotification(User sender, ReturningRequest returningRequest) {
    if (sender == null || returningRequest == null || sender.getLocation() == null) {
      log.warn(
          "Invalid parameters for return request notification: sender={}, returningRequest={}",
          sender,
          returningRequest);
      return;
    }

    Assignment assignment = returningRequest.getAssignment();
    if (assignment == null) {
      log.warn(
          "ReturningRequest has no associated Assignment: returningRequestId={}",
          returningRequest.getId());
      return;
    }

    try {
      if ("ADMIN".equals(sender.getRole().getName()) && assignment.getAssignedTo() != null) {
        createAndSaveNotification(
            NotificationType.RETURN_REQUEST_CREATED,
            sender,
            assignment.getAssignedTo(),
            assignment,
            returningRequest);
      }

      List<User> admins =
          userService.getAdminsByLocation(sender.getLocation().getId()).stream()
              .filter(admin -> !Objects.equals(admin.getId(), sender.getId()))
              .collect(Collectors.toList());

      if (admins.isEmpty()) {
        log.warn(
            "No other admins found to notify for returning request {}", returningRequest.getId());
      } else {
        admins.forEach(
            admin ->
                createAndSaveNotification(
                    NotificationType.RETURN_REQUEST_CREATED,
                    sender,
                    admin,
                    assignment,
                    returningRequest));
      }

    } catch (RuntimeException e) {
      log.error("Failed to create return request notification for sender {}", sender.getId(), e);
    }
  }

  @Async
  @Transactional
  @Override
  public void createReturningRequestRejectedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.RETURN_REQUEST_REJECTED, sender, recipient, assignment, null);
  }

  @Async
  @Transactional
  @Override
  public void createAssignmentAcceptedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_ACCEPTED, sender, recipient, assignment, null);
  }

  @Async
  @Transactional
  @Override
  public void createAssignmentRejectedNotification(
      User sender, User recipient, Assignment assignment) {
    createAndSaveNotification(
        NotificationType.ASSIGNMENT_REJECTED, sender, recipient, assignment, null);
  }
}
