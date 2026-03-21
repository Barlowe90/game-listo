package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;

public interface EntradaEventosHandle {

  void procesarUsuarioCreado(
      String usuarioId,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername);

  void procesarUsuarioActualizado(
      String usuarioId,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername);

  void procesarUsuarioEliminado(String usuarioId) throws ApplicationException;

  void procesarGameCreado(Long gameId, String nombre, String cover);
}
