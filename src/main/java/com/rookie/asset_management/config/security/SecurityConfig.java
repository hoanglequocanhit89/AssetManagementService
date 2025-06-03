package com.rookie.asset_management.config.security;

import com.rookie.asset_management.constant.AllowedOrigin;
import com.rookie.asset_management.constant.Endpoints;
import com.rookie.asset_management.constant.UserRoles;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityConfig {

  JwtAuthenticationFilter jwtAuthenticationFilter;
  LimitLoginAuthenticationProvider limitLoginAuthenticationProvider;

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    // Configure HTTP csrf and cors
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource));
    // custom filter
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    // Configure exception
    http.exceptionHandling(
        exceptionHandling -> exceptionHandling.authenticationEntryPoint(new AuthEntryPointJwt()));
    // configure authorization
    http.authorizeHttpRequests(
        auth ->
            auth.requestMatchers(Endpoints.PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(Endpoints.ADMIN_ENDPOINTS)
                .hasAuthority(UserRoles.ADMIN)
                .requestMatchers(Endpoints.STAFF_ENDPOINTS)
                .hasAuthority(UserRoles.STAFF)
                .anyRequest()
                .authenticated());
    // configure session management
    http.sessionManagement(
        manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      ApplicationEventPublisher applicationEventPublisher) {
    ProviderManager providerManager = new ProviderManager(limitLoginAuthenticationProvider);
    providerManager.setAuthenticationEventPublisher(
        authenticationEventPublisher(applicationEventPublisher));
    return providerManager;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(AllowedOrigin.ALLOWED_ORIGINS));
    configuration.setAllowedMethods(Arrays.asList(AllowedOrigin.ALLOWED_METHODS));
    configuration.setAllowedHeaders(Arrays.asList(AllowedOrigin.ALLOWED_HEADERS));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationEventPublisher authenticationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher) {
    return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
  }
}
