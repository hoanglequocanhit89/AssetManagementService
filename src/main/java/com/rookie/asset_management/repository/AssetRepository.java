package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/**
 * AssetRepository provides database access methods for Asset entities, supporting CRUD operations
 * and custom queries using JPA.
 */
@Repository
public interface AssetRepository extends BaseRepository<Asset, Integer> {
  /**
   * Finds all assets that match the given specification and returns them in a paginated format.
   *
   * @param build the specification to filter assets
   * @param pageable the pagination information
   * @return a page of assets that match the specification
   */
  Page<Asset> findAll(Specification<Asset> build, Pageable pageable);

  /**
   * Checks if an asset with the given name and location exists (case-insensitive).
   *
   * @param name the name of the asset to check
   * @param location the location of the asset to check
   * @return {@code true} if an asset with the given name and location exists, {@code false}
   *     otherwise
   */
  boolean existsByNameAndLocation(String name, Location location);
}
