package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.service.AssignmentService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServiceImpl implements AssignmentService {
  AssignmentRepository assignmentRepository;

  public AssignmentServiceImpl(AssignmentRepository assignmentRepository) {
    this.assignmentRepository = assignmentRepository;
  }
}
