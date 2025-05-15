package com.rookie.asset_management.mapper;

/**
 * This interface defines methods for converting an entity to a DTO and vice versa.
 * @param <E> the entity type
 * @param <D> the DTO type
 */
public interface BaseMapper<E, D> {
    /**
     * Converts an entity to a DTO.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    D toDto(E entity);

    /**
     * Converts a DTO to an entity.
     *
     * @param dto the DTO to convert
     * @return the converted entity
     */
    E toEntity(D dto);
}
