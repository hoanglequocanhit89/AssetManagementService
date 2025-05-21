package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CategoryService} that provides business logic for managing categories
 * such as retrieving category names and creating new categories.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired private CategoryRepository categoryRepository;

  /**
   * Retrieves the list of all category names in the system.
   *
   * @return a list of category names
   * @throws AppException if no categories are found in the database
   */
  @Override
  public List<String> getAllCategoryNames() {
    // Fetch all categories and extract their names
    List<String> names =
        categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toList());

    // Throw exception if list is empty
    if (names.isEmpty()) {
      throw new AppException(HttpStatus.NOT_FOUND, "No categories found");
    }

    return names;
  }

  /**
   * Creates a new category with the specified name and prefix.
   *
   * @param name the name of the new category
   * @param prefix the unique prefix associated with the category
   * @return the created {@link Category} entity
   * @throws AppException if the category name or prefix already exists
   */
  @Override
  public Category createCategory(String name, String prefix) {
    // Validate unique name
    if (categoryRepository.existsByNameIgnoreCase(name)) {
      throw new AppException(
          HttpStatus.CONFLICT, "Category is already existed. Please enter a different category");
    }

    // Validate unique prefix
    if (categoryRepository.existsByPrefixIgnoreCase(prefix)) {
      throw new AppException(
          HttpStatus.CONFLICT, "Prefix is already existed. Please enter a different prefix");
    }

    // Create and populate the category entity
    Category category = new Category();
    category.setName(name);
    category.setPrefix(prefix.toUpperCase());

    // Save to database and return
    return categoryRepository.save(category);
  }
}
