package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;

import java.util.List;

public interface EntradaEventosHandle {

  void procesarUsuarioCreado(String usuarioId, String username, String avatar);

  void procesarUsuarioActualizado(String usuarioId, String username, String avatar);

  void procesarUsuarioEliminado(String usuarioId) throws ApplicationException;

  void procesarGameCreado(Long gameId, String nombre, List<String> platforms);
}
