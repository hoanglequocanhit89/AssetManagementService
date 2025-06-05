package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * AssetRepository provides database access methods for Asset entities, supporting CRUD operations
 * and custom queries using JPA.
 */
@Repository
public interface AssetRepository extends SpecificationRepository<Asset, Integer> {
  /**
   * Finds all assets that match the given specification and returns them in a paginated format.
   *
   * @param build the specification to filter assets
   * @param pageable the pagination information
   * @return a page of assets that match the specification
   */
  Page<Asset> findAll(Specification<Asset> build, Pageable pageable);

  /**
   * Finds an asset by its ID, ensuring it has not been soft-deleted.
   *
   * @param id the ID of the asset to find
   * @return an Optional containing the Asset if found and not deleted, or empty if not found
   */
  Optional<Asset> findByIdAndDisabledFalse(Integer id);

  /**
   * Checks if there are any assignments associated with the given asset ID.
   *
   * @param assetId the ID of the asset to check
   * @return true if there are assignments for the asset, false otherwise
   */
  @Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE a.asset.id = :assetId")
  boolean existsAssignmentByAssetId(Integer assetId);

  /**
   * Finds all assets that match the given specification and returns them sorted by the provided
   * sort criteria.
   *
   * @param build the specification to filter assets
   * @param sort the sorting criteria
   * @return a list of assets that match the specification and are sorted accordingly
   */
  List<Asset> findAll(Specification<Asset> build, Sort sort);

  List<Asset> findByNameAndLocation(String name, Location location);

  List<Asset> findByNameAndLocationAndIdNot(String name, Location location, Integer assetId);
}
