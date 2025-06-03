package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.ReturningRequest;
import java.util.Optional;

/** Repository interface for managing {@link ReturningRequest} entities. */
public interface ReturningRequestRepository
    extends SpecificationRepository<ReturningRequest, Integer> {
  Optional<ReturningRequest> findByIdAndDeletedFalse(Integer id);
}
