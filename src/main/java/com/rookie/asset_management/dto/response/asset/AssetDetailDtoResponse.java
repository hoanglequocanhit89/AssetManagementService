package com.rookie.asset_management.dto.response.asset;

import com.rookie.asset_management.dto.response.assignment.AssignmentDtoResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDetailDtoResponse extends AssetDtoResponse {
  private String specification;
  private LocalDate installedDate;
  private String location;
  private List<AssignmentDtoResponse> assignments;
}
