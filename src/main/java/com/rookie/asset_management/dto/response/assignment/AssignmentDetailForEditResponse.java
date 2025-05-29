package com.rookie.asset_management.dto.response.assignment;

import com.rookie.asset_management.dto.response.asset.AssetBriefDtoResponse;
import com.rookie.asset_management.dto.response.user.UserBriefDtoResponse;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentDetailForEditResponse {

  UserBriefDtoResponse user;

  AssetBriefDtoResponse asset;

  LocalDate assignedDate;

  String note;
}
