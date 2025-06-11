package com.rookie.asset_management.service;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;

/**
 * Interface for creating notifications in the asset management system.
 * This interface defines methods for creating different types of notifications
 * related to assignments and returning requests.
 */
public interface NotificationService {

  /**
   * Creates a notification for an assignment.
   * @param sender the user who is sending the notification
   * @param recipient the user who is the recipient of the notification
   * @param assignment the assignment related to the notification
   */
  void createAssignmentNotification(User sender, User recipient, Assignment assignment);

  /**
   * Creates a notification for a completed returning request.
   * @param sender the user who is sending the notification
   * @param recipient the user who is the recipient of the notification
   * @param returningRequest the returning request related to the notification
   */
  void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest);


  /**
   * Creates a notification for a returning request.
   * @param sender the user who is sending the notification
   * @param returningRequest the returning request related to the notification
   */
  void createReturningRequestNotification(User sender, ReturningRequest returningRequest);

  void createReturningRequestRejectedNotification(
      User sender, User recipient, Assignment assignment);

  void createAssignmentAcceptedNotification(User sender, User recipient, Assignment assignment);

  void createAssignmentRejectedNotification(User sender, User recipient, Assignment assignment);
}
