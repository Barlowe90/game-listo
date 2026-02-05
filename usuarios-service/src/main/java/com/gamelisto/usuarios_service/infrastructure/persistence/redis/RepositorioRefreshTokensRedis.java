package com.gamelisto.usuarios_service.infrastructure.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gamelisto.usuarios_service.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/** Tokens activos y revocados con TTL automático. */
@Repository
public class RepositorioRefreshTokensRedis implements RepositorioRefreshTokens {

  private static final String ACTIVE_PREFIX = "rt:active:";
  private static final String REVOKED_PREFIX = "rt:revoked:";

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public RepositorioRefreshTokensRedis(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void guardarActivo(TokenHash tokenHash, UsuarioId usuarioId, Instant expiresAt) {
    try {
      String key = ACTIVE_PREFIX + tokenHash.value();
      Instant now = Instant.now();
      RefreshTokenDto dto =
          new RefreshTokenDto(usuarioId.value().toString(), now, expiresAt, false);

      String json = objectMapper.writeValueAsString(dto);
      long ttlSeconds = Duration.between(now, expiresAt).getSeconds();

      redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new RuntimeException("Error al guardar refresh token activo", e);
    }
  }

  @Override
  public Optional<RefreshToken> buscarActivo(TokenHash tokenHash) {
    try {
      String key = ACTIVE_PREFIX + tokenHash.value();
      String json = redisTemplate.opsForValue().get(key);

      if (json == null) {
        return Optional.empty();
      }

      RefreshTokenDto dto = objectMapper.readValue(json, RefreshTokenDto.class);
      RefreshToken token =
          RefreshToken.reconstitute(
              tokenHash,
              UsuarioId.of(java.util.UUID.fromString(dto.userId())),
              dto.createdAt(),
              dto.expiresAt(),
              dto.revoked());

      return Optional.of(token);
    } catch (Exception e) {
      throw new RuntimeException("Error al buscar refresh token activo", e);
    }
  }

  @Override
  public void revocar(TokenHash tokenHash, Duration ttl) {
    String activeKey = ACTIVE_PREFIX + tokenHash.value();
    String revokedKey = REVOKED_PREFIX + tokenHash.value();

    // Eliminar de activos
    redisTemplate.delete(activeKey);

    // Añadir a revocados con TTL residual
    redisTemplate.opsForValue().set(revokedKey, "1", ttl.getSeconds(), TimeUnit.SECONDS);
  }

  @Override
  public boolean estaRevocado(TokenHash tokenHash) {
    String key = REVOKED_PREFIX + tokenHash.value();
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  private record RefreshTokenDto(
      String userId, Instant createdAt, Instant expiresAt, boolean revoked) {}
}
