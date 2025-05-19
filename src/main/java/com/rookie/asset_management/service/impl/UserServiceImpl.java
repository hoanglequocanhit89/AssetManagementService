package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.UserDtoResponse;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.UserService;
import com.rookie.asset_management.service.specification.SpecificationBuilder;
import com.rookie.asset_management.service.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends PagingServiceImpl<UserDtoResponse, User, Integer>
    implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

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
    String name = userFilterRequest.getName();
    String staffCode = userFilterRequest.getStaffCode();
    String type = userFilterRequest.getType();
    // Create a specification based on the filter request
    Specification<User> spec =
        new SpecificationBuilder<User>()
            .addIfNotNull(name, UserSpecification.hasFirstNameOrLastNameLike(name))
            .addIfNotNull(staffCode, UserSpecification.hasStaffCodeLike(staffCode))
            .addIfNotNull(type, UserSpecification.hasType(type))
            .addIfNotNull(adminId, UserSpecification.hasSameLocationAs(adminId))
            .addIfNotNull(adminId, UserSpecification.excludeAdmin(adminId))
            .build();
    return getMany(spec, pageable);
  }
}
