package com.rookie.asset_management.service;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;

public interface NotificationService {
  void createAssignmentNotification(User sender, User recipient, Assignment assignment);

  void createReturningRequestCompletedNotification(
      User sender, User recipient, ReturningRequest returningRequest);

  void createReturningRequestNotification(User sender, ReturningRequest returningRequest);

  void createReturningRequestRejectedNotification(
      User sender, User recipient, Assignment assignment);

  void createAssignmentAcceptedNotification(User sender, User recipient, Assignment assignment);

  void createAssignmentRejectedNotification(User sender, User recipient, Assignment assignment);
}
