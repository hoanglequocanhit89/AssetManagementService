package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Location;

/**
 * Repository interface for managing Location entities. This interface extends BaseRepository to
 * provide basic CRUD operations.
 */
public interface LocationRepository extends BaseRepository<Location, Integer> {
  Location findByName(String name);
}
