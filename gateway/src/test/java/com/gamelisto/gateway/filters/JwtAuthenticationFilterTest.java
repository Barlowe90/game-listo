package com.gamelisto.gateway.filters;

import com.gamelisto.gateway.config.JwtProperties;
import com.gamelisto.gateway.security.JwtValidator;
import com.gamelisto.gateway.security.TokenRevocationService;
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
@DisplayName("JwtAuthenticationFilter - tests básicos")
class JwtAuthenticationFilterTest {

  private static final String URL_PROTEGIDA = "http://localhost:8080/v1/usuarios/perfil";
  private static final String HMAC_SHA_256 = "HmacSHA256";
  private static final String USERNAME = "username";
  private static final String EMAIL = "email";
  private static final String ROLES = "roles";

  @Mock private TokenRevocationService tokenRevocationService;
  @Mock private GatewayFilterChain chain;

  private JwtAuthenticationFilter filter;
  private String secret;

  @BeforeEach
  void setUp() {
    secret = "test-secret-key-must-be-at-least-256-bits-long-for-hs256";

    JwtProperties jwtProperties = new JwtProperties();
    jwtProperties.setSecret(secret);
    jwtProperties.setExpiration(900000L);

    JwtValidator jwtValidator = new JwtValidator(jwtProperties);
    filter = new JwtAuthenticationFilter(jwtValidator, tokenRevocationService);
  }

  @Test
  @DisplayName("Permite rutas públicas sin token (login)")
  void permiteRutaPublicaSinToken() {
    MockServerHttpRequest request =
        MockServerHttpRequest.get("http://localhost:8080/v1/usuarios/auth/login").build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain, times(1)).filter(exchange);
    verifyNoInteractions(tokenRevocationService);
  }

  @Test
  @DisplayName("Bloquea ruta protegida sin token (401)")
  void bloqueaRutaProtegidaSinToken() {
    MockServerHttpRequest request = MockServerHttpRequest.get(URL_PROTEGIDA).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Bloquea ruta protegida con token inválido (401)")
  void bloqueaTokenInvalido() {
    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL_PROTEGIDA)
            .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido")
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Permite token válido y enriquece headers X-User-*")
  void permiteTokenValidoYEnriqueceHeaders() {
    String jti = "test-jti-123";
    String userId = "user-123";
    String username = "testuser";
    String email = "test@example.com";

    String token = generateValidTokenWithClaims(jti, userId, username, email, List.of("USER"));

    MockServerHttpRequest request =
        MockServerHttpRequest.get(URL_PROTEGIDA)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(tokenRevocationService.isTokenRevoked(jti)).thenReturn(Mono.just(false));

    when(chain.filter(any()))
        .thenAnswer(
            invocation -> {
              ServerWebExchange enrichedExchange = invocation.getArgument(0);
              ServerHttpRequest enrichedRequest = enrichedExchange.getRequest();

              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Id")).isEqualTo(userId);
              assertThat(enrichedRequest.getHeaders().getFirst("X-User-Roles")).isEqualTo("USER");

              return Mono.empty();
            });

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(tokenRevocationService, times(1)).isTokenRevoked(jti);
    verify(chain, times(1)).filter(any());
  }

  // ===== helpers =====

  private String generateValidTokenWithClaims(
      String jti, String userId, String username, String email, List<String> roles) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(900000);

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
}
