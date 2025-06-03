package com.rookie.asset_management.config.security;

import com.rookie.asset_management.constant.Endpoints;
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

    String jwt = jwtService.getJwtFromCookie(request);

    if (jwt == null || jwt.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    String endpoint = request.getRequestURI();

    if (isPublicEndpoints(endpoint)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
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
      filterChain.doFilter(request, response);
    }

    filterChain.doFilter(request, response);
  }

  private boolean isPublicEndpoints(String endpoint) {
    return Arrays.stream(Endpoints.PUBLIC_ENDPOINTS).anyMatch(endpoint::startsWith);
  }
}
