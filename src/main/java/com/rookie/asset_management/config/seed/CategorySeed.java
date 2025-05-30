package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.repository.CategoryRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@Order(4)
public class CategorySeed extends Seeder implements CommandLineRunner {

  public CategorySeed(Environment environment, CategoryRepository categoryRepository) {
    super(environment);
    this.categoryRepository = categoryRepository;
  }

  private final CategoryRepository categoryRepository;

  @Override
  public void run(String... args) throws Exception {
    if (isNotEnableSeeding()) {
      // Skip seeding if not enabled
      log.info("Category seeding is disabled in the current environment.");
      return;
    }
    if (categoryRepository.count() != 0) {
      // If categories already exist, skip seeding
      log.info("Categories already exist, skipping seeding.");
      return;
    }

    Category laptop = new Category();

    laptop.setName("Laptop");
    laptop.setPrefix("LP");

    Category phone = new Category();
    phone.setName("Phone");
    phone.setPrefix("PH");

    Category monitor = new Category();
    monitor.setName("Monitor");
    monitor.setPrefix("MN");

    categoryRepository.saveAll(List.of(laptop, phone, monitor));
    log.info("Categories seeded successfully.");
  }
}
