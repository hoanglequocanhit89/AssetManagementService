package com.rookie.asset_management.service.impl;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret}")
  String secret;

  @Value("${jwt.expiration}")
  Long jwtExpiresMinutes;

  Claims claims;

  @Override
  public void generateToken(String username, HttpServletResponse response) {
    String jwt =
        Jwts.builder()
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiresMinutes * 60 * 1000))
            .signWith(getSignInKey())
            .compact();

    Cookie cookie = new Cookie("JWT", jwt);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60);

    String cookieValue =
        String.format(
            "%s=%s; Max-Age=%d; Path=%s; Secure; HttpOnly; SameSite=None",
            cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getPath());

    response.addHeader("Set-Cookie", cookieValue);
  }

  @Override
  public String getJwtFromCookie(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, "JWT");
    if (cookie != null) {
      return cookie.getValue();
    }
    return null;
  }

  @Override
  public void validateToken(String token) throws JwtException {
    try {
      claims =
          Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();

    } catch (JwtException e) {
      // catch null, wrong token, expired token
      throw new AppException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  @Override
  public void removeTokenFromCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("JWT", null);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  @Override
  public SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String extractUsername() {
    return claims.getSubject();
  }
}
