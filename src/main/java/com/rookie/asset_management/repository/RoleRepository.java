package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Role;

/**
 * Repository interface for managing Role entities. This interface extends BaseRepository to provide
 * basic CRUD operations.
 */
public interface RoleRepository extends BaseRepository<Role, Integer> {
  /**
   * Checks if a role with the given name exists (case-insensitive).
   *
   * @param name the name of the role to check
   * @return {@code true} if a role with the given name exists, {@code false} otherwise
   */
  Role findByName(String name);
}
