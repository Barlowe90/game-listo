package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.GameRef;
import com.gamelisto.publicaciones.domain.GameRefRepositorio;
import com.gamelisto.publicaciones.domain.UsuarioRef;
import com.gamelisto.publicaciones.domain.UsuariosRefRepositorio;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {

  private final GameRefRepositorio gameRefRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;

  @Override
  public void procesarUsuarioCreado(String usuarioId, String username, String avatar) {
    UsuarioRef usuarioRef = UsuarioRef.create(UUID.fromString(usuarioId), username, avatar);
    usuariosRefRepositorio.save(usuarioRef);
  }

  @Override
  public void procesarUsuarioEliminado(String usuarioId) throws ApplicationException {
    usuariosRefRepositorio.deleteById(UUID.fromString(usuarioId));
  }

  @Override
  public void procesarGameCreado(String gameId, String nombre, String cover) {
    GameRef gameRef = GameRef.create(Long.parseLong(gameId), nombre, cover);
    gameRefRepositorio.save(gameRef);
  }
}
