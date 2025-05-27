package com.rookie.asset_management.controller;

import com.rookie.asset_management.service.AssignmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/assignments")
public class AssignmentController extends ApiV1Controller {

  AssignmentService assignmentService;

  public AssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }
}
