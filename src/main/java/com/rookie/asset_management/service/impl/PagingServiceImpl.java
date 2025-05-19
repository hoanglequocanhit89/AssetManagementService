package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.repository.SpecificationRepository;
import com.rookie.asset_management.service.PagingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Abstract implementation of PagingService that provides common pagination and sorting functionality.
 *
 * @param <T> the type of the DTO
 * @param <E> the type of the entity
 * @param <K> the type of the entity's identifier
 */
public abstract class PagingServiceImpl<T, E, K extends Serializable> implements PagingService<T, E> {

  /**
   * Get the repository for the entity.
   * This method should be implemented by subclasses to return the specific repository.
   *
   * @return the repository
   */
  protected abstract SpecificationRepository<E, K> getRepository();

  /**
   * Convert an entity to a DTO.
   *
   * @param entity the entity to convert
   * @return the DTO
   */
  protected abstract T convertToDto(E entity);

  /**
   * Convert a page of entities to a page of DTOs.
   *
   * @param page      the page of entities
   * @param converter the function to convert an entity to a DTO
   * @return a paginated response containing the DTOs
   */
  protected abstract PagingDtoResponse<T> toPagingResult(Page<E> page, Function<E, T> converter);


  /**
   * * Create a pageable object for pagination and sorting.
   *
   * @param pageNo   the page number to retrieve
   * @param pageSize the number of items per page
   * @param sortDir  the direction to sort (ascending or descending)
   * @param sortBy   the field to sort by
   * @return a pageable object for pagination and sorting
   */
  protected Pageable createPageable(Integer pageNo, Integer pageSize, String sortDir, String sortBy) {
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
    Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
    return PageRequest.of(pageNo, pageSize, sort);
  }

  @Override
  public PagingDtoResponse<T> getMany(Specification<E> spec, Pageable pageable) {
    if (spec != null) {
      Page<E> page = getRepository().findAll(spec, pageable);
      return toPagingResult(page, this::convertToDto);
    }
    Page<E> page = getRepository().findAll(pageable);
    return toPagingResult(page, this::convertToDto);
  }

}
