package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.enums.AssignmentStatus;
import java.util.Optional;

/** Repository interface for managing {@link Assignment} entities. */
public interface AssignmentRepository extends SpecificationRepository<Assignment, Integer> {

  Optional<Assignment> findByIdAndDeletedFalse(Integer id);

  boolean existsByAssetAndStatusAndDeletedFalse(Asset asset, AssignmentStatus assignmentStatus);
}
