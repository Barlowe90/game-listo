package com.gamelisto.api_gateway.filters;

import com.gamelisto.api_gateway.config.JwtProperties;
import com.gamelisto.api_gateway.security.JwtValidator;
import com.gamelisto.api_gateway.security.TokenRevocationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - Filtro de autenticación JWT")
class JwtAuthenticationFilterTest {

  public static final String URL = "http://localhost:8090/v1/usuarios/perfil";
  public static final String HMAC_SHA_256 = "HmacSHA256";
  public static final String USERNAME = "username";
  public static final String EMAIL = "email";
  public static final String ROLES = "roles";
  @Mock private TokenRevocationService tokenRevocationService;
  @Mock private GatewayFilterChain chain;

  private JwtAuthenticationFilter filter;
  private JwtValidator jwtValidator;
  private String secret;

  @BeforeEach
  void setUp() {
    secret = "test-secret-key-must-be-at-least-256-bits-long-for-hs256";
    JwtProperties jwtProperties = new JwtProperties();
    jwtProperties.setSecret(secret);
    jwtProperties.setExpiration(900000L); // 15 min

    jwtValidator = new JwtValidator(jwtProperties);
    filter = new JwtAuthenticationFilter(jwtValidator, tokenRevocationService);
  }

  @Test
  @DisplayName("Debe permitir acceso a rutas públicas sin token (login)")
  void debePermitirRutasPublicasSinTokenLogin() {
    // Arrange
    MockServerHttpRequest request =
        MockServerHttpRequest.get("http://localhost:8090/v1/usuarios/auth/login").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
    verify(tokenRevocationService, never()).isTokenRevoked(any());
  }

  @Test
  @DisplayName("Debe permitir acceso a rutas públicas sin token (registro)")
  void debePermitirRutasPublicasSinTokenRegister() {
    // Arrange
    MockServerHttpRequest request =
        MockServerHttpRequest.get("http://localhost:8090/v1/usuarios/auth/register").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
  }

  @Test
  @DisplayName("Debe permitir acceso a rutas públicas sin token (health)")
  void debePermitirRutasPublicasSinTokenHealth() {
    // Arrange
    MockServerHttpRequest request =
        MockServerHttpRequest.get("http://localhost:8090/actuator/health").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
  }

  @Test
  @DisplayName("Debe bloquear rutas protegidas sin token (401 Unauthorized)")
  void debeBloquerRutasProtegidasSinToken() {
    // Arrange
    MockServerHttpRequest request = MockServerHttpRequest.get(URL).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe bloquear rutas protegidas con token inválido (401)")
  void debeBloquerRutasProtegidasConTokenInvalido() {
    // Arrange
    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido-xyz")
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe bloquear rutas protegidas con token expirado (401)")
  void debeBloquerRutasProtegidasConTokenExpirado() {
    // Arrange
    String expiredToken = generateExpiredToken();
    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe permitir acceso con token válido y no revocado")
  void debePermitirAccesoConTokenValido() {
    // Arrange
    String jti = "test-jti-123";
    String token = generateValidToken(jti);

    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(tokenRevocationService.isTokenRevoked(jti)).thenReturn(Mono.just(false));
    when(chain.filter(any())).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(tokenRevocationService, times(1)).isTokenRevoked(jti);
    verify(chain, times(1)).filter(any());
  }

  @Test
  @DisplayName("Debe enriquecer request con headers X-User-* cuando token es válido")
  void debeEnriquecerRequestConHeaders() {
    // Arrange
    String jti = "test-jti-456";
    String userId = "user-123";
    String username = "testuser";
    String email = "test@example.com";
    String token = generateValidTokenWithClaims(jti, userId, username, email, List.of("USER"));

    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(tokenRevocationService.isTokenRevoked(jti)).thenReturn(Mono.just(false));
    when(chain.filter(any()))
        .thenAnswer(
            invocation -> {
              ServerWebExchange enrichedExchange = invocation.getArgument(0);
              ServerHttpRequest enrichedRequest = enrichedExchange.getRequest();

              // Verificar headers enriquecidos
              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Id")).isEqualTo(userId);
              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Username"))
                  .isEqualTo(username);
              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Email")).isEqualTo(email);
              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Roles")).isEqualTo("USER");

              return Mono.empty();
            });

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(any());
  }

  @Test
  @DisplayName("Debe validar firma del token correctamente")
  void debeValidarFirmaDelToken() {
    // Arrange
    String jti = "test-jti-789";
    String tokenWithWrongSecret = generateTokenWithWrongSecret(jti);

    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenWithWrongSecret)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe manejar tokens revocados (integración con TokenRevocationService)")
  void debeManejarTokensRevocados() {
    // Arrange
    String jti = "revoked-jti-999";
    String token = generateValidToken(jti);

    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(tokenRevocationService.isTokenRevoked(jti)).thenReturn(Mono.just(true));

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(tokenRevocationService, times(1)).isTokenRevoked(jti);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe bloquear peticiones sin prefijo 'Bearer '")
  void debeBloquerPeticionesSinPrefijoBarer() {
    // Arrange
    String token = generateValidToken("jti-test");
    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL)
            .header(HttpHeaders.AUTHORIZATION, token) // Sin prefijo Bearer
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe tener orden de ejecución -100")
  void debeTenerOrdenMenosCien() {
    assertThat(filter.getOrder()).isEqualTo(-100);
  }

  // ==================== Métodos Auxiliares ====================

  private String generateValidToken(String jti) {
    return generateValidTokenWithClaims(
        jti, "user-123", "testuser", "test@example.com", List.of("USER"));
  }

  private String generateValidTokenWithClaims(
      String jti, String userId, String username, String email, List<String> roles) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(900000); // 15 minutos

    Map<String, Object> claims = new HashMap<>();
    claims.put(USERNAME, username);
    claims.put(EMAIL, email);
    claims.put(ROLES, roles);
    claims.put("jti", jti);

    SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userId)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiresAt))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  private String generateExpiredToken() {
    Instant now = Instant.now();
    Instant expiresAt = now.minusMillis(10000); // Expirado hace 10 segundos

    Map<String, Object> claims = new HashMap<>();
    claims.put(USERNAME, "testuser");
    claims.put(EMAIL, "test@example.com");
    claims.put(ROLES, List.of("USER"));
    claims.put("jti", "expired-jti");

    SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject("user-123")
        .setIssuedAt(Date.from(now.minusMillis(20000)))
        .setExpiration(Date.from(expiresAt))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  private String generateTokenWithWrongSecret(String jti) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(900000);

    Map<String, Object> claims = new HashMap<>();
    claims.put(USERNAME, "testuser");
    claims.put(EMAIL, "test@example.com");
    claims.put(ROLES, List.of("USER"));
    claims.put("jti", jti);

    // Usar un secreto diferente para que la firma no coincida
    String wrongSecret = "wrong-secret-key-different-from-configured-secret";
    SecretKey key = new SecretKeySpec(wrongSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject("user-123")
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiresAt))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}
