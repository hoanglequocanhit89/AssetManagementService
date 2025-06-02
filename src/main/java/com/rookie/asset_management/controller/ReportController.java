package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.response.ApiDtoResponse;
import com.rookie.asset_management.dto.response.report.ReportDtoResponse;
import com.rookie.asset_management.service.ReportService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.V1 + "/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
  ReportService reportService;

  @GetMapping
  public ResponseEntity<ApiDtoResponse<List<ReportDtoResponse>>> getAllReports() {
    List<ReportDtoResponse> reports = reportService.getAllReports();
    ApiDtoResponse<List<ReportDtoResponse>> response =
        ApiDtoResponse.<List<ReportDtoResponse>>builder()
            .message("Reports retrieved successfully")
            .data(reports)
            .build();
    return ResponseEntity.ok(response);
  }
}
