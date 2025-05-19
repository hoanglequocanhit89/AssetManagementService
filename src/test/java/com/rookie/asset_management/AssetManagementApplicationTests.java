package com.rookie.asset_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
// use local postgres database for testing
@ActiveProfiles("test")
class AssetManagementApplicationTests {

  @Test
  void contextLoads() {
  }

}
