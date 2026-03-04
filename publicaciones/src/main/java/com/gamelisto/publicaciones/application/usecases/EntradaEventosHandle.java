package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;

public interface EntradaEventosHandle {

  void procesarUsuarioCreado(String usuarioId, String username, String avatar);

  void procesarUsuarioEliminado(String usuarioId) throws ApplicationException;

  void procesarGameCreado(String gameId, String nombre, String plataforma);
}
