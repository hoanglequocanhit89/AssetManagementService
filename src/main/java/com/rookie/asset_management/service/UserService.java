package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;

/** Service interface for managing User entities. layer related to user management. */
public interface UserService {

  /**
   * Method to get all users with pagination and filtering.
   *
   * @param adminId the id of admin to view user by location
   * @param userFilterRequest the filter criteria for users
   * @param page the page number to retrieve
   * @param size the number of users per page
   * @param sortBy the field to sort by
   * @param sortDir the direction to sort (asc or desc)
   * @return a PagingDtoResponse containing a list of UserDtoResponse
   */
  PagingDtoResponse<UserDtoResponse> getAllUsers(
      Integer adminId,
      UserFilterRequest userFilterRequest,
      int page,
      int size,
      String sortBy,
      String sortDir);

  /**
   * Retrieves the details of a user by their unique identifier.
   *
   * @param userId the unique identifier of the user
   * @return a UserDetailDtoResponse containing the user's details
   */
  UserDetailDtoResponse getUserDetails(int userId);

  /**
   * create a new user
   *
   * @param request the creation user request
   * @return the created user
   */
  UserDetailDtoResponse createUser(UserRequestDTO request);
}
