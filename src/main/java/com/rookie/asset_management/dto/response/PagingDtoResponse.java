package com.rookie.asset_management.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * PagingDtoResponse is a generic class that represents a paginated response.
 * It contains the content of the current page, total pages, total elements, size of the page,
 * current page number, and whether the page is empty.
 *
 * @param <T> the type of the content
 */
@Builder
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
@NoArgsConstructor // Default constructor for serialization/deserialization
public class PagingDtoResponse<T> {

  private Collection<T> content;
  private Integer totalPages;
  private Long totalElements;
  private Integer size;
  private Integer page;
  private Boolean empty;

  public PagingDtoResponse(Collection<T> content, Integer totalPages, long totalElements, Integer size, Integer page, boolean empty) {
    this.content = content;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.size = size;
    this.page = page + 1;
    this.empty = empty;
  }
}
