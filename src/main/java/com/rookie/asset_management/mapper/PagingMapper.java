package com.rookie.asset_management.mapper;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.function.Function;

/**
 * Mapper interface for converting between Page E and PagingRes D.
 * This interface provides a method to convert a Page D to a PagingRes D using a mapper function.
 * Should extend this interface to convert a Page E to a PagingRes D.
 */
@Mapper(componentModel = "spring")
public interface PagingMapper {

    /**
     * Converts a Page E to a PagingRes D using a mapper function.<br>
     * E is the type of the entity in the page.
     * D is the type of the DTO.
     *
     * @param pages  the page of entities
     * @param mapper function to map each entity to DTO
     * @return the PagingRes of DTOs
     */
    default <E, D> PagingDtoResponse<D> toPagingResult(Page<E> pages, Function<E, D> mapper) {
        Collection<D> dtoList = pages.getContent().stream()
            .map(mapper)
            .toList();

        return new PagingDtoResponse<>(
            dtoList,
            pages.getTotalPages(),
            pages.getTotalElements(),
            pages.getSize(),
            pages.getNumber(),
            pages.isEmpty()
        );
    }
}
