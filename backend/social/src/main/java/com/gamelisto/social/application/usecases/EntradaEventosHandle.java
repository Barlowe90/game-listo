package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface EntradaEventosHandle {
  void procesarUsuarioCreado(
      UUID usuarioId, String username, String avatar, String discordUserId);

  void procesarUsuarioActualizado(
      UUID usuarioId, String username, String avatar, String discordUserId);

  void procesarUsuarioEliminado(UUID usuarioId);

  void procesarEstadoActualizado(UUID usuarioId, Long gameRef, String estado);
}
