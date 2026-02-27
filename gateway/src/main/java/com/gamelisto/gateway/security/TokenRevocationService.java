package com.gamelisto.gateway.security;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/** Verificar si un token JWT ha sido revocado. Usa Redis como blacklist. */
@Service
public class TokenRevocationService {

  private static final String REVOKED_TOKENS_PREFIX = "revoked:jti:";

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  public TokenRevocationService(ReactiveRedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Mono<Boolean> isTokenRevoked(String jti) {
    String key = REVOKED_TOKENS_PREFIX + jti;
    return redisTemplate.hasKey(key);
  }

  public Mono<Boolean> revokeToken(String jti, Duration ttl) {
    String key = REVOKED_TOKENS_PREFIX + jti;
    return redisTemplate.opsForValue().set(key, "revoked", ttl);
  }
}
