package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.response.PagingDtoResponse;
import com.rookie.asset_management.dto.response.report.ReportDtoResponse;
import java.util.List;

/** Service interface for generating and managing reports in the asset management system. */
public interface ReportService {
  /**
   * Retrieves a list of all reports, optionally sorted by specified fields.
   *
   * @return a list of reports
   */
  List<ReportDtoResponse> getAllReports();

  /**
   * Retrieves a paginated list of reports with optional sorting and search functionality.
   *
   * @param page page number to retrieve (0-based index)
   * @param size number of reports per page
   * @param sortBy the field to sort by (e.g., "total", "assigned")
   * @param sortDir the direction of sorting ("asc" for ascending, "desc" for descending)
   * @return a paginated response containing a list of reports
   */
  PagingDtoResponse<ReportDtoResponse> getAllReports(
      int page, int size, String sortBy, String sortDir);
}
