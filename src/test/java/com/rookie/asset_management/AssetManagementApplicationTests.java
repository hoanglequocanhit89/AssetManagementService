package com.rookie.asset_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
// use local postgres database for testing
@TestPropertySource(properties = {
	"POSTGRES_URL=jdbc:postgresql://localhost:5432/asset_management",
	"POSTGRES_USER=postgres",
	"POSTGRES_PASSWORD=postgres",
	"SERVER_PORT=8081"
})
class AssetManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
