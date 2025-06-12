package com.rookie.asset_management.dto.request.asset;

import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.validation.ValidAssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAssetDtoRequest {
  @NotBlank(message = "Asset name is required")
  private String name;

  @NotBlank(message = "Specification is required")
  private String specification;

  @NotNull(message = "Installed date is required")
  private LocalDate installedDate;

  @ValidAssetStatus private AssetStatus state;
}
