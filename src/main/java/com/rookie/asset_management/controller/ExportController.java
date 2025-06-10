package com.rookie.asset_management.controller;

import com.rookie.asset_management.constant.ApiPaths;
import com.rookie.asset_management.dto.response.report.CategoryReportDtoResponse;
import com.rookie.asset_management.service.ReportService;
import com.rookie.asset_management.service.impl.ExportServiceStrategy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.V1 + "/exports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportController {
  ExportServiceStrategy exportServiceStrategy;
  ReportService reportService;

  // add any other required services here to handle different export formats

  @GetMapping("/categories/xlsx")
  public ResponseEntity<byte[]> exportCategoriesToXlsx(
      @RequestParam(required = false) String timestamp) {
    // Use the provided timestamp or fallback to the server's current time
    String formattedTimestamp;
    if (timestamp != null && !timestamp.isEmpty()) {
      formattedTimestamp = timestamp.replace(":", "-").replace(" ", "_"); // Sanitize input
    } else {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
      formattedTimestamp = dateFormat.format(new Date());
    }
    // Initialize the export service strategy with the specific exporter for categories
    List<CategoryReportDtoResponse> data = reportService.getAllReports();
    byte[] excelContent = exportServiceStrategy.export("category", "excel", data);
    String filename = "data_report_" + formattedTimestamp + ".xlsx";
    return createExcelResponse(excelContent, filename);
  }

  private ResponseEntity<byte[]> createExcelResponse(byte[] content, String filename) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(
        MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    headers.setContentDispositionFormData("attachment", filename);
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

    return ResponseEntity.ok().headers(headers).body(content);
  }
}
