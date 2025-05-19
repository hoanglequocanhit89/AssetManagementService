package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * PagingService interface for handling pagination and sorting of entities.
 *
 * @param <T> the type of the DTO
 * @param <E> the type of the entity
 */
public interface PagingService<T, E> {
  /**
   * Get a paginated list of entities.
   *
   * @param spec the spec to filter the entities. This can be null.
   * @param pageable the pagination information.
   * @return a paginated response containing the entities
   */
  PagingDtoResponse<T> getMany(Specification<E> spec, Pageable pageable);
}
