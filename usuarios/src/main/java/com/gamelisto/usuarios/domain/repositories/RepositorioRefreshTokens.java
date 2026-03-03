package com.gamelisto.usuarios.domain.repositories;

import com.gamelisto.usuarios.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface RepositorioRefreshTokens {

  void guardarActivo(TokenHash tokenHash, UsuarioId usuarioId, Instant expiresAt);

  Optional<RefreshToken> buscarActivo(TokenHash tokenHash);

  void revocar(TokenHash tokenHash, Duration ttl);

  boolean estaRevocado(TokenHash tokenHash);
}
