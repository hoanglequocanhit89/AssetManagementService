package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.UserRequestDTO;
import com.rookie.asset_management.dto.request.user.UpdateUserRequest;
import com.rookie.asset_management.dto.request.user.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.user.CreateUserDtoResponse;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDetailDtoResponse;
import com.rookie.asset_management.dto.response.user.UserDtoResponse;
import java.util.List;

/** Service interface for managing User entities. layer related to user management. */
public interface UserService {

  /**
   * Method to get all users with pagination and filtering.
   *
   * @param userFilterRequest the filter criteria for users
   * @param page the page number to retrieve
   * @param size the number of users per page
   * @param sortBy the field to sort by
   * @param sortDir the direction to sort (asc or desc)
   * @return a PagingDtoResponse containing a list of UserDtoResponse
   */
  PagingDtoResponse<UserDtoResponse> getAllUsers(
      UserFilterRequest userFilterRequest, int page, int size, String sortBy, String sortDir);

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
  CreateUserDtoResponse createUser(UserRequestDTO request);

  /**
   * Updates the details of an existing user.
   *
   * @param userId the unique identifier of the user to update
   * @param request an UpdateUserRequest containing the new details for the user
   */
  void updateUser(int userId, UpdateUserRequest request);

  /**
   * Deletes a user by their unique identifier.
   *
   * @param userId the unique identifier of the user to delete
   */
  void deleteUser(int userId);

  /**
   * Retrieves a paginated and filtered list of brief user information.
   *
   * @param query a search query to filter users (staff code or first name)
   * @param sortBy the field to sort by
   * @param sortDir the direction to sort (asc or desc)
   * @return a PagingDtoResponse containing a list of UserBriefDtoResponse
   */
  List<UserBriefDtoResponse> getAllUserBrief(String query, String sortBy, String sortDir);
}
