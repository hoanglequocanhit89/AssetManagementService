package com.rookie.asset_management.controller;

import com.rookie.asset_management.dto.request.CreateCategoryRequest;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing categories in the asset management system. Provides endpoints for
 * retrieving category names and creating new categories.
 */
@RestController
@RequestMapping("api/v1/category")
public class CategoryController {

  @Autowired CategoryService categoryService;

  /**
   * Retrieves the list of all existing category names.
   *
   * @return a list of category names
   */
  @GetMapping
  public List<String> getCategoryNames() {
    return categoryService.getAllCategoryNames();
  }

  /**
   * Creates a new category with a unique name and prefix.
   *
   * @param request the request body containing the new category's name and prefix
   * @return a ResponseEntity containing the created category and HTTP status 201 (CREATED)
   */
  @PostMapping
  public ResponseEntity<Category> createCategory(@RequestBody CreateCategoryRequest request) {
    Category category = categoryService.createCategory(request.getName(), request.getPrefix());
    return new ResponseEntity<>(category, HttpStatus.CREATED);
  }
}
