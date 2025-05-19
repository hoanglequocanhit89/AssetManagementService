package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.service.ExampleService;

import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl implements ExampleService {
  // declare any necessary dependencies here (e.g., repositories, mappers, etc.)
  @Override
  public String exampleMethod() {
    return "Hello from ExampleServiceImpl!";
  }
  // implement other service methods, and private methods as needed
}
