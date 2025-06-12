package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.service.NotificationService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.V1 + "/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
  NotificationService notificationService;

  @GetMapping
  public ResponseEntity<ApiDtoResponse<List<NotificationDtoResponse>>> getAllNotifications() {
    List<NotificationDtoResponse> notifications = notificationService.getAllNotifications();
    ApiDtoResponse<List<NotificationDtoResponse>> response =
        ApiDtoResponse.<List<NotificationDtoResponse>>builder().data(notifications).build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/unread-count")
  public ResponseEntity<ApiDtoResponse<Integer>> getUnreadNotificationsCount() {
    Integer unreadCount = notificationService.getUnreadNotificationsCount();
    ApiDtoResponse<Integer> response = ApiDtoResponse.<Integer>builder().data(unreadCount).build();
    return ResponseEntity.ok(response);
  }

  @PatchMapping("{notificationId}/mark-as-read")
  public ResponseEntity<ApiDtoResponse<Boolean>> getMarkAsReadNotification(
      @PathVariable("notificationId") Long notificationId) {
    notificationService.markNotificationAsRead(notificationId.intValue());
    ApiDtoResponse<Boolean> response = ApiDtoResponse.<Boolean>builder().data(true).build();
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/mark-all-as-read")
  public ResponseEntity<ApiDtoResponse<Boolean>> markAllNotificationsAsRead() {
    notificationService.markAllNotificationsAsRead();
    ApiDtoResponse<Boolean> response = ApiDtoResponse.<Boolean>builder().data(true).build();
    return ResponseEntity.ok(response);
  }
}
