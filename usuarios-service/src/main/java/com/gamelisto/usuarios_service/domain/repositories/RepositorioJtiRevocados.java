package com.gamelisto.usuarios_service.domain.repositories;

import com.gamelisto.usuarios_service.domain.refreshtoken.Jti;
import java.time.Duration;

public interface RepositorioJtiRevocados {
  void revocar(Jti jti, Duration ttl);

  boolean estaRevocado(Jti jti);
}
