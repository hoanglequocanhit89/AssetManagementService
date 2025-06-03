package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.response.return_request.ReturningRequestDtoResponse;
import com.rookie.asset_management.dto.response.returning.ReturningRequestDetailDtoResponse;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReturningRequestMapper
    extends PagingMapper<ReturningRequest, ReturningRequestDtoResponse> {

  /**
   * Maps a ReturningRequest entity to a ReturningRequestDtoResponse.
   *
   * @param entity the ReturningRequest entity to map
   * @return the mapped ReturningRequestDtoResponse
   */
  @Override
  @Mapping(target = "id", source = "id")
  @Mapping(target = "assetCode", source = "assignment.asset.assetCode")
  @Mapping(target = "assetName", source = "assignment.asset.name")
  @Mapping(target = "assignedDate", source = "assignment.assignedDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "requestedBy", source = "requestedBy.username")
  @Mapping(target = "acceptedBy", source = "acceptedBy.username")
  ReturningRequestDtoResponse toDto(ReturningRequest entity);

  /**
   * Maps a ReturningRequestDtoResponse to a ReturningRequest entity.
   *
   * @param dto the ReturningRequestDtoResponse to map
   * @return the mapped ReturningRequest entity
   */
  @Override
  @Mapping(target = "acceptedBy", expression = "java(toUser(dto.getAcceptedBy()))")
  @Mapping(target = "requestedBy", expression = "java(toUser(dto.getRequestedBy()))")
  ReturningRequest toEntity(ReturningRequestDtoResponse dto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "assetCode", source = "assignment.asset.assetCode")
  @Mapping(target = "assetName", source = "assignment.asset.name")
  @Mapping(target = "requestedBy", source = "requestedBy.username")
  @Mapping(target = "assignedDate", source = "assignment.assignedDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "status", source = "status")
  ReturningRequestDetailDtoResponse toDetailDto(ReturningRequest returningRequest);

  /**
   * Converts a username to a User entity.
   *
   * @param username the username to convert
   * @return the User entity, or null if the username is null
   */
  default User toUser(String username) {
    if (username == null) {
      return null;
    }
    User user = new User();
    user.setUsername(username);
    return user;
  }
}
