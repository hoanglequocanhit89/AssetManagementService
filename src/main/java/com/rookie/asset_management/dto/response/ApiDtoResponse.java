package com.rookie.asset_management.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * The ApiDtoResponse class is a generic class that represents a standard response structure for API responses.
 * @param <T> the type of the data being returned in the response
 */
@Builder
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
public class ApiDtoResponse<T> {
    private String message;
    private T data;
}
