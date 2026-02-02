package com.gamelisto.usuarios_service.infrastructure.persistence.redis;

import com.gamelisto.usuarios_service.domain.refreshtoken.Jti;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioJtiRevocados;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Implementación Redis del repositorio de JTI revocados. Blacklist para invalidación inmediata de
 * access tokens.
 */
@Repository
public class RepositorioJtiRevocadosRedis implements RepositorioJtiRevocados {

  private static final String REVOKED_PREFIX = "jti:revoked:";

  private final StringRedisTemplate redisTemplate;

  public RepositorioJtiRevocadosRedis(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public void revocar(Jti jti, Duration ttl) {
    String key = REVOKED_PREFIX + jti.value();
    redisTemplate.opsForValue().set(key, "1", ttl.getSeconds(), TimeUnit.SECONDS);
  }

  @Override
  public boolean estaRevocado(Jti jti) {
    String key = REVOKED_PREFIX + jti.value();
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}
