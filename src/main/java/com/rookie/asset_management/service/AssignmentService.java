package com.rookie.asset_management.service;

import com.rookie.asset_management.dto.request.assignment.CreateUpdateAssignmentRequest;
import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;

public interface AssignmentService {

  AssignmentListDtoResponse createAssignment(CreateUpdateAssignmentRequest request);
}
