package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for notification services in the asset management system. This interface serves as a
 * marker for notification-related services.
 */
public interface NotificationService {
  /**
   * Retrieves all notifications for the current user.
   *
   * @return a list of NotificationDtoResponse objects representing the user's notifications.
   */
  List<NotificationDtoResponse> getAllNotifications();

  /**
   * Retrieves all unread notifications for the current user.
   *
   * @return a number of NotificationDtoResponse objects representing the user's unread
   *     notifications.
   */
  Integer getUnreadNotificationsCount();

  /**
   * Marks a notification as read.
   *
   * @param notificationId the ID of the notification to mark as read.
   */
  void markNotificationAsRead(Integer notificationId);

  /** Marks all notifications as read for the current user. */
  void markAllNotificationsAsRead();

  @Async
  @Transactional
  void createAssignmentNotification(User sender, User recipient, Assignment assignment);

  @Transactional
  void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest);

  @Transactional
  void createReturningRequestNotification(User sender, ReturningRequest returningRequest);

  @Async
  @Transactional
  void createReturningRequestRejectedNotification(
      User sender, User recipient, Assignment assignment);

  @Async
  @Transactional
  void createAssignmentAcceptedNotification(User sender, User recipient, Assignment assignment);

  @Async
  @Transactional
  void createAssignmentRejectedNotification(User sender, User recipient, Assignment assignment);
}
