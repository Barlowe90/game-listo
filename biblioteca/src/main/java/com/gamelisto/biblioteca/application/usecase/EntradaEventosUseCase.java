package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {

  private final GameRefRepositorio gameRefRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;

  @Override
  public void procesarUsuarioCreado(String usuarioId, String username, String rol, String avatar) {
    UsuarioRef usuarioRef =
        UsuarioRef.create(UsuarioId.fromString(usuarioId), username, avatar, rol);
    usuariosRefRepositorio.save(usuarioRef);
  }

  @Override
  public void procesarUsuarioEliminado(String usuarioId) throws ApplicationException {
    usuariosRefRepositorio.deleteById(UsuarioId.fromString(usuarioId));
  }

  @Override
  public void procesarGameCreado(Long gameId, String nombre, String cover) {
    GameRef gameRef = GameRef.create(gameId, nombre, cover);
    gameRefRepositorio.save(gameRef);
  }
}
