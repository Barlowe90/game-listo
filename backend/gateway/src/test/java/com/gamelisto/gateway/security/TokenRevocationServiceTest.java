package com.gamelisto.gateway.security;

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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenRevocationService - tests básicos")
class TokenRevocationServiceTest {

  @Mock private ReactiveRedisTemplate<String, String> redisTemplate;
  @Mock private ReactiveValueOperations<String, String> valueOps;

  private TokenRevocationService service;

  @BeforeEach
  void setUp() {
    service = new TokenRevocationService(redisTemplate);
  }

  @Test
  @DisplayName("isTokenRevoked devuelve false si no existe en Redis")
  void isTokenRevokedFalse() {
    when(redisTemplate.hasKey("revoked:jti:valid-jti")).thenReturn(Mono.just(false));

    StepVerifier.create(service.isTokenRevoked("valid-jti")).expectNext(false).verifyComplete();

    verify(redisTemplate, times(1)).hasKey("revoked:jti:valid-jti");
  }

  @Test
  @DisplayName("isTokenRevoked devuelve true si existe en Redis")
  void isTokenRevokedTrue() {
    when(redisTemplate.hasKey("revoked:jti:revoked-jti")).thenReturn(Mono.just(true));

    StepVerifier.create(service.isTokenRevoked("revoked-jti")).expectNext(true).verifyComplete();

    verify(redisTemplate, times(1)).hasKey("revoked:jti:revoked-jti");
  }

  @Test
  @DisplayName("revokeToken guarda la marca de revocado con TTL")
  void revokeTokenConTTL() {
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.set(eq("revoked:jti:jti-to-revoke"), eq("revoked"), eq(Duration.ofMinutes(15))))
        .thenReturn(Mono.just(true));

    StepVerifier.create(service.revokeToken("jti-to-revoke", Duration.ofMinutes(15)))
        .expectNext(true)
        .verifyComplete();

    verify(valueOps, times(1)).set("revoked:jti:jti-to-revoke", "revoked", Duration.ofMinutes(15));
  }
}
