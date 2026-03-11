package com.gamelisto.usuarios.domain.refreshtoken;

import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

@Getter
public class RefreshToken {

  private final TokenHash tokenHash;
  private final UsuarioId usuarioId;
  private final Instant createdAt;
  private final Instant expiresAt;
  private boolean revoked;

  public RefreshToken(
      TokenHash tokenHash,
      UsuarioId usuarioId,
      Instant createdAt,
      Instant expiresAt,
      boolean revoked) {

    comprobarInvariantes(tokenHash, usuarioId, createdAt, expiresAt);

    comprobarFechaExpiracion(createdAt, expiresAt);

    this.tokenHash = tokenHash;
    this.usuarioId = usuarioId;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.revoked = revoked;
  }

  private static void comprobarFechaExpiracion(Instant createdAt, Instant expiresAt) {
    if (expiresAt.isBefore(createdAt)) {
      throw new DomainException(
          "La fecha de expiración no puede ser anterior a la fecha de creación");
    }
  }

  private static void comprobarInvariantes(
      TokenHash tokenHash, UsuarioId usuarioId, Instant createdAt, Instant expiresAt) {
    Objects.requireNonNull(tokenHash, "TokenHash no puede ser nulo");
    Objects.requireNonNull(usuarioId, "UsuarioId no puede ser nulo");
    Objects.requireNonNull(createdAt, "createdAt no puede ser nulo");
    Objects.requireNonNull(expiresAt, "expiresAt no puede ser nulo");
  }

  public static RefreshToken create(
      TokenHash tokenHash, UsuarioId usuarioId, Duration expirationDuration) {
    Instant now = Instant.now();
    Instant expiredAt = now.plus(expirationDuration);
    return new RefreshToken(tokenHash, usuarioId, now, expiredAt, false);
  }

  public static RefreshToken reconstitute(
      TokenHash tokenHash,
      UsuarioId usuarioId,
      Instant createdAt,
      Instant expiresAt,
      boolean revoked) {

    return new RefreshToken(tokenHash, usuarioId, createdAt, expiresAt, revoked);
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return !isExpired() && !revoked;
  }

  public void revoke() {
    this.revoked = true;
  }

  public Duration getTtl() {
    return Duration.between(Instant.now(), expiresAt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RefreshToken that = (RefreshToken) o;
    return Objects.equals(tokenHash, that.tokenHash);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(tokenHash);
  }

  @Override
  public String toString() {
    return "RefreshToken{"
        + "tokenHash="
        + tokenHash
        + ", usuarioId="
        + usuarioId
        + ", createdAt="
        + createdAt
        + ", expiresAt="
        + expiresAt
        + ", revoked="
        + revoked
        + '}';
  }
}
