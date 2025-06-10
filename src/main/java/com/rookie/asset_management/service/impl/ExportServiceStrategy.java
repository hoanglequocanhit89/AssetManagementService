package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.ExportService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service for handling export operations based on the specified type and format. This service uses
 * a strategy pattern to delegate the export task to the appropriate ExportService implementation
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportServiceStrategy {
  private final List<ExportService<?>> exportServices;

  /**
   * Exports data based on the specified type and format.
   *
   * @param format the format to export the data to (e.g., "csv", "excel")
   * @param data the data to be exported, which can be of any type
   * @return a byte array containing the exported data
   */
  @SuppressWarnings("unchecked")
  public <T> byte[] export(String type, String format, List<T> data) {
    return exportServices.stream()
        .filter(exportService -> exportService.supports(type, format))
        // find the first export service that supports the given type and format
        .findFirst()
        .map(exportService -> ((ExportService<T>) exportService).exportData(data))
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "No export service found"));
  }
}
