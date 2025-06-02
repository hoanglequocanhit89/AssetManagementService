package com.rookie.asset_management.service;

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
}
