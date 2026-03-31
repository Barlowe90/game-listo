package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {

  private final GameRefRepositorio gameRefRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;
  private final CrearBibliotecaParaUsuarioHandle crearBiblioteca;

  @Override
  public void procesarUsuarioCreado(String usuarioId, String username, String avatar) {
    UsuarioRef usuarioRef = UsuarioRef.create(UsuarioId.fromString(usuarioId), username, avatar);
    usuariosRefRepositorio.save(usuarioRef);
    crearBiblioteca.execute(UUID.fromString(usuarioId));
  }

  @Override
  public void procesarUsuarioActualizado(String usuarioId, String username, String avatar) {
    UsuarioRef usuarioRef = UsuarioRef.create(UsuarioId.fromString(usuarioId), username, avatar);
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
