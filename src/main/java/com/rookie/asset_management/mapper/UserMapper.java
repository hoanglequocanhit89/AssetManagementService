package com.rookie.asset_management.mapper;


import com.rookie.asset_management.dto.response.UserDtoResponse;
import com.rookie.asset_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between User entity and UserDtoResponse DTO.
 * This interface extends PagingMapper to provide pagination support.
 * Should be annotated with @Mapper to enable MapStruct code generation.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends PagingMapper<User, UserDtoResponse> {
  /**
   * override to map the userProfile fields to the User DTO response.
   * Converts a UserCreation DTO to a User entity.
   * @param entity the entity to convert
   * @return the converted user DTO
   */
  @Override
  @Mapping(source = "userProfile.firstName", target = "firstName")
  @Mapping(source = "userProfile.lastName", target = "lastName")
  @Mapping(target = "type", expression = "java(entity.getRole().getName())") // map role to type
  @Mapping(target = "canDisable", expression = "java(entity.getAssignments().size() > 0 ? false : true)") // map canDisable to true if no assignments
  UserDtoResponse toDto(User entity);
}
