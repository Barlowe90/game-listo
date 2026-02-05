package com.gamelisto.usuarios_service.infrastructure.persistence.redis;

import com.gamelisto.usuarios_service.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.test.config.RedisTestContainerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@Import(RepositorioRefreshTokensRedis.class)
@ActiveProfiles("test")
@DisplayName("RepositorioRefreshTokensRedis - Gestión de refresh tokens")
@ExtendWith(RedisTestContainerExtension.class)
class RepositorioRefreshTokensRedisTest {

  @Autowired private RepositorioRefreshTokensRedis repositorio;
  @Autowired private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    // Limpiar todas las claves relacionadas con refresh tokens
    redisTemplate.delete(redisTemplate.keys("rt:*"));
  }

  @Test
  @DisplayName("Debe guardar refresh token en Redis")
  void debeGuardarRefreshTokenEnRedis() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    // Act
    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);

    // Assert
    String key = "rt:active:" + tokenHash.value();
    assertThat(redisTemplate.hasKey(key)).isTrue();
  }

  @Test
  @DisplayName("Debe recuperar refresh token por ID")
  void debeRecuperarRefreshTokenPorId() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);

    // Act
    Optional<RefreshToken> result = repositorio.buscarActivo(tokenHash);

    // Assert
    assertThat(result).isPresent();
    RefreshToken token = result.get();
    assertThat(token.getTokenHash()).isEqualTo(tokenHash);
    assertThat(token.getUsuarioId()).isEqualTo(usuarioId);
    assertThat(token.isRevoked()).isFalse();
  }

  @Test
  @DisplayName("Debe eliminar refresh token")
  void debeEliminarRefreshToken() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);

    // Act
    Duration ttl = Duration.between(Instant.now(), expiresAt);
    repositorio.revocar(tokenHash, ttl);

    // Assert
    String activeKey = "rt:active:" + tokenHash.value();
    String revokedKey = "rt:revoked:" + tokenHash.value();

    assertThat(redisTemplate.hasKey(activeKey)).isFalse();
    assertThat(redisTemplate.hasKey(revokedKey)).isTrue();
  }

  @Test
  @DisplayName("Debe establecer TTL correcto (7 días por defecto)")
  void debeEstablecerTTLCorrecto() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    // Act
    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);

    // Assert
    String key = "rt:active:" + tokenHash.value();
    Long ttlSeconds = redisTemplate.getExpire(key);
    assertThat(ttlSeconds).isGreaterThan(604000L); // ~7 días
  }

  @Test
  @DisplayName("Debe retornar Optional.empty() si token no existe")
  void debeRetornarEmptySiTokenNoExiste() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);

    // Act
    Optional<RefreshToken> result = repositorio.buscarActivo(tokenHash);

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Debe serializar y deserializar RefreshToken correctamente")
  void debeSerializarYDeserializarCorrectamente() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.of(UUID.randomUUID());
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    // Act
    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);
    Optional<RefreshToken> result = repositorio.buscarActivo(tokenHash);

    // Assert
    assertThat(result).isPresent();
    RefreshToken token = result.get();
    assertThat(token.getTokenHash().value()).isEqualTo(tokenHash.value());
    assertThat(token.getUsuarioId().value()).isEqualTo(usuarioId.value());
    assertThat(token.getExpiresAt()).isBetween(expiresAt.minusSeconds(1), expiresAt.plusSeconds(1));
  }

  @Test
  @DisplayName("Debe verificar si token está revocado")
  void debeVerificarSiTokenEstaRevocado() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

    repositorio.guardarActivo(tokenHash, usuarioId, expiresAt);
    repositorio.revocar(tokenHash, Duration.ofDays(7));

    // Act
    boolean estaRevocado = repositorio.estaRevocado(tokenHash);

    // Assert
    assertThat(estaRevocado).isTrue();
  }

  @Test
  @DisplayName("Debe retornar false si token no está revocado")
  void debeRetornarFalseSiTokenNoEstaRevocado() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);

    // Act
    boolean estaRevocado = repositorio.estaRevocado(tokenHash);

    // Assert
    assertThat(estaRevocado).isFalse();
  }
}
