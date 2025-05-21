package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.User;
import java.util.Optional;

/**
 * Repository interface for User entity. It extends the SpecificationRepository interface to provide
 * basic CRUD operations and custom query capabilities.
 */
public interface UserRepository extends SpecificationRepository<User, Integer> {
  /**
   * find user by id who is not disabled
   *
   * @param id id of the user to find
   * @return Optional user entity
   */
  Optional<User> findByIdAndDisabledFalse(Integer id);

  /**
   * find user by id
   *
   * @param username username of the user to find
   * @return Optional user entity
   */
  Optional<User> findByUsername(String username);

  /**
   * Checks if a user with the given username exists (case-insensitive).
   *
   * @param username username of the user to check
   * @return true if a user with the given username exists, false otherwise
   */
  boolean existsByUsername(String username);
}
