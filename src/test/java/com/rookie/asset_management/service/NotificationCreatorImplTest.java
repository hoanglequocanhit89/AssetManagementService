package com.rookie.asset_management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.impl.handler.NotificationCreatorImpl;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationCreatorImplTest {

  @Mock private NotificationRepository notificationRepository;

  @Mock private UserService userService;

  @InjectMocks private NotificationCreatorImpl notificationService;

  private User sender;
  private User recipient;
  private Assignment assignment;
  private ReturningRequest returningRequest;
  private Location location;
  private Role adminRole;

  @BeforeEach
  void setUp() {

    // Initialize test data
    sender = new User();
    sender.setId(1);

    recipient = new User();
    recipient.setId(2);

    location = new Location();
    location.setId(1);
    sender.setLocation(location);

    adminRole = new Role();
    adminRole.setName("ADMIN");
    sender.setRole(adminRole);

    assignment = new Assignment();
    assignment.setId(1);
    assignment.setAssignedTo(recipient);

    returningRequest = new ReturningRequest();
    returningRequest.setId(1);
    returningRequest.setAssignment(assignment);
  }

  @Test
  void createAssignmentAcceptedNotification_success() {
    notificationService.createAssignmentAcceptedNotification(sender, recipient, assignment);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void createAssignmentNotification_nullSender_logsWarning() {
    notificationService.createAssignmentNotification(null, recipient, assignment);

    verify(notificationRepository, times(0)).save(any());
  }

  @Test
  void createAssignmentNotification_repositoryThrowsException_logsError() {
    doThrow(new RuntimeException("DB error"))
        .when(notificationRepository)
        .save(any(Notification.class));

    notificationService.createAssignmentNotification(sender, recipient, assignment);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void createReturningRequestCompletedNotification_success() {
    notificationService.createReturningRequestCompletedNotification(
        sender, recipient, returningRequest);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void createReturningRequestNotification_adminSender_notifiesAssigneeAndOtherAdmins() {
    User admin2 = new User();
    admin2.setId(3);
    admin2.setRole(adminRole);
    when(userService.getAdminsByLocation(1)).thenReturn(List.of(admin2));

    notificationService.createReturningRequestNotification(sender, returningRequest);

    verify(notificationRepository, times(2)).save(any(Notification.class));
    verify(userService, times(1)).getAdminsByLocation(1);
  }

  @Test
  void createReturningRequestNotification_nonAdminSender_notifiesAdminsOnly() {
    Role userRole = new Role();
    userRole.setName("USER");
    sender.setRole(userRole);
    User admin = new User();
    admin.setId(3);
    when(userService.getAdminsByLocation(1)).thenReturn(List.of(admin));

    notificationService.createReturningRequestNotification(sender, returningRequest);

    verify(notificationRepository, times(1)).save(any(Notification.class));
    verify(userService, times(1)).getAdminsByLocation(1);
  }

  @Test
  void createReturningRequestRejectedNotification_success() {
    notificationService.createReturningRequestRejectedNotification(sender, recipient, assignment);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void createAssignmentRejectedNotification_success() {
    notificationService.createAssignmentRejectedNotification(sender, recipient, assignment);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void createReturningRequestNotification_emptyAdminList_noNotifications() {
    when(userService.getAdminsByLocation(1)).thenReturn(Collections.emptyList());

    notificationService.createReturningRequestNotification(sender, returningRequest);

    verify(notificationRepository, times(1)).save(any(Notification.class));
    verify(userService, times(1)).getAdminsByLocation(1);
  }
}
