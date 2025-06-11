package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity. It extends the SpecificationRepository interface to provide
 * basic CRUD operations and custom query capabilities.
 */
@Repository
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

  /**
   * Checks if a user with the given email exists (case-insensitive).
   *
   * @param email email of the user to check
   * @return true if a user with the given email exists and is not disabled, false otherwise
   */
  boolean existsByEmailAndDisabledFalse(String email);

  /**
   * Finds all admin users that are not disabled and are assigned to the location with the given ID.
   *
   * @param locationId the ID of the location
   * @return a list of admin users
   */
  @Query(
      "SELECT u FROM User u WHERE u.role.name = 'ADMIN' AND u.location.id = :locationId AND u.disabled = false")
  List<User> findAdminsByLocationId(@Param("locationId") Integer locationId);
}
