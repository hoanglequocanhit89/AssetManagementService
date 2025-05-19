package com.rookie.asset_management.mapper;

import java.util.List;

/**
 * This interface defines methods for converting an entity to a DTO and vice versa.
 *
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

  /**
   * Converts a list of entities to a list of DTOs.
   *
   * @param entities the list of entities to convert
   * @return the list of converted DTOs
   */
  default List<D> toDtoList(List<E> entities) {
    return entities.stream().map(this::toDto).toList();
  }

  /**
   * Converts a list of DTOs to a list of entities.
   *
   * @param dtos the list of DTOs to convert
   * @return the list of converted entities
   */
  default List<E> toEntityList(List<D> dtos) {
    return dtos.stream().map(this::toEntity).toList();
  }
}
