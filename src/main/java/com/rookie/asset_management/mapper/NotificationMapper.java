package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.response.notification.NotificationDtoResponse;
import com.rookie.asset_management.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper interface for converting Notification entities to NotificationDtoResponse DTOs. */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends BaseMapper<Notification, NotificationDtoResponse> {

  @Mapping(target = "senderName", expression = "java(mapToSenderName(entity))")
  @Mapping(target = "assetName", expression = "java(mapToAssetName(entity))")
  @Override
  NotificationDtoResponse toDto(Notification entity);

  /**
   * Maps the sender of the notification to a string representing the sender's username.
   *
   * @param entity the Notification entity
   * @return the username of the sender, or null if the sender is not set
   */
  default String mapToSenderName(Notification entity) {
    return entity.getSender() != null ? entity.getSender().getUsername() : null;
  }

  /**
   * Maps the asset associated with the notification to its name.
   *
   * @param entity the Notification entity
   * @return the name of the asset, or null if the asset is not set
   */
  default String mapToAssetName(Notification entity) {
    return entity.getAssignment() != null && entity.getAssignment().getAsset() != null
        ? entity.getAssignment().getAsset().getName()
        : null;
  }
}
