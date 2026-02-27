package com.gamelisto.usuarios.domain.repositories;

import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import java.time.Duration;

public interface RepositorioJtiRevocados {
  void revocar(Jti jti, Duration ttl);

  boolean estaRevocado(Jti jti);
}
