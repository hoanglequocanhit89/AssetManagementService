package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.entity.User;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface, providing functionality related to user
 * management within the asset management system.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
  UserRepository userRepository;
  UserMapper userMapper;

  @Override
  public UserDetailDtoResponse getUserDetails(int userId) {
    User user =
        userRepository
            .findByIdAndDisabledFalse(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, ("User not found")));

    return userMapper.toUserDetailsDto(user);
  }
}
