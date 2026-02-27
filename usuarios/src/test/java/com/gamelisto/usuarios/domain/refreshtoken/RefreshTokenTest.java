package com.gamelisto.usuarios.domain.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RefreshToken - Aggregate root para tokens de refresco")
class RefreshTokenTest {

  @Test
  @DisplayName("Debe crear nuevo refresh token con create()")
  void debeCrearNuevoRefreshToken() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Duration duracion = Duration.ofDays(7);

    // Act
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, duracion);

    // Assert
    assertThat(token).isNotNull();
    assertThat(token.getTokenHash()).isEqualTo(tokenHash);
    assertThat(token.getUsuarioId()).isEqualTo(usuarioId);
    assertThat(token.getCreatedAt()).isNotNull();
    assertThat(token.getExpiresAt()).isNotNull();
    assertThat(token.isRevoked()).isFalse();
    assertThat(token.getExpiresAt()).isAfter(token.getCreatedAt());
  }

  @Test
  @DisplayName("Debe reconstituir refresh token con reconstitute()")
  void debeReconstituirRefreshToken() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant createdAt = Instant.now().minus(Duration.ofDays(1));
    Instant expiresAt = Instant.now().plus(Duration.ofDays(6));
    boolean revoked = false;

    // Act
    RefreshToken token =
        RefreshToken.reconstitute(tokenHash, usuarioId, createdAt, expiresAt, revoked);

    // Assert
    assertThat(token).isNotNull();
    assertThat(token.getTokenHash()).isEqualTo(tokenHash);
    assertThat(token.getUsuarioId()).isEqualTo(usuarioId);
    assertThat(token.getCreatedAt()).isEqualTo(createdAt);
    assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
    assertThat(token.isRevoked()).isEqualTo(revoked);
  }

  @Test
  @DisplayName("Debe validar expiración correctamente")
  void debeValidarExpiracionCorrectamente() {
    // Arrange - Token no expirado
    TokenValue tokenValue1 = TokenValue.generate();
    TokenHash tokenHash1 = TokenHash.from(tokenValue1);
    UsuarioId usuarioId = UsuarioId.generate();
    RefreshToken tokenValido = RefreshToken.create(tokenHash1, usuarioId, Duration.ofDays(7));

    // Assert - Token válido
    assertThat(tokenValido.isExpired()).isFalse();

    // Arrange - Token expirado
    TokenValue tokenValue2 = TokenValue.generate();
    TokenHash tokenHash2 = TokenHash.from(tokenValue2);
    Instant createdAt = Instant.now().minus(Duration.ofDays(8));
    Instant expiresAt = Instant.now().minus(Duration.ofDays(1));
    RefreshToken tokenExpirado =
        RefreshToken.reconstitute(tokenHash2, usuarioId, createdAt, expiresAt, false);

    // Assert - Token expirado
    assertThat(tokenExpirado.isExpired()).isTrue();
  }

  @Test
  @DisplayName("Debe marcar como revocado")
  void debeMarcarComoRevocado() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, Duration.ofDays(7));

    assertThat(token.isRevoked()).isFalse();

    // Act
    token.revoke();

    // Assert
    assertThat(token.isRevoked()).isTrue();
  }

  @Test
  @DisplayName("Debe rechazar token expirado como inválido")
  void debeRechazarTokenExpiradoComoInvalido() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant createdAt = Instant.now().minus(Duration.ofDays(8));
    Instant expiresAt = Instant.now().minus(Duration.ofDays(1));
    RefreshToken token =
        RefreshToken.reconstitute(tokenHash, usuarioId, createdAt, expiresAt, false);

    // Act & Assert
    assertThat(token.isValid()).isFalse();
    assertThat(token.isExpired()).isTrue();
    assertThat(token.isRevoked()).isFalse();
  }

  @Test
  @DisplayName("Debe rechazar token revocado como inválido")
  void debeRechazarTokenRevocadoComoInvalido() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, Duration.ofDays(7));

    // Act
    token.revoke();

    // Assert
    assertThat(token.isValid()).isFalse();
    assertThat(token.isExpired()).isFalse();
    assertThat(token.isRevoked()).isTrue();
  }

  @Test
  @DisplayName("Debe generar token hash único para cada token")
  void debeGenerarTokenHashUnicoParaCadaToken() {
    // Arrange
    TokenValue tokenValue1 = TokenValue.generate();
    TokenValue tokenValue2 = TokenValue.generate();
    TokenHash tokenHash1 = TokenHash.from(tokenValue1);
    TokenHash tokenHash2 = TokenHash.from(tokenValue2);
    UsuarioId usuarioId = UsuarioId.generate();

    RefreshToken token1 = RefreshToken.create(tokenHash1, usuarioId, Duration.ofDays(7));
    RefreshToken token2 = RefreshToken.create(tokenHash2, usuarioId, Duration.ofDays(7));

    // Assert
    assertThat(token1.getTokenHash()).isNotEqualTo(token2.getTokenHash());
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("Debe asociar token a usuario correctamente")
  void debeAsociarTokenAUsuarioCorrectamente() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId1 = UsuarioId.generate();
    UsuarioId usuarioId2 = UsuarioId.generate();

    RefreshToken token1 = RefreshToken.create(tokenHash, usuarioId1, Duration.ofDays(7));
    RefreshToken token2 = RefreshToken.create(tokenHash, usuarioId2, Duration.ofDays(7));

    // Assert
    assertThat(token1.getUsuarioId()).isEqualTo(usuarioId1);
    assertThat(token2.getUsuarioId()).isEqualTo(usuarioId2);
    assertThat(token1.getUsuarioId()).isNotEqualTo(token2.getUsuarioId());
  }

  @Test
  @DisplayName("Debe lanzar excepción si TokenHash es nulo")
  void debeLanzarExcepcionSiTokenHashEsNulo() {
    // Arrange
    UsuarioId usuarioId = UsuarioId.generate();
    Duration duracion = Duration.ofDays(7);

    // Act & Assert
    assertThatThrownBy(() -> RefreshToken.create(null, usuarioId, duracion))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("TokenHash no puede ser nulo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si UsuarioId es nulo")
  void debeLanzarExcepcionSiUsuarioIdEsNulo() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    Duration duracion = Duration.ofDays(7);

    // Act & Assert
    assertThatThrownBy(() -> RefreshToken.create(tokenHash, null, duracion))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("UsuarioId no puede ser nulo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si fecha de expiración es anterior a creación")
  void debeLanzarExcepcionSiFechaExpiracionEsAnteriorACreacion() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.minus(Duration.ofDays(1)); // Expiración antes de creación

    // Act & Assert
    assertThatThrownBy(
            () -> RefreshToken.reconstitute(tokenHash, usuarioId, createdAt, expiresAt, false))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "La fecha de expiración no puede ser anterior a la fecha de creación");
  }

  @Test
  @DisplayName("Debe calcular TTL correctamente")
  void debeCalcularTtlCorrectamente() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    Duration duracionOriginal = Duration.ofDays(7);
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, duracionOriginal);

    // Act
    Duration ttl = token.getTtl();

    // Assert
    assertThat(ttl).isNotNull();
    assertThat(ttl.toDays()).isLessThanOrEqualTo(7);
    assertThat(ttl.toDays()).isGreaterThanOrEqualTo(6); // Tolerancia por tiempo de ejecución
  }

  @Test
  @DisplayName("Debe validar que token válido no esté expirado ni revocado")
  void debeValidarQueTokenValidoNoEsteExpiradoNiRevocado() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, Duration.ofDays(7));

    // Act & Assert
    assertThat(token.isValid()).isTrue();
    assertThat(token.isExpired()).isFalse();
    assertThat(token.isRevoked()).isFalse();
  }

  @Test
  @DisplayName("Debe implementar equals correctamente")
  void debeImplementarEqualsCorrectamente() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();

    RefreshToken token1 = RefreshToken.create(tokenHash, usuarioId, Duration.ofDays(7));
    RefreshToken token2 =
        RefreshToken.reconstitute(
            tokenHash, usuarioId, token1.getCreatedAt(), token1.getExpiresAt(), token1.isRevoked());

    // Assert
    assertThat(token1).isEqualTo(token2);
    assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
  }

  @Test
  @DisplayName("Debe implementar toString sin exponer información sensible")
  void debeImplementarToStringSinExponerInformacionSensible() {
    // Arrange
    TokenValue tokenValue = TokenValue.generate();
    TokenHash tokenHash = TokenHash.from(tokenValue);
    UsuarioId usuarioId = UsuarioId.generate();
    RefreshToken token = RefreshToken.create(tokenHash, usuarioId, Duration.ofDays(7));

    // Act
    String resultado = token.toString();

    // Assert
    assertThat(resultado)
        .isNotNull()
        .contains("RefreshToken")
        .contains("tokenHash")
        .contains("usuarioId");
  }
}
