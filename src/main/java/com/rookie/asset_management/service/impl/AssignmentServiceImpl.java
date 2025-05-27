package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.dto.response.assignment.AssignmentListDtoResponse;
import com.rookie.asset_management.entity.Assignment;
import com.rookie.asset_management.mapper.AssignmentMapper;
import com.rookie.asset_management.repository.AssignmentRepository;
import com.rookie.asset_management.service.AssignmentService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentServiceImpl
    extends PagingServiceImpl<AssignmentListDtoResponse, Assignment, Integer>
    implements AssignmentService {
  AssignmentRepository assignmentRepository;
  AssignmentMapper assignmentMapper;

  @Autowired
  public AssignmentServiceImpl(
      AssignmentRepository assignmentRepository, AssignmentMapper assignmentMapper) {
    super(assignmentMapper, assignmentRepository);
    this.assignmentMapper = assignmentMapper;
    this.assignmentRepository = assignmentRepository;
  }
}
