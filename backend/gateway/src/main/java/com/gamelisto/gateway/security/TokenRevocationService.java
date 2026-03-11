package com.gamelisto.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/** Verificar si un token JWT ha sido revocado. Usa Redis como blacklist. */
@Service
public class TokenRevocationService {

  private static final String REVOKED_TOKENS_PREFIX = "revoked:jti:";

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  private static final Logger logger = LoggerFactory.getLogger(TokenRevocationService.class);

  public TokenRevocationService(ReactiveRedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Mono<Boolean> isTokenRevoked(String jti) {
    String key = REVOKED_TOKENS_PREFIX + jti;
    // Si Redis falla, por seguridad hacemos "fail-closed" y tratamos el token como revocado.
    return redisTemplate
        .hasKey(key)
        .onErrorResume(
            ex -> {
              logger.error(
                  "Error accediendo a Redis para comprobar JTI revocado ({}). Negando acceso.",
                  jti,
                  ex);
              return Mono.just(true);
            });
  }

  public Mono<Boolean> revokeToken(String jti, Duration ttl) {
    String key = REVOKED_TOKENS_PREFIX + jti;
    return redisTemplate.opsForValue().set(key, "revoked", ttl);
  }
}
