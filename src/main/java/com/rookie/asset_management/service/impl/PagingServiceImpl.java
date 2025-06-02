package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.mapper.PagingMapper;
import com.rookie.asset_management.repository.SpecificationRepository;
import com.rookie.asset_management.service.PagingService;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Abstract implementation of PagingService that provides common pagination and sorting
 * functionality.
 *
 * @param <D> the type of the DTO
 * @param <E> the type of the entity
 * @param <K> the type of the entity's identifier
 */
@RequiredArgsConstructor
public abstract class PagingServiceImpl<D, E, K extends Serializable>
    implements PagingService<D, E> {

  private final PagingMapper<E, D> pagingMapper;

  private final SpecificationRepository<E, K> specificationRepository;

  /**
   * * Create a pageable object for pagination and sorting.
   *
   * @param pageNo the page number to retrieve
   * @param pageSize the number of items per page
   * @param sortDir the direction to sort (ascending or descending)
   * @param sortBy the field to sort by
   * @return a pageable object for pagination and sorting
   */
  protected Pageable createPageable(
      Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
    // Validate and set default values for pagination and sorting parameters
    // Shouldn't throw exception, just set default values
    if (pageNo < 0) {
      pageNo = 0; // Default to the first page
    }
    if (pageSize <= 0) {
      pageSize = 10; // Default page size
    }
    if (pageSize > 100) {
      pageSize = 100; // Maximum page size
    }
    if (sortBy == null || sortBy.isEmpty()) {
      sortBy = "id"; // Default sort field
    }
    if (sortDir == null || sortDir.isEmpty()) {
      sortDir = "asc"; // Default sort direction
    }
    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by(Sort.Order.asc(sortBy).ignoreCase())
            : Sort.by(Sort.Order.desc(sortBy).ignoreCase());
    return PageRequest.of(pageNo, pageSize, sort);
  }

  @Override
  public PagingDtoResponse<D> getMany(Specification<E> spec, Pageable pageable) {
    if (spec != null) {
      Page<E> page = specificationRepository.findAll(spec, pageable);
      return pagingMapper.toPagingResult(page, pagingMapper::toDto);
    }
    Page<E> page = specificationRepository.findAll(pageable);
    return pagingMapper.toPagingResult(page, pagingMapper::toDto);
  }
}
