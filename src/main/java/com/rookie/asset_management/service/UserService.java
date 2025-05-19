package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.UserDtoResponse;

/**
 * Service interface for managing User entities. This interface extends BaseService to provide basic
 * CRUD operations.
 */
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
}
