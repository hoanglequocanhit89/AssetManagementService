package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.NotificationMapper;
import com.rookie.asset_management.repository.NotificationRepository;
import com.rookie.asset_management.service.NotificationService;
import com.rookie.asset_management.util.SecurityUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

  NotificationRepository notificationRepository;
  NotificationMapper notificationMapper;

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
}
