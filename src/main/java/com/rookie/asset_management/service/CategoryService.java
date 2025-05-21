package com.rookie.asset_management.service;

import com.rookie.asset_management.entity.Category;
import java.util.List;

/**
 * Service interface for managing categories. Defines operations related to retrieving and creating
 * categories.
 */
public interface CategoryService {
  /**
   * Retrieves the names of all categories available in the system.
   *
   * @return a list of category names
   */
  List<String> getAllCategoryNames();

  /**
   * Creates a new category with the given name and prefix.
   *
   * @param name the name of the category
   * @param prefix the unique prefix for the category (used for asset code generation)
   * @return the created {@link Category} entity
   */
  Category createCategory(String name, String prefix);
}
