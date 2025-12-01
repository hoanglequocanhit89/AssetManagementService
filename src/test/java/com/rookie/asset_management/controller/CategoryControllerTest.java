package com.rookie.asset_management.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.request.CreateCategoryRequest;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.CategoryDtoResponse;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock private CategoryService categoryService;

  @InjectMocks private CategoryController categoryController;

  @Nested
  @DisplayName("getAllCategory Tests")
  class GetAllCategoryTest {

    @Test
    @DisplayName("should return all categories successfully")
    void getAllCategory_Success_ReturnsCategories() {
      CategoryDtoResponse category1 =
          CategoryDtoResponse.builder().id(1).name("Laptop").prefix("LA").build();
      CategoryDtoResponse category2 =
          CategoryDtoResponse.builder().id(2).name("Monitor").prefix("MO").build();

      when(categoryService.getAllCategory()).thenReturn(List.of(category1, category2));

      ResponseEntity<ApiDtoResponse<List<CategoryDtoResponse>>> response =
          categoryController.getAllCategory();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Category names retrieved successfully.", response.getBody().getMessage());
      assertEquals(2, response.getBody().getData().size());
      verify(categoryService).getAllCategory();
    }
  }

  @Nested
  @DisplayName("createCategory Tests")
  class CreateCategoryTest {

    @Test
    @DisplayName("should create category successfully")
    void createCategory_Success_ReturnsCreated() {
      CreateCategoryRequest request =
          CreateCategoryRequest.builder().name("Keyboard").prefix("KB").build();

      Category createdCategory = new Category();
      createdCategory.setId(3);
      createdCategory.setName("Keyboard");
      createdCategory.setPrefix("KB");

      when(categoryService.createCategory("Keyboard", "KB")).thenReturn(createdCategory);

      ResponseEntity<ApiDtoResponse<CategoryDtoResponse>> response =
          categoryController.createCategory(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Category created successfully.", response.getBody().getMessage());
      assertEquals("Keyboard", response.getBody().getData().getName());
      assertEquals("KB", response.getBody().getData().getPrefix());
      verify(categoryService).createCategory("Keyboard", "KB");
    }
  }
}
