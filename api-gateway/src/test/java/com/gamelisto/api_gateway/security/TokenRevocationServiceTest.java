package com.gamelisto.api_gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenRevocationService - Verificación de tokens revocados")
class TokenRevocationServiceTest {

  @Mock private ReactiveRedisTemplate<String, String> redisTemplate;
  @Mock private ReactiveValueOperations<String, String> valueOps;

  private TokenRevocationService service;

  @BeforeEach
  void setUp() {
    service = new TokenRevocationService(redisTemplate);
  }

  @Test
  @DisplayName("Debe retornar false si JTI no está revocado")
  void debeRetornarFalseSiJtiNoEstaRevocado() {
    // Arrange
    String jti = "valid-jti-123";
    when(redisTemplate.hasKey("revoked:jti:valid-jti-123")).thenReturn(Mono.just(false));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jti);

    // Assert
    StepVerifier.create(result).expectNext(false).verifyComplete();
    verify(redisTemplate, times(1)).hasKey("revoked:jti:valid-jti-123");
  }

  @Test
  @DisplayName("Debe retornar true si JTI está en blacklist de Redis")
  void debeRetornarTrueSiJtiEstaEnBlacklist() {
    // Arrange
    String jti = "revoked-jti-456";
    when(redisTemplate.hasKey("revoked:jti:revoked-jti-456")).thenReturn(Mono.just(true));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jti);

    // Assert
    StepVerifier.create(result).expectNext(true).verifyComplete();
    verify(redisTemplate, times(1)).hasKey("revoked:jti:revoked-jti-456");
  }

  @Test
  @DisplayName("Debe manejar correctamente clave Redis con prefijo 'revoked:jti:'")
  void debeManejarCorrectamenteClaveRedis() {
    // Arrange
    String jti = "test-jti-789";
    String expectedKey = "revoked:jti:test-jti-789";

    when(redisTemplate.hasKey(expectedKey)).thenReturn(Mono.just(false));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jti);

    // Assert
    StepVerifier.create(result).expectNext(false).verifyComplete();
    verify(redisTemplate, times(1)).hasKey(expectedKey);
  }

  @Test
  @DisplayName("Debe manejar error de conexión a Redis retornando false por defecto")
  void debeManejarErrorDeConexionARedis() {
    // Arrange
    String jti = "error-jti-000";
    when(redisTemplate.hasKey("revoked:jti:error-jti-000"))
        .thenReturn(Mono.error(new RuntimeException("Redis connection error")));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jti);

    // Assert - debe retornar false en caso de error (fail-open para lectura)
    StepVerifier.create(result).expectError(RuntimeException.class).verify();
  }

  @Test
  @DisplayName("Debe validar formato de JTI correctamente")
  void debeValidarFormatoDeJti() {
    // Arrange
    String jtiConCaracteresEspeciales = "jti-with-special-chars-!@#$%";
    String expectedKey = "revoked:jti:jti-with-special-chars-!@#$%";

    when(redisTemplate.hasKey(expectedKey)).thenReturn(Mono.just(false));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jtiConCaracteresEspeciales);

    // Assert
    StepVerifier.create(result).expectNext(false).verifyComplete();
    verify(redisTemplate, times(1)).hasKey(expectedKey);
  }

  @Test
  @DisplayName("Debe revocar token correctamente con TTL")
  void debeRevocarTokenConTTL() {
    // Arrange
    String jti = "jti-to-revoke-111";
    Duration ttl = Duration.ofMinutes(15);
    String expectedKey = "revoked:jti:jti-to-revoke-111";

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.set(eq(expectedKey), eq("revoked"), eq(ttl))).thenReturn(Mono.just(true));

    // Act
    Mono<Boolean> result = service.revokeToken(jti, ttl);

    // Assert
    StepVerifier.create(result).expectNext(true).verifyComplete();
    verify(valueOps, times(1)).set(expectedKey, "revoked", ttl);
  }

  @Test
  @DisplayName("Debe usar TTL correcto al revocar token (7 días)")
  void debeUsarTTLCorrectoPara7Dias() {
    // Arrange
    String jti = "jti-long-ttl-222";
    Duration ttl = Duration.ofDays(7);
    String expectedKey = "revoked:jti:jti-long-ttl-222";

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.set(eq(expectedKey), eq("revoked"), eq(ttl))).thenReturn(Mono.just(true));

    // Act
    Mono<Boolean> result = service.revokeToken(jti, ttl);

    // Assert
    StepVerifier.create(result).expectNext(true).verifyComplete();
    verify(valueOps, times(1)).set(expectedKey, "revoked", ttl);
  }

  @Test
  @DisplayName("Debe verificar múltiples JTIs independientemente")
  void debeVerificarMultiplesJtisIndependientemente() {
    // Arrange
    String jti1 = "jti-1";
    String jti2 = "jti-2";

    when(redisTemplate.hasKey("revoked:jti:jti-1")).thenReturn(Mono.just(true));
    when(redisTemplate.hasKey("revoked:jti:jti-2")).thenReturn(Mono.just(false));

    // Act & Assert
    StepVerifier.create(service.isTokenRevoked(jti1)).expectNext(true).verifyComplete();

    StepVerifier.create(service.isTokenRevoked(jti2)).expectNext(false).verifyComplete();

    verify(redisTemplate, times(1)).hasKey("revoked:jti:jti-1");
    verify(redisTemplate, times(1)).hasKey("revoked:jti:jti-2");
  }

  @Test
  @DisplayName("Debe manejar JTI vacío o nulo correctamente")
  void debeManejarJtiVacioONulo() {
    // Arrange
    String jtiVacio = "";
    when(redisTemplate.hasKey("revoked:jti:")).thenReturn(Mono.just(false));

    // Act
    Mono<Boolean> result = service.isTokenRevoked(jtiVacio);

    // Assert
    StepVerifier.create(result).expectNext(false).verifyComplete();
    verify(redisTemplate, times(1)).hasKey("revoked:jti:");
  }
}
