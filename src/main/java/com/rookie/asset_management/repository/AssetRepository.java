package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Asset;
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
  Page<Asset> findAll(Specification<Asset> build, Pageable pageable);
}
