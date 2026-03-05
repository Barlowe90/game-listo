package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;

public interface EntradaEventosHandle {

  void procesarUsuarioCreado(String usuarioId, String username, String rol, String avatar);

  void procesarUsuarioEliminado(String usuarioId) throws ApplicationException;

  void procesarGameCreado(Long gameId, String nombre, String cover);
}
