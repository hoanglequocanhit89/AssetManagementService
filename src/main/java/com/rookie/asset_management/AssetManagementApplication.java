package com.rookie.asset_management;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssetManagementApplication {

	public static void main(String[] args) {
		// Load environment variables from .env file
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		// start the application after loading the environment variables to ensure properties are set
		SpringApplication.run(AssetManagementApplication.class, args);
	}

}
