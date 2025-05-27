package com.rookie.asset_management.controller;

import com.rookie.asset_management.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/assignments")
public class AssignmentController extends ApiV1Controller {

  @Autowired AssignmentService assignmentService;
}
