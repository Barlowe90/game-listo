package com.gamelisto.social.application.usecases;

public interface EntradaEventosHandle {
  void procesarUsuarioCreado(String usuarioId, String username, String avatar);

  void procesarUsuarioEliminado(String usuarioId);
}
