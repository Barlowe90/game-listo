package com.gamelisto.api_gateway.security;

import com.gamelisto.api_gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtValidator - Validación de tokens JWT")
class JwtValidatorTest {

  private JwtValidator jwtValidator;
  private SecretKey secretKey;
  private static final String TEST_SECRET =
      "test-secret-key-must-be-at-least-256-bits-long-for-hs256";

  @BeforeEach
  void setUp() {
    JwtProperties properties = new JwtProperties();
    properties.setSecret(TEST_SECRET);
    properties.setExpiration(900000L); // 15 minutos

    jwtValidator = new JwtValidator(properties);
    secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  @DisplayName("Debe validar correctamente un token JWT válido")
  void debeValidarTokenValido() {
    // Arrange
    String userId = UUID.randomUUID().toString();
    String username = "testuser";
    String email = "test@example.com";
    List<String> roles = List.of("USER");
    String jti = UUID.randomUUID().toString();

    String token = createValidToken(userId, username, email, roles, jti);

    // Act
    Claims claims = jwtValidator.validateToken(token);

    // Assert
    assertThat(claims).isNotNull();
    assertThat(jwtValidator.getUserId(claims)).isEqualTo(userId);
    assertThat(jwtValidator.getUsername(claims)).isEqualTo(username);
    assertThat(jwtValidator.getEmail(claims)).isEqualTo(email);
    assertThat(jwtValidator.getRoles(claims)).containsExactlyElementsOf(roles);
    assertThat(jwtValidator.getJti(claims)).isEqualTo(jti);
    assertThat(jwtValidator.isTokenExpired(claims)).isFalse();
  }

  @Test
  @DisplayName("Debe rechazar un token expirado")
  void debeRechazarTokenExpirado() {
    // Arrange
    String token = createExpiredToken();

    // Act & Assert
    assertThatThrownBy(() -> jwtValidator.validateToken(token))
        .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
  }

  @Test
  @DisplayName("Debe rechazar un token con firma inválida")
  void debeRechazarTokenConFirmaInvalida() {
    // Arrange
    String token = createTokenWithInvalidSignature();

    // Act & Assert
    assertThatThrownBy(() -> jwtValidator.validateToken(token))
        .isInstanceOf(io.jsonwebtoken.security.SignatureException.class);
  }

  @Test
  @DisplayName("Debe detectar token expirado mediante isTokenExpired")
  void debeDetectarTokenExpirado() {
    // Arrange
    String userId = UUID.randomUUID().toString();
    Instant expiration = Instant.now().minus(1, ChronoUnit.HOURS);

    Claims claims = Jwts.claims().subject(userId).expiration(Date.from(expiration)).build();

    // Act & Assert
    assertThat(jwtValidator.isTokenExpired(claims)).isTrue();
  }

  @Test
  @DisplayName("Debe extraer múltiples roles correctamente")
  void debeExtraerMultiplesRoles() {
    // Arrange
    List<String> roles = List.of("USER", "ADMIN", "MODERATOR");
    String token =
        createValidToken(
            UUID.randomUUID().toString(),
            "adminuser",
            "admin@example.com",
            roles,
            UUID.randomUUID().toString());

    // Act
    Claims claims = jwtValidator.validateToken(token);
    List<String> extractedRoles = jwtValidator.getRoles(claims);

    // Assert
    assertThat(extractedRoles).containsExactlyElementsOf(roles);
  }

  // Helper methods

  private String createValidToken(
      String userId, String username, String email, List<String> roles, String jti) {
    Instant now = Instant.now();
    Instant expiration = now.plus(15, ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(userId)
        .claim("username", username)
        .claim("email", email)
        .claim("roles", roles)
        .id(jti)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .signWith(secretKey)
        .compact();
  }

  private String createExpiredToken() {
    Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
    Instant expiration = past.plus(15, ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(UUID.randomUUID().toString())
        .claim("username", "expireduser")
        .claim("email", "expired@example.com")
        .claim("roles", List.of("USER"))
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(past))
        .expiration(Date.from(expiration))
        .signWith(secretKey)
        .compact();
  }

  private String createTokenWithInvalidSignature() {
    SecretKey wrongKey =
        Keys.hmacShaKeyFor(
            "wrong-secret-key-must-be-at-least-256-bits-long-for-hs256"
                .getBytes(StandardCharsets.UTF_8));

    Instant now = Instant.now();
    Instant expiration = now.plus(15, ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(UUID.randomUUID().toString())
        .claim("username", "testuser")
        .claim("email", "test@example.com")
        .claim("roles", List.of("USER"))
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .signWith(wrongKey)
        .compact();
  }
}
