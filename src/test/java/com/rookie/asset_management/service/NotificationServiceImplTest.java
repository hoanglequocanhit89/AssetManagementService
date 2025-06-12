package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserDetailModel;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.NotificationMapper;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.impl.NotificationServiceImpl;
import com.rookie.asset_management.util.SecurityUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock private NotificationRepository notificationRepository;

  @Mock private NotificationMapper notificationMapper;

  @InjectMocks private NotificationServiceImpl notificationServiceImpl;

  @BeforeEach
  void setupSecurityContext() {
    // Clear any existing authentication
    SecurityContextHolder.clearContext();
  }

  private void mockAuthenticatedUser(User user) {
    UserDetailModel userDetails = new UserDetailModel(user);
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      // Create authentication with the user
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(userDetails, "password", Collections.emptyList());

      // Add user principal to the authentication
      auth.setDetails(user);

      // Set the authentication in the security context
      SecurityContextHolder.getContext().setAuthentication(auth);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(user);
    }
  }

  @Test
  @DisplayName("Should mark notification as read successfully")
  void markNotificationAsRead_marksNotificationAsReadSuccessfully() {
    User user = new User();
    user.setDisabled(false);
    user.setUsername("username");
    Notification notification = new Notification();
    notification.setRecipient(user);
    notification.setRead(false);

    mockAuthenticatedUser(user);
    when(notificationRepository.findById(anyInt())).thenReturn(Optional.of(notification));

    notificationServiceImpl.markNotificationAsRead(1);

    assertTrue(notification.isRead());
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  @DisplayName("Should mark notification as read failure")
  void markNotificationAsRead_throwsExceptionWhenNotificationDoesNotExist() {
    User user = new User();
    user.setUsername("username");
    user.setDisabled(false);

    mockAuthenticatedUser(user);
    when(notificationRepository.findById(anyInt())).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> notificationServiceImpl.markNotificationAsRead(1));
  }

  @Test
  @DisplayName("Should throw AppException if notification is already marked as read")
  void markNotificationAsRead_throwsExceptionWhenNotificationIsAlreadyMarkedAsRead() {
    User user = new User();
    user.setUsername("username");
    user.setDisabled(false);
    Notification notification = new Notification();
    notification.setRecipient(user);
    notification.setRead(true);

    mockAuthenticatedUser(user);
    when(notificationRepository.findById(anyInt())).thenReturn(Optional.of(notification));

    AppException exception =
        assertThrows(AppException.class, () -> notificationServiceImpl.markNotificationAsRead(1));
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatusCode());
  }

  @Test
  @DisplayName(
      "Should throw AppException if user does not have permission to mark notification as read")
  void markNotificationAsRead_throwsExceptionWhenUserDoesNotHavePermission() {
    User user = new User();
    user.setUsername("username");
    user.setDisabled(false);
    Notification notification = new Notification();
    User otherUser = new User();
    otherUser.setUsername("otherUser");
    otherUser.setDisabled(false);
    notification.setRecipient(otherUser);
    notification.setRead(false);

    mockAuthenticatedUser(user);
    when(notificationRepository.findById(anyInt())).thenReturn(Optional.of(notification));

    AppException exception =
        assertThrows(AppException.class, () -> notificationServiceImpl.markNotificationAsRead(1));
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatusCode());
  }

  @Test
  @DisplayName("Should throw AppException if notification not found")
  void markNotificationAsRead_throwsAppExceptionIfNotificationNotFound() {
    // Given
    User user = new User();
    user.setUsername("username");
    user.setDisabled(false);

    mockAuthenticatedUser(user);
    when(notificationRepository.findById(anyInt())).thenReturn(Optional.empty());

    // When & Then
    AppException exception =
        assertThrows(AppException.class, () -> notificationServiceImpl.markNotificationAsRead(1));
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatusCode());
    assertEquals("Notification not found", exception.getMessage());
  }

  @Test
  @DisplayName("Should return all notifications successfully")
  void getAllNotifications_returnsAllNotificationsSuccessfully() {
    User user = new User();
    user.setDisabled(false);
    Notification notification1 = new Notification();
    notification1.setRecipient(user);
    Notification notification2 = new Notification();
    notification2.setRecipient(user);

    mockAuthenticatedUser(user);
    when(notificationRepository.findAllByRecipient(user))
        .thenReturn(List.of(notification1, notification2));
    when(notificationMapper.toDto(notification1))
        .thenReturn(NotificationDtoResponse.builder().build());
    when(notificationMapper.toDto(notification2))
        .thenReturn(NotificationDtoResponse.builder().build());

    List<NotificationDtoResponse> result = notificationServiceImpl.getAllNotifications();

    assertEquals(2, result.size());
    verify(notificationRepository, times(1)).findAllByRecipient(user);
  }

  @Test
  @DisplayName("Should return unread notifications count successfully")
  void getUnreadNotificationsCount_returnsUnreadNotificationsCountSuccessfully() {
    User user = new User();
    user.setDisabled(false);
    user.setUsername("username");
    Notification notification1 = new Notification();
    notification1.setRecipient(user);
    notification1.setRead(false);
    Notification notification2 = new Notification();
    notification2.setRecipient(user);
    notification2.setRead(true);

    mockAuthenticatedUser(user);
    when(notificationRepository.findAllByRecipientAndIsRead(user, false))
        .thenReturn(List.of(notification1));

    Integer unreadCount = notificationServiceImpl.getUnreadNotificationsCount();

    assertEquals(1, unreadCount);
    verify(notificationRepository, times(1)).findAllByRecipientAndIsRead(user, false);
  }

  @Test
  @DisplayName("Should return zero unread notifications when no notifications exist")
  void getUnreadNotificationsCount_returnsZeroWhenNoNotificationsExist() {
    User user = new User();
    user.setDisabled(false);
    user.setUsername("username");

    mockAuthenticatedUser(user);
    when(notificationRepository.findAllByRecipientAndIsRead(user, false))
        .thenReturn(Collections.emptyList());

    Integer unreadCount = notificationServiceImpl.getUnreadNotificationsCount();

    assertEquals(0, unreadCount);
    verify(notificationRepository, times(1)).findAllByRecipientAndIsRead(user, false);
  }

  @Test
  @DisplayName("Should mark all notifications as read successfully")
  void markAllNotificationsAsRead_marksAllNotificationsAsReadSuccessfully() {
    User user = new User();
    user.setUsername("username");
    user.setDisabled(false);
    Notification notification1 = new Notification();
    notification1.setRecipient(user);
    notification1.setRead(false);
    Notification notification2 = new Notification();
    notification2.setRecipient(user);
    notification2.setRead(false);

    mockAuthenticatedUser(user);
    when(notificationRepository.findAllByRecipientAndIsRead(user, false))
        .thenReturn(List.of(notification1, notification2));

    notificationServiceImpl.markAllNotificationsAsRead();

    assertTrue(notification1.isRead());
    assertTrue(notification2.isRead());
    verify(notificationRepository, times(1)).saveAll(List.of(notification1, notification2));
  }
}
