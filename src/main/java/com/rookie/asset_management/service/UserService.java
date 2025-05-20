package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;

/**
 * UserService interface for the asset management system. This represents a placeholder for service
 * layer related to user management.
 */
public interface UserService {

  /**
   * Retrieves the details of a user by their unique identifier.
   *
   * @param userId the unique identifier of the user
   * @return a UserDetailDtoResponse containing the user's details
   */
  UserDetailDtoResponse getUserDetails(int userId);
}
