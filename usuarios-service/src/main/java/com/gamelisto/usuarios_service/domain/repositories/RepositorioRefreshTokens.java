package com.gamelisto.usuarios_service.domain.repositories;

import com.gamelisto.usuarios_service.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios_service.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface RepositorioRefreshTokens {

  void guardarActivo(TokenHash tokenHash, UsuarioId usuarioId, Instant expiresAt);

  Optional<RefreshToken> buscarActivo(TokenHash tokenHash);

  void revocar(TokenHash tokenHash, Duration ttl);

  boolean estaRevocado(TokenHash tokenHash);
}
