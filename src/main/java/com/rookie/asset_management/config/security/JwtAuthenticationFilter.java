package com.rookie.asset_management.config.security;

import com.rookie.asset_management.constant.Endpoints;
import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.CustomUserDetailsService;
import com.rookie.asset_management.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  JwtService jwtService;
  CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Skip JWT validation for public endpoints
    if (isPublicEndpoint(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String jwt = jwtService.getJwtFromCookie(request);
      jwtService.validateToken(jwt);
      String username = jwtService.extractUsername();

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authToken);
      SecurityContextHolder.setContext(context);

    } catch (Exception e) {
      throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private boolean isPublicEndpoint(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    return Arrays.stream(Endpoints.PUBLIC_ENDPOINTS)
        .anyMatch(
            endpoint ->
                requestURI.startsWith(endpoint.replace("/**", ""))
                    || requestURI.matches(endpoint.replace("**", ".*")));
  }
}
