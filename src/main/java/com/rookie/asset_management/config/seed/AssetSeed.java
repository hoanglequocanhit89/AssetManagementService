package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Asset;
import com.rookie.asset_management.entity.Category;
import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.enums.AssetStatus;
import com.rookie.asset_management.repository.AssetRepository;
import com.rookie.asset_management.repository.CategoryRepository;
import com.rookie.asset_management.repository.LocationRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@Order(5) // Uncomment this line if you want to specify the order of execution
public class AssetSeed extends Seeder implements CommandLineRunner {

  private final AssetRepository assetRepository;
  private final CategoryRepository categoryRepository;
  private final LocationRepository locationRepository;

  public AssetSeed(
      Environment environment,
      AssetRepository assetRepository,
      CategoryRepository categoryRepository,
      LocationRepository locationRepository) {
    super(environment);
    this.assetRepository = assetRepository;
    this.categoryRepository = categoryRepository;
    this.locationRepository = locationRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (isNotEnableSeeding()) {
      // Skip seeding if not enabled
      log.info("Asset seeding is disabled in the current environment.");
      return;
    }
    if (assetRepository.count() != 0) {
      // If assets already exist, skip seeding
      log.info("Assets already exist, skipping seeding.");
      return;
    }

    Location locationDN = locationRepository.findByName("DN");
    Location locationHN = locationRepository.findByName("HN");
    Location locationHCM = locationRepository.findByName("HCM");

    List<Asset> assetDN = createAssets(locationDN);
    List<Asset> assetHN = createAssets(locationHN);
    List<Asset> assetHCM = createAssets(locationHCM);

    assetRepository.saveAll(assetDN);
    assetRepository.saveAll(assetHN);
    assetRepository.saveAll(assetHCM);

    log.info("Assets seeded successfully.");
  }

  private List<Asset> createAssets(Location location) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    Category laptop = categoryRepository.findByName("Laptop");
    Category phone = categoryRepository.findByName("Phone");
    Category monitor = categoryRepository.findByName("Monitor");

    // available
    Asset asset1 = new Asset();
    asset1.setName("Dell XPS 13");
    asset1.setCategory(laptop);
    asset1.setLocation(location);
    asset1.setSpecification("Intel Core i7, 16GB RAM, 512GB SSD");
    asset1.setInstalledDate(LocalDate.parse("01-01-2010", formatter));
    asset1.setStatus(AssetStatus.AVAILABLE);
    asset1.setAssetCode("ASSET001");

    Asset asset2 = new Asset();
    asset2.setName("iPhone 13");
    asset2.setCategory(phone);
    asset2.setLocation(location);
    asset2.setSpecification("128GB, Black");
    asset2.setInstalledDate(LocalDate.parse("15-02-2022", formatter));
    asset2.setStatus(AssetStatus.AVAILABLE);
    asset2.setAssetCode("ASSET002");

    Asset asset3 = new Asset();
    asset3.setName("LG UltraFine 5K");
    asset3.setCategory(monitor);
    asset3.setLocation(location);
    asset3.setSpecification("27-inch, 5K Retina");
    asset3.setInstalledDate(LocalDate.parse("10-10-2023", formatter));
    asset3.setStatus(AssetStatus.AVAILABLE);
    asset3.setAssetCode("ASSET003");

    // in use
    Asset asset4 = new Asset();
    asset4.setName("MacBook Pro 16");
    asset4.setCategory(laptop);
    asset4.setLocation(location);
    asset4.setSpecification("Intel Core i9, 32GB RAM, 1TB SSD");
    asset4.setInstalledDate(LocalDate.parse("01-11-2021", formatter));
    asset4.setStatus(AssetStatus.ASSIGNED);
    asset4.setAssetCode("ASSET004");

    Asset asset5 = new Asset();
    asset5.setName("Samsung Galaxy S21");
    asset5.setCategory(phone);
    asset5.setLocation(location);
    asset5.setSpecification("256GB, Phantom Gray");
    asset5.setInstalledDate(LocalDate.parse("14-02-2023", formatter));
    asset5.setStatus(AssetStatus.ASSIGNED);
    asset5.setAssetCode("ASSET005");

    Asset asset6 = new Asset();
    asset6.setName("Dell UltraSharp 27");
    asset6.setCategory(monitor);
    asset6.setLocation(location);
    asset6.setSpecification("27-inch, 4K UHD");
    asset6.setInstalledDate(LocalDate.parse("11-03-2021", formatter));
    asset6.setStatus(AssetStatus.ASSIGNED);
    asset6.setAssetCode("ASSET006");

    // not available
    Asset asset7 = new Asset();
    asset7.setName("Lenovo ThinkPad X1");
    asset7.setCategory(laptop);
    asset7.setLocation(location);
    asset7.setSpecification("Intel Core i7, 16GB RAM, 1TB SSD");
    asset7.setInstalledDate(LocalDate.parse("01-02-2023", formatter));
    asset7.setStatus(AssetStatus.NOT_AVAILABLE);
    asset7.setAssetCode("ASSET007");

    Asset asset8 = new Asset();
    asset8.setName("Google Pixel 6");
    asset8.setCategory(phone);
    asset8.setLocation(location);
    asset8.setSpecification("128GB, Sorta Seafoam");
    asset8.setInstalledDate(LocalDate.parse("18-02-2023", formatter));
    asset8.setStatus(AssetStatus.NOT_AVAILABLE);
    asset8.setAssetCode("ASSET008");

    Asset asset9 = new Asset();
    asset9.setName("BenQ PD3220U");
    asset9.setCategory(monitor);
    asset9.setLocation(location);
    asset9.setSpecification("32-inch, 4K UHD");
    asset9.setInstalledDate(LocalDate.parse("22-04-2023", formatter));
    asset9.setStatus(AssetStatus.NOT_AVAILABLE);
    asset9.setAssetCode("ASSET009");

    // recycled
    Asset asset10 = new Asset();
    asset10.setName("HP Spectre x360");
    asset10.setCategory(laptop);
    asset10.setLocation(location);
    asset10.setSpecification("Intel Core i7, 16GB RAM, 512GB SSD");
    asset10.setInstalledDate(LocalDate.parse("01-01-2023", formatter));
    asset10.setStatus(AssetStatus.RECYCLED);
    asset10.setAssetCode("ASSET010");

    Asset asset11 = new Asset();
    asset11.setName("OnePlus 9");
    asset11.setCategory(phone);
    asset11.setLocation(location);
    asset11.setSpecification("256GB, Morning Mist");
    asset11.setInstalledDate(LocalDate.parse("15-02-2023", formatter));
    asset11.setStatus(AssetStatus.RECYCLED);
    asset11.setAssetCode("ASSET011");

    Asset asset12 = new Asset();
    asset12.setName("ASUS ProArt PA32UCX");
    asset12.setCategory(monitor);
    asset12.setLocation(location);
    asset12.setSpecification("32-inch, 4K HDR");
    asset12.setInstalledDate(LocalDate.parse("10-03-2023", formatter));
    asset12.setStatus(AssetStatus.RECYCLED);
    asset12.setAssetCode("ASSET012");

    // recycling
    Asset asset13 = new Asset();
    asset13.setName("Acer Swift 3");
    asset13.setCategory(laptop);
    asset13.setLocation(location);
    asset13.setSpecification("Intel Core i5, 8GB RAM, 512GB SSD");
    asset13.setInstalledDate(LocalDate.parse("01-01-2023", formatter));
    asset13.setStatus(AssetStatus.WAITING);
    asset13.setAssetCode("ASSET013");

    Asset asset14 = new Asset();
    asset14.setName("Xiaomi Mi 11");
    asset14.setCategory(phone);
    asset14.setLocation(location);
    asset14.setSpecification("256GB, Horizon Blue");
    asset14.setInstalledDate(LocalDate.parse("15-02-2023", formatter));
    asset14.setStatus(AssetStatus.WAITING);
    asset14.setAssetCode("ASSET014");

    Asset asset15 = new Asset();
    asset15.setName("LG 34WK95U-W");
    asset15.setCategory(monitor);
    asset15.setLocation(location);
    asset15.setSpecification("34-inch, 5K UltraWide");
    asset15.setInstalledDate(LocalDate.parse("10-03-2023", formatter));
    asset15.setStatus(AssetStatus.WAITING);
    asset15.setAssetCode("ASSET015");

    return List.of(
        asset1, asset2, asset3, asset4, asset5, asset6, asset7, asset8, asset9, asset10, asset11,
        asset12, asset13, asset14, asset15);
  }
}
