package com.rookie.asset_management.dto.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rookie.asset_management.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDtoResponse {
  Integer id;
  String senderName;
  NotificationType type;
  String assetName;

  @JsonProperty("isRead")
  boolean isRead;

  LocalDateTime createdAt;
}
