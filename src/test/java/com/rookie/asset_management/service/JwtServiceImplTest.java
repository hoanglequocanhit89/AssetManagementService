package com.rookie.asset_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rookie.asset_management.exception.AppException;
import com.rookie.asset_management.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

  @InjectMocks private JwtServiceImpl jwtService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  private static final String SECRET = "dGhpc2lzYXNlY3JldGtleWZvcmp3dHRlc3RpbmcxMjM0NTY3ODkw"; // Base64 encoded
  private static final Long JWT_EXPIRES_MINUTES = 60L; // 60 minutes
  private static final String USERNAME = "testUser";
  private SecretKey secretKey;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    // Set the secret and jwtExpiresMinutes using reflection since they are injected via @Value
    setField(jwtService, "secret", SECRET);
    setField(jwtService, "jwtExpiresMinutes", JWT_EXPIRES_MINUTES);

    // Generate the secret key
    secretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(SECRET));
  }

  @Test
  void generateToken_shouldGenerateTokenAndSetCookieInResponse() {
    // Act
    jwtService.generateToken(USERNAME, response);

    // Assert
    verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
  }

  @Test
  void getJwtFromCookie_shouldReturnTokenWhenCookieExists() {
    // Arrange
    String token = "sampleToken";
    Cookie jwtCookie = new Cookie("JWT", token);
    when(request.getCookies()).thenReturn(new Cookie[] {jwtCookie});

    // Act
    String result = jwtService.getJwtFromCookie(request);

    // Assert
    assertEquals(token, result);
    verify(request, times(1)).getCookies();
  }

  @Test
  void getJwtFromCookie_shouldReturnNullWhenCookieDoesNotExist() {
    // Arrange
    when(request.getCookies()).thenReturn(new Cookie[] {});

    // Act
    String result = jwtService.getJwtFromCookie(request);

    // Assert
    assertNull(result);
    verify(request, times(1)).getCookies();
  }

  @Test
  void validateToken_shouldPassForValidToken() {
    // Arrange
    String token =
        Jwts.builder()
            .subject(USERNAME)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRES_MINUTES * 60 * 1000))
            .signWith(secretKey)
            .compact();

    // Act
    jwtService.validateToken(token);

    // Assert
    assertEquals(USERNAME, jwtService.extractUsername());
  }

  @Test
  void validateToken_shouldThrowExceptionForInvalidToken() {
    // Arrange
    String invalidToken = "invalidToken";

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> jwtService.validateToken(invalidToken));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatusCode());
  }

  @Test
  void validateToken_shouldThrowExceptionForExpiredToken() {
    // Arrange
    String expiredToken =
        Jwts.builder()
            .subject(USERNAME)
            .issuedAt(new Date(System.currentTimeMillis() - 2 * JWT_EXPIRES_MINUTES * 60 * 1000))
            .expiration(new Date(System.currentTimeMillis() - JWT_EXPIRES_MINUTES * 60 * 1000))
            .signWith(secretKey)
            .compact();

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> jwtService.validateToken(expiredToken));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatusCode());
    assertTrue(exception.getMessage().contains("JWT expired"));
  }

  @Test
  void removeTokenFromCookie_shouldClearCookieInResponse() {
    // Act
    jwtService.removeTokenFromCookie(response);

    // Assert
    verify(response, times(1)).addCookie(any(Cookie.class));
  }

  @Test
  void getSignInKey_shouldReturnValidSecretKey() {
    // Act
    SecretKey result = jwtService.getSignInKey();

    // Assert
    assertNotNull(result);
    assertEquals(secretKey, result);
  }

  @Test
  void extractUsername_shouldReturnUsernameFromClaims() throws NoSuchFieldException, IllegalAccessException {
    // Arrange
    setField(jwtService, "claims", null); // Đảm bảo claims ban đầu là null
    String token =
        Jwts.builder()
            .subject(USERNAME)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRES_MINUTES * 60 * 1000))
            .signWith(secretKey)
            .compact();
    jwtService.validateToken(token);

    // Act
    String result = jwtService.extractUsername();

    // Assert
    assertEquals(USERNAME, result);
  }

  @Test
  void extractUsername_shouldThrowExceptionWhenClaimsNotSet() throws NoSuchFieldException, IllegalAccessException {
    // Arrange
    setField(jwtService, "claims", null);

    // Act & Assert
    assertThrows(NullPointerException.class, () -> jwtService.extractUsername());
  }

  // Helper method to set private fields using reflection
  private void setField(Object target, String fieldName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
