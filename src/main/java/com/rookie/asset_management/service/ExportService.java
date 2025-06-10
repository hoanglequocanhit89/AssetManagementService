package com.rookie.asset_management.service;

import java.util.List;

/**
 * Service interface for exporting data in the asset management system. This interface can be
 * implemented to provide export functionality for various data types, such as reports, user data,
 * or asset information.
 *
 * @param <D> the type of data to be exported
 */
public interface ExportService<D> {
  /**
   * Exports data to a specified format (e.g., CSV, Excel). The implementation should handle the
   * actual export logic.
   *
   * @return a byte array containing the exported data
   */
  byte[] exportData(List<D> data); // Method to export data in the specified format

  /**
   * Checks if the export service supports a specific type and format.
   *
   * @param type the type of data to be exported (e.g., "report", "user")
   * @param format the format to check support for (e.g., "csv", "excel")
   * @return true if the service supports the specified type and format, false otherwise
   */
  boolean supports(
      String type, String format); // Method to check if the service supports the specified format
}
