package com.rookie.asset_management.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.rookie.asset_management.service.LoginAttemptService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
  public static final int MAX_ATTEMPT = 5; // Maximum number of login attempts allowed
  private final Cache<String, Integer> attemptsCache;
  private final Cache<String, Long> lockoutCache;
  private static final int ATTEMPT_DURATION = 24; // Duration for which attempts are tracked (hour)
  private static final int LOCKOUT_DURATION = 1; // Duration for which IPs are blocked (hour)

  public LoginAttemptServiceImpl() {
    super();
    // Initialize the cache with a maximum of 5 attempts per IP address
    // The cache will expire entries after 24 hours of inactivity
    this.attemptsCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(ATTEMPT_DURATION, TimeUnit.HOURS)
            .maximumSize(1000) // Limit the cache size to 1000 entries
            .build(
                new CacheLoader<>() {
                  @Override
                  public Integer load(final String key) {
                    return 0; // Default value for new keys
                  }
                });
    // Initialize the lockout cache to track blocked IP addresses
    // The lockout cache will expire entries after 1 hour of inactivity
    this.lockoutCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(LOCKOUT_DURATION, TimeUnit.HOURS)
            .build(
                new CacheLoader<>() {
                  @Override
                  public Long load(final String key) {
                    return 0L; // Default value for new keys
                  }
                });
  }

  @Override
  public void loginFailed(final String key) {
    int attempts = attemptsCache.asMap().getOrDefault(key, 0);
    attempts++;
    attemptsCache.put(key, attempts);

    if (attempts >= MAX_ATTEMPT) {
      lockoutCache.put(key, System.currentTimeMillis());
    }
  }

  @Override
  public void loginSucceeded(String key) {
    attemptsCache.invalidate(key);
    lockoutCache.invalidate(key);
  }

  @Override
  public boolean isBlocked(String key) {
    return lockoutCache.getIfPresent(key) != null;
  }
}
