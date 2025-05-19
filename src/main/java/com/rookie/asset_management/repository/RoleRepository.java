package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Role;

/**
 * Repository interface for managing Role entities. This interface extends BaseRepository to provide
 * basic CRUD operations.
 */
public interface RoleRepository extends BaseRepository<Role, Integer> {
  Role findByName(String name);
}
