package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.response.assignment.AssignmentDetailDtoResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentDetailForEditResponse;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.dto.response.assignment.MyAssignmentDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class})
public interface AssignmentMapper extends PagingMapper<Assignment, AssignmentListDtoResponse> {
  @Override
  @Mapping(target = "id", source = "id")
  @Mapping(target = "assetCode", source = "asset.assetCode")
  @Mapping(target = "assetName", source = "asset.name")
  @Mapping(target = "assignedDate", source = "assignedDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "assignedTo", expression = "java(toAssigningUser(entity.getAssignedTo()))")
  @Mapping(target = "assignedBy", expression = "java(toAssigningUser(entity.getAssignedBy()))")
  AssignmentListDtoResponse toDto(Assignment entity);

  @Override
  @Mapping(target = "assignedTo", expression = "java(toUser(dto.getAssignedTo()))")
  @Mapping(target = "assignedBy", expression = "java(toUser(dto.getAssignedBy()))")
  Assignment toEntity(AssignmentListDtoResponse dto);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "assetCode", source = "asset.assetCode")
  @Mapping(target = "assetName", source = "asset.name")
  @Mapping(target = "specification", source = "asset.specification")
  @Mapping(target = "assignedTo", source = "assignedTo.username")
  @Mapping(target = "assignedBy", source = "assignedBy.username")
  @Mapping(target = "assignedDate", source = "assignedDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "note", source = "note")
  AssignmentDetailDtoResponse toDetailDto(Assignment assignment);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "assetCode", source = "asset.assetCode")
  @Mapping(target = "assetName", source = "asset.name")
  @Mapping(target = "category", source = "asset.category.name")
  @Mapping(target = "assignedDate", source = "assignedDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "status", source = "status")
  MyAssignmentDtoResponse toMyAssignmentDto(Assignment entity);

  @Mapping(source = "assignedTo", target = "user", qualifiedByName = "toUserBriefDto")
  @Mapping(target = "asset.id", source = "asset.id")
  @Mapping(target = "asset.assetCode", source = "asset.assetCode")
  @Mapping(target = "asset.assetName", source = "asset.name")
  @Mapping(target = "asset.categoryName", source = "asset.category.name")
  AssignmentDetailForEditResponse toDetailForEditDto(Assignment assignment);

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
