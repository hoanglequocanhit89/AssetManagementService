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

  Optional<User> findByUsername(String username);

  /**
   * @param username
   * @return
   */
  boolean existsByUsername(String username);
}
