package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.rookie.asset_management.dto.response.CategoryDtoResponse;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.service.impl.CategoryServiceImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private CategoryServiceImpl categoryService;

  private Category category1;
  private Category category2;

  @BeforeEach
  public void setUp() {
    category1 = new Category();
    category1.setId(1);
    category1.setName("Category 1");

    category2 = new Category();
    category2.setId(2);
    category2.setName("Category 2");
  }

  @Test
  public void testGetAllCategory_returnsAllCategories() {
    // Arrange
    when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

    // Act
    List<CategoryDtoResponse> result = categoryService.getAllCategory();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Category 1", result.get(0).getName());
    assertEquals("Category 2", result.get(1).getName());
    verify(categoryRepository).findAll();
  }

  @Test
  public void testGetAllCategory_returnsEmptyList() {
    // Arrange when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

    // Act
    List<CategoryDtoResponse> result = categoryService.getAllCategory();

    // Assert
    assertTrue(result.isEmpty());
    verify(categoryRepository).findAll();
  }

  @Test
  public void testGetAllCategory_handlesNullCategoryNames() {
    // Arrange
    Category categoryWithNullName = new Category();
    categoryWithNullName.setId(3);
    categoryWithNullName.setName(null);

    when(categoryRepository.findAll()).thenReturn(Collections.singletonList(categoryWithNullName));

    // Act
    List<CategoryDtoResponse> result = categoryService.getAllCategory();

    // Assert
    assertEquals(1, result.size());
    assertEquals(null, result.get(0).getName());
    verify(categoryRepository).findAll();
  }

  @Test
  public void createCategory_ShouldCreateCategory_WhenNameAndPrefixAreUnique() {
    String name = "UniqueCategory";
    String prefix = "UC";

    when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(false);
    when(categoryRepository.existsByPrefixIgnoreCase(prefix)).thenReturn(false);
    Category expectedCategory = new Category();
    expectedCategory.setName(name);
    expectedCategory.setPrefix(prefix.toUpperCase());
    when(categoryRepository.save(any(Category.class))).thenReturn(expectedCategory);

    Category result = categoryService.createCategory(name, prefix);

    assertNotNull(result);
    assertEquals(name, result.getName());
    assertEquals(prefix.toUpperCase(), result.getPrefix());
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  public void createCategory_ShouldThrowException_WhenNameAlreadyExists() {
    String name = "ExistingCategory";
    String prefix = "EC";

    when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(true);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              categoryService.createCategory(name, prefix);
            });

    assertEquals(
        "Category is already existed. Please enter a different category", exception.getMessage());
    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  public void createCategory_ShouldThrowException_WhenPrefixAlreadyExists() {
    String name = "NewCategory";
    String prefix = "ExistingPrefix";

    when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(false);
    when(categoryRepository.existsByPrefixIgnoreCase(prefix)).thenReturn(true);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              categoryService.createCategory(name, prefix);
            });

    assertEquals(
        "Prefix is already existed. Please enter a different prefix", exception.getMessage());
    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  public void createCategory_ShouldThrowException_WhenNameAndPrefixBothExist() {
    String name = "ExistingCategory";
    String prefix = "ExistingPrefix";

    when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(true);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              categoryService.createCategory(name, prefix);
            });

    assertEquals(
        "Category is already existed. Please enter a different category", exception.getMessage());
    verify(categoryRepository, never()).save(any(Category.class));
  }
}
