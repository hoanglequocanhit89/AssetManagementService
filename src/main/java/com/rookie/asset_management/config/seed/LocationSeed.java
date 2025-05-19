package com.rookie.asset_management.config.seed;

import com.rookie.asset_management.entity.Location;
import com.rookie.asset_management.repository.LocationRepository;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Slf4j
@Configuration
@Order(1)
@RequiredArgsConstructor
public class LocationSeed implements CommandLineRunner {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if locations already exist
        if (locationRepository.count() != 0) {
            // If locations already exist, skip seeding
            log.info("Locations already exist, skipping seeding.");
            return;
        }
        // Create locations
        Location location1 = new Location();
        location1.setName("DN");
        Location location2 = new Location();
        location2.setName("HN");
        Location location3 = new Location();
        location3.setName("HCM");
        // Save locations
        locationRepository.saveAll(List.of(location1, location2, location3));
        log.info("Locations seeded successfully.");
    }
}
