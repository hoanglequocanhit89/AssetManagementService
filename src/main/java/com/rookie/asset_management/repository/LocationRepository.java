package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Location;

/**
 * Repository interface for managing Location entities. This interface extends BaseRepository to
 * provide basic CRUD operations.
 */
public interface LocationRepository extends BaseRepository<Location, Integer> {
  /**
   * Checks if a location with the given name exists (case-insensitive).
   *
   * @param name the name of the location to check
   * @return {@code true} if a location with the given name exists, {@code false} otherwise
   */
  Location findByName(String name);
}
