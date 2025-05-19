package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.UserService;
import com.rookie.asset_management.service.specification.UserSpecification;
import com.rookie.asset_management.util.SpecificationBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface, providing functionality related to user
 * management within the asset management system.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl extends PagingServiceImpl<UserDtoResponse, User, Integer>
    implements UserService {
  UserRepository userRepository;
  UserMapper userMapper;

  // Autowired constructor for paging service implementation
  @Autowired
  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    super(userMapper, userRepository);
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public PagingDtoResponse<UserDtoResponse> getAllUsers(
      Integer adminId,
      UserFilterRequest userFilterRequest,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    if (adminId == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Admin ID is required to get user list based on admin location");
    }
    // check if the sortBy is sort by firstName or lastName
    // must set to related.property because of the join
    if (sortBy != null && sortBy.equals("firstName")) {
      sortBy = "userProfile.firstName";
    }
    if (sortBy != null && sortBy.equals("lastName")) {
      sortBy = "userProfile.lastName";
    }
    // Create a pageable object for pagination and sorting with default values
    Pageable pageable = createPageable(page, size, sortDir, sortBy);
    // destructure the filter request
    String query = userFilterRequest.getQuery();
    String type = userFilterRequest.getType();
    // Create a specification based on the filter request
    Specification<User> spec =
        new SpecificationBuilder<User>()
            .addIfNotNull(query, UserSpecification.hasNameOrCodeLike(query))
            .addIfNotNull(type, UserSpecification.hasType(type))
            .addIfNotNull(adminId, UserSpecification.hasSameLocationAs(adminId))
            .addIfNotNull(adminId, UserSpecification.excludeAdmin(adminId))
            .build();
    return getMany(spec, pageable);
  }

  @Override
  public UserDetailDtoResponse getUserDetails(int userId) {
    User user =
        userRepository
            .findByIdAndDisabledFalse(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ("User not found")));

    return userMapper.toUserDetailsDto(user);
  }
}
