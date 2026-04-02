package com.gamelisto.usuarios.infrastructure.out.persistence.redis;

import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.repositories.RepositorioJtiRevocados;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Blacklist para invalidación inmediata de access tokens.
 */
@Repository
@RequiredArgsConstructor
public class RepositorioJtiRevocadosRedis implements RepositorioJtiRevocados {

    private static final String REVOKED_PREFIX = "jti:revoked:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void revocar(Jti jti, Duration ttl) {
        String key = REVOKED_PREFIX + jti.value();
        redisTemplate.opsForValue().set(key, "1", ttl.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public boolean estaRevocado(Jti jti) {
        String key = REVOKED_PREFIX + jti.value();
        return redisTemplate.hasKey(key);
    }
}
