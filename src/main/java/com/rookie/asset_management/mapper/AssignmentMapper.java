package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper extends PagingMapper<Assignment, AssignmentListDtoResponse> {
  @Override
  @Mapping(target = "assignedTo", expression = "java(toAssigningUser(entity.getAssignedTo()))")
  @Mapping(target = "assignedBy", expression = "java(toAssigningUser(entity.getAssignedBy()))")
  @Mapping(target = "assetCode", source = "asset.assetCode")
  @Mapping(target = "assetName", source = "asset.name")
  AssignmentListDtoResponse toDto(Assignment entity);

  @Override
  @Mapping(target = "assignedTo", expression = "java(toUser(dto.getAssignedTo()))")
  @Mapping(target = "assignedBy", expression = "java(toUser(dto.getAssignedBy()))")
  Assignment toEntity(AssignmentListDtoResponse dto);

  default String toAssigningUser(User user) {
    if (user == null) {
      return null;
    }
    return user.getUsername();
  }

  default User toUser(String username) {
    if (username == null) {
      return null;
    }
    User user = new User();
    user.setUsername(username);
    return user;
  }
}
