package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface EntradaEventosHandle {
  void procesarUsuarioCreado(UUID usuarioId, String username, String avatar);

  void procesarUsuarioEliminado(UUID usuarioId);
}
