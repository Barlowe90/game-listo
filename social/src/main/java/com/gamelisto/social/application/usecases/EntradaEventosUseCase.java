package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.GrafoUsuarioRepositorio;
import com.gamelisto.social.dominio.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {
  private final GrafoUsuarioRepositorio grafoUsuarioRepositorio;

  @Override
  public void procesarUsuarioCreado(String usuarioId, String username, String avatar) {
    UserRef user = UserRef.of(usuarioId, username, avatar);
    grafoUsuarioRepositorio.upsertUser(user);
  }

  @Override
  public void procesarUsuarioEliminado(String usuarioId) {
    grafoUsuarioRepositorio.deleteUser(usuarioId);
  }
}
