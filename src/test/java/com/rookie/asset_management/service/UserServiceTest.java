package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.dto.request.UserFilterRequest;
import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.UserDtoResponse;
import com.rookie.asset_management.mapper.UserMapper;
import com.rookie.asset_management.repository.UserRepository;
import com.rookie.asset_management.service.impl.UserServiceImpl;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  @Test
  @DisplayName("Test getAllUsers with valid request")
  void testGetAllUsers() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    int adminId = 2;

    UserDtoResponse mockUserDtoResponse =
        UserDtoResponse.builder()
            .id(1)
            .firstName("firstName")
            .lastName("lastName")
            .staffCode("SD1234")
            .type("Admin")
            .build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(mockUserDtoResponse)));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(mockUserDtoResponse), 1, 1, 1, 1, false));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(adminId, userFilterRequest, 0, 10, "id", "asc");

    assertEquals(1, result.getTotalElements());
    assertEquals("firstName", result.getContent().stream().toList().getFirst().getFirstName());
  }

  @Test
  @DisplayName("Test getAllUsers with empty result without exception")
  void testGetAllUsersEmptyResult() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(), 0, 0, 0, 0, true));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(1, userFilterRequest, 0, 10, "id", "asc");

    assertEquals(0, result.getTotalElements());
    assertEquals(true, result.getEmpty());
  }

  @Test
  @DisplayName("test getAllUsers with default sorting with firstName ascending")
  void testGetAllUsersWithDefaultSorting() {
    UserFilterRequest userFilterRequest = UserFilterRequest.builder().build();

    UserDtoResponse first =
        UserDtoResponse.builder()
            .id(1)
            .firstName("A")
            .lastName("A")
            .staffCode("SD1234")
            .type("Admin")
            .build();

    UserDtoResponse second =
        UserDtoResponse.builder()
            .id(2)
            .firstName("B")
            .lastName("B")
            .staffCode("SD1234")
            .type("Admin")
            .build();

    UserDtoResponse third =
        UserDtoResponse.builder()
            .id(3)
            .firstName("C")
            .lastName("C")
            .staffCode("SD1234")
            .type("Admin")
            .build();

    when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(first, second, third)));

    when(userMapper.toPagingResult(any(PageImpl.class), any(Function.class)))
        .thenReturn(new PagingDtoResponse<>(List.of(first, second, third), 1, 3, 1, 1, false));

    PagingDtoResponse<UserDtoResponse> result =
        userService.getAllUsers(1, userFilterRequest, 0, 10, null, null);

    assertEquals(3, result.getTotalElements());
    assertEquals("A", result.getContent().stream().toList().getFirst().getFirstName());
    assertEquals("B", result.getContent().stream().toList().get(1).getFirstName());
    assertEquals("C", result.getContent().stream().toList().get(2).getFirstName());
  }
}
