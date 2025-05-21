package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Category;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Category} entities.
 *
 * <p>Provides methods for checking the existence of categories by name and prefix.
 */
@Repository
public interface CategoryRepository extends BaseRepository<Category, Integer> {
  /**
   * Checks whether a category with the given name exists (case-insensitive).
   *
   * @param name the category name to check
   * @return {@code true} if a category with the given name exists, {@code false} otherwise
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Checks whether a category with the given prefix exists (case-insensitive).
   *
   * @param prefix the category prefix to check
   * @return {@code true} if a category with the given prefix exists, {@code false} otherwise
   */
  boolean existsByPrefixIgnoreCase(String prefix);

  /**
   * find category by name
   *
   * @param name searched name
   * @return the founded category
   */
  Category findByName(String name);
}
