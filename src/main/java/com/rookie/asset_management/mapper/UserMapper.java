package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.entity.ReturningRequest;
import com.rookie.asset_management.entity.Role;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.entity.UserProfile;
import com.rookie.asset_management.enums.AssignmentStatus;
import com.rookie.asset_management.enums.ReturningRequestStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for converting between User entity and UserDtoResponse DTO. This interface
 * extends PagingMapper to provide pagination support. Should be annotated with @Mapper to enable
 * MapStruct code generation.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends PagingMapper<User, UserDtoResponse> {

  /**
   * Map the user creation request dto to entity
   *
   * @param dto the user creation request dto
   * @return the user entity
   */
  @Mapping(target = "userProfile.firstName", source = "firstName")
  @Mapping(target = "userProfile.lastName", source = "lastName")
  @Mapping(target = "userProfile.dob", source = "dob")
  @Mapping(target = "userProfile.gender", source = "gender")
  @Mapping(target = "joinedDate", source = "joinedDate")
  @Mapping(target = "location", expression = "java(mapLocation(dto.getLocation(), dto.getType()))")
  User toEntity(UserRequestDTO dto);

  /**
   * override to map the userProfile fields to the User DTO response. Converts a UserCreation DTO to
   * a User entity.
   *
   * @param user the user to convert
   * @return the converted user DTO
   */
  @Override
  @Mapping(target = "fullName", expression = "java(user.getUserProfile().getFullName())")
  @Mapping(source = "role.name", target = "role")
  @Mapping(target = "canDisable", expression = "java(canDisable(user.getAssignments()))")
  // map canDisable to true if no assignments
  UserDtoResponse toDto(User user);

  /**
   * Converts a {@link User} entity to a {@link UserDetailDtoResponse}.
   *
   * @param user the user entity
   * @return the user details dto response
   */
  @Mapping(target = "id", source = "id")
  @Mapping(target = "location", source = "location.name")
  @Mapping(target = "role", source = "role.name")
  @Mapping(target = "firstName", source = "userProfile.firstName")
  @Mapping(target = "lastName", source = "userProfile.lastName")
  @Mapping(target = "fullName", expression = "java(user.getUserProfile().getFullName())")
  @Mapping(target = "dob", source = "userProfile.dob")
  @Mapping(target = "gender", source = "userProfile.gender")
  UserDetailDtoResponse toUserDetailsDto(User user);

  /**
   * default method to map a role name to a {@link Role} entity. This helps to convert a role name
   * string to a Role entity.
   *
   * @param roleName the name of the role
   * @return the Role entity
   */
  default Role map(String roleName) {
    if (roleName == null) {
      return null;
    }
    Role role = new Role();
    role.setName(roleName);
    return role;
  }

  /**
   * Updates the fields of a user entity from an updateUserRequest DTO, ignoring the role field.
   *
   * @param dto the updateUserRequest DTO containing the new user details
   * @param user the user entity to update
   */
  @Mapping(target = "role", ignore = true)
  void updateUserFromDto(UpdateUserRequest dto, @MappingTarget User user);

  /**
   * Updates the fields of a user profile entity from an updateUserRequest DTO.
   *
   * @param dto the updateUserRequest DTO containing the new user profile details
   * @param profile the user profile entity to update
   */
  void updateUserProfileFromDto(UpdateUserRequest dto, @MappingTarget UserProfile profile);

  /**
   * default method to map a location name to a {@link Location} entity. This helps to convert a
   * location name string to a Location entity
   *
   * @param location the location name
   * @return the Location entity
   */
  default Location mapLocation(String location, String type) {
    if ("Admin".equalsIgnoreCase(type) && location != null && !location.trim().isEmpty()) {
      // Validate and map location from request for Admin
      String trimmedLocation = location.trim().toUpperCase();
      if (!trimmedLocation.equals("HCM")
          && !trimmedLocation.equals("HN")
          && !trimmedLocation.equals("DN")) {
        throw new IllegalArgumentException("Invalid location. Must be one of: HCM, HN, DN");
      }
      Location loc = new Location();
      loc.setName(trimmedLocation);
      switch (trimmedLocation) {
        case "HCM":
          loc.setId(3);
          break;
        case "HN":
          loc.setId(2);
          break;
        case "DN":
          loc.setId(1);
          break;
      }
      return loc;
    }
    // For Staff or when location is not provided, return null to be handled by service
    return null;
  }

  /**
   * default method to check if the user can be disabled. A user can be disabled if all their
   * assignments are not in WAITING status.
   *
   * @param assignments the list of assignments
   * @return true if the user can be disabled, false otherwise
   */
  default boolean canDisable(List<Assignment> assignments) {
    for (Assignment assignment : assignments) {
      AssignmentStatus assignmentStatus = assignment.getStatus();
      if (assignmentStatus == AssignmentStatus.WAITING) {
        return false;
      }
      ReturningRequest returningRequest = assignment.getReturningRequest();
      if (assignmentStatus == AssignmentStatus.ACCEPTED
          && returningRequest != null
          && returningRequest.getStatus() == ReturningRequestStatus.WAITING) {
        return false;
      }
    }
    return true;
  }

  /**
   * Converts a User entity to a UserBriefDtoResponse.
   *
   * @param user the user entity
   * @return the user brief dto response
   */
  @Mapping(target = "role", source = "role.name")
  @Mapping(target = "fullName", expression = "java(user.getUserProfile().getFullName())")
  UserBriefDtoResponse toUserBriefDto(User user);
}
