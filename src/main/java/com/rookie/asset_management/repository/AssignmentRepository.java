package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Assignment;
import java.util.Optional;

/** Repository interface for managing {@link Assignment} entities. */
public interface AssignmentRepository extends SpecificationRepository<Assignment, Integer> {

  Optional<Assignment> findByIdAndDeletedFalse(Integer id);
}
